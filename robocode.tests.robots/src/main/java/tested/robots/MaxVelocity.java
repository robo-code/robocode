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
public class MaxVelocity extends robocode.AdvancedRobot {

	public void run() {
		for (;;) {
			int time = (int) getTime();

			if (time < 200) {
				out.println(time + ": " + getVelocity());
			}

			switch (time) {
			case 1:
				setAhead(1000);
				break;

			case 20:
				setBack(1000);
				break;

			case 40:
				setMaxVelocity(4.2);
				setAhead(1000);
				break;

			case 60:
				setBack(1000);
				break;

			case 80:
				setMaxVelocity(100);
				setAhead(1000);
				break;				

			case 100:
				setBack(1000);
				break;

			case 120:
				setMaxVelocity(-2);
				setAhead(1000);
				break;

			case 140:
				setBack(1000);
				break;

			case 160:
				setMaxVelocity(0);
				setAhead(1000);
				break;

			case 180:
				setBack(1000);
				break;
			}
			execute();
		}
	}
}
