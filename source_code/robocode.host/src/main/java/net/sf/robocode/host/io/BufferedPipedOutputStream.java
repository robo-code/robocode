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
 *     - Bugfix: Changed the if-statement of "if (readIndex == writeIndex)" in
 *       the read() method to a while-statement in order to correct text-output
 *       bug, where old text or odd characters were printed out
 *     - The setReadIndexToNextLine() has been embedded into the write(int b)
 *       method
 *******************************************************************************/
package net.sf.robocode.host.io;


import java.io.IOException;
import java.io.OutputStream;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public class BufferedPipedOutputStream extends OutputStream {

	private final Object monitor = new Object();
	private final byte[] buf;
	private volatile int readIndex;
	private volatile int writeIndex;
	private volatile boolean waiting;
	private volatile boolean closed;
	private BufferedPipedInputStream in;
	private final boolean skipLines;

	public BufferedPipedOutputStream(int bufferSize, boolean skipLines) {
		this.buf = new byte[bufferSize];
		this.skipLines = skipLines;
	}

	/*
	 * @see OutputStream#write(int)
	 */
	@Override
	public void write(int b) throws IOException {
		synchronized (monitor) {
			if (closed) {
				throw new IOException("Stream is closed.");
			}

			buf[writeIndex++] = (byte) (b & 0xff);
			if (writeIndex == buf.length) {
				writeIndex = 0;
			}
			if (writeIndex == readIndex) {
				// skipping a line!
				if (skipLines) {
					boolean writeIndexReached = false;

					while (buf[readIndex] != '\n') {
						readIndex++;
						if (readIndex == buf.length) {
							readIndex = 0;
						}
						if (readIndex == writeIndex) {
							writeIndexReached = true;
						}
					}
					if (!writeIndexReached) {
						readIndex++;
						if (readIndex == buf.length) {
							readIndex = 0;
						}
					}
				} else {
					throw new IOException("Buffer is full.");
				}
			}
			if (waiting) {
				monitor.notifyAll();
			}
		}
	}

	protected int read() throws IOException {
		synchronized (monitor) {
			while (readIndex == writeIndex) {
				waiting = true;
				try {
					if (!closed) {
						monitor.wait(10000);
					}
					if (closed) {
						return -1;
					}
				} catch (InterruptedException e) {
					// Immediately reasserts the exception by interrupting the caller thread itself
					Thread.currentThread().interrupt();

					IOException ioException = new IOException("read interrupted");

					ioException.initCause(e);
					throw ioException;
				}
			}
			int result = buf[readIndex++];

			if (readIndex == buf.length) {
				readIndex = 0;
			}

			return result;
		}
	}

	public int read(byte b[], int off, int len) throws IOException {
		if (b == null) {
			throw new NullPointerException();
		} else if (off < 0 || len < 0 || off >= b.length || (off + len) > b.length) {
			throw new IndexOutOfBoundsException();
		} else if (len == 0) {
			return 0;
		}
		int first = read();

		if (first == -1) {
			return -1;
		}

		b[off] = (byte) (first & 0xff);
		int count = 1;

		synchronized (monitor) {
			for (int i = 1; readIndex != writeIndex && i < len; i++) {
				b[off + i] = buf[readIndex++];
				count++;
				if (readIndex == buf.length) {
					readIndex = 0;
				}
			}
		}
		return count;
	}

	protected int available() {
		synchronized (monitor) {
			if (writeIndex == readIndex) {
				return 0;
			} else if (writeIndex > readIndex) {
				return writeIndex - readIndex;
			} else {
				return buf.length - readIndex + writeIndex;
			}
		}
	}

	public BufferedPipedInputStream getInputStream() {
		synchronized (monitor) {
			if (in == null) {
				in = new BufferedPipedInputStream(this);
			}
			return in;
		}
	}

	@Override
	public void close() {
		synchronized (monitor) {
			closed = true;
			if (waiting) {
				monitor.notifyAll();
			}
		}
	}
}
