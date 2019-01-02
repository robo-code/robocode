/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package tested.robots;


import robocode.AdvancedRobot;
import robocode.Condition;
import robocode.CustomEvent;
import robocode.ScannedRobotEvent;


/**
 * @author Pavel Savara (original)
 */
public class CustomEvents extends AdvancedRobot {
	@Override
	public void run() {

		addCustomEvent(new Condition("onTick99", 99) {
			public boolean test() {
				return true;
			}
		});

		addCustomEvent(new Condition("onTick30", 30) {
			public boolean test() {
				return true;
			}
		});

		addCustomEvent(new Condition("onLowEnergy98", 98) {
			public boolean test() {
				return getEnergy() < 20;
			}
		});
		while (true) {
			ahead(100); // Move ahead 100
			turnGunRight(360); // Spin gun around
			back(100); // Move back 100
			turnGunRight(360); // Spin gun around
		}
	}

	public void onCustomEvent(CustomEvent e) {
		out.println(getTime() + " " + e.getCondition().getName());
	}

	public void onScannedRobot(ScannedRobotEvent e) {
		out.println(getTime() + " onScannedRobot10");
	}
}
