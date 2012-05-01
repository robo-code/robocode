/*******************************************************************************
 * Copyright (c) 2001, 2009 Mathew A. Nelson and Robocode contributors
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


import junit.framework.Assert;
import net.sf.robocode.test.helpers.RobotTestBed;
import org.junit.Test;

import robocode.control.events.BattleFinishedEvent;
import robocode.control.events.TurnEndedEvent;
import robocode.control.snapshot.IScoreSnapshot;
import robocode.control.snapshot.ITurnSnapshot;


/**
 * @author Flemming N. Larsen (original)
 */
public class TestFairPlay extends RobotTestBed {

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
		return 100;
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

		double minTotal = Math.min(score1.getCombinedScore(), score2.getCombinedScore());
		double maxTotal = Math.max(score1.getCombinedScore(), score2.getCombinedScore());

		Assert.assertTrue("The total scores should be almost the same", maxTotal / minTotal <= 1.02);

		int minFirsts = Math.min(score1.getTotalFirsts(), score2.getTotalFirsts());
		int maxFirsts = Math.max(score1.getTotalFirsts(), score2.getTotalFirsts());

		Assert.assertTrue("The total firsts should be almost the same", maxFirsts / minFirsts <= 1.02);
	}
}
