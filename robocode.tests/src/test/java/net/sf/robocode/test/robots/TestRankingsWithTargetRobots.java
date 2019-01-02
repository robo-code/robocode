/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.test.robots;


import net.sf.robocode.test.helpers.RobocodeTestBed;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Assert;
import robocode.control.events.BattleFinishedEvent;
import robocode.control.events.TurnEndedEvent;
import robocode.control.snapshot.IScoreSnapshot;
import robocode.control.snapshot.ITurnSnapshot;


/**
 * This test is used for checking the rankings of two sample.Target robots, i.e.
 * robots that does not shot, but is just waiting for someone to shot them. As
 * two sample.Targets robots are not fighting each other, the battles are
 * expected to end fairly even, but not necessarily due to collisions between
 * the robots and the wall.
 * 
 * @author Flemming N. Larsen (original)
 */
public class TestRankingsWithTargetRobots extends RobocodeTestBed {

	ITurnSnapshot lastTurnSnapshot;

	@Override
	public String getRobotNames() {
		return "sample.Target,sample.Target";
	}

	@Override
	public int getNumRounds() {
		return 20;
	}

	@Override
	public void onTurnEnded(TurnEndedEvent event) {
		super.onTurnEnded(event);

		lastTurnSnapshot = event.getTurnSnapshot();
	}

	@Override
	public void onBattleFinished(BattleFinishedEvent event) {
		final IScoreSnapshot[] scores = lastTurnSnapshot.getSortedTeamScores();
		final IScoreSnapshot score1 = scores[0];
		final IScoreSnapshot score2 = scores[1];

		// 1sts + 2nds = number of rounds, e.g. 4 + 6 = 10 (where 4 is 1st places, and 6 is 2nd places)
		Assert.assertThat("1st ranked robot's total 1st and 2nd places must be equal to the number of rounds",
				score1.getTotalFirsts() + score1.getTotalSeconds(), is(getNumRounds()));
		Assert.assertThat("2nd ranked robot's total 1st and 2nd places must be equal to the number of rounds",
				score2.getTotalFirsts() + score2.getTotalSeconds(), is(getNumRounds()));

		// If 1st robot's 1sts = 6, 2nds = 4, then 2nd robot's 1sts = 4, 2nds = 6
		/* Assert.assertThat(
		 "1st ranked robot's number of 1st places must be equal to the 2nd ranked robot's number of 2nd places",
		 score1.getTotalFirsts(), is(score2.getTotalSeconds()));
		 Assert.assertThat(
		 "2nd ranked robot's number of 1st places must be equal to the 1st ranked robot's number of 2nd places",
		 score2.getTotalFirsts(), is(score1.getTotalSeconds()));*/
	}
}
