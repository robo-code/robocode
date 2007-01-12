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
 * A HitWallEvent is sent to {@link robocode.Robot#onHitWall} when you collide a wall.
 * You can use the information contained in this event to determine what to do.
 *
 * @author Mathew A. Nelson (original)
 */
public class HitWallEvent extends Event {
	private double bearing = 0.0;

	/**
	 * Called by the game to create a new HitWallEvent.
	 */
	public HitWallEvent(double bearing) {
		this.bearing = bearing;
	}

	/**
	 * Returns the angle to the wall you hit, relative to your robot's heading.  -180 <= getBearing() < 180
	 *
	 * @return the angle to the wall you hit, in degrees
	 */
	public double getBearing() {
		return getBearingDegrees();
	}

	/**
	 * @deprecated use getBearing
	 */
	public double getBearingDegrees() {
		return bearing * 180.0 / Math.PI;
	}

	/**
	 * Returns the angle to the wall you hit in radians, relative to your robot's heading.  -PI <= getBearing() < PI
	 *
	 * @return the angle to the wall you hit, in radians
	 */
	public double getBearingRadians() {
		return bearing;
	}
}
