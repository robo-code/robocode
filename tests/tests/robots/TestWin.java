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


import robocode.battle.snapshot.RobotSnapshot;
import robocode.battle.events.TurnEndedEvent;
import robocode.battle.events.BattleCompletedEvent;
import robocode.BattleResults;
import org.junit.Assert;
import org.junit.After;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.is;
import helpers.RobotTestBed;


/**
 * @author Pavel Savara (original)
 */
public class TestWin extends RobotTestBed {
	private int win = 0;
	private int end = 0;
	private BattleResults[] results; 

	@Test
	public void run() {
		super.run();
	}

	@Override
	public int getNumRounds() {
		return 5;
	}

	@Override
	public String getRobotNames() {
		return "sample.Target,testing.BattleWin";        
	}

	@Override
	public void onTurnEnded(TurnEndedEvent event) {
		RobotSnapshot robot = event.getTurnSnapshot().getRobots().get(1);
		final String streamSnapshot = robot.getOutputStreamSnapshot();

		if (streamSnapshot.contains("Win!")) {
			win++;
		}
		if (streamSnapshot.contains("BattleEnded!")) {
			end++;
		}
		System.out.print(streamSnapshot);
	}

	public void onBattleCompleted(BattleCompletedEvent event) {
		results = event.getResults();
	}

	@After
	public void tearDownWin() {
		Assert.assertThat("always should win", win, is(getNumRounds()));
		Assert.assertThat("should get BattleEnded event", end, is(1));
		Assert.assertThat("always should be FIRST", results[0].getTeamLeaderName(), is("testing.BattleWin"));
		Assert.assertThat("always should get score", results[0].getScore(), is(892));
	}

}
