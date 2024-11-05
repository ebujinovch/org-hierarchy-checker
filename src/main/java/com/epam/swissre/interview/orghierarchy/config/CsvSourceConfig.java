package com.epam.swissre.interview.orghierarchy.config;

/**
 * The {@link CsvSourceConfig} class contains the configuration values for the loading of
 * organization hierarchy data from a CSV file
 *
 * @param defaultSource the default path to the file used if none provided from command line
 * @param maxLineCount  the maximum acceptable number of lines in the file
 */
public record CsvSourceConfig(String defaultSource, int maxLineCount) {

}
