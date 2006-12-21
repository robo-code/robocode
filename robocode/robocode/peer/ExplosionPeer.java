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
 *     - Code cleanup
 *******************************************************************************/
package robocode.peer;


import robocode.battle.*;


/**
 * @author Mathew A. Nelson (original)
 * @author Luis Crespo (added states)
 * @author Flemming N. Larsen (current)
 */
public class ExplosionPeer extends BulletPeer {
	private int explosionImageIndex = 1;

	public ExplosionPeer(RobotPeer owner, Battle battle) {
		super(owner, battle);

		this.hasHitVictim = true;
		this.victim = owner;
		this.power = 1;
		this.isActive = false;
		this.state = BULLET_STATE_EXPLODED;
		this.lastState = BULLET_STATE_EXPLODED;
	}

	public final void update() {
		setX(getOwner().getX());
		setY(getOwner().getY());
		nextFrame();
		if (frame >= getBattle().getManager().getImageManager().getExplosionFrames(explosionImageIndex)) {
			battle.removeBullet(this);
		}
		updateBulletState();
	}

	public int getExplosionImageIndex() {
		return explosionImageIndex;
	}
}
