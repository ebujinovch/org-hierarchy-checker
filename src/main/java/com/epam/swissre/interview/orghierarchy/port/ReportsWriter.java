package com.epam.swissre.interview.orghierarchy.port;

import com.epam.swissre.interview.orghierarchy.model.Employee;
import java.util.List;
import java.util.Map;

/**
 * The {@code OutputWriter} interface provides methods to output various reports on organizational
 * hierarchy. Implementations of this interface define how these reports are presented, such as
 * printing to the console.
 *
 * <p>Reports include:
 * <ul>
 *   <li>Employees with long reporting lines</li>
 *   <li>Underpaid managers relative to their position</li>
 *   <li>Overpaid managers relative to their position</li>
 * </ul>
 */
public interface ReportsWriter {

  /**
   * Outputs a report of employees with long reporting lines. Each employee is associated with a
   * list of IDs representing their reporting chain.
   *
   * @param longReportingLines a map where each key is an {@link Employee} with an extensive
   *                           reporting line, and each value is a list of integer IDs representing
   *                           the employees in their reporting chain
   */
  void longReportingLines(Map<Employee, List<Integer>> longReportingLines);

  /**
   * Outputs a report of managers identified as underpaid. Each manager is associated with a
   * calculated underpayment amount.
   *
   * @param underpaidManagers a map where each key is an {@link Employee} who is a manager
   *                          identified as underpaid, and each value is a double representing the
   *                          degree of underpayment
   */
  void underpaidManagers(Map<Employee, Double> underpaidManagers);

  /**
   * Outputs a report of managers identified as overpaid. Each manager is associated with a
   * calculated overpayment amount.
   *
   * @param overpaidManagers a map where each key is an {@link Employee} who is a manager identified
   *                         as overpaid, and each value is a double representing the degree of
   *                         overpayment
   */
  void overpaidManagers(Map<Employee, Double> overpaidManagers);
}
