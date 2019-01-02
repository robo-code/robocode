/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.host.security;


import net.sf.robocode.core.Container;
import net.sf.robocode.host.IHostManager;

import java.io.OutputStream;
import java.io.PrintStream;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public class SecurePrintStream extends PrintStream {

	public SecurePrintStream(OutputStream out, boolean autoFlush) {
		super(out, autoFlush);
	}

	@Override
	public final boolean checkError() {
		PrintStream out = checkAccess();

		return (out == this) ? super.checkError() : out.checkError();
	}

	@Override
	public final void close() {
		PrintStream out = checkAccess();

		if (out == this) {
			super.close();
		} else {
			out.close();
		}
	}

	@Override
	public final void flush() {
		PrintStream out = checkAccess();

		if (out == this) {
			super.flush();
		} else {
			out.flush();
		}
	}

	@Override
	public final void print(char[] s) {
		PrintStream out = checkAccess();

		if (out == this) {
			super.print(s);
		} else {
			out.print(s);
		}
	}

	@Override
	public final void print(char c) {
		PrintStream out = checkAccess();

		if (out == this) {
			super.print(c);
		} else {
			out.print(c);
		}
	}

	@Override
	public final void print(double d) {
		PrintStream out = checkAccess();

		if (out == this) {
			super.print(d);
		} else {
			out.print(d);
		}
	}

	@Override
	public final void print(float f) {
		PrintStream out = checkAccess();

		if (out == this) {
			super.print(f);
		} else {
			out.print(f);
		}
	}

	@Override
	public final void print(int i) {
		PrintStream out = checkAccess();

		if (out == this) {
			super.print(i);
		} else {
			out.print(i);
		}
	}

	@Override
	public final void print(long l) {
		PrintStream out = checkAccess();

		if (out == this) {
			super.print(l);
		} else {
			out.print(l);
		}
	}

	@Override
	public final void print(Object obj) {
		PrintStream out = checkAccess();

		if (out == this) {
			super.print(obj);
		} else {
			out.print(obj);
		}
	}

	@Override
	public final void print(String s) {
		PrintStream out = checkAccess();

		if (out == this) {
			super.print(s);
		} else {
			out.print(s);
		}
	}

	@Override
	public final void print(boolean b) {
		PrintStream out = checkAccess();

		if (out == this) {
			super.print(b);
		} else {
			out.print(b);
		}
	}

	@Override
	public final void println() {
		PrintStream out = checkAccess();

		if (out == this) {
			super.println();
		} else {
			out.println();
		}
	}

	@Override
	public final void println(char[] x) {
		PrintStream out = checkAccess();

		if (out == this) {
			super.println(x);
		} else {
			out.println(x);
		}
	}

	@Override
	public final void println(char x) {
		PrintStream out = checkAccess();

		if (out == this) {
			super.println(x);
		} else {
			out.println(x);
		}
	}

	@Override
	public final void println(double x) {
		PrintStream out = checkAccess();

		if (out == this) {
			super.println(x);
		} else {
			out.println(x);
		}
	}

	@Override
	public final void println(float x) {
		PrintStream out = checkAccess();

		if (out == this) {
			super.println(x);
		} else {
			out.println(x);
		}
	}

	@Override
	public final void println(int x) {
		PrintStream out = checkAccess();

		if (out == this) {
			super.println(x);
		} else {
			out.println(x);
		}
	}

	@Override
	public final void println(long x) {
		PrintStream out = checkAccess();

		if (out == this) {
			super.println(x);
		} else {
			out.println(x);
		}
	}

	@Override
	public final void println(Object x) {
		PrintStream out = checkAccess();

		if (out == this) {
			super.println(x);
		} else {
			out.println(x);
		}
	}

	@Override
	public final void println(String x) {
		PrintStream out = checkAccess();

		if (out == this) {
			super.println(x);
		} else {
			out.println(x);
		}
	}

	@Override
	public final void println(boolean x) {
		PrintStream out = checkAccess();

		if (out == this) {
			super.println(x);
		} else {
			out.println(x);
		}
	}

	@Override
	public final void write(byte[] buf, int off, int len) {
		PrintStream out = checkAccess();

		if (out == this) {
			super.write(buf, off, len);
		} else {
			out.write(buf, off, len);
		}
	}

	@Override
	public final void write(int b) {
		PrintStream out = checkAccess();

		if (out == this) {
			super.write(b);
		} else {
			out.write(b);
		}
	}

	private PrintStream checkAccess() {
		PrintStream out = Container.getComponent(IHostManager.class).getRobotOutputStream();

		return (out == null) ? this : out;
	}
}
