/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Updated Javadocs
 *******************************************************************************/
package robocodeui.control;


/**
 * A Listener interface for receiving callbacks from the {@link RobocodeEngine}.
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @see RobocodeEngine
 */
public interface RobocodeListener {

	/**
	 * This method is called when a battle completes successfully.
	 *
	 * @param battle  information about the battle that completed
	 * @param results an array containing the results for the individual robot
	 * @see RobocodeEngine#runBattle(BattleSpecification)
	 * @see BattleSpecification
	 * @see RobotResults
	 */
	void battleComplete(BattleSpecification battle, RobotResults[] results);

	/**
	 * This method is called when a battle has been aborted.
	 *
	 * @param battle information about the battle that was aborted
	 * @see RobocodeEngine#abortCurrentBattle()
	 * @see BattleSpecification
	 */
	void battleAborted(BattleSpecification battle);

	/**
	 * This method is called when the game logs messages that is normally
	 * written out to the console.
	 *
	 * @param message the message logged by the game
	 * @see RobocodeEngine#runBattle(BattleSpecification)
	 */
	void battleMessage(String message);
}
