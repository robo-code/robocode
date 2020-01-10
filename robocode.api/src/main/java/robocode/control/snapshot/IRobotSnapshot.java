/**
 * Copyright (c) 2001-2020 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package robocode.control.snapshot;


/**
 * Interface of a robot snapshot at a specific time in a battle.
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
	 * Returns the short name of the robot.
	 *
	 * @return the short name of the robot.
	 */
	String getShortName();

	/**
	 * Returns the very short name of the robot.
	 *
	 * @return the very short name of the robot.
	 */
	String getVeryShortName();

	/**
	 * Returns the name of the team, which can be the name of a robot if the contestant is not a team, but a robot.  
	 *
	 * @return the name of the team.
	 */
	String getTeamName();

	/**
	 * Returns the index of the robot, which is unique for the specific robot and constant during a battle.
	 *
	 * @return the robot index.
	 *
	 * @since 1.7.4
	 *
	 * @see #getTeamIndex()
	 */
	int getRobotIndex();

	/**
	 * Returns the index of the team that this robot is a member of, which is unique for the specific team and constant
	 * during a battle.
	 *
	 * @return the team index or -1 if the robot is not a member of a team.
	 *
	 * @since 1.7.4
	 *
	 * @see #getRobotIndex()
	 */
	int getTeamIndex();

	/**
	 * Returns the contestant index, which is unique for each robot or team participating in a battle.
	 * Note: If a team of robots is participating in a battle, this method will return the team index (see
	 * {@link #getTeamIndex()}); otherwise the robot index (see {@link #getRobotIndex()}) is used instead.
	 * This method is used for the battle results as scores are calculated for either a team of robots or individual
	 * robot.
	 * 
	 * @return the contestant index of the robot or team.
	 *
	 * @see #getRobotIndex()
	 * @see #getTeamIndex()
	 */
	int getContestantIndex();

	/**
	 * Returns the robot state.
	 *
	 * @return the robot state.
	 */
	RobotState getState();

	/**
	 * Returns the energy level of the robot.
	 *
	 * @return the energy level of the robot.
	 */
	double getEnergy();

	/**
	 * Returns the velocity of the robot.
	 *
	 * @return the velocity of the robot.
	 */
	double getVelocity();

	/**
	 * Returns the body heading of the robot in radians.
	 *
	 * @return the body heading of the robot in radians.
	 */
	double getBodyHeading();

	/**
	 * Returns the gun heading of the robot in radians.
	 *
	 * @return the gun heading of the robot in radians.
	 */
	double getGunHeading();

	/**
	 * Returns the radar heading of the robot in radians.
	 *
	 * @return the radar heading of the robot in radians.
	 */
	double getRadarHeading();

	/**
	 * Returns the gun heat of the robot.
	 *
	 * @return the gun heat of the robot.
	 */
	double getGunHeat();

	/**
	 * Returns the X position of the robot.
	 *
	 * @return the X position of the robot.
	 */
	double getX();

	/**
	 * Returns the Y position of the robot.
	 *
	 * @return the Y position of the robot.
	 */
	double getY();

	/**
	 * Returns the color of the body.
	 *
	 * @return an ARGB color value. (Bits 24-31 are alpha, 16-23 are red, 8-15 are green, 0-7 are blue)
	 * 
	 * @see java.awt.Color#getRGB()
	 */
	int getBodyColor();

	/**
	 * Returns the color of the gun.
	 *
	 * @return an ARGB color value. (Bits 24-31 are alpha, 16-23 are red, 8-15 are green, 0-7 are blue)
	 * 
	 * @see java.awt.Color#getRGB()
	 */
	int getGunColor();

	/**
	 * Returns the color of the radar.
	 *
	 * @return an ARGB color value. (Bits 24-31 are alpha, 16-23 are red, 8-15 are green, 0-7 are blue)
	 * 
	 * @see java.awt.Color#getRGB()
	 */
	int getRadarColor();

	/**
	 * Returns the color of the scan arc.
	 *
	 * @return an ARGB color value. (Bits 24-31 are alpha, 16-23 are red, 8-15 are green, 0-7 are blue)
	 * 
	 * @see java.awt.Color#getRGB()
	 */
	int getScanColor();

	/**
	 * Checks if this robot is a {@link robocode.Droid Droid}.
	 *
	 * @return {@code true} if this robot is a Droid; {@code false} otherwise.
	 */
	boolean isDroid();

	/**
	 * Checks if this robot is a {@link robocode.BorderSentry BorderSentry}.
	 *
	 * @return {@code true} if this robot is a BorderSentry; {@code false} otherwise.
	 *
	 * @since 1.9.0.0
	 */
	boolean isSentryRobot();

	/**
	 * Checks if this robot is a {@link robocode.robotinterfaces.IPaintRobot IPaintRobot} or is invoking getGraphics()
	 *
	 * @return {@code true} if this robot is a painting; {@code false} otherwise.
	 */
	boolean isPaintRobot();

	/**
	 * Checks if painting is enabled for this robot.
	 *
	 * @return {@code true} if painting is enabled for this robot; {@code false} otherwise.
	 */
	boolean isPaintEnabled();

	/**
	 * Checks if RobocodeSG painting (the point (0,0) is in the upper left corner) is enabled for this robot.
	 *
	 * @return {@code true} if RobocodeSG painting is enabled for this robot; {@code false} otherwise.
	 */
	boolean isSGPaintEnabled();

	/**
	 * Returns a snapshot of debug properties.
	 * 
	 * @return a snapshot of debug properties.
	 */
	IDebugProperty[] getDebugProperties();

	/**
	 * Returns a snapshot of the output print stream for this robot.
	 *
	 * @return a string containing the snapshot of the output print stream.
	 */
	String getOutputStreamSnapshot();

	/**
	 * Returns a snapshot of the current score for this robot.
	 *
	 * @return a snapshot of the current score for this robot.
	 */
	IScoreSnapshot getScoreSnapshot();
}
