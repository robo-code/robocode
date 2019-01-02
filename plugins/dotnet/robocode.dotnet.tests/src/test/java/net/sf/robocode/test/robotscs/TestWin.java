/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.test.robotscs;


import net.sf.robocode.test.helpers.RobocodeTestBed;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Assert;
import org.junit.Test;
import robocode.BattleResults;
import robocode.control.events.BattleCompletedEvent;
import robocode.control.events.TurnEndedEvent;
import robocode.control.snapshot.IRobotSnapshot;


/**
 * @author Pavel Savara (original)
 */
public class TestWin extends RobocodeTestBed {
	private int win = 0;
	private int end = 0;
	private int rend = 0;
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
		return "SampleCs.Target,tested.robotscs.BattleWin";        
	}

	@Override
	public void onTurnEnded(TurnEndedEvent event) {
		super.onTurnEnded(event);
		IRobotSnapshot robot = event.getTurnSnapshot().getRobots()[1];
		final String streamSnapshot = robot.getOutputStreamSnapshot();

		if (streamSnapshot.contains("Win!")) {
			win++;
		}
		if (streamSnapshot.contains("BattleEnded!")) {
			end++;
		}
		if (streamSnapshot.contains("RoundEnded!")) {
			rend++;
		}
	}

	public void onBattleCompleted(BattleCompletedEvent event) {
		results = event.getSortedResults();
	}

	@Override
	protected void runTeardown() {
		Assert.assertThat("always should win", win, is(getNumRounds()));
		Assert.assertThat("should get BattleEnded event", end, is(1));
		Assert.assertThat("should get RoundEnded event", rend, is(5));
		Assert.assertThat("always should be FIRST", results[0].getTeamLeaderName(), is("tested.robotscs.BattleWin"));
		Assert.assertThat("always should get score", results[0].getScore(), is(900));
	}
}
