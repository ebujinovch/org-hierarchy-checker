package com.epam.swissre.interview.orghierarchy;

import com.epam.swissre.interview.orghierarchy.service.ServiceFactory;
import java.util.Arrays;

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

  /**
   * The main method serves as the entry point of the application. It loads the organization
   * hierarchy from a specified CSV file or a default file if no path is provided. It then performs
   * the analysis and prints the results to the console.
   *
   * @param args Command-line arguments, where the first argument can specify the path to the CSV
   *             file containing the employee data.
   */
  public static void main(String... args) {
    ServiceFactory.newOrgHierarchyAnalyzerService()
        .analyze(Arrays.stream(args).findFirst().orElse(null));
  }
}
