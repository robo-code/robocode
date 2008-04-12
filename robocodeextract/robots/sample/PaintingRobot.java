/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Stefan Westen
 *     - Initial implementation
 *     Pavel Savara
 *     - Included in Robocode samples
 *******************************************************************************/
package sample;

import robocode.HitByBulletEvent;
import robocode.ScannedRobotEvent;
import robocode.Robot;

import java.awt.*;

/**
 * @author Stefan Westen (SGSample)
 * @author Pavel Savara
 */
public class PaintingRobot extends Robot {
    public void run() {
        while (true) {
            ahead(100);
            turnGunRight(360);
            back(100);
            turnGunRight(360);
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        fire(1);
    }

    public void onHitByBullet(HitByBulletEvent e) {
        turnLeft(90 - e.getBearing());
    }

    public void onPaint(java.awt.Graphics2D g) {
        g.setColor(Color.red);
        g.drawOval((int) (getX() - 50), (int) (getY() - 50), 100, 100);
    }
}