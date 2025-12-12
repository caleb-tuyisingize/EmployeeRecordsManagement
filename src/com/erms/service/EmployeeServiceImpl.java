package com.erms.service;

import com.erms.db.DBConnection;
import com.erms.model.Employee;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Implementation of the Abstraction
public class EmployeeServiceImpl implements EmployeeService {
    
    // SQL Statements using PreparedStatement placeholders (?)
    private static final String INSERT_SQL = "INSERT INTO employees (first_name, last_name, email, position, salary) VALUES (?, ?, ?, ?, ?)";
    private static final String SELECT_ALL_SQL = "SELECT id, first_name, last_name, email, position, salary FROM employees";
    private static final String UPDATE_SQL = "UPDATE employees SET first_name = ?, last_name = ?, email = ?, position = ?, salary = ? WHERE id = ?";
    private static final String DELETE_SQL = "DELETE FROM employees WHERE id = ?";

    // C - Create Operation
    @Override
    public void addEmployee(Employee emp) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL)) {
            
            ps.setString(1, emp.getFirstName());
            ps.setString(2, emp.getLastName());
            ps.setString(3, emp.getEmail());
            ps.setString(4, emp.getPosition());
            ps.setDouble(5, emp.getSalary());
            
            ps.executeUpdate();
            
        } catch (SQLException e) {
            // Proper exception handling
            System.err.println("Error adding employee: " + e.getMessage());
            throw new SQLException("Database error during INSERT operation.", e);
        }
    }

    // R - Read Operation
    @Override
    public List<Employee> getAllEmployees() throws SQLException {
        List<Employee> employees = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Employee emp = new Employee(
                    rs.getInt("id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email"),
                    rs.getString("position"),
                    rs.getDouble("salary")
                );
                employees.add(emp);
            }
        } catch (SQLException e) {
            System.err.println("Error reading employees: " + e.getMessage());
            throw new SQLException("Database error during SELECT operation.", e);
        }
        return employees;
    }

    // U - Update Operation
    @Override
    public void updateEmployee(Employee emp) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_SQL)) {
            
            ps.setString(1, emp.getFirstName());
            ps.setString(2, emp.getLastName());
            ps.setString(3, emp.getEmail());
            ps.setString(4, emp.getPosition());
            ps.setDouble(5, emp.getSalary());
            ps.setInt(6, emp.getId()); // ID is in the WHERE clause
            
            ps.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Error updating employee: " + e.getMessage());
            throw new SQLException("Database error during UPDATE operation.", e);
        }
    }

    // D - Delete Operation
    @Override
    public void deleteEmployee(int id) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_SQL)) {
            
            ps.setInt(1, id);
            
            ps.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Error deleting employee: " + e.getMessage());
            throw new SQLException("Database error during DELETE operation.", e);
        }
    }
}