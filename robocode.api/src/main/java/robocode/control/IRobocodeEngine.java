/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package robocode.control;


import robocode.control.events.IBattleListener;


/**
 * Interface for the RobocodeEngine.
 * 
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (contributor)
 */
public interface IRobocodeEngine {

	/**
	 * Adds a battle listener that must receive events occurring in battles.
	 *
	 * @param listener the battle listener that must retrieve the event from
	 *                 the battles.
	 * @see #removeBattleListener(robocode.control.events.IBattleListener)
	 * @since 1.6.2
	 */
	void addBattleListener(IBattleListener listener);

	/**
	 * Removes a battle listener that has previously been added to this object.
	 *
	 * @param listener the battle listener that must be removed.
	 * @see #addBattleListener(robocode.control.events.IBattleListener)
	 * @since 1.6.2
	 */
	void removeBattleListener(IBattleListener listener);

	/**
	 * Closes the RobocodeEngine and releases any allocated resources it holds.
	 * You should call this when you have finished using the RobocodeEngine.
	 * This method automatically disposes the Robocode window if it open.
	 */
	void close();

	/**
	 * Returns the installed version of Robocode controlled by this RobocodeEngine.
	 *
	 * @return the installed version of Robocode controlled by this RobocodeEngine.
	 */
	String getVersion();

	/**
	 * Shows or hides the Robocode window.
	 *
	 * @param visible {@code true} if the Robocode window must be set visible;
	 *                {@code false} otherwise.
	 */
	void setVisible(boolean visible);

	/**
	 * Returns all robots available from the local robot repository of Robocode.
	 * These robots must exists in the /robocode/robots directory, and must be
	 * compiled in advance, before these robot are returned with this method.
	 *
	 * @return an array of all available robots from the local robot repository.
	 * @see robocode.control.RobotSpecification
	 * @see #getLocalRepository(String)
	 */
	RobotSpecification[] getLocalRepository();

	/**
	 * Returns a selection of robots available from the local robot repository
	 * of Robocode. These robots must exists in the /robocode/robots directory,
	 * and must be compiled in advance, before these robot are returned with this method.
	 * <p>
	 * Notice: If a specified robot cannot be found in the repository, it will
	 * not be returned in the array of robots returned by this method.
	 *
	 * @param selectedRobotList a comma or space separated list of robots to
	 *                          return. The full class name must be used for
	 *                          specifying the individual robot, e.g.
	 *                          "sample.Corners, sample.Crazy".
	 * @return an array containing the available robots from the local robot
	 *         repository based on the selected robots specified with the
	 *         {@code selectedRobotList} parameter.
	 * @see robocode.control.RobotSpecification
	 * @see #getLocalRepository()
	 * @since 1.6.2
	 */
	RobotSpecification[] getLocalRepository(String selectedRobotList);

	/**
	 * Runs the specified battle.
	 *
	 * @param battleSpecification the specification of the battle to run including the
	 *                            participation robots.
	 * @see #runBattle(robocode.control.BattleSpecification, boolean)
	 * @see robocode.control.BattleSpecification
	 * @see #getLocalRepository()
	 */
	void runBattle(BattleSpecification battleSpecification);

	/**
	 * Runs the specified battle.
	 *
	 * @param battleSpecification	   the specification of the battle to run including the
	 *                     participating robots.
	 * @param waitTillOver will block caller till end of battle if set
	 * @see #runBattle(robocode.control.BattleSpecification)
	 * @see robocode.control.BattleSpecification
	 * @see #getLocalRepository()
	 * @since 1.6.2
	 */
	void runBattle(BattleSpecification battleSpecification, boolean waitTillOver);

	/**
	 * Runs the specified battle.
	 *
	 * @param battleSpecification the specification of the battle to run including the
	 *                     participating robots.
	 * @param initialPositions a comma or space separated list like: x1,y1,heading1,
	 *        x2,y2,heading2, which are the coordinates and heading of robot #1 and #2.
	 *        So e.g. 0,0,180, 50,80,270 means that robot #1 has position (0,0) and
	 *        heading 180, and robot #2 has position (50,80) and heading 270.
	 * @param waitTillOver will block caller till end of battle if set
	 * @see #runBattle(BattleSpecification)
	 * @see BattleSpecification
	 * @see #getLocalRepository()
	 * @since 1.7.1.2
	 */
	void runBattle(BattleSpecification battleSpecification, String initialPositions, boolean waitTillOver);

	/**
	 * Will block caller until current battle is over.
	 * @see #runBattle(robocode.control.BattleSpecification)
	 * @see #runBattle(robocode.control.BattleSpecification, boolean)
	 * @since 1.6.2
	 */
	void waitTillBattleOver();

	/**
	 * Aborts the current battle if it is running.
	 *
	 * @see #runBattle(robocode.control.BattleSpecification)
	 */
	void abortCurrentBattle();
}
