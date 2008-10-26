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
import org.junit.Test;
import org.junit.After;
import org.junit.Assert;
import static org.hamcrest.CoreMatchers.is;
import robocode.battle.events.TurnEndedEvent;
import robocode.battle.events.BattleStartedEvent;
import robocode.battle.events.BattleCompletedEvent;
import robocode.battle.snapshot.RobotSnapshot;
import robocode.BattleResults;

import java.util.List;


/**
 * @author Pavel Savara (original)
 */
public class TestDuplicates extends RobotTestBed {
	List<RobotSnapshot> robots;
	private BattleResults[] results;

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
		return "sample.Fire,sample.Crazy,sample.Crazy,sample.Fire,";
	}

	@Override
	public void onBattleStarted(BattleStartedEvent event) {
		super.onBattleStarted(event);
		robots = event.getTurnSnapshot().getRobots();
	}

	@Override
	public void onBattleCompleted(BattleCompletedEvent event) {
		results = event.getResults();
	}

	@After
	public void tearDownDuplicates() {
		Assert.assertThat(robots.get(0).getName(), is("sample.Fire (1)"));
		Assert.assertThat(robots.get(1).getName(), is("sample.Crazy (1)"));
		Assert.assertThat(robots.get(2).getName(), is("sample.Crazy (2)"));
		Assert.assertThat(robots.get(3).getName(), is("sample.Fire (2)"));
	}
}
