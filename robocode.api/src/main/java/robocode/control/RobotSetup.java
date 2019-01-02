/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package robocode.control;


/**
 * Contains the initial position and heading for a robot.
 *
 * @author Flemming N. Larsen (original)
 * 
 * @since 1.9.2.0
 */
public class RobotSetup implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private final Double x;
	private final Double y;
	private final Double heading;

	/**
	 * Constructs a new RobotSetup.
	 *
	 * @param x is the x coordinate, where {@code null} means random.
	 * @param y is the y coordinate, where {@code null} means random.
	 * @param heading is the heading in degrees of the body, gun, and radar, where {@code null} means random.
	 */
	public RobotSetup(Double x, Double y, Double heading) {
		this.x = x;
		this.y = y;
		this.heading = heading;
	}

	/**
	 * Returns the x coordinate.
	 * @return the x coordinate, where {@code null} means unspecified (random).
	 */
	public Double getX() {
		return x;
	}

	/**
	 * Returns the y coordinate.
	 * @return the y coordinate, where {@code null} means unspecified (random).
	 */
	public Double getY() {
		return y;
	}

	/**
	 * Returns the body, gun, and radar heading (in degrees).
	 * @return the heading (in degrees), where {@code null} means unspecified (random).
	 */
	public Double getHeading() {
		return heading;
	}
}
