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
package robocode.peer;

import robocode.peer.robot.IBattleEventManager;
import robocode.peer.robot.EventManager;
import robocode.peer.robot.IRobotEventManager;

/**
 * @author Pavel Savara (original)
 */
public class RobotPeerData {
    // Robot States: all states last one turn, except ALIVE and DEAD
    public static final int
            STATE_ACTIVE = 0,
            STATE_HIT_WALL = 1,
            STATE_HIT_ROBOT = 2,
            STATE_DEAD = 3;

    public static final int
            WIDTH = 40,
            HEIGHT = 40;

    protected static final int
            HALF_WIDTH_OFFSET = (WIDTH / 2 - 2),
            HALF_HEIGHT_OFFSET = (HEIGHT / 2 - 2);

    protected static final long
            MAX_SET_CALL_COUNT = 10000,
            MAX_GET_CALL_COUNT = 10000;


    private EventManager eventManager;

    protected void setEventManager(EventManager eventManager) {
        this.eventManager=eventManager;
    }

    public IRobotEventManager getRobotEventManager() {
        return eventManager;
    }

    public IBattleEventManager getBattleEventManager() {
        return eventManager;
    }

}
