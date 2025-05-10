/*
 * Copyright (c) 2001-2023 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.io;


import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static net.sf.robocode.io.Logger.logError;

import java.io.*;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;


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
		try {
			Files.copy(srcFile.toPath(), destFile.toPath(), REPLACE_EXISTING);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * Copies a folder recursively to another path.
	 *
	 * @param src  the input file to copy
	 * @param dest the output file to copy to
	 * @throws IOException if an I/O exception occurs
	 */
	public static void copyFolder(String src, String dest) throws IOException {
    // Validate inputs
    if (src == null || dest == null) {
        throw new NullPointerException("Source and destination paths cannot be null");
    }
    
    File srcFile = new File(src).getCanonicalFile();
    File destFile = new File(dest).getCanonicalFile();
    
    // Check if source and destination are the same
    if (srcFile.equals(destFile)) {
        throw new IOException("Source and destination cannot be the same");
    }
    
    // Convert to paths for modern Java operations
    Path srcPath = srcFile.toPath();
    Path destPath = destFile.toPath();
    
    // Ensure destination parent directory exists
    Files.createDirectories(destPath);
    
    // Safely copy directory contents
    try (Stream<Path> stream = Files.walk(srcPath)) {
        stream.forEach(source -> {
            try {
                // Calculate relative path
                Path relativePath = srcPath.relativize(source);
                Path target = destPath.resolve(relativePath);
                
                // Security check - ensure we're not writing outside destination directory
                if (!target.normalize().startsWith(destPath.normalize())) {
                    throw new SecurityException("Path traversal attempt detected");
                }
                
                if (Files.isDirectory(source)) {
                    if (!Files.exists(target)) {
                        Files.createDirectory(target);
                    }
                } else {
                    Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (SecurityException e) {
                throw new RuntimeException("Security violation: " + e.getMessage(), e);
            } catch (DirectoryNotEmptyException e) {
                // Directory already exists and is not empty - can ignore
            } catch (IOException e) {
                throw new RuntimeException("Failed to copy: " + e.getMessage(), e);
            }
        });
    }
}

	/**
	 * Deletes a directory.
	 *
	 * @param dir the file for the directory to delete
	 * @return true if success
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
								Logger.logError("Cannot delete: " + file);
							}
						} else {
							Logger.logWarning(file + " may be a symlink. Ignoring.");
						}
					} catch (IOException e) {
						Logger.logWarning("Cannot determine canonical file for " + file + ". Ignoring.");
					}
				} else {
					if (file.exists() && !file.delete()) {
						Logger.logError("Cannot delete: " + file);
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
				Logger.logError("Cannot create dir: " + dir);
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
