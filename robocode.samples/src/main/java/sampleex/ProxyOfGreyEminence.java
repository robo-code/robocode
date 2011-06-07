/*******************************************************************************
 * Copyright (c) 2001-2011 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package sampleex;


import robocode.AdvancedRobot;
import robocode.HitByBulletEvent;
import robocode.ScannedRobotEvent;


/**
 * This is just dummy proxy, it's hiding the Eminence and
 * giving it more freedom to inherit from Monk rather than from Robot.
 */
public class ProxyOfGreyEminence extends AdvancedRobot {
	private final GreyEminence monk;

	public ProxyOfGreyEminence() {
		monk = new GreyEminence(this);
	}

	public void onHitByBullet(HitByBulletEvent event) {
		monk.onHitByBullet(event);
	}

	public void onScannedRobot(ScannedRobotEvent event) {
		monk.onScannedRobot(event);
	}

	public void run() {
		monk.run();
	}
}


/**
 * The power behind the throne.
 */
class GreyEminence extends RegullarMonk {
	private final ProxyOfGreyEminence proxy;

	public GreyEminence(ProxyOfGreyEminence proxy) {
		this.proxy = proxy;
	}

	/**
	 * This is not showing any aditional qualities over normal MyFirst robot.
	 * But it could, because architecture is no more tied by inheritance from Robot base class.
	 */
	public void run() {
		while (true) {
			proxy.ahead(100); // Move ahead 100
			proxy.turnGunRight(360); // Spin gun around
			proxy.back(100); // Move back 100
			proxy.turnGunRight(360); // Spin gun around
		}
	}

	public void onScannedRobot(ScannedRobotEvent e) {
		proxy.fire(1);
	}

	public void onHitByBullet(HitByBulletEvent e) {
		proxy.turnLeft(90 - e.getBearing());
	}
}
