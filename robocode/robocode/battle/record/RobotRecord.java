/*******************************************************************************
 * Copyright (c) 2001-2007 Mathew A. Nelson and Robocode contributors
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


import java.awt.Color;
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
	public short heading; // 2 pi maps to 0 - 65535 (range of short type)

	// Radar heading (using fixed point precision)
	public short radarHeading; // 2 pi maps to 0 - 65535 (range of short type)

	// Gun heading (using fixed point precision)
	public short gunHeading; // 2 pi maps to 0 - 65535 (range of short type)

	// Body color
	public Color bodyColor;

	// Gun color
	public Color gunColor;

	// Radar color
	public Color radarColor;

	// Bullet color
	public Color bulletColor;

	// Scan arc color
	public Color scanColor;

	/**
	 * Constructs a new robot record.
	 *
	 * @param owner index of the robot
	 * @param robot the robot peer that is copied into this record
	 */
	public RobotRecord(int index, RobotPeer robot) {
		this.index = (byte) index;
		x = (short) (robot.getX() + 0.5);
		y = (short) (robot.getY() + 0.5);
		energy = (short) (robot.getEnergy() * 10);
		heading = (short) (32768 * robot.getHeading() / Math.PI);
		radarHeading = (short) (32768 * robot.getRadarHeading() / Math.PI);
		gunHeading = (short) (32768 * robot.getGunHeading() / Math.PI);
		state = (byte) robot.getState();
		bodyColor = robot.getBodyColor();
		gunColor = robot.getGunColor();
		radarColor = robot.getRadarColor();
		bulletColor = robot.getBulletColor();
		scanColor = robot.getScanColor();
	}
}
