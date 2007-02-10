/*******************************************************************************
 * Copyright (c) 2001, 2007 Mathew A. Nelson and Robocode contributors
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
 *     - Added the quoteFileName()
 *******************************************************************************/
package robocode.io;


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
			FileUtil.setCwd(new File(""));
		} catch (IOException e) {}
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
	 * @throws IOException
	 */
	public static void setCwd(File cwd) throws IOException {
		FileUtil.cwd = cwd.getCanonicalFile();
	}

	/**
	 * Returns the file type of a file, i.e. it's extension.
	 *
	 * @param file the file
	 * @return the file type of the file, e.g. ".class", ".jar" or "" if the
	 *    file name does not contain an extension.
	 */
	public static String getFileType(File file) {
		return getFileType(file.getName());
	}

	/**
	 * Returns the file type of a file name, i.e. it's extension.
	 *
	 * @param file the file name
	 * @return the file type of the file name, e.g. ".class", ".jar" or "" if
	 *    the file name does not contain an extension.
	 */
	public static String getFileType(String fileName) {
		int lastdot = fileName.lastIndexOf('.');

		return (lastdot < 0) ? "" : fileName.substring(lastdot);
	}

	/**
	 * Quotes a file name if it contains whitespaces and has not already been
	 * quoted.
	 *
	 * @param filename the file to quote
	 * @return a quoted version of the specified filename
	 */
	public static String quoteFileName(String filename) {
		if (filename.startsWith("\"") || filename.endsWith("\"")) {
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
	 * Copies a file.
	 *
	 * @param srcFile the source file
	 * @param destFile the destination file
	 * @throws IOException
	 */
	public static void copy(File srcFile, File destFile) throws IOException {
		if (srcFile.equals(destFile)) {
			throw new IOException("You cannot copy a file onto itself");
		}
		byte buf[] = new byte[4096];
		FileInputStream in = new FileInputStream(srcFile);
		FileOutputStream out = new FileOutputStream(destFile);

		while (in.available() > 0) {
			int count = in.read(buf, 0, 4096);

			out.write(buf, 0, count);
		}
	}

	/**
	 * Deletes a directory.
	 *
	 * @param dir the file for the direcory to delete
	 * @return <code>true</code> if the directory was deleted;
	 *    <code>false</code> otherwise if e.g. the file is not a directory
	 */
	public static boolean deleteDir(File dir) {
		if (!dir.isDirectory()) {
			return false;
		}

		for (File file : dir.listFiles()) {
			if (file.isDirectory()) {
				try {
					// Test for symlink and ignore.
					// Robocode won't create one, but just in case a user does...
					if (file.getCanonicalFile().getParentFile().equals(dir.getCanonicalFile())) {
						deleteDir(file);
						file.delete();
					} else {
						System.out.println("Warning: " + file + " may be a symlink.  Ignoring.");
					}
				} catch (IOException e) {
					System.out.println("Warning: Cannot determine canonical file for " + file + " - ignoring.");
				}
			} else {
				file.delete();
			}
		}
		dir.delete();

		return true;
	}

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
}
