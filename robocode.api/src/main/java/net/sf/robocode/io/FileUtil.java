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
 *     - Moved the former robocode.util.Constants and all file operations from
 *       robocode.util.Utils into this new class
 *     - Added/updated JavaDoc for all methods
 *     - Added the quoteFileName(), createDir(), getRobotsDir(), getConfigDir(),
 *       getRobocodeConfigFile(), getWindowConfigFile(), getCompilerConfigFile()
 *******************************************************************************/
package net.sf.robocode.io;


import static net.sf.robocode.io.Logger.logError;

import java.io.*;


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
	 * Returns the file type of a file, i.e. it's extension.
	 *
	 * @param file the file
	 * @return the file type of the file, e.g. ".class", ".jar" or "" if the
	 *         file name does not contain an extension.
	 */
	public static String getFileType(File file) {
		return getFileType(file.getName());
	}

	/**
	 * Returns the file type of a file name, i.e. it's extension.
	 *
	 * @param fileName the file name
	 * @return the file type of the file name, e.g. ".class", ".jar" or "" if
	 *         the file name does not contain an extension.
	 */
	public static String getFileType(String fileName) {
		int lastdot = fileName.lastIndexOf('.');

		return (lastdot < 0) ? "" : fileName.substring(lastdot);
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

		byte buf[] = new byte[4096];

		FileInputStream in = null;
		FileOutputStream out = null;

		try {
			in = new FileInputStream(srcFile);
			out = new FileOutputStream(destFile);

			while (in.available() > 0) {
				out.write(buf, 0, in.read(buf, 0, buf.length));
			}
		} finally {
			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.close();
			}
		}
	}

	/**
	 * Deletes a directory.
	 *
	 * @param dir the file for the directory to delete
	 * @return true if susscess
	 */
	public static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			for (File file : dir.listFiles()) {
				if (file.isDirectory()) {
					try {
						// Test for symlink and ignore.
						// Robocode won't create one, but just in case a user does...
						if (file.getCanonicalFile().getParentFile().equals(dir.getCanonicalFile())) {
							deleteDir(file);
							if (file.exists() && !file.delete()) {
								Logger.logError("Can't delete: " + file);
							}
						} else {
							Logger.logMessage("Warning: " + file + " may be a symlink.  Ignoring.");
						}
					} catch (IOException e) {
						Logger.logMessage("Warning: Cannot determine canonical file for " + file + " - ignoring.");
					}
				} else {
					if (file.exists() && !file.delete()) {
						Logger.logError("Can't delete: " + file);
					}
				}
			}
			return dir.delete();
		}
		return false;
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
				Logger.logError("Can't create" + dir);
			}
		}
		return dir;
	}

	/**
	 * Returns the class name of the specified filename.
	 *
	 * @param fileName the filename to extract the class name from
	 * @return the class name of the specified filename
	 */
	public static String getClassName(String fileName) {
		int lastdot = fileName.lastIndexOf('.');

		if (lastdot < 0) {
			return fileName;
		}
		if (fileName.length() - 1 == lastdot) {
			return fileName.substring(0, fileName.length() - 1);
		}
		return fileName.substring(0, lastdot);
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
	 * Returns the directory containing caches files of robots.
	 *
	 * @return a File that is the directory containing the robot cache.
	 */
	public static File getRobotCacheDir() {
		return createDir(new File(getRobotsDir(), "/.robotcache/"));
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
	 * Returns the directory containing maps.
	 * If the directory does not exist, it will be created automatically.
	 *
	 * @return a File that is the directory containing maps.
	 */
	public static File getMapsDir() {
		return createDir(new File(cwd, "/maps"));
	}

	/**
	 * Returns the directory containing extension jars.
	 * If the directory does not extist, it will be created automatically.
	 * 
	 * @return a File that is the directory containing extension jars
	 */
	public static File getExtensionsDir() {
		return createDir(new File(cwd, "/extensions"));
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
	 * @return a File that the window configuration file.
	 */
	public static File getWindowConfigFile() {
		return new File(getConfigDir(), "window.properties");
	}

	/**
	 * Returns the compiler configuration file.
	 *
	 * @return a File that the compiler configuration file.
	 */
	public static File getCompilerConfigFile() {
		return new File(getConfigDir(), "compiler.properties");
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
}
