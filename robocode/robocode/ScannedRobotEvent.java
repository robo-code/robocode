/*******************************************************************************
 * Copyright (c) 2001 Mathew Nelson
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew Nelson - initial API and implementation
 *******************************************************************************/
package robocode;

/**
 * A ScannedRobotEvent is sent to {@link robocode.Robot#onScannedRobot} when you scan a robot.
 * You can use the information contained in this event to determine what to do.
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
 */
public ScannedRobotEvent(String name, double energy, double bearing, double distance, double heading, double velocity) {
	super();
	this.name = name;
	this.energy= energy;
	//int t = (int)(10 * energy + .1);
	//this.energy = (double)t / 10.0;
	this.bearing = bearing;
	this.distance = distance;
	this.heading = heading;
	this.velocity = velocity;
}
/**
 * Returns the angle to the robot, relative to your robot's heading, in degrees.  -180 < getRobotBearing() <= 180
 * @return the angle to the robot
 */
public double getBearing() {
	return bearing * 180.0 / Math.PI;
}
/**
 * Returns the angle to the robot, relative to your robot's heading, in radians.  -PI < getRobotBearingRadians() <= PI
 * @return the angle to the robot
 */
public double getBearingRadians() {
	return bearing;
}
/**
 * Returns the distance to the robot you scanned (your center to his center).
 * @return the distance to the robot you scanned.
 */
public double getDistance() {
	return distance;
}
/**
 * Returns the energy of the robot
 * @return the energy of the robot
 */
public double getEnergy() {
	return energy;
}
/**
 * Returns the direction the robot is facing, in degrees.  0 <= getRobotHeading() < 360
 * @return the direction the robot is facing
 */
public double getHeading() {
	return heading * 180.0 / Math.PI;
}
/**
 * Returns the direction the robot is facing, in radians.  0 <= getRobotHeading() < 2*PI
 * @return the direction the robot is facing
 */
public double getHeadingRadians() {
	return heading;
}
/**
 * @deprecated use getEnergy()
 */
public double getLife() {
	return energy;
}
/**
 * Returns the name of the robot
 * @return the name of the robot
 */
public java.lang.String getName() {
	return name;
}
/**
 * @deprecated use getBearing()
 */
public double getRobotBearing() {
	return getBearing();
}
/**
 * @deprecated use getBearing()
 */
public double getRobotBearingDegrees() {
	return getBearing();
}
/**
 * @deprecated use getBearingRadians()
 */
public double getRobotBearingRadians() {
	return getBearingRadians();
}
/**
 * @deprecated use getDistance()
 */
public double getRobotDistance() {
	return getDistance();
}
/**
 * @deprecated use getHeading()
 */
public double getRobotHeading() {
	return getHeading();
}
/**
 * @deprecated use getHeading()
 */
public double getRobotHeadingDegrees() {
	return getHeading();
}
/**
 * @deprecated use getHeadingRadians()
 */
public double getRobotHeadingRadians() {
	return getHeadingRadians();
}
/**
 * @deprecated use getEnergy()
 */
public double getRobotLife() {
	return getEnergy();
}
/**
 * @deprecated use getName()
 */
public java.lang.String getRobotName() {
	return getName();
}
/**
 * @deprecated use getVelocity()
 */
public double getRobotVelocity() {
	return getVelocity();
}
/**
 * Returns the velocity of the robot
 * @return the velocity of the robot
 */
public double getVelocity() {
	return velocity;
}
}
