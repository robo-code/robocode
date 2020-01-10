/**
 * Copyright (c) 2001-2020 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
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
 * @see IJuniorRobot
 * @see IInteractiveRobot
 * @see IAdvancedRobot
 * @see ITeamRobot
 *
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (contributor)
 *
 * @since 1.6
 */
public interface IBasicRobot {

	/**
	 * This method is called by the game to invoke the
	 * {@link java.lang.Runnable#run() run()} method of your robot, where the program
	 * of your robot is implemented.
	 *
	 * @return a runnable implementation
	 * @see java.lang.Runnable#run()
	 * @since 1.6
	 */
	Runnable getRobotRunnable();

	/**
	 * This method is called by the game to notify this robot about basic
	 * robot event. Hence, this method must be implemented so it returns your
	 * {@link IBasicEvents} listener.
	 *
	 * @return listener to basic events or {@code null} if this robot should
	 *         not receive the notifications.
	 * @since 1.6
	 */
	IBasicEvents getBasicEventListener();

	/**
	 * Do not call this method! Your robot will simply stop interacting with
	 * the game.
	 * <p>
	 * This method is called by the game. A robot peer is the object that deals
	 * with game mechanics and rules, and makes sure your robot abides by them.
	 *
	 * @param peer the robot peer supplied by the game
	 */
	void setPeer(IBasicRobotPeer peer);

	/**
	 * Do not call this method!
	 * <p>
	 * This method is called by the game when setting the output stream for your
	 * robot.
	 *
	 * @param out the new output print stream for this robot
	 * @since 1.6
	 */
	void setOut(java.io.PrintStream out);
}
