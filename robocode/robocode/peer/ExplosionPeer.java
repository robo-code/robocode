/*******************************************************************************
 * Copyright (c) 2001-2006 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Luis Crespo
 *     - Added states
 *     Flemming N. Larsen
 *     - Access to managers is now static
 *     - Code cleanup
 *******************************************************************************/
package robocode.peer;


import robocode.battle.*;
import robocode.manager.ImageManager;


/**
 * @author Mathew A. Nelson (original)
 * @author Luis Crespo (added states)
 * @author Flemming N. Larsen (current)
 */
public class ExplosionPeer extends BulletPeer {
	private int WHICH_EXPLOSION = 1;

	public ExplosionPeer(RobotPeer owner, Battle battle) {
		super(owner, battle);

		this.hasHitVictim = true;
		this.victim = owner;
		this.power = 1;
		this.isActive = false;
		this.bulletState = BULLET_STATE_EXPLODED;
		this.lastBulletState = BULLET_STATE_EXPLODED;
	}

	public final void update() {
		setX(getOwner().getX());
		setY(getOwner().getY());
		nextFrame();
		if (frame >= ImageManager.getExplosionFrames(WHICH_EXPLOSION)) {
			battle.removeBullet(this);
		}
		updateBulletState();
	}

	public int getWhichExplosion() {
		return WHICH_EXPLOSION;
	}
}
