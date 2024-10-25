package com.epam.swissre.interview.orghierarchy.model;

/**
 * Represents an employee with basic details including their manager.
 */
public record Employee(int id, String firstName, String lastName, double salary, Integer managerId) {
  public Employee {
    if (id <= 0) {
      throw new IllegalArgumentException("ID must be positive.");
    }
    if (salary < 0) {
      throw new IllegalArgumentException("Salary must be non-negative.");
    }
  }
}
