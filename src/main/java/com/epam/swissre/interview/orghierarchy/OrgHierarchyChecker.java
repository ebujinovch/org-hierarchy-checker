package com.epam.swissre.interview.orghierarchy;

import com.epam.swissre.interview.orghierarchy.model.Employee;
import com.epam.swissre.interview.orghierarchy.model.Organization;
import com.epam.swissre.interview.orghierarchy.service.OrgHierarchyAnalyzerService;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The {@code OrgHierarchyChecker} class is responsible for loading an organizational hierarchy from
 * a CSV file, analyzing the structure, and reporting findings related to employee salaries and
 * reporting lines.
 * <p>
 * The analysis checks for the following:
 * <ul>
 *   <li>Managers who are underpaid based on the average salary of their direct reports.</li>
 *   <li>Managers who are overpaid based on the average salary of their direct reports.</li>
 *   <li>Employees with too long reporting lines to the CEO.</li>
 * </ul>
 * The results of the analysis are printed to the console.
 * </p>
 */
public class OrgHierarchyChecker {

  private static final double FACTOR_MIN_WAGE_ABOVE_AVERAGE = 1.2;
  private static final double FACTOR_MAX_WAGE_ABOVE_AVERAGE = 1.5;
  private static final int MAX_MANAGERS_TO_CEO = 4;

  /**
   * The main method serves as the entry point of the application. It loads the organization
   * hierarchy from a specified CSV file or a default file if no path is provided. It then performs
   * the analysis and prints the results to the console.
   *
   * @param args Command-line arguments, where the first argument can specify the path to the CSV
   *             file containing the employee data.
   */
  public static void main(String... args) {
    // File path from arg or default
    String filePath = args.length > 0 ? args[0] : "org-hierarchy-example-1.csv";

    Organization organization = new OrgHierarchyLoader().loadOrgHierarchyCsv(filePath);
    OrgHierarchyAnalyzerService service = new OrgHierarchyAnalyzerService();

    Map<Employee, List<Integer>> longReportingLines = service.getLongReportingLines(organization,
        MAX_MANAGERS_TO_CEO);
    Map<Employee, Double> underpaidManagers = service.getUnderpaidManagers(organization,
        FACTOR_MIN_WAGE_ABOVE_AVERAGE);
    Map<Employee, Double> overpaidManagers = service.getOverpaidManagers(organization,
        FACTOR_MAX_WAGE_ABOVE_AVERAGE);

    System.out.println(organization);

    System.out.println("Too long reporting lines: " + mapToOutputLines(longReportingLines,
        e -> e.getKey() + " reports to " + e.getValue()));

    System.out.println("Underpaid managers: " + mapToOutputLines(underpaidManagers,
        e -> String.format("%s earns less than intended by %.2f", e.getKey(), e.getValue())));

    System.out.println("Overpaid managers: " + mapToOutputLines(overpaidManagers,
        e -> String.format("%s earns more than intended by %.2f", e.getKey(), e.getValue())));
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
}
