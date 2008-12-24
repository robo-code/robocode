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

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public final class VersionManager implements IVersionManager {
	private static String version;

	public VersionManager() {}

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
			net.sf.robocode.io.Logger.logError("Unable to check for new version: ", e);
			newVersLine = null;
		} catch (IOException e) {
			net.sf.robocode.io.Logger.logError("Unable to check for new version: " + e);
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
		return getVersionStatic();
	}

	public static String getVersionStatic() {
		if (version == null) {
			version = getVersionFromJar();
		}
		return version;
	}

	public int getVersionInt() {
		if (version == null) {
			version = getVersionFromJar();
		}
		if (version.equals("unknown")) {
			return 0;
		}
		final Version v = new Version(version);

		return v.getEra() * 0x010000 + v.getMajor() * 0x000100 + v.getMinor();
	}

	private static String getVersionFromJar() {
		String versionString = null;

		BufferedReader in = null;

		try {
			URL versionsUrl = VersionManager.class.getResource("/versions.txt");

			if (versionsUrl == null) {
				net.sf.robocode.io.Logger.logError("no url");
				versionString = "unknown";
			} else {
				in = new BufferedReader(new InputStreamReader(versionsUrl.openStream()));

				versionString = in.readLine();
				while (versionString != null && !versionString.substring(0, 8).equalsIgnoreCase("Version ")) {
					versionString = in.readLine();
				}
			}
		} catch (FileNotFoundException e) {
			net.sf.robocode.io.Logger.logError("No versions.txt file in robocode.jar.");
			versionString = "unknown";
		} catch (IOException e) {
			net.sf.robocode.io.Logger.logError("IO Exception reading versions.txt from robocode.jar" + e);
			versionString = "unknown";
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ignored) {}
			}
		}

		String version = "unknown";

		if (versionString != null) {
			try {
				version = versionString.substring(8);
			} catch (Exception e) {
				version = "unknown";
			}
		}
		if (version.equals("unknown")) {
			net.sf.robocode.io.Logger.logError("Warning:  Getting version from file.");
			return getVersionFromFile();
		}
		return version;
	}

	private static String getVersionFromFile() {
		String versionString = null;

		FileReader fileReader = null;
		BufferedReader in = null;

		try {
			fileReader = new FileReader(new File(FileUtil.getCwd(), "versions.txt"));
			in = new BufferedReader(fileReader);

			versionString = in.readLine();
			while (versionString != null && !versionString.substring(0, 8).equalsIgnoreCase("Version ")) {
				versionString = in.readLine();
			}
		} catch (FileNotFoundException e) {
			net.sf.robocode.io.Logger.logError("No versions.txt file.");
			versionString = "unknown";
		} catch (IOException e) {
			net.sf.robocode.io.Logger.logError("IO Exception reading versions.txt" + e);
			versionString = "unknown";
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

		String version = "unknown";

		if (versionString != null) {
			try {
				version = versionString.substring(8);
			} catch (Exception e) {
				version = "unknown";
			}
		}
		return version;
	}

	public int compare(String a, String b) {
		return new Version(a).compareTo(new Version(b));
	}

	class Version implements Comparable<Object> {

		private final String version;

		public Version(String version) {
			this.version = version.replaceAll("Alpha", ".A.").replaceAll("Beta", ".B.").replaceAll("\\s++", ".").replaceAll(
					"\\.++", "\\.");
		}

		public boolean isAlpha() {
			return (version.matches(".*(A|a).*"));
		}

		public boolean isBeta() {
			return (version.matches(".*(B|b).*"));
		}

		public int getEra() {
			final String[] numbers = version.split("\\.");

			if (numbers.length < 3) {
				throw new Error("Unexpected format");
			}
			return Integer.parseInt(numbers[0]);
		}

		public int getMajor() {
			final String[] numbers = version.split("\\.");

			if (numbers.length < 3) {
				throw new Error("Unexpected format");
			}
			return Integer.parseInt(numbers[1]);
		}

		public int getMinor() {
			final String[] numbers = version.split("\\.");

			if (numbers.length < 3) {
				throw new Error("Unexpected format");
			}
			return Integer.parseInt(numbers[2]);
		}

		public boolean isFinal() {
			return !(isAlpha() || isBeta());
		}

		public int compareTo(Object o) {
			if (o == null) {
				throw new IllegalArgumentException();
			}
			if (o instanceof String) {
				return compareTo(new Version((String) o));
			} else if (o instanceof Version) {
				Version v = (Version) o;

				if (version.equalsIgnoreCase(v.version)) {
					return 0;
				}
				String[] left = version.split("[. \t]");
				String[] right = v.version.split("[. \t]");

				return compare(left, right);
			} else {
				throw new IllegalArgumentException("The input object must be a String or Version object");
			}
		}

		private int compare(String[] left, String[] right) {
			int i;

			for (i = 0; i < left.length && i < right.length; i++) {
				int res = left[i].compareToIgnoreCase(right[i]);

				if (res != 0) {
					return res;
				}
			}
			if (left.length > right.length) {
				if (left[i].equals("B") || left[i].equals("A")) {
					return -1;
				}
				return 1;
			}
			if (left.length < right.length) {
				if (right[i].equals("B") || right[i].equals("A")) {
					return 1;
				}
				return -1;
			}
			return 0;
		}

		@Override
		public String toString() {
			return version;
		}
	}

}
