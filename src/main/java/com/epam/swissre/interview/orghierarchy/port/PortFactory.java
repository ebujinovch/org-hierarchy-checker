package com.epam.swissre.interview.orghierarchy.port;

import com.epam.swissre.interview.orghierarchy.config.ConfigLoader;

/**
 * Factory interface for creating instances of organization hierarchy-related ports.
 * Provides methods for obtaining implementations of {@link ReportsWriter} and {@link OrgHierarchyLoader}.
 * This factory helps to decouple the client code from specific implementations.
 */
public interface PortFactory {

  /**
   * Creates a new instance of {@link ReportsWriter} that outputs reports to the console.
   *
   * @return a {@link ConsoleReportsWriter} instance configured to write to {@code System.out}
   */
  static ReportsWriter newConsoleReportsWriter() {
    return new ConsoleReportsWriter(System.out);
  }

  /**
   * Creates a new instance of {@link OrgHierarchyLoader} that loads organization hierarchy data
   * from a CSV file, as specified in the application configuration.
   *
   * @return a {@link CsvOrgHierarchyLoader} instance configured with the CSV source from {@link ConfigLoader#getCsvSourceConfig()}
   */
  static OrgHierarchyLoader newCsvOrgHierarchyLoader() {
    return new CsvOrgHierarchyLoader(ConfigLoader.getCsvSourceConfig());
  }
}
