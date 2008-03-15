/**
 * ****************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 * <p/>
 * Contributors:
 * Pavel Savara
 * - Initial implementation
 * *****************************************************************************
 */

package robocode.peer.proxies;

import robocode.battle.record.RobotRecord;
import robocode.peer.BulletPeer;
import robocode.peer.robot.*;

public interface IBattleRobotProxy extends IReadingRobotProxy {
    void lockWrite();

    void unlockWrite();

    void cleanup();

    RobotOutputStream getOut();

    IBattleEventManager getBattleEventManager();

    RobotStatistics getRobotStatistics();

    RobotThreadManager getRobotThreadManager();

    RobotMessageManager getBattleMessageManager();

    RobotClassManager getRobotClassManager();

    RobotFileSystemManager getRobotFileSystemManager();

    IDisplayRobotProxy getDisplayView();

    BulletPeer b_getCurrentBullet();

    void setDuplicate(int d);

    void setScan(boolean value);

    void b_kill();

    void b_wakeup();

    void b_zap(double z);

    void b_scan(java.util.List<IBattleRobotProxy> robots);

    void b_update(java.util.List<IBattleRobotProxy> battleRobots);

    void b_preInitialize();

    void b_initialize(double x, double y, double heading, java.util.List<IBattleRobotProxy> battleRobots);

    void b_updateBoundingBox();


    void b_setHalt(boolean h);

    void b_setRecord(RobotRecord rr);

    void b_adjustGunHeat(double diference);

    void b_setState(int newState);

    void b_setWinner(boolean w);

    void b_setEnergy(double e);

    void b_setSkippedTurns(int s);

    void b_setCurrentBullet(BulletPeer currentBullet);

    void b_setStatistics(RobotStatistics newStatistics);
}
