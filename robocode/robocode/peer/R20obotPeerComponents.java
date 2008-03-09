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

import robocode.battlefield.BattleField;
import robocode.battlefield.DefaultBattleField;
import robocode.battle.Battle;
import robocode.peer.robot.*;
import robocode.robotinterfaces.IBasicRobot;

/**
 * Set at start never change, read only, no synchro
 * @author Pavel Savara (original)
 */
public class R20obotPeerComponents extends R10obotPeerEvents {
    
    private BattleField battleField= new DefaultBattleField(800, 600);
    private Battle battle = new Battle(battleField, null);
    private RobotClassManager robotClassManager;
    private RobotFileSystemManager robotFileSystemManager;
    private RobotThreadManager robotThreadManager;
    private RobotOutputStream out;
    private IBasicRobot robot;

    public void b_setRobot(IBasicRobot newRobot) {
        robot = newRobot;
        if (robot != null) {
            if (info.isTeamRobot()) {
                setMessageManager(new RobotMessageManager((RobotPeer)this));
            }
            getBattleEventManager().setRobot(newRobot);
        }
    }

    public IBasicRobot getRobot() {
        return robot;
    }

    public void initComponents(RobotClassManager robotClassManager, long fileSystemQuota){
        this.robotClassManager = robotClassManager;
        robotThreadManager = new RobotThreadManager((RobotPeer)this);
        robotFileSystemManager = new RobotFileSystemManager((RobotPeer)this, fileSystemQuota);
    }
    
    public void setBattle(Battle newBattle) {
        battle = newBattle;
        if (battle==null){
            battleField = null;
        }
        else{
            battleField = battle.getBattleField();
        }
    }

    protected void cleanupComponents(){
        // Cleanup and remove class manager
        if (getRobotClassManager() != null) {
            getRobotClassManager().cleanup();
        }

        // Remove the file system and the manager
        robotClassManager = null;
        robotFileSystemManager = null;
        robotThreadManager = null;
        out = null;
    }


    public RobotOutputStream getOut() {
        //TODO ZAMO synchronize
        if (out == null) {
            if (getBattle() != null) {
                out = new RobotOutputStream(getBattle().getBattleThread());
            }
        }
        return out;
    }

    public Battle getBattle() {
        return battle;
    }

    public RobotClassManager getRobotClassManager() {
        return robotClassManager;
    }

    public RobotFileSystemManager getRobotFileSystemManager() {
        return robotFileSystemManager;
    }

    public RobotThreadManager getRobotThreadManager() {
        return robotThreadManager;
    }

    public double getBattleFieldHeight() {
        return battleField.getHeight();
    }

    public double getBattleFieldWidth() {
        return battleField.getWidth();
    }
}