package com.epam.swissre.interview.orghierarchy.config;

/**
 * The {@link ReportingConfig} class contains the configuration values of the organizational
 * hierarchy reporting
 *
 * @param maxManagersToCEO           the maximum allowed number of managers leading to the CEO
 * @param minSalaryFactorForManagers the minimum factor above the average salary that a manager
 *                                   should earn
 * @param maxSalaryFactorForManagers the maximum factor above the average salary that a manager
 *                                   should earn
 */
public record ReportingConfig(int maxManagersToCEO, double minSalaryFactorForManagers,
                              double maxSalaryFactorForManagers) {

}
