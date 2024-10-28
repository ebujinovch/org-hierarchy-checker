package com.epam.swissre.interview.orghierarchy.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Represents an organization's structure, allowing operations on employees.
 */
public final class Organization {
  private final Map<Integer, Employee> employees = new HashMap<>();

  /**
   * Adds an employee to the organization.
   *
   * @param employee the employee to add
   * @throws IllegalArgumentException if an employee with the same ID already exists
   */
  public void addEmployee(Employee employee) {
    if (employees.containsKey(employee.id())) {
      throw new IllegalArgumentException("Employee with ID " + employee.id() + " already exists.");
    }
    employees.put(employee.id(), employee);
  }

  /**
   * Retrieves an employee by ID.
   *
   * @param id the ID of the employee
   * @return an Optional with the employee with the given ID, or an empty Optional
   */
  public Optional<Employee> getEmployeeById(int id) {
    return Optional.ofNullable(employees.get(id));
  }

  /**
   * Returns an unmodifiable view of all employees.
   *
   * @return an unmodifiable map of employees
   */
  public Map<Integer, Employee> getEmployees() {
    return Collections.unmodifiableMap(employees);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("Organization Structure:\n");
    employees.values().forEach(employee -> sb.append(employee).append("\n"));
    return sb.toString();
  }
}
