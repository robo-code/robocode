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
import java.io.InputStream;


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

	/**
	 * Replaces {@link System#in} for robots when their classes are loaded, so they never get the real one. Existing robots
	 * may have code using it, which must not prevent them from being loaded, and a robot is only stopped if it really
	 * tries to read from it.
	 */
	public static final InputStream in = new InputStream() {
		@Override
		public int read() {
			getThreadManager().denyRobotSystemIn();
			throw new SecurityException("Robots cannot read from System.in");
		}
	};

	private RobotFileAccess() {
	}

	// File modifications

	public static boolean delete(File file) {
		return checkWrite(file).delete();
	}

	public static boolean mkdir(File file) {
		return checkWrite(file).mkdir();
	}

	public static boolean mkdirs(File file) {
		return checkWrite(file).mkdirs();
	}

	// File information

	public static boolean exists(File file) {
		return checkRead(file).exists();
	}

	public static boolean isFile(File file) {
		return checkRead(file).isFile();
	}

	public static boolean isDirectory(File file) {
		return checkRead(file).isDirectory();
	}

	public static boolean isHidden(File file) {
		return checkRead(file).isHidden();
	}

	public static boolean canRead(File file) {
		return checkRead(file).canRead();
	}

	public static boolean canWrite(File file) {
		return checkRead(file).canWrite();
	}

	public static boolean canExecute(File file) {
		return checkRead(file).canExecute();
	}

	public static long length(File file) {
		return checkRead(file).length();
	}

	public static long lastModified(File file) {
		return checkRead(file).lastModified();
	}

	public static String[] list(File file) {
		return checkRead(file).list();
	}

	public static String[] list(File file, FilenameFilter filter) {
		return checkRead(file).list(filter);
	}

	public static File[] listFiles(File file) {
		return checkRead(file).listFiles();
	}

	public static File[] listFiles(File file, FilenameFilter filter) {
		return checkRead(file).listFiles(filter);
	}

	public static File[] listFiles(File file, FileFilter filter) {
		return checkRead(file).listFiles(filter);
	}

	// Returns a new File with the path that was checked, as the path of a subclass of File can change after the check
	static File checkRead(File file) {
		File checked = new File(file.getPath());

		getThreadManager().checkRobotFileRead(checked);
		return checked;
	}

	static String checkRead(String fileName) {
		getThreadManager().checkRobotFileRead(new File(fileName));
		return fileName;
	}

	// Returns a new File with the path that was checked, as the path of a subclass of File can change after the check
	private static File checkWrite(File file) {
		File checked = new File(file.getPath());

		getThreadManager().checkRobotFileWrite(checked);
		return checked;
	}

	static IThreadManagerBase getThreadManager() {
		IThreadManagerBase threadManager = ContainerBase.getComponent(IThreadManagerBase.class);

		if (threadManager == null) {
			throw new SecurityException("Robot file access is not available");
		}
		return threadManager;
	}
}
