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
package robocode.peer.proxies;


import robocode.peer.IRobotPeer;
import robocode.peer.TeamPeer;
import robocode.util.BoundingRectangle;

import java.awt.*;
import java.awt.geom.Arc2D;


/**
 * @author Pavel Savara (original)
 */
public interface IReadingRobotProxy {
    boolean isAlive();

    boolean isDead();

    boolean isDroid();

    boolean isTeamRobot();

    boolean isAdvancedRobot();

    boolean isInteractiveRobot();

    boolean isInteractiveListener();

    boolean isDuplicate();

    boolean isPaintEnabled();

    boolean isSGPaintEnabled();

    IRobotPeer getPeer();

    TeamPeer getTeamPeer();

    int getState();

    boolean isRunning();

    boolean isSleeping();

    boolean isWinner();

    boolean isTeamLeader();

    boolean isIORobot();

    double getVelocity();

    double getGunHeat();

    double getEnergy();

    double getX();

    double getY();

    double getBodyHeading();

    double getRadarHeading();

    double getGunHeading();

    Arc2D getScanArc();

    boolean getScan();

    int getSkippedTurns();

    double getBattleFieldWidth();

    BoundingRectangle getBoundingBox();

    double getBodyTurnRemaining();

    double getRadarTurnRemaining();

    double getGunTurnRemaining();

    double getDistanceRemaining();

    Color getBodyColor();

    Color getGunColor();

    Color getRadarColor();

    Color getScanColor();

    Color getBulletColor();

    String getName();

    String getShortName();

    String getVeryShortName();

    String getFullClassNameWithVersion();

    String getNonVersionedName();

    String getUniqueFullClassNameWithVersion();
}
