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
import robocode.peer.BulletPeer;


/**
 * Bullet record used for replaying battles representing a snapshot state of a
 * bullet state. Note that this class contains fields that are bytes and shorts
 * etc. in order to keep memory footprint as small as possible.
 *
 * @author Flemming N. Larsen (original)
 */
public class BulletRecord {

	// Index of the robot that fired the bullet
	public byte owner;

	// State of the bullet, where
	// bit 0-2 is the robot state
	// bit 5 is specifying if the bullet is an explosion peer
	// bit 6 is specifying if the bullet has hit a victim
	// bit 7 is specifying if the bullet has hit another bullet
	public byte state;

	// Bullet power multiplied by 10 (fixed point precision)
	public byte power;

	// Index of the frame
	public byte frame;

	// The painted coordinates of the bullet
	public short x;
	public short y;

	// Bullet color packed as RGB 565, where 0 means null
	public short color;

	/**
	 * Constructs a new bullet record.
	 *
	 * @param owner  index of the robot that fired the bullet
	 * @param bullet the bullet peer that is copied into this record
	 */
	public BulletRecord(int owner, BulletPeer bullet) {
		this.owner = (byte) owner;
		x = (short) (bullet.getPaintX() + 0.5);
		y = (short) (bullet.getPaintY() + 0.5);
		power = (byte) (bullet.getPower() * 10);
		frame = (byte) bullet.getFrame();
		state = (byte) bullet.getState();
		color = toRGB565(bullet.getColor());
	}
}
