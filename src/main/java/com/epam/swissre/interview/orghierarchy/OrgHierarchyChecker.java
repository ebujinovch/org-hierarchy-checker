package com.epam.swissre.interview.orghierarchy;

import com.epam.swissre.interview.orghierarchy.model.Employee;
import com.epam.swissre.interview.orghierarchy.model.Organization;

public class OrgHierarchyChecker {
  public static void main(String[] args) {
    Organization org = new Organization();
    org.addEmployee(new Employee(123, "Joe", "Doe", 60000, null)); // CEO
    org.addEmployee(new Employee(124, "Martin", "Chekov", 45000, 123));
    org.addEmployee(new Employee(125, "Bob", "Ronstad", 47000, 123));

    System.out.println(org);
  }
}
