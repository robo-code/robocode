/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Flemming N. Larsen
 *     - Initial implementation
 *******************************************************************************/
package net.sf.robocode.test.robots;


import net.sf.robocode.test.helpers.RobotTestBed;
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
public class TestScoring extends RobotTestBed {

	ITurnSnapshot lastTurnSnapshot;

	@Override
	public String getRobotNames() {
		return "sample.Corners,sample.Crazy,sample.Fire,sample.RamFire,sample.SittingDuck,sample.SpinBot,sample.Tracker,sample.TrackFire,sample.Walls";
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
			double currentScore = 0.5;

			for (int scoreIndex = 0; scoreIndex < score.getCurrentScores().size(); scoreIndex++) {
				currentScore += score.getCurrentScores().get(scoreIndex);
			}

			// TODO: figure out how this test should work with updated scoring
			// Assert.assertThat(score.getName(), (int)currentScore, is((int) (0.5 + score.getCurrentCombinedScore())));
		}
	}

	@Override
	public void onRoundEnded(RoundEndedEvent event) {
		super.onRoundEnded(event);

		for (IScoreSnapshot score : lastTurnSnapshot.getSortedTeamScores()) {

			double totalScore = (int) (0.5);

			for (int scoreIndex = 0; scoreIndex < score.getCurrentScores().size(); scoreIndex++) {
				totalScore += score.getTotalScores().get(scoreIndex);
			}

			// Assert.assertThat(score.getName(), (int)totalScore, is((int) (0.5 + score.getCombinedScore())));
		}
	}

	@Override
	public void onBattleFinished(BattleFinishedEvent event) {
		for (IScoreSnapshot score : lastTurnSnapshot.getSortedTeamScores()) {

			double totalScore = (int) (0.5);

			for (int scoreIndex = 0; scoreIndex < score.getCurrentScores().size(); scoreIndex++) {
				totalScore += score.getTotalScores().get(scoreIndex);
			}

			// Assert.assertThat(score.getName(), (int)totalScore, is((int) (0.5 + score.getCombinedScore())));
		}
	}
}
