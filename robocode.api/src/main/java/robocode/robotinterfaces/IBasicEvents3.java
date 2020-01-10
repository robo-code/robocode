/**
 * Copyright (c) 2001-2020 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package robocode.robotinterfaces;


import robocode.BattleEndedEvent;
import robocode.RoundEndedEvent;


/**
 * Second extended version of the {@link IBasicEvents} interface.
 *
 * @author Flemming N. Larsen (original)
 *
 * @since 1.7.2
 */
public interface IBasicEvents3 extends IBasicEvents2 {

	/**
	 * This method is called after the end of a round.
	 * You should override it in your robot if you want to be informed of this event.
	 * <p>
	 * Example:
	 * <pre>
	 *   public void onRoundEnded(RoundEndedEvent event) {
	 *       out.println("The round has ended");
	 *   }
	 * </pre>
	 *
	 * @param event the RoundEndedEvent event set by the game
	 * @see RoundEndedEvent
	 * @see #onBattleEnded(BattleEndedEvent)
	 * @see robocode.WinEvent
	 * @see robocode.DeathEvent
	 * @see robocode.Event
	 * 
	 * @since 1.7.2
	 */
	void onRoundEnded(RoundEndedEvent event);
}
