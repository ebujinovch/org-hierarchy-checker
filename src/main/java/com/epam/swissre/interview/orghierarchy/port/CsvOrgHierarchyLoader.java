package com.epam.swissre.interview.orghierarchy.port;

import com.epam.swissre.interview.orghierarchy.config.CsvSourceConfig;
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
 * The {@code CsvOrgHierarchyLoader} class loads and parses organizational hierarchy data from a CSV
 * file. It converts CSV lines into {@link Employee} objects and constructs an {@link Organization}
 * instance populated with these employees.
 * <p>
 * The class enforces constraints on the input data, including a maximum number of employees and
 * proper formatting of CSV lines.
 * </p>
 */
class CsvOrgHierarchyLoader implements OrgHierarchyLoader {

  private final CsvSourceConfig config;

  public CsvOrgHierarchyLoader(CsvSourceConfig csvSourceConfig) {
    config = csvSourceConfig;
  }

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
   * @param source optional path to the CSV file. Defaulting to a default path specified in the
   *               config
   * @return a new {@link Organization} instance with loaded employees
   * @throws BadCsvFileException      if the CSV file is missing, inaccessible, or too long
   * @throws EmployeeCsvLineException if an employee line is improperly formatted
   */
  @Override
  public Organization loadOrgHierarchy(String source) {
    String actualSource =
        source == null || source.trim().isEmpty() ? config.defaultSource() : source;
    try (Reader fileReader = new FileReader(actualSource)) {
      Organization organization = loadOrgHierarchyCsv(fileReader);
      System.out.println("Loaded the following organization: " + organization);
      return organization;
    } catch (IOException e) {
      throw new BadCsvFileException("CSV file is missing or inaccessible: " + actualSource, e);
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
  Organization loadOrgHierarchyCsv(Reader reader) {
    List<String> lines;

    try (reader) {
      lines = new BufferedReader(reader).lines().toList();
    } catch (UncheckedIOException | IOException e) {
      throw new BadCsvFileException("Error reading from the provided reader.", e);
    }

    if (lines.size() > config.maxLineCount()) {
      throw new BadCsvFileException(String.format(
          "The provided file is too long: contains %d rows, while the allowed max is %d",
          lines.size(), config.maxLineCount()));
    }

    Organization organization = new Organization();

    lines.stream()
        .skip(1) // Skip the header row and parse each line
        .filter(line -> !line.trim().isEmpty())
        .forEach(line -> {
          try {
            organization.addEmployee(parseEmployee(line));
          } catch (IllegalArgumentException e) {
            throw new EmployeeCsvLineException("Error parsing employee data: " + line, e);
          }
        });

    return organization;
  }
}
