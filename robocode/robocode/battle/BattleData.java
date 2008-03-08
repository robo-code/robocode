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
package robocode.battle;

import robocode.peer.*;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Pavel Savara (original)
 */
public class BattleData {

    // Objects in the battle
    private List<IDisplayRobotPeer> displayRobots = new CopyOnWriteArrayList<IDisplayRobotPeer>();
    private List<IBattleRobotPeer> battleRobots = new CopyOnWriteArrayList<IBattleRobotPeer>();
    private List<IContestantPeer> contestants = new CopyOnWriteArrayList<IContestantPeer>();
    private List<IBattleBulletPeer> battleBullets = new CopyOnWriteArrayList<IBattleBulletPeer>();
    private List<IDisplayBulletPeer> displayBullets = new CopyOnWriteArrayList<IDisplayBulletPeer>();



    protected void addContestant(IContestantPeer c) {
        if (!contestants.contains(c)) {
            contestants.add(c);
        }
    }

    protected void clearBullets(){
        battleBullets.clear();
        displayBullets.clear();
    }

    public void addBullet(BulletPeer bullet){
        battleBullets.add(bullet);
        displayBullets.add(bullet);
    }

    public List<IBattleBulletPeer> getBattleBullets() {
        return battleBullets;
    }

    public List<IDisplayBulletPeer> getDisplayBullets() {
        return displayBullets;
    }

    public void removeBullet(BulletPeer bullet) {
        battleBullets.remove(bullet);
        displayBullets.remove(bullet);
    }

    public List<IContestantPeer> getContestants() {
        return contestants;
    }

    public void addRobotPeer(RobotPeer robotPeer){
        displayRobots.add(robotPeer);
        battleRobots.add(robotPeer);
    }

    public List<IDisplayRobotPeer> getDisplayRobots() {
        return displayRobots;
    }

    public List<IBattleRobotPeer> getBattleRobots() {
        return battleRobots;
    }

    protected void cleanupData(){
        if (contestants != null) {
            contestants.clear();
            contestants = null;
        }

        if (getBattleRobots() != null) {
            getBattleRobots().clear();
            displayRobots =null;
            battleRobots =null;
        }
    }


}
