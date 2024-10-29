package com.epam.swissre.interview.orghierarchy.exception;

/**
 * Exception thrown when there is an issue with an employee's CSV line.
 */
public class EmployeeCsvLineException extends OrgHierarchyException {

  public EmployeeCsvLineException(String message, Throwable cause) {
    super(message, cause);
  }
}
