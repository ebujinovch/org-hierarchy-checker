package com.epam.swissre.interview.orghierarchy.service;

import com.epam.swissre.interview.orghierarchy.model.Employee;
import com.epam.swissre.interview.orghierarchy.model.Organization;
import java.util.List;
import java.util.Map;

/**
 * The {@code OrgHierarchyReportBuilderService} provides methods to build reports about the
 * organizational hierarchy of employees. It calculates various metrics related to employee
 * salaries, reporting lines, and identifies issues such as underpaid or overpaid managers and long
 * reporting lines.
 */
public interface OrgHierarchyReportingService {

  /**
   * Identifies employees with reporting lines that exceed the specified maximum number of managers
   * leading to the CEO.
   *
   * @param organization the organization containing employees
   * @return a map of employees with reporting lines that are too long
   */
  Map<Employee, List<Integer>> getLongReportingLines(Organization organization);

  /**
   * Identifies managers who are underpaid compared to the average salary of their direct reports,
   * based on a specified minimum factor above the average.
   *
   * @param organization the organization containing employees
   * @return a map of underpaid managers with the amount they are underpaid
   */
  Map<Employee, Double> getUnderpaidManagers(Organization organization);

  /**
   * Identifies managers who are overpaid compared to the average salary of their direct reports,
   * based on a specified maximum factor above the average.
   *
   * @param organization the organization containing employees
   * @return a map of overpaid managers with the amount they are overpaid
   */
  Map<Employee, Double> getOverpaidManagers(Organization organization);
}
