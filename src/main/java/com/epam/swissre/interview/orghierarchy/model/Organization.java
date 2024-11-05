package com.epam.swissre.interview.orghierarchy.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Represents an organization consisting of employees. Provides storage and retrieval of employees
 * by ID and collection of all employees.
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
    if (employee == null) {
      throw new IllegalArgumentException("Employee cannot be null.");
    }
    if (employees.put(employee.id(), employee) != null) {
      throw new IllegalArgumentException("Duplicate employee ID: " + employee.id());
    }
  }

  /**
   * Retrieves an employee by their unique ID.
   *
   * @param id the unique identifier of the employee
   * @return an Optional containing the employee if found, otherwise empty
   */
  public Optional<Employee> getEmployeeById(int id) {
    return Optional.ofNullable(employees.get(id));
  }

  /**
   * Returns all employees within the organization.
   *
   * @return a collection of all employees
   */
  public Collection<Employee> getEmployees() {
    return Collections.unmodifiableCollection(employees.values());
  }

  @Override
  public String toString() {
    return "Organization{" +
        "employees=" + getEmployees().stream().map(Employee::toString)
        .collect(Collectors.joining("," + System.lineSeparator())) +
        '}';
  }
}
