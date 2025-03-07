import java.io.IOException;
import java.io.RandomAccessFile;
import javax.swing.JOptionPane;

public class EmployeeDAO {
    private RandomAccessFile output;
    private RandomAccessFile input;

    // Constructor that takes an existing file
    public EmployeeDAO(RandomAccessFile input, RandomAccessFile output) {
        this.input = input;
        this.output = output;
    }

    // Add an employee to the file
    public long addEmployee(Employee employeeToAdd) {
        long currentRecordStart = 0;

        // Check if PPS already exists
        if (isPpsExist(employeeToAdd.getPps())) {
            JOptionPane.showMessageDialog(null, "PPS number already exists! Employee not added.");
            return -1; // Return -1 to indicate failure
        }

        try {
            RandomAccessEmployeeRecord record = new RandomAccessEmployeeRecord(
                employeeToAdd.getEmployeeId(), employeeToAdd.getPps(),
                employeeToAdd.getSurname(), employeeToAdd.getFirstName(),
                employeeToAdd.getGender(), employeeToAdd.getDepartment(),
                employeeToAdd.getSalary(), employeeToAdd.getFullTime());

            output.seek(output.length());  // Move to the end of the file
            record.write(output);  // Write the record
            currentRecordStart = output.length();
        } catch (IOException ioException) {
            JOptionPane.showMessageDialog(null, "Error writing to file!");
        }

        return currentRecordStart - RandomAccessEmployeeRecord.SIZE;
    }


    // Modify an employee record
    public void updateEmployee(Employee updatedEmployee, long byteToStart) {
        Employee existingEmployee = readEmployee(byteToStart);
        if (existingEmployee == null || existingEmployee.getEmployeeId() <= 0) {
            JOptionPane.showMessageDialog(null, "Cannot update. Employee does not exist.");
            return;
        }

        try {
            RandomAccessEmployeeRecord record = new RandomAccessEmployeeRecord(
                updatedEmployee.getEmployeeId(), updatedEmployee.getPps(),
                updatedEmployee.getSurname(), updatedEmployee.getFirstName(),
                updatedEmployee.getGender(), updatedEmployee.getDepartment(),
                updatedEmployee.getSalary(), updatedEmployee.getFullTime());

            output.seek(byteToStart);
            record.write(output);
        } catch (IOException ioException) {
            JOptionPane.showMessageDialog(null, "Error writing to file!");
        }
    }


    // Delete an employee record (overwrite with empty record)
    public void deleteEmployee(long byteToStart) {
        Employee existingEmployee = readEmployee(byteToStart);
        if (existingEmployee == null || existingEmployee.getEmployeeId() <= 0) {
            JOptionPane.showMessageDialog(null, "Cannot delete. Employee does not exist.");
            return;
        }

        try {
            RandomAccessEmployeeRecord emptyRecord = new RandomAccessEmployeeRecord();
            output.seek(byteToStart);
            emptyRecord.write(output);
        } catch (IOException ioException) {
            JOptionPane.showMessageDialog(null, "Error deleting record!");
        }
    }


    // Read employee record
    public Employee readEmployee(long byteToStart) {
        Employee employee = null;
        RandomAccessEmployeeRecord record = new RandomAccessEmployeeRecord();
        try {
            input.seek(byteToStart);
            record.read(input);
            employee = record;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error reading record!");
        }
        return employee;
    }
    
 // Search employee by ID or Surname
    public Employee searchEmployee(String searchValue, boolean searchById) {
        Employee employee = null;
        RandomAccessEmployeeRecord record = new RandomAccessEmployeeRecord();
        long currentByte = 0;

        try {
            while (currentByte < input.length()) {
                input.seek(currentByte);
                record.read(input);

                // If searching by ID and it matches
                if (searchById && record.getEmployeeId() == Integer.parseInt(searchValue)) {
                    employee = record;
                    break;
                }

                // If searching by surname and it matches
                if (!searchById && record.getSurname().trim().equalsIgnoreCase(searchValue)) {
                    employee = record;
                    break;
                }

                currentByte += RandomAccessEmployeeRecord.SIZE;
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error searching for employee.");
        }
        return employee;
    }

 // Check if a PPS Number already exists in the file
    public boolean isPpsExist(String pps) {
        RandomAccessEmployeeRecord record = new RandomAccessEmployeeRecord();
        long currentByte = 0;
        boolean ppsExists = false;

        try {
            while (currentByte < input.length()) {
                input.seek(currentByte);
                record.read(input);

                // If the PPS matches, return true
                if (record.getPps().trim().equalsIgnoreCase(pps)) {
                    ppsExists = true;
                    break;
                }

                currentByte += RandomAccessEmployeeRecord.SIZE;
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error checking PPS number.");
        }

        return ppsExists;
    }

}
