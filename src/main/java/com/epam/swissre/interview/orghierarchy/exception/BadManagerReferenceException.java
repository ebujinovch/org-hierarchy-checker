package com.epam.swissre.interview.orghierarchy.exception;

/**
 * Exception thrown when the manager reference points to a missing employee or there is a wrong
 * number of employees with null managerId
 */
public class BadManagerReferenceException extends OrgHierarchyException {

  public BadManagerReferenceException(String message) {
    super(message);
  }
}
