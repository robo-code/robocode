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
 * Creation date: (10/18/2001 6:42:47 PM)
 * @author: Administrator
 */
public class SecurePrintStream extends java.io.PrintStream {
	private RobocodePermission printPermission = null;

	/**
	 * SecurePrintStream constructor comment.
	 * @param out java.io.OutputStream
	 */
	public SecurePrintStream(java.io.OutputStream out, String accessString) {
		super(out);
		this.printPermission = new RobocodePermission(accessString);
	}

	/**
	 * SecurePrintStream constructor comment.
	 * @param out java.io.OutputStream
	 * @param autoFlush boolean
	 */
	public SecurePrintStream(java.io.OutputStream out, boolean autoFlush, String accessString) {
		super(out, autoFlush);
		this.printPermission = new RobocodePermission(accessString);
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
		// super.println("Checking for " + x);
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

	/**
	 * Insert the method's description here.
	 * Creation date: (10/18/2001 6:49:33 PM)
	 */
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
