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

import robocode.Bullet;

import java.util.List;
import java.awt.*;
import java.awt.geom.Line2D;

/**
 * @author Pavel Savara (original)
 */
public interface IBattleBulletPeer {
    void update(List<IBattleRobotPeer> robots, List<IBattleBulletPeer> allBullets);
    IBattleRobotPeer getOwner();
    double getPaintX();
    double getPaintY();
    int getExplosionImageIndex();
    double getX();
    double getY();
    double getPower();
    int getFrame();
    int getState();
    Color getColor();
    boolean isActive();
    Bullet getBullet();
    void setState(int state);
    Line2D.Double getBoundingLine();
}
