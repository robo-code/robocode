/*
 * Copyright (c) 2001-2026 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package robocode.util;


import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.zip.ZipFile;


/**
 * Used internally by Robocode for checking file reads made by robots.
 * <p>
 * When robot classes are loaded, {@code new ZipFile(..)} is replaced with {@code new RobotZipFile(..)}, which checks that
 * the robot only reads files in its own root package directory or data directory. The constructors with a mode are not
 * supported, as these can delete the file. Robots should not use this class directly.
 *
 * @author Flemming N. Larsen (original)
 */
public final class RobotZipFile extends ZipFile {

	public RobotZipFile(String name) throws IOException {
		super(RobotFileAccess.checkRead(name));
	}

	public RobotZipFile(File file) throws IOException {
		super(RobotFileAccess.checkRead(file));
	}

	public RobotZipFile(String name, Charset charset) throws IOException {
		super(RobotFileAccess.checkRead(name), charset);
	}

	public RobotZipFile(File file, Charset charset) throws IOException {
		super(RobotFileAccess.checkRead(file), charset);
	}
}
