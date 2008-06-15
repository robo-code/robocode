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
package robocode.battle;


import robocode.Event;

import java.io.InputStream;


/**
 * Used for controlling a robot from the e.g. the UI.
 *
 * @author Flemming N. Larsen
 *
 * @since 1.6.1
 */
public interface IRobotControl {

	/**
	 * Returns the name of the robot.
	 * @return the name of the robot.
	 */
	String getName();

	/**
	 * Returns the short name of the robot.
	 * @return the short name of the robot.
	 */
	String getShortName();

	/**
	 * Returns the unique full class name with version.
	 * @return the unique full class name with version.
	 */
	String getUniqueFullClassNameWithVersion();

	/**
	 * Kills the robot.
	 */
	void kill();

	/**
	 * Returns the output stream of the robot, which is an input stream for us.
	 * @return an input stream used for reading out the robot's output.
	 */
	InputStream getOutput();

	/**
	 * Enable or disable the robot paintings.
	 *
	 * @param enable {@code true} if paint must be enabled; {@code false} otherwise.
	 */
	void setPaintEnabled(boolean enable);

	/**
	 * Enable or disable the robot paintings using the RobocodeSG coordinate system
	 * with the y-axis reversed compared to the coordinate system used in Robocode.
	 *
	 * @param enable {@code true} if RobocodeSG paint coordinate system must be
	 *               enabled when painting the robot; {@code false} otherwise.
	 */
	void setSGPaintEnabled(boolean enable);

	/**
	 * Sends an interactive event for the robot.
	 *
	 * @param event the interactive event that has occurred to the robot.
	 */
	void sendInteractiveEvent(Event event);
}
