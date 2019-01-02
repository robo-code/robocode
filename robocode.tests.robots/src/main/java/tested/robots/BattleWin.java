/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package tested.robots;


import robocode.*;


/**
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (contributor)
 */
public class BattleWin extends Robot {

	@Override
	public void run() {
		while (true) {
			ahead(100); // Move ahead 100
			turnGunRight(360); // Spin gun around
			back(100); // Move back 100
			turnGunRight(360); // Spin gun around
		}
	}

	@Override
	public void onScannedRobot(ScannedRobotEvent e) {
		fire(1);
	}

	@Override
	public void onHitByBullet(HitByBulletEvent e) {
		turnLeft(90 - e.getBearing());
	}

	@Override
	public void onWin(WinEvent e) {
		out.println("Win!");
	}

	@Override
	public void onDeath(DeathEvent e) {
		out.println("Death!");
	}

	@Override
	public void onRoundEnded(RoundEndedEvent event) {
		out.println("RoundEnded!");
	}

	@Override
	public void onBattleEnded(BattleEndedEvent event) {
		out.println("BattleEnded!");
	}
}
