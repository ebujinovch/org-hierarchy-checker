package com.epam.swissre.interview.orghierarchy.model;

import java.util.Optional;

/**
 * Represents an employee in the organization. Each employee has an ID, name, salary, and an
 * optional manager.
 *
 * <p>Instances of this record are immutable and include validation to ensure correct values
 * are provided for fields.
 */
public record Employee(int id, String firstName, String lastName, int salary, Integer managerId) {

  /**
   * Constructs an Employee instance with the specified attributes.
   *
   * @param id        the unique identifier of the employee, must be positive
   * @param firstName the first name of the employee, must not be null
   * @param lastName  the last name of the employee, must not be null
   * @param salary    the salary of the employee in integer format, must be non-negative
   * @param managerId the ID of the direct manager, or null if no manager exists
   * @throws IllegalArgumentException if the id is non-positive, salary is negative, or any required
   *                                  field is null
   */
  public Employee {
    if (id <= 0 || salary < 0 || firstName == null || lastName == null) {
      throw new IllegalArgumentException("Invalid employee parameters provided.");
    }
  }

  /**
   * Returns the ID of the direct manager, if available.
   *
   * @return an Optional containing the manager ID, or an empty Optional if no manager exists
   */
  public Optional<Integer> getManagerId() {
    return Optional.ofNullable(managerId);
  }
}
