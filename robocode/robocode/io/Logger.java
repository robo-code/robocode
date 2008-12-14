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


import robocode.control.events.BattleErrorEvent;
import robocode.control.events.BattleMessageEvent;
import robocode.control.events.IBattleListener;
import robocode.security.RobocodeSecurityManager;
import robocode.security.SecurePrintStream;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;


/**
 * This is a class used for logging.
 *
 * @author Flemming N. Larsen (original)
 * @author Mathew A. Nelson (original)
 */
public class Logger {
	private static IBattleListener logListener;
	private final static StringBuffer logBuffer = new StringBuffer();

	public static void setLogListener(IBattleListener logListener) {
		Logger.logListener = logListener;
	}

	public static void logMessage(String s) {
		logMessage(s, true);
	}

	public static void logMessage(Throwable e) {
		logMessage(toStackTraceString(e));
	}

	public static void logMessage(String message, Throwable t) {
		logMessage(message + ":\n" + toStackTraceString(t));
	}

	public static void logError(String message, Throwable t) {
		logError(message + ":\n" + toStackTraceString(t));
	}

	public static void logError(Throwable t) {
		logError(toStackTraceString(t));
	}

	public static void logMessage(String s, boolean newline) {
		if (logListener == null) {
			if (newline) {
				SecurePrintStream.realOut.println(s);
			} else {
				SecurePrintStream.realOut.print(s);
				SecurePrintStream.realOut.flush();
			}
		} else {
			synchronized (logBuffer) {
				if (!RobocodeSecurityManager.isSafeThreadSt()) {
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

	public static void logError(String s) {
		if (logListener == null) {
			SecurePrintStream.realErr.println(s);
		} else {
			logListener.onBattleError(new BattleErrorEvent(s));
		}
	}

	private static String toStackTraceString(Throwable t) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);

		t.printStackTrace(ps);
		ps.close();
		return baos.toString();
	}
}
