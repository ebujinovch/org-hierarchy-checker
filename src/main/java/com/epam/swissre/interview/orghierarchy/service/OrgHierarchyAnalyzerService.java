package com.epam.swissre.interview.orghierarchy.service;

/**
 * The {@link OrgHierarchyAnalyzerService} loads and analyzes the organizational hierarchy indicated
 * by the source and provides the needed reports
 */
public interface OrgHierarchyAnalyzerService {

  /**
   * Loads and analyzes the organizational hierarchy indicated by the source, then provides the
   * needed reports
   *
   * @param source the source of the data with the organizational hierarchy
   */
  void analyze(String source);
}
