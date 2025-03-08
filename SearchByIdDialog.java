import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SearchByIdDialog extends JDialog {
    // Listener interface to handle search action (implemented by controller)
    public interface SearchByIdListener {
        Employee onSearchById(int id);
    }

    private SearchByIdListener listener;
    private JTextField idField;
    private JButton searchButton;
    private JButton cancelButton;

    public SearchByIdDialog(Frame parent, SearchByIdListener listener) {
        super(parent, "Search By ID", true);
        this.listener = listener;
        setupUI();
    }

    private void setupUI() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Enter Employee ID:"), gbc);
        gbc.gridx = 1;
        idField = new JTextField(10);
        add(idField, gbc);

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
        String idText = idField.getText().trim();
        if (!ValidationUtil.isValidId(idText, 100)) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric ID (1-100).");
            return;
        }
        int id = Integer.parseInt(idText);
        Employee result = listener.onSearchById(id);
        if (result != null) {
            // Display the found employee in a summary dialog
            new EmployeeSummaryDialog((Frame) getParent(), result).setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Employee with ID " + id + " not found.");
        }
    }
}
