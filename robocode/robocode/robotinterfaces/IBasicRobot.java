/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package robocode.robotinterfaces;


import robocode.robotinterfaces.peer.IBasicRobotPeer;


/**
 * A robot interface for creating a basic type of robot like {@link robocode.Robot}
 * that is able to receive common robot events, but not interactive events as
 * with the {@link robocode.Robot} class.
 * A basic robot allows blocking calls only and cannot handle custom events nor
 * writes to the file system like an advanced robot.
 *
 * @see robocode.Robot
 * @see IInteractiveRobot
 * @see IAdvancedRobot
 * @see ITeamRobot
 *
 * @author Pavel Savara (original)
 *
 * @since 1.6
 */
public interface IBasicRobot {

	/**
	 * This method is called by the game to invoke the <code>run()</code> method
	 * of your robot, where the program of your robot is implemented.
	 *
	 * @see java.lang.Runnable
	 *
	 * @return a runnable implementation
	 */
	Runnable getRobotRunnable();

	/**
	 * This method is called by the game to notify this robot about basic
	 * robot event. Hence, this method must be implemented so it returns your
	 * {@link IBasicEvents} listener.
	 *
	 * @return listener to basic events
	 */
	IBasicEvents getBasicEventListener();

	/**
	 * This method is called by the game. A robot peer is the object that deals
	 * with game mechanics and rules, and makes sure your robot abides by them.
	 * Do not call this method! Your robot will simply stop interacting with
	 * the game.
	 */
	void setPeer(IBasicRobotPeer peer);

	/**
	 * This method is called by the game when setting the output stream for your
	 * robot.
	 * Do not call this method!
	 */
	void setOut(java.io.PrintStream out);
}
