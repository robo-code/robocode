package robocode.robotinterfaces;

import robocode.peer.RobotPeer;

/**
 * ****************************************************************************
 * Copyright (c) 2001, 2007 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 * <p/>
 * Contributors:
 * Pavel Savara
 * - Refactoring
 * *****************************************************************************
 */
public interface IRobotBase extends Runnable {

    /**
     * This method is called by the game. RobotPeer is the object that deals with
     * game mechanics and rules, and makes sure your robot abides by them.
     * Do not call this method! Your robot will simply stop interacting with the game.
     */
    void setPeer(RobotPeer peer);
    RobotPeer getPeer();

    void setOut(java.io.PrintStream out);
    java.io.PrintStream getOut();

    IBaseRobotEvents getRobotEventListener();
    IBaseSystemEvents getSystemEventListener();
}
