/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
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

	ExplosionPeer(RobotPeer owner, BattleRules battleRules) {
		super(owner, battleRules, 0);
		frame = 0;
		x = owner.getX();
		y = owner.getY();
		victim = owner;
		power = 1;
		state = BulletState.EXPLODED;
		explosionImageIndex = 1;
	}

	@Override
	public final void update(List<RobotPeer> robots, List<BulletPeer> bullets) {
		frame++;

		x = owner.getX();
		y = owner.getY();

		updateBulletState();
	}

	@Override
	protected int getExplosionLength() {
		return EXPLOSION_LENGTH;
	}
}
