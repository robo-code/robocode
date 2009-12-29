/*******************************************************************************
 * Copyright (c) 2001, 2009 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *     Flemming N. Larsen
 *     - Extended to include the DeathEvent
 *******************************************************************************/
package net.sf.robocode.test.robots;


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
	private BattleResults[] results; 
	private StringBuffer outputBuf = new StringBuffer();
	
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
		return "sample.MyFirstRobot,tested.robots.BattleWin";        
	}

	@Override
	public void onTurnEnded(TurnEndedEvent event) {
		super.onTurnEnded(event);
		IRobotSnapshot robot = event.getTurnSnapshot().getRobots()[1];
		final String streamSnapshot = robot.getOutputStreamSnapshot();

		outputBuf.append(streamSnapshot);
	}

	public void onBattleCompleted(BattleCompletedEvent event) {
		results = event.getSortedResults();
	}

	@Override
	protected void runTeardown() {
		String[] lines = outputBuf.toString().split("\\n");

		Assert.assertThat(lines[1], is("Round 1 of 5"));
		Assert.assertThat(lines[3], is("SYSTEM: tested.robots.BattleWin has died"));
		Assert.assertThat(lines[4], is("Death!"));
		Assert.assertThat(lines[6], is("Round 2 of 5"));
		Assert.assertThat(lines[8], is("SYSTEM: Bonus for killing sample.MyFirstRobot: 26"));
		Assert.assertThat(lines[9], is("SYSTEM: tested.robots.BattleWin wins the round."));
		Assert.assertThat(lines[10], is("Win!"));
		Assert.assertThat(lines[12], is("Round 3 of 5"));
		Assert.assertThat(lines[14], is("SYSTEM: Bonus for killing sample.MyFirstRobot: 14"));
		Assert.assertThat(lines[15], is("SYSTEM: tested.robots.BattleWin wins the round."));
		Assert.assertThat(lines[16], is("Win!"));
		Assert.assertThat(lines[18], is("Round 4 of 5"));
		Assert.assertThat(lines[20], is("SYSTEM: tested.robots.BattleWin wins the round."));
		Assert.assertThat(lines[21], is("Win!"));
		Assert.assertThat(lines[22], is("SYSTEM: tested.robots.BattleWin has died"));
		Assert.assertThat(lines[23], is("Death!"));
		Assert.assertThat(lines[25], is("Round 5 of 5"));
		Assert.assertThat(lines[27], is("SYSTEM: tested.robots.BattleWin wins the round."));
		Assert.assertThat(lines[28], is("Win!"));
		Assert.assertThat(lines[29], is("BattleEnded!"));
		Assert.assertThat(lines[30], is("SYSTEM: tested.robots.BattleWin has died"));
		Assert.assertThat(lines[31], is("Death!"));

		Assert.assertThat("1st robot should get right score", results[0].getScore(), is(485));
		Assert.assertThat("2nd robot should get right score", results[1].getScore(), is(280));
	}
}
