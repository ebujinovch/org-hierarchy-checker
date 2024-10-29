package com.epam.swissre.interview.orghierarchy.exception;

/**
 * Exception is thrown when there is a circular dependency between employees
 */
public class CircularReferenceException extends OrgHierarchyException {

  public CircularReferenceException(String message) {
    super(message);
  }
}
