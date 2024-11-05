package com.epam.swissre.interview.orghierarchy.service;

import com.epam.swissre.interview.orghierarchy.config.ConfigLoader;
import com.epam.swissre.interview.orghierarchy.port.PortFactory;

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
        PortFactory.newCsvOrgHierarchyLoader(),
        PortFactory.newConsoleReportsWriter(),
        new SimpleOrgHierarchyReportingService(ConfigLoader.getReportingConfig()));
  }
}
