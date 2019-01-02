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
import org.junit.Ignore;
import robocode.BattleResults;
import robocode.control.events.BattleCompletedEvent;
import robocode.control.events.BattleFinishedEvent;
import robocode.control.events.RoundStartedEvent;
import robocode.control.events.TurnEndedEvent;
import robocode.control.snapshot.IRobotSnapshot;


/**
 * @author Pavel Savara (original)
 */
@Ignore(".NET is little bit slow, so it gives skipped turns for battles with many robots sometimes")
public class TestDuplicatesAndScore extends RobocodeTestBed {
	private IRobotSnapshot[] robots;
	private BattleResults[] results;

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
		return "SampleCs.Fire,tested.robotscs.TestTeam,tested.robotscs.TestTeam,SampleCs.Crazy";
	}

	@Override
	public void onRoundStarted(final RoundStartedEvent event) {
		super.onRoundStarted(event);
		if (event.getRound() == 0) {
			robots = event.getStartSnapshot().getRobots();
			Assert.assertEquals(8, robots.length);

			net.sf.robocode.test.helpers.Assert.assertNear(566.2968069, robots[0].getX());
			net.sf.robocode.test.helpers.Assert.assertNear(436.3146436, robots[1].getX());
			net.sf.robocode.test.helpers.Assert.assertNear(317.3362130, robots[2].getX());
			net.sf.robocode.test.helpers.Assert.assertNear(717.6994092, robots[3].getX());
			net.sf.robocode.test.helpers.Assert.assertNear(145.5531935, robots[4].getX());
			net.sf.robocode.test.helpers.Assert.assertNear(734.4305396, robots[5].getX());
			net.sf.robocode.test.helpers.Assert.assertNear(335.7732607, robots[6].getX());
			net.sf.robocode.test.helpers.Assert.assertNear(390.8076498, robots[7].getX());

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
		int lastTurn = event.getTurnSnapshot().getTurn();

		robots = event.getTurnSnapshot().getRobots();

		if (lastTurn == 1) {
			net.sf.robocode.test.helpers.Assert.assertNear(566.2968069, robots[0].getX());
			net.sf.robocode.test.helpers.Assert.assertNear(437.1810299, robots[1].getX());
			net.sf.robocode.test.helpers.Assert.assertNear(317.3362130, robots[2].getX());
			net.sf.robocode.test.helpers.Assert.assertNear(717.6994092, robots[3].getX());
			net.sf.robocode.test.helpers.Assert.assertNear(145.2637220, robots[4].getX());
			net.sf.robocode.test.helpers.Assert.assertNear(734.4305396, robots[5].getX());
			net.sf.robocode.test.helpers.Assert.assertNear(335.7732607, robots[6].getX());
			net.sf.robocode.test.helpers.Assert.assertNear(389.8100070, robots[7].getX());
		}

		if (lastTurn == 1365) {         
			net.sf.robocode.test.helpers.Assert.assertNear(563.1336824, robots[0].getX());
			net.sf.robocode.test.helpers.Assert.assertNear(572.9500601, robots[1].getX());
			net.sf.robocode.test.helpers.Assert.assertNear(317.3362130, robots[2].getX());
			net.sf.robocode.test.helpers.Assert.assertNear(578.2231942, robots[3].getX());
			net.sf.robocode.test.helpers.Assert.assertNear(120.3355009, robots[4].getX());
			net.sf.robocode.test.helpers.Assert.assertNear(734.4305396, robots[5].getX());
			net.sf.robocode.test.helpers.Assert.assertNear(244.9583274, robots[6].getX());
			net.sf.robocode.test.helpers.Assert.assertNear(333.1785378, robots[7].getX());
		}

		Assert.assertFalse("skipped", robots[0].getOutputStreamSnapshot().contains("skipped"));
		Assert.assertFalse("skipped", robots[1].getOutputStreamSnapshot().contains("skipped"));
		Assert.assertFalse("skipped", robots[2].getOutputStreamSnapshot().contains("skipped"));
		Assert.assertFalse("skipped", robots[3].getOutputStreamSnapshot().contains("skipped"));
		Assert.assertFalse("skipped", robots[4].getOutputStreamSnapshot().contains("skipped"));
		Assert.assertFalse("skipped", robots[5].getOutputStreamSnapshot().contains("skipped"));
		Assert.assertFalse("skipped", robots[6].getOutputStreamSnapshot().contains("skipped"));
		Assert.assertFalse("skipped", robots[7].getOutputStreamSnapshot().contains("skipped"));
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
		Assert.assertThat(robots[0].getName(), is("SampleCs.Fire (1)"));
		Assert.assertThat(robots[1].getName(), is("SampleCsTeam.MyFirstLeader (1)"));
		Assert.assertThat(robots[2].getName(), is("SampleCsTeam.MyFirstDroid (1)"));
		Assert.assertThat(robots[3].getName(), is("SampleCs.Fire (2)"));
		Assert.assertThat(robots[4].getName(), is("SampleCsTeam.MyFirstLeader (2)"));
		Assert.assertThat(robots[5].getName(), is("SampleCsTeam.MyFirstDroid (2)"));
		Assert.assertThat(robots[6].getName(), is("SampleCs.Fire (3)"));
		Assert.assertThat(robots[7].getName(), is("SampleCs.Crazy"));

		// the results are different from Java version because of strictfp in Java Math
		// as oposed to FPU driven .NET, which gives better precision.
		// difference acumulates to make big difference in results
		Assert.assertThat(results[0].getTeamLeaderName(), is("tested.robotscs.TestTeam (2)"));
		Assert.assertThat(results[1].getTeamLeaderName(), is("tested.robotscs.TestTeam (1)"));
		Assert.assertThat(results[2].getTeamLeaderName(), is("SampleCs.Crazy"));
		Assert.assertThat(results[3].getTeamLeaderName(), is("SampleCs.Fire (1)"));

		Assert.assertThat(results[0].getLastSurvivorBonus(), is(0));
		Assert.assertThat(results[1].getLastSurvivorBonus(), is(50));
		Assert.assertThat(results[2].getLastSurvivorBonus(), is(0));
		Assert.assertThat(results[3].getLastSurvivorBonus(), is(0));

		Assert.assertThat(results[0].getRamDamage(), is(0));
		Assert.assertThat(results[1].getRamDamage(), is(5));
		Assert.assertThat(results[2].getRamDamage(), is(14));
		Assert.assertThat(results[3].getRamDamage(), is(1));

		Assert.assertThat(results[0].getBulletDamageBonus(), is(55));
		Assert.assertThat(results[1].getBulletDamageBonus(), is(0));
		Assert.assertThat(results[2].getBulletDamageBonus(), is(0));
		Assert.assertThat(results[3].getBulletDamageBonus(), is(0));

		Assert.assertThat(results[0].getBulletDamage(), is(480));
		Assert.assertThat(results[1].getBulletDamage(), is(124));
		Assert.assertThat(results[2].getBulletDamage(), is(148));
		Assert.assertThat(results[3].getBulletDamage(), is(152));

		Assert.assertThat(results[0].getScore(), is(1135));
		Assert.assertThat(results[1].getScore(), is(434));
		Assert.assertThat(results[2].getScore(), is(312));
		Assert.assertThat(results[3].getScore(), is(253));

	}
}
