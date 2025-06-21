/*
 * Copyright (c) 2001-2025 Mathew A. Nelson and Robocode contributors
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
 * A sample robot that demonstrates graphical painting and debugging features.
 * <p>
 * This robot demonstrates the onPaint() and getGraphics() methods, as well as
 * the debugging properties feature on RobotDialog. It moves in a seesaw motion
 * and spins the gun around at each end. When painting is enabled, it displays
 * a red circle outline around itself with a translucent green fill.
 *
 * @author Stefan Westen (original SGSample)
 * @author Pavel Savara (contributor)
 */
public class PaintingRobot extends Robot {

	/**
	 * Main robot behavior method that implements a seesaw movement pattern.
	 * The robot moves forward, spins the gun, then backward, and repeats.
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
	 * Fires at scanned robots and demonstrates the debugging properties feature
	 * by storing information about the scanned robot.
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		// demonstrate the feature of debugging properties on RobotDialog
		setDebugProperty("lastScannedRobot", e.getName() + " at " + e.getBearing() + " degrees at time " + getTime());
		
		fire(1);
	}

	/**
	 * Responds when the robot is hit by a bullet by turning perpendicular
	 * to the bullet's path to potentially avoid future shots.
	 * Also demonstrates debugging features by setting properties and
	 * drawing orange circles at the hit location.
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		// demonstrate the feature of debugging properties on RobotDialog
		setDebugProperty("lastHitBy", e.getName() + " with power of bullet " + e.getPower() + " at time " + getTime());

		// show how to remove a debugging property
		setDebugProperty("lastScannedRobot", null);

		// debugging by painting to battle view
		Graphics2D g = getGraphics();

		g.setColor(Color.orange);
		g.drawOval((int) (getX() - 55), (int) (getY() - 55), 110, 110);
		g.drawOval((int) (getX() - 56), (int) (getY() - 56), 112, 112);
		g.drawOval((int) (getX() - 59), (int) (getY() - 59), 118, 118);
		g.drawOval((int) (getX() - 60), (int) (getY() - 60), 120, 120);

		turnLeft(90 - e.getBearing());
	}

	/**
	 * Paints custom graphics for the robot.
	 * Draws a red circle outline around the robot and overlays a translucent
	 *  green-filled circle to create a visual effect.
	 */
	public void onPaint(Graphics2D g) {
		g.setColor(Color.red);
		g.drawOval((int) (getX() - 50), (int) (getY() - 50), 100, 100);
		g.setColor(new Color(0, 0xFF, 0, 30));
		g.fillOval((int) (getX() - 60), (int) (getY() - 60), 120, 120);
	}
}
