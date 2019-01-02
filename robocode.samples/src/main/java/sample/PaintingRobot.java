/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package sample;


import robocode.HitByBulletEvent;
import robocode.Robot;
import robocode.ScannedRobotEvent;

import java.awt.*;


/**
 * PaintingRobot - a sample robot that demonstrates the onPaint() and
 * getGraphics() methods.
 * Also demonstrate feature of debugging properties on RobotDialog
 * <p>
 * Moves in a seesaw motion, and spins the gun around at each end.
 * When painting is enabled for this robot, a red circle will be painted
 * around this robot.
 *
 * @author Stefan Westen (original SGSample)
 * @author Pavel Savara (contributor)
 */
public class PaintingRobot extends Robot {

	/**
	 * PaintingRobot's run method - Seesaw
	 */
	public void run() {
		while (true) {
			ahead(100);
			turnGunRight(360);
			back(100);
			turnGunRight(360);
		}
	}

	/**
	 * Fire when we see a robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		// demonstrate feature of debugging properties on RobotDialog
		setDebugProperty("lastScannedRobot", e.getName() + " at " + e.getBearing() + " degrees at time " + getTime());
		
		fire(1);
	}

	/**
	 * We were hit!  Turn perpendicular to the bullet,
	 * so our seesaw might avoid a future shot.
	 * In addition, draw orange circles where we were hit.
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		// demonstrate feature of debugging properties on RobotDialog
		setDebugProperty("lastHitBy", e.getName() + " with power of bullet " + e.getPower() + " at time " + getTime());

		// show how to remove debugging property
		setDebugProperty("lastScannedRobot", null);

		// gebugging by painting to battle view
		Graphics2D g = getGraphics();

		g.setColor(Color.orange);
		g.drawOval((int) (getX() - 55), (int) (getY() - 55), 110, 110);
		g.drawOval((int) (getX() - 56), (int) (getY() - 56), 112, 112);
		g.drawOval((int) (getX() - 59), (int) (getY() - 59), 118, 118);
		g.drawOval((int) (getX() - 60), (int) (getY() - 60), 120, 120);

		turnLeft(90 - e.getBearing());
	}

	/**
	 * Paint a red circle around our PaintingRobot
	 */
	public void onPaint(Graphics2D g) {
		g.setColor(Color.red);
		g.drawOval((int) (getX() - 50), (int) (getY() - 50), 100, 100);
		g.setColor(new Color(0, 0xFF, 0, 30));
		g.fillOval((int) (getX() - 60), (int) (getY() - 60), 120, 120);
	}
}
