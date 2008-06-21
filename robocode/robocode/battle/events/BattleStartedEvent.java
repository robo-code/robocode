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
package robocode.battle.events;

import robocode.battle.BattleProperties;
import robocode.battle.snapshot.TurnSnapshot;

/**
 * @author Pavel Savara (original)
 */
public class BattleStartedEvent extends BattleEvent {
    private final TurnSnapshot start;
    private final BattleProperties battleProperties;
    private final boolean isReplay;

    public BattleStartedEvent(TurnSnapshot start, BattleProperties battleProperties, boolean isReplay) {
        this.start = start;
        this.battleProperties = battleProperties;
        this.isReplay = isReplay;
    }

    public TurnSnapshot getTurnSnapshot() {
        return start;
    }

    public BattleProperties getBattleProperties() {
        return battleProperties;
    }

    public boolean isReplay() {
        return isReplay;
    }
}
