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
package samplescala

import robocode.{MouseEvent, ScannedRobotEvent, Robot, HitByBulletEvent}

class MyScalaRobot extends Robot {
  override def run() {
    while (true) {
      ahead(100); // Move ahead 100
      turnGunRight(360); // Spin gun around
      back(100); // Move back 100
      turnGunRight(360); // Spin gun around
    }
  }

  override def onScannedRobot(e:ScannedRobotEvent) {
    fire(1);
  }

  override def onHitByBullet(e:HitByBulletEvent) {
    turnLeft(90 - e.getBearing());
  }
}