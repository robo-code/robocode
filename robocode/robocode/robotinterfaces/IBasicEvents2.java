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
package robocode.robotinterfaces;


import robocode.BattleEndedEvent;


/**
 * First extended version of the {@link IBasicEvents} interface.
 *
 * @author Pavel Savara (original)
 *
 * @since 1.6.1
 */
public interface IBasicEvents2 extends IBasicEvents {
    /**
     * This method is called after end of the battle, even when the battle is aborted.
     * <p/>
     * Your robot could save lessons learned.
     *
     * @param event the win event set by the game
     * @see robocode.BattleEndedEvent
     * @see robocode.WinEvent
     * @see robocode.DeathEvent
     * @see robocode.Event
     */
    void onBattleEnded(BattleEndedEvent event);
}
