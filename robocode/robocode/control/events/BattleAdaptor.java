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


import robocode.control.IBattleListener;


/**
 * This is handy base class for anyone implementing robocode.control.IBattleListener
 * It saves you from implementing empty handlers for events you are not
 * interested to receive.
 *  
 * @author Flemming N. Larsen (original)
 * @since 1.6.1
 */
public abstract class BattleAdaptor implements IBattleListener {

	/**
	 * {@inheritDoc}
	 */
	public void onBattleStarted(final BattleStartedEvent event) {}

	/**
	 * {@inheritDoc}
	 */
	public void onBattleFinished(final BattleFinishedEvent event) {}

	/**
	 * {@inheritDoc}
	 */
	public void onBattleCompleted(final BattleCompletedEvent event) {}

	/**
	 * {@inheritDoc}
	 */
	public void onBattlePaused(final BattlePausedEvent event) {}

	/**
	 * {@inheritDoc}
	 */
	public void onBattleResumed(final BattleResumedEvent event) {}

	/**
	 * {@inheritDoc}
	 */
	public void onRoundStarted(final RoundStartedEvent event) {}

	/**
	 * {@inheritDoc}
	 */
	public void onRoundEnded(final RoundEndedEvent event) {}

	/**
	 * {@inheritDoc}
	 */
	public void onTurnStarted(final TurnStartedEvent event) {}

	/**
	 * {@inheritDoc}
	 */
	public void onTurnEnded(final TurnEndedEvent event) {}

	/**
	 * {@inheritDoc}
	 */
	public void onBattleMessage(final BattleMessageEvent event) {}

	/**
	 * {@inheritDoc}
	 */
	public void onBattleError(final BattleErrorEvent event) {}
}
