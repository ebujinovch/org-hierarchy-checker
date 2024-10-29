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

    assertEquals(1, employee.getId());
    assertEquals("John", employee.getFirstName());
    assertEquals("Doe", employee.getLastName());
    assertEquals(50000, employee.getSalary());
    assertEquals(Optional.of(2), employee.getManagerId());
  }

  @Test
  void createEmployee_withoutManager_shouldHaveEmptyManagerId() {
    Employee employee = new Employee(1, "John", "Doe", 50000, null);

    assertTrue(employee.getManagerId().isEmpty());
  }

  @Test
  void setManager_withValidManager_shouldSetTheManagerFieldCorrectly() {
    Employee manager = new Employee(2, "Jane", "Smith", 60000, null);
    Employee employee = new Employee(1, "John", "Doe", 50000, 2);

    employee.setManager(manager);

    assertEquals(Optional.of(manager), employee.getManager());
  }

  @Test
  void getManager_withoutSettingManager_shouldReturnEmptyOptional() {
    Employee employee = new Employee(1, "John", "Doe", 50000, 2);

    assertTrue(employee.getManager().isEmpty());
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
