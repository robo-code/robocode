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
package robocode.peer.robot;


import java.io.*;
import robocode.io.*;


public class RobotOutputStream extends java.io.PrintStream {
	private BufferedPipedOutputStream bufferedStream = null;
	private PrintStream out = null;
	
	private Thread battleThread = null;
	
	private int count = 0;
	private int max = 100;
	
	private boolean messaged = false;

	public RobotOutputStream(Thread battleThread) {
		super(new BufferedPipedOutputStream(8192, true, true));
		bufferedStream = (BufferedPipedOutputStream) super.out;
		out = new PrintStream(bufferedStream);
		this.battleThread = battleThread;
	}

	public BufferedPipedInputStream getInputStream() {
		return bufferedStream.getInputStream();
	}

	public final boolean isOkToPrint() {
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

	public synchronized void print(char[] s) {
		if (isOkToPrint()) {
			out.print(s);
			if (s != null) {
				count += (s.length / 1000);
			}
		}
	}

	public synchronized void print(char c) {
		if (isOkToPrint()) {
			out.print(c);
		}
	}

	public synchronized void print(double d) {
		if (isOkToPrint()) {
			out.print(d);
		}
	}

	public synchronized void print(float f) {
		if (isOkToPrint()) {
			out.print(f);
		}
	}

	public synchronized void print(int i) {
		if (isOkToPrint()) {
			out.print(i);
		}
	}

	public synchronized void print(long l) {
		if (isOkToPrint()) {
			out.print(l);
		}
	}

	public synchronized void print(Object obj) {
		if (isOkToPrint()) {
			out.print(obj);
			if (obj != null) {
				count += obj.toString().length() / 1000;
			}
		}
	}

	public synchronized void print(String s) {
		if (isOkToPrint()) {
			out.print(s);
			if (s != null) {
				count += s.length() / 1000;
			}
		}
	}

	public synchronized void print(boolean b) {
		if (isOkToPrint()) {
			out.print(b);
		}
	}

	public synchronized void println() {
		if (isOkToPrint()) {
			out.println();
		}
	}

	public synchronized void println(char[] x) {
		if (isOkToPrint()) {
			out.println(x);
			if (x != null) {
				count += (x.length / 1000);
			}
		}
	}

	public synchronized void println(char x) {
		if (isOkToPrint()) {
			out.println(x);
		}
	}

	public synchronized void println(double x) {
		if (isOkToPrint()) {
			out.println(x);
		}
	}

	public synchronized void println(float x) {
		if (isOkToPrint()) {
			out.println(x);
		}
	}

	public synchronized void println(int x) {
		if (isOkToPrint()) {
			out.println(x);
		}
	}

	public synchronized void println(long x) {
		if (isOkToPrint()) {
			out.println(x);
		}
	}

	public synchronized void println(Object x) {
		if (isOkToPrint()) {
			out.println(x);
			if (x != null) {
				count += x.toString().length() / 1000;
			}
		}
	}

	public synchronized void println(String x) {
		if (isOkToPrint()) {
			out.println(x);
			if (x != null) {
				count += x.length() / 1000;
			}
		}
	}

	public synchronized void println(boolean x) {
		if (isOkToPrint()) {
			out.println(x);
		}
	}

	public synchronized void write(byte[] buf) {
		if (isOkToPrint()) {
			out.print(buf);
			count += buf.length / 1000;
		}
	}

	public synchronized void write(int b) {
		if (isOkToPrint()) {
			out.print(b);
		}
	}

	public synchronized void printStackTrace(Throwable t) {
		if (isOkToPrint()) {
			t.printStackTrace(out);
		}
	}

}
