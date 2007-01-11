/*******************************************************************************
 * Copyright (c) 2001, 2007 Mathew A. Nelson and Robocode contributors
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
 *******************************************************************************/
package robocode.security;


import java.io.*;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public class SecurePrintStream extends PrintStream {

	public SecurePrintStream(OutputStream out, String accessString) {
		super(out);
	}

	public SecurePrintStream(OutputStream out, boolean autoFlush, String accessString) {
		super(out, autoFlush);
	}

	public final boolean checkError() {
		PrintStream out = checkAccess();

		if (out == this) {
			return super.checkError();
		} else {
			return out.checkError();
		}
	}

	public final void close() {
		PrintStream out = checkAccess();

		if (out == this) {
			super.close();
		} else {
			out.close();
		}
	}

	public final void flush() {
		PrintStream out = checkAccess();

		if (out == this) {
			super.flush();
		} else {
			out.flush();
		}
	}

	public final void print(char[] s) {
		PrintStream out = checkAccess();

		if (out == this) {
			super.print(s);
		} else {
			out.print(s);
		}
	}

	public final void print(char c) {
		PrintStream out = checkAccess();

		if (out == this) {
			super.print(c);
		} else {
			out.print(c);
		}
	}

	public final void print(double d) {
		PrintStream out = checkAccess();

		if (out == this) {
			super.print(d);
		} else {
			out.print(d);
		}
	}

	public final void print(float f) {
		PrintStream out = checkAccess();

		if (out == this) {
			super.print(f);
		} else {
			out.print(f);
		}
	}

	public final void print(int i) {
		PrintStream out = checkAccess();

		if (out == this) {
			super.print(i);
		} else {
			out.print(i);
		}
	}

	public final void print(long l) {
		PrintStream out = checkAccess();

		if (out == this) {
			super.print(l);
		} else {
			out.print(l);
		}
	}

	public final void print(Object obj) {
		PrintStream out = checkAccess();

		if (out == this) {
			super.print(obj);
		} else {
			out.print(obj);
		}
	}

	public final void print(String s) {
		PrintStream out = checkAccess();

		if (out == this) {
			super.print(s);
		} else {
			out.print(s);
		}
	}

	public final void print(boolean b) {
		PrintStream out = checkAccess();

		if (out == this) {
			super.print(b);
		} else {
			out.print(b);
		}
	}

	public final void println() {
		PrintStream out = checkAccess();

		if (out == this) {
			super.println();
		} else {
			out.println();
		}
	}

	public final void println(char[] x) {
		PrintStream out = checkAccess();

		if (out == this) {
			super.println(x);
		} else {
			out.println(x);
		}
	}

	public final void println(char x) {
		PrintStream out = checkAccess();

		if (out == this) {
			super.println(x);
		} else {
			out.println(x);
		}
	}

	public final void println(double x) {
		PrintStream out = checkAccess();

		if (out == this) {
			super.println(x);
		} else {
			out.println(x);
		}
	}

	public final void println(float x) {
		PrintStream out = checkAccess();

		if (out == this) {
			super.println(x);
		} else {
			out.println(x);
		}
	}

	public final void println(int x) {
		PrintStream out = checkAccess();

		if (out == this) {
			super.println(x);
		} else {
			out.println(x);
		}
	}

	public final void println(long x) {
		PrintStream out = checkAccess();

		if (out == this) {
			super.println(x);
		} else {
			out.println(x);
		}
	}

	public final void println(Object x) {
		PrintStream out = checkAccess();

		if (out == this) {
			super.println(x);
		} else {
			out.println(x);
		}
	}

	public final void println(String x) {
		PrintStream out = checkAccess();

		if (out == this) {
			super.println(x);
		} else {
			out.println(x);
		}
	}

	public final void println(boolean x) {
		PrintStream out = checkAccess();

		if (out == this) {
			super.println(x);
		} else {
			out.println(x);
		}
	}

	public final void write(byte[] buf, int off, int len) {
		PrintStream out = checkAccess();

		if (out == this) {
			super.write(buf, off, len);
		} else {
			out.write(buf, off, len);
		}
	}

	public final void write(int b) {
		PrintStream out = checkAccess();

		if (out == this) {
			super.write(b);
		} else {
			out.write(b);
		}
	}

	private PrintStream checkAccess() {
		SecurityManager securityManager = System.getSecurityManager();

		if (securityManager != null && securityManager instanceof RobocodeSecurityManager) {
			RobocodeSecurityManager rsm = (RobocodeSecurityManager) securityManager;
			PrintStream out = rsm.getRobotOutputStream();

			if (out == null) {
				return this;
			} else {
				return out;
			}
		}
		return this;
	}
}
