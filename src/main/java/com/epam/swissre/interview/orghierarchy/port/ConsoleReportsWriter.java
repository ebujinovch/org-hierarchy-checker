package com.epam.swissre.interview.orghierarchy.port;

import com.epam.swissre.interview.orghierarchy.model.Employee;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Outputs the reports on the organization hierarchy to the console.
 */
public class ConsoleReportsWriter implements ReportsWriter {

  private final PrintStream outputStream;

  public ConsoleReportsWriter(PrintStream outputStream) {
    this.outputStream = outputStream;
  }

  /**
   * Converts a map of key-value pairs into a formatted string output.
   *
   * @param map           The map to be converted to a string.
   * @param lineFormatter A function that formats each entry in the map to a string.
   * @param <K>           The type of keys in the map.
   * @param <V>           The type of values in the map.
   * @return A formatted string representing the contents of the map.
   */
  static <K, V> String mapToOutputLines(Map<K, V> map,
      Function<Entry<K, V>, String> lineFormatter) {
    return map.entrySet().stream()
        .map(lineFormatter)
        .collect(
            Collectors.joining("," + System.lineSeparator() + "\t", System.lineSeparator() + "\t",
                ""));
  }

  private <K, V> void doWriteToStream(String prefix, String lineFormat, Map<K, V> map) {
    outputStream.println(
        prefix + mapToOutputLines(map, e -> String.format(lineFormat, e.getKey(), e.getValue())));
  }

  @Override
  public void longReportingLines(Map<Employee, List<Integer>> longReportingLines) {
    doWriteToStream("Too long reporting lines: ", "%s reports to %s", longReportingLines);
  }

  @Override
  public void underpaidManagers(Map<Employee, Double> underpaidManagers) {
    doWriteToStream("Underpaid managers: ", "%s earns less than intended by %.2f",
        underpaidManagers);
  }

  @Override
  public void overpaidManagers(Map<Employee, Double> overpaidManagers) {
    doWriteToStream("Overpaid managers: ", "%s earns more than intended by %.2f", overpaidManagers);
  }
}
