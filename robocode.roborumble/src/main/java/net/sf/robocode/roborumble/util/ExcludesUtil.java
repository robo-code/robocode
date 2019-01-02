/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.roborumble.util;


import java.util.Properties;


/**
 * Utility class for working with the EXCLUDE property for excluding robots from download and battles.
 * 
 * @author Flemming N. Larsen (original)
 */
public class ExcludesUtil {

	private static String[] excludes;

	/**
	 * Read and prepared exclude filters for robots that should not be downloaded or participate in battles.
	 *
	 * @param properties the properties to store.
	 * @return a string array containing the excluded robots or null if no excludes are defined.
	 */
	public static void setExcludes(Properties properties) {
		if (excludes != null) {
			return;
		}

		// Read and prepare exclude filters
		String exclude = properties.getProperty("EXCLUDE");
	
		if (exclude != null) {
			// Convert into regular expression
	
			// Dots must be dots, not "any character" in the regular expression
			exclude = exclude.replaceAll("\\.", "\\\\.");
	
			// The wildcard character ? corresponds to the regular expression .?
			exclude = exclude.replaceAll("\\?", ".?");
	
			// The wildcard character * corresponds to the regular expression .*
			exclude = exclude.replaceAll("\\*", ".*");
	
			// Split the exclude line into independent exclude filters that are trimmed for white-spaces
			excludes = exclude.split("[,;]+"); // white spaces are allowed in the filter due to robot version numbers
		}
	}

	/**
	 * Checks if a participant is excluded.
	 *
	 * @param botNameVersion the name and version of the participant.
	 * @return true if the participant is excluded; false otherwise.
	 */
	public static boolean isExcluded(String botNameVersion) {
		if (excludes == null) {
			return false;
		}

		// Check the name against all exclude filters
		for (int i = excludes.length - 1; i >= 0; i--) {
			try {
				if (botNameVersion.matches(excludes[i])) {
					return true;
				}
			} catch (java.util.regex.PatternSyntaxException e) {
				// Clear the current exclude if the syntax is illegal (for next time this method is called)
				excludes[i] = "";
			}
		}

		// Not excluded
		return false;
	}
}
