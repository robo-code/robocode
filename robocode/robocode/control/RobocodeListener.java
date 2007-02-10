/*******************************************************************************
 * Copyright (c) 2001, 2007 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *******************************************************************************/
package robocode.control;


/**
 * RobocodeListener - Interface for receiving callbacks from RobocodeEngine
 *
 * @author Mathew A. Nelson (original)
 */
public interface RobocodeListener {

	/**
	 * Called when battle is complete.  Contains array of results.
	 *
	 * @param battle Which battle completed
	 * @param results Results for this battle
	 */
	void battleComplete(BattleSpecification battle, RobotResults[] results);

	/**
	 * Called when battle is aborted for any reason.
	 *
	 * @param battle Which battle aborted
	 */
	void battleAborted(BattleSpecification battle);

	/**
	 * Called for messages that would normally go to the Java console
	 *
	 * @param message
	 */
	void battleMessage(String message);
}
