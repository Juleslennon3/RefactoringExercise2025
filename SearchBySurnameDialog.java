import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SearchBySurnameDialog extends JDialog {
    // Listener interface to handle surname search (implemented by controller)
    public interface SearchBySurnameListener {
        Employee onSearchBySurname(String surname);
    }

    private SearchBySurnameListener listener;
    private JTextField surnameField;
    private JButton searchButton;
    private JButton cancelButton;

    public SearchBySurnameDialog(Frame parent, SearchBySurnameListener listener) {
        super(parent, "Search By Surname", true);
        this.listener = listener;
        setupUI();
    }

    private void setupUI() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Enter Surname:"), gbc);
        gbc.gridx = 1;
        surnameField = new JTextField(15);
        add(surnameField, gbc);

        searchButton = new JButton("Search");
        cancelButton = new JButton("Cancel");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(searchButton);
        buttonPanel.add(cancelButton);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        add(buttonPanel, gbc);

        // Button actions
        searchButton.addActionListener(e -> onSearch());
        cancelButton.addActionListener(e -> dispose());

        pack();
        setLocationRelativeTo(getParent());
    }

    private void onSearch() {
        String surname = surnameField.getText().trim();
        if (surname.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a surname to search.");
            return;
        }
        Employee result = listener.onSearchBySurname(surname);
        if (result != null) {
            new EmployeeSummaryDialog((Frame) getParent(), result).setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "No employee found with surname \"" + surname + "\".");
        }
    }
}
