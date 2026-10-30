/*
 * Copyright (c) 2001-2025 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package robocode.util;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;


/**
 * Used internally by Robocode for checking file reads made by robots.
 * <p>
 * When robot classes are loaded, {@code new FileInputStream(..)} is replaced with {@code new RobotFileInputStream(..)},
 * which checks that the robot only reads files in its own root package directory or data directory.
 * Robots should not use this class directly.
 *
 * @author Flemming N. Larsen (original)
 */
public class RobotFileInputStream extends FileInputStream {

	public RobotFileInputStream(String name) throws FileNotFoundException {
		super(RobotFileAccess.checkRead(name));
	}

	public RobotFileInputStream(File file) throws FileNotFoundException {
		super(RobotFileAccess.checkRead(file));
	}
}
