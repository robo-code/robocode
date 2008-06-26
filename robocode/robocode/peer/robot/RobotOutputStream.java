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
 *     - Optimized regarding thread synchronization
 *******************************************************************************/
package robocode.peer.robot;


import robocode.io.BufferedPipedOutputStream;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public class RobotOutputStream extends java.io.PrintStream {

	private final static int MAX = 100;

	private Thread battleThread;

	private int count = 0;
	private boolean messaged = false;
	private StringBuilder text;
	private final Object syncRoot = new Object();

	public RobotOutputStream(Thread battleThread) {
		super(new BufferedPipedOutputStream(128, true));
		this.battleThread = battleThread;
		this.text = new StringBuilder(8192);
	}

	public String readAndReset() {
		synchronized (syncRoot) {
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
			if (count++ > MAX) {
				if (Thread.currentThread() == battleThread) {
					return true;
				}
				if (!messaged) {
					text.append(
							"SYSTEM: This robot is printing too much between actions.  Output stopped until next action.");
					text.append("\n");
					messaged = true;
				}
				return false;
			}
			messaged = false;
			return true;
		}
	}

	public void resetCounter() {
		synchronized (syncRoot) {
			count = 0;
		}
	}

	@Override
	public void print(char[] s) {
		if (s != null) {
			synchronized (syncRoot) {
				if (isOkToPrint()) {
					text.append(s);
					count += (s.length / 1000);
				}
			}
		}
	}

	@Override
	public void print(char c) {
		synchronized (syncRoot) {
			if (isOkToPrint()) {
				text.append(c);
			}
		}
	}

	@Override
	public void print(double d) {
		synchronized (syncRoot) {
			if (isOkToPrint()) {
				text.append(d);
			}
		}
	}

	@Override
	public void print(float f) {
		synchronized (syncRoot) {
			if (isOkToPrint()) {
				text.append(f);
			}
		}
	}

	@Override
	public void print(int i) {
		synchronized (syncRoot) {
			if (isOkToPrint()) {
				text.append(i);
			}
		}
	}

	@Override
	public void print(long l) {
		synchronized (syncRoot) {
			if (isOkToPrint()) {
				text.append(l);
			}
		}
	}

	@Override
	public void print(Object obj) {
		if (obj != null) {
			synchronized (syncRoot) {
				if (isOkToPrint()) {
					final String str = obj.toString();

					text.append(str);
					count += (str.length() / 1000);
				}
			}
		}
	}

	@Override
	public void print(String s) {
		if (s != null) {
			synchronized (syncRoot) {
				if (isOkToPrint()) {
					text.append(s);
					count += (s.length() / 1000);
				}
			}
		}
	}

	@Override
	public void print(boolean b) {
		synchronized (syncRoot) {
			if (isOkToPrint()) {
				text.append(b);
			}
		}
	}

	@Override
	public void println() {
		synchronized (syncRoot) {
			if (isOkToPrint()) {
				text.append("\n");
			}
		}
	}

	@Override
	public void println(char[] x) {
		if (x != null) {
			synchronized (syncRoot) {
				if (isOkToPrint()) {
					text.append(x);
					text.append("\n");
					count += (x.length / 1000);
				}
			}
		}
	}

	@Override
	public void println(char x) {
		synchronized (syncRoot) {
			if (isOkToPrint()) {
				text.append(x);
				text.append("\n");
			}
		}
	}

	@Override
	public void println(double x) {
		synchronized (syncRoot) {
			if (isOkToPrint()) {
				text.append(x);
				text.append("\n");
			}
		}
	}

	@Override
	public void println(float x) {
		synchronized (syncRoot) {
			if (isOkToPrint()) {
				text.append(x);
				text.append("\n");
			}
		}
	}

	@Override
	public void println(int x) {
		synchronized (syncRoot) {
			if (isOkToPrint()) {
				text.append(x);
				text.append("\n");
			}
		}
	}

	@Override
	public void println(long x) {
		synchronized (syncRoot) {
			if (isOkToPrint()) {
				text.append(x);
				text.append("\n");
			}
		}
	}

	@Override
	public void println(Object x) {
		if (x != null) {
			synchronized (syncRoot) {
				if (isOkToPrint()) {
					text.append(x);
					text.append("\n");
					count += (x.toString().length() / 1000);
				}
			}
		}
	}

	@Override
	public void println(String x) {
		if (x != null) {
			synchronized (syncRoot) {
				if (isOkToPrint()) {
					text.append(x);
					text.append("\n");
					count += (x.length() / 1000);
				}
			}
		}
	}

	@Override
	public void println(boolean x) {
		synchronized (syncRoot) {
			if (isOkToPrint()) {
				text.append("\n");
				text.append(x);
			}
		}
	}

	@Override
	public void write(byte[] buf) {
		synchronized (syncRoot) {
			if (isOkToPrint()) {
				text.append(Arrays.toString(buf));
				count += (buf.length / 1000);
			}
		}
	}

	@Override
	public void write(int b) {
		synchronized (syncRoot) {
			if (isOkToPrint()) {
				text.append(b);
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
					text.append(sw.toString());
				}
			}
		}
	}
}
