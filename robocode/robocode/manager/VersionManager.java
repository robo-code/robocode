/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Code cleanup
 *     - Changed to give notification only if the available version number is
 *       greater than the version retrieved from the robocode.jar file, and only
 *       give warning if the user rejects downloading a new version, if the new
 *       version is a final version
 *     - Changed the checkdate time interval from 10 days to 5 days
 *     - Updated to use methods from WindowUtil, FileUtil, Logger, which replaces
 *       methods that have been (re)moved from the Utils and Constants class
 *     - Added a connect timeout of 5 seconds when checking for a new version
 *     - Added missing close() on input stream readers
 *     - Added the Version class for comparing versions with compareTo()
 *******************************************************************************/
package robocode.manager;


import robocode.dialog.WindowUtil;
import robocode.io.FileUtil;
import static robocode.io.Logger.logError;
import static robocode.io.Logger.logMessage;

import javax.swing.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Date;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public final class VersionManager implements IVersionManager {
	private final static String INSTALL_URL = "http://robocode.sourceforge.net/installer";

	private static String version;
	private final RobocodeManager manager;

	public VersionManager(RobocodeManager manager) {
		this.manager = manager;
	}

	public void checkUpdateCheck() {
		Date lastCheckedDate = manager.getProperties().getVersionChecked();

		Date today = new Date();

		if (lastCheckedDate == null) {
			lastCheckedDate = today;
			manager.getProperties().setVersionChecked(lastCheckedDate);
			manager.saveProperties();
		}
		Calendar checkDate = Calendar.getInstance();

		checkDate.setTime(lastCheckedDate);
		checkDate.add(Calendar.DATE, 5);

		if (checkDate.getTime().before(today) && checkForNewVersion(false)) {
			manager.getProperties().setVersionChecked(today);
			manager.saveProperties();
		}
	}

	public boolean checkForNewVersion(boolean notifyNoUpdate) {
		URL url;

		try {
			url = new URL("http://robocode.sourceforge.net/version/version.html");
		} catch (MalformedURLException e) {
			logError("Unable to check for new version: ", e);
			if (notifyNoUpdate) {
				WindowUtil.messageError("Unable to check for new version: " + e);
			}
			return false;
		}

		InputStream inputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader reader = null;

		try {
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

			String newVersLine = reader.readLine();
			String curVersLine = getVersion();

			boolean newVersionAvailable = false;

			if (newVersLine != null && curVersLine != null) {
				Version newVersion = new Version(newVersLine);

				if (newVersion.compareTo(curVersLine) > 0) {
					newVersionAvailable = true;

					if (JOptionPane.showConfirmDialog(manager.getWindowManager().getRobocodeFrame(),
							"Version " + newVersion + " of Robocode is now available.  Would you like to download it?",
							"Version " + newVersion + " available", JOptionPane.YES_NO_OPTION)
							== JOptionPane.YES_OPTION) {
						try {
							BrowserManager.openURL(INSTALL_URL);
						} catch (IOException e) {
							JOptionPane.showMessageDialog(manager.getWindowManager().getRobocodeFrame(), e.getMessage(),
									"Unable to open browser!", JOptionPane.INFORMATION_MESSAGE);
						}
					} else if (newVersion.isFinal()) {
						JOptionPane.showMessageDialog(manager.getWindowManager().getRobocodeFrame(),
								"It is highly recommended that you always download the latest version.  You may get it at "
								+ INSTALL_URL,
								"Update when you can!",
								JOptionPane.INFORMATION_MESSAGE);
					}
				}
			}
			if (!newVersionAvailable && notifyNoUpdate) {
				JOptionPane.showMessageDialog(manager.getWindowManager().getRobocodeFrame(),
						"You have version " + version + ".  This is the latest version of Robocode.", "No update available",
						JOptionPane.INFORMATION_MESSAGE);
			}
		} catch (IOException e) {
			logError("Unable to check for new version: " + e);
			if (notifyNoUpdate) {
				WindowUtil.messageError("Unable to check for new version: " + e);
			}
			return false;
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException ignored) {}
			}
			if (inputStreamReader != null) {
				try {
					inputStreamReader.close();
				} catch (IOException ignored) {}
			}
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException ignored) {}
			}
		}

		return true;
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

	public static int getVersionInt() {
		if (version == null) {
			version = getVersionFromJar();
		}
		if (version == "unknown") {
			return 0;
		}
		final Version v = new Version(version);

		return v.getEra() * 0x010000 + v.getMajor() * 0x000100 + v.getMinor() * 0x000001;
	}

	private static String getVersionFromJar() {
		String versionString = null;

		BufferedReader in = null;

		try {
			URL versionsUrl = VersionManager.class.getResource("/resources/versions.txt");

			if (versionsUrl == null) {
				logError("no url");
				versionString = "unknown";
			} else {
				in = new BufferedReader(new InputStreamReader(versionsUrl.openStream()));

				versionString = in.readLine();
				while (versionString != null && !versionString.substring(0, 8).equalsIgnoreCase("Version ")) {
					versionString = in.readLine();
				}
			}
		} catch (FileNotFoundException e) {
			logError("No versions.txt file in robocode.jar.");
			versionString = "unknown";
		} catch (IOException e) {
			logError("IO Exception reading versions.txt from robocode.jar" + e);
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
			logError("Warning:  Getting version from file.");
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
			logError("No versions.txt file.");
			versionString = "unknown";
		} catch (IOException e) {
			logError("IO Exception reading versions.txt" + e);
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

	public static int compare(String a, String b) {
		return new Version(a).compareTo(new Version(b));
	}
}


class Version implements Comparable<Object> {

	private final String version;

	public Version(String version) {
		this.version = version.replaceAll("\\.\\s++", ".").replaceAll("\\s++|lpha|eta", "");
	}

	public boolean isAlpha() {
		return (version.matches(".*(A|a).*"));
	}

	public boolean isBeta() {
		return (version.matches(".*(B|b).*"));
	}

	public int getEra() {
		final String[] blocks = version.split(" ");

		if (blocks.length < 3) {
			throw new Error("Unexpected format");
		}
		final String[] numbers = blocks[1].split(".");

		if (numbers.length < 3) {
			throw new Error("Unexpected format");
		}
		return Integer.parseInt(numbers[0]);
	}

	public int getMajor() {
		final String[] blocks = version.split(" ");

		if (blocks.length < 3) {
			throw new Error("Unexpected format");
		}
		final String[] numbers = blocks[1].split(".");

		if (numbers.length < 3) {
			throw new Error("Unexpected format");
		}
		return Integer.parseInt(numbers[1]);
	}

	public int getMinor() {
		final String[] blocks = version.split(" ");

		if (blocks.length < 3) {
			throw new Error("Unexpected format");
		}
		final String[] numbers = blocks[1].split(".");

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

			String[] split1 = version.split(" ", 2);
			String[] split2 = v.version.split(" ", 2);

			if (split1[0].equalsIgnoreCase(split2[0])) {
				if (split1.length == 1) {
					return 1;
				}
				if (split2.length == 1) {
					return -1;
				}

				split1 = split1[1].split(" ", 2);
				split2 = split2[1].split(" ", 2);

				int compare = split1[0].compareToIgnoreCase(split2[0]);

				if (compare == 0) {
					if (split1.length == 1) {
						return -1;
					}
					if (split2.length == 1) {
						return 1;
					}
					return split1[1].compareToIgnoreCase(split2[1]);
				}
				return compare;
			}

			split1 = split1[0].split("\\.", 3);
			split2 = split2[0].split("\\.", 3);

			split1[0] = split1[0].trim();
			split2[0] = split2[0].trim();
			int compare = split1[0].compareToIgnoreCase(split2[0]);

			if (compare == 0) {
				if (split1.length == 1) {
					return -1;
				}
				if (split2.length == 1) {
					return 1;
				}

				split1[1] = (split1[1] + '\uffff').trim();
				split2[1] = (split2[1] + '\uffff').trim();
				compare = split1[1].compareToIgnoreCase(split2[1]);

				if (compare == 0) {
					if (split1.length == 2) {
						return -1;
					}
					if (split2.length == 2) {
						return 1;
					}

					split1[2] = (split1[2] + '\uffff').trim();
					split2[2] = (split2[2] + '\uffff').trim();
					compare = split1[2].compareToIgnoreCase(split2[2]);

					if (compare == 0) {
						if (split1.length == 3) {
							return -1;
						}
						if (split2.length == 3) {
							return 1;
						}
					}
					return compare;
				}
				return compare;
			}
			return compare;

		} else {
			throw new IllegalArgumentException("The input object must be a String or Version object");
		}
	}

	@Override
	public String toString() {
		return version;
	}
}
