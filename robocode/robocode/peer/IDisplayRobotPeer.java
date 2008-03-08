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

import robocode.peer.robot.IDisplayEventManager;
import robocode.peer.robot.RobotOutputStream;

import java.awt.*;
import java.awt.geom.Arc2D;

/**
 * @author Pavel Savara (original)
 */
public interface IDisplayRobotPeer {
    boolean isAlive();
    boolean isDead();
    boolean isDroid();
    boolean isInteractiveRobot();
    boolean isInteractiveListener();
    boolean isDuplicate();
    boolean isPaintEnabled();
    boolean isSGPaintEnabled();

    void setPaintEnabled(boolean enabled);
    void setSGPaintEnabled(boolean enabled);

    double getEnergy();
    double getX();
    double getY();
    double getHeading();
    double getRadarHeading();
    double getGunHeading();
    Arc2D getScanArc();
    
    Color getBodyColor();
    Color getGunColor();
    Color getRadarColor();
    Color getScanColor();
    Color getBulletColor();
    String getName();
    String getShortName();
    String getVeryShortName();
    String getFullClassNameWithVersion();
    String getUniqueFullClassNameWithVersion();
    RobotOutputStream getOut();

    //TODO synchronize
    void d_kill();
    void d_setScan(boolean v);
    void d_setDuplicate(int d);
    IDisplayEventManager getDisplayEventManager();
    void onPaint(Graphics2D g);
}
