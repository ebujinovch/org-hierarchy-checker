package com.epam.swissre.interview.orghierarchy;

import com.epam.swissre.interview.orghierarchy.exception.EmployeeCsvLineException;
import com.epam.swissre.interview.orghierarchy.exception.MissingCsvFileException;
import com.epam.swissre.interview.orghierarchy.model.Employee;
import com.epam.swissre.interview.orghierarchy.model.Organization;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;

public final class OrgHierarchyLoader {

  /**
   * Reads employee data from a CSV file path and returns a new Organization instance populated with
   * employees.
   *
   * @param filePath the path to the CSV file
   * @return a new Organization instance with loaded employees
   * @throws MissingCsvFileException if the CSV file is missing or inaccessible
   */
  public static Organization loadOrgHierarchyCsv(String filePath) {
    try (Reader fileReader = new FileReader(filePath)) {
      return loadOrgHierarchyCsv(fileReader);
    } catch (IOException e) {
      throw new MissingCsvFileException("CSV file is missing or inaccessible: " + filePath, e);
    }
  }

  /**
   * Reads employee data from a given Reader and returns a new Organization instance populated with
   * employees. Allows loading from any source of text, including test strings.
   *
   * @param reader the Reader providing CSV content
   * @return a new Organization instance with loaded employees
   * @throws EmployeeCsvLineException if an employee line is improperly formatted
   */
  public static Organization loadOrgHierarchyCsv(Reader reader) {
    List<String> lines;

    try (reader) {
      lines = new BufferedReader(reader).lines().toList();
    } catch (IOException e) {
      throw new MissingCsvFileException("Error reading from the provided reader.", e);
    }

    Organization organization = new Organization();

    // Skip the header row and parse each line
    lines.stream().skip(1).forEach(line -> {
      if (!line.trim().isEmpty()) {
        try {
          Employee employee = parseEmployee(line);
          organization.addEmployee(employee);
        } catch (IllegalArgumentException e) {
          throw new EmployeeCsvLineException("Error parsing employee data: " + line, e);
        }
      }
    });

    return organization;
  }

  /**
   * Parses a single line of CSV data into an Employee object.
   *
   * @param line the CSV line representing an employee
   * @return the Employee object created from the line
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
}
