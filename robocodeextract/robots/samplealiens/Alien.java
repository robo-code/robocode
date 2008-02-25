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


import robocode.*;
import robocode.robotinterfaces.*;
import robocode.robotinterfaces.peer.*;

import java.io.PrintStream;


/**
 * @author Pavel Savara (original)
 */
public class Alien implements IInteractiveRobot, IBasicEvents, Runnable {

	PrintStream out;
	IStandardRobotPeer peer;
	public IInteractiveEvents getSystemEventListener() {
		return null;
	}

	public Runnable getRobotRunnable() {
		return this;
	}

	public IBasicEvents getBasicEventListener() {
		return this;
	}

	public void setPeer(IBasicRobotPeer iRobotPeer) {
		peer = (IStandardRobotPeer) iRobotPeer;
	}

	public IBasicRobotPeer getPeer() {
		return peer;
	}

	public void setOut(PrintStream printStream) {
		out = printStream;
	}

	public void run() {
		while (true) {
			peer.move(100); // Move ahead 100
			peer.turnGun(Math.PI * 2); // Spin gun around
			peer.move(-100); // Move back 100
			peer.turnGun(Math.PI * 2); // Spin gun around
		}
	}

	public void onScannedRobot(ScannedRobotEvent event) {
		peer.setFire(1);
	}

	public void onHitByBullet(HitByBulletEvent event) {
		peer.turnChassis(Math.PI / 2 + event.getBearingRadians());
	}

	public void onStatus(StatusEvent e) {}

	public void onBulletHit(BulletHitEvent event) {}

	public void onBulletHitBullet(BulletHitBulletEvent event) {}

	public void onBulletMissed(BulletMissedEvent event) {}

	public void onDeath(DeathEvent event) {}

	public void onHitRobot(HitRobotEvent event) {}

	public void onHitWall(HitWallEvent event) {}

	public void onRobotDeath(RobotDeathEvent event) {}

	public void onWin(WinEvent event) {}
}
