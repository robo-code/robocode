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
package net.sf.robocode.io;


import robocode.control.events.BattleErrorEvent;
import robocode.control.events.BattleMessageEvent;
import robocode.control.events.IBattleListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;


/**
 * This is a class used for logging.
 *
 * @author Flemming N. Larsen (original)
 * @author Mathew A. Nelson (original)
 */
public class Logger {
	public static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("Robocode");
	private static IBattleListener logListener;

	public static void setLogListener(IBattleListener logListener) {
		Logger.logListener = logListener;
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

	public static void logMessage(String message) {
		if (logListener == null) {
			logger.info(message);
		} else {
			logListener.onBattleMessage(new BattleMessageEvent(message));
		}
	}

	public static void logWarning(String message) {
		if (logListener == null) {
			logger.warn(message);
		} else {
			logListener.onBattleMessage(new BattleMessageEvent(message));
		}
	}

	public static void logError(String message) {
		if (logListener == null) {
			logger.error(message);
		} else {
			logListener.onBattleError(new BattleErrorEvent(message));
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
}
