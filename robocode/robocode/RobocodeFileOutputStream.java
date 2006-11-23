/*******************************************************************************
 * Copyright (c) 2001-2006 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Code cleanup
 *******************************************************************************/
package robocode;


import java.io.*;
import robocode.peer.RobotPeer;
import robocode.security.*;
import robocode.peer.robot.RobotFileSystemManager;
import robocode.exception.*;
import robocode.util.*;
import robocode.manager.*;


/**
 * RobocodeFileOutputStream
 * 
 * @see java.io.FileOutputStream
 *
 * @author Mathew A. Nelson
 */
public class RobocodeFileOutputStream extends OutputStream {
	private static ThreadManager threadManager;
	private java.io.FileOutputStream out;
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
	public RobocodeFileOutputStream(String name, boolean append) throws java.io.IOException {
		if (threadManager == null) {
			Utils.log("RobocodeFileOutputStream.threadManager cannot be null!");
		}
		Thread c = Thread.currentThread();

		this.name = name;
		RobotPeer r = threadManager.getRobotPeer(c);

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
	public final void close() throws java.io.IOException {
		fileSystemManager.removeStream(this);
		out.close();
	}

	/**
	 * See java.io.FileOutputStream
	 * 
	 * @see java.io.FileOutputStream
	 */
	public final void flush() throws java.io.IOException {
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
	public final void write(byte[] b) throws java.io.IOException {
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
	public final void write(byte[] b, int off, int len) throws java.io.IOException {
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
	public final void write(int b) throws java.io.IOException {
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
