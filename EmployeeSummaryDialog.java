/*
 * 
 * This is the summary dialog for displaying all Employee details
 * 
 * */

import java.awt.Component;
import javax.swing.*;
import java.awt.*;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;

public class EmployeeSummaryDialog extends JDialog implements ActionListener {
	// vector with all Employees details
	Vector<Object> allEmployees;
	JButton back;
	
	public EmployeeSummaryDialog(Frame parent, Employee employee) {
	    super(parent, "Employee Summary", true);
	    setupUI(employee);
	}

	private void setupUI(Employee employee) {
	    setLayout(new BorderLayout());
	    JPanel panel = new JPanel(new GridLayout(8, 2, 5, 5));

	    panel.add(new JLabel("Employee ID:"));   panel.add(new JLabel(String.valueOf(employee.getEmployeeId())));
	    panel.add(new JLabel("PPS Number:"));    panel.add(new JLabel(employee.getPps()));
	    panel.add(new JLabel("First Name:"));    panel.add(new JLabel(employee.getFirstName()));
	    panel.add(new JLabel("Surname:"));       panel.add(new JLabel(employee.getSurname()));
	    panel.add(new JLabel("Gender:"));        panel.add(new JLabel(employee.getGender() == 'M' ? "Male" : "Female"));
	    panel.add(new JLabel("Department:"));    panel.add(new JLabel(employee.getDepartment()));
	    panel.add(new JLabel("Salary:"));        panel.add(new JLabel(String.valueOf(employee.getSalary())));
	    panel.add(new JLabel("Full Time:"));     panel.add(new JLabel(employee.getFullTime() ? "Yes" : "No"));

	    add(panel, BorderLayout.CENTER);
	    
	    JButton closeButton = new JButton("Close");
	    closeButton.addActionListener(e -> dispose());
	    add(closeButton, BorderLayout.SOUTH);

	    pack();
	    setLocationRelativeTo(getParent());
	}
	// Initialize container
	public Container summaryPane() {
		JPanel summaryDialog = new JPanel(new MigLayout());
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JTable employeeTable;
		DefaultTableModel tableModel;
		// column center alignment
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		// column left alignment 
		DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
		Vector<String> header = new Vector<String>();
		// header names
		String[] headerName = { "ID", "PPS Number", "Surname", "First Name", "Gender", "Department", "Salary",
				"Full Time" };
		// column widths
		int[] colWidth = { 15, 100, 120, 120, 50, 120, 80, 80 };
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		leftRenderer.setHorizontalAlignment(JLabel.LEFT);
		// add headers
		for (int i = 0; i < headerName.length; i++) {
			header.addElement(headerName[i]);
		}// end for
		// Construct table and choose table model for each column
		tableModel = new DefaultTableModel(this.allEmployees, header) {
			public Class getColumnClass(int c) {
				switch (c) {
				case 0:
					return Integer.class;
				case 4:
					return Character.class;
				case 6:
					return Double.class;
				case 7:
					return Boolean.class;
				default:
					return String.class;
				}// end switch
			}// end getColumnClass
		};

		employeeTable = new JTable(tableModel);
		// add header names to table
		for (int i = 0; i < employeeTable.getColumnCount(); i++) {
			employeeTable.getColumn(headerName[i]).setMinWidth(colWidth[i]);
		}// end for
		// set alignments
		employeeTable.getColumnModel().getColumn(0).setCellRenderer(leftRenderer);
		employeeTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
		employeeTable.getColumnModel().getColumn(6).setCellRenderer(new DecimalFormatRenderer());

		employeeTable.setEnabled(false);
		employeeTable.setPreferredScrollableViewportSize(new Dimension(800, (15 * employeeTable.getRowCount() + 15)));
		employeeTable.setAutoCreateRowSorter(true);
		JScrollPane scrollPane = new JScrollPane(employeeTable);

		buttonPanel.add(back = new JButton("Back"));
		back.addActionListener(this);
		back.setToolTipText("Return to main screen");
		
		summaryDialog.add(buttonPanel,"growx, pushx, wrap");
		summaryDialog.add(scrollPane,"growx, pushx, wrap");
		scrollPane.setBorder(BorderFactory.createTitledBorder("Employee Details"));
		
		return summaryDialog;
	}// end summaryPane

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == back){
			dispose();
		}

	}
	// format for salary column
	static class DecimalFormatRenderer extends DefaultTableCellRenderer {
		 private static final DecimalFormat format = new DecimalFormat(
		 "\u20ac ###,###,##0.00" );

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {

			Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			 JLabel label = (JLabel) c;
			 label.setHorizontalAlignment(JLabel.RIGHT);
			 // format salary column
			value = format.format((Number) value);

			return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		}// end getTableCellRendererComponent
	}// DefaultTableCellRenderer
}// end class EmployeeSummaryDialog
