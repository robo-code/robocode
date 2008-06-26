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

	public void onBattleStarted(BattleStartedEvent event) {}

	public void onBattleEnded(BattleEndedEvent event) {}

	public void onBattleCompleted(BattleCompletedEvent event) {}

	public void onBattlePaused(BattlePausedEvent event) {}

	public void onBattleResumed(BattleResumedEvent event) {}

	public void onRoundStarted(RoundStartedEvent event) {}

	public void onRoundEnded(RoundEndedEvent event) {}

	public void onTurnStarted(TurnStartedEvent event) {}

	public void onTurnEnded(TurnEndedEvent event) {}

	public void onBattleMessage(BattleMessageEvent event) {}

	public void onBattleError(BattleErrorEvent event) {}
}
