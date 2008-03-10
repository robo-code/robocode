/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Code cleanup
 *     - Bugfix: updateMovement() checked for distanceRemaining > 1 instead of
 *       distanceRemaining > 0 if slowingDown and moveDirection == -1
 *     - Bugfix: Substituted wait(10000) with wait() in execute() method, so
 *       that robots do not hang when game is paused
 *     - Bugfix: Teleportation when turning the robot to 0 degrees while forcing
 *       the robot towards the bottom
 *     - Added setPaintEnabled() and isPaintEnabled()
 *     - Added setSGPaintEnabled() and isSGPaintEnabled()
 *     - Replaced the colorIndex with bodyColor, gunColor, and radarColor
 *     - Replaced the setColors() with setBodyColor(), setGunColor(), and
 *       setRadarColor()
 *     - Added bulletColor, scanColor, setBulletColor(), and setScanColor() and
 *       removed getColorIndex()
 *     - Optimizations
 *     - Ported to Java 5
 *     - Bugfix: HitRobotEvent.isMyFault() returned false despite the fact that
 *       the robot was moving toward the robot it collides with. This was the
 *       case when distanceRemaining == 0
 *     - Removed isDead field as the robot state is used as replacement
 *     - Added isAlive() method
 *     - Added constructor for creating a new robot with a name only
 *     - Added the set() that copies a RobotRecord into this robot in order to
 *       support the replay feature
 *     - Fixed synchronization issues with several member fields
 *     - Added features to support the new JuniorRobot class
 *     - Added cleanupStaticFields() for clearing static fields on robots
 *     - Added getMaxTurnRate()
 *     - Added turnAndMove() in order to support the turnAheadLeft(),
 *       turnAheadRight(), turnBackLeft(), and turnBackRight() for the
 *       JuniorRobot, which moves the robot in a perfect curve that follows a
 *       circle
 *     - Changed the behaviour of checkRobotCollision() so that HitRobotEvents
 *       are only created and sent to robot when damage do occur. Previously, a
 *       robot could receive HitRobotEvents even when no damage was done
 *     Luis Crespo
 *     - Added states
 *     Titus Chen
 *     - Bugfix: Hit wall and teleporting problems with checkWallCollision()
 *     Robert D. Maupin
 *     - Replaced old collection types like Vector and Hashtable with
 *       synchronizet List and HashMap
 *     Nathaniel Troutman
 *     - Added cleanup() method for cleaning up references to internal classes
 *       to prevent circular references causing memory leaks
 *     Pavel Savara
 *     - Re-work of robot interfaces
 *******************************************************************************/
package robocode.peer;


import robocode.peer.robot.*;
import robocode.peer.data.RobotPeerInfo;
import robocode.peer.data.RobotPeerStatus;
import robocode.peer.data.RobotPeerCommands;
import robocode.peer.views.*;
import robocode.robotinterfaces.peer.IBasicRobotView;
import robocode.robotinterfaces.IBasicRobot;
import robocode.battlefield.DefaultBattleField;
import robocode.battle.Battle;
import robocode.repository.RobotFileSpecification;

import java.security.AccessControlException;


/**
 * RobotPeer is an object that deals with game mechanics and rules, and makes
 * sure that robots abides the rules.
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Luis Crespo (contributor)
 * @author Titus Chen (contributor)
 * @author Robert D. Maupin (contributor)
 * @author Nathaniel Troutman (contributor)
 * @author Pavel Savara (contributor)
 */
public class RobotPeer extends RobotPeerSync implements IContestantPeer, IRobotPeer, IRobotRobotPeer, IBattleRobotPeer, IDisplayRobotPeer
{
    //data
    private RobotPeerInfo info;
    private RobotPeerStatus status;
    private RobotPeerCommands commands;

    //view
    private IBasicRobotView robotView;
    private IBattleRobotView battleView;
    private IDisplayRobotView displayView;
    private IRobotRunnableView robotRunableView;

    //components
    private RobotOutputStream out;
    private IBasicRobot robot;
    private Battle battle;
    private RobotClassManager robotClassManager;
    private RobotFileSystemManager robotFileSystemManager;
    private RobotThreadManager robotThreadManager;
    private RobotMessageManager robotMessageManager;
    private EventManager robotEventManager;

    public RobotPeer(RobotClassManager robotClassManager, long fileSystemQuota) {
        super();

        //dummy
        this.battle = new Battle(new DefaultBattleField(800, 600), null);

        //data
        info=new RobotPeerInfo();
        status=new RobotPeerStatus();
        commands=new RobotPeerCommands();

        //views

        //components
        this.robotClassManager = robotClassManager;
        this.robotThreadManager = new RobotThreadManager(this);
        this.robotFileSystemManager = new RobotFileSystemManager(this, fileSystemQuota);
        this.robotEventManager =new EventManager(this);
    }

    public void setBattle(Battle newBattle) {
        battle = newBattle;
    }

    public void setRobot(IBasicRobot newRobot) {
       robot = newRobot;
       if (robot != null) {
           if (info.isTeamRobot()) {
               robotMessageManager= new RobotMessageManager(this);
           }
       }
       robotEventManager.setRobot(newRobot);
   }

   public void setInfo(RobotFileSpecification rfs){
       info.setupInfo(rfs,this);
       status.setupInfo(this);
       commands.setupInfo(this);
       if (info.isTeamRobot()) {
           robotView =new TeamRobotView(this);
           info.setupTeam(robotClassManager.getTeamManager());
       }
       else if (info.isAdvancedRobot()) {
           robotView =new AdvancedRobotView(this);
       }
       else if (info.isInteractiveRobot()) {
           robotView =new StandardRobotView(this);
       }
       else if (info.isJuniorRobot()) {
           robotView =new JuniorRobotView(this);
       }
       else {
            throw new AccessControlException("Unknown robot type");
       }
   }

    public void cleanup() {
        //data
        info.cleanup();
        status.cleanup();
        commands.cleanup();

        //view
        ((BasicRobotView)robotView).cleanup();
        robotRunableView.cleanup();
        displayView.cleanup();
        battleView.cleanup();

        //components
        robot=null;
        out=null;
        battle=null;

        if (robotEventManager!=null){
            robotEventManager.cleanup();
        }
        robotEventManager=null;
        
        if (robotMessageManager!=null){
            robotMessageManager.cleanup();
        }
        robotMessageManager=null;

        if (robotClassManager != null) {
            robotClassManager.cleanup();
        }
        robotClassManager = null;

        robotFileSystemManager = null;
        robotThreadManager = null;
    }


    //data
    public RobotPeerInfo getInfo() {
        return info;
    }

    public RobotPeerStatus getStatus() {
        return status;
    }

    public RobotPeerCommands getCommands() {
        return commands;
    }

    //views
    public IRobotRunnableView getRobotRunnableView() {
        return robotRunableView;
    }

    public IDisplayRobotView getDisplayView() {
        return displayView;
    }

    public IBattleRobotView getBattleView() {
        return battleView;
    }

    public IBasicRobotView getRobotView() {
        return robotView;
    }

    public void run() {
        robotRunableView.run();
    }

    //components
    public RobotOutputStream getOut() {
        return out;
    }

    public IBasicRobot getRobot() {
        return robot;
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

    public RobotMessageManager getRobotMessageManager() {
        return robotMessageManager;
    }

    public IRobotEventManager getRobotEventManager() {
        return robotEventManager;
    }

    public IDisplayEventManager getDisplayEventManager() {
        return robotEventManager;
    }

    public IBattleEventManager getBattleEventManager() {
        return robotEventManager;
    }

    //security
    public void forceStop(){
        //intentionaly not synchronized to prevent block from user code
        status.setRunning(false);
        status.getStatistics().setInactive();
    }

    public void forceUncharge(){
        //intentionaly not synchronized to prevent block from user code
        status.setEnergy(0);
    }

    public String getName() {
        //intentionaly not synchronized to prevent block from user code
        return info.getName();
    }


    //IContestant
    public IContestantStatistics getRobotStatistics() {
        return status.getStatistics();
    }

    public int compareTo(IContestantPeer cp) {
		double score1 = getRobotStatistics().getTotalScore();
		double score2 = cp.getRobotStatistics().getTotalScore();

		if (getBattle().isRunning()) {
			score1 += getRobotStatistics().getCurrentScore();
			score2 += cp.getRobotStatistics().getCurrentScore();
		}
		return (int) (score2 + 0.5) - (int) (score1 + 0.5);
	}
}
