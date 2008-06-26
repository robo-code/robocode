/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Updated Javadocs
 *******************************************************************************/
package robocode;


import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;


/**
 * RobocodeFileWriter is similar to a {@link java.io.FileWriter} and is used for
 * writing data out to a file, which you got by calling {@link
 * AdvancedRobot#getDataFile(String) getDataFile()}.
 * <p/>
 * You should read {@link java.io.FileWriter} for documentation of this class.
 * <p/>
 * Please notice that the max. size of your data file is set to 200000
 * (~195 KB).
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @see AdvancedRobot#getDataFile(String)
 * @see java.io.FileWriter
 */
public class RobocodeFileWriter extends java.io.OutputStreamWriter {

	/**
	 * Constructs a new RobocodeFileWriter.
	 * See {@link java.io.FileWriter#FileWriter(File)} for documentation about
	 * this constructor.
	 *
	 * @param file the file to write to.
	 * @throws java.io.IOException if an I/O exception occurs.
	 * @see java.io.FileWriter#FileWriter(File)
	 */
	public RobocodeFileWriter(File file) throws IOException {
		super(new RobocodeFileOutputStream(file));
	}

	/**
	 * Constructs a new RobocodeFileWriter.
	 * See {@link java.io.FileWriter#FileWriter(FileDescriptor)} for
	 * documentation about this constructor.
	 *
	 * @param fd the file descriptor of the file to write to.
	 * @see java.io.FileWriter#FileWriter(FileDescriptor)
	 */
	public RobocodeFileWriter(FileDescriptor fd) {
		super(new RobocodeFileOutputStream(fd));
	}

	/**
	 * Constructs a new RobocodeFileWriter.
	 * See {@link java.io.FileWriter#FileWriter(String)} for documentation about
	 * this constructor.
	 *
	 * @param fileName the filename of the file to write to.
	 * @throws java.io.IOException if an I/O exception occurs.
	 * @see java.io.FileWriter#FileWriter(String)
	 */
	public RobocodeFileWriter(String fileName) throws IOException {
		super(new RobocodeFileOutputStream(fileName));
	}

	/**
	 * Constructs a new RobocodeFileWriter.
	 * See {@link java.io.FileWriter#FileWriter(String, boolean)} for
	 * documentation about this constructor.
	 *
	 * @param fileName the filename of the file to write to.
	 * @param append   set this to true if the output must be appended to the file.
	 * @throws java.io.IOException if an I/O exception occurs.
	 * @see java.io.FileWriter#FileWriter(String, boolean)
	 */
	public RobocodeFileWriter(String fileName, boolean append) throws IOException {
		super(new RobocodeFileOutputStream(fileName, append));
	}
}
