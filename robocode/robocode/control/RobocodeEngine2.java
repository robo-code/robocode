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
 *     Flemming N. Larsen
 *     - Javadocs
 *******************************************************************************/
package robocode.control;


import robocode.battle.events.IBattleListener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * The RobocodeEngine2 is the new version and extension of the old
 * {@link RobocodeEngine}. Hence, it is the new interface provided for external
 * applications in order to let these applications run battles within the
 * Robocode application, and to get the results from these battles.
 *
 * @author Pavel Savara (original)
 * @see RobocodeEngine
 * @since 1.6.1
 */
public class RobocodeEngine2 extends RobocodeEngine {

	/**
	 * Creates a new RobocodeEngine for controlling Robocode.
	 *
	 * @param robocodeHome the root directory of Robocode, e.g. C:\Robocode.
	 * @see RobocodeEngine#RobocodeEngine(RobocodeListener)
	 * @see RobocodeEngine#RobocodeEngine(File, RobocodeListener)
	 * @see #close()
	 */
	public RobocodeEngine2(File robocodeHome) {
		super(robocodeHome, null);
	}

	/**
	 * Adds a battle listener that must receive events occurring in battles.
	 *
	 * @param listener the battle listener that must retrieve the event from
	 *                 the battles.
	 * @see #addBattleListener(IBattleListener)
	 */
	public void addBattleListener(IBattleListener listener) {
		manager.getBattleManager().addListener(listener);
	}

	/**
	 * Removes a battle listener that has previously been added to this object.
	 *
	 * @param listener the battle listener that must be removed.
	 * @see #removeBattleListener(IBattleListener)
	 */
	public void removeBattleListener(IBattleListener listener) {
		manager.getBattleManager().removeListener(listener);
	}

	/**
	 * Runs the specified battle.
	 *
	 * @param battle	   the specification of the battle to run including the
	 *                     participating robots.
	 * @param waitTillOver will block caller till end of battle if set
	 * @see robocode.battle.events.IBattleListener
	 * @see RobocodeListener#battleComplete(BattleSpecification, RobotResults[])
	 * @see RobocodeListener#battleMessage(String)
	 * @see BattleSpecification
	 * @see #getLocalRepository()
	 */
	public void runBattle(BattleSpecification battle, boolean waitTillOver) {
		manager.getBattleManager().startNewBattle(battle, waitTillOver);
	}

	/**
	 * Returns a selection of robots available from the local robot repository
	 * of Robocode. These robots must exists in the /robocode/robots directory,
	 * and must be compiled in advance.
	 * </p>
	 * Notice: If a specified robot cannot be found in the repository, it will
	 * not be returned in the array of robots returned by this method.
	 *
	 * @param selectedRobotList a comma or space separated list of robots to
	 *                          return. The full class name must be used for
	 *                          specifying the individual robot, e.g.
	 *                          "sample.Corners, sample.Crazy"
	 * @return an array containing the available robots from the local robot
	 *         repository based on the selected robots specified with the
	 *         {@code selectedRobotList} parameter.
	 * @see RobotSpecification
	 * @see RobocodeEngine#getLocalRepository()
	 */
	public RobotSpecification[] getLocalRepository(String selectedRobotList) {
		RobotSpecification[] repository = getLocalRepository();

		HashMap<String, RobotSpecification> robotSpecMap = new HashMap<String, RobotSpecification>();

		for (RobotSpecification spec : repository) {
			robotSpecMap.put(spec.getNameAndVersion(), spec);
		}

		String[] selectedRobots = selectedRobotList.split("[\\s,;]+");

		List<RobotSpecification> selectedRobotSpecs = new ArrayList<RobotSpecification>();

		RobotSpecification spec;

		for (String robot : selectedRobots) {
			spec = robotSpecMap.get(robot);
			if (spec != null) {
				selectedRobotSpecs.add(spec);
			}
		}
		return selectedRobotSpecs.toArray(new RobotSpecification[selectedRobotSpecs.size()]);
	}

	/**
	 * Prints out all running threads to the standard system out (console).
	 */
	public static void printRunningThreads() {
		ThreadGroup currentGroup = Thread.currentThread().getThreadGroup();

		if (currentGroup == null) {
			return;
		}

		while (currentGroup.getParent() != null) {
			currentGroup = currentGroup.getParent();
		}

		ThreadGroup groups[] = new ThreadGroup[256];
		Thread threads[] = new Thread[256];

		int numGroups = currentGroup.enumerate(groups, true);

		for (int i = 0; i < numGroups; i++) {
			currentGroup = groups[i];
			if (currentGroup.isDaemon()) {
				System.out.print("  ");
			} else {
				System.out.print("* ");
			}
			System.out.println("In group: " + currentGroup.getName());
			int numThreads = currentGroup.enumerate(threads);

			for (int j = 0; j < numThreads; j++) {
				if (threads[j].isDaemon()) {
					System.out.print("  ");
				} else {
					System.out.print("* ");
				}
				System.out.println(threads[j].getName());
			}
			System.out.println("---------------");
		}
	}
}
