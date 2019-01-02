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
import robocode.control.events.BattleFinishedEvent;
import robocode.control.events.RoundEndedEvent;
import robocode.control.events.TurnEndedEvent;
import robocode.control.snapshot.IScoreSnapshot;
import robocode.control.snapshot.ITurnSnapshot;


/**
 * This test is used for testing if the current scores and total scores are
 * calculated correctly. Current scores are tested at each turn, and the total
 * scores are tested per round and per battle.
 * 
 * @author Flemming N. Larsen (original)
 */
public class TestScoring extends RobocodeTestBed {

	ITurnSnapshot lastTurnSnapshot;

	@Override
	public String getRobotNames() {
		return "SampleCs.Corners,SampleCs.Crazy,SampleCs.Fire,SampleCs.RamFire,SampleCs.SittingDuck,SampleCs.SpinBot,SampleCs.Tracker,SampleCs.TrackFire,SampleCs.Walls";
	}

	@Override
	public int getNumRounds() {
		return 10;
	}

	@Override
	public void onTurnEnded(TurnEndedEvent event) {
		super.onTurnEnded(event);

		lastTurnSnapshot = event.getTurnSnapshot();

		for (IScoreSnapshot score : lastTurnSnapshot.getSortedTeamScores()) {
			// Notice that the is no such thing as a 'current total last
			// survivor bonus'
			int currentScore = (int) (0.5 + score.getCurrentSurvivalScore() + score.getCurrentBulletDamageScore()
					+ score.getCurrentBulletKillBonus() + score.getCurrentRammingDamageScore()
					+ score.getCurrentRammingKillBonus() + score.getCurrentSurvivalBonus());

			Assert.assertThat(score.getName(), currentScore, is((int) (0.5 + score.getCurrentScore())));
		}
	}

	@Override
	public void onRoundEnded(RoundEndedEvent event) {
		super.onRoundEnded(event);

		for (IScoreSnapshot score : lastTurnSnapshot.getSortedTeamScores()) {

			int totalScore = (int) (0.5 + score.getTotalSurvivalScore() + score.getTotalLastSurvivorBonus()
					+ score.getTotalBulletDamageScore() + score.getTotalBulletKillBonus() + score.getTotalRammingDamageScore()
					+ score.getTotalRammingKillBonus());

			Assert.assertThat(score.getName(), totalScore, is((int) (0.5 + score.getTotalScore())));
		}
	}

	@Override
	public void onBattleFinished(BattleFinishedEvent event) {
		for (IScoreSnapshot score : lastTurnSnapshot.getSortedTeamScores()) {

			int totalScore = (int) (0.5 + score.getTotalSurvivalScore() + score.getTotalLastSurvivorBonus()
					+ score.getTotalBulletDamageScore() + score.getTotalBulletKillBonus() + score.getTotalRammingDamageScore()
					+ score.getTotalRammingKillBonus());

			Assert.assertThat(totalScore, is((int) (0.5 + score.getTotalScore())));
		}
	}
}
