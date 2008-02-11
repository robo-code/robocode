/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * This sample is showing how to implement robot with robot interfaces rather than
 * on top of standard base classes only.
 * Slave derived from standard base class is there there only to forward calls to RobotPeer.
 * MasterBase is just helper base class.
 * Master is final robot.
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package sample;

import robocode.robotinterfaces.*;
import robocode.robotinterfaces.IAdvancedEvents;
import robocode.robotinterfaces.ISystemEvents;
import robocode.robotinterfaces.IBasicEvents;
import robocode.peer.RobotPeer;
import robocode.ScannedRobotEvent;
import robocode.HitByBulletEvent;
import robocode.AdvancedRobot;

import java.io.PrintStream;

public class MasterAndSlave extends MasterBase implements IAdvancedRobot {

    /**
     * This is not showing any aditional qualities over normal MyFirst robot.
     * But it could, because architecture is no more tied by inheritance from Robot base class.
     */
    public void run()
    {
        while(true) {
            ahead(100); // Move ahead 100
            turnGunRight(360); // Spin gun around
            back(100); // Move back 100
            turnGunRight(360); // Spin gun around
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        fire(1);
    }

    public void onHitByBullet(HitByBulletEvent e) {
        turnLeft(90 - e.getBearing());
    }
}

/**
 * Infrastructure base class, for helpers and boring implementation details
 */
abstract class MasterBase {

    public MasterBase()
    {
        helperRobot=new Slave(this);
    }

    private AdvancedRobot helperRobot;
    public IAdvancedEvents getAdvancedEventListener() {
        return helperRobot;
    }

    public ISystemEvents getSystemEventListener() {
        return helperRobot;
    }

    public Runnable getRobotRunnable() {
        return helperRobot;
    }

    public IBasicEvents getBasicEventListener() {
        return helperRobot;
    }

    public void setPeer(RobotPeer robotPeer) {
        helperRobot.setPeer(robotPeer);
    }

    public RobotPeer getPeer() {
        return helperRobot.getPeer();
    }

    public void setOut(PrintStream printStream) {
        helperRobot.setOut(printStream);
    }

    public PrintStream getOut() {
        return helperRobot.getOut(); 
    }

    public void turnGunRight(double degrees) {
        helperRobot.turnGunRight(degrees);
    }

    public void turnLeft(double degrees) {
        helperRobot.turnLeft(degrees);
    }

    public void ahead(double distance) {
        helperRobot.ahead(distance);
    }

    public void back(double distance) {
        helperRobot.back(distance);
    }

    public void fire(double power) {
        helperRobot.fire(power);
    }

    public void onScannedRobot(ScannedRobotEvent e) { }
    public void onHitByBullet(HitByBulletEvent e) { }
    public void run() { }
}

/**
 * This is robot derived from AdvancedRobot.
 * Only reason to use this inheritance and this class is that external robots are unable to call RobotPeer directly
 */
class Slave extends AdvancedRobot
{
    MasterBase parent;

    public Slave(MasterBase parent)
    {
        this.parent=parent;
    }

    public void run()
    {
        parent.run();
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        parent.onScannedRobot(e);
    }

    public void onHitByBullet(HitByBulletEvent e) {
        parent.onHitByBullet(e);
    }
}
