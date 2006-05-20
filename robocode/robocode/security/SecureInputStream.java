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
package robocode.security;


import java.io.*;


/**
 * Insert the type's description here.
 * Creation date: (10/18/2001 7:28:59 PM)
 * @author: Administrator
 */
public class SecureInputStream extends java.io.InputStream {
	private RobocodePermission inputPermission = null;
	private InputStream in = null;

	/**
	 * SecureInputStream constructor comment.
	 */
	public SecureInputStream(InputStream in, String accessString) {
		super();
		this.in = in;
		this.inputPermission = new RobocodePermission(accessString);
	}

	public final int available() throws IOException {
		checkAccess();
		return in.available();
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (10/18/2001 6:49:33 PM)
	 */
	private void checkAccess() {
		SecurityManager securityManager = System.getSecurityManager();

		if (securityManager != null) {
			securityManager.checkPermission(inputPermission);
		}
	}

	public final void close() throws IOException {
		checkAccess();
		in.close();
	}

	public final void mark(int readlimit) {
		checkAccess();
		in.mark(readlimit);
	}

	public final boolean markSupported() {
		checkAccess();
		return in.markSupported();
	}

	/**
	 * Reads the next byte of data from the input stream. The value byte is
	 * returned as an <code>int</code> in the range <code>0</code> to
	 * <code>255</code>. If no byte is available because the end of the stream
	 * has been reached, the value <code>-1</code> is returned. This method
	 * blocks until input data is available, the end of the stream is detected,
	 * or an exception is thrown.
	 *
	 * <p> A subclass must provide an implementation of this method.
	 *
	 * @return     the next byte of data, or <code>-1</code> if the end of the
	 *             stream is reached.
	 * @exception  IOException  if an I/O error occurs.
	 */
	public final int read() throws java.io.IOException {
		checkAccess();
		return in.read();
	}

	public final int read(byte[] b) throws IOException {
		checkAccess();
		return in.read(b);
	}

	public final int read(byte[] b, int off, int len) throws IOException {
		checkAccess();
		return in.read(b, off, len);
	}

	public final void reset() throws IOException {
		checkAccess();
		in.reset();
	}

	public final long skip(long n) throws IOException {
		checkAccess();
		return in.skip(n);
	}
}
