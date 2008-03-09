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
package robocode.peer.views;

import robocode.peer.IRobotPeer;
import robocode.peer.TeamPeer;
import robocode.peer.data.RobotPeerCommands;
import robocode.peer.data.RobotPeerInfo;
import robocode.peer.data.RobotPeerStatus;
import robocode.peer.robot.RobotOutputStream;
import robocode.peer.robot.RobotStatistics;
import robocode.util.BoundingRectangle;

import java.awt.*;
import java.awt.geom.Arc2D;

/**
 * @author Pavel Savara (original)
 */
public class ReadingRobotView implements IReadingRobotView {
    private IRobotPeer peer;
    protected RobotPeerStatus status;
    protected RobotPeerInfo info;
    protected RobotPeerCommands commands;

    public ReadingRobotView(IRobotPeer peer) {
        this.peer = peer;
        this.info = peer.getInfo();
        this.status = peer.getStatus();
        this.commands = peer.getCommands();
    }

    public void cleanup(){
        this.peer = null;
        this.info = null;
        this.status = null;
        this.commands = null;
    }



    public final RobotOutputStream getOut(){
        //TODO synch
        return peer.getOut();
    }


    public final int getState() {
        peer.lockRead();
        try{
            return status.getState();
        }
        finally {
            peer.unlockRead();
        }
    }

    public boolean isTeammate(IBattleRobotView robot) {
        TeamPeer teamPeer = info.getTeamPeer();
        return (teamPeer != null && teamPeer == robot.getTeamPeer());
    }

    public final boolean isDroid() {
        return false;  //TODO ZAMO
    }

    public boolean isTeamRobot() {
        return false;  //TODO ZAMO
    }

    public boolean isAdvancedRobot() {
        return false;  //TODO ZAMO
    }

    public final boolean isInteractiveRobot() {
        return false;  //TODO ZAMO
    }

    public final boolean isInteractiveListener() {
        return false;  //TODO ZAMO
    }

    public final boolean isDuplicate() {
        return false;  //TODO ZAMO
    }

    public final boolean isPaintEnabled() {
        return false;  //TODO ZAMO
    }

    public final boolean isSGPaintEnabled() {
        return false;  //TODO ZAMO
    }

    public IRobotPeer getPeer() {
        return peer;
    }

    public final Arc2D getScanArc() {
        return null;  //TODO ZAMO
    }

    public final String getShortName() {
        return null;  //TODO ZAMO
    }

    public final String getVeryShortName() {
        return null;  //TODO ZAMO
    }

    public final String getFullClassNameWithVersion() {
        return null;  //TODO ZAMO
    }

    public String getNonVersionedName() {
        return null;  //TODO ZAMO
    }

    public final String getUniqueFullClassNameWithVersion() {
        return null;  //TODO ZAMO
    }

    public final boolean isDead() {
        return false;  //TODO ZAMO
    }

    public final boolean isAlive() {
        return false;  //TODO ZAMO
    }

    public final boolean isRunning() {
        return false;  //TODO ZAMO
    }

    public final boolean isSleeping() {
        return false;  //TODO ZAMO
    }

    public final boolean isWinner() {
        return false;  //TODO ZAMO
    }

    public final boolean isTeamLeader() {
        return false;  //TODO ZAMO
    }

    public final boolean isIORobot() {
        return false;  //TODO ZAMO
    }

    public final String getName() {
        return null;  //TODO ZAMO
    }

    public final Color getBodyColor() {
        return null;  //TODO ZAMO
    }

    public final Color getGunColor() {
        return null;  //TODO ZAMO
    }

    public final Color getRadarColor() {
        return null;  //TODO ZAMO
    }

    public final Color getScanColor() {
        return null;  //TODO ZAMO
    }

    public final Color getBulletColor() {
        return null;  //TODO ZAMO
    }

    public final double getX() {
        return 0;  //TODO ZAMO
    }

    public final double getY() {
        return 0;  //TODO ZAMO
    }

    public final double getBattleFieldWidth() {
        return 0;  //TODO ZAMO
    }

    public final double getBattleFieldHeight() {
        return 0;  //TODO ZAMO
    }

    public final double getEnergy() {
        return 0;  //TODO ZAMO
    }

    public final double getHeading() {
        return 0;  //TODO ZAMO
    }

    public final double getRadarHeading() {
        return 0;  //TODO ZAMO
    }

    public final double getGunHeading() {
        return 0;  //TODO ZAMO
    }

    public final double getVelocity() {
        return 0;  //TODO ZAMO
    }

    public final double getTurnRemaining() {
        return 0;  //TODO ZAMO
    }

    public final double getRadarTurnRemaining() {
        return 0;  //TODO ZAMO
    }

    public final double getGunTurnRemaining() {
        return 0;  //TODO ZAMO
    }

    public final double getDistanceRemaining() {
        return 0;  //TODO ZAMO
    }

    public final double getGunHeat() {
        return 0;  //TODO ZAMO
    }

    public final boolean getScan() {
        return false;  //TODO ZAMO
    }

    public final int getSkippedTurns() {
        return 0;  //TODO ZAMO
    }

    public final TeamPeer getTeamPeer() {
        return null;  //TODO ZAMO
    }

    public final BoundingRectangle getBoundingBox() {
        return null;  //TODO ZAMO
    }

    public final void b_setStatistics(RobotStatistics newStatistics) {
        //TODO ZAMO
    }
}
