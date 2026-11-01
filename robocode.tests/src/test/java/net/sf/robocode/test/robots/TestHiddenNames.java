/*
 * Copyright (c) 2001-2026 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.test.robots;


import net.sf.robocode.test.helpers.Assert;
import net.sf.robocode.test.helpers.RobocodeTestBed;
import org.junit.jupiter.api.Test;
import robocode.control.BattleSpecification;
import robocode.control.RobotSpecification;
import robocode.control.events.TurnEndedEvent;


/**
 * Many robots compare enemy names with == or !=, which works for real names because a robot's name is one
 * object. Hidden names must behave the same, or those robots lose track of their enemies.
 *
 * @author Robert Maupin (original)
 */
public class TestHiddenNames extends RobocodeTestBed {
	private boolean sawHiddenName;

	@Test
	public void run() {
		super.run();
		Assert.assertTrue(sawHiddenName, "The robot never saw a hidden name");
	}

	@Override
	public String getRobotName() {
		return "tested.robots.HiddenNames";
	}

	@Override
	public String getEnemyName() {
		return "sample.Target";
	}

	@Override
	protected void runBattle(String robotList, int numRounds, String initialPositions) {
		RobotSpecification[] robots = engine.getLocalRepository(robotList);
		BattleSpecification battle = new BattleSpecification(battleFieldSpec, numRounds, 450, 0.1, 100, true, robots);

		engine.runBattle(battle, initialPositions, true, false);
	}

	@Override
	public void onTurnEnded(TurnEndedEvent event) {
		super.onTurnEnded(event);
		String out = event.getTurnSnapshot().getRobots()[0].getOutputStreamSnapshot();

		if (out.contains("Enemy name: #")) {
			sawHiddenName = true;
		}
		if (out.contains("sample.Target")) {
			Assert.fail("The robot saw the enemy's real name");
		}
		if (out.contains("The name is a new object")) {
			Assert.fail("The hidden name was not the same object each time");
		}
	}
}
