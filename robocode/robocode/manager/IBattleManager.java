/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Flemming N. Larsen
 *     - Initial implementation
 *******************************************************************************/
package robocode.manager;


import robocode.Event;
import robocode.battle.BattleProperties;
import robocode.control.BattleSpecification;
import robocode.control.events.IBattleListener;


/**
 * Used for controlling a robot from the e.g. the UI or RobocodeEngine.
 *
 * @author Flemming N. Larsen
 * @since 1.6.1
 */
public interface IBattleManager {

	/**
	 * Kills the robot.
	 *
	 * @param robotIndex the index of the robot to kill.
	 */
	void killRobot(int robotIndex);

	/**
	 * Enable or disable the robot paintings.
	 *
	 * @param robotIndex the index of the robot that must have its paintings enabled or disabled.
	 * @param enable {@code true} if paint must be enabled; {@code false} otherwise.
	 */
	void setPaintEnabled(int robotIndex, boolean enable);

	/**
	 * Enable or disable the robot paintings using the RobocodeSG coordinate system
	 * with the y-axis reversed compared to the coordinate system used in Robocode.
	 *
	 * @param robotIndex the index of the robot that must use RobocodeSG paintings.
	 * @param enable {@code true} if RobocodeSG paint coordinate system must be
	 *               enabled when painting the robot; {@code false} otherwise.
	 */
	void setSGPaintEnabled(int robotIndex, boolean enable);

	/**
	 * Sends an interactive event for the robot.
	 *
	 * @param event the interactive event that has occurred to the robot.
	 */
	void sendInteractiveEvent(Event event);

	void startNewBattle(BattleProperties battleProperties, boolean waitTillOver);

	void startNewBattle(BattleSpecification spec, boolean waitTillOver);

	void waitTillOver(); 

	void nextTurn();

	void prevTurn();

	void pauseBattle();

	void resumeBattle();

	void togglePauseResumeBattle();

	void resumeIfPausedBattle(); // TODO refactor, remove

	void pauseIfResumedBattle(); // TODO refactor, remove

	void restart();

	void replay();

	void stop(boolean waitTillEnd);

	void addListener(IBattleListener listener);

	void removeListener(IBattleListener listener);

	boolean isManagedTPS();

	void setManagedTPS(boolean value);

	String getBattlePath();

	String getBattleFilename();

	void setBattleFilename(String newBattleFilename);

	BattleProperties loadBattleProperties();

	void saveBattleProperties();

	BattleProperties getBattleProperties();

	void setDefaultBattleProperties();

	void cleanup();
}
