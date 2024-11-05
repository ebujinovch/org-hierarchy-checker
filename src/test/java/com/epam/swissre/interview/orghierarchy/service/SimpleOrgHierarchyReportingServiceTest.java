package com.epam.swissre.interview.orghierarchy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.epam.swissre.interview.orghierarchy.Fixture;
import com.epam.swissre.interview.orghierarchy.config.ReportingConfig;
import com.epam.swissre.interview.orghierarchy.exception.BadManagerReferenceException;
import com.epam.swissre.interview.orghierarchy.exception.CircularReferenceException;
import com.epam.swissre.interview.orghierarchy.model.Employee;
import com.epam.swissre.interview.orghierarchy.model.Organization;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class SimpleOrgHierarchyReportingServiceTest implements Fixture {

  private static final ReportingConfig CONFIG = new ReportingConfig(4, 1.2, 1.5);

  private final SimpleOrgHierarchyReportingService reportingService = new SimpleOrgHierarchyReportingService(
      CONFIG);

  @Test
  void buildReportingLines_withValidHierarchy_shouldReturnExpectedReportingLines() {
    Organization organization = Fixture.createSampleOrganization();

    Map<Employee, List<Integer>> reportingLines = reportingService.buildReportingLines(
        organization);

    assertEquals(6, reportingLines.size(), "Expected reporting lines for all employees.");
    assertEquals(List.of(CEO.id()), reportingLines.get(MANAGER_1));
    assertEquals(List.of(MANAGER_1.id(), CEO.id()), reportingLines.get(MANAGER_2));
    assertEquals(List.of(MANAGER_2.id(), MANAGER_1.id(), CEO.id()),
        reportingLines.get(MANAGER_3));
    assertEquals(List.of(MANAGER_3.id(), MANAGER_2.id(), MANAGER_1.id(), CEO.id()),
        reportingLines.get(EMPLOYEE));
    assertEquals(List.of(MANAGER_2.id(), MANAGER_1.id(), CEO.id()),
        reportingLines.get(EMPLOYEE_SENIOR));
  }

  @Test
  void buildReportingLines_withNoCEOPresent_shouldThrowBadManagerReferenceException() {
    Organization organization = new Organization();
    organization.addEmployee(MANAGER_1);  // Only manager without a CEO

    BadManagerReferenceException exception = assertThrows(
        BadManagerReferenceException.class,
        () -> reportingService.buildReportingLines(organization)
    );

    assertEquals("The hierarchy must have exactly one CEO, but instead has the following: []",
        exception.getMessage(),
        "Expected error message to mention missing CEO.");
  }

  @Test
  void buildReportingLines_withMultipleCEOsPresent_shouldThrowBadManagerReferenceException() {
    Employee secondCEO = new Employee(7, "SecondCEO", "Duplicate", 120000, null);
    Organization organization = Fixture.createSampleOrganization();
    organization.addEmployee(secondCEO);  // Adding a second CEO

    BadManagerReferenceException exception = assertThrows(
        BadManagerReferenceException.class,
        () -> reportingService.buildReportingLines(organization)
    );

    assertEquals("The hierarchy must have exactly one CEO, but instead has the following: [1, 7]",
        exception.getMessage(),
        "Expected error message to mention multiple CEOs.");
  }

  @Test
  void buildReportingLines_withBadManagerReference_shouldThrowBadManagerReferenceException() {
    Organization organization = Fixture.createSampleOrganization();
    organization.addEmployee(EMPLOYEE_NON_EXISTENT_MANAGER);

    BadManagerReferenceException exception = assertThrows(
        BadManagerReferenceException.class,
        () -> reportingService.buildReportingLines(organization)
    );

    assertEquals("Bad manager id [-1] specified for employee [999]", exception.getMessage(),
        "Expected error message to mention non-existent manager ID.");
  }

  @Test
  void buildReportingLines_withCircularReference_shouldThrowCircularReferenceException() {
    // Circular reference 9->8->7->9
    Employee circularManager = new Employee(7, "Circular", "Manager", 45000, 9);
    Employee circularWorker1 = new Employee(8, "Circular", "Worker1", 45000, 7);
    Employee circularWorker2 = new Employee(9, "Circular", "Worker2", 45000, 8);
    Organization organization = Fixture.createSampleOrganization();
    organization.addEmployee(circularManager);
    organization.addEmployee(circularWorker1);
    organization.addEmployee(circularWorker2);

    CircularReferenceException exception = assertThrows(
        CircularReferenceException.class,
        () -> reportingService.buildReportingLines(organization)
    );

    assertEquals(
        "Circular reference detected in hierarchy for employee ID=[7]. The path: [9, 8, 7]",
        exception.getMessage(),
        "Expected error message to mention circular reference.");
  }

  @Test
  void getLongReportingLines_withExcessiveManagerCount_shouldReturnReportingLinesExceedingMax() {
    Organization organization = Fixture.createSampleOrganization();

    SimpleOrgHierarchyReportingService reportingService = new SimpleOrgHierarchyReportingService(
        new ReportingConfig(2, 1.2, 1.5));

    Map<Employee, List<Integer>> longReportingLines = reportingService.getLongReportingLines(
        organization);

    assertEquals(1, longReportingLines.size(), "Expected one employee with long reporting line.");
    assertTrue(longReportingLines.containsKey(EMPLOYEE),
        "Expected long reporting line for EMPLOYEE.");
  }

  @Test
  void getLongReportingLines_withAcceptableManagerCount_shouldReturnEmptyMap() {
    Organization organization = Fixture.createSampleOrganization();

    SimpleOrgHierarchyReportingService reportingService = new SimpleOrgHierarchyReportingService(
        new ReportingConfig(3, 1.2, 1.5));

    Map<Employee, List<Integer>> longReportingLines = reportingService.getLongReportingLines(
        organization);

    assertTrue(longReportingLines.isEmpty(),
        "Expected no long reporting lines within acceptable count.");
  }

  @Test
  void getUnderpaidManagers_withUnderpaidManager_shouldReturnUnderpaidManagers() {
    Employee underpaidManager = new Employee(2, "Underpaid", "Manager", 71999, 1);
    Organization organization = new Organization();
    organization.addEmployee(CEO);
    organization.addEmployee(underpaidManager);
    organization.addEmployee(MANAGER_2);
    organization.addEmployee(MANAGER_3);
    organization.addEmployee(EMPLOYEE);

    Map<Employee, Double> underpaidManagers = reportingService.getUnderpaidManagers(organization);

    assertEquals(1, underpaidManagers.size(), "Expected one underpaid manager.");
    assertTrue(underpaidManagers.containsKey(underpaidManager),
        "Expected underpaid manager in the result.");
    assertEquals(1, underpaidManagers.get(underpaidManager),
        "The salary deficit is calculated correctly");
  }

  @Test
  void getUnderpaidManagers_withAllManagersMeetingMinimum_shouldReturnEmptyMap() {
    Organization organization = Fixture.createSampleOrganization();

    Map<Employee, Double> underpaidManagers = reportingService.getUnderpaidManagers(organization);

    assertTrue(underpaidManagers.isEmpty(), "Expected no underpaid managers.");
  }

  @Test
  void getOverpaidManagers_withOverpaidManager_shouldReturnOverpaidManagers() {
    Employee overpaidManager = new Employee(2, "Overpaid", "Manager", 90001, 1);
    Organization organization = new Organization();
    organization.addEmployee(CEO);
    organization.addEmployee(overpaidManager);
    organization.addEmployee(MANAGER_2);
    organization.addEmployee(MANAGER_3);
    organization.addEmployee(EMPLOYEE);

    Map<Employee, Double> overpaidManagers = reportingService.getOverpaidManagers(organization);

    assertEquals(1, overpaidManagers.size(), "Expected one overpaid manager.");
    assertTrue(overpaidManagers.containsKey(overpaidManager),
        "Expected overpaid manager in the result.");
    assertEquals(1, overpaidManagers.get(overpaidManager),
        "The salary excess is calculated correctly");
  }

  @Test
  void getOverpaidManagers_withAllManagersWithinMaximum_shouldReturnEmptyMap() {
    Organization organization = Fixture.createSampleOrganization();

    Map<Employee, Double> overpaidManagers = reportingService.getOverpaidManagers(organization);

    assertTrue(overpaidManagers.isEmpty(), "Expected no overpaid managers.");
  }
}
