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
 * @author Pavel Savara (original)
 */
public class EventPriorityFilter extends AdvancedRobot {

	@Override
	public void run() {
		// noinspection InfiniteLoopStatement
		for (;;) {
			ahead(10); // make sure we eventually hits a wall to receive onHitWall
		}
	}

	@Override
	public void onScannedRobot(ScannedRobotEvent event) {
		out.println("Scanned!!!"); // a robot was scanned -> problem!
	}

	@Override
	public void onHitWall(HitWallEvent e) {
		// when the radar is turned here, at least another robot should be scanned
		// but event should not be dispatched because we are in handler with higher priority   
		turnRadarRight(360);
	}
}
