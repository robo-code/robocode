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
package robocode.battle.snapshot;


import robocode.battle.Battle;
import robocode.peer.BulletPeer;
import robocode.peer.RobotPeer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * A battle snapshot, which is a view of the data for the battle at a particular
 * instant in time.
 * </p>
 * Note that this class is implemented as an immutable object. The idea of the
 * immutable object is that it cannot be modified after it has been created.
 * See the <a href="http://en.wikipedia.org/wiki/Immutable_object">Immutable
 * object</a> definition on Wikipedia.
 * </p>
 * Immutable objects are considered to be more thread-safe than mutable
 * objects, if implemented correctly.
 * </p>
 * All member fields must be final, and provided thru the constructor.
 * The constructor <em>must</em> make a deep copy of the data assigned to the
 * member fields and the getters of this class must return a copy of the data
 * that to return.
 *
 * @author Flemming N. Larsen (original)
 * @since 1.6.1
 */
public class BattleSnapshot implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	// The width of the battlefield
	// private final int fieldWidth;

	// The height of the battlefield
	// private final int fieldHeight;

	// List of all robots participating in the battle
	private final List<RobotSnapshot> robots;

	// List of all bullets currently the battlefield
	private final List<BulletSnapshot> bullets;

	// Current TPS (turns per second)
	private final int tps;

	/**
	 * Constructs a snapshot of the battle.
	 *
	 * @param battle the battle to make a snapshot of.
	 */
	public BattleSnapshot(Battle battle) {
		// fieldWidth = battle.getBattleField().getWidth();
		// fieldHeight = battle.getBattleField().getHeight();

		robots = new ArrayList<RobotSnapshot>();
		bullets = new ArrayList<BulletSnapshot>();

		for (RobotPeer robotPeer : battle.getRobots()) {
			robots.add(new RobotSnapshot(robotPeer));
		}

		for (BulletPeer bulletPeer : battle.getBullets()) {
			bullets.add(new BulletSnapshot(bulletPeer));
		}

		tps = battle.getTPS();
	}

	/**
	 * Returns the width of the battlefield.
	 *
	 * @return the width of the battlefield.
	 */
	// public int getFieldWidth() {
	// return fieldWidth;
	// }

	/**
	 * Returns the height of the battlefield.
	 *
	 * @return the height of the battlefield.
	 */
	// public int getFieldHeight() {
	// return fieldHeight;
	// }

	/**
	 * Returns all robots participating in the battle.
	 *
	 * @return a list containing all robots participating in the battle.
	 */
	public List<RobotSnapshot> getRobots() {
		return Collections.unmodifiableList(robots);
	}

	/**
	 * Returns all bullets currently the battlefield.
	 *
	 * @return a list containing all bullets currently the battlefield.
	 */
	public List<BulletSnapshot> getBullets() {
		return Collections.unmodifiableList(bullets);
	}

	/**
	 * Returns the current TPS (turns per second).
	 *
	 * @return the current TPS (turns per second).
	 */
	public int getTPS() {
		return tps;
	}
}
