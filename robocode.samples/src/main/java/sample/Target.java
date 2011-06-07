/*******************************************************************************
 * Copyright (c) 2001-2011 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial implementation
 *     Flemming N. Larsen
 *     - Maintainance
 *******************************************************************************/
package sample;


import robocode.AdvancedRobot;
import robocode.Condition;
import robocode.CustomEvent;

import java.awt.*;


/**
 * Target - a sample robot by Mathew Nelson, and maintained by Flemming N. Larsen
 * <p/>
 * Sits still.  Moves every time energy drops by 20.
 * This Robot demonstrates custom events.
 */
public class Target extends AdvancedRobot {

	int trigger; // Keeps track of when to move

	/**
	 * TrackFire's run method
	 */
	public void run() {
		// Set colors
		setBodyColor(Color.white);
		setGunColor(Color.white);
		setRadarColor(Color.white);

		// Initially, we'll move when life hits 80
		trigger = 80;
		// Add a custom event named "trigger hit",
		addCustomEvent(new Condition("triggerhit") {
			public boolean test() {
				return (getEnergy() <= trigger);
			}
		});
	}

	/**
	 * onCustomEvent handler
	 */
	public void onCustomEvent(CustomEvent e) {
		// If our custom event "triggerhit" went off,
		if (e.getCondition().getName().equals("triggerhit")) {
			// Adjust the trigger value, or
			// else the event will fire again and again and again...
			trigger -= 20;
			out.println("Ouch, down to " + (int) (getEnergy() + .5) + " energy.");
			// move around a bit.
			turnLeft(65);
			ahead(100);
		}
	}
}
