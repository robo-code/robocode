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
import robocode.repository.RobotFileSpecification;
import robocode.*;

import java.awt.*;
import java.io.Serializable;
import java.io.IOException;
import java.io.File;

/**
 * @author Pavel Savara (original)
 */
public interface IBattleRobotPeer extends IBattleReaderRobotPeer, IBattleWriterRobotPeer, IContestantPeer {
    Color getBodyColor();
    Color getGunColor();
    Color getRadarColor();
    Color getScanColor();
    Color getBulletColor();

    
    String getName();
    RobotOutputStream getOut();
    RobotThreadManager getRobotThreadManager();
    void setStatistics(RobotStatistics newStatistics);

    void kill();
    void wakeup();
    void zap(double z);

    void scan(List<IBattleRobotPeer> robots);
    void update(List<IBattleRobotPeer> battleRobots);

    void preInitialize();
    void initialize(double x, double y, double heading, List<IBattleRobotPeer> battleRobots);
    void setRobot(IBasicRobot newRobot);
    void initInfo(RobotFileSpecification rfs);
    void updateBoundingBox();

    void cleanup();
    void cleanupStaticFields();
    void b_setHalt(boolean h);
    void b_setDuplicate(int d);
    void b_setRecord(RobotRecord rr);

    TeamPeer getTeamPeer();
    BoundingRectangle getBoundingBox();
    RobotStatistics getRobotStatistics();
    RobotClassManager getRobotClassManager();
    RobotFileSystemManager getRobotFileSystemManager();
    IBattleEventManager getBattleEventManager();
    RobotMessageManager getMessageManager();
}