package com.epam.swissre.interview.orghierarchy.model;

import java.util.Optional;

/**
 * Represents an employee in the organization. Each employee has an ID, name, salary, and an
 * optional manager.
 */
public class Employee {

  private final int id;
  private final String firstName;
  private final String lastName;
  private final int salary;
  private final Integer managerId;
  private Employee manager; // Reference to the direct manager, set dynamically after instantiation.

  /**
   * Constructs an Employee instance.
   *
   * @param id        the unique identifier of the employee
   * @param firstName the first name of the employee
   * @param lastName  the last name of the employee
   * @param salary    the salary of the employee in integer format
   * @param managerId the ID of the direct manager, or null if no manager exists
   */
  public Employee(int id, String firstName, String lastName, int salary, Integer managerId) {
    if (id <= 0 || salary < 0 || firstName == null || lastName == null) {
      throw new IllegalArgumentException("Invalid employee parameters provided.");
    }
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.salary = salary;
    this.managerId = managerId;
  }

  /**
   * Returns the employee ID.
   *
   * @return the unique ID of the employee
   */
  public int getId() {
    return id;
  }

  /**
   * Returns the first name of the employee.
   *
   * @return the first name of the employee
   */
  public String getFirstName() {
    return firstName;
  }

  /**
   * Returns the last name of the employee.
   *
   * @return the last name of the employee
   */
  public String getLastName() {
    return lastName;
  }

  /**
   * Returns the salary of the employee.
   *
   * @return the salary as an integer
   */
  public int getSalary() {
    return salary;
  }

  /**
   * Returns the ID of the direct manager, if available.
   *
   * @return an Optional containing the manager ID or empty if no manager
   */
  public Optional<Integer> getManagerId() {
    return Optional.ofNullable(managerId);
  }

  /**
   * Returns the direct manager of this employee, if available.
   *
   * @return an Optional containing the direct manager or empty if no manager
   */
  public Optional<Employee> getManager() {
    return Optional.ofNullable(manager);
  }

  /**
   * Sets the direct manager for this employee.
   *
   * @param manager the direct manager to assign
   */
  public void setManager(Employee manager) {
    this.manager = manager;
  }

  @Override
  public String toString() {
    return "Employee{" +
        "id=" + id +
        ", firstName='" + firstName + '\'' +
        ", lastName='" + lastName + '\'' +
        ", salary=" + salary +
        ", managerId=" + managerId +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Employee employee = (Employee) o;
    return id == employee.id;
  }

  @Override
  public int hashCode() {
    return id;
  }
}
