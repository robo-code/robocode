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
package tested.robots;


import robocode.Robot;


/**
 * @author Pavel Savara (original)
 */
public class Random extends Robot {

	@Override
	public void run() {

		while (true) {
			ahead(100 * Math.random());
			turnRight(180 * Math.random());
			back(100 * Math.random());
			turnLeft(360 * Math.random());
		}
	}
}

