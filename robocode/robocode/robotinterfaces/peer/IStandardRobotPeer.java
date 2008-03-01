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
package robocode.robotinterfaces.peer;


/**
 * The standard robot peer for standard robot types like {@link robocode.Robot},
 * {@link robocode.AdvancedRobot}, and {@link robocode.TeamRobot}.
 * <p>
 * A robot peer is the object that deals with game mechanics and rules, and
 * makes sure your robot abides by them.
 * 
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (javadoc)
 *
 * @since 1.6
 */
public interface IStandardRobotPeer extends IBasicRobotPeer {

	/**
	 * Immediately stops all movement, and saves it for a call to
	 * {@link #resume()}. If there is already movement saved from a previous
	 * stop, you can overwrite it by calling {@code stop(true)}.
	 *
	 * @param overwrite If there is already movement saved from a previous stop,
	 *    you can overwrite it by calling {@code stop(true)}.
	 *
	 * @see #resume()
	 */
	void stop(boolean overwrite);

	/**
	 * Immediately resumes the movement you stopped by {@link #stop(boolean)},
	 * if any.
	 * <p>
	 * This call executes immediately, and does not return until it is complete.
	 *
	 * @see #stop(boolean)
	 */
	void resume();

	void scanReset();
	void turnRadar(double radians);

	// fast setters
	void setAdjustGunForBodyTurn(boolean newAdjustGunForBodyTurn);
	void setAdjustRadarForGunTurn(boolean newAdjustRadarForGunTurn);
	void setAdjustRadarForBodyTurn(boolean newAdjustRadarForBodyTurn);
}
