package com.epam.swissre.interview.orghierarchy.config;

import static com.epam.swissre.interview.orghierarchy.config.ConfigLoader.MAX_MANAGERS_TO_CEO;
import static com.epam.swissre.interview.orghierarchy.config.ConfigLoader.MAX_SALARY_FACTOR_FOR_MANAGERS;
import static com.epam.swissre.interview.orghierarchy.config.ConfigLoader.MIN_SALARY_FACTOR_FOR_MANAGERS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.epam.swissre.interview.orghierarchy.exception.ConfigurationException;
import java.util.Properties;
import org.junit.jupiter.api.Test;

class ConfigLoaderTest {

  private final Properties properties = new Properties();

  @Test
  void parseReportingConfig_withValidProperties_shouldReturnCorrectConfig() {
    properties.setProperty(MAX_MANAGERS_TO_CEO, "5");
    properties.setProperty(MIN_SALARY_FACTOR_FOR_MANAGERS, "1.2");
    properties.setProperty(MAX_SALARY_FACTOR_FOR_MANAGERS, "2.5");

    ReportingConfig config = ConfigLoader.parseReportingConfig(properties);

    assertNotNull(config);
    assertEquals(5, config.maxManagersToCEO());
    assertEquals(1.2, config.minSalaryFactorForManagers());
    assertEquals(2.5, config.maxSalaryFactorForManagers());
  }

  @Test
  void parseReportingConfig_withMissingProperty_shouldThrowConfigurationException() {
    properties.setProperty(MAX_MANAGERS_TO_CEO, "5");
    properties.setProperty(MIN_SALARY_FACTOR_FOR_MANAGERS, "1.2");

    ConfigurationException exception = assertThrows(ConfigurationException.class,
        () -> ConfigLoader.parseReportingConfig(properties));
    assertEquals("No value found for the property maxSalaryFactorForManagers",
        exception.getMessage());
  }

  @Test
  void parseReportingConfig_withInvalidIntegerProperty_shouldThrowConfigurationException() {
    properties.setProperty(MAX_MANAGERS_TO_CEO, "invalid");
    properties.setProperty(MIN_SALARY_FACTOR_FOR_MANAGERS, "1.2");
    properties.setProperty(MAX_SALARY_FACTOR_FOR_MANAGERS, "2.5");

    ConfigurationException exception = assertThrows(ConfigurationException.class,
        () -> ConfigLoader.parseReportingConfig(properties));
    assertEquals("Invalid format for configuration properties", exception.getMessage());
    assertInstanceOf(NumberFormatException.class, exception.getCause());
  }

  @Test
  void parseReportingConfig_withInvalidDoubleProperty_shouldThrowConfigurationException() {
    properties.setProperty(MAX_MANAGERS_TO_CEO, "5");
    properties.setProperty(MIN_SALARY_FACTOR_FOR_MANAGERS, "invalid");
    properties.setProperty(MAX_SALARY_FACTOR_FOR_MANAGERS, "2.5");

    ConfigurationException exception = assertThrows(ConfigurationException.class,
        () -> ConfigLoader.parseReportingConfig(properties));
    assertEquals("Invalid format for configuration properties", exception.getMessage());
    assertInstanceOf(NumberFormatException.class, exception.getCause());
  }

  @Test
  void parseReportingConfig_withNegativeIntegerValue_shouldThrowConfigurationException() {
    properties.setProperty(MAX_MANAGERS_TO_CEO, "-5");
    properties.setProperty(MIN_SALARY_FACTOR_FOR_MANAGERS, "1.2");
    properties.setProperty(MAX_SALARY_FACTOR_FOR_MANAGERS, "2.5");

    ConfigurationException exception = assertThrows(ConfigurationException.class,
        () -> ConfigLoader.parseReportingConfig(properties));
    assertEquals("Found value -5.00 for the property " + MAX_MANAGERS_TO_CEO
        + " while it must be greater than zero", exception.getMessage());
  }

  @Test
  void parseReportingConfig_withNegativeDoubleValue_shouldThrowConfigurationException() {
    properties.setProperty(MAX_MANAGERS_TO_CEO, "5");
    properties.setProperty(MIN_SALARY_FACTOR_FOR_MANAGERS, "-1.2");
    properties.setProperty(MAX_SALARY_FACTOR_FOR_MANAGERS, "2.5");

    ConfigurationException exception = assertThrows(ConfigurationException.class,
        () -> ConfigLoader.parseReportingConfig(properties));
    assertEquals("Found value -1.20 for the property " + MIN_SALARY_FACTOR_FOR_MANAGERS
        + " while it must be greater than zero", exception.getMessage());
  }

  @Test
  void parseCsvSourceConfig_withValidProperties_shouldReturnCorrectCsvConfig() {
    properties.setProperty(ConfigLoader.CSV_MAX_LINE_COUNT, "1000");
    properties.setProperty(ConfigLoader.CSV_DEFAULT_SOURCE, "default.csv");

    CsvSourceConfig csvConfig = ConfigLoader.parseCsvSourceConfig(properties);

    assertNotNull(csvConfig);
    assertEquals(1000, csvConfig.maxLineCount());
    assertEquals("default.csv", csvConfig.defaultSource());
  }

  @Test
  void parseCsvSourceConfig_withMissingMaxLineCount_shouldThrowConfigurationException() {
    properties.setProperty(ConfigLoader.CSV_DEFAULT_SOURCE, "default.csv");

    ConfigurationException exception = assertThrows(ConfigurationException.class,
        () -> ConfigLoader.parseCsvSourceConfig(properties));
    assertEquals("No value found for the property " + ConfigLoader.CSV_MAX_LINE_COUNT,
        exception.getMessage());
  }

  @Test
  void parseCsvSourceConfig_withInvalidMaxLineCount_shouldThrowConfigurationException() {
    properties.setProperty(ConfigLoader.CSV_DEFAULT_SOURCE, "default.csv");
    properties.setProperty(ConfigLoader.CSV_MAX_LINE_COUNT, "invalid");

    ConfigurationException exception = assertThrows(ConfigurationException.class,
        () -> ConfigLoader.parseCsvSourceConfig(properties));
    assertEquals("Invalid format for configuration properties", exception.getMessage());
    assertInstanceOf(NumberFormatException.class, exception.getCause());
  }

  @Test
  void getReportingConfig_shouldReturnCurrentConfigs() {
    ReportingConfig reportingConfig = ConfigLoader.getReportingConfig();
    assertEquals(4, reportingConfig.maxManagersToCEO());
    assertEquals(1.2, reportingConfig.minSalaryFactorForManagers());
    assertEquals(1.5, reportingConfig.maxSalaryFactorForManagers());
  }

  @Test
  void getCsvSourceConfig_shouldReturnCurrentConfigs() {
    CsvSourceConfig csvSourceConfig = ConfigLoader.getCsvSourceConfig();
    assertEquals("org-hierarchy-example-1.csv", csvSourceConfig.defaultSource());
    assertEquals(1001, csvSourceConfig.maxLineCount());
  }
}
