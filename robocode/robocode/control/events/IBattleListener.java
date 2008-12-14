/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Flemming N. Larsen
 *     - Initial implementation
 *******************************************************************************/
package robocode.control.events;


import robocode.control.BattleSpecification;


/**
 * @author Flemming N. Larsen (original)
 * @since 1.6.1
 */
public interface IBattleListener {

	public void onBattleStarted(final BattleStartedEvent event);

	public void onBattleFinished(final BattleFinishedEvent event);

	public void onBattleCompleted(final BattleCompletedEvent event);

	public void onBattlePaused(final BattlePausedEvent event);

	public void onBattleResumed(final BattleResumedEvent event);

	public void onRoundStarted(final RoundStartedEvent event);

	public void onRoundEnded(final RoundEndedEvent event);

	public void onTurnStarted(final TurnStartedEvent event);

	public void onTurnEnded(final TurnEndedEvent event);

	/**
	 * This method is called when the game logs messages that is normally
	 * written out to the console.
	 * <p/>
	 * Note: may be called from multiple threads
	 *
	 * @param event the message logged by the game
	 * @see robocode.control.RobocodeEngine#runBattle(BattleSpecification)
	 */
	void onBattleMessage(final BattleMessageEvent event);

	/**
	 * This method is called when the game logs messages that is normally
	 * written out to the err console.
	 * <p/>
	 * Note: may be called from multiple threads
	 *
	 * @param event the message logged by the game
	 * @see robocode.control.RobocodeEngine#runBattle(BattleSpecification)
	 */
	void onBattleError(final BattleErrorEvent event);
}
