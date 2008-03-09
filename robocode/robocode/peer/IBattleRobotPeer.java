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

import java.util.List;
import robocode.peer.robot.*;
import robocode.util.BoundingRectangle;
import robocode.robotinterfaces.IBasicRobot;
import robocode.robotinterfaces.peer.IReaderRobotPeer;
import robocode.battle.record.RobotRecord;
import robocode.repository.RobotFileSpecification;
import robocode.*;

import java.awt.*;
import java.io.Serializable;
import java.io.IOException;
import java.io.File;

/**
 * @author Pavel Savara (original)
 */
public interface IBattleRobotPeer extends IContestantPeer {

    void lockRead();
    void lockWrite();
    void unlockRead();
    void unlockWrite();
    void checkReadLock();
    void checkWriteLock();

    IBasicRobot getRobot();
    String getName();
    RobotOutputStream getOut();
    RobotThreadManager getRobotThreadManager();


    int getState();
    boolean isDead();
    boolean isAlive();
    boolean isRunning();
    boolean isSleeping();
    boolean isWinner();
    boolean isTeamLeader();
    boolean isIORobot();

    Color getBodyColor();
    Color getGunColor();
    Color getRadarColor();
    Color getScanColor();
    Color getBulletColor();
    double getX();
    double getY();
    double getBattleFieldWidth();
    double getEnergy();
    double getHeading();
    double getRadarHeading();
    double getGunHeading();
    double getVelocity();
    double getTurnRemaining();
    double getRadarTurnRemaining();
    double getGunTurnRemaining();
    double getDistanceRemaining();
    double getGunHeat();
    boolean getScan();
    int getSkippedTurns();


    void b_kill();
    void b_wakeup();
    void b_zap(double z);

    void b_scan(List<IBattleRobotPeer> robots);
    void b_update(List<IBattleRobotPeer> battleRobots);

    void b_preInitialize();
    void b_initialize(double x, double y, double heading, List<IBattleRobotPeer> battleRobots);
    void b_setRobot(IBasicRobot newRobot);
    void b_setupInfo(RobotFileSpecification rfs);
    void b_updateBoundingBox();

    void b_cleanup();
    void b_cleanupStaticFields();
    void b_setHalt(boolean h);
    void b_setDuplicate(int d);
    void b_setRecord(RobotRecord rr);
    void b_adjustGunHeat(double diference);
    void b_setState(int newState);
    void b_setWinner(boolean w);
    void b_setEnergy(double e);
    void b_setScan(boolean value);
    void b_setSkippedTurns(int s);
    BulletPeer b_getCurrentBullet();
    void b_setCurrentBullet(BulletPeer currentBullet);
    void setStatistics(RobotStatistics newStatistics);
    
    TeamPeer getTeamPeer();
    BoundingRectangle getBoundingBox();
    RobotStatistics getRobotStatistics();
    RobotClassManager getRobotClassManager();
    RobotFileSystemManager getRobotFileSystemManager();
    IBattleEventManager getBattleEventManager();
    RobotMessageManager getMessageManager();
}