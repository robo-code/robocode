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
 *******************************************************************************/
package robocode.robotinterfaces;

import robocode.BattleEndedEvent;

/**
 * First extended version of IBasicEvents interface 
 *
 * @author Pavel Savara (original)
 */
public interface IBasicEvents2 extends IBasicEvents {
    /**
     * This method is called after end of the battle. Even in case that battle is aborted.
     * <p/>
     * Your robot could save lesons learned.
     *
     * @param event the win event set by the game
     * @see robocode.WinEvent
     * @see robocode.Event
     */
    void onBattleEnded(BattleEndedEvent event);
}
