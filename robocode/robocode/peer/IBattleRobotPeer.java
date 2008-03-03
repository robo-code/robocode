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
import robocode.battle.record.RobotRecord;

import java.awt.*;

/**
 * @author Pavel Savara (original)
 */
public interface IBattleRobotPeer extends IContestantPeer {
    String getName();
    RobotOutputStream getOut();
    RobotThreadManager getRobotThreadManager();
    void setStatistics(RobotStatistics newStatistics);

    int getState();
    boolean isDead();
    boolean isAlive();
    boolean isRunning();
    boolean isSleeping();
    boolean isWinner();
    boolean isTeamLeader();
    boolean isIORobot();
    void setState(int newState);
    void setWinner(boolean w);
    void setEnergy(double e);
    void setScan(boolean value);
    void setSkippedTurns(int s);
    
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

    Color getBodyColor();
    Color getGunColor();
    Color getRadarColor();
    Color getScanColor();
    Color getBulletColor();

    void kill();
    void wakeup();
    void zap(double z);

    void scan(List<IBattleRobotPeer> robots);
    void update(List<IBattleRobotPeer> battleRobots);

    void updateBoundingBox();
    void initialize(double x, double y, double heading, List<IBattleRobotPeer> battleRobots);
    void setRobot(IBasicRobot newRobot);
    void preInitialize();
    void cleanup();
    void cleanupStaticFields();
    void setHalt(boolean h);

    void setRecord(RobotRecord rr);
    void setDroid(boolean droid);
    void setJuniorRobot(boolean value);
    void setInteractiveRobot(boolean value);
    void setAdvancedRobot(boolean value);
    void setTeamRobot(boolean value);

    TeamPeer getTeamPeer();
    BoundingRectangle getBoundingBox();
    RobotStatistics getRobotStatistics();
    RobotClassManager getRobotClassManager();
    RobotFileSystemManager getRobotFileSystemManager();
    IBattleEventManager getBattleEventManager();
    RobotMessageManager getMessageManager();
}
