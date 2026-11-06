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
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;


/**
 * Used internally by Robocode for keeping existing robots working with the robot security.
 * <p>
 * When robot classes are loaded, {@code new FileWriter(..)} is replaced with {@code new RobotFileWriter(..)}, which stops
 * the robot, as robots must use {@link robocode.RobocodeFileWriter}. Existing robots can have code using a
 * {@code FileWriter} that never runs, and this must not prevent them from being loaded. Nothing is opened by this class.
 * Robots should not use this class directly.
 *
 * @author Flemming N. Larsen (original)
 */
public final class RobotFileWriter extends FileWriter {

	public RobotFileWriter(String fileName) throws IOException {
		super(RobotCompat.denyWrite(fileName));
	}

	public RobotFileWriter(String fileName, boolean append) throws IOException {
		super(RobotCompat.denyWrite(fileName), append);
	}

	public RobotFileWriter(File file) throws IOException {
		super(RobotCompat.denyWrite(file));
	}

	public RobotFileWriter(File file, boolean append) throws IOException {
		super(RobotCompat.denyWrite(file), append);
	}

	public RobotFileWriter(FileDescriptor fd) {
		super(RobotCompat.denyWrite(fd));
	}

	public RobotFileWriter(String fileName, Charset charset) throws IOException {
		super(RobotCompat.denyWrite(fileName));
	}

	public RobotFileWriter(String fileName, Charset charset, boolean append) throws IOException {
		super(RobotCompat.denyWrite(fileName), append);
	}

	public RobotFileWriter(File file, Charset charset) throws IOException {
		super(RobotCompat.denyWrite(file));
	}

	public RobotFileWriter(File file, Charset charset, boolean append) throws IOException {
		super(RobotCompat.denyWrite(file), append);
	}
}
