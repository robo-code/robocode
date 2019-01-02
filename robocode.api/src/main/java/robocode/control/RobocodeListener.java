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
 * @deprecated Since 1.6.2. Use the {@link IBattleListener} instead.
 * <p>
 * A listener interface for receiving callbacks from the {@link RobocodeEngine}.
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 *
 * @see IBattleListener
 */
@Deprecated
public interface RobocodeListener {

	/**
	 * @deprecated Since 1.6.2. Use the
	 * {@link IBattleListener#onBattleCompleted(robocode.control.events.BattleCompletedEvent)
	 * IBattleListener.onBattleCompleted()} instead.
	 * <p>
	 * This method is called when a battle completes successfully.
	 *
	 * @param battle  information about the battle that completed
	 * @param results an array containing the results for the individual robot
	 */
	@Deprecated
	void battleComplete(BattleSpecification battle, RobotResults[] results);

	/**
	 * @deprecated Since 1.6.2. Use the
	 * {@link IBattleListener#onBattleFinished(robocode.control.events.BattleFinishedEvent)
	 * IBattleListener.onBattleFinished()} instead.
	 * <p>
	 * This method is called when a battle has been aborted.
	 *
	 * @param battle information about the battle that was aborted
	 */
	@Deprecated
	void battleAborted(BattleSpecification battle);

	/**
	 * @deprecated Since 1.6.2. Use the
	 * {@link IBattleListener#onBattleMessage(robocode.control.events.BattleMessageEvent)
	 * IBattleListener.onBattleMessage()} instead.
	 * <p>
	 * This method is called when the game logs messages that is normally
	 * written out to the console.
	 *
	 * @param message the message logged by the game
	 */
	@Deprecated
	void battleMessage(String message);
}
