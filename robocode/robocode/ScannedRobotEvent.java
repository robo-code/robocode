/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Updated Javadocs
 *******************************************************************************/
package robocode;


import robocode.peer.RobotStatics;
import robocode.robotinterfaces.IBasicEvents;
import robocode.robotinterfaces.IBasicRobot;

import java.awt.*;


/**
 * A ScannedRobotEvent is sent to {@link Robot#onScannedRobot(ScannedRobotEvent)
 * onScannedRobot()} when you scan a robot.
 * You can use the information contained in this event to determine what to do.
 *
 * @author Mathew A. Nelson (original)
 */
public final class ScannedRobotEvent extends Event {
	private static int classPriority = 10;

	private final String name;
	private final double energy;
	private final double heading;
	private final double bearing;
	private final double distance;
	private final double velocity;

	/**
	 * Called by the game to create a new ScannedRobotEvent.
	 *
	 * @param name	 the name of the scanned robot
	 * @param energy   the energy of the scanned robot
	 * @param bearing  the bearing of the scanned robot, in radians
	 * @param distance the distance from your robot to the scanned robot
	 * @param heading  the heading of the scanned robot
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
	 * @return the energy of the robot
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
	 * @return the bearing to the robot you scanned, in degrees
	 * @deprecated Use {@link #getBearing()} instead.
	 */
	@Deprecated
	public double getRobotBearing() {
		return getBearing();
	}

	/**
	 * @return the robot bearing in degrees
	 * @deprecated Use {@link #getBearing()} instead.
	 */
	@Deprecated
	public double getRobotBearingDegrees() {
		return getBearing();
	}

	/**
	 * @return the bearing to the robot you scanned, in radians
	 * @deprecated Use {@link #getBearingRadians()} instead.
	 */
	@Deprecated
	public double getRobotBearingRadians() {
		return getBearingRadians();
	}

	/**
	 * @return the distance to the robot.
	 * @deprecated Use {@link #getDistance()} instead.
	 */
	@Deprecated
	public double getRobotDistance() {
		return getDistance();
	}

	/**
	 * @return the heading of the robot, in degrees
	 * @deprecated Use {@link #getHeading()} instead.
	 */
	@Deprecated
	public double getRobotHeading() {
		return getHeading();
	}

	/**
	 * @return the heading of the robot, in degrees
	 * @deprecated Use {@link #getHeading()} instead.
	 */
	@Deprecated
	public double getRobotHeadingDegrees() {
		return getHeading();
	}

	/**
	 * @return the heading of the robot, in radians
	 * @deprecated Use {@link #getHeadingRadians()} instead.
	 */
	@Deprecated
	public double getRobotHeadingRadians() {
		return getHeadingRadians();
	}

	/**
	 * @return the energy of the robot
	 * @deprecated Use {@link #getEnergy()} instead.
	 */
	@Deprecated
	public double getRobotLife() {
		return getEnergy();
	}

	/**
	 * @return the name of the robot
	 * @deprecated Use {@link #getName()} instead.
	 */
	@Deprecated
	public String getRobotName() {
		return getName();
	}

	/**
	 * @return the velocity of the robot
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(Event event) {
		final int res = super.compareTo(event);

		if (res != 0) {
			return res;
		}
		// Compare the distance, if the events are ScannedRobotEvents
		// The shorter distance to the robot, the higher priority
		if (event instanceof ScannedRobotEvent) {
			return (int) (this.getDistance() - ((ScannedRobotEvent) event).getDistance());
		}
		// No difference found
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int getDefaultPriority() {
		return classPriority;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispatch(IBasicRobot robot, RobotStatics statics, Graphics2D graphics) {
		IBasicEvents listener = robot.getBasicEventListener();

		if (listener != null) {
			listener.onScannedRobot(this);
		}
	}
}
