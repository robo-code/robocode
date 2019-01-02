/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package tested.robots;


/**
 * @author Flemming N. Larsen (original)
 */
public class ReverseDirection extends robocode.AdvancedRobot {
	public void run() {
		setAhead(Double.POSITIVE_INFINITY);
		setMaxVelocity(1.0);
		execute();
		// robot has a positive velocity of 1.0

		out.println(getVelocity());		

		setMaxVelocity(0.5);
		setAhead(Double.NEGATIVE_INFINITY);
		execute();
		// robot should have a negative velocity of -0.5

		out.println(getVelocity());
	}
}
