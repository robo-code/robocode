/*******************************************************************************
 * Copyright (c) 2001-2012 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 *
 * Contributors:
 *     Flemming N. Larsen
 *     - Initial implementation
 *******************************************************************************/
package net.sf.robocode.io;


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
		if (battleListener != null) {
			int level = logEvent.getLevel().toInt();
			if (level >= Level.ERROR_INT) {
				battleListener.onBattleError(new BattleErrorEvent(logEvent.getMessage().toString()));
			} else if (level >= Level.INFO_INT) {
				battleListener.onBattleMessage(new BattleMessageEvent(logEvent.getMessage().toString()));
			}
		}
	}

	@Override
	public void close() {}

	@Override
	public boolean requiresLayout() {
		return false;
	}
}
