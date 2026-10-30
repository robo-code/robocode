/*
 * Copyright (c) 2001-2025 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.security;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Member;


/**
 * @author Pavel Savara (original)
 */
public interface IThreadManagerBase {
	boolean isSafeThread();
	FileOutputStream createRobotFileStream(String fileName, boolean append) throws IOException;

	/**
	 * Checks that the current robot is allowed to modify a file, i.e. that the file is in the robot's data directory.
	 *
	 * @param file the file to modify.
	 * @throws SecurityException if the robot is not allowed to modify the file.
	 */
	void checkRobotFileWrite(File file);

	/**
	 * Checks that the current robot is allowed to read a file, i.e. that the file is in the robot's own root package
	 * directory or data directory.
	 *
	 * @param file the file to read.
	 * @throws SecurityException if the robot is not allowed to read the file.
	 */
	void checkRobotFileRead(File file);

	/**
	 * Returns the class loader of the current robot.
	 *
	 * @return the class loader of the robot.
	 * @throws SecurityException if the current thread is not a robot thread.
	 */
	ClassLoader getRobotClassLoader();

	/**
	 * Checks that the current robot is allowed to use a member via reflection, i.e. that the member belongs to a class
	 * of the robot, or that robots are allowed to use the member directly in their code.
	 *
	 * @param member        the field, method, or constructor to use.
	 * @param setAccessible true if the robot wants to suppress the Java language access checks for the member, which
	 *                      is only allowed for members of the robot's own classes.
	 * @return true if the member belongs to a class of the robot; false otherwise.
	 * @throws SecurityException if the robot is not allowed to use the member.
	 */
	boolean checkRobotReflection(Member member, boolean setAccessible);
}
