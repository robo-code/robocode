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
 * bullet state.
 *
 * @author Flemming N. Larsen
 */
public class BulletRecord {
	
	// Index of the robot that fired the bullet
	public short owner;

	// State of the bullet
	public byte state;

	// Bullet power multiplied by 10 (fixed point precision)
	public byte power;

	// Coordinates of the bullet
	public short x;
	public short y;

	// Flag specifying if the bullet is an explosion peer
	public boolean isExplosion;

	// Index of the frame
	public byte frame;

	// Delta coordinates on the robot the bullet has hit
	public byte deltaX;
	public byte deltaY;

	// Flag specifying if the bullet has hit a victim
	public boolean hasHitVictim;

	// Flag specifying if the bullet has hit another bullet
	public boolean hasHitBullet;

	/**
	 * Constructs a new bullet record.
	 *
	 * @param owner index of the robot that fired the bullet
	 * @param bullet the bullet peer that is copied into this record
	 */
	public BulletRecord(int owner, BulletPeer bullet) {
		this.owner = (byte) owner;
		state = (byte) bullet.getState();
		x = (short) (bullet.getX() + 0.5);
		y = (short) (bullet.getY() + 0.5);
		power = (byte) (bullet.getPower() * 10);
		frame = (byte) bullet.getFrame();
		isExplosion = bullet.getExplosionImageIndex() == 1;
		deltaX = (byte) (bullet.deltaX + 0.5);
		deltaY = (byte) (bullet.deltaY + 0.5);
		hasHitVictim = bullet.hasHitVictim;
		hasHitBullet = bullet.hasHitBullet;
	}
}
