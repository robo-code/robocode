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
import org.junit.Test;
import robocode.control.BattleSpecification;
import robocode.control.RobotSpecification;
import robocode.control.events.TurnEndedEvent;
import robocode.control.snapshot.IRobotSnapshot;


/**
 * A robot that is hit by a bullet from a robot on the other team must not see that robot's real name. Whether a
 * name is hidden depends on the robot seeing it, not on the team of the robot that fired.
 *
 * @author Flemming N. Larsen (original)
 */
public class TestHiddenNamesInTeam extends RobocodeTestBed {
	private boolean sawHiddenName;

	@Test
	public void run() {
		super.run();
		Assert.assertTrue("The robot was never hit by a robot with a hidden name", sawHiddenName);
	}

	@Override
	public String getRobotName() {
		return "tested.robots.HiddenNamesTeam";
	}

	@Override
	public String getEnemyName() {
		return "tested.robots.TestTeam";
	}

	@Override
	public int getExpectedRobotCount(String list) {
		return 5;
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
		for (IRobotSnapshot robot : event.getTurnSnapshot().getRobots()) {
			if (robot.getName().startsWith("tested.robots.HiddenNames")) {
				String out = robot.getOutputStreamSnapshot();

				if (out.contains("Hit by: #")) {
					sawHiddenName = true;
				}
				if (out.contains("sample.Fire") || out.contains("sampleteam.")) {
					Assert.fail("The robot saw the enemy's real name: " + out);
				}
			}
		}
	}
}
