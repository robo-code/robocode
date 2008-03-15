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

import robocode.Bullet;
import robocode.peer.IDisplayBulletProxy;

import java.awt.geom.Line2D;
import java.util.List;

/**
 * @author Pavel Savara (original)
 */
public interface IBattleBulletProxy extends IDisplayBulletProxy {
    void update(List<IBattleRobotProxy> robots, List<IBattleBulletProxy> allBullets);

    IBattleRobotProxy getOwner();

    double getX();

    double getY();

    boolean isActive();

    void setState(int state);

    Bullet getBullet();

    Line2D.Double getBoundingLine();
}
