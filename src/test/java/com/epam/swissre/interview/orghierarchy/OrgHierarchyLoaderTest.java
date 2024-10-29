package com.epam.swissre.interview.orghierarchy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.epam.swissre.interview.orghierarchy.exception.BadCsvFileException;
import com.epam.swissre.interview.orghierarchy.exception.EmployeeCsvLineException;
import com.epam.swissre.interview.orghierarchy.model.Employee;
import com.epam.swissre.interview.orghierarchy.model.Organization;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import org.junit.jupiter.api.Test;

class OrgHierarchyLoaderTest {

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
  private final OrgHierarchyLoader loader = new OrgHierarchyLoader();

  @Test
  void loadOrgHierarchyCsv_withValidData_shouldLoadAllEmployeesCorrectly() {
    Organization organization = loader.loadOrgHierarchyCsv(
        new StringReader(VALID_CSV_CONTENT));

    assertNotNull(organization, "Organization should not be null");
    assertEquals(5, organization.getEmployees().size(), "Should contain exactly 5 employees");

    Employee brett = organization.getEmployeeById(305).orElseThrow();
    assertEquals(305, brett.getId(), "Brett's id should match");
    assertEquals("Brett", brett.getFirstName(), "Brett's first name should match");
    assertEquals("Hardleaf", brett.getLastName(), "Brett's last name should match");
    assertEquals(34000, brett.getSalary(), "Brett's salary should match");
    assertEquals(300, brett.getManagerId().orElse(0), "Brett's manager id should match");

    assertTrue(organization.getEmployeeById(123).orElseThrow().getManagerId().isEmpty(),
        "CEO should have no manager");
  }

  @Test
  public void loadOrgHierarchyCsv_tooManyLines_shouldThrowBadCsvFileException() {
    // Prepare a CSV string with 1002 lines (1 header + 1001 employee lines)
    StringBuilder csvContent = new StringBuilder("Id,firstName,lastName,salary,managerId\n");
    for (int i = 1; i <= 1001; i++) {
      csvContent.append(i).append(",FirstName").append(i)
          .append(",LastName").append(i)
          .append(",50000,").append((i > 1) ? (i - 1) : "").append("\n");
    }

    // Create a reader from the CSV string
    BufferedReader reader = new BufferedReader(new StringReader(csvContent.toString()));
    OrgHierarchyLoader loader = new OrgHierarchyLoader();

    // Assert that a BadCsvFileException is thrown
    BadCsvFileException exception = assertThrows(BadCsvFileException.class,
        () -> loader.loadOrgHierarchyCsv(reader));
    assertEquals("The provided file is too long: contains 1002 rows, while the allowed max is 1001",
        exception.getMessage(),
        "Expected the exception to state the acceptable limits of the file.");
  }

  @Test
  void loadOrgHierarchyCsv_withInvalidSalary_shouldThrowEmployeeCsvLineException() {
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
  void loadOrgHierarchyCsv_withIncompleteLine_shouldThrowEmployeeCsvLineException() {
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
  void loadOrgHierarchyCsv_withEmptyFile_shouldReturnEmptyOrganization() {
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
  void loadOrgHierarchyCsv_withNonNumericId_shouldThrowEmployeeCsvLineException() {
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
  void loadOrgHierarchyCsv_withNullManagerId_shouldSetManagerIdToNullForCEO() {
    Organization organization = loader.loadOrgHierarchyCsv(
        new StringReader(VALID_CSV_CONTENT));

    Employee ceo = organization.getEmployeeById(123).orElseThrow();
    assertTrue(ceo.getManager().isEmpty(), "CEO should have a null manager ID");
  }

  @Test
  void loadOrgHierarchyCsv_withMissingFile_shouldThrowMissingCsvFileException() {
    String nonExistentFilePath = "non-existent-file.csv";

    BadCsvFileException exception = assertThrows(
        BadCsvFileException.class,
        () -> loader.loadOrgHierarchyCsv(nonExistentFilePath)
    );

    assertEquals("CSV file is missing or inaccessible: " + nonExistentFilePath,
        exception.getMessage(),
        "Exception message should indicate missing or inaccessible file");
  }

  @Test
  void loadOrgHierarchyCsv_withInaccessibleFile_shouldThrowMissingCsvFileException()
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
