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
package robocode.ui;


import robocode.peer.BulletPeer;
import robocode.peer.RobotPeer;


/**
 * @author Pavel Savara (original)
 */
public interface ISoundManager extends robocode.ui.ILoadableManager {
	void playBulletSound(BulletPeer bp);
	void playRobotSound(RobotPeer rp);
	void stopBackgroundMusic();
	void playEndOfBattleMusic();
	void playBackgroundMusic();
	void playThemeMusic();
}
