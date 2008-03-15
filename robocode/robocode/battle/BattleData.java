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

import robocode.peer.BulletPeer;
import robocode.peer.IContestantPeer;
import robocode.peer.IDisplayBulletProxy;
import robocode.peer.RobotPeer;
import robocode.peer.proxies.IBattleBulletProxy;
import robocode.peer.proxies.IBattleRobotProxy;
import robocode.peer.proxies.IDisplayRobotProxy;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Pavel Savara (original)
 */
public class BattleData {

    // Objects in the battle
    private List<RobotPeer> robotsPeers = new CopyOnWriteArrayList<RobotPeer>();
    private List<IDisplayRobotProxy> displayRobots = new CopyOnWriteArrayList<IDisplayRobotProxy>();
    private List<IBattleRobotProxy> battleRobots = new CopyOnWriteArrayList<IBattleRobotProxy>();
    private List<IContestantPeer> contestants = new CopyOnWriteArrayList<IContestantPeer>();
    private List<IBattleBulletProxy> battleBullets = new CopyOnWriteArrayList<IBattleBulletProxy>();
    private List<IDisplayBulletProxy> displayBullets = new CopyOnWriteArrayList<IDisplayBulletProxy>();


    protected void addContestant(IContestantPeer c) {
        if (!contestants.contains(c)) {
            contestants.add(c);
        }
    }

    protected void clearBullets() {
        battleBullets.clear();
        displayBullets.clear();
    }

    public void addBullet(BulletPeer bullet) {
        battleBullets.add(bullet);
        displayBullets.add(bullet);
    }

    public List<IBattleBulletProxy> getBattleBullets() {
        return battleBullets;
    }

    public List<IDisplayBulletProxy> getDisplayBullets() {
        return displayBullets;
    }

    public void removeBullet(BulletPeer bullet) {
        battleBullets.remove(bullet);
        displayBullets.remove(bullet);
    }

    public List<IContestantPeer> getContestants() {
        return contestants;
    }

    public void addRobotPeer(RobotPeer robotPeer) {
        robotsPeers.add(robotPeer);
        displayRobots.add(robotPeer.getDisplayView());
        battleRobots.add(robotPeer.getBattleView());
    }

    public List<RobotPeer> getRobotPeers() {
        return robotsPeers;
    }

    public List<IDisplayRobotProxy> getDisplayRobots() {
        return displayRobots;
    }

    public List<IBattleRobotProxy> getBattleRobots() {
        return battleRobots;
    }

    protected void cleanupData() {
        if (contestants != null) {
            contestants.clear();
            contestants = null;
        }

        if (getBattleRobots() != null) {
            getBattleRobots().clear();
            displayRobots = null;
            battleRobots = null;
        }
    }


}
