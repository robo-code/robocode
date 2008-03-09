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

import robocode.battle.Battle;
import robocode.peer.data.RobotPeerCommands;
import robocode.peer.data.RobotPeerInfo;
import robocode.peer.data.RobotPeerStatus;
import robocode.peer.robot.*;
import robocode.peer.views.IBattleRobotView;
import robocode.peer.views.IDisplayRobotView;
import robocode.peer.views.IRobotRunnableView;
import robocode.robotinterfaces.IBasicRobot;
import robocode.robotinterfaces.peer.IBasicRobotView;
import robocode.repository.RobotFileSpecification;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Pavel Savara (original)
 */
public interface IRobotPeer extends Runnable {
    //sync
    void lockRead();
    void lockWrite();
    void unlockRead();
    void unlockWrite();
    void checkReadLock();
    void checkWriteLock();
    void checkNoLock();
    ReentrantLock getSyncRoot();

    //init
    void setRobot(IBasicRobot newRobot);
    void setInfo(RobotFileSpecification rfs);

    //data
    RobotPeerInfo getInfo();
    RobotPeerStatus getStatus();
    RobotPeerCommands getCommands();

    //view
    IRobotRunnableView getRobotRunnableView();
    IDisplayRobotView getDisplayView();
    IBattleRobotView getBattleView();
    IBasicRobotView getRobotView();

    //components
    RobotOutputStream getOut();
    IBasicRobot getRobot();
    Battle getBattle();
    RobotClassManager getRobotClassManager();
    RobotFileSystemManager getRobotFileSystemManager();
    RobotThreadManager getRobotThreadManager();
    RobotMessageManager getRobotMessageManager();

    //robot
    IRobotEventManager getRobotEventManager();

    //display
    IDisplayEventManager getDisplayEventManager();

    //battle
    IBattleEventManager getBattleEventManager();
}
