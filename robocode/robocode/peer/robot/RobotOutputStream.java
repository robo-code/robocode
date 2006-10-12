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
 *     - Optimized regarding thread synchronization
 *******************************************************************************/
package robocode.peer.robot;


import java.io.*;
import robocode.io.*;


/**
 * @author Mathew A. Nelson (original)
 */
public class RobotOutputStream extends java.io.PrintStream {

	private BufferedPipedOutputStream bufferedStream;
	private PrintStream out;
	
	private Thread battleThread;

	private int count;
	private int max = 100;
	
	private boolean messaged;

	public RobotOutputStream(Thread battleThread) {
		super(new BufferedPipedOutputStream(8192, true, true));
		bufferedStream = (BufferedPipedOutputStream) super.out;
		out = new PrintStream(bufferedStream);
		this.battleThread = battleThread;
	}

	public BufferedPipedInputStream getInputStream() {
		return bufferedStream.getInputStream();
	}

	public final synchronized boolean isOkToPrint() {
		count++;
		if (count > max) {
			if (Thread.currentThread() == battleThread) {
				return true;
			}
			if (messaged) {
				return false;
			} else {
				out.println(
						"SYSTEM: This robot is printing too much between actions.  Output stopped until next action.");
				messaged = true;
				return false;
			}
		} else {
			messaged = false;
			return true;
		}
	}

	public synchronized void resetCounter() {
		count = 0;
	}

	public void print(char[] s) {
		if (isOkToPrint()) {
			out.print(s);
			if (s != null) {
				synchronized (this) {
					count += (s.length / 1000);
				}
			}
		}
	}

	public void print(char c) {
		if (isOkToPrint()) {
			out.print(c);
		}
	}

	public void print(double d) {
		if (isOkToPrint()) {
			out.print(d);
		}
	}

	public void print(float f) {
		if (isOkToPrint()) {
			out.print(f);
		}
	}

	public void print(int i) {
		if (isOkToPrint()) {
			out.print(i);
		}
	}

	public void print(long l) {
		if (isOkToPrint()) {
			out.print(l);
		}
	}

	public void print(Object obj) {
		if (isOkToPrint()) {
			out.print(obj);
			if (obj != null) {
				synchronized (this) {
					count += obj.toString().length() / 1000;
				}
			}
		}
	}

	public void print(String s) {
		if (isOkToPrint()) {
			out.print(s);
			if (s != null) {
				synchronized (this) {
					count += s.length() / 1000;
				}
			}
		}
	}

	public void print(boolean b) {
		if (isOkToPrint()) {
			out.print(b);
		}
	}

	public void println() {
		if (isOkToPrint()) {
			out.println();
		}
	}

	public void println(char[] x) {
		if (isOkToPrint()) {
			out.println(x);
			if (x != null) {
				synchronized (this) {
					count += (x.length / 1000);
				}
			}
		}
	}

	public void println(char x) {
		if (isOkToPrint()) {
			out.println(x);
		}
	}

	public void println(double x) {
		if (isOkToPrint()) {
			out.println(x);
		}
	}

	public void println(float x) {
		if (isOkToPrint()) {
			out.println(x);
		}
	}

	public void println(int x) {
		if (isOkToPrint()) {
			out.println(x);
		}
	}

	public void println(long x) {
		if (isOkToPrint()) {
			out.println(x);
		}
	}

	public void println(Object x) {
		if (isOkToPrint()) {
			out.println(x);
			if (x != null) {
				synchronized (this) {
					count += x.toString().length() / 1000;
				}
			}
		}
	}

	public void println(String x) {
		if (isOkToPrint()) {
			out.println(x);
			if (x != null) {
				synchronized (this) {
					count += x.length() / 1000;
				}
			}
		}
	}

	public void println(boolean x) {
		if (isOkToPrint()) {
			out.println(x);
		}
	}

	public void write(byte[] buf) {
		if (isOkToPrint()) {
			out.print(buf);
			synchronized (this) {
				count += buf.length / 1000;
			}
		}
	}

	public void write(int b) {
		if (isOkToPrint()) {
			out.print(b);
		}
	}

	public void printStackTrace(Throwable t) {
		if (isOkToPrint()) {
			t.printStackTrace(out);
		}
	}
}