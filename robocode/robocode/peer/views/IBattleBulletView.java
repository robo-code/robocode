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

import robocode.Bullet;
import robocode.peer.IDisplayBulletView;

import java.awt.geom.Line2D;
import java.util.List;

/**
 * @author Pavel Savara (original)
 */
public interface IBattleBulletView extends IDisplayBulletView {
    void update(List<IBattleRobotView> robots, List<IBattleBulletView> allBullets);
    IBattleRobotView getOwner();
    double getX();
    double getY();
    boolean isActive();
    void setState(int state);
    Bullet getBullet();
    Line2D.Double getBoundingLine();
}
