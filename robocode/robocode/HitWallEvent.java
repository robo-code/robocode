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
 * A HitWallEvent is sent to {@link Robot#onHitWall onHitWall} when you collide
 * a wall. You can use the information contained in this event to determine what
 * to do.
 *
 * @author Mathew A. Nelson (original)
 */
public class HitWallEvent extends Event {
	private double bearing = 0.0;

	/**
	 * Called by the game to create a new HitWallEvent.
	 *
	 * @param bearing the bearing to the wall that your robot hit, in radians
	 */
	public HitWallEvent(double bearing) {
		this.bearing = bearing;
	}

	/**
	 * Returns the bearing to the wall you hit, relative to your robot's
	 * heading, in degrees (-180 <= getBearing() < 180)
	 *
	 * @return the bearing to the wall you hit, in degrees
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
	 * Returns the bearing to the wall you hit, relative to your robot's
	 * heading, in radians (-PI <= getBearingRadians() < PI)
	 *
	 * @return the bearing to the wall you hit, in radians
	 */
	public double getBearingRadians() {
		return bearing;
	}
}
