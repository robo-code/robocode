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
 *******************************************************************************/
package robocode;


import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;


/**
 * RobocodeFileWriter is used for writing data out to a file, which you got by
 * calling {@link AdvancedRobot#getDataFile(String)}.
 * <p>
 * You should read java.io.FileWriter for documentation of this class.
 * <p>
 * Please notice that the max. size of your data file is set to 200000
 * (~195 KB).
 *
 * @see AdvancedRobot#getDataFile(String)
 * @see java.io.FileWriter
 *
 * @author Mathew A. Nelson (original)
 */
public class RobocodeFileWriter extends java.io.OutputStreamWriter {

	/**
	 * RobocodeFileWriter constructor -- see java.io.FileWriter for docs!
	 *
	 * @see java.io.FileWriter
	 */
	public RobocodeFileWriter(File file) throws IOException {
		super(new RobocodeFileOutputStream(file));
	}

	/**
	 * RobocodeFileWriter constructor -- see java.io.FileWriter for docs!
	 *
	 * @see java.io.FileWriter
	 */
	public RobocodeFileWriter(FileDescriptor fd) {
		super(new RobocodeFileOutputStream(fd));
	}

	/**
	 * RobocodeFileWriter constructor -- see java.io.FileWriter for docs!
	 *
	 * @see java.io.FileWriter
	 */
	public RobocodeFileWriter(String fileName) throws IOException {
		super(new RobocodeFileOutputStream(fileName));
	}

	/**
	 * RobocodeFileWriter constructor -- see java.io.FileWriter for docs!
	 *
	 * @see java.io.FileWriter
	 */
	public RobocodeFileWriter(String fileName, boolean append) throws IOException {
		super(new RobocodeFileOutputStream(fileName, append));
	}
}
