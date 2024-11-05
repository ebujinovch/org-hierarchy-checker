package com.epam.swissre.interview.orghierarchy.port;

import com.epam.swissre.interview.orghierarchy.model.Organization;

/**
 * The {@code OrgHierarchyLoader} is responsible for validating the input data, loading, and parsing
 * organizational hierarchy data from source.
 */
public interface OrgHierarchyLoader {

  /**
   * Loads the @{@link Organization} from the given {@code source}
   *
   * @param source the reference to the source of data
   * @return the loaded @{@link Organization}
   */
  Organization loadOrgHierarchy(String source);
}
