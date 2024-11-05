package com.epam.swissre.interview.orghierarchy.config;


import com.epam.swissre.interview.orghierarchy.exception.ConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * The {@link ConfigLoader} reads and provides the required configuration values.
 */
public class ConfigLoader {

  static final String MAX_MANAGERS_TO_CEO = "maxManagersToCEO";
  static final String CSV_MAX_LINE_COUNT = "csv.maxLineCount";
  static final String CSV_DEFAULT_SOURCE = "csv.defaultSource";
  static final String MIN_SALARY_FACTOR_FOR_MANAGERS = "minSalaryFactorForManagers";
  static final String MAX_SALARY_FACTOR_FOR_MANAGERS = "maxSalaryFactorForManagers";
  private static final Map<Class<?>, Object> PARSED_CONFIGS = new HashMap<>();

  /**
   * Gets the reporting configuration from the properties file.
   *
   * @return an instance of {@link ReportingConfig} with loaded values
   * @throws ConfigurationException if required configuration properties are missing or invalid
   */
  public static ReportingConfig getReportingConfig() {
    return (ReportingConfig) PARSED_CONFIGS.computeIfAbsent(ReportingConfig.class,
        c -> parseReportingConfig(Holder.PROPERTIES));
  }

  /**
   * Gets the CSV source configuration from the properties file.
   *
   * @return an instance of {@link ReportingConfig} with loaded values
   * @throws ConfigurationException if required configuration properties are missing or invalid
   */
  public static CsvSourceConfig getCsvSourceConfig() {
    return (CsvSourceConfig) PARSED_CONFIGS.computeIfAbsent(CsvSourceConfig.class,
        c -> parseCsvSourceConfig(Holder.PROPERTIES));
  }

  static CsvSourceConfig parseCsvSourceConfig(Properties properties) {
    try {
      int maxLineCount = Integer.parseInt(getRequiredProperty(properties, CSV_MAX_LINE_COUNT));

      assertIsPositive(maxLineCount, CSV_MAX_LINE_COUNT);

      return new CsvSourceConfig(getRequiredProperty(properties, CSV_DEFAULT_SOURCE), maxLineCount);
    } catch (NumberFormatException e) {
      throw new ConfigurationException("Invalid format for configuration properties", e);
    }
  }

  static ReportingConfig parseReportingConfig(Properties properties) {
    try {
      int maxManagersToCEO = Integer.parseInt(getRequiredProperty(properties, MAX_MANAGERS_TO_CEO));
      double minSalaryFactorForManagers = Double.parseDouble(
          getRequiredProperty(properties, MIN_SALARY_FACTOR_FOR_MANAGERS));
      double maxSalaryFactorForManagers = Double.parseDouble(
          getRequiredProperty(properties, MAX_SALARY_FACTOR_FOR_MANAGERS));

      assertIsPositive(maxManagersToCEO, MAX_MANAGERS_TO_CEO);
      assertIsPositive(minSalaryFactorForManagers, MIN_SALARY_FACTOR_FOR_MANAGERS);
      assertIsPositive(maxSalaryFactorForManagers, MAX_SALARY_FACTOR_FOR_MANAGERS);

      return new ReportingConfig(maxManagersToCEO, minSalaryFactorForManagers,
          maxSalaryFactorForManagers);
    } catch (NumberFormatException e) {
      throw new ConfigurationException("Invalid format for configuration properties", e);
    }
  }

  private static void assertIsPositive(Number value, String propertyName) {
    if (value.doubleValue() < 0) {
      throw new ConfigurationException(
          String.format("Found value %.2f for the property %s while it must be greater than zero",
              value.doubleValue(), propertyName));
    }
  }

  private static String getRequiredProperty(Properties properties, String propertyName) {
    return assertNotNull(properties.getProperty(propertyName), propertyName);
  }

  private static String assertNotNull(String value, String propertyName) {
    if (value == null) {
      throw new ConfigurationException(
          String.format("No value found for the property %s", propertyName));
    }
    return value;
  }

  // lazy loading singleton of config properties
  private static class Holder {

    private static final String SYS_PROP_CONFIG_PATH = "config-file";

    private static final String DEFAULT_CONFIG_FILE = "config.properties";
    // not protecting against mutation, as it is not exposed outside ConfigLoader
    static final Properties PROPERTIES = loadProperties();

    private static Properties loadProperties() {
      String configPath = System.getProperty(SYS_PROP_CONFIG_PATH, DEFAULT_CONFIG_FILE);
      try (InputStream input = ConfigLoader.class.getClassLoader()
          .getResourceAsStream(configPath)) {
        if (input == null) {
          throw new ConfigurationException("Configuration file not found: " + configPath);
        }
        Properties properties = new Properties();
        properties.load(input);
        return properties;
      } catch (IOException e) {
        throw new ConfigurationException("Error reading configuration file: " + configPath, e);
      }
    }
  }
}
