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
public class TestDuplicatesAndScore extends RobocodeTestBed {
	private IRobotSnapshot[] robots;
	private BattleResults[] results;
	private int lastTurn;

	@Test
	public void run() {
		super.run();
	}

	@Override
	public int getExpectedRobotCount(String list) {
		return 8;
	}

	@Override
	public String getRobotNames() {
		return "sample.Fire,tested.robots.TestTeam,tested.robots.TestTeam,sample.Crazy";
	}

	@Override
	public void onRoundStarted(final RoundStartedEvent event) {
		super.onRoundStarted(event);
		if (event.getRound() == 0) {
			robots = event.getStartSnapshot().getRobots();
			Assert.assertEquals(8, robots.length);

			net.sf.robocode.test.helpers.Assert.assertNear(568.1445492, robots[0].getX());
			net.sf.robocode.test.helpers.Assert.assertNear(436.7181397, robots[1].getX());
			net.sf.robocode.test.helpers.Assert.assertNear(316.4177265, robots[2].getX());
			net.sf.robocode.test.helpers.Assert.assertNear(721.2294027, robots[3].getX());
			net.sf.robocode.test.helpers.Assert.assertNear(142.7260067, robots[4].getX());
			net.sf.robocode.test.helpers.Assert.assertNear(738.1464346, robots[5].getX());
			net.sf.robocode.test.helpers.Assert.assertNear(335.0596303, robots[6].getX());
			net.sf.robocode.test.helpers.Assert.assertNear(390.7055126, robots[7].getX());

			net.sf.robocode.test.helpers.Assert.assertNear(100.000, robots[0].getEnergy());
			net.sf.robocode.test.helpers.Assert.assertNear(200.000, robots[1].getEnergy());
			net.sf.robocode.test.helpers.Assert.assertNear(120.000, robots[2].getEnergy());
			net.sf.robocode.test.helpers.Assert.assertNear(100.000, robots[3].getEnergy());
			net.sf.robocode.test.helpers.Assert.assertNear(200.000, robots[4].getEnergy());
			net.sf.robocode.test.helpers.Assert.assertNear(120.000, robots[5].getEnergy());
			net.sf.robocode.test.helpers.Assert.assertNear(100.000, robots[6].getEnergy());
			net.sf.robocode.test.helpers.Assert.assertNear(100.000, robots[7].getEnergy());
		}
	}

	@Override
	public void onTurnEnded(TurnEndedEvent event) {
		super.onTurnEnded(event);
		lastTurn = event.getTurnSnapshot().getTurn();
		robots = event.getTurnSnapshot().getRobots();

		if (lastTurn == 1) {
			net.sf.robocode.test.helpers.Assert.assertNear(568.1445492, robots[0].getX());
			net.sf.robocode.test.helpers.Assert.assertNear(437.5845260, robots[1].getX());
			net.sf.robocode.test.helpers.Assert.assertNear(316.4177265, robots[2].getX());
			net.sf.robocode.test.helpers.Assert.assertNear(721.2294027, robots[3].getX());
			net.sf.robocode.test.helpers.Assert.assertNear(142.4365354, robots[4].getX());
			net.sf.robocode.test.helpers.Assert.assertNear(738.1464346, robots[5].getX());
			net.sf.robocode.test.helpers.Assert.assertNear(335.0596303, robots[6].getX());
			net.sf.robocode.test.helpers.Assert.assertNear(389.7078698, robots[7].getX());
		}

		if (lastTurn == 1365) {
			net.sf.robocode.test.helpers.Assert.assertNear(468.5942997, robots[0].getX());
			net.sf.robocode.test.helpers.Assert.assertNear(415.4262123, robots[1].getX());
			net.sf.robocode.test.helpers.Assert.assertNear(317.3362130, robots[2].getX());
			net.sf.robocode.test.helpers.Assert.assertNear(671.1937618, robots[3].getX());
			net.sf.robocode.test.helpers.Assert.assertNear(365.6640401, robots[4].getX());
			net.sf.robocode.test.helpers.Assert.assertNear(734.4305396, robots[5].getX());
			net.sf.robocode.test.helpers.Assert.assertNear(368.6778268, robots[6].getX());
			net.sf.robocode.test.helpers.Assert.assertNear(112.8133694, robots[7].getX());
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

		Assert.assertThat(results[0].getTeamLeaderName(), is("tested.robots.TestTeam (2)"));
		Assert.assertThat(results[1].getTeamLeaderName(), is("tested.robots.TestTeam (1)"));
		Assert.assertThat(results[2].getTeamLeaderName(), is("sample.Fire (1)"));
		Assert.assertThat(results[3].getTeamLeaderName(), is("sample.Crazy"));

		Assert.assertThat(results[0].getLastSurvivorBonus(), is(100));
		Assert.assertThat(results[1].getLastSurvivorBonus(), is(0));
		Assert.assertThat(results[2].getLastSurvivorBonus(), is(0));
		Assert.assertThat(results[3].getLastSurvivorBonus(), is(0));

		Assert.assertThat(results[0].getRamDamage(), is(0));
		Assert.assertThat(results[1].getRamDamage(), is(2));
		Assert.assertThat(results[2].getRamDamage(), is(2));
		Assert.assertThat(results[3].getRamDamage(), is(10));

		Assert.assertThat(results[0].getBulletDamageBonus(), is(93));
		Assert.assertThat(results[1].getBulletDamageBonus(), is(18));
		Assert.assertThat(results[2].getBulletDamageBonus(), is(0));
		Assert.assertThat(results[3].getBulletDamageBonus(), is(0));

		Assert.assertThat(results[0].getBulletDamage(), is(513));
		Assert.assertThat(results[1].getBulletDamage(), is(280));
		Assert.assertThat(results[2].getBulletDamage(), is(89));
		Assert.assertThat(results[3].getBulletDamage(), is(48));

		Assert.assertThat(results[0].getScore(), is(1206));
		Assert.assertThat(results[1].getScore(), is(650));
		Assert.assertThat(results[2].getScore(), is(242));
		Assert.assertThat(results[3].getScore(), is(158));

		Assert.assertThat(lastTurn, is(893));
	}
}
