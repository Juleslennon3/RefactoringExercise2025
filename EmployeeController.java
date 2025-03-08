import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class EmployeeController implements 
        AddRecordDialog.AddRecordListener,
        SearchByIdDialog.SearchByIdListener,
        SearchBySurnameDialog.SearchBySurnameListener {
    private static final int MAX_RECORDS = 100;
    private static final int RECORD_SIZE = 1200;  // byte size of each record in the file

    private RandomAccessFile file;
    private List<Employee> employees = new ArrayList<>(MAX_RECORDS);      // in-memory cache of employee records
    private List<EmployeeObserver> observers = new ArrayList<>();         // registered observers (e.g., UI views)

    public EmployeeController() {
        try {
            file = new RandomAccessFile("employees.dat", "rw");
            // If file is shorter than expected size, initialize it with blank records
            if (file.length() < RECORD_SIZE * MAX_RECORDS) {
                RandomAccessEmployeeRecord blank = new RandomAccessEmployeeRecord();
                for (int i = 0; i < MAX_RECORDS; i++) {
                    blank.write(file);  // write 100 blank records to ensure file is initialized
                }
            }
            loadAllEmployees();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Register an observer to be notified when employee data changes. */
    public void addObserver(EmployeeObserver observer) {
        observers.add(observer);
    }

    /** Remove an observer from notifications. */
    public void removeObserver(EmployeeObserver observer) {
        observers.remove(observer);
    }

    /** Notify all observers that the employee data has changed. */
    private void notifyObservers() {
        for (EmployeeObserver obs : observers) {
            obs.employeeDataChanged();
        }
    }

    /** Load all employee records from the file into the in-memory list. */
    private void loadAllEmployees() throws IOException {
        employees.clear();
        file.seek(0);
        for (int index = 0; index < MAX_RECORDS; index++) {
            file.seek(index * RECORD_SIZE);
            int id = file.readInt();  // read employee ID
            if (id == 0) {
                // No record at this slot (id=0 indicates blank record)
                employees.add(null);
                // Skip the rest of this record's bytes
                file.seek((index * RECORD_SIZE) + RECORD_SIZE);
            } else {
                // Read fixed-length fields for pps, first name, surname
                String pps = readFixedLengthString();
                String firstName = readFixedLengthString();
                String surname = readFixedLengthString();
                char gender = file.readBoolean() ? 'M' : 'F';
                String department = readFixedLengthString();
                int salary = file.readInt();
                boolean fullTime = file.readBoolean();
                // Trim any trailing spaces from fixed-length strings and create Employee object
                Employee emp = new Employee(id, pps.trim(), firstName.trim(), surname.trim(), gender, department.trim(), salary, fullTime);
                employees.add(emp);
            }
        }
    }

    /** Utility: Read a fixed-length (15 char) string from the file. */
    private String readFixedLengthString() throws IOException {
        char[] chars = new char[15];
        for (int i = 0; i < chars.length; i++) {
            chars[i] = file.readChar();
        }
        return new String(chars);
    }

    /** Utility: Write a string as fixed-length (15 char) to the file (pads or truncates as needed). */
    private void writeFixedLengthString(String s) throws IOException {
        StringBuffer buffer = (s != null) ? new StringBuffer(s) : new StringBuffer(15);
        buffer.setLength(15);
        file.writeChars(buffer.toString());
    }

    /** Search for an employee by ID using the strategy pattern (IdSearchStrategy). */
    public Employee searchById(int id) {
        SearchStrategy strategy = new IdSearchStrategy();
        return strategy.search(String.valueOf(id), employees);
    }

    /** Search for an employee by surname using the strategy pattern (SurnameSearchStrategy). */
    public Employee searchBySurname(String surname) {
        SearchStrategy strategy = new SurnameSearchStrategy();
        return strategy.search(surname, employees);
    }

    /** Add a new employee record. Returns true if successful, false if failed (e.g., ID already in use). */
    public boolean addEmployee(Employee newEmp) {
        int id = newEmp.getId();
        if (id < 1 || id > MAX_RECORDS) return false;
        if (employees.get(id - 1) != null) {
            // Slot already occupied
            return false;
        }
        try {
            // Write the new record to file at the correct position
            file.seek((id - 1) * RECORD_SIZE);
            file.writeInt(newEmp.getId());
            writeFixedLengthString(newEmp.getPps());
            writeFixedLengthString(newEmp.getFirstName());
            writeFixedLengthString(newEmp.getSurname());
            file.writeBoolean(newEmp.getGender() == 'M');
            writeFixedLengthString(newEmp.getDepartment());
            file.writeDouble(newEmp.getSalary());
            file.writeBoolean(newEmp.getFullTime());
            // Update in-memory list
            employees.set(id - 1, newEmp);
            // Notify UI observers of data change
            notifyObservers();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Edit an existing employee record. Returns true if successful. */
    public boolean editEmployee(Employee updatedEmp) {
    	int id = updatedEmp.getEmployeeId();
        if (id < 1 || id > MAX_RECORDS) return false;
        if (employees.get(id - 1) == null) {
            // No record exists at this ID to edit
            return false;
        }
        try {
            // Overwrite the record at the given position with new data
            file.seek((id - 1) * RECORD_SIZE);
            file.writeInt(updatedEmp.getEmployeeId());
            writeFixedLengthString(updatedEmp.getPps());
            writeFixedLengthString(updatedEmp.getFirstName());
            writeFixedLengthString(updatedEmp.getSurname());
            file.writeBoolean(updatedEmp.getGender() == 'M');
            writeFixedLengthString(updatedEmp.getDepartment());
            file.writeDouble(updatedEmp.getSalary());
            file.writeBoolean(updatedEmp.getFullTime());
            // Update memory cache
            employees.set(id - 1, updatedEmp);
            notifyObservers();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Delete an employee record by ID (mark as blank). Returns true if successful. */
    public boolean deleteEmployee(int id) {
        if (id < 1 || id > MAX_RECORDS) return false;
        if (employees.get(id - 1) == null) {
            // Already empty
            return false;
        }
        try {
            // Write a blank record (id=0 and empty fields) at this position
            file.seek((id - 1) * RECORD_SIZE);
            file.writeInt(0);
            writeFixedLengthString("");  // blank PPS
            writeFixedLengthString("");  // blank first name
            writeFixedLengthString("");  // blank surname
            file.writeBoolean(false);
            writeFixedLengthString("");  // blank department
            file.writeInt(0);
            file.writeBoolean(false);
            // Update memory cache
            employees.set(id - 1, null);
            notifyObservers();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Get the next non-empty employee record after the given employee ID. */
    public Employee getNextEmployee(int currentId) {
        for (int rec = currentId + 1; rec <= MAX_RECORDS; rec++) {
            Employee emp = employees.get(rec - 1);
            if (emp != null) {
                return emp;
            }
        }
        return null;
    }

    /** Get the previous non-empty employee record before the given employee ID. */
    public Employee getPreviousEmployee(int currentId) {
        for (int rec = currentId - 1; rec >= 1; rec--) {
            Employee emp = employees.get(rec - 1);
            if (emp != null) {
                return emp;
            }
        }
        return null;
    }

    /** Close the underlying file (should be called on application exit). */
    public void close() {
        try {
            if (file != null) file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Implementation of listener interface methods (delegating to controller logic):

    @Override
    public void onAddEmployee(Employee emp) {
        addEmployee(emp);
    }

    @Override
    public void onEditEmployee(Employee emp) {
        editEmployee(emp);
    }

    @Override
    public Employee onSearchById(int id) {
        return searchById(id);
    }

    @Override
    public Employee onSearchBySurname(String surname) {
        return searchBySurname(surname);
    }
}
