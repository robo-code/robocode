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
 *     Flemming N. Larsen
 *     - Javadocs
 *******************************************************************************/
package robocode.control.snapshot;


import robocode.peer.DebugProperty;
import robocode.peer.RobotState;


/**
 * Interface of a robot snapshot.
 * 
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (contributor)
 *
 * @since 1.6.2
 */
public interface IRobotSnapshot {

	/**
	 * Returns the name of the robot.
	 *
	 * @return the name of the robot.
	 */
	String getName();

	/**
	 * Returns the very short name of the robot.
	 *
	 * @return the very short name of the robot.
	 */
	String getShortName();

	/**
	 * Returns the very short name of the robot.
	 *
	 * @return the very short name of the robot.
	 */
	String getVeryShortName();

	/**
	 * Returns the name of the team, or name of the robot if the contestant is not a team.
	 *
	 * @return the name of the team, or name of the robot if the contestant is not a team.
	 */
	String getTeamName();

	/**
	 * Returns the index of the contestant that will not be changed during a battle.
	 *
	 * @return the index of the contestant that will not be changed during a battle.
	 */
	int getContestantIndex();

	/**
	 * Returns the robot status.
	 *
	 * @return the robot status.
	 */
	RobotState getState();

	/**
	 * Returns the energy level.
	 *
	 * @return the energy level.
	 */
	double getEnergy();

	/**
	 * Returns the velocity.
	 *
	 * @return the velocity.
	 */
	double getVelocity();

	/**
	 * Returns the body heading in radians.
	 *
	 * @return the body heading in radians.
	 */
	double getBodyHeading();

	/**
	 * Returns the gun heading in radians.
	 *
	 * @return the gun heading in radians.
	 */
	double getGunHeading();

	/**
	 * Returns the radar heading in radians.
	 *
	 * @return the radar heading in radians.
	 */
	double getRadarHeading();

	/**
	 * Returns the gun heat.
	 *
	 * @return the gun heat.
	 */
	double getGunHeat();

	/**
	 * Returns the x coordinate of the robot.
	 *
	 * @return the x coordinate of the robot.
	 */
	double getX();

	/**
	 * Returns the y coordinate of the robot.
	 *
	 * @return the y coordinate of the robot.
	 */
	double getY();

	/**
	 * Returns the color of the body.
	 *
	 * @return a RGBA color value. (Bits 24-31 are alpha, 16-23 are red, 8-15 are green, 0-7 are blue)
	 * 
	 * @see java.awt.Color#getRGB()
	 */
	int getBodyColor();

	/**
	 * Returns the color of the gun.
	 *
	 * @return a RGBA color value. (Bits 24-31 are alpha, 16-23 are red, 8-15 are green, 0-7 are blue)
	 * 
	 * @see java.awt.Color#getRGB()
	 */
	int getGunColor();

	/**
	 * Returns the color of the radar.
	 *
	 * @return a RGBA color value. (Bits 24-31 are alpha, 16-23 are red, 8-15 are green, 0-7 are blue)
	 * 
	 * @see java.awt.Color#getRGB()
	 */
	int getRadarColor();

	/**
	 * Returns the color of the scan arc.
	 *
	 * @return a RGBA color value. (Bits 24-31 are alpha, 16-23 are red, 8-15 are green, 0-7 are blue)
	 * 
	 * @see java.awt.Color#getRGB()
	 */
	int getScanColor();

	/**
	 * Returns a flag specifying if this robot is a {@link robocode.Droid Droid}.
	 *
	 * @return {@code true} if this robot is a Droid; {@code false} otherwise.
	 */
	boolean isDroid();

	/**
	 * Returns a flag specifying if this robot is an {@link robocode.robotinterfaces.IPaintRobot IPaintRobot}
	 * or is asking for getGraphics().
	 *
	 * @return {@code true} if this robot is a an IPaintRobot or is asking for getGraphics();
	 *         {@code false} otherwise.
	 */
	boolean isPaintRobot();

	/**
	 * Returns a flag specifying if robot's (onPaint) painting is enabled for the robot.
	 *
	 * @return {@code true} if the paintings for this robot is enabled;
	 *         {@code false} otherwise.
	 */
	boolean isPaintEnabled();

	/**
	 * Returns a flag specifying if RobocodeSG painting is enabled for the robot.
	 *
	 * @return {@code true} if RobocodeSG painting is enabled for this robot;
	 *         {@code false} otherwise.
	 */
	boolean isSGPaintEnabled();

	/**
	 * Returns a list of all debug properties.
	 * 
	 * @return a list of all debug properties.
	 */
	DebugProperty[] getDebugProperties();

	/**
	 * Returns a snapshot of the output print stream for this robot.
	 *
	 * @return a string containing the snapshot of the output print stream.
	 */
	String getOutputStreamSnapshot();

	/**
	 * Returns snapshot current score.
	 *
	 * @return snapshot current score.
	 */
	IScoreSnapshot getScoreSnapshot();
}
