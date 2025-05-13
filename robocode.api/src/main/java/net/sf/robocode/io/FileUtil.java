/*
 * Copyright (c) 2001-2025 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.io;


import java.io.Closeable;
import java.io.File;
import java.io.Flushable;
import java.io.IOException;
import java.nio.file.Files;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static net.sf.robocode.io.Logger.logError;


/**
 * This is a class for file utilization.
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public class FileUtil {

	// Current working directory
	private static File cwd;

	// Initializes the current working directory
	static {
		try {
			final String wd = System.getProperty("WORKINGDIRECTORY", "");

			FileUtil.setCwd(new File(wd));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the current working directory.
	 *
	 * @return a File for the current working directory
	 */
	public static File getCwd() {
		return cwd;
	}

	/**
	 * Changes the current working directory.
	 *
	 * @param cwd a File that is the new working directory
	 * @throws IOException if an I/O exception occurs
	 */
	public static void setCwd(File cwd) throws IOException {
		FileUtil.cwd = cwd.getCanonicalFile();
	}

	/**
	 * Quotes a file name if it contains white spaces and has not already been
	 * quoted.
	 *
	 * @param filename the file to quote
	 * @return a quoted version of the specified filename
	 */
	public static String quoteFileName(String filename) {
		if (filename.startsWith("\"") && filename.endsWith("\"")) {
			return filename;
		}
		if (System.getProperty("os.name").toLowerCase().startsWith("windows") && filename.startsWith("file://")) {
			filename = filename.substring(7);
		}
		if (filename.matches(".*\\s+?.*")) {
			return '"' + filename + '"';
		}
		return filename;
	}

	/**
	 * Copies a file into another file.
	 *
	 * @param srcFile  the input file to copy
	 * @param destFile the output file to copy to
	 * @throws IOException if an I/O exception occurs
	 */
	public static void copy(File srcFile, File destFile) throws IOException {
		if (srcFile.equals(destFile)) {
			throw new IOException("You cannot copy a file onto itself");
		}
		try {
			Files.copy(srcFile.toPath(), destFile.toPath(), REPLACE_EXISTING);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * Creates a directory if it does not exist already
	 *
	 * @param dir the File that represents the new directory to create.
	 * @return the created directory
	 */
	public static File createDir(File dir) {
		if (dir != null && !dir.exists()) {
			if (!dir.mkdir()) {
				Logger.logError("Cannot create dir: " + dir);
			}
		}
		return dir;
	}

	/**
	 * Returns the directory containing the robots.
	 *
	 * @return a File that is the directory containing the robots
	 */
	public static File getRobotsDir() {
		String robotPath = System.getProperty("ROBOTPATH");
		File file;

		if (robotPath != null) {
			file = new File(robotPath);
		} else {
			file = new File(cwd, "/robots");
		}
		return createDir(file);
	}

	/**
	 * Returns the robot database file.
	 *
	 * @return a File that is the directory containing the robot cache.
	 */
	public static File getRobotDatabaseFile() {
		return new File(getRobotsDir(), "/robot.database");
	}

	/**
	 * Returns the directory containing data files of robots.
	 *
	 * @return a File that is the directory containing the robot data.
	 */
	public static File getRobotsDataDir() {
		return createDir(new File(getRobotsDir(), "/.data/"));
	}

	/**
	 * Returns the directory containing the battle files.
	 *
	 * @return a File that is the directory containing the battle files
	 */
	public static File getBattlesDir() {
		return createDir(new File(cwd, "/battles"));
	}

	/**
	 * Returns the directory containing the configuration files.
	 * If the directory does not exist, it will be created automatically.
	 *
	 * @return a File that is the directory containing configuration files
	 */
	public static File getConfigDir() {
		return createDir(new File(cwd, "/config"));
	}

	/**
	 * Returns the directory containing the screen shot files.
	 * If the directory does not exist, it will be created automatically.
	 *
	 * @return a File that is the directory containing screen shot files
	 */
	public static File getScreenshotsDir() {
		return createDir(new File(cwd, "/screenshots"));
	}

	/**
	 * Returns the Robocode configuration file.
	 *
	 * @return a File that is the Robocode configuration file.
	 */
	public static File getRobocodeConfigFile() {
		return new File(getConfigDir(), "robocode.properties");
	}

	/**
	 * Returns the window configuration file.
	 *
	 * @return a File that is the window configuration file.
	 */
	public static File getWindowConfigFile() {
		return new File(getConfigDir(), "window.properties");
	}

	/**
	 * Returns the compiler configuration file.
	 *
	 * @return a File that is the compiler configuration file.
	 */
	public static File getCompilerConfigFile() {
		return new File(getConfigDir(), "compiler.properties");
	}

	/**
	 * Returns the source code editor configuration file.
	 *
	 * @return a File that is the editor configuration file.
	 */
	public static File getEditorConfigFile() {
		return new File(getConfigDir(), "editor.properties");
	}

	/**
	 * Returns the editor theme directory.
	 *
	 * @return a File that is the directory containing the editor theme files.
	 */
	public static File getEditorThemeConfigDir() {
		return createDir(new File(cwd, "/theme/editor"));
	}

	/**
	 * Cleans up a stream by flushing it and closing it if it is not null.
	 *
	 * @param stream the stream to clean up.
	 */
	public static void cleanupStream(Object stream) {
		if (stream == null) {
			return;
		}
		if (stream instanceof Flushable) {
			try {
				((Flushable) stream).flush();
			} catch (IOException e) {
				logError(e);
			}
		}
		if (stream instanceof Closeable) {
			try {
				((Closeable) stream).close();
			} catch (IOException e) {
				logError(e);
			}
		}
	}

	/**
	 * Checks if a filename is valid.
	 *
	 * @param file the filename to check.
	 * @return true if the filename is valid; false otherwise.
	 */
	public static boolean isFilenameValid(String file) {
		File f = new File(file);
		try {
			f.getCanonicalPath();
			return true;
		} catch (IOException e) {
			return false;
		}
	}
}
