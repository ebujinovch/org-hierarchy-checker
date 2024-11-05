package com.epam.swissre.interview.orghierarchy.exception;

/**
 * Exception thrown when a CSV file is missing, inaccessible or too long.
 */
public class ConfigurationException extends OrgHierarchyException {

  public ConfigurationException(String message) {
    super(message);
  }

  public ConfigurationException(String message, Throwable cause) {
    super(message, cause);
  }
}
