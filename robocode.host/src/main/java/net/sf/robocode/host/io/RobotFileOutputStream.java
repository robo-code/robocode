/*
 * Copyright (c) 2001-2023 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.host.io;


import java.io.FileOutputStream;
import java.io.IOException;


/**
 * @author Pavel Savara (original)
 */
public class RobotFileOutputStream extends FileOutputStream {

	private final RobotFileSystemManager fileSystemManager;

	public RobotFileOutputStream(String filename, boolean append, RobotFileSystemManager fileSystemManager) throws IOException {
		super(filename, append);
		this.fileSystemManager = fileSystemManager;
		fileSystemManager.addStream(this);
	}

	@Override
	public final void close() throws IOException {
		fileSystemManager.removeStream(this);
		super.close();
	}

	@Override
	public final void write(byte[] b) throws IOException {
		try {
			fileSystemManager.checkQuota(b.length);
			super.write(b);
		} catch (IOException e) {
			try {
				close();
			} catch (IOException ignored) {}
			throw e;
		}
	}

	@Override
	public final void write(byte[] b, int off, int len) throws IOException {
		// Sanity check
		if (b == null) {
			throw new NullPointerException();
		}

		// Comprehensive bounds checking to prevent integer overflow
		if (off < 0 || len < 0 || len > b.length || off > b.length - len) {
			throw new ArrayIndexOutOfBoundsException();
		}

		try {
			fileSystemManager.checkQuota(len);
			super.write(b, off, len);
		} catch (IOException e) {
			try {
				close();
			} catch (IOException ignore) {
			}
			throw e;
		}
	}

	@Override
	public final void write(int b) throws IOException {
		try {
			fileSystemManager.checkQuota(1);
			super.write(b);
		} catch (IOException e) {
			try {
				close();
			} catch (IOException ignored) {}
			throw e;
		}
	}
}
