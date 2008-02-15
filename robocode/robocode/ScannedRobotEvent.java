/*******************************************************************************
 * Copyright (c) 2001, 2007 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Updated Javadoc
 *******************************************************************************/
package robocode;


/**
 * A ScannedRobotEvent is sent to {@link Robot#onScannedRobot onScannedRobot}
 * when you scan a robot. You can use the information contained in this event to
 * determine what to do.
 *
 * @author Mathew A. Nelson (original)
 */
public class ScannedRobotEvent extends Event {
	private String name;
	private double energy;
	private double heading;
	private double bearing;
	private double distance;
	private double velocity;

	/**
	 * Called by the game to create a new ScannedRobotEvent.
	 * 
	 * @param name the name of the scanned robot
	 * @param energy the energy of the scanned robot
	 * @param bearing the bearing of the scanned robot, in radians
	 * @param distance the distance from your robot to the scanned robot
	 * @param heading the heading of the scanned robot
	 * @param velocity the velocity of the scanned robot
	 */
	public ScannedRobotEvent(String name, double energy, double bearing, double distance, double heading, double velocity) {
		super();
		this.name = name;
		this.energy = energy;
		this.bearing = bearing;
		this.distance = distance;
		this.heading = heading;
		this.velocity = velocity;
	}

	/**
	 * Returns the bearing to the robot you scanned, relative to your robot's
	 * heading, in degrees (-180 <= getBearing() < 180)
	 *
	 * @return the bearing to the robot you scanned, in degrees
	 */
	public double getBearing() {
		return bearing * 180.0 / Math.PI;
	}

	/**
	 * Returns the bearing to the robot you scanned, relative to your robot's
	 * heading, in radians (-PI <= getBearingRadians() < PI)
	 *
	 * @return the bearing to the robot you scanned, in radians
	 */
	public double getBearingRadians() {
		return bearing;
	}

	/**
	 * Returns the distance to the robot (your center to his center).
	 *
	 * @return the distance to the robot.
	 */
	public double getDistance() {
		return distance;
	}

	/**
	 * Returns the energy of the robot.
	 *
	 * @return the energy of the robot
	 */
	public double getEnergy() {
		return energy;
	}

	/**
	 * Returns the heading of the robot, in degrees (0 <= getHeading() < 360)
	 *
	 * @return the heading of the robot, in degrees
	 */
	public double getHeading() {
		return heading * 180.0 / Math.PI;
	}

	/**
	 * Returns the heading of the robot, in radians (0 <= getHeading() < 2 * PI)
	 *
	 * @return the heading of the robot, in radians
	 */
	public double getHeadingRadians() {
		return heading;
	}

	/**
	 * @deprecated Use {@link #getEnergy()} instead.
	 */
	@Deprecated
	public double getLife() {
		return energy;
	}

	/**
	 * Returns the name of the robot.
	 *
	 * @return the name of the robot
	 */
	public String getName() {
		return name;
	}

	/**
	 * @deprecated Use {@link #getBearing()} instead.
	 */
	@Deprecated
	public double getRobotBearing() {
		return getBearing();
	}

	/**
	 * @deprecated Use {@link #getBearing()} instead.
	 */
	@Deprecated
	public double getRobotBearingDegrees() {
		return getBearing();
	}

	/**
	 * @deprecated Use {@link #getBearingRadians()} instead.
	 */
	@Deprecated
	public double getRobotBearingRadians() {
		return getBearingRadians();
	}

	/**
	 * @deprecated Use {@link #getDistance()} instead.
	 */
	@Deprecated
	public double getRobotDistance() {
		return getDistance();
	}

	/**
	 * @deprecated Use {@link #getHeading()} instead.
	 */
	@Deprecated
	public double getRobotHeading() {
		return getHeading();
	}

	/**
	 * @deprecated Use {@link #getHeading()} instead.
	 */
	@Deprecated
	public double getRobotHeadingDegrees() {
		return getHeading();
	}

	/**
	 * @deprecated Use {@link #getHeadingRadians()} instead.
	 */
	@Deprecated
	public double getRobotHeadingRadians() {
		return getHeadingRadians();
	}

	/**
	 * @deprecated Use {@link #getEnergy()} instead.
	 */
	@Deprecated
	public double getRobotLife() {
		return getEnergy();
	}

	/**
	 * @deprecated Use {@link #getName()} instead.
	 */
	@Deprecated
	public String getRobotName() {
		return getName();
	}

	/**
	 * @deprecated Use {@link #getVelocity()} instead.
	 */
	@Deprecated
	public double getRobotVelocity() {
		return getVelocity();
	}

	/**
	 * Returns the velocity of the robot.
	 *
	 * @return the velocity of the robot
	 */
	public double getVelocity() {
		return velocity;
	}
}
