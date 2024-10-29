package com.epam.swissre.interview.orghierarchy.service;

import com.epam.swissre.interview.orghierarchy.exception.BadManagerReferenceException;
import com.epam.swissre.interview.orghierarchy.exception.CircularReferenceException;
import com.epam.swissre.interview.orghierarchy.model.Employee;
import com.epam.swissre.interview.orghierarchy.model.Organization;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.OptionalDouble;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The {@code OrgHierarchyAnalyzerService} class provides methods to analyze the organizational
 * hierarchy of employees. It calculates various metrics related to employee salaries, reporting
 * lines, and identifies issues such as underpaid or overpaid managers and long reporting lines.
 */
public class OrgHierarchyAnalyzerService {

  private static List<Employee> getDirectReportsForManager(Organization organization,
      int managerId) {
    return organization.getEmployees().stream()
        .filter(e -> e.getManagerId().isPresent() && e.getManagerId().get() == managerId)
        .toList();
  }

  /**
   * Builds a stream of entries mapping each employee to the average salary of their direct
   * reports.
   *
   * @param organization the organization containing employees
   * @return a stream of entries with each employee and their corresponding average salary of direct
   * reports
   */
  private static Stream<Entry<Employee, Double>> buildDirectReportsAvgSalaryMap(
      Organization organization) {
    return organization.getEmployees().stream()
        // build the list of direct reports for each employee
        .map(employee -> Map.entry(employee,
            getDirectReportsForManager(organization, employee.getId())))
        // map employee to the average of his direct reports
        .map(entry -> Map.entry(entry.getKey(), calcAverageSalary(entry.getValue())))
        // pick only those having direct reports, i.e. an average for them
        .filter(entry -> entry.getValue().isPresent())
        .map(entry -> Map.entry(entry.getKey(), entry.getValue().getAsDouble()));
  }

  private static OptionalDouble calcAverageSalary(List<Employee> employees) {
    return employees.stream().mapToInt(Employee::getSalary).average();
  }

  /**
   * Builds the reporting line for a given employee, which includes their manager IDs up to and
   * including the CEO.
   *
   * @param employee the employee for whom to build the reporting line
   * @return a list of manager IDs leading up to the CEO
   */
  private static List<Integer> buildReportingLine(Employee employee) {
    return buildReportingLine(employee, new ArrayList<>());
  }

  /**
   * Recursive helper method to build the reporting line for an employee.
   *
   * @param employee      the employee for whom to build the reporting line
   * @param reportingLine the current list of reporting IDs
   * @return a list of manager IDs leading up to the CEO
   * @throws CircularReferenceException if a circular reference is detected in the hierarchy
   */
  private static List<Integer> buildReportingLine(Employee employee, List<Integer> reportingLine) {
    if (employee.getManager().isEmpty()) {
      return reportingLine;
    } else {
      Employee manager = employee.getManager().get();
      if (reportingLine.contains(manager.getId())) {
        throw new CircularReferenceException(String.format(
            "Circular reference detected in hierarchy for employee ID=[%d]. The path: %s",
            employee.getId(), reportingLine));
      }
      reportingLine.add(manager.getId());
      return buildReportingLine(manager, reportingLine);
    }
  }

  /**
   * Establishes the reporting structure for each employee in the organization. Links employees to
   * their direct managers based on `managerId` and builds their corresponding reporting lines.
   *
   * @param organization the organization containing employees
   * @return a map of each employee and their reporting line
   * @throws BadManagerReferenceException if a reference to a non-existent employee is provided or
   *                                      the number of CEOs is not exactly one
   * @throws CircularReferenceException   if there is a circular reference in the hierarchy
   */
  public Map<Employee, List<Integer>> buildReportingLines(Organization organization) {
    List<Integer> ceoIds = organization.getEmployees().stream()
        .filter(e -> e.getManagerId().isEmpty())
        .map(Employee::getId)
        .toList();
    // assuming that there should always be exactly one CEO
    if (ceoIds.size() != 1) {
      throw new BadManagerReferenceException(
          "The hierarchy must have exactly one CEO, but instead has the following: " + ceoIds);
    }

    // set the manager refs
    organization.getEmployees().stream()
        .filter(e -> e.getManagerId().isPresent())
        .forEach(employee -> employee.setManager(
            organization.getEmployeeById(employee.getManagerId().get())
                .orElseThrow(() -> new BadManagerReferenceException(
                    String.format("Bad manager id [%d] specified for employee [%d]",
                        employee.getManagerId().get(), employee.getId()))))
        );

    return organization.getEmployees().stream()
        .map(e -> Map.entry(e, buildReportingLine(e)))
        .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
  }

  /**
   * Retrieves employees with reporting lines that exceed the specified maximum number of managers
   * leading to the CEO.
   *
   * @param organization     the organization containing employees
   * @param maxManagersToCEO the maximum allowed number of managers leading to the CEO
   * @return a map of employees with reporting lines that are too long
   */
  public Map<Employee, List<Integer>> getLongReportingLines(Organization organization,
      int maxManagersToCEO) {
    return buildReportingLines(organization).entrySet().stream()
        .filter(e -> e.getValue().size() > maxManagersToCEO + 1)
        .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
  }

  /**
   * Identifies managers who are underpaid compared to the average salary of their direct reports,
   * based on a specified minimum factor above the average.
   *
   * @param organization               the organization containing employees
   * @param minSalaryFactorForManagers the minimum factor above the average salary that a manager
   *                                   should earn
   * @return a map of underpaid managers with the amount they are underpaid
   */
  public Map<Employee, Double> getUnderpaidManagers(Organization organization,
      double minSalaryFactorForManagers) {
    return buildDirectReportsAvgSalaryMap(organization)
        // map the managers to the diff between intended minimum wage and their current wage
        .map(entry -> Map.entry(entry.getKey(),
            minSalaryFactorForManagers * entry.getValue()
                - entry.getKey().getSalary()))
        // pick only those having a positive diff, i.e. being below the intended minimum wage
        .filter(entry -> entry.getValue() > 0)
        .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
  }

  /**
   * Identifies managers who are overpaid compared to the average salary of their direct reports,
   * based on a specified maximum factor above the average.
   *
   * @param organization               the organization containing employees
   * @param maxSalaryFactorForManagers the maximum factor above the average salary that a manager
   *                                   should earn
   * @return a map of overpaid managers with the amount they are overpaid
   */
  public Map<Employee, Double> getOverpaidManagers(Organization organization,
      double maxSalaryFactorForManagers) {
    return buildDirectReportsAvgSalaryMap(organization)
        // map the managers to the diff between intended maximum wage and their current wage
        .map(entry -> Map.entry(entry.getKey(),
            entry.getKey().getSalary()
                - maxSalaryFactorForManagers * entry.getValue()))
        // pick only those having a positive diff, i.e. being above the intended maximum wage
        .filter(entry -> entry.getValue() > 0)
        .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
  }
}
