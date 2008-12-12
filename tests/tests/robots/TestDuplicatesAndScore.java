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
import static org.hamcrest.CoreMatchers.is;
import org.junit.Assert;
import org.junit.Test;
import robocode.BattleResults;
import robocode.control.events.BattleCompletedEvent;
import robocode.control.events.BattleFinishedEvent;
import robocode.control.events.RoundStartedEvent;
import robocode.control.events.TurnEndedEvent;
import robocode.control.snapshot.IRobotSnapshot;


/**
 * @author Pavel Savara (original)
 */
public class TestDuplicatesAndScore extends RobotTestBed {
	private IRobotSnapshot[] robots;
	private BattleResults[] results;
	private int lastTurn;

	@Test
	public void run() {
		super.run();
	}

	@Override
	public int getExpectedRobotCount(String list) {
		return 4;
	}

	@Override
	public String getRobotNames() {
		return "sample.Fire,testing.TestTeam,testing.TestTeam,sample.Crazy";
	}

	@Override
	public void onRoundStarted(final RoundStartedEvent event) {
		super.onRoundStarted(event);
		if (event.getRound() == 0) {
			robots = event.getStartSnapshot().getRobots();

			helpers.Assert.assertNear(566.2968069, robots[0].getX());
			helpers.Assert.assertNear(436.3146436, robots[1].getX());
			helpers.Assert.assertNear(317.3362130, robots[2].getX());
			helpers.Assert.assertNear(717.6994092, robots[3].getX());
			helpers.Assert.assertNear(145.5531935, robots[4].getX());
			helpers.Assert.assertNear(734.4305396, robots[5].getX());
			helpers.Assert.assertNear(335.7732607, robots[6].getX());
			helpers.Assert.assertNear(390.8076498, robots[7].getX());

			helpers.Assert.assertNear(100.000, robots[0].getEnergy());
			helpers.Assert.assertNear(200.000, robots[1].getEnergy());
			helpers.Assert.assertNear(120.000, robots[2].getEnergy());
			helpers.Assert.assertNear(100.000, robots[3].getEnergy());
			helpers.Assert.assertNear(200.000, robots[4].getEnergy());
			helpers.Assert.assertNear(120.000, robots[5].getEnergy());
			helpers.Assert.assertNear(100.000, robots[6].getEnergy());
			helpers.Assert.assertNear(100.000, robots[7].getEnergy());
		}
	}

	@Override
	public void onTurnEnded(TurnEndedEvent event) {
		super.onTurnEnded(event);
		lastTurn = event.getTurnSnapshot().getTurn();
		robots = event.getTurnSnapshot().getRobots();

		if (lastTurn == 1) {
			helpers.Assert.assertNear(566.2968069, robots[0].getX());
			helpers.Assert.assertNear(437.1810299, robots[1].getX());
			helpers.Assert.assertNear(317.3362130, robots[2].getX());
			helpers.Assert.assertNear(717.6994092, robots[3].getX());
			helpers.Assert.assertNear(145.2637220, robots[4].getX());
			helpers.Assert.assertNear(734.4305396, robots[5].getX());
			helpers.Assert.assertNear(335.7732607, robots[6].getX());
			helpers.Assert.assertNear(389.8100070, robots[7].getX());
		}

		if (lastTurn == 1365) {
			helpers.Assert.assertNear(456.1227607, robots[0].getX());
			helpers.Assert.assertNear(401.8030122, robots[1].getX());
			helpers.Assert.assertNear(317.3362130, robots[2].getX());
			helpers.Assert.assertNear(685.9435204, robots[3].getX());
			helpers.Assert.assertNear(97.20308612, robots[4].getX());
			helpers.Assert.assertNear(734.4305396, robots[5].getX());
			helpers.Assert.assertNear(327.2578349, robots[6].getX());
			helpers.Assert.assertNear(523.5054202, robots[7].getX());
		}
	}

	@Override
	public void onBattleFinished(BattleFinishedEvent event) {
		if (event.isAborted()) {
			results = null;
		}
	}

	@Override
	public void onBattleCompleted(BattleCompletedEvent event) {
		results = event.getSortedResults();
	}

	@Override
	protected void runTeardown() {
		Assert.assertNotNull(results);
		Assert.assertNotNull(robots);
		Assert.assertThat(robots[0].getName(), is("sample.Fire (1)"));
		Assert.assertThat(robots[1].getName(), is("sampleteam.MyFirstLeader (1)"));
		Assert.assertThat(robots[2].getName(), is("sampleteam.MyFirstDroid (1)"));
		Assert.assertThat(robots[3].getName(), is("sample.Fire (2)"));
		Assert.assertThat(robots[4].getName(), is("sampleteam.MyFirstLeader (2)"));
		Assert.assertThat(robots[5].getName(), is("sampleteam.MyFirstDroid (2)"));
		Assert.assertThat(robots[6].getName(), is("sample.Fire (3)"));
		Assert.assertThat(robots[7].getName(), is("sample.Crazy"));
		Assert.assertThat(results[0].getTeamLeaderName(), is("testing.TestTeam (2)"));
		Assert.assertThat(results[1].getTeamLeaderName(), is("sample.Fire (1)"));
		Assert.assertThat(results[2].getTeamLeaderName(), is("testing.TestTeam (1)"));
		Assert.assertThat(results[3].getTeamLeaderName(), is("sample.Crazy"));

		Assert.assertThat(results[0].getLastSurvivorBonus(), is(0));
		Assert.assertThat(results[1].getLastSurvivorBonus(), is(70));
		Assert.assertThat(results[2].getLastSurvivorBonus(), is(0));
		Assert.assertThat(results[3].getLastSurvivorBonus(), is(0));

		Assert.assertThat(results[0].getRamDamage(), is(0));
		Assert.assertThat(results[1].getRamDamage(), is(1));
		Assert.assertThat(results[2].getRamDamage(), is(6));
		Assert.assertThat(results[3].getRamDamage(), is(11));

		Assert.assertThat(results[0].getBulletDamageBonus(), is(83));
		Assert.assertThat(results[1].getBulletDamageBonus(), is(19));
		Assert.assertThat(results[2].getBulletDamageBonus(), is(0));
		Assert.assertThat(results[3].getBulletDamageBonus(), is(0));

		Assert.assertThat(results[0].getBulletDamage(), is(496));
		Assert.assertThat(results[1].getBulletDamage(), is(216));
		Assert.assertThat(results[2].getBulletDamage(), is(420));
		Assert.assertThat(results[3].getBulletDamage(), is(136));

		Assert.assertThat(results[1].getScore(), is(657));
		Assert.assertThat(results[0].getScore(), is(979));
		Assert.assertThat(results[2].getScore(), is(576));
		Assert.assertThat(results[3].getScore(), is(347));
		Assert.assertThat(lastTurn, is(1370));
	}
}
