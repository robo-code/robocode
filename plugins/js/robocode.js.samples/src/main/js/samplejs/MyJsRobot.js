/*******************************************************************************
 * Copyright (c) 2001, 2009 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
importPackage(robocode)

function samplejs.MyJsRobot() {
    this.inherits(robocode.Robot)

    this.run() = function() {
        while (true) {
            ahead(100); // Move ahead 100
            turnGunRight(360); // Spin gun around
            back(100); // Move back 100
            turnGunRight(360); // Spin gun around
        }
    }

    this.onScannedRobot = function(e) {
        fire(1);
    }

    this.onHitByBullet = function(e) {
        turnLeft(90 - e.getBearing());
    }
}

var roboct = new JavaAdapter(robocode.Robot ,samplejs.MyJsRobot()); 
