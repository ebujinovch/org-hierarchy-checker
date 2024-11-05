package com.epam.swissre.interview.orghierarchy.port;

import static com.epam.swissre.interview.orghierarchy.Fixture.CEO;
import static com.epam.swissre.interview.orghierarchy.Fixture.MANAGER_1;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.epam.swissre.interview.orghierarchy.model.Employee;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ConsoleReportsWriterTest {

  private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
  private final ConsoleReportsWriter writer = new ConsoleReportsWriter(new PrintStream(outputStreamCaptor));

  @Test
  void longReportingLines_withExcessiveReportingLevels_shouldPrintExpectedOutput() {
    Map<Employee, List<Integer>> longReportingLines = Map.of(MANAGER_1, List.of(2, 3, 4));

    writer.longReportingLines(longReportingLines);

    String expectedOutput = """
            Too long reporting lines: \r
            	Employee[id=2, firstName=Manager1, lastName=Smith, salary=72000, managerId=1] reports to [2, 3, 4]
            """;
    assertEquals(expectedOutput.trim(), outputStreamCaptor.toString().trim());
  }

  @Test
  void underpaidManagers_withManagersBelowIntendedSalary_shouldPrintExpectedOutput() {
    Map<Employee, Double> underpaidManagers = Map.of(MANAGER_1, 5000.0);

    writer.underpaidManagers(underpaidManagers);

    String expectedOutput = """
            Underpaid managers: \r
            	Employee[id=2, firstName=Manager1, lastName=Smith, salary=72000, managerId=1] earns less than intended by 5000.00
            """;
    assertEquals(expectedOutput.trim(), outputStreamCaptor.toString().trim());
  }

  @Test
  void overpaidManagers_withManagersAboveIntendedSalary_shouldPrintExpectedOutput() {
    Map<Employee, Double> overpaidManagers = Map.of(CEO, 3000.0);

    writer.overpaidManagers(overpaidManagers);

    String expectedOutput = """
            Overpaid managers: \r
            	Employee[id=1, firstName=CEO, lastName=Boss, salary=100000, managerId=null] earns more than intended by 3000.00
            """;
    assertEquals(expectedOutput.trim(), outputStreamCaptor.toString().trim());
  }
}
