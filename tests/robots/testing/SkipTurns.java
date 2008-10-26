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
package testing;


import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;
import robocode.SkippedTurnEvent;


/**
 * @author Pavel Savara (original)
 */
public class SkipTurns extends AdvancedRobot {

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
		slowResponse();
	}

	@Override
	public void onSkippedTurn(SkippedTurnEvent event) {
		out.println("Skipped!!!");

		// satisfied, end battle please 
		throw new Error();
	}

	private void slowResponse() {
		Object w = new Object();

		synchronized (w) {
			try {
				w.wait(1200);
			} catch (InterruptedException e) {
				// eat interupt
				e.printStackTrace(out);
			}
		}
	}
}
