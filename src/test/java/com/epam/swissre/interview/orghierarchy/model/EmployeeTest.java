package com.epam.swissre.interview.orghierarchy.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.Test;

class EmployeeTest {

  @Test
  void createEmployee_withValidData_shouldSetAllFieldsCorrectly() {
    Employee employee = new Employee(1, "John", "Doe", 50000, 2);

    assertEquals(1, employee.id());
    assertEquals("John", employee.firstName());
    assertEquals("Doe", employee.lastName());
    assertEquals(50000, employee.salary());
    assertEquals(Optional.of(2), employee.getManagerId());
  }

  @Test
  void createEmployee_withoutManager_shouldHaveEmptyManagerId() {
    Employee employee = new Employee(1, "John", "Doe", 50000, null);

    assertTrue(employee.getManagerId().isEmpty());
  }

  @Test
  void createEmployee_withInvalidId_shouldThrowIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> new Employee(0, "John", "Doe", 50000, null),
        "Employee ID must be greater than zero.");
  }

  @Test
  void createEmployee_withNegativeSalary_shouldThrowIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> new Employee(1, "John", "Doe", -100, null),
        "Salary cannot be negative.");
  }
}
