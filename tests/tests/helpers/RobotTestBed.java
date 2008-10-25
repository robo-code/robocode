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

package helpers;


import robocode.battle.events.*;
import robocode.battle.snapshot.RobotSnapshot;
import robocode.control.*;
import robocode.security.SecurePrintStream;
import robocode.io.FileUtil;
import org.junit.Before;
import org.junit.Assert;
import org.junit.After;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.is;

import java.util.Random;


/**
 * @author Pavel Savara (original)
 */
public abstract class RobotTestBed extends BattleAdaptor {
	protected RobocodeEngine2 engine;
	protected BattlefieldSpecification battleFieldSpec = new BattlefieldSpecification();
	protected int errors = 0;
	protected int messages = 0;

	public RobotTestBed() {
		System.setProperty("EXPERIMENTAL", "true");
		engine = new RobocodeEngine2(FileUtil.getCwd());
		engine.addBattleListener(this);
	}

	public void onBattleMessage(BattleMessageEvent event) {
		SecurePrintStream.realOut.println(event.getMessage());
		messages++;
	}

	public void onBattleError(BattleErrorEvent event) {
		SecurePrintStream.realErr.println(event.getError());
		errors++;
	}

	public boolean isDumpingPositions() {
		return false;
	}

	public boolean isDumpingTurns() {
		return false;
	}

	public void onTurnEnded(TurnEndedEvent event) {
		if (isDumpingTurns()) {
			SecurePrintStream.realOut.println("turn " + event.getTurnSnapshot().getTurn());
		}
		for (RobotSnapshot robot : event.getTurnSnapshot().getRobots()) {
			if (isDumpingPositions()) {
				SecurePrintStream.realOut.print(robot.getVeryShortName());
				SecurePrintStream.realOut.print(" X:");
				SecurePrintStream.realOut.print(robot.getX());
				SecurePrintStream.realOut.print(" Y:");
				SecurePrintStream.realOut.print(robot.getY());
				SecurePrintStream.realOut.print(" V:");
				SecurePrintStream.realOut.print(robot.getVelocity());
				SecurePrintStream.realOut.println();
			}
			SecurePrintStream.realOut.print(robot.getOutputStreamSnapshot());
		}
	}

	public void onBattleStarted(BattleStartedEvent event) {
		if (isDeterministic() && event.getTurnSnapshot().getRobots().size() == 2) {
			final Random random = RandomFactory.getRandom();

			helpers.Assert.assertNear(0.9848415, random.nextDouble());
		}
	}

	public abstract String getRobotNames();

	public int getNumRounds() {
		return 1;
	}

	public int getExpectedRobotCount(String list) {
		return list.split("[\\s,;]+").length;
	}

	public boolean isDeterministic() {
		return true;
	}

	@Before
	public void setup() {
		if (isDeterministic()) {
			RandomFactory.resetDeterministic(0);
			helpers.Assert.assertNear(0.730967, RandomFactory.getRandom().nextDouble());
		}
		errors = 0;
		messages = 0;
	}

	@After
	public void tearDown() {
		Assert.assertThat(errors, is(0));
	}

	@Test
	public void run() {
		final String list = getRobotNames();
		final RobotSpecification[] robotSpecifications = engine.getLocalRepository(list);

		Assert.assertEquals(getExpectedRobotCount(list), robotSpecifications.length);
		engine.runBattle(new BattleSpecification(getNumRounds(), battleFieldSpec, robotSpecifications), true);
	}
}
