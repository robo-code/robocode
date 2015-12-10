/**
 * Copyright (c) 2001-2016 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 */
package sampleex;


import robocode.*;
import robocode.robotinterfaces.IBasicEvents;
import robocode.robotinterfaces.IBasicRobot;
import robocode.robotinterfaces.peer.IBasicRobotPeer;
import robocode.robotinterfaces.peer.IStandardRobotPeer;

import java.io.PrintStream;


/**
 * A sample robot.
 * Is not inherited from classic base robots, uses new experimental access to RobotPeer.
 * Use -DEXPERIMENTAL=true to start robocode for this robot.
 * This composition version is showing possible decomposition of robot, main runnable and event handler to different classes.
 *
 * @author Pavel Savara (original)
 */
public class AlienComposition implements IBasicRobot {
	PrintStream out;
	IStandardRobotPeer peer;
	final AlienMain main;
	final AlienEventHandler handler;

	public AlienComposition() {
		main = new AlienMain();
		handler = new AlienEventHandler();
	}

	public void setPeer(IBasicRobotPeer iRobotPeer) {
		peer = (IStandardRobotPeer) iRobotPeer;
	}

	public void setOut(PrintStream printStream) {
		out = printStream;
	}

	public Runnable getRobotRunnable() {
		return main;
	}

	public IBasicEvents getBasicEventListener() {
		return handler;
	}

	class AlienMain implements Runnable {
		public void run() {
			while (true) {
				peer.move(100); // Move ahead 100
				peer.turnGun(Math.PI * 2); // Spin gun around
				peer.move(-100); // Move back 100
				peer.turnGun(Math.PI * 2); // Spin gun around
			}
		}
	}


	class AlienEventHandler implements IBasicEvents {
		public void onScannedRobot(ScannedRobotEvent e) {
			peer.setFire(1);
		}

		public void onHitByBullet(HitByBulletEvent e) {
			peer.turnBody(Math.PI / 2 + e.getBearingRadians());
		}

		public void onStatus(StatusEvent e) {}

		public void onBulletHit(BulletHitEvent e) {}

		public void onBulletHitBullet(BulletHitBulletEvent e) {}

		public void onBulletMissed(BulletMissedEvent e) {}

		public void onDeath(DeathEvent e) {}

		public void onHitRobot(HitRobotEvent e) {}

		public void onHitWall(HitWallEvent e) {}

		public void onRobotDeath(RobotDeathEvent e) {}

		public void onWin(WinEvent e) {}
	}
}

