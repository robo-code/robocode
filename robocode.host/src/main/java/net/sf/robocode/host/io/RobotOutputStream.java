/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.host.io;


import java.io.PrintWriter;
import java.io.StringWriter;

import net.sf.robocode.io.RobocodeProperties;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public class RobotOutputStream extends java.io.PrintStream {

	private static final int MAX_CHARS = 50000;

	private int count = 0;
	private boolean messaged = false;
	private final StringBuilder text;
	private final Object syncRoot = new Object();
	
	public RobotOutputStream() {
		super(new BufferedPipedOutputStream(128, true));
		this.text = new StringBuilder(8192);
	}

	public String readAndReset() {
		synchronized (syncRoot) {

			// Out's counter must be reset before processing event.
			// Otherwise, it will not be reset when printing in the onScannedEvent()
			// before a scan() call, which will potentially cause a new onScannedEvent()
			// and therefore not be able to reset the counter.
			count = 0;

			if (text.length() > 0) {
				final String result = text.toString();

				text.setLength(0);
				return result;
			}
			return "";
		}
	}

	private boolean isOkToPrint() {
		synchronized (syncRoot) {
			if (RobocodeProperties.isDebuggingOn()) { // It is always allowed to print when debugging is enabled.
				return true;
			}
			if (count > MAX_CHARS) {
				if (!messaged) {
					text.append(
							"\nSYSTEM: This robot is printing too much between actions.  Output stopped until next action.\n");
					messaged = true;
				}
				return false;
			}
			messaged = false;
			return true;
		}
	}

	@Override
	public void print(char[] s) {
		synchronized (syncRoot) {
			if (isOkToPrint()) {
				int origLen = text.length();

				text.append(s);
				count += text.length() - origLen;
			}
		}
	}

	@Override
	public void print(char c) {
		synchronized (syncRoot) {
			if (isOkToPrint()) {
				text.append(c);
				count++;
			}
		}
	}

	@Override
	public void print(double d) {
		synchronized (syncRoot) {
			if (isOkToPrint()) {
				int origLen = text.length();

				text.append(d);
				count += text.length() - origLen;
			}
		}
	}

	@Override
	public void print(float f) {
		synchronized (syncRoot) {
			if (isOkToPrint()) {
				int origLen = text.length();

				text.append(f);
				count += text.length() - origLen;
			}
		}
	}

	@Override
	public void print(int i) {
		synchronized (syncRoot) {
			if (isOkToPrint()) {
				int origLen = text.length();

				text.append(i);
				count += text.length() - origLen;
			}
		}
	}

	@Override
	public void print(long l) {
		synchronized (syncRoot) {
			if (isOkToPrint()) {
				int origLen = text.length();

				text.append(l);
				count += text.length() - origLen;
			}
		}
	}

	@Override
	public void print(Object obj) {
		synchronized (syncRoot) {
			if (isOkToPrint()) {
				int origLen = text.length();

				if (obj != null) {
					String s = obj.toString();

					text.append(s);
				} else {
					text.append((Object) null);
				}
				count += text.length() - origLen;
			}
		}
	}

	@Override
	public void print(String s) {
		synchronized (syncRoot) {
			if (isOkToPrint()) {
				int origLen = text.length();

				text.append(s);
				count += text.length() - origLen;
			}
		}
	}

	@Override
	public void print(boolean b) {
		synchronized (syncRoot) {
			if (isOkToPrint()) {
				int origLen = text.length();

				text.append(b);
				count += text.length() - origLen;
			}
		}
	}

	@Override
	public void println() {
		print('\n');
	}

	@Override
	public void println(char[] x) {
		print(x);
		print('\n');
	}

	@Override
	public void println(char x) {
		print(x);
		print('\n');
	}

	@Override
	public void println(double x) {
		print(x);
		print('\n');
	}

	@Override
	public void println(float x) {
		print(x);
		print('\n');
	}

	@Override
	public void println(int x) {
		print(x);
		print('\n');
	}

	@Override
	public void println(long x) {
		print(x);
		print('\n');
	}

	@Override
	public void println(Object x) {
		print(x);
		print('\n');
	}

	@Override
	public void println(String x) {
		print(x);
		print('\n');
	}

	@Override
	public void println(boolean x) {
		print(x);
		print('\n');
	}

	@Override
	public void write(byte[] buf) {
		write(buf, 0, buf.length);
	}

	@Override
	public void write(byte[] buf, int off, int len) {
		synchronized (syncRoot) {
			if (isOkToPrint()) {
				if (buf != null) {
					int origLen = text.length();

					for (int i = 0; i < len; i++) {
						text.append((char) buf[off + i]);
					}
					count += text.length() - origLen;
				}
			}
		}
	}

	@Override
	public void write(int b) {
		synchronized (syncRoot) {
			if (isOkToPrint()) {
				int origLen = text.length();

				text.append((char) b);
				count += text.length() - origLen;
			}
		}
	}

	public void printStackTrace(Throwable t) {
		if (t != null) {
			synchronized (syncRoot) {
				if (isOkToPrint()) {
					StringWriter sw = new StringWriter();
					final PrintWriter writer = new PrintWriter(sw);

					t.printStackTrace(writer);
					writer.flush();

					int origLen = text.length();

					text.append(sw.toString());
					count += text.length() - origLen;
				}
			}
		}
	}
}
