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


import robocode.io.BufferedPipedInputStream;
import robocode.io.BufferedPipedOutputStream;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public class RobotOutputStream extends java.io.PrintStream {

	private final static int MAX = 100;

	private BufferedPipedOutputStream bufferedStream;
	private PrintStream out;

	private Thread battleThread;

	private AtomicInteger count = new AtomicInteger();

	private AtomicBoolean messaged = new AtomicBoolean(false);

	public RobotOutputStream(Thread battleThread) {
		super(new BufferedPipedOutputStream(8192, true));
		bufferedStream = (BufferedPipedOutputStream) super.out;
		out = new PrintStream(bufferedStream);
		this.battleThread = battleThread;
	}

	public BufferedPipedInputStream getInputStream() {
		return bufferedStream.getInputStream();
	}

	public final boolean isOkToPrint() {
		if (count.incrementAndGet() > MAX) {
			if (Thread.currentThread() == battleThread) {
				return true;
			}
			if (!messaged.get()) {
				out.println(
						"SYSTEM: This robot is printing too much between actions.  Output stopped until next action.");
				messaged.set(true);
			}
			return false;
		}
		messaged.set(false);
		return true;
	}

	public void resetCounter() {
		count.set(0);
	}

	@Override
	public void print(char[] s) {
		if (isOkToPrint()) {
			out.print(s);
			if (s != null) {
				count.addAndGet(s.length / 1000);
			}
		}
	}

	@Override
	public void print(char c) {
		if (isOkToPrint()) {
			out.print(c);
		}
	}

	@Override
	public void print(double d) {
		if (isOkToPrint()) {
			out.print(d);
		}
	}

	@Override
	public void print(float f) {
		if (isOkToPrint()) {
			out.print(f);
		}
	}

	@Override
	public void print(int i) {
		if (isOkToPrint()) {
			out.print(i);
		}
	}

	@Override
	public void print(long l) {
		if (isOkToPrint()) {
			out.print(l);
		}
	}

	@Override
	public void print(Object obj) {
		if (isOkToPrint()) {
			out.print(obj);
			if (obj != null) {
				count.addAndGet(obj.toString().length() / 1000);
			}
		}
	}

	@Override
	public void print(String s) {
		if (isOkToPrint()) {
			out.print(s);
			if (s != null) {
				count.addAndGet(s.length() / 1000);
			}
		}
	}

	@Override
	public void print(boolean b) {
		if (isOkToPrint()) {
			out.print(b);
		}
	}

	@Override
	public void println() {
		if (isOkToPrint()) {
			out.println();
		}
	}

	@Override
	public void println(char[] x) {
		if (isOkToPrint()) {
			out.println(x);
			if (x != null) {
				count.addAndGet(x.length / 1000);
			}
		}
	}

	@Override
	public void println(char x) {
		if (isOkToPrint()) {
			out.println(x);
		}
	}

	@Override
	public void println(double x) {
		if (isOkToPrint()) {
			out.println(x);
		}
	}

	@Override
	public void println(float x) {
		if (isOkToPrint()) {
			out.println(x);
		}
	}

	@Override
	public void println(int x) {
		if (isOkToPrint()) {
			out.println(x);
		}
	}

	@Override
	public void println(long x) {
		if (isOkToPrint()) {
			out.println(x);
		}
	}

	@Override
	public void println(Object x) {
		if (isOkToPrint()) {
			out.println(x);
			if (x != null) {
				count.addAndGet(x.toString().length() / 1000);
			}
		}
	}

	@Override
	public void println(String x) {
		if (isOkToPrint()) {
			out.println(x);
			if (x != null) {
				count.addAndGet(x.length() / 1000);
			}
		}
	}

	@Override
	public void println(boolean x) {
		if (isOkToPrint()) {
			out.println(x);
		}
	}

	@Override
	public void write(byte[] buf) {
		if (isOkToPrint()) {
			out.print(Arrays.toString(buf));
			count.addAndGet(buf.length / 1000);
		}
	}

	@Override
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
