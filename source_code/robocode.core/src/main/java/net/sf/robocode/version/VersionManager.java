/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package net.sf.robocode.version;


import net.sf.robocode.io.FileUtil;
import static net.sf.robocode.io.Logger.logError;
import static net.sf.robocode.io.Logger.logMessage;
import net.sf.robocode.settings.ISettingsManager;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;


/**
 * @author Pavel Savara (original)
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public final class VersionManager implements IVersionManager {

	private static final String UNKNOWN_VERSION = "unknown";

	private static Version version;
	final ISettingsManager settingsManager;
	final boolean versionChanged;

	public VersionManager(ISettingsManager settingsManager) {
		this.settingsManager = settingsManager;
		if (settingsManager != null) {
			versionChanged = !settingsManager.getLastRunVersion().equals(getVersion());
			if (versionChanged) {
				settingsManager.setLastRunVersion(getVersion());
			}
		} else {
			versionChanged = false;			
		}
	}

	public String checkForNewVersion() {
		String newVersLine = null;
		InputStream inputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader reader = null;

		try {
			URL url = new URL("http://robocode.sourceforge.net/version/version.html");

			URLConnection urlConnection = url.openConnection();

			urlConnection.setConnectTimeout(5000);

			if (urlConnection instanceof HttpURLConnection) {
				net.sf.robocode.io.Logger.logMessage("Update checking with http.");
				HttpURLConnection h = (HttpURLConnection) urlConnection;

				if (h.usingProxy()) {
					net.sf.robocode.io.Logger.logMessage("http using proxy.");
				}
			}
			inputStream = urlConnection.getInputStream();
			inputStreamReader = new InputStreamReader(inputStream);
			reader = new BufferedReader(inputStreamReader);

			newVersLine = reader.readLine();

		} catch (MalformedURLException e) {
			logError("Unable to check for new version", e);
			newVersLine = null;
		} catch (IOException e) {
			logError("Unable to check for new version", e);
			newVersLine = null;
		} finally {
			FileUtil.cleanupStream(inputStream);
			FileUtil.cleanupStream(inputStreamReader);
			FileUtil.cleanupStream(reader);
		}
		return newVersLine;
	}

	public boolean isFinal(String version) {
		return new Version(version).isFinal();
	}

	public String getVersion() {
		return getVersionInstance().toString();
	}

	private static Version getVersionInstance() {
		if (version == null) {
			version = new Version(getVersionFromJar());
		}
		return version;
	}

	public boolean isLastRunVersionChanged() {
		return versionChanged;
	}

	public int getVersionAsInt() {
		Version v = getVersionInstance();

		return (v.getMajor() << 24) + (v.getMinor() << 16) + (v.getRevision() << 8) + v.getBuild();
	}

	private static String getVersionFromJar() {
		String versionString = null;

		BufferedReader in = null;

		try {
			URL versionsUrl = VersionManager.class.getResource("/versions.txt");

			if (versionsUrl == null) {
				logMessage("The URL for the versions.txt was not found");
				versionString = UNKNOWN_VERSION;
			} else {
				final URLConnection connection = versionsUrl.openConnection();

				connection.setUseCaches(false);
				final InputStream is = connection.getInputStream();

				in = new BufferedReader(new InputStreamReader(is));

				versionString = in.readLine();
				while (versionString != null && !versionString.substring(0, 8).equalsIgnoreCase("Version ")) {
					versionString = in.readLine();
				}
			}
		} catch (FileNotFoundException e) {
			logError("No versions.txt file in robocode.jar");
			versionString = UNKNOWN_VERSION;
		} catch (IOException e) {
			logError("IO Exception reading versions.txt from robocode.jar" + e);
			versionString = UNKNOWN_VERSION;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ignored) {}
			}
		}

		String version = UNKNOWN_VERSION;

		if (versionString != null && !versionString.equals(UNKNOWN_VERSION)) {
			try {
				version = versionString.substring(7);
			} catch (Exception ignore) {}
		}
		if (version.equals(UNKNOWN_VERSION)) {
			logMessage("Warning: Getting version from file");
			return getVersionFromFile();
		}
		return version;
	}

	private static String getVersionFromFile() {
		String versionString = null;

		FileReader fileReader = null;
		BufferedReader in = null;

		try {
			File dir = FileUtil.getCwd();

			if (System.getProperty("TESTING", "false").equals("true")) {
				dir = dir.getParentFile().getParentFile().getParentFile();
			}
			fileReader = new FileReader(new File(dir, "versions.txt"));
			in = new BufferedReader(fileReader);

			versionString = in.readLine();
		} catch (FileNotFoundException e) {
			logError("No versions.txt file.");
			versionString = UNKNOWN_VERSION;
		} catch (IOException e) {
			logError("IO Exception reading versions.txt" + e);
			versionString = UNKNOWN_VERSION;
		} finally {
			if (fileReader != null) {
				try {
					fileReader.close();
				} catch (IOException ignored) {}
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException ignored) {}
			}
		}

		String version = UNKNOWN_VERSION;

		if (versionString != null && !versionString.equals(UNKNOWN_VERSION)) {
			try {
				version = versionString.substring(7);
			} catch (Exception ignore) {}
		}
		return version;
	}

	public int compare(String a, String b) {
		return new Version(a).compareTo(new Version(b));
	}

	static class Version implements Comparable<Object> {

		private final String version;

		// The allowed format is <major>.<minor>.<revision>.<build> where all of these are ints
		private final int major;
		private final int minor;
		private final int revision;
		private final int build;

		// <maturity> <maturity version>, e.g. in "Beta 3" the maturity is 2, and maturity version is 3 
		public final int maturity; // Alpha is 1, Beta is 2, Final is 3
		public final int maturity_version; // The number following e.g. "Alpha" or "Beta"

		public Version(String version) {
			
			// Validate version format
			if (!version.matches(
					"\\s*[0-9]+\\.[0-9]+(\\.[0-9]+)?(\\.[0-9]+)?(\\s?(([aA]lpha)|([bB]eta))(\\s?[0-9])?)?\\s*")) {
				throw new IllegalArgumentException("The format of the version string is not a valid");
			}
			this.version = version;

			// Split the version number into its integer numbers
			final String[] numbers = version.trim().split("\\.");

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
	}
}
