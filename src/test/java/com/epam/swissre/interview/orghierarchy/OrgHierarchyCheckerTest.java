package com.epam.swissre.interview.orghierarchy;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
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
            "Organization{employees=Employee[id=305, firstName=Brett, lastName=Hardleaf, salary=34000, managerId=300],"),
        "Expected the output to contain the listing of employees in the organization");
    assertTrue(actualOutput.contains("Too long reporting lines:"),
        "Expected the output to contain the list of too long reporting lines");
    assertTrue(actualOutput.contains("Underpaid managers:"),
        "Expected the output to contain the list of underpaid managers");
    assertTrue(actualOutput.contains("Overpaid managers:"),
        "Expected the output to contain the list of overpaid managers");
  }
}
