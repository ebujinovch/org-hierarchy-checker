package com.epam.swissre.interview.orghierarchy.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.Test;

class OrganizationTest {

  private final Organization organization = new Organization();

  @Test
  void addEmployee_withValidEmployee_shouldAddEmployeeToOrganization() {
    Employee employee = new Employee(1, "John", "Doe", 50000, null);

    organization.addEmployee(employee);

    assertEquals(Optional.of(employee), organization.getEmployeeById(1));
  }

  @Test
  void addEmployee_withNull_shouldThrowIllegalArgumentException() {
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> organization.addEmployee(null),
        "Employee with the same ID already exists.");
    assertEquals("Employee cannot be null.", exception.getMessage(),
        "Expected the message to reflect that employee is null");
  }

  @Test
  void addEmployee_withDuplicateId_shouldThrowIllegalArgumentException() {
    Employee employee1 = new Employee(1, "John", "Doe", 50000, null);
    Employee employee2 = new Employee(1, "Jane", "Smith", 55000, null);

    organization.addEmployee(employee1);

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> organization.addEmployee(employee2),
        "Employee with the same ID already exists.");
    assertEquals("Duplicate employee ID: " + employee2.id(), exception.getMessage(),
        "Expected the message to reflect that employee is null");
  }

  @Test
  void getEmployeeById_whenEmployeeExists_shouldReturnEmployee() {
    Employee employee = new Employee(1, "John", "Doe", 50000, null);

    organization.addEmployee(employee);

    Optional<Employee> result = organization.getEmployeeById(1);

    assertTrue(result.isPresent(), "Expected to find the employee with ID 1.");
    assertEquals(employee, result.get());
  }

  @Test
  void getEmployeeById_whenEmployeeDoesNotExist_shouldReturnEmptyOptional() {
    Optional<Employee> result = organization.getEmployeeById(999);

    assertTrue(result.isEmpty(), "Expected no employee to be found with ID 999.");
  }

  @Test
  void getEmployees_shouldReturnUnmodifiableCollectionOfEmployees() {
    Employee employee1 = new Employee(1, "John", "Doe", 50000, null);
    Employee employee2 = new Employee(2, "Jane", "Smith", 60000, 1);

    organization.addEmployee(employee1);
    organization.addEmployee(employee2);

    var employees = organization.getEmployees();

    assertEquals(2, employees.size(), "Expected 2 employees in the organization.");
    assertTrue(employees.contains(employee1), "Expected employee 1 to be present.");
    assertTrue(employees.contains(employee2), "Expected employee 2 to be present.");

    assertThrows(UnsupportedOperationException.class,
        () -> employees.add(new Employee(3, "Will", "Turner", 70000, 2)),
        "Expected UnsupportedOperationException when trying to modify the unmodifiable collection.");
  }

  @Test
  void toString_shouldContainDescriptionOfEveryEmployee() {
    Employee employee1 = new Employee(1, "John", "Doe", 50000, null);
    Employee employee2 = new Employee(2, "Jane", "Smith", 60000, 1);

    organization.addEmployee(employee1);
    organization.addEmployee(employee2);

    String organizationString = organization.toString();

    assertTrue(organizationString.contains(employee1.toString()),
        "Expected to have employee1 in organization string representation");
    assertTrue(organizationString.contains(employee2.toString()),
        "Expected to have employee2 in organization string representation");

  }
}
