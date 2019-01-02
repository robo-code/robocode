/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.test.robots;


import net.sf.robocode.test.helpers.RobocodeTestBed;

import org.junit.Assert;
import org.junit.Test;

import robocode.control.events.BattleFinishedEvent;
import robocode.control.events.TurnEndedEvent;
import robocode.control.snapshot.IScoreSnapshot;
import robocode.control.snapshot.ITurnSnapshot;


/**
 * @author Flemming N. Larsen (original)
 */
public class TestFairPlay extends RobocodeTestBed {

	ITurnSnapshot lastTurnSnapshot;

	@Test
	public void run() {
		super.run();
	}

	public String getRobotNames() {
		return "tested.robots.FairPlay,tested.robots.FairPlay";
	}

	@Override
	public int getNumRounds() {
		return 200;
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

		double minTotal = Math.min(score1.getTotalScore(), score2.getTotalScore());
		double maxTotal = Math.max(score1.getTotalScore(), score2.getTotalScore());

		Assert.assertTrue("The total scores should be almost the same", maxTotal / minTotal <= 1.05);

		double minFirsts = Math.min(score1.getTotalFirsts(), score2.getTotalFirsts());
		double maxFirsts = Math.max(score1.getTotalFirsts(), score2.getTotalFirsts());

		Assert.assertTrue("The total firsts should be almost the same", maxFirsts / minFirsts <= 1.05);
	}
}
