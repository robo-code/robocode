/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.host.security;


import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;


/**
 * @author Mathew A. Nelson (original)
 */
public class SecureInputStream extends java.io.InputStream {
	private RobocodePermission inputPermission = null;
	private InputStream in = null;

	/**
	 * SecureInputStream constructor comment.
	 *
	 * @param in original
	 */
	public SecureInputStream(InputStream in) {
		super();
		this.in = in;
		this.inputPermission = new RobocodePermission("System.in");
	}

	@Override
	public final int available() throws IOException {
		checkAccess();
		return in.available();
	}

	private void checkAccess() {
		AccessController.checkPermission(inputPermission);
	}

	@Override
	public final void close() throws IOException {
		checkAccess();
		in.close();
	}

	@Override
	public final synchronized void mark(int readlimit) {
		checkAccess();
		in.mark(readlimit);
	}

	@Override
	public final boolean markSupported() {
		checkAccess();
		return in.markSupported();
	}

	/**
	 * Reads the next byte of data from the input stream. The value byte is
	 * returned as an {@code int} in the range 0 to 255. If no byte is available
	 * because the end of the stream has been reached, the value -1 is returned.
	 * This method blocks until input data is available, the end of the stream
	 * is detected, or an exception is thrown.
	 * <p>
	 * <p> A subclass must provide an implementation of this method.
	 *
	 * @return the next byte of data, or -1 if the end of the stream is reached.
	 * @throws IOException if an I/O error occurs.
	 */
	@Override
	public final int read() throws java.io.IOException {
		checkAccess();
		return in.read();
	}

	@Override
	public final int read(byte[] b) throws IOException {
		checkAccess();
		return in.read(b);
	}

	@Override
	public final int read(byte[] b, int off, int len) throws IOException {
		checkAccess();
		return in.read(b, off, len);
	}

	@Override
	public final synchronized void reset() throws IOException {
		checkAccess();
		in.reset();
	}

	@Override
	public final long skip(long n) throws IOException {
		checkAccess();
		return in.skip(n);
	}
}
