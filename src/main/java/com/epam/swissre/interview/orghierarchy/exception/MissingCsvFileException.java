package com.epam.swissre.interview.orghierarchy.exception;

/**
 * Exception thrown when a CSV file is missing or inaccessible.
 */
public class MissingCsvFileException extends OrgHierarchyException {
  public MissingCsvFileException(String message, Throwable cause) {
    super(message, cause);
  }
}
