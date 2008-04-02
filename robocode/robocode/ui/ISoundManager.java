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


import robocode.peer.proxies.IBattleBulletProxy;
import robocode.peer.proxies.IBattleRobotProxy;


/**
 * @author Pavel Savara (original)
 */
public interface ISoundManager extends robocode.ui.ILoadableManager {

    /**
     * Plays a bullet sound depending on the bullet's state
     *
     * @param bp the bullet peer
     */
    void playBulletSound(IBattleBulletProxy bp, float battleFiedsWidth);

    /**
     * Plays a robot sound depending on the robot's state
     *
     * @param rp the robot peer
     */
    void playRobotSound(IBattleRobotProxy rp);

    /**
     * Stops the background music.
     */
    void stopBackgroundMusic();

    /**
     * Plays the end of battle music once.
     */
    void playEndOfBattleMusic();

    /**
     * Plays the background music, which is looping forever until stopped.
     */
    void playBackgroundMusic();

    /**
     * Plays the theme music once.
     */
    void playThemeMusic();
}
