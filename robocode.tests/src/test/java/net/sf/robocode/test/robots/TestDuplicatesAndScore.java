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
 *******************************************************************************/
package net.sf.robocode.test.robots;


import net.sf.robocode.test.helpers.Assert;
import net.sf.robocode.test.helpers.RobocodeTestBed;
import static org.hamcrest.CoreMatchers.is;
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

			Assert.assertNear(121.1808112, robots[0].getX());
			Assert.assertNear(46.86691852, robots[1].getX());
			Assert.assertNear(667.6700705, robots[2].getX());
			Assert.assertNear(164.1111119, robots[3].getX());
			Assert.assertNear(549.3672140, robots[4].getX());
			Assert.assertNear(45.32324235, robots[5].getX());
			Assert.assertNear(445.8273626, robots[6].getX());
			Assert.assertNear(482.2900391, robots[7].getX());

			Assert.assertNear(100.000, robots[0].getEnergy());
			Assert.assertNear(200.000, robots[1].getEnergy());
			Assert.assertNear(120.000, robots[2].getEnergy());
			Assert.assertNear(100.000, robots[3].getEnergy());
			Assert.assertNear(200.000, robots[4].getEnergy());
			Assert.assertNear(120.000, robots[5].getEnergy());
			Assert.assertNear(100.000, robots[6].getEnergy());
			Assert.assertNear(100.000, robots[7].getEnergy());
		}
	}

	@Override
	public void onTurnEnded(TurnEndedEvent event) {
		super.onTurnEnded(event);
		lastTurn = event.getTurnSnapshot().getTurn();
		robots = event.getTurnSnapshot().getRobots();

		if (lastTurn == 1) {
			Assert.assertNear(121.1808112, robots[0].getX());
			Assert.assertNear(45.94128271, robots[1].getX());
			Assert.assertNear(667.6700705, robots[2].getX());
			Assert.assertNear(164.1111119, robots[3].getX());
			Assert.assertNear(549.4251430, robots[4].getX());
			Assert.assertNear(45.32324235, robots[5].getX());
			Assert.assertNear(445.8273626, robots[6].getX());
			Assert.assertNear(482.6041885, robots[7].getX());
		}

		if (lastTurn == 1365) {
			Assert.assertNear(93.78681970, robots[0].getX());
			Assert.assertNear(83.24536817, robots[1].getX());
			Assert.assertNear(667.6700705, robots[2].getX());
			Assert.assertNear(203.2946956, robots[3].getX());
			Assert.assertNear(567.9242868, robots[4].getX());
			Assert.assertNear(45.32324235, robots[5].getX());
			Assert.assertNear(478.8533871, robots[6].getX());
			Assert.assertNear(123.9697256, robots[7].getX());
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
		Assert.assertThat(results[2].getTeamLeaderName(), is("sample.Crazy"));
		Assert.assertThat(results[3].getTeamLeaderName(), is("sample.Fire (1)"));

		Assert.assertThat(results[0].getLastSurvivorBonus(), is(50));
		Assert.assertThat(results[1].getLastSurvivorBonus(), is(0));
		Assert.assertThat(results[2].getLastSurvivorBonus(), is(0));
		Assert.assertThat(results[3].getLastSurvivorBonus(), is(0));

		Assert.assertThat(results[0].getRamDamage(), is(2));
		Assert.assertThat(results[1].getRamDamage(), is(0));
		Assert.assertThat(results[2].getRamDamage(), is(5));
		Assert.assertThat(results[3].getRamDamage(), is(0));

		Assert.assertThat(results[0].getBulletDamageBonus(), is(53));
		Assert.assertThat(results[1].getBulletDamageBonus(), is(43));
		Assert.assertThat(results[2].getBulletDamageBonus(), is(0));
		Assert.assertThat(results[3].getBulletDamageBonus(), is(0));

		Assert.assertThat(results[0].getBulletDamage(), is(457));
		Assert.assertThat(results[1].getBulletDamage(), is(381));
		Assert.assertThat(results[2].getBulletDamage(), is(54));
		Assert.assertThat(results[3].getBulletDamage(), is(76));

		Assert.assertThat(results[0].getScore(), is(1163));
		Assert.assertThat(results[1].getScore(), is(774));
		Assert.assertThat(results[2].getScore(), is(158));
		Assert.assertThat(results[3].getScore(), is(126));

		Assert.assertThat(lastTurn, is(2145));
	}
}
