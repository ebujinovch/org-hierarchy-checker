package com.epam.swissre.interview.orghierarchy;

import com.epam.swissre.interview.orghierarchy.model.Employee;
import com.epam.swissre.interview.orghierarchy.model.Organization;
import java.util.List;

public interface Fixture {

  Employee CEO = new Employee(1, "CEO", "Boss", 100000, null);
  Employee MANAGER_1 = new Employee(2, "Manager1", "Smith", 72000, 1);
  Employee MANAGER_2 = new Employee(3, "Manager2", "Johnson", 60000, 2);
  Employee MANAGER_3 = new Employee(4, "Manager3", "Brown", 50000, 3);
  Employee EMPLOYEE = new Employee(5, "Worker", "Jones", 40000, 4);
  Employee EMPLOYEE_SENIOR = new Employee(6, "Senior Worker", "Jones", 50000, 3);
  Employee EMPLOYEE_NON_EXISTENT_MANAGER = new Employee(999, "Worker", "Jones", 40000, -1);

  static Organization createSampleOrganization() {
    Organization organization = new Organization();
    List.of(CEO, MANAGER_1, MANAGER_2, MANAGER_3, EMPLOYEE, EMPLOYEE_SENIOR)
        .forEach(organization::addEmployee);
    return organization;
  }
}
