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
 *******************************************************************************/
package robocode;


/**
 * A HitRobotEvent is sent to {@link robocode.Robot#onHitRobot} when you collide with another robot
 * You can use the information contained in this event to determine what to do.
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
	 */
	public HitRobotEvent(String name, double bearing, double energy, boolean atFault) {
		this.robotName = name;
		this.bearing = bearing;
		this.energy = energy;
		this.atFault = atFault;
	}

	/**
	 * Returns the angle to the robot you hit, relative to your robot's heading.  -180 <= getBearing() < 180
	 *
	 * @return the angle to the robot you hit, in degrees
	 */
	public double getBearing() {
		return getBearingDegrees();
	}

	/**
	 * @deprecated use getBearing
	 */
	@Deprecated
	public double getBearingDegrees() {
		return bearing * 180.0 / Math.PI;
	}

	/**
	 * Returns the angle to the robot you hit in radians, relative to your robot's heading.  -PI <= getBearing() < PI
	 *
	 * @return the angle to the robot you hit, in radians
	 */
	public double getBearingRadians() {
		return bearing;
	}

	/**
	 * Returns the energy of the robot you hit
	 *
	 * @return the energy of the robot you hit
	 */
	public double getEnergy() {
		return energy;
	}

	/**
	 * Returns the name of the robot you hit
	 *
	 * @return the name of the robot you hit
	 */
	public String getName() {
		return robotName;
	}

	/**
	 * @deprecated use getName
	 */
	@Deprecated
	public String getRobotName() {
		return robotName;
	}

	/**
	 * If you were moving toward the robot you hit, isMyFault() will return true.
	 * If isMyFault() is true, then your robot's movement (including turning)
	 * will have stopped and been marked complete.
	 * Note:  If two robots are moving toward each other and collide,
	 * they will each receive two HitRobotEvents.  The first will be the one if isMyFault()
	 * returns true.
	 *
	 * @return whether or not you were moving toward the other robot.
	 */
	public boolean isMyFault() {
		return atFault;
	}
}
