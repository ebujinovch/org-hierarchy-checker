package com.epam.swissre.interview.orghierarchy.exception;

/**
 * Exception thrown when a CSV file is missing, inaccessible or too long.
 */
public class BadCsvFileException extends OrgHierarchyException {

  public BadCsvFileException(String message) {
    super(message);
  }

  public BadCsvFileException(String message, Throwable cause) {
    super(message, cause);
  }
}
