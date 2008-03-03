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

import robocode.peer.robot.IDisplayEventManager;
import robocode.peer.robot.RobotOutputStream;

import java.awt.geom.Arc2D;
import java.awt.*;

public class DisplayRobotPeerProxy implements IDisplayRobotPeer{

    RobotPeer peer;

    public DisplayRobotPeerProxy(RobotPeer peer) {
        this.peer=peer;
    }

    public boolean isAlive() {
        return false;  //TODO ZAMO
    }

    public boolean isDead() {
        return false;  //TODO ZAMO
    }

    public boolean isDroid() {
        return false;  //TODO ZAMO
    }

    public boolean isInteractiveRobot() {
        return false;  //TODO ZAMO
    }

    public boolean isInteractiveListener() {
        return false;  //TODO ZAMO
    }

    public boolean isDuplicate() {
        return false;  //TODO ZAMO
    }

    public boolean isPaintEnabled() {
        return false;  //TODO ZAMO
    }

    public boolean isSGPaintEnabled() {
        return false;  //TODO ZAMO
    }

    public void setDuplicate(int d) {
        //TODO ZAMO
    }

    public void setPaintEnabled(boolean enabled) {
        //TODO ZAMO
    }

    public void setSGPaintEnabled(boolean enabled) {
        //TODO ZAMO
    }

    public double getEnergy() {
        return 0;  //TODO ZAMO
    }

    public double getX() {
        return 0;  //TODO ZAMO
    }

    public double getY() {
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

    public Arc2D getScanArc() {
        return null;  //TODO ZAMO
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

    public String getName() {
        return null;  //TODO ZAMO
    }

    public String getShortName() {
        return null;  //TODO ZAMO
    }

    public String getVeryShortName() {
        return null;  //TODO ZAMO
    }

    public String getFullClassNameWithVersion() {
        return null;  //TODO ZAMO
    }

    public String getUniqueFullClassNameWithVersion() {
        return null;  //TODO ZAMO
    }

    public void kill() {
        //TODO ZAMO
    }

    public IDisplayEventManager getDisplayEventManager() {
        return null;  //TODO ZAMO
    }

    public RobotOutputStream getOut() {
        return null;  //TODO ZAMO
    }
}
