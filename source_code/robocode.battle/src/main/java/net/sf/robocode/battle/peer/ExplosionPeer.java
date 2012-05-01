/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
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
 *     - Added getExplosionLength() that overrides the one at the super class
 *     - Changed so that the update() method is no longer removing the bullet
 *       from the battle field, which is now handled by updateBulletState()
 *     Titus Chen
 *     - Bugfix: Added Battle parameter to the constructor that takes a
 *       BulletRecord as parameter due to a NullPointerException that was raised
 *       as the battleField variable was not intialized
 *******************************************************************************/
package net.sf.robocode.battle.peer;


import robocode.BattleRules;
import robocode.control.snapshot.BulletState;

import java.util.List;


/**
 * @author Mathew A. Nelson (original)
 * @author Luis Crespo (contributor)
 * @author Flemming N. Larsen (contributor)
 * @author Titus Chen (contributor)
 */
public class ExplosionPeer extends BulletPeer {

	private static final int EXPLOSION_LENGTH = 71;

	public ExplosionPeer(RobotPeer owner, BattleRules battleRules) {
		super(owner, battleRules, -1);

		x = owner.getX();
		y = owner.getY();
		victim = owner;
		power = 1;
		state = BulletState.EXPLODED;
		explosionImageIndex = 1;
	}

	@Override
	public void update(List<RobotPeer> robots, List<BulletPeer> bullets, List<RobjectPeer> robjects) {
		x = owner.getX();
		y = owner.getY();

		nextFrame();

		updateBulletState();
	}

	@Override
	protected int getExplosionLength() {
		return EXPLOSION_LENGTH;
	}
}
