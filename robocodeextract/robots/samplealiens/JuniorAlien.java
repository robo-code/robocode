/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package samplealiens;


import java.awt.Graphics2D;
import java.io.PrintStream;

import robocode.robotinterfaces.*;
import robocode.robotinterfaces.peer.*;
import robocode.*;


/**
 * @author Pavel Savara (original)
 */
public class JuniorAlien implements IJuniorRobot, IJuniorEvents, IBasicEvents, Runnable {
	PrintStream out;
	IJuniorRobotPeer peer;

	public Runnable getRobotRunnable() {
		return this;
	}

	public IBasicEvents getBasicEventListener() {
		return this;
	}

	public void setPeer(IBasicRobotPeer iBasicRobotPeer) {
		peer = (IJuniorRobotPeer) iBasicRobotPeer;
	}

	public IBasicRobotPeer getPeer() {
		return peer;
	}

	public void setOut(PrintStream printStream) {
		this.out = printStream;
	}

	public void onJuniorEvent(CustomEvent e) {}

	public void onStatus(StatusEvent e) {}

	public void onBulletHit(BulletHitEvent e) {}

	public void onBulletHitBullet(BulletHitBulletEvent e) {}

	public void onBulletMissed(BulletMissedEvent e) {}

	public void onDeath(DeathEvent e) {}

	public void onHitRobot(HitRobotEvent e) {}

	public void onHitWall(HitWallEvent e) {}

	public void onRobotDeath(RobotDeathEvent e) {}

	public void onWin(WinEvent e) {}

	public void onPaint(Graphics2D g) {}

	public void onHitByBullet(HitByBulletEvent e) {
		peer.turnChassis(Math.PI / 2 + e.getBearingRadians());
	}

	public void onScannedRobot(ScannedRobotEvent e) {
		peer.setFire(1);
	}

	public void run() {
		while (true) {
			peer.move(100); // Move ahead 100
			peer.turnGun(Math.PI * 2); // Spin gun around
			peer.move(-100); // Move back 100
			peer.turnGun(Math.PI * 2); // Spin gun around
		}
	}
}
