import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EmployeeDetails extends JFrame implements EmployeeObserver {
    private EmployeeController controller;
    // UI components to display current employee details
    private JTextField idField;
    private JTextField ppsField;
    private JTextField firstNameField;
    private JTextField surnameField;
    private JTextField genderField;
    private JTextField departmentField;
    private JTextField salaryField;
    private JTextField fullTimeField;
    // Currently displayed employee
    private Employee currentEmployee;

    public EmployeeDetails() {
        super("Employee Details");
        controller = new EmployeeController();
        controller.addObserver(this);  // register as observer for data changes
        setupUI();
        loadFirstEmployee();  // display the first existing employee, if any
    }

    private void setupUI() {
        setLayout(new BorderLayout());
        // Panel to display employee fields
        JPanel displayPanel = new JPanel(new GridLayout(8, 2, 5, 5));
        displayPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        displayPanel.add(new JLabel("Employee ID:"));       idField = new JTextField();       idField.setEditable(false);       displayPanel.add(idField);
        displayPanel.add(new JLabel("PPS Number:"));        ppsField = new JTextField();      ppsField.setEditable(false);      displayPanel.add(ppsField);
        displayPanel.add(new JLabel("First Name:"));        firstNameField = new JTextField();firstNameField.setEditable(false);displayPanel.add(firstNameField);
        displayPanel.add(new JLabel("Surname:"));           surnameField = new JTextField();  surnameField.setEditable(false);  displayPanel.add(surnameField);
        displayPanel.add(new JLabel("Gender:"));            genderField = new JTextField();   genderField.setEditable(false);   displayPanel.add(genderField);
        displayPanel.add(new JLabel("Department:"));        departmentField = new JTextField();departmentField.setEditable(false);displayPanel.add(departmentField);
        displayPanel.add(new JLabel("Salary:"));            salaryField = new JTextField();   salaryField.setEditable(false);   displayPanel.add(salaryField);
        displayPanel.add(new JLabel("Full Time:"));         fullTimeField = new JTextField(); fullTimeField.setEditable(false); displayPanel.add(fullTimeField);
        add(displayPanel, BorderLayout.CENTER);

        // Navigation buttons panel
        JPanel navPanel = new JPanel();
        JButton firstButton = new JButton("First");
        JButton prevButton = new JButton("Previous");
        JButton nextButton = new JButton("Next");
        JButton lastButton = new JButton("Last");
        navPanel.add(firstButton);
        navPanel.add(prevButton);
        navPanel.add(nextButton);
        navPanel.add(lastButton);
        add(navPanel, BorderLayout.SOUTH);

        // Menu bar with actions
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        fileMenu.add(exitItem);
        JMenu actionMenu = new JMenu("Actions");
        JMenuItem addItem = new JMenuItem("Add");
        JMenuItem editItem = new JMenuItem("Edit");
        JMenuItem deleteItem = new JMenuItem("Delete");
        JMenuItem searchByIdItem = new JMenuItem("Search by ID");
        JMenuItem searchBySurnameItem = new JMenuItem("Search by Surname");
        actionMenu.add(addItem);
        actionMenu.add(editItem);
        actionMenu.add(deleteItem);
        actionMenu.addSeparator();
        actionMenu.add(searchByIdItem);
        actionMenu.add(searchBySurnameItem);
        menuBar.add(fileMenu);
        menuBar.add(actionMenu);
        setJMenuBar(menuBar);

        // Action listeners for menu items
        addItem.addActionListener(e -> {
            // Open AddRecordDialog for adding a new employee
            AddRecordDialog addDialog = new AddRecordDialog(EmployeeDetails.this, controller);
            addDialog.setVisible(true);
        });
        editItem.addActionListener(e -> {
            // Open AddRecordDialog in edit mode for the current employee
            if (currentEmployee != null) {
                AddRecordDialog editDialog = new AddRecordDialog(EmployeeDetails.this, controller, currentEmployee);
                editDialog.setVisible(true);
            }
        });
        deleteItem.addActionListener(e -> {
            // Delete the current employee after confirmation
            if (currentEmployee != null) {
                int confirm = JOptionPane.showConfirmDialog(EmployeeDetails.this,
                        "Are you sure you want to delete this record?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                	controller.deleteEmployee(currentEmployee.getEmployeeId());

                }
            }
        });
        searchByIdItem.addActionListener(e -> {
            // Open dialog to search by ID
            SearchByIdDialog searchDialog = new SearchByIdDialog(EmployeeDetails.this, controller);
            searchDialog.setVisible(true);
        });
        searchBySurnameItem.addActionListener(e -> {
            // Open dialog to search by surname
            SearchBySurnameDialog searchDialog = new SearchBySurnameDialog(EmployeeDetails.this, controller);
            searchDialog.setVisible(true);
        });
        exitItem.addActionListener(e -> {
            controller.close();
            System.exit(0);
        });

        // Action listeners for navigation buttons
        firstButton.addActionListener(e -> loadFirstEmployee());
        prevButton.addActionListener(e -> {
            if (currentEmployee != null) {
                Employee prev = controller.getPreviousEmployee(currentEmployee.getId());
                if (prev != null) {
                    displayEmployee(prev);
                }
            }
        });
        nextButton.addActionListener(e -> {
            if (currentEmployee != null) {
                Employee next = controller.getNextEmployee(currentEmployee.getId());
                if (next != null) {
                    displayEmployee(next);
                }
            }
        });
        lastButton.addActionListener(e -> {
            // Find and display the last existing employee
            Employee last = null;
            for (int id = 100; id >= 1; id--) {
                Employee emp = controller.searchById(id);
                if (emp != null) {
                    last = emp;
                    break;
                }
            }
            if (last != null) {
                displayEmployee(last);
            }
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
    }

    /** Load the first non-empty employee record into the display. */
    private void loadFirstEmployee() {
        Employee first = null;
        for (int id = 1; id <= 100; id++) {
            first = controller.searchById(id);
            if (first != null) break;
        }
        displayEmployee(first);
    }

    /** Display the given employee's details in the UI fields (or clear fields if null). */
    private void displayEmployee(Employee emp) {
        currentEmployee = emp;
        if (emp == null) {
            // No employee to display (e.g., file empty or record deleted)
            idField.setText("");
            ppsField.setText("");
            firstNameField.setText("");
            surnameField.setText("");
            genderField.setText("");
            departmentField.setText("");
            salaryField.setText("");
            fullTimeField.setText("");
        } else {
            idField.setText(String.valueOf(emp.getId()));
            ppsField.setText(emp.getPps());
            firstNameField.setText(emp.getFirstName());
            surnameField.setText(emp.getSurname());
            genderField.setText(emp.getGender() == 'M' ? "Male" : "Female");
            departmentField.setText(emp.getDepartment());
            salaryField.setText(String.valueOf(emp.getSalary()));
            fullTimeField.setText(emp.getFullTime() ? "Yes" : "No");
        }
    }

    /** Observer callback: called when employee data changes in the controller. */
    @Override
    public void employeeDataChanged() {
        // If current record was edited or deleted, refresh the display accordingly
        if (currentEmployee != null) {
            Employee updated = controller.searchById(currentEmployee.getId());
            if (updated == null) {
                // Current employee was deleted or no longer exists; load the first available record
                loadFirstEmployee();
            } else {
                // Current employee was updated; refresh its details
                displayEmployee(updated);
            }
        } else {
            // If no current employee (e.g., list was empty and one was added), load the first new record
            loadFirstEmployee();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            EmployeeDetails employeeDetails = new EmployeeDetails();
            employeeDetails.setVisible(true);
        });
    }

}
