/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Flemming N. Larsen
 *     - Initial implementation
 *******************************************************************************/
package robocode.battle.record;


import static robocode.gfx.ColorUtil.toRGB565;
import robocode.peer.RobotPeer;


/**
 * Robot record used for replaying battles containing a snapshot of a robot state.
 * Note that this class contains fields that are bytes and shorts
 * etc. in order to keep memory footprint as small as possible.
 *
 * @author Flemming N. Larsen (original)
 */
public class RobotRecord {

	// Index of the robot
	public byte index;

	// State of the robot
	public byte state;

	// Coordinates of the robot
	public short x;
	public short y;

	// Robot energy multiplied by 10 (fixed point precision)
	public short energy;

	// Body heading (using fixed point precision)
	public byte heading; // 2 pi maps to 0 - 255

	// Radar heading (using fixed point precision)
	public byte radarHeading; // 2 pi maps to 0 - 255

	// Gun heading (using fixed point precision)
	public byte gunHeading; // 2 pi maps to 0 - 255

	// Body color packed as RGB 565, where 0 means null
	public short bodyColor;

	// Gun color packed as RGB 565, where 0 means null
	public short gunColor;

	// Radar color packed as RGB 565, where 0 means null
	public short radarColor;

	// Scan color packed as RGB 565, where 0 means null
	public short scanColor;

	/**
	 * Constructs a new robot record.
	 *
	 * @param index index of the owner robot
	 * @param robot the robot peer that is copied into this record
	 */
	public RobotRecord(int index, RobotPeer robot) {
		this.index = (byte) index;
		x = (short) (robot.getX() + 0.5);
		y = (short) (robot.getY() + 0.5);
		energy = (short) (robot.getEnergy() * 10);
		heading = (byte) (128 * robot.getBodyHeading() / Math.PI);
		radarHeading = (byte) (128 * robot.getRadarHeading() / Math.PI);
		gunHeading = (byte) (128 * robot.getGunHeading() / Math.PI);
		state = (byte) robot.getState();
		bodyColor = toRGB565(robot.getBodyColor());
		gunColor = toRGB565(robot.getGunColor());
		radarColor = toRGB565(robot.getRadarColor());
		scanColor = toRGB565(robot.getScanColor());
	}
}
