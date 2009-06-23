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
package net.sf.robocode.test.samples;


import robocode.BattleResults;
import robocode.control.*;
import robocode.control.events.*;

import java.io.File;


/**
 * Sample application that runs two sample robots in Robocode.
 *
 * @author Flemming N. Larsen
 */
public class RobocodeRunner {

	public static void main(String[] args) {

		// Battle listener used for receiving battle events
		BattleObserver battleListener = new BattleObserver();

		// Create the RobocodeEngine
		// RobocodeEngine engine = new RobocodeEngine(); // Run from current working directory
		IRobocodeEngine engine = new RobocodeEngine(new File("C:/Robocode")); // Run from C:/Robocode

		// Add battle listener to our RobocodeEngine
		engine.addBattleListener(battleListener);

		// Show the battles
		engine.setVisible(true);

		// Setup the battle specification

		int numberOfRounds = 5;
		BattlefieldSpecification battlefield = new BattlefieldSpecification(800, 600); // 800x600
		RobotSpecification[] selectedRobots = engine.getLocalRepository("sample.RamFire, sample.Corners");

		BattleSpecification battleSpec = new BattleSpecification(numberOfRounds, battlefield, selectedRobots);

		// Run our specified battle and let it run till it's over
		engine.runBattle(battleSpec, true/* wait till the battle is over */);

		// Cleanup our RobocodeEngine
		engine.close();

		// Make sure that the Java VM is shut down properly
		System.exit(0);
	}

	/**
	 * BattleListener for handling the battle event we are interested in.
	 */
	static class BattleObserver extends BattleAdaptor {

		public void onBattleStarted(BattleStartedEvent e) {
			System.out.println("-- Battle was started --");
		}

		public void onBattleFinished(BattleFinishedEvent e) {
			if (e.isAborted()) {
				System.out.println("-- Battle was aborted --");
			} else {
				System.out.println("-- Battle was finished succesfully --");
			}
		}

		public void onBattleCompleted(BattleCompletedEvent e) {
			System.out.println("-- Battle has completed --");

			// Print out the sorted results with the robot names
			System.out.println("\n-- Battle results --");
			for (BattleResults result : e.getSortedResults()) {
				System.out.println("  " + result.getTeamName() + ": " + result.getCombinedScore());
			}
		}

		public void onBattleMessage(BattleMessageEvent e) {
			System.out.println("Msg> " + e.getMessage());
		}

		public void onBattleError(BattleErrorEvent e) {
			System.out.println("Err> " + e.getError());
		}
	}
}
