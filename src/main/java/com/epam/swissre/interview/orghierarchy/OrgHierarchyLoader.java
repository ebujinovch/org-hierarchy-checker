package com.epam.swissre.interview.orghierarchy;

import com.epam.swissre.interview.orghierarchy.exception.BadCsvFileException;
import com.epam.swissre.interview.orghierarchy.exception.EmployeeCsvLineException;
import com.epam.swissre.interview.orghierarchy.model.Employee;
import com.epam.swissre.interview.orghierarchy.model.Organization;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.List;

/**
 * The {@code OrgHierarchyLoader} class is responsible for loading and parsing organizational
 * hierarchy data from a CSV file or any text source. It converts CSV lines into {@link Employee}
 * objects and constructs an {@link Organization} instance populated with these employees.
 * <p>
 * The class enforces constraints on the input data, including a maximum number of employees and
 * proper formatting of CSV lines.
 * </p>
 */
public final class OrgHierarchyLoader {

  // assuming that 1000 is the max number of employees, adding another one for the header
  private static final int MAX_LINE_COUNT = 1001;

  /**
   * Parses a single line of CSV data into an {@link Employee} object.
   *
   * @param line the CSV line representing an employee
   * @return the {@link Employee} object created from the line
   * @throws IllegalArgumentException if the line format is invalid
   */
  private static Employee parseEmployee(String line) {
    String[] fields = Arrays.stream(line.split(","))
        .map(String::trim)
        .toArray(String[]::new);
    if (fields.length < 4 || fields.length > 5) {
      throw new IllegalArgumentException("Invalid CSV line format: " + line);
    }

    int id = Integer.parseInt(fields[0]);
    String firstName = fields[1];
    String lastName = fields[2];
    int salary = Integer.parseInt(fields[3]);
    Integer managerId = fields.length == 5 && !fields[4].isEmpty()
        ? Integer.parseInt(fields[4]) : null;

    return new Employee(id, firstName, lastName, salary, managerId);
  }

  /**
   * Reads employee data from a CSV file path and returns a new {@link Organization} instance
   * populated with employees.
   *
   * @param filePath the path to the CSV file
   * @return a new {@link Organization} instance with loaded employees
   * @throws BadCsvFileException      if the CSV file is missing, inaccessible, or too long
   * @throws EmployeeCsvLineException if an employee line is improperly formatted
   */
  public Organization loadOrgHierarchyCsv(String filePath) {
    try (Reader fileReader = new FileReader(filePath)) {
      return loadOrgHierarchyCsv(fileReader);
    } catch (IOException e) {
      throw new BadCsvFileException("CSV file is missing or inaccessible: " + filePath, e);
    }
  }

  /**
   * Reads employee data from a given {@link Reader} and returns a new {@link Organization} instance
   * populated with employees. This method allows loading from any source of text, including test
   * strings.
   *
   * @param reader the {@link Reader} providing CSV content
   * @return a new {@link Organization} instance with loaded employees
   * @throws BadCsvFileException      if there was an error reading from the file, or it is too
   *                                  long
   * @throws EmployeeCsvLineException if an employee line is improperly formatted
   */
  public Organization loadOrgHierarchyCsv(Reader reader) {
    List<String> lines;

    try (reader) {
      lines = new BufferedReader(reader).lines().toList();
    } catch (UncheckedIOException | IOException e) {
      throw new BadCsvFileException("Error reading from the provided reader.", e);
    }

    if (lines.size() > MAX_LINE_COUNT) {
      throw new BadCsvFileException(String.format(
          "The provided file is too long: contains %d rows, while the allowed max is %d",
          lines.size(), MAX_LINE_COUNT));
    }

    Organization organization = new Organization();

    lines.stream()
        .skip(1) // Skip the header row and parse each line
        .filter(line -> !line.trim().isEmpty())
        .forEach(line -> {
          try {
            Employee employee = parseEmployee(line);
            organization.addEmployee(employee);
          } catch (IllegalArgumentException e) {
            throw new EmployeeCsvLineException("Error parsing employee data: " + line, e);
          }
        });

    return organization;
  }
}
