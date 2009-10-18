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


import net.sf.robocode.security.HiddenAccess;
import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;


/**
 * @author Pavel Savara (original)
 */
public class IncludeNamespaceAttack extends AdvancedRobot {

	@Override
	public void run() {
		// noinspection InfiniteLoopStatement
		for (;;) {
			turnLeft(100);
			ahead(10);
			turnLeft(100);
			back(10);
		}
	}

	@Override
	public void onScannedRobot(ScannedRobotEvent event) {
		namespaceAttack();
	}

	private void namespaceAttack() {
		try {
			HiddenAccess.createRules(10, 10, 10, 10, 1);
		} catch (Throwable e) {
			// swalow security exception
			e.printStackTrace(out);
		}
	}
}
