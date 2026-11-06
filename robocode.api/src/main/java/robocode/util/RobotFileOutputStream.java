/*
 * Copyright (c) 2001-2026 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package robocode.util;


import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;


/**
 * Used internally by Robocode for keeping existing robots working with the robot security.
 * <p>
 * When robot classes are loaded, {@code new FileOutputStream(..)} is replaced with {@code new RobotFileOutputStream(..)},
 * which stops the robot, as robots must use {@link robocode.RobocodeFileOutputStream}. Existing robots can have code
 * using a {@code FileOutputStream} that never runs, and this must not prevent them from being loaded. Nothing is opened
 * by this class. Robots should not use this class directly.
 *
 * @author Flemming N. Larsen (original)
 */
public final class RobotFileOutputStream extends FileOutputStream {

	public RobotFileOutputStream(String name) throws FileNotFoundException {
		super(RobotCompat.denyWrite(name));
	}

	public RobotFileOutputStream(String name, boolean append) throws FileNotFoundException {
		super(RobotCompat.denyWrite(name), append);
	}

	public RobotFileOutputStream(File file) throws FileNotFoundException {
		super(RobotCompat.denyWrite(file));
	}

	public RobotFileOutputStream(File file, boolean append) throws FileNotFoundException {
		super(RobotCompat.denyWrite(file), append);
	}

	public RobotFileOutputStream(FileDescriptor fd) {
		super(RobotCompat.denyWrite(fd));
	}
}
