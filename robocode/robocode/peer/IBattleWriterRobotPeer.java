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

/**
 * @author Pavel Savara (original)
 */
public interface IBattleWriterRobotPeer {
    void b_setState(int newState);
    void b_setWinner(boolean w);
    void b_setEnergy(double e);
    void b_setScan(boolean value);
    void b_setSkippedTurns(int s);
    BulletPeer b_getCurrentBullet();
    void b_setCurrentBullet(BulletPeer currentBullet);
}
