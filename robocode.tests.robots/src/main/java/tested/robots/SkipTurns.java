/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package tested.robots;


import robocode.AdvancedRobot;
import robocode.SkippedTurnEvent;
import robocode.StatusEvent;


/**
 * @author Pavel Savara (original)
 */
public class SkipTurns extends AdvancedRobot {
	private volatile int skipped = 0;

	static final int LIMIT = 5;

	@Override
	public void run() {
		// noinspection InfiniteLoopStatement
		for (;;) {
			if (skipped > LIMIT) {
				throw new Error("satisfied, end battle please");
			}
			turnLeft(10);
			if (skipped > LIMIT) {
				// satisfied, end battle please
				throw new Error();
			}
			ahead(1);
			if (skipped > LIMIT) {
				// satisfied, end battle please
				throw new Error();
			}
			turnLeft(10);
			if (skipped > LIMIT) {
				// satisfied, end battle please
				throw new Error();
			}
			back(1);
		}
	}

	@Override
	public void onStatus(StatusEvent e) {
		out.println("live");
		slowResponse();
	}

	@Override
	public void onSkippedTurn(SkippedTurnEvent event) {
		out.println("Skipped!!!");

		skipped++;
	}

	private final Object w = new Object();

	private void slowResponse() {

		synchronized (w) {
			try {
				if (skipped > 3) {
					w.wait(3000);
				} else {
					w.wait(130);
				}
			} catch (InterruptedException e) {
				// eat interrupt
				e.printStackTrace(out);
			}
		}
	}
}
