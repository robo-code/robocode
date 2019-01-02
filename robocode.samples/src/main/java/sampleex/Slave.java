/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package sampleex;


import robocode.AdvancedRobot;
import robocode.HitByBulletEvent;
import robocode.ScannedRobotEvent;


/**
 * This is robot derived from AdvancedRobot.
 * Only reason to use this inheritance and this class is that external robots are unable to call RobotPeer directly.
 *
 * @author Pavel Savara (original)
 */
class Slave extends AdvancedRobot {
	final MasterBase parent;

	public Slave(MasterBase parent) {
		this.parent = parent;
	}

	public void run() {
		parent.run();
	}

	public void onScannedRobot(ScannedRobotEvent e) {
		parent.onScannedRobot(e);
	}

	public void onHitByBullet(HitByBulletEvent e) {
		parent.onHitByBullet(e);
	}
}
