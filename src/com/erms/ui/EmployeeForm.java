package com.erms.ui;

import com.erms.model.Employee;
import com.erms.service.EmployeeService;
import com.erms.service.EmployeeServiceImpl;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class EmployeeForm extends JFrame {

    // Required Swing Components (5+ components used)
    private JTextField txtId, txtFirstName, txtLastName, txtEmail, txtSalary;
    private JComboBox<String> cmbPosition; // JComboBox is used
    private JTable employeeTable; // JTable is used
    private DefaultTableModel tableModel;
    private JButton btnAdd, btnLoad, btnUpdate, btnDelete, btnClear;

    private final EmployeeService employeeService;

    // Regular Expressions for Validation
    private static final String EMAIL_REGEX = "^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,3})+$";
    private static final String NAME_REGEX = "^[a-zA-Z]+$";
    private static final String SALARY_REGEX = "^\\d+(\\.\\d{1,2})?$";

    public EmployeeForm() {
        super("Employee Records Management System (CRUD) - Genina , Karebu, Genina, Innocente, Albertine");
        this.employeeService = (EmployeeService) new EmployeeServiceImpl();
        
        // Setup the Main Window
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout(10, 10)); // BorderLayout is used
        
        initializeComponents();
        addComponentsToFrame();
        addActionListeners();
        
        loadEmployees(); // Load data on startup
        
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initializeComponents() {
        // Form Fields (JTextField, JComboBox)
        txtId = new JTextField(5);
        txtId.setEditable(false); // ID is read-only
        txtFirstName = new JTextField(15);
        txtLastName = new JTextField(15);
        txtEmail = new JTextField(20);
        txtSalary = new JTextField(10);
        
        // Set all text fields to tomato color background
        Color tomatoColor = new Color(144, 238, 144); // Tomato color
        setTextFieldColor(txtId, tomatoColor);
        setTextFieldColor(txtFirstName, tomatoColor);
        setTextFieldColor(txtLastName, tomatoColor);
        setTextFieldColor(txtEmail, tomatoColor);
        setTextFieldColor(txtSalary, tomatoColor);
        
        String[] positions = {"Software Engineer", "Project Manager", "HR Specialist", "Sales Representative"};
        cmbPosition = new JComboBox<>(positions); // JComboBox

        // Buttons (JButton)
        btnAdd = new JButton("Add Record");
        btnLoad = new JButton("View/Load");
        btnUpdate = new JButton("Update");
        btnDelete = new JButton("Delete");
        btnClear = new JButton("Clear Form");

        // JTable setup
        String[] columnNames = {"ID", "First Name", "Last Name", "Email", "Position", "Salary"};
        tableModel = new DefaultTableModel(columnNames, 0);
        employeeTable = new JTable(tableModel);
        
        // Apply custom renderer for light green background and black text
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        Color lightGreen = new Color(144, 238, 144); // Light green color
        renderer.setBackground(lightGreen);
        renderer.setForeground(Color.BLACK); // Absolute black font color
        
        for (int i = 0; i < employeeTable.getColumnCount(); i++) {
            employeeTable.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
    }
    
    // Helper method to set text field colors
    private void setTextFieldColor(JTextField field, Color bgColor) {
        field.setBackground(bgColor);
        field.setForeground(Color.BLACK); // Black text for contrast
        field.setCaretColor(Color.BLACK);
    }

    private void addComponentsToFrame() {
        // --- Input Panel (North) ---
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        // ID (hidden but needed for updates)
        gbc.gridx = 0; gbc.gridy = row; inputPanel.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1; inputPanel.add(txtId, gbc);
        
        // First Name
        row++; gbc.gridx = 0; gbc.gridy = row; inputPanel.add(new JLabel("First Name:"), gbc);
        gbc.gridx = 1; inputPanel.add(txtFirstName, gbc);
        
        // Last Name
        gbc.gridx = 2; inputPanel.add(new JLabel("Last Name:"), gbc);
        gbc.gridx = 3; inputPanel.add(txtLastName, gbc);

        // Email
        row++; gbc.gridx = 0; gbc.gridy = row; inputPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; inputPanel.add(txtEmail, gbc);
        gbc.gridwidth = 1;

        // Position
        row++; gbc.gridx = 0; gbc.gridy = row; inputPanel.add(new JLabel("Position:"), gbc);
        gbc.gridx = 1; inputPanel.add(cmbPosition, gbc);
        
        // Salary
        gbc.gridx = 2; inputPanel.add(new JLabel("Salary:"), gbc);
        gbc.gridx = 3; inputPanel.add(txtSalary, gbc);

        add(inputPanel, BorderLayout.NORTH);

        // --- Button Panel (South) ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnLoad);
        buttonPanel.add(btnClear);
        add(buttonPanel, BorderLayout.SOUTH);

        // --- Table Panel (Center) ---
        // JScrollPane is used
        JScrollPane scrollPane = new JScrollPane(employeeTable); 
        add(scrollPane, BorderLayout.CENTER);
    }

    // --- Action Listeners and Core Logic ---
    private void addActionListeners() {
        btnAdd.addActionListener(e -> handleAdd());
        btnLoad.addActionListener(e -> loadEmployees());
        btnUpdate.addActionListener(e -> handleUpdate());
        btnDelete.addActionListener(e -> handleDelete());
        btnClear.addActionListener(e -> clearForm());
        
        // Listener for selecting rows in the JTable
        employeeTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = employeeTable.getSelectedRow();
                if (selectedRow >= 0) {
                    fillFormFromTable(selectedRow);
                }
            }
        });
    }

    private void fillFormFromTable(int row) {
        // Get data from selected row
        txtId.setText(tableModel.getValueAt(row, 0).toString());
        txtFirstName.setText(tableModel.getValueAt(row, 1).toString());
        txtLastName.setText(tableModel.getValueAt(row, 2).toString());
        txtEmail.setText(tableModel.getValueAt(row, 3).toString());
        cmbPosition.setSelectedItem(tableModel.getValueAt(row, 4).toString());
        txtSalary.setText(tableModel.getValueAt(row, 5).toString());
    }
    
    // R - Read/Load Operation
    private void loadEmployees() {
        tableModel.setRowCount(0); // Clear existing data
        try {
            List<Employee> employees = employeeService.getAllEmployees();
            for (Employee emp : employees) {
                tableModel.addRow(new Object[]{
                    emp.getId(),
                    emp.getFirstName(),
                    emp.getLastName(),
                    emp.getEmail(),
                    emp.getPosition(),
                    emp.getSalary()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: Failed to load employees.", 
                                          "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    // C - Create Operation
    private void handleAdd() {
        if (!validateInput()) {
            return; // Validation failed
        }
        
        try {
            Employee newEmp = new Employee(
                txtFirstName.getText(),
                txtLastName.getText(),
                txtEmail.getText(),
                (String) cmbPosition.getSelectedItem(),
                Double.parseDouble(txtSalary.getText())
            );
            
            employeeService.addEmployee(newEmp);
            JOptionPane.showMessageDialog(this, "Employee added successfully!");
            clearForm();
            loadEmployees(); // Refresh the table
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to add employee: " + e.getMessage(), 
                                          "Database Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid salary value.", 
                                          "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // U - Update Operation
    private void handleUpdate() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a record to update.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!validateInput()) {
            return; // Validation failed
        }
        
        try {
            Employee updatedEmp = new Employee(
                Integer.parseInt(txtId.getText()),
                txtFirstName.getText(),
                txtLastName.getText(),
                txtEmail.getText(),
                (String) cmbPosition.getSelectedItem(),
                Double.parseDouble(txtSalary.getText())
            );
            
            employeeService.updateEmployee(updatedEmp);
            JOptionPane.showMessageDialog(this, "Employee updated successfully!");
            clearForm();
            loadEmployees(); // Refresh the table
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to update employee: " + e.getMessage(), 
                                          "Database Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid salary value.", 
                                          "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // D - Delete Operation
    private void handleDelete() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a record to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this record?", 
                                                     "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int idToDelete = Integer.parseInt(txtId.getText());
                employeeService.deleteEmployee(idToDelete);
                JOptionPane.showMessageDialog(this, "Employee deleted successfully!");
                clearForm();
                loadEmployees(); // Refresh the table
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Failed to delete employee: " + e.getMessage(), 
                                              "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Clear Operation
    private void clearForm() {
        txtId.setText("");
        txtFirstName.setText("");
        txtLastName.setText("");
        txtEmail.setText("");
        txtSalary.setText("");
        cmbPosition.setSelectedIndex(0);
        employeeTable.clearSelection();
    }

    // Data Validation using RegEx
    private boolean validateInput() {
        String fName = txtFirstName.getText().trim();
        String lName = txtLastName.getText().trim();
        String email = txtEmail.getText().trim();
        String salary = txtSalary.getText().trim();
        
        if (fName.isEmpty() || lName.isEmpty() || email.isEmpty() || salary.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (!fName.matches(NAME_REGEX)) {
            JOptionPane.showMessageDialog(this, "First Name is invalid (letters only).", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (!lName.matches(NAME_REGEX)) {
            JOptionPane.showMessageDialog(this, "Last Name is invalid (letters only).", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!email.matches(EMAIL_REGEX)) {
            JOptionPane.showMessageDialog(this, "Email is invalid.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!salary.matches(SALARY_REGEX)) {
            JOptionPane.showMessageDialog(this, "Salary is invalid (e.g., 75000.00).", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }

    public static void main(String[] args) {
        // Run the application on the Event Dispatch Thread (EDT) for thread safety
        SwingUtilities.invokeLater(() -> new EmployeeForm());
    }
}