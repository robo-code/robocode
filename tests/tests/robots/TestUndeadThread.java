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
package robots;


import helpers.RobotTestBed;
import helpers.Assert;
import org.junit.Test;
import robocode.battle.events.TurnEndedEvent;
import robocode.battle.events.BattleErrorEvent;

import java.io.File;


/**
 * @author Pavel Savara (original)
 */
public class TestUndeadThread extends RobotTestBed {
	boolean messagedDestroy;
	boolean messagedForcing;

	@Test
	public void run() {
		super.run();
	}

	public void onBattleError(BattleErrorEvent event) {
		super.onBattleError(event);
		final String error = event.getError();

		if (error.contains("is not stopping.  Forcing a stop.")) {
			messagedForcing = true;
		}
		if (error.contains("Warning, could not destroy")) {
			messagedDestroy = true;
		}
	}

	@Override
	protected int getExpectedErrors() {
		return 4;
	}

	@Override
	public String getRobotNames() {
		return "sample.SittingDuck,testing.UndeadThread";
	}

	@Override
	protected void runTeardown() {
		Assert.assertTrue(messagedForcing);
		Assert.assertTrue(messagedDestroy);
		Assert.assertFalse(new File("C:\\Robocode.attack").exists());
	}

}
