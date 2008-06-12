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
package testing;

import robocode.*;

/**
 * @author Pavel Savara (original)
 */
public class BattleWin extends Robot {
    public void run() {
        while (true) {
            ahead(100); // Move ahead 100
            turnGunRight(360); // Spin gun around
            back(100); // Move back 100
            turnGunRight(360); // Spin gun around
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        fire(1);
    }

    public void onHitByBullet(HitByBulletEvent e) {
        turnLeft(90 - e.getBearing());
    }

    public void onWin(WinEvent e) {
        out.println("Won!");
    }

    public void onDeath(DeathEvent e) {
        out.println("Died");
    }

    public void onBattleEnd(BattleEndedEvent event) {
        out.println("Battle ended "+event.getAborted());
    }
}
