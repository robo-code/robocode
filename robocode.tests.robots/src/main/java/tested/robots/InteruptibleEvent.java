/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package tested.robots;


import robocode.AdvancedRobot;
import robocode.HitWallEvent;
import robocode.ScannedRobotEvent;


/**
 * Based on bug 2212289.
 *
 * Robot provided for the test named TestScanForRobotsWhenHitWall, which tests if onScannedRobot()
 * is called when the radar is turned from within a onHitWall() event.
 *
 * @author Flemming N. Larsen (original)
 */
public class InteruptibleEvent extends AdvancedRobot {

	@Override
	public void run() {
		setEventPriority("HitWallEvent", getEventPriority("ScannedRobotEvent")); // make same as scan

		// noinspection InfiniteLoopStatement
		for (;;) {
			ahead(10); // make sure we eventually hits a wall to receive onHitWall
		}
	}

	@Override
	public void onScannedRobot(ScannedRobotEvent event) {
		out.println("Scanned!!!"); // a robot was scanned -> success!
	}

	@Override
	public void onHitWall(HitWallEvent e) {
		setInterruptible(true); // make sure that the event handler can be interrupted and restarted
		turnRadarRight(360); // when the radar is turned here, at least another robot should be scanned
	}
}
