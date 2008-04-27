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
 *     - Initial implementation based on methods from robocode.util.Utils, which
 *       has been moved to this class
 *******************************************************************************/
package robocode.io;


import robocodeui.control.RobocodeListener;


/**
 * This is a class used for logging.
 *
 * @author Flemming N. Larsen (original)
 * @author Mathew A. Nelson (original)
 */
public class Logger {
	private static RobocodeListener logListener;
	private static String logBuffer = "";

	public static void setLogListener(RobocodeListener logListener) {
		Logger.logListener = logListener;
	}

	public static void log(String s) {
		if (logListener == null) {
			System.err.println(s);
		} else {
			logListener.battleMessage(s);
		}
	}

	public static void log(String s, Throwable e) {
		if (logListener == null) {
			System.err.println(s + ": " + e);
			e.printStackTrace(System.err);
		} else {
			logListener.battleMessage(s + ": " + e);
		}
	}

	public static void log(String s, boolean newline) {
		if (logListener == null) {
			if (newline) {
				System.err.println(s);
			} else {
				System.err.print(s);
				System.err.flush();
			}
		} else {
			if (newline) {
				logListener.battleMessage(logBuffer + s);
				logBuffer = "";
			} else {
				logBuffer += s;
			}
		}
	}

	public static void log(Throwable e) {
		if (logListener == null) {
			System.err.println(e);
			e.printStackTrace(System.err);
		} else {
			logListener.battleMessage("" + e);
		}
	}
}
