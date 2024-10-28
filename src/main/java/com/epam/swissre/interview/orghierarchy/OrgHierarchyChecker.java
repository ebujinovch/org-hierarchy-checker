package com.epam.swissre.interview.orghierarchy;

import com.epam.swissre.interview.orghierarchy.model.Organization;

public class OrgHierarchyChecker {
  public static void main(String[] args) {
    String filePath = args.length > 0 ? args[0] : "org-hierarchy-example-1.csv"; // Default file path

    Organization org = OrgHierarchyLoader.loadOrgHierarchyCsv(filePath);
    System.out.println(org);
  }
}
