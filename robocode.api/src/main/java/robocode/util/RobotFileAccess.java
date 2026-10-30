/*
 * Copyright (c) 2001-2025 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package robocode.util;


import net.sf.robocode.core.ContainerBase;
import net.sf.robocode.security.IThreadManagerBase;

import java.io.File;


/**
 * Used internally by Robocode for checking file modifications made by robots.
 * <p>
 * When robot classes are loaded, calls to {@link File#delete()}, {@link File#mkdir()}, and {@link File#mkdirs()} are
 * replaced with calls to the corresponding methods of this class, which only allow modifying files and directories
 * in the robot's own data directory. Robots should not use this class directly.
 *
 * @author Flemming N. Larsen (original)
 */
public final class RobotFileAccess {

	private RobotFileAccess() {
	}

	/**
	 * Deletes a file in the robot's data directory.
	 *
	 * @param file the file to delete.
	 * @return the result of {@link File#delete()}.
	 * @throws SecurityException if the file is not in the robot's data directory.
	 */
	public static boolean delete(File file) {
		checkWrite(file);
		return file.delete();
	}

	/**
	 * Creates a directory in the robot's data directory.
	 *
	 * @param file the directory to create.
	 * @return the result of {@link File#mkdir()}.
	 * @throws SecurityException if the directory is not in the robot's data directory.
	 */
	public static boolean mkdir(File file) {
		checkWrite(file);
		return file.mkdir();
	}

	/**
	 * Creates a directory, including parent directories, in the robot's data directory.
	 *
	 * @param file the directory to create.
	 * @return the result of {@link File#mkdirs()}.
	 * @throws SecurityException if the directory is not in the robot's data directory.
	 */
	public static boolean mkdirs(File file) {
		checkWrite(file);
		return file.mkdirs();
	}

	private static void checkWrite(File file) {
		IThreadManagerBase threadManager = ContainerBase.getComponent(IThreadManagerBase.class);

		if (threadManager == null) {
			throw new SecurityException("Robot file access is not available");
		}
		threadManager.checkRobotFileWrite(file);
	}
}
