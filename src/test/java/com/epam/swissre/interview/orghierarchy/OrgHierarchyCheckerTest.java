package com.epam.swissre.interview.orghierarchy;

import static com.epam.swissre.interview.orghierarchy.Fixture.MANAGER_1;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.epam.swissre.interview.orghierarchy.model.Employee;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrgHierarchyCheckerTest {

  private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

  @BeforeEach
  void setUp() {
    // Redirect System.out to capture output for testing
    System.setOut(new PrintStream(outputStream));
  }

  @Test
  void main_withDefaultFilePath_shouldOutputExpectedHierarchyAnalysis() {
    // Run the OrgHierarchyChecker's main method with default file path
    OrgHierarchyChecker.main();

    // Assert the captured output contains the expected summary
    String actualOutput = outputStream.toString();
    assertTrue(actualOutput.contains(
            "Organization{employees=Employee{id=305, firstName='Brett', lastName='Hardleaf', salary=34000, managerId=300},"),
        "Expected the output to contain the listing of employees in the organization");
    assertTrue(actualOutput.contains("Too long reporting lines:"),
        "Expected the output to contain the list of too long reporting lines");
    assertTrue(actualOutput.contains("Underpaid managers:"),
        "Expected the output to contain the list of underpaid managers");
    assertTrue(actualOutput.contains("Overpaid managers:"),
        "Expected the output to contain the list of overpaid managers");
  }

  @Test
  void mapToOutputLines_withFormattedEntries_shouldReturnFormattedString() {
    Map<Employee, Double> overpaidManagers = Map.of(MANAGER_1, 78000.0);

    String formattedOutput = OrgHierarchyChecker.mapToOutputLines(overpaidManagers,
        entry -> String.format("%s earns more than intended by %.2f", entry.getKey(),
            entry.getValue()));

    String expectedOutput = String.format("%n\t%s earns more than intended by %.2f", MANAGER_1,
        78000.0);

    assertEquals(expectedOutput, formattedOutput, "Formatted output should match expected result");
  }
}
