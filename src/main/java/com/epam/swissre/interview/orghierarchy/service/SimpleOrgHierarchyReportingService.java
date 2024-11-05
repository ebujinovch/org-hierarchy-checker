package com.epam.swissre.interview.orghierarchy.service;

import com.epam.swissre.interview.orghierarchy.config.ReportingConfig;
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
public class SimpleOrgHierarchyReportingService implements OrgHierarchyReportingService {

  private final ReportingConfig config;

  public SimpleOrgHierarchyReportingService(ReportingConfig config) {
    this.config = config;
  }

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
            getDirectReportsForManager(organization, employee.id())))
        // map employee to the average of his direct reports
        .map(entry -> Map.entry(entry.getKey(), calcAverageSalary(entry.getValue())))
        // pick only those having direct reports, i.e. an average for them
        .filter(entry -> entry.getValue().isPresent())
        .map(entry -> Map.entry(entry.getKey(), entry.getValue().getAsDouble()));
  }

  private static OptionalDouble calcAverageSalary(List<Employee> employees) {
    return employees.stream().mapToInt(Employee::salary).average();
  }

  /**
   * Builds the reporting line for a given employee, which includes their manager IDs up to and
   * including the CEO.
   *
   * @param organization the organization containing the entire set of employees
   * @param employee     the employee for whom to build the reporting line
   * @return a list of manager IDs leading up to the CEO
   */
  private static List<Integer> buildReportingLine(Organization organization, Employee employee) {
    return buildReportingLine(organization, employee, new ArrayList<>());
  }

  /**
   * Recursive helper method to build the reporting line for an employee.
   *
   * @param organization  the organization containing the entire set of employees
   * @param employee      the employee for whom to build the reporting line
   * @param reportingLine the current list of reporting IDs
   * @return a list of manager IDs leading up to the CEO
   * @throws BadManagerReferenceException if a reference to a non-existent employee is provided
   * @throws CircularReferenceException   if a circular reference is detected in the hierarchy
   */
  private static List<Integer> buildReportingLine(Organization organization, Employee employee,
      List<Integer> reportingLine) {
    if (employee.getManagerId().isEmpty()) {
      return reportingLine;
    } else {
      Integer managerId = employee.getManagerId().get();
      Employee manager = organization.getEmployeeById(managerId).orElseThrow(
          () -> new BadManagerReferenceException(
              String.format("Bad manager id [%d] specified for employee [%d]",
                  employee.getManagerId().get(), employee.id())));
      if (reportingLine.contains(manager.id())) {
        throw new CircularReferenceException(String.format(
            "Circular reference detected in hierarchy for employee ID=[%d]. The path: %s",
            employee.id(), reportingLine));
      }
      reportingLine.add(manager.id());
      return buildReportingLine(organization, manager, reportingLine);
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
  Map<Employee, List<Integer>> buildReportingLines(Organization organization) {
    List<Integer> ceoIds = organization.getEmployees().stream()
        .filter(e -> e.getManagerId().isEmpty())
        .map(Employee::id)
        .toList();
    // assuming that there should always be exactly one CEO
    if (ceoIds.size() != 1) {
      throw new BadManagerReferenceException(
          "The hierarchy must have exactly one CEO, but instead has the following: " + ceoIds);
    }

    return organization.getEmployees().stream()
        .map(e -> Map.entry(e, buildReportingLine(organization, e)))
        .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
  }

  @Override
  public Map<Employee, List<Integer>> getLongReportingLines(Organization organization) {
    return buildReportingLines(organization).entrySet().stream()
        .filter(e -> e.getValue().size() > config.maxManagersToCEO() + 1)
        .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
  }

  @Override
  public Map<Employee, Double> getUnderpaidManagers(Organization organization) {
    return buildDirectReportsAvgSalaryMap(organization)
        // map the managers to the diff between intended minimum wage and their current wage
        .map(entry -> Map.entry(entry.getKey(),
            config.minSalaryFactorForManagers() * entry.getValue()
                - entry.getKey().salary()))
        // pick only those having a positive diff, i.e. being below the intended minimum wage
        .filter(entry -> entry.getValue() > 0)
        .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
  }

  @Override
  public Map<Employee, Double> getOverpaidManagers(Organization organization) {
    return buildDirectReportsAvgSalaryMap(organization)
        // map the managers to the diff between intended maximum wage and their current wage
        .map(entry -> Map.entry(entry.getKey(),
            entry.getKey().salary()
                - config.maxSalaryFactorForManagers() * entry.getValue()))
        // pick only those having a positive diff, i.e. being above the intended maximum wage
        .filter(entry -> entry.getValue() > 0)
        .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
  }
}
