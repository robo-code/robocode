/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package sample;


import static org.junit.Assert.assertTrue;
import robocode.control.events.BattleCompletedEvent;
import robocode.control.events.TurnEndedEvent;
import robocode.control.snapshot.IRobotSnapshot;
import robocode.control.testing.RobotTestBed;


/**
 * Tests that sample.Walls moves to all four corners.
 *
 * @author Philip Johnson (original)
 * @author Pavel Savara (contributor)
 */
public class TestWallBehavior extends RobotTestBed {

	/**
	 * True if the robot visited this corner during the test case.
	 */
	boolean visitedUpperLeft = false;

	/**
	 * True if the robot visited this corner during the test case.
	 */
	boolean visitedUpperRight = false;

	/**
	 * True if the robot visited this corner during the test case.
	 */
	boolean visitedLowerLeft = false;

	/**
	 * True if the robot visited this corner during the test case.
	 */
	boolean visitedLowerRight = false;

	/**
	 * Specifies that SittingDuck and DaCruzer are to be matched up in this test case.
	 *
	 * @return The comma-delimited list of robots in this match.
	 */
	@Override
	public String getRobotNames() {
		return "sample.SittingDuck,sample.Walls";
	}

	/**
	 * This test runs for 1 round.
	 *
	 * @return The number of rounds.
	 */
	@Override
	public int getNumRounds() {
		return 1;
	}

	/**
	 * After each turn, check to see if we're at a corner.  If so, set the corresponding flag.
	 *
	 * @param event Info about the current state of the battle.
	 */
	@Override
	public void onTurnEnded(TurnEndedEvent event) {
		IRobotSnapshot robot = event.getTurnSnapshot().getRobots()[1];
		double xPos = robot.getX();
		double yPos = robot.getY();

		if ((xPos < 40) && (yPos < 40)) {
			visitedUpperLeft = true;
		}
		if ((xPos < 40 && (yPos > (height - 40)))) {
			visitedLowerLeft = true;
		}
		if ((xPos > (width - 40)) && (yPos < 40)) {
			visitedUpperRight = true;
		}
		if ((xPos > (width - 40) && (yPos > (height - 40)))) {
			visitedLowerRight = true;
		}
	}

	/**
	 * After the battle, check to see that we've visited the corners.
	 *
	 * @param event Details about the completed battle.
	 */
	@Override
	public void onBattleCompleted(BattleCompletedEvent
			event) {
		assertTrue("Check UpperLeft", visitedUpperLeft);
		assertTrue("Check LowerLeft", visitedLowerLeft);
		assertTrue("Check UpperRight", visitedUpperRight);
		assertTrue("Check LowerRight", visitedLowerRight);
	}
}
