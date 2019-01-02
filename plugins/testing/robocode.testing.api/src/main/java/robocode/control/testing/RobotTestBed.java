/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package robocode.control.testing;


import robocode.control.BattleSpecification;
import robocode.control.BattlefieldSpecification;
import robocode.control.IRobocodeEngine;
import robocode.control.RandomFactory;
import robocode.control.RobocodeEngine;
import robocode.control.RobotSpecification;
import robocode.control.events.BattleAdaptor;
import robocode.control.events.BattleErrorEvent;
import robocode.control.events.BattleMessageEvent;
import robocode.control.events.TurnEndedEvent;
import net.sf.robocode.io.Logger;
import static org.hamcrest.CoreMatchers.is;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import robocode.control.snapshot.IRobotSnapshot;

import java.io.File;


/**
 * RobotTestBed provides a superclass that can be extended in order to implement JUnit tests
 * for Robocode robots.
 * <p>
 * The user must set the system property robocode.home to the location of the robocode installation,
 * otherwise we cannot set up the Robocode engine.  If robocode.home is not a system property,
 * we throw a RuntimeException.
 *
 * @author Philip Johnson (original)
 * @author Pavel Savara (contributor)
 */
public abstract class RobotTestBed extends BattleAdaptor {

	/**
	 * The Robocode game engine instance used for this test.
	 */
	protected final IRobocodeEngine engine;

	/**
	 * The battlefield specification, which is the default.
	 */
	protected final BattlefieldSpecification battleFieldSpec = new BattlefieldSpecification();

	/**
	 * The number of errors generated during this battle so far.
	 */
	protected int errors = 0;

	/**
	 * The number of messages generated during this battle so far.
	 */
	protected int messages = 0;

	/**
	 * The height of the battle field, initialized at the beginning of the battle.
	 */
	protected int height = 0;

	/**
	 * The width of the battle field, initialized at the beginning of the battle.
	 */
	protected int width = 0;

	/**
	 * True to specify that the position during each turn should be printed out.
	 */
	protected boolean isDumpingPositions = false;

	/**
	 * True to specify that each turn should be printed out.
	 */
	protected boolean isDumpingTurns = false;

	/**
	 * True to specify that Robot output should be printed out.
	 */
	protected boolean isDumpingOutput = false;

	/**
	 * True to specify that error messages should be printed out.
	 */
	protected boolean isDumpingErrors = true;

	/**
	 * True to specify that Robot messages should be printed out.
	 */
	protected boolean isDumpingMessages = true;

	/**
	 * Create an instance of RobotTestBed which provides default implementations for some (not all)
	 * of the IBattleListener methods.   Your subclass can provide overridden methods for more
	 * of the IBattleListener methods, or override any of the methods in this class, in order to
	 * check for the desired behavior by your robot.
	 * <p>
	 * Also instantiates a Robocode engine for running the test battle. This requires a system
	 * property called robocode.home to be defined and to provide the path to a Robocode installation
	 * containing the robots under test.
	 *
	 * @throws RuntimeException If robocode.home is not defined or does not point to a robocode
	 *                          installation.
	 */
	public RobotTestBed() {
		// Set some system properties for use by the robocode engine.
		System.setProperty("EXPERIMENTAL", "true");
		System.setProperty("TESTING", "true");

		// Check that robocode.home is defined and points to a robocode installation.
		String robocodeHome = System.getProperty("robocode.home");

		if (robocodeHome == null) {
			throw new RuntimeException("System property robocode.home is not set.");
		}

		File robocodeJar = new File(new File(robocodeHome), "libs/robocode.jar");

		if (!robocodeJar.exists()) {
			throw new RuntimeException("robocode.jar not found. robocode.home: " + robocodeHome);
		}

		// Now create the robocode engine.
		engine = new RobocodeEngine(new File(robocodeHome));
	}

	/**
	 * Called after each turn, and implements basic logging information about the turn number and
	 * the position of each robot.
	 * <p>
	 * Override this method to perform testing at the end of each turn.
	 *
	 * @param event The TurnEndedEvent.
	 */
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

	/**
	 * Must return a comma-separated list of fully qualified robot names to be in this battle.
	 * <p>
	 * You must override this event to specify the robots to battle in this test case.
	 *
	 * @return The list of robots.
	 */
	public abstract String getRobotNames();

	/**
	 * Provides the number of rounds in this battle.  Defaults to 1.
	 * Override this to change the number of rounds.
	 *
	 * @return The number of rounds.
	 */
	public int getNumRounds() {
		return 1;
	}

	/**
	 * Returns a comma or space separated list like: x1,y1,heading1, x2,y2,heading2, which are the
	 * coordinates and heading of robot #1 and #2. So "0,0,180, 50,80,270" means that robot #1
	 * has position (0,0) and heading 180, and robot #2 has position (50,80) and heading 270.
	 * <p>
	 * Override this method to explicitly specify the initial positions.
	 * <p>
	 * Defaults to null, which means that the initial positions are determined randomly.  Since
	 * battles are deterministic by default, the initial positions are randomly chosen but will
	 * always be the same each time you run the test case.
	 *
	 * @return The list of initial positions.
	 */
	public String getInitialPositions() {
		return null;
	}

	/**
	 * Provides the number of robots in this battle.
	 *
	 * @param robotList The list of robots.
	 * @return The number of robots in this battle.
	 */
	private int getExpectedRobotCount(String robotList) {
		return robotList.split("[\\s,;]+").length;
	}

	/**
	 * Defaults to true, indicating that the battle is deterministic and robots will always start
	 * in the same position each time.
	 * <p>
	 * Override to support random initialization.
	 *
	 * @return True if the battle will be deterministic.
	 */
	public boolean isDeterministic() {
		return true;
	}

	/**
	 * The setup method run before each test, which sets up the listener on the engine for testing.
	 * Don't override this method; instead, override runSetup to add behavior before the test
	 * battle starts.
	 */
	@Before
	public void setup() {
		engine.addBattleListener(this);
		if (isDeterministic()) {
			RandomFactory.resetDeterministic(0);
		}
		this.height = battleFieldSpec.getHeight();
		this.width = battleFieldSpec.getWidth();
		errors = 0;
		messages = 0;
	}

	/**
	 * After each test is run, the listener is removed.
	 * Don't override this method; instead, override runTearDown to add behavior after the test
	 * battle ends.
	 */
	@After
	public void tearDown() {
		engine.removeBattleListener(this);
	}

	/**
	 * Runs a test, invoking the runSetup method before the battle starts and the
	 * runTearDown method after the battle ends.
	 * Asserts that the expected number of errors for this battle were obtained.
	 */
	@Test
	public void run() {
		runSetup();
		runBattle(getRobotNames(), getNumRounds(), getInitialPositions());
		runTeardown();
		RobotTestBedAssert.assertThat(errors, is(getExpectedErrors()));
	}

	/**
	 * Specifies how many errors you expect this battle to generate.
	 * Defaults to 0. Override this method to change the number of expected errors.
	 *
	 * @return The expected number of errors.
	 */
	protected int getExpectedErrors() {
		return 0;
	}

	/**
	 * Invoked before the test battle begins.
	 * Default behavior is to do nothing.
	 * Override this method in your test case to add behavior before the battle starts.
	 */
	protected void runSetup() {// Default does nothing.
	}

	/**
	 * Invoked after the test battle ends.
	 * Default behavior is to do nothing.
	 * Override this method in your test case to add behavior after the battle ends.
	 */
	protected void runTeardown() {// Default does nothing.
	}

	/**
	 * Runs the test battle.
	 *
	 * @param robotList        The list of robots.
	 * @param numRounds        The number of rounds.
	 * @param initialPositions The initial positions for the robots.
	 */
	private void runBattle(String robotList, int numRounds, String initialPositions) {
		final RobotSpecification[] robotSpecifications = engine.getLocalRepository(robotList);

		if (getExpectedRobotCount(robotList) > 0) {
			RobotTestBedAssert.assertNotNull("Robot not loaded", robotSpecifications);
			RobotTestBedAssert.assertEquals("Robot not loaded", getExpectedRobotCount(robotList),
					robotSpecifications.length);
			engine.runBattle(new BattleSpecification(numRounds, battleFieldSpec, robotSpecifications), initialPositions,
					true);
		}
	}

	/**
	 * Keep track of the number of messages. Print them if desired.
	 *
	 * @param event The BattleMessageEvent.
	 */
	public void onBattleMessage(BattleMessageEvent event) {
		if (isDumpingMessages) {
			Logger.realOut.println(event.getMessage());
		}
		messages++;
	}

	/**
	 * Keep track of the number of errors.  Print them if desired.
	 *
	 * @param event The BattleErrorEvent.
	 */
	public void onBattleError(BattleErrorEvent event) {
		if (isDumpingErrors) {
			Logger.realErr.println(event.getError());
		}
		errors++;
	}
}
