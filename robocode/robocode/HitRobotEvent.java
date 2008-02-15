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
 * A HitRobotEvent is sent to {@link Robot#onHitRobot onHitRobot} when your
 * robot collides with another robot. You can use the information contained in
 * this event to determine what to do.
 *
 * @author Mathew A. Nelson (original)
 */
public class HitRobotEvent extends Event {
	private String robotName;
	private double bearing;
	private double energy;
	private boolean atFault;

	/**
	 * Called by the game to create a new HitRobotEvent.
	 * 
	 * @param name the name of the robot you hit
	 * @param bearing the bearing to the robot that your robot hit, in radians
	 * @param energy the amount of energy of the robot you hit
	 * @param atFault {@code true} if your robot was moving toward the other
	 *    robot; {@code false} otherwise 
	 */
	public HitRobotEvent(String name, double bearing, double energy, boolean atFault) {
		this.robotName = name;
		this.bearing = bearing;
		this.energy = energy;
		this.atFault = atFault;
	}

	/**
	 * Returns the bearing to the robot you hit, relative to your robot's
	 * heading, in degrees (-180 <= getBearing() < 180)
	 *
	 * @return the bearing to the robot you hit, in degrees
	 */
	public double getBearing() {
		return bearing * 180.0 / Math.PI;
	}

	/**
	 * @deprecated Use {@link #getBearing()} instead.
	 */
	@Deprecated
	public double getBearingDegrees() {
		return getBearing();
	}

	/**
	 * Returns the bearing to the robot you hit, relative to your robot's
	 * heading, in radians (-PI <= getBearingRadians() < PI)
	 *
	 * @return the bearing to the robot you hit, in radians
	 */
	public double getBearingRadians() {
		return bearing;
	}

	/**
	 * Returns the amount of energy of the robot you hit.
	 *
	 * @return the amount of energy of the robot you hit
	 */
	public double getEnergy() {
		return energy;
	}

	/**
	 * Returns the name of the robot you hit.
	 *
	 * @return the name of the robot you hit
	 */
	public String getName() {
		return robotName;
	}

	/**
	 * @deprecated Use {@link #getName()} instead.
	 */
	@Deprecated
	public String getRobotName() {
		return robotName;
	}

	/**
	 * Checks if your robot was moving towards the robot that was hit.
	 * <p>
	 * If isMyFault() returns {@code true} then your robot's movement (including
	 * turning) will have stopped and been marked complete.
	 * <p>
	 * Note: If two robots are moving toward each other and collide, they will
	 * each receive two HitRobotEvents. The first will be the one if isMyFault()
	 * returns {@code true}.
	 * 
	 * @return {@code true} if your robot was moving towards the robot that was
	 *    hit; {@code false} otherwise.
	 */
	public boolean isMyFault() {
		return atFault;
	}
}
