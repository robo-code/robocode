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
 *     - Code cleanup
 *     - Updated to use methods from the Logger, which replaces logger methods
 *       that has been (re)moved from the robocode.util.Utils class
 *     - Fixed potential NullPointerExceptions
 *     - Updated Javadocs
 *******************************************************************************/
package robocode;


import robocode.exception.RobotException;
import robocode.io.Logger;
import robocode.manager.ThreadManager;
import robocode.peer.RobotPeer;
import robocode.peer.robot.RobotFileSystemManager;
import robocode.security.RobocodePermission;
import robocode.security.RobocodeSecurityManager;

import java.io.*;


/**
 * RobocodeFileOutputStream is similar to a {@link java.io.FileOutputStream}
 * and is used for streaming/writing data out to a file, which you got
 * previously by calling {@link AdvancedRobot#getDataFile(String) getDataFile()}.
 * <p/>
 * You should read {@link java.io.FileOutputStream} for documentation of this
 * class.
 * <p/>
 * Please notice that the max. size of your data file is set to 200000
 * (~195 KB).
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @see AdvancedRobot#getDataFile(String)
 * @see java.io.FileOutputStream
 */
public class RobocodeFileOutputStream extends OutputStream {
	private static ThreadManager threadManager;
	private FileOutputStream out;
	private String fileName;
	private RobotFileSystemManager fileSystemManager;

	/**
	 * Constructs a new RobocodeFileOutputStream.
	 * See {@link java.io.FileOutputStream#FileOutputStream(File)}
	 * for documentation about this constructor.
	 *
	 * @see java.io.FileOutputStream#FileOutputStream(File)
	 */
	public RobocodeFileOutputStream(File file) throws IOException {
		this(file.getPath());
	}

	/**
	 * Constructs a new RobocodeFileOutputStream.
	 * See {@link java.io.FileOutputStream#FileOutputStream(FileDescriptor)}
	 * for documentation about this constructor.
	 *
	 * @see java.io.FileOutputStream#FileOutputStream(FileDescriptor)
	 */
	public RobocodeFileOutputStream(FileDescriptor fdObj) {
		throw new RobotException("Creating a RobocodeFileOutputStream with a FileDescriptor is not supported.");
	}

	/**
	 * Constructs a new RobocodeFileOutputStream.
	 * See {@link java.io.FileOutputStream#FileOutputStream(String)}
	 * for documentation about this constructor.
	 *
	 * @see java.io.FileOutputStream#FileOutputStream(String)
	 */
	public RobocodeFileOutputStream(String fileName) throws java.io.IOException {
		this(fileName, false);
	}

	/**
	 * Constructs a new RobocodeFileOutputStream.
	 * See {@link java.io.FileOutputStream#FileOutputStream(String, boolean)}
	 * for documentation about this constructor.
	 *
	 * @see java.io.FileOutputStream#FileOutputStream(String, boolean)
	 */
	public RobocodeFileOutputStream(String fileName, boolean append) throws IOException {
		if (threadManager == null) {
			Logger.logError("RobocodeFileOutputStream.threadManager cannot be null!");
			return;
		}
		Thread c = Thread.currentThread();

		this.fileName = fileName;
		RobotPeer robotPeer = threadManager.getRobotPeer(c);

		if (robotPeer == null) {
			Logger.logError("RobotPeer is null");
			return;
		}

		if (!robotPeer.isAdvancedRobot()) {
			throw new RobotException("Only advanced robots may write to the filesystem");
		}

		this.fileSystemManager = robotPeer.getRobotFileSystemManager();

		// First, we see if the file exists:
		File f = new File(fileName);
		long len;

		if (f.exists()) {
			len = f.length();
		} else {
			fileSystemManager.checkQuota();
			len = 0;
		}

		RobocodeSecurityManager securityManager;

		if (System.getSecurityManager() instanceof RobocodeSecurityManager) {
			securityManager = (RobocodeSecurityManager) System.getSecurityManager();
			securityManager.getFileOutputStream(this, append);
			if (!append) {
				fileSystemManager.adjustQuota(-len);
			}
			fileSystemManager.addStream(this);
		} else {
			throw new RobotException("Non robocode security manager?");
		}
	}

	/**
	 * Closes this output stream. See {@link java.io.FileOutputStream#close()}
	 * for documentation about this method.
	 *
	 * @see java.io.FileOutputStream#close()
	 */
	@Override
	public final void close() throws IOException {
		fileSystemManager.removeStream(this);
		out.close();
	}

	/**
	 * Flushes this output stream. See {@link java.io.FileOutputStream#flush()}
	 * for documentation about this method.
	 *
	 * @see java.io.FileOutputStream#flush()
	 */
	@Override
	public final void flush() throws IOException {
		out.flush();
	}

	/**
	 * Returns the filename of this output stream.
	 *
	 * @return the filename of this output stream.
	 */
	public final String getName() {
		return fileName;
	}

	/**
	 * The system calls this method, you should not call it.
	 */
	public final void setFileOutputStream(FileOutputStream out) {
		this.out = out;
	}

	/**
	 * The system calls this method, you should not call it.
	 */
	public static void setThreadManager(ThreadManager threadManager) {
		System.getSecurityManager().checkPermission(new RobocodePermission("setThreadManager"));
		RobocodeFileOutputStream.threadManager = threadManager;
	}

	/**
	 * Writes a byte array to this output stream.
	 * See {@link java.io.FileOutputStream#write(byte[])} for documentation
	 * about this method.
	 *
	 * @see java.io.FileOutputStream#write(byte[])
	 */
	@Override
	public final void write(byte[] b) throws IOException {
		try {
			fileSystemManager.checkQuota(b.length);
			out.write(b);
		} catch (IOException e) {
			try {
				close();
			} catch (IOException f) {}
			throw e;
		}
	}

	/**
	 * Writes a byte array to this output stream.
	 * See {@link java.io.FileOutputStream#write(byte[], int, int)} for
	 * documentation about this method.
	 *
	 * @see java.io.FileOutputStream#write(byte[], int, int)
	 */
	@Override
	public final void write(byte[] b, int off, int len) throws IOException {
		if (len < 0) {
			throw new IndexOutOfBoundsException();
		}
		try {
			fileSystemManager.checkQuota(len);
			out.write(b, off, len);
		} catch (IOException e) {
			close();
			try {
				close();
			} catch (IOException f) {}
			throw e;
		}
	}

	/**
	 * Writes a single byte to this output stream.
	 * See {@link java.io.FileOutputStream#write(int)} for documentation about
	 * this method.
	 *
	 * @see java.io.FileOutputStream#write(int)
	 */
	@Override
	public final void write(int b) throws IOException {
		try {
			fileSystemManager.checkQuota(1);
			out.write(b);
		} catch (IOException e) {
			close();
			try {
				close();
			} catch (IOException f) {}
			throw e;
		}
	}
}
