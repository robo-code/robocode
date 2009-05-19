/*******************************************************************************
 * Copyright (c) 2001, 2009 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Flemming N. Larsen
 *     - Initial implementation
 *******************************************************************************/
package net.sf.robocode.io;


import net.sf.robocode.security.HiddenAccess;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

import robocode.control.events.BattleErrorEvent;
import robocode.control.events.BattleMessageEvent;
import robocode.control.events.IBattleListener;


public class BattleListenerLogAppender extends AppenderSkeleton {

	private static IBattleListener battleListener;

	private final static BattleListenerLogAppender battleListenerLogAppender = new BattleListenerLogAppender();

	public static void setLogListener(IBattleListener battleListener) {
		BattleListenerLogAppender.battleListener = battleListener;
		
		Logger.getRootLogger().addAppender(battleListenerLogAppender);
	}

	@Override
	protected void append(LoggingEvent logEvent) {
		if (battleListener == null || !HiddenAccess.isSafeThread()) { // Make sure only safe thread will pass here
			return;
		}

		String message = logEvent.getMessage().toString();
		int level = logEvent.getLevel().toInt();

		if (level >= Level.ERROR_INT) {
			battleListener.onBattleError(new BattleErrorEvent(message));
		} else if (level >= Level.INFO_INT) {
			battleListener.onBattleMessage(new BattleMessageEvent(message));
		}
	}

	public void close() {}

	public boolean requiresLayout() {
		return false;
	}
}
