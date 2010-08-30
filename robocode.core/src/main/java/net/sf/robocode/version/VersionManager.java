/*******************************************************************************
 * Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package net.sf.robocode.version;


import net.sf.robocode.io.FileUtil;
import static net.sf.robocode.io.Logger.logError;
import static net.sf.robocode.io.Logger.logMessage;
import static net.sf.robocode.io.Logger.logWarning;
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

	private static final String VERSIONS_TXT = "versions.txt";
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
				logMessage("Update checking with http.");
				HttpURLConnection h = (HttpURLConnection) urlConnection;

				if (h.usingProxy()) {
					logMessage("http using proxy.");
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

	public String getVersionN() {
		Version v = getVersionInstance();

		return v.getMajor() + "." + v.getMinor() + "." + v.getRevision() + "." + v.getBuild();
	}

	public int getVersionAsInt() {
		Version v = getVersionInstance();

		return (v.getMajor() << 24) + (v.getMinor() << 16) + (v.getRevision() << 8) + v.getBuild();
	}

	private static String getVersionFromJar() {
		String versionString = null;

		BufferedReader in = null;

		try {
			URL versionsUrl = VersionManager.class.getResource("/" + VERSIONS_TXT);

			if (versionsUrl == null) {
				logError("The URL for the " + VERSIONS_TXT + " was not found");
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
			logError("No " + VERSIONS_TXT + " file in robocode.jar");
			versionString = UNKNOWN_VERSION;
		} catch (IOException e) {
			logError("Error while reading " + VERSIONS_TXT + " from robocode.jar: " + e);
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
			logWarning("Getting version from file");
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
			fileReader = new FileReader(new File(dir, VERSIONS_TXT));
			in = new BufferedReader(fileReader);

			versionString = in.readLine();
		} catch (FileNotFoundException e) {
			logError("No " + VERSIONS_TXT + " file.");
			versionString = UNKNOWN_VERSION;
		} catch (IOException e) {
			logError("IO Exception reading " + VERSIONS_TXT, e);
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
}
