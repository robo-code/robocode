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
 *     Flemming N. Larsen
 *     - Updated to use methods from the Logger, which replaces logger methods
 *       that has been (re)moved from the robocode.util.Utils class
 *     - Fixed potential NullPointerExceptions
 *     - Code cleanup
 *******************************************************************************/
package robocode;


import java.io.*;

import robocode.exception.RobotException;
import robocode.io.Logger;
import robocode.manager.ThreadManager;
import robocode.peer.RobotPeer;
import robocode.peer.robot.RobotFileSystemManager;
import robocode.security.RobocodePermission;
import robocode.security.RobocodeSecurityManager;


/**
 * RobocodeFileOutputStream is used for streaming/writing data out to a file,
 * which you got by calling {@link AdvancedRobot#getDataFile(String)}.
 * <p>
 * You should read java.io.FileOutputStream for documentation of this class.
 * <p>
 * Please notice that the max. size of your data file is set to 200000
 * (~195 KB).
 *
 * @see AdvancedRobot#getDataFile(String)
 * @see java.io.FileOutputStream
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public class RobocodeFileOutputStream extends OutputStream {
	private static ThreadManager threadManager;
	private FileOutputStream out;
	private String name = null;
	private RobotFileSystemManager fileSystemManager;

	/**
	 * RobocodeFileOutputStream constructor -- see FileOutputStream for docs!
	 * @see java.io.FileOutputStream
	 */
	public RobocodeFileOutputStream(File file) throws IOException {
		this(file.getPath());
	}

	/**
	 * RobocodeFileOutputStream constructor -- see FileOutputStream for docs!
	 * @see java.io.FileOutputStream
	 */
	public RobocodeFileOutputStream(FileDescriptor fdObj) {
		throw new RobotException("Creating a RobocodeFileOutputStream with a FileDescriptor is not supported.");
	}

	/**
	 * RobocodeFileOutputStream constructor -- see FileOutputStream for docs!
	 * @see java.io.FileOutputStream
	 */
	public RobocodeFileOutputStream(String name) throws java.io.IOException {
		this(name, false);
	}

	/**
	 * RobocodeFileOutputStream constructor -- see FileOutputStream for docs!
	 * @see java.io.FileOutputStream
	 */
	public RobocodeFileOutputStream(String name, boolean append) throws IOException {
		if (threadManager == null) {
			Logger.log("RobocodeFileOutputStream.threadManager cannot be null!");
			return;
		}
		Thread c = Thread.currentThread();

		this.name = name;
		RobotPeer r = threadManager.getRobotPeer(c);
		if (r == null) {
			Logger.log("RobotPeer is null");
			return;
		}

		if (!(r.getRobot() instanceof AdvancedRobot)) {
			throw new RobotException("Only robots that extend AdvancedRobot may write to the filesystem.");
		}

		this.fileSystemManager = r.getRobotFileSystemManager();

		// First, we see if the file exists:
		File f = new File(name);
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
	 * See java.io.FileOutputStream
	 *
	 * @see java.io.FileOutputStream
	 */
	@Override
	public final void close() throws IOException {
		fileSystemManager.removeStream(this);
		out.close();
	}

	/**
	 * See java.io.FileOutputStream
	 *
	 * @see java.io.FileOutputStream
	 */
	@Override
	public final void flush() throws IOException {
		out.flush();
	}

	/**
	 * Returns the filename
	 */
	public final String getName() {
		return name;
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
	public final static void setThreadManager(ThreadManager threadManager) {
		System.getSecurityManager().checkPermission(new RobocodePermission("setThreadManager"));
		RobocodeFileOutputStream.threadManager = threadManager;
	}

	/**
	 * See java.io.FileOutputStream
	 *
	 * @see java.io.FileOutputStream
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
	 * See java.io.FileOutputStream
	 *
	 * @see java.io.FileOutputStream
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
	 * See java.io.FileOutputStream
	 *
	 * @see java.io.FileOutputStream
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
