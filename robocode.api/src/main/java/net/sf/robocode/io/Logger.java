/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.io;


import net.sf.robocode.security.HiddenAccess;
import net.sf.robocode.util.StringUtil;
import robocode.control.events.BattleErrorEvent;
import robocode.control.events.BattleMessageEvent;
import robocode.control.events.IBattleListener;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;


/**
 * This is a class used for logging.
 *
 * @author Flemming N. Larsen (original)
 * @author Mathew A. Nelson (original)
 */
public class Logger {
	public static final PrintStream realOut = System.out;
	public static final PrintStream realErr = System.err;

	private static IBattleListener logListener;
	
	private final static StringBuffer logBuffer = new StringBuffer();

	public static void setLogListener(IBattleListener logListener) {
		Logger.logListener = logListener;
	}

	public static void logMessage(String s) {
		logMessage(s, true);
	}

	public static void logMessage(String s, boolean newline) {
		if (logListener == null) {
			if (System.getProperty("logMessages", "true").equalsIgnoreCase("true")) {
				s = StringUtil.toBasicLatin(s);
				if (newline) {
					realOut.println(s);
				} else {
					realOut.print(s);
					realOut.flush();
				}
			}
		} else {
			synchronized (logBuffer) {
				if (!HiddenAccess.isSafeThread()) {
					// we just queue it, to not let unsafe thread travel thru system
					logBuffer.append(s);
					logBuffer.append("\n");
				} else if (newline) {
					logListener.onBattleMessage(new BattleMessageEvent(logBuffer + s));
					logBuffer.setLength(0);
				} else {
					logBuffer.append(s);
				}
			}
		}
	}

	public static void logWarning(String s) {
		logMessage("Warning: " + s, true);
	}

	public static void logError(String message, Throwable t) {
		logError(message + ":\n" + toStackTraceString(t));
	}

	public static void logError(Throwable t) {
		logError(toStackTraceString(t));
	}

	public static void logError(String s) {
		if (logListener == null) {
			if (System.getProperty("logErrors", "true").equalsIgnoreCase("true")) {
				s = StringUtil.toBasicLatin(s);
				realErr.println(s);
			}
		} else {
			logListener.onBattleError(new BattleErrorEvent(s));
		}
	}

	private static String toStackTraceString(Throwable t) {
		if (t == null) {
			return "";
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);

		t.printStackTrace(ps);
		ps.close();
		return baos.toString();
	}

	public static void printlnToRobotsConsole(String s) {
		// this will get redirected to robot's console
		System.out.println(s);
	}
}
