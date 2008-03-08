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

import java.awt.*;

/**
 * @author Pavel Savara (original)
 */
public interface IBattleReaderRobotPeer {

    void lockRead();
    void unlockRead();
    void checkReadLock();

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
}
