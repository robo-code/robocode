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
 *     Luis Crespo
 *     - Added states
 *     Flemming N. Larsen
 *     - Added constructor for the BulletPeer in order to support replay feature
 *     - Code cleanup
 *******************************************************************************/
package robocode.peer;


import robocode.battle.*;
import robocode.battle.record.BulletRecord;


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
		this.state = STATE_EXPLODED;
		this.lastState = STATE_EXPLODED;
	}

	public ExplosionPeer(RobotPeer owner, BulletRecord br) {
		super(owner, br);
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
