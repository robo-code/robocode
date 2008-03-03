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
import robocode.peer.robot.IDisplayEventManager;

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


    private boolean isJuniorRobot;
    private boolean isInteractiveRobot;
    private boolean isAdvancedRobot;
    private boolean isTeamRobot;
    private boolean isDroid;

    private EventManager eventManager;

    //////////////////////////////////////
    // Display access 
    //////////////////////////////////////

    public IDisplayEventManager getDisplayEventManager() {
        return eventManager;
    }

    //////////////////////////////////////
    // Battle access 
    //////////////////////////////////////
    protected void setEventManager(EventManager eventManager) {
        this.eventManager=eventManager;
    }

    public IBattleEventManager getBattleEventManager() {
        return eventManager;
    }

    public boolean isDroid() {
        return isDroid;
    }

    public void setDroid(boolean droid) {
        this.isDroid = droid;
    }

    /**
     * Returns <code>true</code> if the robot is implementing the
     * {@link robocode.robotinterfaces.IJuniorRobot}; <code>false</code> otherwise.
     */
    public boolean isJuniorRobot() {
        return isJuniorRobot;
    }

    public void setJuniorRobot(boolean value) {
        this.isJuniorRobot = value;
    }

    /**
     * Returns <code>true</code> if the robot is implementing the
     * {@link robocode.robotinterfaces.IInteractiveRobot}; <code>false</code> otherwise.
     */
    public boolean isInteractiveRobot() {
        return isInteractiveRobot;
    }

    public void setInteractiveRobot(boolean value) {
        this.isInteractiveRobot = value;
    }

    /**
     * Returns <code>true</code> if the robot is implementing the
     * {@link robocode.robotinterfaces.IAdvancedRobot}; <code>false</code> otherwise.
     */
    public boolean isAdvancedRobot() {
        return isAdvancedRobot;
    }

    public void setAdvancedRobot(boolean value) {
        this.isAdvancedRobot = value;
    }

    /**
     * Returns <code>true</code> if the robot is implementing the
     * {@link robocode.robotinterfaces.ITeamRobot}; <code>false</code> otherwise.
     */
    public boolean isTeamRobot() {
        return isTeamRobot;
    }

    public void setTeamRobot(boolean value) {
        this.isTeamRobot = value;
    }

    //////////////////////////////////////
    // Robot access
    //////////////////////////////////////

    public IRobotEventManager getRobotEventManager() {
        return eventManager;
    }


}
