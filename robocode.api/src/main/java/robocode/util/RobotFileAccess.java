/*
 * Copyright (c) 2001-2026 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package robocode.util;


import net.sf.robocode.core.ContainerBase;
import net.sf.robocode.security.IThreadManagerBase;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;


/**
 * Used internally by Robocode for checking file access made by robots.
 * <p>
 * When robot classes are loaded, calls to methods of {@link File} that modify files or read information about files
 * are replaced with calls to the corresponding static methods of this class. These methods check that the robot is
 * only modifying files in its own data directory, and only reading files in its own root package directory or data
 * directory. Robots should not use this class directly.
 *
 * @author Flemming N. Larsen (original)
 */
public final class RobotFileAccess {

	private RobotFileAccess() {
	}

	// File modifications

	public static boolean delete(File file) {
		checkWrite(file);
		return file.delete();
	}

	public static boolean mkdir(File file) {
		checkWrite(file);
		return file.mkdir();
	}

	public static boolean mkdirs(File file) {
		checkWrite(file);
		return file.mkdirs();
	}

	// File information

	public static boolean exists(File file) {
		checkRead(file);
		return file.exists();
	}

	public static boolean isFile(File file) {
		checkRead(file);
		return file.isFile();
	}

	public static boolean isDirectory(File file) {
		checkRead(file);
		return file.isDirectory();
	}

	public static boolean isHidden(File file) {
		checkRead(file);
		return file.isHidden();
	}

	public static boolean canRead(File file) {
		checkRead(file);
		return file.canRead();
	}

	public static boolean canWrite(File file) {
		checkRead(file);
		return file.canWrite();
	}

	public static boolean canExecute(File file) {
		checkRead(file);
		return file.canExecute();
	}

	public static long length(File file) {
		checkRead(file);
		return file.length();
	}

	public static long lastModified(File file) {
		checkRead(file);
		return file.lastModified();
	}

	public static String[] list(File file) {
		checkRead(file);
		return file.list();
	}

	public static String[] list(File file, FilenameFilter filter) {
		checkRead(file);
		return file.list(filter);
	}

	public static File[] listFiles(File file) {
		checkRead(file);
		return file.listFiles();
	}

	public static File[] listFiles(File file, FilenameFilter filter) {
		checkRead(file);
		return file.listFiles(filter);
	}

	public static File[] listFiles(File file, FileFilter filter) {
		checkRead(file);
		return file.listFiles(filter);
	}

	static File checkRead(File file) {
		getThreadManager().checkRobotFileRead(file);
		return file;
	}

	static String checkRead(String fileName) {
		getThreadManager().checkRobotFileRead(new File(fileName));
		return fileName;
	}

	private static void checkWrite(File file) {
		getThreadManager().checkRobotFileWrite(file);
	}

	static IThreadManagerBase getThreadManager() {
		IThreadManagerBase threadManager = ContainerBase.getComponent(IThreadManagerBase.class);

		if (threadManager == null) {
			throw new SecurityException("Robot file access is not available");
		}
		return threadManager;
	}
}
