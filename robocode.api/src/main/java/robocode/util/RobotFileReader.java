/*
 * Copyright (c) 2001-2026 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package robocode.util;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;


/**
 * Used internally by Robocode for checking file reads made by robots.
 * <p>
 * When robot classes are loaded, {@code new FileReader(..)} is replaced with {@code new RobotFileReader(..)},
 * which checks that the robot only reads files in its own root package directory or data directory.
 * Robots should not use this class directly.
 *
 * @author Flemming N. Larsen (original)
 */
public class RobotFileReader extends FileReader {

	public RobotFileReader(String fileName) throws FileNotFoundException {
		super(RobotFileAccess.checkRead(fileName));
	}

	public RobotFileReader(File file) throws FileNotFoundException {
		super(RobotFileAccess.checkRead(file));
	}
}
