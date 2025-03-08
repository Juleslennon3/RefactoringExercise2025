import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AddRecordDialog extends JDialog {
    // Listener interface for add/edit actions (to decouple from main UI)
    public interface AddRecordListener {
        void onAddEmployee(Employee emp);
        void onEditEmployee(Employee emp);
    }

    private AddRecordListener listener;
    private boolean editMode = false;
    private Employee existingEmployee;  // holds the employee being edited, if any

    // Form input fields
    private JTextField idField;
    private JTextField ppsField;
    private JTextField firstNameField;
    private JTextField surnameField;
    private JComboBox<String> genderCombo;
    private JTextField departmentField;
    private JTextField salaryField;
    private JCheckBox fullTimeCheck;
    private JButton saveButton;
    private JButton cancelButton;

    /** Constructor for adding a new record */
    public AddRecordDialog(Frame parent, AddRecordListener listener) {
        super(parent, "Add Employee", true);
        this.listener = listener;
        setupUI();
    }

    /** Constructor for editing an existing record */
    public AddRecordDialog(Frame parent, AddRecordListener listener, Employee employeeToEdit) {
        super(parent, "Edit Employee", true);
        this.listener = listener;
        this.editMode = true;
        this.existingEmployee = employeeToEdit;
        setupUI();
        // Populate fields with existing data for editing
        if (existingEmployee != null) {
            idField.setText(String.valueOf(existingEmployee.getId()));
            idField.setEditable(false);  // ID cannot be changed when editing
            ppsField.setText(existingEmployee.getPps());
            firstNameField.setText(existingEmployee.getFirstName());
            surnameField.setText(existingEmployee.getSurname());
            genderCombo.setSelectedItem(existingEmployee.getGender() == 'M' ? "Male" : "Female");
            departmentField.setText(existingEmployee.getDepartment());
            salaryField.setText(String.valueOf(existingEmployee.getSalary()));
            fullTimeCheck.setSelected(existingEmployee.getFullTime());
        }
    }

    private void setupUI() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        // Build form labels and fields
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Employee ID:"), gbc);
        gbc.gridx = 1;
        idField = new JTextField(10);
        add(idField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("PPS Number:"), gbc);
        gbc.gridx = 1;
        ppsField = new JTextField(15);
        add(ppsField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("First Name:"), gbc);
        gbc.gridx = 1;
        firstNameField = new JTextField(15);
        add(firstNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        add(new JLabel("Surname:"), gbc);
        gbc.gridx = 1;
        surnameField = new JTextField(15);
        add(surnameField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        add(new JLabel("Gender:"), gbc);
        gbc.gridx = 1;
        genderCombo = new JComboBox<>(new String[]{"Male", "Female"});
        add(genderCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        add(new JLabel("Department:"), gbc);
        gbc.gridx = 1;
        departmentField = new JTextField(15);
        add(departmentField, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        add(new JLabel("Salary:"), gbc);
        gbc.gridx = 1;
        salaryField = new JTextField(10);
        add(salaryField, gbc);

        gbc.gridx = 0; gbc.gridy = 7;
        add(new JLabel("Full Time:"), gbc);
        gbc.gridx = 1;
        fullTimeCheck = new JCheckBox();
        add(fullTimeCheck, gbc);

        // Buttons
        saveButton = new JButton(editMode ? "Save Changes" : "Add");
        cancelButton = new JButton("Cancel");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        add(buttonPanel, gbc);

        // Button listeners
        saveButton.addActionListener(e -> onSave());
        cancelButton.addActionListener(e -> dispose());

        pack();
        setLocationRelativeTo(getParent());
    }

    /** Handle Save/Add button click: validate inputs and notify listener. */
    private void onSave() {
        // Validate input fields using ValidationUtil
        if (!ValidationUtil.isValidId(idField.getText(), 100)) {
            JOptionPane.showMessageDialog(this, "Invalid Employee ID (must be a number between 1 and 100).");
            return;
        }
        if (!ValidationUtil.isValidPps(ppsField.getText())) {
            JOptionPane.showMessageDialog(this, "PPS Number must be exactly 7 characters.");
            return;
        }
        if (!ValidationUtil.isValidName(firstNameField.getText()) || !ValidationUtil.isValidName(surnameField.getText())) {
            JOptionPane.showMessageDialog(this, "First Name and Surname cannot be empty.");
            return;
        }
        if (!ValidationUtil.isValidSalary(salaryField.getText())) {
            JOptionPane.showMessageDialog(this, "Salary must be a valid non-negative number.");
            return;
        }
        // Construct Employee object from input fields
        int id = Integer.parseInt(idField.getText().trim());
        String pps = ppsField.getText().trim();
        String firstName = firstNameField.getText().trim();
        String surname = surnameField.getText().trim();
        char gender = genderCombo.getSelectedItem().equals("Male") ? 'M' : 'F';
        String department = departmentField.getText().trim();
        double salary = Double.parseDouble(salaryField.getText().trim());
        boolean fullTime = fullTimeCheck.isSelected();

        Employee emp = new Employee(id, pps, firstName, surname, gender, department, salary, fullTime);
        if (editMode) {
            listener.onEditEmployee(emp);
        } else {
            listener.onAddEmployee(emp);
        }
        dispose();
    }
}
