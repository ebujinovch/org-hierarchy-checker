package com.epam.swissre.interview.orghierarchy.port;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.epam.swissre.interview.orghierarchy.config.CsvSourceConfig;
import com.epam.swissre.interview.orghierarchy.exception.BadCsvFileException;
import com.epam.swissre.interview.orghierarchy.exception.EmployeeCsvLineException;
import com.epam.swissre.interview.orghierarchy.model.Employee;
import com.epam.swissre.interview.orghierarchy.model.Organization;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class CsvOrgHierarchyLoaderTest {

  private static final String VALID_CSV_CONTENT = """
      Id,firstName,lastName,salary,managerId
      123,Joe,Doe,60000,
      124,Martin,Chekov,45000,123
      125,Bob,Ronstad,47000,123
      300,Alice,Hasacat,50000,124
      305,Brett,Hardleaf,34000,300
      """;
  private static final String INVALID_CSV_LINE_CONTENT = """
      Id,firstName,lastName,salary,managerId
      123,Joe,Doe,60000,
      124,Martin,Chekov,invalid_salary,123
      """;
  private static final String INCOMPLETE_CSV_LINE_CONTENT = """
      Id,firstName,lastName,salary,managerId
      123,Joe,Doe,60000,
      124,Martin
      """;
  private static final String DEFAULT_SOURCE = "default-file.csv";
  private final CsvOrgHierarchyLoader loader = new CsvOrgHierarchyLoader(
      new CsvSourceConfig(DEFAULT_SOURCE, 1001));

  @Test
  void loadOrgHierarchyCsv_withValidData_shouldLoadAllEmployeesCorrectly() {
    Organization organization = loader.loadOrgHierarchyCsv(
        new StringReader(VALID_CSV_CONTENT));

    assertNotNull(organization, "Organization should not be null");
    assertEquals(5, organization.getEmployees().size(), "Should contain exactly 5 employees");

    Employee brett = organization.getEmployeeById(305).orElseThrow();
    assertEquals(305, brett.id(), "Brett's id should match");
    assertEquals("Brett", brett.firstName(), "Brett's first name should match");
    assertEquals("Hardleaf", brett.lastName(), "Brett's last name should match");
    assertEquals(34000, brett.salary(), "Brett's salary should match");
    assertEquals(300, brett.getManagerId().orElse(0), "Brett's manager id should match");

    assertTrue(organization.getEmployeeById(123).orElseThrow().getManagerId().isEmpty(),
        "CEO should have no manager");
  }

  @Test
  public void loadOrgHierarchyCsv_tooManyLines_shouldThrowBadFileException() {
    // Prepare a CSV string with 2 lines (1 header + 1 employee lines)
    String csvContent = """
        Id,firstName,lastName,salary,managerId
        1, John, Doe, 50000,
        """;

    // Create a reader from the CSV string
    BufferedReader reader = new BufferedReader(new StringReader(csvContent));
    CsvOrgHierarchyLoader loader = new CsvOrgHierarchyLoader(new CsvSourceConfig(null, 1));

    // Assert that a BadCsvFileException is thrown
    BadCsvFileException exception = assertThrows(BadCsvFileException.class,
        () -> loader.loadOrgHierarchyCsv(reader));
    assertEquals("The provided file is too long: contains 2 rows, while the allowed max is 1",
        exception.getMessage(),
        "Expected the exception to state the acceptable limits of the file.");
  }

  @Test
  void loadOrgHierarchyCsv_withInvalidSalary_shouldThrowEmployeeLineException() {
    EmployeeCsvLineException exception = assertThrows(EmployeeCsvLineException.class,
        () -> loader.loadOrgHierarchyCsv(new StringReader(INVALID_CSV_LINE_CONTENT)));

    assertTrue(exception.getMessage().contains("Error parsing employee data"),
        "Exception message should indicate a parsing error");
    assertNotNull(exception.getCause(), "Exception should contain the root cause");
    assertInstanceOf(NumberFormatException.class, exception.getCause(),
        "The root cause should be a NumberFormatException");
    assertTrue(exception.getCause().getMessage().contains("For input string: \"invalid_salary\""),
        "Exception message should indicate a parsing error");
  }

  @Test
  void loadOrgHierarchyCsv_withIncompleteLine_shouldThrowEmployeeLineException() {
    EmployeeCsvLineException exception = assertThrows(EmployeeCsvLineException.class,
        () -> loader.loadOrgHierarchyCsv(
            new StringReader(INCOMPLETE_CSV_LINE_CONTENT)));

    assertTrue(exception.getMessage().contains("Error parsing employee data"),
        "Exception message should indicate an error parsing the csv");
    assertNotNull(exception.getCause(), "Exception should contain the root cause");
    assertInstanceOf(IllegalArgumentException.class, exception.getCause(),
        "The root cause should be a IllegalArgumentException");
    assertTrue(exception.getCause().getMessage().contains("Invalid CSV line format"),
        "Exception message should indicate an invalid format");
  }

  @Test
  void loadOrgHierarchy_withEmptyFile_shouldReturnEmptyOrganization() {
    String emptyCsvContent = "Id,firstName,lastName,salary,managerId\n";

    Organization organization = loader.loadOrgHierarchyCsv(
        new StringReader(emptyCsvContent));

    assertNotNull(organization, "Organization should not be null");
    assertTrue(organization.getEmployees().isEmpty(),
        "Organization should be empty for an empty file");
  }

  @Test
  void loadOrgHierarchyCsv_withExtraWhitespaceInCsv_shouldTrimAndLoadSuccessfully() {
    String csvWithWhitespace = """
         Id,firstName,lastName,salary,managerId
         123 ,  Joe , Doe ,  60000  ,\s
         124 , Martin , Chekov , 45000 , 123\s
        \s""";

    Organization organization = loader.loadOrgHierarchyCsv(
        new StringReader(csvWithWhitespace));

    assertEquals(2, organization.getEmployees().size(), "Should contain exactly 2 employees");
    assertTrue(organization.getEmployeeById(123).isPresent(),
        "Employee with ID 123 should be loaded");
  }

  @Test
  void loadOrgHierarchyCsv_withNonNumericId_shouldThrowEmployeeLineException() {
    String invalidIdCsv = """
        Id,firstName,lastName,salary,managerId
        invalid_id,Joe,Doe,60000,
        """;

    EmployeeCsvLineException exception = assertThrows(EmployeeCsvLineException.class,
        () -> loader.loadOrgHierarchyCsv(new StringReader(invalidIdCsv)));

    assertTrue(exception.getMessage().contains("Error parsing employee data"),
        "Exception message should indicate a parsing error due to non-numeric ID");
  }

  @Test
  void loadOrgHierarchy_withNullManagerId_shouldSetManagerIdToNullForCEO() {
    Organization organization = loader.loadOrgHierarchyCsv(
        new StringReader(VALID_CSV_CONTENT));

    Employee ceo = organization.getEmployeeById(123).orElseThrow();
    assertTrue(ceo.getManagerId().isEmpty(), "CEO should have a null manager ID");
  }

  @Test
  void loadOrgHierarchyCsv_withMissingFile_shouldThrowMissingFileException() {
    String nonExistentFilePath = "non-existent-file.csv";

    BadCsvFileException exception = assertThrows(
        BadCsvFileException.class,
        () -> loader.loadOrgHierarchy(nonExistentFilePath)
    );

    assertEquals("CSV file is missing or inaccessible: " + nonExistentFilePath,
        exception.getMessage(),
        "Exception message should indicate missing or inaccessible file");
  }

  @ParameterizedTest
  @NullAndEmptySource
  void loadOrgHierarchyCsv_withNoSource_shouldDefaultToConfig(String source) {
    BadCsvFileException exception = assertThrows(
        BadCsvFileException.class,
        () -> loader.loadOrgHierarchy(source)
    );

    assertEquals("CSV file is missing or inaccessible: " + DEFAULT_SOURCE,
        exception.getMessage(),
        "Exception message should indicate the default file path");
  }

  @Test
  void loadOrgHierarchyCsv_withInaccessibleFile_shouldThrowMissingFileException()
      throws IOException {
    Reader reader = Reader.nullReader();
    reader.close();
    BadCsvFileException exception = assertThrows(
        BadCsvFileException.class,
        () -> loader.loadOrgHierarchyCsv(reader)
    );

    assertEquals("Error reading from the provided reader.", exception.getMessage(),
        "Exception message should indicate an error reading from the file");
  }
}
