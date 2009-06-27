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

package net.sf.robocode.test.helpers;


import net.sf.robocode.io.Logger;
import static org.hamcrest.CoreMatchers.is;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import robocode.control.*;
import robocode.control.events.*;
import robocode.control.snapshot.IRobotSnapshot;
import robocode.util.Utils;

import java.io.File;
import java.io.IOException;
import java.util.Random;


/**
 * @author Pavel Savara (original)
 */
public abstract class RobotTestBed extends BattleAdaptor {
	protected static final IRobocodeEngine engine;
	protected final BattlefieldSpecification battleFieldSpec = new BattlefieldSpecification();
	protected static int errors = 0;
	protected static int messages = 0;
	protected static String absolutePath;
	
	public static boolean isDumpingPositions = false;
	public static boolean isDumpingTurns = false;
	public static boolean isDumpingOutput = true;
	public static boolean isDumpingErrors = true;
	public static boolean isDumpingMessages = true;

	static {
		System.setProperty("EXPERIMENTAL", "true");
		System.setProperty("TESTING", "true");
		System.setProperty("WORKINGDIRECTORY", "target//test-classes");
		System.setProperty("ROBOTPATH", "target//classes");
		engine = new RobocodeEngine(new BattleAdaptor() {
			public void onBattleMessage(BattleMessageEvent event) {
				if (isDumpingMessages) {
					Logger.realOut.println(event.getMessage());
				}
				messages++;
			}

			public void onBattleError(BattleErrorEvent event) {
				if (isDumpingErrors) {
					Logger.realErr.println(event.getError());
				}
				errors++;
			}
		});
		try {
			absolutePath = new File("").getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace(Logger.realErr);
		}
	}

	public RobotTestBed() {
		// silent when running in maven
		if (System.getProperty("surefire.test.class.path", null) != null) {
			isDumpingOutput = false;
			isDumpingErrors = false;
			isDumpingMessages = false;
		}
		errors = 0;
		messages = 0;
	}

	public void onTurnEnded(TurnEndedEvent event) {
		if (isDumpingTurns) {
			Logger.realOut.println("turn " + event.getTurnSnapshot().getTurn());
		}
		for (IRobotSnapshot robot : event.getTurnSnapshot().getRobots()) {
			if (isDumpingPositions) {
				Logger.realOut.print(robot.getVeryShortName());
				Logger.realOut.print(" X:");
				Logger.realOut.print(robot.getX());
				Logger.realOut.print(" Y:");
				Logger.realOut.print(robot.getY());
				Logger.realOut.print(" V:");
				Logger.realOut.print(robot.getVelocity());
				Logger.realOut.println();
			}
			if (isDumpingOutput) {
				Logger.realOut.print(robot.getOutputStreamSnapshot());
			}
		}
	}

	public void onBattleStarted(BattleStartedEvent event) {
		if (isDeterministic() && isCheckOnBattleStart()) {
			final Random random = Utils.getRandom();

			Assert.assertNear(0.98484154, random.nextDouble());
		}
	}

	public abstract String getRobotNames();

	public int getNumRounds() {
		return 1;
	}

	public String getInitialPositions() {
		return null;
	}

	public int getExpectedRobotCount(String robotList) {
		return robotList.split("[\\s,;]+").length;
	}

	public boolean isDeterministic() {
		return true;
	}

	public boolean isCheckOnBattleStart() {
		return false;
	}

	@Before
	public void setup() {
		engine.addBattleListener(this);
		if (isDeterministic()) {
			RandomFactory.resetDeterministic(0);
			if (isCheckOnBattleStart()) {
				Assert.assertNear(0.730967, RandomFactory.getRandom().nextDouble());
			}
		}
		errors = 0;
		messages = 0;
	}

	@After
	public void tearDown() {
		engine.removeBattleListener(this);
	}

	@Test
	public void run() {
		runSetup();
		runBattle(getRobotNames(), getNumRounds(), getInitialPositions());
		runTeardown();
		Assert.assertThat(errors, is(getExpectedErrors()));
	}

	protected int getExpectedErrors() {
		return 0;
	}

	protected void runSetup() {}

	protected void runTeardown() {}

	protected void runBattle(String robotList, int numRounds, String initialPositions) {
		final RobotSpecification[] robotSpecifications = engine.getLocalRepository(robotList);

		if (getExpectedRobotCount(robotList) > 0) {
			Assert.assertNotNull("Robot were not loaded", robotSpecifications);
			Assert.assertEquals("Robot were not loaded", getExpectedRobotCount(robotList), robotSpecifications.length);
			engine.runBattle(new BattleSpecification(numRounds, battleFieldSpec, robotSpecifications), initialPositions,
					true);
		}
	}
}
