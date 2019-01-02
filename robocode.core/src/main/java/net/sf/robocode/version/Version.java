/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.version;


/**
 * @author Flemming N. Larsen (original)
 */
public class Version implements Comparable<Object> {

	private final String version;

	// The allowed format is <major>.<minor>.<revision>.<build> where all of these are ints
	private final int major;
	private final int minor;
	private final int revision;
	private final int build;

	// <maturity> <maturity version>, e.g. in "Beta 3" the maturity is 2, and maturity version is 3 
	private final int maturity; // Alpha is 1, Beta is 2, Final is 3
	private final int maturity_version; // The number following e.g. "Alpha" or "Beta"

	public Version(String version) {
		
		// Validate version format
		if (!version.matches("\\s*[0-9]+\\.[0-9]+(\\.[0-9]+)?(\\.[0-9]+)?(\\s?(([aA]lpha)|([bB]eta))(\\s?[0-9]+)?)?\\s*")) {
			throw new IllegalArgumentException("The format of the version string is not a valid");
		}
		version = version.trim();
		this.version = version;

		// Split the version number into its integer numbers
		final String[] numbers = version.split("\\.");

		// Parse the major version
		int major = 0;

		if (numbers.length >= 1) {
			try {
				major = Integer.parseInt(numbers[0]);
			} catch (NumberFormatException ignore) {}
		}
		this.major = major;

		// Parse the minor version
		int minor = 0;

		if (numbers.length >= 2) {
			try {
				String[] split = numbers[1].split("\\s++|([aA]lpha)|([bB]eta)");
				
				minor = Integer.parseInt(split[0]);
			} catch (NumberFormatException ignore) {}
		}
		this.minor = minor;

		// Parse the revision
		int revision = 0;

		if (numbers.length >= 3) {
			try {
				String[] split = numbers[2].split("\\s++|([aA]lpha)|([bB]eta)");

				revision = Integer.parseInt(split[0]);
			} catch (NumberFormatException ignore) {}
		}
		this.revision = revision;

		// Parse the build number
		int build = 0;

		if (numbers.length >= 4) {
			try {
				String[] split = numbers[3].split("\\s++|([aA]lpha)|([bB]eta)");

				build = Integer.parseInt(split[0]);
			} catch (NumberFormatException ignore) {}
		}
		this.build = build;

		// Parse the maturity version, e.g. "Beta 1"
		int maturity;
		int maturity_version = 1;

		if (isAlpha()) {
			maturity = 1;
			final String[] split = version.split("[aA]lpha");

			if (split.length >= 2) {
				maturity_version = Integer.parseInt(split[1].trim());
			}
		} else if (isBeta()) {
			maturity = 2;
			final String[] split = version.split("[bB]eta");

			if (split.length >= 2) {
				maturity_version = Integer.parseInt(split[1].trim());
			}
		} else {
			maturity = 3;
		}
		this.maturity = maturity;
		this.maturity_version = maturity_version;
	}

	public boolean isAlpha() {
		return (version.matches(".*[aA]lpha.*"));
	}

	public boolean isBeta() {
		return (version.matches(".*[bB]eta.*"));
	}

	public boolean isFinal() {
		return !(isAlpha() || isBeta());
	}

	public int getMajor() {
		return major;
	}

	public int getMinor() {
		return minor;
	}

	public int getRevision() {
		return revision;
	}

	public int getBuild() {
		return build;
	}

	@Override
	public String toString() {
		return version;
	}

	public int compareTo(Object o) {
		if (o == null) {
			throw new IllegalArgumentException("The input object cannot be null");
		}
		if (o instanceof String) {
			return compareTo(new Version((String) o));
		}
		if (o instanceof Version) {
			Version v = (Version) o;

			long delta = getVersionLong() - v.getVersionLong();

			return (delta == 0) ? 0 : (delta < 0) ? -1 : 1;
		}
		throw new IllegalArgumentException("The input object must be a String or Version object");
	}

	private long getVersionLong() {
		return ((long) major << 40) + ((long) minor << 32) + (revision << 24) + (build << 16) + (maturity << 8)
				+ maturity_version;
	}

	public static int compare(String oneVersion, String anotherVersion) {
		return new Version(oneVersion).compareTo(new Version(anotherVersion));
	}

	public static boolean isBeta(String version) {
		return new Version(version).isBeta();
	}

	public static boolean isFinal(String version) {
		return new Version(version).isFinal();
	}
}

