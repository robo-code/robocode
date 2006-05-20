/*******************************************************************************
 * Copyright (c) 2001 Mathew Nelson
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew Nelson - initial API and implementation
 *******************************************************************************/
package robocode;


import java.io.*;


/**
 * See java.io.FileWriter
 * @see java.io.FileWriter
 */
public class RobocodeFileWriter extends java.io.OutputStreamWriter {

	/**
	 * RobocodeFileWriter constructor -- see java.io.FileWriter for docs!
	 * @see java.io.FileWriter
	 */
	public RobocodeFileWriter(File file) throws IOException {
		super(new RobocodeFileOutputStream(file));
	}

	/**
	 * RobocodeFileWriter constructor -- see java.io.FileWriter for docs!
	 * @see java.io.FileWriter
	 */
	public RobocodeFileWriter(FileDescriptor fd) {
		super(new RobocodeFileOutputStream(fd));
	}

	/**
	 * RobocodeFileWriter constructor -- see java.io.FileWriter for docs!
	 * @see java.io.FileWriter
	 */
	public RobocodeFileWriter(String fileName) throws IOException {
		super(new RobocodeFileOutputStream(fileName));
	}

	/**
	 * RobocodeFileWriter constructor -- see java.io.FileWriter for docs!
	 * @see java.io.FileWriter
	 */
	public RobocodeFileWriter(String fileName, boolean append) throws IOException {
		super(new RobocodeFileOutputStream(fileName, append));
	}
}
