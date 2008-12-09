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
package robocode.battle.events;


/**
 * @author Flemming N. Larsen (original)
 * @since 1.6.1
 */
public class BattleAdaptor implements IBattleListener {

	public void onBattleStarted(final BattleStartedEvent event) {}

	public void onBattleEnded(final BattleFinishedEvent event) {}

	public void onBattleCompleted(final BattleCompletedEvent event) {}

	public void onBattlePaused(final BattlePausedEvent event) {}

	public void onBattleResumed(final BattleResumedEvent event) {}

	public void onRoundStarted(final RoundStartedEvent event) {}

	public void onRoundEnded(final RoundEndedEvent event) {}

	public void onTurnStarted(final TurnStartedEvent event) {}

	public void onTurnEnded(final TurnEndedEvent event) {}

	public void onBattleMessage(final BattleMessageEvent event) {}

	public void onBattleError(final BattleErrorEvent event) {}
}
