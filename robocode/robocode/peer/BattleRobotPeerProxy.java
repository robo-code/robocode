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
package robocode.peer;

import robocode.peer.robot.*;
import robocode.robotinterfaces.IBasicRobot;
import robocode.battle.record.RobotRecord;
import robocode.util.BoundingRectangle;

import java.awt.*;
import java.util.List;

public class BattleRobotPeerProxy implements IBattleRobotPeer{

    RobotPeer peer;

    public BattleRobotPeerProxy(RobotPeer peer) {
        this.peer=peer;
    }

    public IContestantStatistics getStatistics() {
        return null;  //TODO ZAMO
    }

    public String getName() {
        return null;  //TODO ZAMO
    }

    public RobotOutputStream getOut() {
        return null;  //TODO ZAMO
    }

    public RobotThreadManager getRobotThreadManager() {
        return null;  //TODO ZAMO
    }

    public int getState() {
        return 0;  //TODO ZAMO
    }

    public boolean isDead() {
        return false;  //TODO ZAMO
    }

    public boolean isAlive() {
        return false;  //TODO ZAMO
    }

    public boolean isRunning() {
        return false;  //TODO ZAMO
    }

    public boolean isSleeping() {
        return false;  //TODO ZAMO
    }

    public boolean isWinner() {
        return false;  //TODO ZAMO
    }

    public boolean isTeamLeader() {
        return false;  //TODO ZAMO
    }

    public boolean isIORobot() {
        return false;  //TODO ZAMO
    }

    public double getX() {
        return 0;  //TODO ZAMO
    }

    public double getY() {
        return 0;  //TODO ZAMO
    }

    public double getBattleFieldWidth() {
        return 0;  //TODO ZAMO
    }

    public double getEnergy() {
        return 0;  //TODO ZAMO
    }

    public double getHeading() {
        return 0;  //TODO ZAMO
    }

    public double getRadarHeading() {
        return 0;  //TODO ZAMO
    }

    public double getGunHeading() {
        return 0;  //TODO ZAMO
    }

    public double getVelocity() {
        return 0;  //TODO ZAMO
    }

    public double getTurnRemaining() {
        return 0;  //TODO ZAMO
    }

    public double getRadarTurnRemaining() {
        return 0;  //TODO ZAMO
    }

    public double getGunTurnRemaining() {
        return 0;  //TODO ZAMO
    }

    public double getDistanceRemaining() {
        return 0;  //TODO ZAMO
    }

    public double getGunHeat() {
        return 0;  //TODO ZAMO
    }

    public boolean getScan() {
        return false;  //TODO ZAMO
    }

    public int getSkippedTurns() {
        return 0;  //TODO ZAMO
    }

    public Color getBodyColor() {
        return null;  //TODO ZAMO
    }

    public Color getGunColor() {
        return null;  //TODO ZAMO
    }

    public Color getRadarColor() {
        return null;  //TODO ZAMO
    }

    public Color getScanColor() {
        return null;  //TODO ZAMO
    }

    public Color getBulletColor() {
        return null;  //TODO ZAMO
    }

    public void setStatistics(RobotStatistics newStatistics) {
        //TODO ZAMO
    }

    public void setState(int newState) {
        //TODO ZAMO
    }

    public void setWinner(boolean w) {
        //TODO ZAMO
    }

    public void setEnergy(double e) {
        //TODO ZAMO
    }

    public void setScan(boolean value) {
        //TODO ZAMO
    }

    public void setSkippedTurns(int s) {
        //TODO ZAMO
    }

    public void kill() {
        //TODO ZAMO
    }

    public void wakeup() {
        //TODO ZAMO
    }

    public void zap(double z) {
        //TODO ZAMO
    }

    public void scan(java.util.List<IBattleRobotPeer> robots) {
        //TODO ZAMO
    }

    public void update(List<IBattleRobotPeer> battleRobots) {
        //TODO ZAMO
    }

    public void updateBoundingBox() {
        //TODO ZAMO
    }

    public void initialize(double x, double y, double heading, List<IBattleRobotPeer> battleRobots) {
        //TODO ZAMO
    }

    public void setRobot(IBasicRobot newRobot) {
        //TODO ZAMO
    }

    public void preInitialize() {
        //TODO ZAMO
    }

    public void cleanup() {
        //TODO ZAMO
    }

    public void cleanupStaticFields() {
        //TODO ZAMO
    }

    public void setHalt(boolean h) {
        //TODO ZAMO
    }

    public void setRecord(RobotRecord rr) {
        //TODO ZAMO
    }

    public void setDroid(boolean droid) {
        //TODO ZAMO
    }

    public void setJuniorRobot(boolean value) {
        //TODO ZAMO
    }

    public void setInteractiveRobot(boolean value) {
        //TODO ZAMO
    }

    public void setAdvancedRobot(boolean value) {
        //TODO ZAMO
    }

    public void setTeamRobot(boolean value) {
        //TODO ZAMO
    }

    public TeamPeer getTeamPeer() {
        return null;  //TODO ZAMO
    }

    public BoundingRectangle getBoundingBox() {
        return null;  //TODO ZAMO
    }

    public RobotStatistics getRobotStatistics() {
        return null;  //TODO ZAMO
    }

    public RobotClassManager getRobotClassManager() {
        return null;  //TODO ZAMO
    }

    public RobotFileSystemManager getRobotFileSystemManager() {
        return null;  //TODO ZAMO
    }

    public IBattleEventManager getBattleEventManager() {
        return null;  //TODO ZAMO
    }

    public RobotMessageManager getMessageManager() {
        return null;  //TODO ZAMO
    }

    public int compareTo(IContestantPeer iContestantPeer) {
        return 0;  //TODO ZAMO
    }
}
