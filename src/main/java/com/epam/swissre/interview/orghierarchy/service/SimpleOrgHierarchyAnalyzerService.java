package com.epam.swissre.interview.orghierarchy.service;

import com.epam.swissre.interview.orghierarchy.model.Organization;
import com.epam.swissre.interview.orghierarchy.port.OrgHierarchyLoader;
import com.epam.swissre.interview.orghierarchy.port.ReportsWriter;

/**
 * Loads the organization hierarchy data from source using the {@link OrgHierarchyLoader}, then
 * builds the reports with the help of {@link OrgHierarchyReportingService}, and outputs them via
 * the {@link ReportsWriter}
 */
class SimpleOrgHierarchyAnalyzerService implements OrgHierarchyAnalyzerService {

  private final OrgHierarchyLoader loader;
  private final ReportsWriter writer;
  private final OrgHierarchyReportingService reportingService;

  public SimpleOrgHierarchyAnalyzerService(OrgHierarchyLoader loader, ReportsWriter writer,
      OrgHierarchyReportingService reportingService) {
    this.loader = loader;
    this.writer = writer;
    this.reportingService = reportingService;
  }

  @Override
  public void analyze(String source) {
    Organization organization = loader.loadOrgHierarchy(source);
    writer.longReportingLines(reportingService.getLongReportingLines(organization));
    writer.underpaidManagers(reportingService.getUnderpaidManagers(organization));
    writer.overpaidManagers(reportingService.getOverpaidManagers(organization));
  }
}
