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
 *     - Code cleanup
 *     - Added constructor for the BulletPeer in order to support replay feature
 *     - Fixed synchronization issue with update()
 *     - Replaced getting the number of explosion frames from image manager with
 *       integer constant
 *     Titus Chen
 *     - Bugfix: Added Battle parameter to the constructor that takes a
 *       BulletRecord as parameter due to a NullPointerException that was raised
 *       as the battleField variable was not intialized
 *******************************************************************************/
package robocode.peer;


import robocode.battle.Battle;
import robocode.battle.record.BulletRecord;


/**
 * @author Mathew A. Nelson (original)
 * @author Luis Crespo (contributor)
 * @author Flemming N. Larsen (contributor)
 * @author Titus Chen (contributor)
 */
public class ExplosionPeer extends BulletPeer {

	private static final int EXPLOSION_LENGTH = 71;

	public ExplosionPeer(RobotPeer owner, Battle battle) {
		super(owner, battle);

		hasHitVictim = true;
		victim = owner;
		power = 1;
		state = STATE_EXPLODED;
		lastState = STATE_EXPLODED;
		explosionImageIndex = 1;
	}

	public ExplosionPeer(RobotPeer owner, Battle battle, BulletRecord br) {
		super(owner, battle, br);
	}

	@Override
	public synchronized final void update() {
		x = owner.getX();
		y = owner.getY();
		nextFrame();
		if (frame >= EXPLOSION_LENGTH) {
			battle.removeBullet(this);
		}
		updateBulletState();
	}
}
