/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package robocode.control.events;


/**
 * The listener interface for receiving "interesting" battle events from the game, e.g. when a battle,
 * round or turn is started or ended.
 * <p>
 * When implementing this battle listener you should implement the {@link BattleAdaptor} in order to
 * only implement the event handler methods you are interested in.
 *
 * @see BattleAdaptor
 *
 * @author Flemming N. Larsen (original)
 *
 * @since 1.6.2
 */
public interface IBattleListener {

	/**
	 * This method is called when a new battle has started.
	 * <p>
	 * You must override this method in order to get informed about this event and receive the event details.
	 *
	 * @see BattleStartedEvent
	 * @see #onBattleCompleted(BattleCompletedEvent)
	 * @see #onBattleFinished(BattleFinishedEvent)
	 *
	 * @param event the event details.
	 */
	void onBattleStarted(final BattleStartedEvent event);

	/**
	 * This method is called when the battle has finished. This event is always sent as the last battle event,
	 * both when the battle is completed successfully, terminated due to an error, or aborted by the user.
	 * Hence, this events is well-suited for cleanup after the battle. 
	 * <p>
	 * You must override this method in order to get informed about this event and receive the event details.
	 *
	 * @see BattleFinishedEvent
	 * @see #onBattleStarted(BattleStartedEvent)
	 * @see #onBattleCompleted(BattleCompletedEvent)
	 *
	 * @param event the event details.
	 */
	void onBattleFinished(final BattleFinishedEvent event);

	/**
	 * This method is called when the battle has completed successfully and results are available.
	 * This event will not occur if the battle is terminated or aborted by the user before the battle is completed. 
	 * <p>
	 * You must override this method in order to get informed about this event and receive the event details.
	 *
	 * @see BattleCompletedEvent
	 * @see #onBattleStarted(BattleStartedEvent)
	 * @see #onBattleFinished(BattleFinishedEvent)
	 *
	 * @param event the event details.
	 */
	void onBattleCompleted(final BattleCompletedEvent event);

	/**
	 * This method is called when the battle has been paused, either by the user or the game.  
	 * <p>
	 * You must override this method in order to get informed about this event and receive the event details.
	 *
	 * @see BattlePausedEvent
	 * @see #onBattleResumed(BattleResumedEvent)
	 *
	 * @param event the event details.
	 */
	void onBattlePaused(final BattlePausedEvent event);

	/**
	 * This method is called when the battle has been resumed (after having been paused).  
	 * <p>
	 * You must override this method in order to get informed about this event and receive the event details.
	 *
	 * @see BattleResumedEvent
	 * @see #onBattlePaused(BattlePausedEvent)
	 *
	 * @param event the event details.
	 */
	void onBattleResumed(final BattleResumedEvent event);

	/**
	 * This method is called when a new round in a battle has started.
	 * <p>
	 * You must override this method in order to get informed about this event and receive the event details.
	 *
	 * @see RoundEndedEvent
	 * @see #onRoundEnded(RoundEndedEvent)
	 *
	 * @param event the event details.
	 */
	void onRoundStarted(final RoundStartedEvent event);

	/**
	 * This method is called when the current round of a battle has ended. 
	 * <p>
	 * You must override this method in order to get informed about this event and receive the event details.
	 *
	 * @see RoundEndedEvent
	 * @see #onRoundStarted(RoundStartedEvent)
	 *
	 * @param event the event details.
	 */
	void onRoundEnded(final RoundEndedEvent event);

	/**
	 * This method is called when a new turn in a battle round has started. 
	 * <p>
	 * You must override this method in order to get informed about this event and receive the event details.
	 *
	 * @see TurnStartedEvent
	 * @see #onTurnEnded(TurnEndedEvent)
	 *
	 * @param event the event details.
	 */
	void onTurnStarted(final TurnStartedEvent event);

	/**
	 * This method is called when the current turn in a battle round is ended. 
	 * <p>
	 * You must override this method in order to get informed about this event and receive the event details.
	 *
	 * @see TurnEndedEvent
	 * @see #onTurnStarted(TurnStartedEvent)
	 *
	 * @param event the event details.
	 */
	void onTurnEnded(final TurnEndedEvent event);

	/**
	 * This method is called when the game has sent a new information message.
	 * <p>
	 * You must override this method in order to get informed about this event and receive the event details.
	 *
	 * @see BattleMessageEvent
	 * @see #onBattleError(BattleErrorEvent)
	 *
	 * @param event the event details.
	 */
	void onBattleMessage(final BattleMessageEvent event);

	/**
	 * This method is called when the game has sent an error message.
	 * <p>
	 * You must override this method in order to get informed about this event and receive the event details.
	 *
	 * @see BattleErrorEvent
	 * @see #onBattleMessage(BattleMessageEvent)
	 *
	 * @param event the event details.
	 */
	void onBattleError(final BattleErrorEvent event);
}
