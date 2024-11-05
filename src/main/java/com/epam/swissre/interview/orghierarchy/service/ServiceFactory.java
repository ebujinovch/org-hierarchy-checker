package com.epam.swissre.interview.orghierarchy.service;

import com.epam.swissre.interview.orghierarchy.config.ConfigLoader;
import com.epam.swissre.interview.orghierarchy.port.ConsoleReportsWriter;
import com.epam.swissre.interview.orghierarchy.port.CsvOrgHierarchyLoader;

/**
 * The {@link ServiceFactory} is responsible for building and wiring together the instances of the
 * required services
 */
public interface ServiceFactory {

  /**
   * Static factory method to instantiate an {@link OrgHierarchyAnalyzerService} with all the
   * dependencies
   *
   * @return a new instance of an {@link OrgHierarchyAnalyzerService}
   */
  static OrgHierarchyAnalyzerService newOrgHierarchyAnalyzerService() {
    // can be parameterized if/when other implementations of the service, loader, or writer are available
    return new SimpleOrgHierarchyAnalyzerService(
        new CsvOrgHierarchyLoader(ConfigLoader.getCsvSourceConfig()),
        new ConsoleReportsWriter(System.out),
        new SimpleOrgHierarchyReportingService(ConfigLoader.getReportingConfig()));
  }
}
