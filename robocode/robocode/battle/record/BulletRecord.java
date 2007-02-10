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

	// Coordinates of the bullet
	public short x;
	public short y;

	// Delta coordinates on the robot the bullet has hit
	public byte deltaX;
	public byte deltaY;

	/**
	 * Constructs a new bullet record.
	 *
	 * @param owner index of the robot that fired the bullet
	 * @param bullet the bullet peer that is copied into this record
	 */
	public BulletRecord(int owner, BulletPeer bullet) {
		this.owner = (byte) owner;
		x = (short) (bullet.getX() + 0.5);
		y = (short) (bullet.getY() + 0.5);
		power = (byte) (bullet.getPower() * 10);
		frame = (byte) bullet.getFrame();
		deltaX = (byte) (bullet.deltaX + 0.5);
		deltaY = (byte) (bullet.deltaY + 0.5);

		state = (byte) bullet.getState();
		if (bullet.getExplosionImageIndex() == 1) {
			state |= 0x20; // set bit 5;
		}
		if (bullet.hasHitVictim) {
			state |= 0x40; // set bit 6
		}
		if (bullet.hasHitBullet) {
			state |= 0x80; // set bit 7
		}
	}
}
