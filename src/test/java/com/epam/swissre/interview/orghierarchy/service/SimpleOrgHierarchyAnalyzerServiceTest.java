package com.epam.swissre.interview.orghierarchy.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.epam.swissre.interview.orghierarchy.exception.BadCsvFileException;
import com.epam.swissre.interview.orghierarchy.model.Employee;
import com.epam.swissre.interview.orghierarchy.model.Organization;
import com.epam.swissre.interview.orghierarchy.port.OrgHierarchyLoader;
import com.epam.swissre.interview.orghierarchy.port.ReportsWriter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.junit.jupiter.api.Test;

class SimpleOrgHierarchyAnalyzerServiceTest {

  private final MockOrgHierarchyLoader mockLoader = new MockOrgHierarchyLoader();
  private final MockReportsWriter mockWriter = new MockReportsWriter();
  private final MockOrgHierarchyReportingService mockReportingService = new MockOrgHierarchyReportingService();
  private final SimpleOrgHierarchyAnalyzerService analyzerService =
      new SimpleOrgHierarchyAnalyzerService(mockLoader, mockWriter, mockReportingService);

  @Test
  void analyze_withValidSource_shouldCompleteWithoutExceptions() {
    mockLoader.setExpectedSource("validSource");
    mockLoader.setReturnOrganization(new Organization());

    assertDoesNotThrow(() -> analyzerService.analyze("validSource"));

    mockLoader.verifyLoadCalled();
    mockWriter.verifyLongReportingLinesCalledWith(Collections.emptyMap());
    mockWriter.verifyUnderpaidManagersCalledWith(Collections.emptyMap());
    mockWriter.verifyOverpaidManagersCalledWith(Collections.emptyMap());
  }

  @Test
  void analyze_withInvalidSource_shouldThrowException() {
    mockLoader.setExpectedSource("invalidSource");
    mockLoader.setThrowException(new BadCsvFileException("Invalid source"));

    assertThrows(BadCsvFileException.class, () -> analyzerService.analyze("invalidSource"));

    mockLoader.verifyLoadCalled();
    mockWriter.verifyNoInteractions();
  }

  @Test
  void analyze_withNonEmptyReports_shouldCallWriterWithExpectedPayloads() {
    mockLoader.setExpectedSource("validSource");
    mockLoader.setReturnOrganization(new Organization());

    Map<Employee, List<Integer>> expectedLongReportingLines = Map.of(
        new Employee(2, "Bob", "Manager", 80000, 1), List.of(1, 2));
    Map<Employee, Double> expectedUnderpaidManagers = Map.of(
        new Employee(3, "Charlie", "Supervisor", 50000, 2), 10000.0);
    Map<Employee, Double> expectedOverpaidManagers = Map.of(
        new Employee(4, "Dave", "Lead", 110000, 2), 5000.0);

    mockReportingService.setLongReportingLines(expectedLongReportingLines);
    mockReportingService.setUnderpaidManagers(expectedUnderpaidManagers);
    mockReportingService.setOverpaidManagers(expectedOverpaidManagers);

    assertDoesNotThrow(() -> analyzerService.analyze("validSource"));

    mockLoader.verifyLoadCalled();
    mockWriter.verifyLongReportingLinesCalledWith(expectedLongReportingLines);
    mockWriter.verifyUnderpaidManagersCalledWith(expectedUnderpaidManagers);
    mockWriter.verifyOverpaidManagersCalledWith(expectedOverpaidManagers);
  }

  // Mock classes with parameterization and interaction verification

  private static class MockOrgHierarchyLoader implements OrgHierarchyLoader {

    private String expectedSource;
    private Organization returnOrganization;
    private Exception throwException;
    private boolean loadCalled = false;

    void setExpectedSource(String expectedSource) {
      this.expectedSource = expectedSource;
    }

    void setReturnOrganization(Organization organization) {
      this.returnOrganization = organization;
    }

    void setThrowException(Exception exception) {
      this.throwException = exception;
    }

    @Override
    public Organization loadOrgHierarchy(String source) {
      loadCalled = true;
      if (throwException != null) {
        throw (RuntimeException) throwException;
      }
      if (!Objects.equals(expectedSource, source)) {
        throw new IllegalArgumentException("Unexpected source: " + source);
      }
      return returnOrganization;
    }

    void verifyLoadCalled() {
      if (!loadCalled) {
        throw new AssertionError("Expected loadOrgHierarchy to be called");
      }
    }
  }

  private static class MockReportsWriter implements ReportsWriter {

    private Map<Employee, List<Integer>> expectedLongReportingLines;
    private Map<Employee, Double> expectedUnderpaidManagers;
    private Map<Employee, Double> expectedOverpaidManagers;

    private boolean longReportingLinesCalled = false;
    private boolean underpaidManagersCalled = false;
    private boolean overpaidManagersCalled = false;

    void verifyLongReportingLinesCalledWith(Map<Employee, List<Integer>> expected) {
      if (!longReportingLinesCalled || !Objects.equals(expected, expectedLongReportingLines)) {
        throw new AssertionError("Expected longReportingLines to be called with: " + expected);
      }
    }

    void verifyUnderpaidManagersCalledWith(Map<Employee, Double> expected) {
      if (!underpaidManagersCalled || !Objects.equals(expected, expectedUnderpaidManagers)) {
        throw new AssertionError("Expected underpaidManagers to be called with: " + expected);
      }
    }

    void verifyOverpaidManagersCalledWith(Map<Employee, Double> expected) {
      if (!overpaidManagersCalled || !Objects.equals(expected, expectedOverpaidManagers)) {
        throw new AssertionError("Expected overpaidManagers to be called with: " + expected);
      }
    }

    void verifyNoInteractions() {
      if (longReportingLinesCalled || underpaidManagersCalled || overpaidManagersCalled) {
        throw new AssertionError("Expected no interactions with ReportsWriter");
      }
    }

    @Override
    public void longReportingLines(Map<Employee, List<Integer>> longReportingLines) {
      this.longReportingLinesCalled = true;
      this.expectedLongReportingLines = longReportingLines;
    }

    @Override
    public void underpaidManagers(Map<Employee, Double> underpaidManagers) {
      this.underpaidManagersCalled = true;
      this.expectedUnderpaidManagers = underpaidManagers;
    }

    @Override
    public void overpaidManagers(Map<Employee, Double> overpaidManagers) {
      this.overpaidManagersCalled = true;
      this.expectedOverpaidManagers = overpaidManagers;
    }
  }

  private static class MockOrgHierarchyReportingService implements OrgHierarchyReportingService {

    private Map<Employee, List<Integer>> longReportingLines = Collections.emptyMap();
    private Map<Employee, Double> underpaidManagers = Collections.emptyMap();
    private Map<Employee, Double> overpaidManagers = Collections.emptyMap();

    void setLongReportingLines(Map<Employee, List<Integer>> longReportingLines) {
      this.longReportingLines = longReportingLines;
    }

    void setUnderpaidManagers(Map<Employee, Double> underpaidManagers) {
      this.underpaidManagers = underpaidManagers;
    }

    void setOverpaidManagers(Map<Employee, Double> overpaidManagers) {
      this.overpaidManagers = overpaidManagers;
    }

    @Override
    public Map<Employee, List<Integer>> getLongReportingLines(Organization organization) {
      return longReportingLines;
    }

    @Override
    public Map<Employee, Double> getUnderpaidManagers(Organization organization) {
      return underpaidManagers;
    }

    @Override
    public Map<Employee, Double> getOverpaidManagers(Organization organization) {
      return overpaidManagers;
    }
  }
}
