/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package tested.robots;


import robocode.*;
import robocode.Event;
import robocode.robotinterfaces.IBasicEvents;
import robocode.robotinterfaces.IJuniorRobot;
import robocode.robotinterfaces.peer.IBasicRobotPeer;
import robocode.util.Utils;

import java.awt.*;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;


/**
 * @author Pavel Savara (original)
 */
public class JuniorEvents implements IJuniorRobot, IBasicEvents, Runnable {

	IBasicRobotPeer peer;
	PrintStream out;
	final Map<String, Integer> counts = new HashMap<String, Integer>();
	Bullet bullet;

	public void run() {
		// noinspection InfiniteLoopStatement
		while (true) {
			peer.move(100); // Move ahead 100
			peer.turnGun(Math.PI * 2); // Spin gun around
			peer.move(-100); // Move back 100
			peer.turnGun(Math.PI * 2); // Spin gun around
		}
	}

	public Runnable getRobotRunnable() {
		return this;
	}

	public IBasicEvents getBasicEventListener() {
		return this;
	}

	public void setPeer(IBasicRobotPeer peer) {
		this.peer = peer;
	}

	public void setOut(PrintStream out) {
		this.out = out;
	}

	public void onStatus(StatusEvent event) {
		count(event);
		final Graphics2D g = peer.getGraphics();

		g.setColor(Color.orange);
		g.drawOval((int) (peer.getX() - 55), (int) (peer.getY() - 55), 110, 110);
	}

	public void onBulletHit(BulletHitEvent event) {
		count(event);
	}

	public void onBulletHitBullet(BulletHitBulletEvent event) {
		count(event);
	}

	public void onBulletMissed(BulletMissedEvent event) {
		count(event);
	}

	public void onDeath(DeathEvent event) {
		count(event);
	}

	public void onHitByBullet(HitByBulletEvent event) {
		count(event);
	}

	public void onHitRobot(HitRobotEvent event) {
		count(event);
	}

	public void onHitWall(HitWallEvent event) {
		count(event);
	}

	public void onRobotDeath(RobotDeathEvent event) {
		count(event);
	}

	public void onWin(WinEvent event) {
		count(event);

		// this is tested output
		for (Map.Entry<String, Integer> s : counts.entrySet()) {
			out.println(s.getKey() + " " + s.getValue());
		}
		out.println("last bullet heading " + bullet.getHeadingRadians());
	}

	public void onScannedRobot(ScannedRobotEvent event) {
		count(event);

		// Turn gun to point at the scanned robot
		peer.turnGun(Utils.normalAbsoluteAngle(peer.getBodyHeading() + event.getBearingRadians() - peer.getGunHeading())); //

		// Fire!
		double power = 1;

		Bullet firedBullet = peer.fire(power);

		if (firedBullet != null) {
			this.bullet = firedBullet;
		}
	}

	private void count(Event event) {
		final String name = event.getClass().getName();
		Integer curr = counts.get(name);

		if (curr == null) {
			curr = 0;
		}
		counts.put(name, curr + 1);
	}
}
