package com.erms.service;

import com.erms.model.Employee;
import java.sql.SQLException;
import java.util.List;

// Abstraction through Interface
public interface EmployeeService {
    // Create
    void addEmployee(Employee emp) throws SQLException;
    // Read
    List<Employee> getAllEmployees() throws SQLException;
    // Update
    void updateEmployee(Employee emp) throws SQLException;
    // Delete
    void deleteEmployee(int id) throws SQLException;
}