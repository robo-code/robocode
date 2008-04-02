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
import robocode.Rules;
import robocode.exception.DeathException;
import robocode.exception.DisabledException;
import robocode.exception.RobotException;
import robocode.exception.WinException;
import robocode.peer.BulletPeer;
import robocode.peer.IRobotRobotPeer;
import robocode.peer.data.RobotPeerCommands;
import robocode.peer.data.RobotPeerInfo;
import robocode.peer.data.RobotPeerStatus;
import robocode.robotinterfaces.peer.IBasicRobotPeer;

import java.awt.*;
import static java.lang.Math.max;
import static java.lang.Math.min;


/**
 * @author Pavel Savara (original)
 */
public class BasicRobotProxy implements IBasicRobotPeer {

    IRobotRobotPeer peer;
    IRobotRunnableProxy view;
    RobotPeerStatus status;
    RobotPeerInfo info;
    RobotPeerCommands commands;

    public BasicRobotProxy(IRobotRobotPeer peer) {
        this.peer = peer;
        this.view = peer.getRobotRunnableView();
        this.info = peer.getInfo();
        this.status = peer.getStatus();
        this.commands = peer.getCommands();
    }

    public final void cleanup() {
        this.peer = null;
        this.view = null;
        this.info = null;
        this.status = null;
        this.commands = null;
    }

    public static final long
            MAX_SET_CALL_COUNT = 10000,
            MAX_GET_CALL_COUNT = 10000;

    // counters
    public final void getCall() {
        // don't care about sync
        int val = status.incGetCall();

        if (val >= MAX_GET_CALL_COUNT) {
            peer.getOut().println("SYSTEM: You have made " + val + " calls to getXX methods without calling execute()");
            throw new DisabledException("Too many calls to getXX methods");
        }
    }

    public final void setCall() {
        // don't care about sync
        int val = status.incSetCall();

        if (val >= MAX_SET_CALL_COUNT) {
            peer.getOut().println("SYSTEM: You have made " + val + " calls to setXX methods without calling execute()");
            throw new DisabledException("Too many calls to setXX methods");
        }
    }

    // AdvancedRobot calls below
    public final double getRadarTurnRemaining() {
        getCall();
        peer.lockRead();
        try {
            return commands.getRadarTurnRemaining();
        } finally {
            peer.unlockRead();
        }
    }

    public final double getDistanceRemaining() {
        getCall();
        peer.lockRead();
        try {
            return commands.getDistanceRemaining();
        } finally {
            peer.unlockRead();
        }
    }

    public final double getBodyTurnRemaining() {
        getCall();
        peer.lockRead();
        try {
            return commands.getBodyTurnRemaining();
        } finally {
            peer.unlockRead();
        }
    }

    // Robot calls below
    public final double getVelocity() {
        getCall();
        peer.lockRead();
        try {
            return status.getVelocity();
        } finally {
            peer.unlockRead();
        }
    }

    public final double getRadarHeading() {
        getCall();
        peer.lockRead();
        try {
            return status.getRadarHeading();
        } finally {
            peer.unlockRead();
        }
    }

    public final double getGunCoolingRate() {
        getCall();
        peer.lockRead();
        try {
            return view.getGunCoolingRate();
        } finally {
            peer.unlockRead();
        }
    }

    public final String getName() {
        getCall();
        peer.lockRead();
        try {
            return info.getName();
        } finally {
            peer.unlockRead();
        }
    }

    public final long getTime() {
        getCall();
        peer.lockRead();
        try {
            return view.getTime();
        } finally {
            peer.unlockRead();
        }
    }

    // Junior calls below
    public final double getBodyHeading() {
        getCall();
        peer.lockRead();
        try {
            return status.getBodyHeading();
        } finally {
            peer.unlockRead();
        }
    }

    public final double getGunHeading() {
        getCall();
        peer.lockRead();
        try {
            return status.getGunHeading();
        } finally {
            peer.unlockRead();
        }
    }

    public final double getGunTurnRemaining() {
        getCall();
        peer.lockRead();
        try {
            return commands.getGunTurnRemaining();
        } finally {
            peer.unlockRead();
        }
    }

    public final double getEnergy() {
        getCall();
        peer.lockRead();
        try {
            return status.getEnergy();
        } finally {
            peer.unlockRead();
        }
    }

    public final double getGunHeat() {
        getCall();
        peer.lockRead();
        try {
            return status.getGunHeat();
        } finally {
            peer.unlockRead();
        }
    }

    public final double getBattleFieldHeight() {
        getCall();
        peer.lockRead();
        try {
            return view.getBattleFieldHeight();
        } finally {
            peer.unlockRead();
        }
    }

    public final double getBattleFieldWidth() {
        getCall();
        peer.lockRead();
        try {
            return view.getBattleFieldWidth();
        } finally {
            peer.unlockRead();
        }
    }

    public final double getX() {
        getCall();
        peer.lockRead();
        try {
            return status.getX();
        } finally {
            peer.unlockRead();
        }
    }

    public final double getY() {
        getCall();
        peer.lockRead();
        try {
            return status.getY();
        } finally {
            peer.unlockRead();
        }
    }

    public final int getOthers() {
        getCall();
        peer.lockRead();
        try {
            return view.getOthers();
        } finally {
            peer.unlockRead();
        }
    }

    public final int getNumRounds() {
        getCall();
        peer.lockRead();
        try {
            return view.getNumRounds();
        } finally {
            peer.unlockRead();
        }
    }

    public final int getRoundNum() {
        getCall();
        peer.lockRead();
        try {
            return view.getRoundNum();
        } finally {
            peer.unlockRead();
        }
    }

    public Bullet fire(double power) {
        Bullet bullet = setFire(power);

        execute();
        return bullet;
    }

    // asynchronous actions
    public Bullet setFire(double power) {
        setCall();
        peer.lockWrite();
        try {
            if (Double.isNaN(power)) {
                peer.getOut().println("SYSTEM: You cannot call fire(NaN)");
                return null;
            }
            if (status.getGunHeat() > 0 || status.getEnergy() == 0) {
                return null;
            }

            double firePower = min(status.getEnergy(), min(max(power, Rules.MIN_BULLET_POWER), Rules.MAX_BULLET_POWER));

            this.status.setEnergy(status.getEnergy() - firePower);

            // ZAMO: moved to battle thread: gunHeat += Rules.getGunHeat(firePower); addBullet();
            BulletPeer bullet = new BulletPeer(peer.getBattleProxy(), peer.getBattle());

            bullet.setPower(firePower);
            bullet.setVelocity(Rules.getBulletSpeed(firePower));
            if (commands.isFireAssistValid()) {
                bullet.setHeading(commands.getFireAssistAngle());
            } else {
                bullet.setHeading(status.getGunHeading());
            }
            bullet.setX(status.getX());
            bullet.setY(status.getY());

            commands.setCurrentBullet(bullet);

            return bullet.getBullet();
        } finally {
            peer.unlockWrite();
        }
    }

    // blocking actions
    public final void execute() {
        execImplementation();
    }

    public final void move(double distance) {
        peer.checkNoLock();

        setMoveImplementation(distance);
        do {
            execute(); // Always tick at least once
        } while (commands.getDistanceRemainingSync() != 0);
    }

    public final void turnBody(double radians) {
        peer.checkNoLock();

        setTurnBodyImplementation(radians);
        do {
            execute(); // Always tick at least once
        } while (commands.getTurnRemainingSync() != 0);
    }

    public final void turnGun(double radians) {
        peer.checkNoLock();

        setTurnGunImplementation(radians);
        do {
            execute(); // Always tick at least once
        } while (commands.getGunTurnRemainingSync() != 0);
    }

    // fast setters
    public final void setBodyColor(Color color) {
        setCall();
        peer.lockWrite();
        try {
            status.setBodyColor(color);
        } finally {
            peer.unlockWrite();
        }
    }

    public final void setGunColor(Color color) {
        setCall();
        peer.lockWrite();
        try {
            status.setGunColor(color);
        } finally {
            peer.unlockWrite();
        }
    }

    public final void setRadarColor(Color color) {
        setCall();
        peer.lockWrite();
        try {
            status.setRadarColor(color);
        } finally {
            peer.unlockWrite();
        }
    }

    public final void setBulletColor(Color color) {
        setCall();
        peer.lockWrite();
        try {
            status.setBulletColor(color);
        } finally {
            peer.unlockWrite();
        }
    }

    public final void setScanColor(Color color) {
        setCall();
        peer.lockWrite();
        try {
            status.setScanColor(color);
        } finally {
            peer.unlockWrite();
        }
    }

    public static void death() {
        throw new DeathException();
    }

    // // // //  // // // // // // // // // // // // // // // // // // // // // // // //
    // private implementations
    // // // //  // // // // // // // // // // // // // // // // // // // // // // // //

    protected final void setTurnBodyImplementation(double radians) {
        peer.lockWrite();
        try {
            if (status.getEnergy() > 0) {
                commands.setBodyTurnRemaining(radians);
            }
        } finally {
            peer.unlockWrite();
        }
    }

    protected final void setTurnGunImplementation(double radians) {
        peer.lockWrite();
        try {
            commands.setGunTurnRemaining(radians);
        } finally {
            peer.unlockWrite();
        }
    }

    protected final void setMoveImplementation(double distance) {
        peer.lockWrite();
        try {
            if (status.getEnergy() == 0) {
                return;
            }
            commands.setDistanceRemaining(distance);
            commands.setAcceleration(0);

            if (distance == 0) {
                commands.setMoveDirection(0);
            } else if (distance > 0) {
                commands.setMoveDirection(1);
            } else {
                commands.setMoveDirection(-1);
            }
            commands.setSlowingDown(false);
        } finally {
            peer.unlockWrite();
        }
    }

    protected final void turnAndMoveImplementation(double distance, double radians) {
        peer.checkNoLock();

        if (distance == 0) {
            turnBody(radians);
            return;
        }

        int turns;
        final double savedMaxVelocity;
        final double savedMaxTurnRate;
        final double absDistance;

        peer.lockWrite();
        try {

            // Save current max. velocity and max. turn rate so they can be restored
            savedMaxVelocity = commands.getMaxVelocity();
            savedMaxTurnRate = commands.getMaxTurnRate();

            final double absDegrees = Math.abs(Math.toDegrees(radians));

            absDistance = Math.abs(distance);

            // -- Calculate max. velocity for moving perfect in a circle --

            // maxTurnRate = 10 * 0.75 * velocity  (Robocode rule), and
            // maxTurnRate = velocity * degrees / distance  (curve turn rate)
            //
            // Hence, max. velocity = 10 / (degrees / distance + 0.75)

            final double maxVelocity = Math.min(Rules.MAX_VELOCITY, 10 / (absDegrees / absDistance + 0.75));

            // -- Calculate number of turns for acceleration + deceleration --

            double accDist = 0; // accumulated distance during acceleration
            double decDist = 0; // accumulated distance during deceleration

            turns = 0; // number of turns to it will take to move the distance

            // Calculate the amount of turn it will take to accelerate + decelerate
            // up to the max. velocity, but stop if the distance for used for
            // acceleration + deceleration gets bigger than the total distance to move
            for (int t = 1; t < maxVelocity; t++) {

                // Add the current velocity to the acceleration distance
                accDist += t;

                // Every 2nd time we add the deceleration distance needed to
                // get to a velocity of 0
                if (t > 2 && (t % 2) > 0) {
                    decDist += t - 2;
                }

                // Stop if the acceleration + deceleration > total distance to move
                if ((accDist + decDist) >= absDistance) {
                    break;
                }

                // Increment turn for acceleration
                turns++;

                // Every 2nd time we increment time for deceleration
                if (t > 2 && (t % 2) > 0) {
                    turns++;
                }
            }

            // Add number of turns for the remaining distance at max velocity
            if ((accDist + decDist) < absDistance) {
                turns += (int) ((absDistance - accDist - decDist) / maxVelocity + 1);
            }

            // -- Move and turn in a curve --

            // Set the calculated max. velocity
            commands.setMaxVelocity(maxVelocity);

            // Set the robot to move the specified distance
            setMoveImplementation(distance);
            // Set the robot to turn its body to the specified amount of radians
            setTurnBodyImplementation(radians);
        } finally {
            peer.unlockWrite();
        }

        // Loop thru the number of turns it will take to move the distance and adjust
        // the max. turn rate so it fit the current velocity of the robot
        for (int t = turns; t >= 0; t--) {
            peer.lockWrite();
            try {
                commands.setMaxTurnRate(status.getVelocitySync() * radians / absDistance);
            } finally {
                peer.unlockWrite();
            }
            execute(); // Perform next turn
        }

        peer.lockWrite();
        try {
            // Restore the saved max. velocity and max. turn rate
            commands.setMaxVelocity(savedMaxVelocity);
            commands.setMaxTurnRate(savedMaxTurnRate);
        } finally {
            peer.unlockWrite();
        }
    }

    protected final void execImplementation() {
        // Entering tick
        if (Thread.currentThread() != peer.getRobotThreadManager().getRunThread()) {
            throw new RobotException("You cannot take action in this thread!");
        }

        commands.checkNoLock();

        initializeExec();

        // Notifying battle that we're asleep
        synchronized (peer.getSyncRoot()) {
            peer.getSyncRoot().notifyAll();

            // Sleeping and waiting for battle to wake us up.
            try {
                peer.getSyncRoot().wait();
            } catch (InterruptedException e) {// We are expecting this to happen when a round is ended!
            }
        }

        finalizeExec();
    }

    private void initializeExec() {
        peer.lockWrite();
        try {
            if (commands.isTestingCondition()) {
                throw new RobotException(
                        "You cannot take action inside Condition.test().  You should handle onCustomEvent instead.");
            }

            status.setSetCallCount(0);
            status.setGetCallCount(0);

            // This stops autoscan from scanning...
            if (commands.getWaitCondition() != null && commands.getWaitCondition().test()) {
                commands.setWaitCondition(null);
            }

            // If we are stopping, yet the robot took action (in onWin or onDeath), stop now.
            if (commands.isStopping()) {
                if (status.isDead()) {
                    death();
                } else if (status.isWinner()) {
                    throw new WinException();
                }
            }

            // Notify the battle that we are now asleep.
            // This ends any pending wait() call in battle.runRound().
            // Should not actually take place until we release the lock in wait(), below.
            status.setSleeping(true);
        } finally {
            peer.unlockWrite();
        }
    }

    private void finalizeExec() {
        peer.lockWrite();
        try {
            status.setSleeping(false);
            // Notify battle thread, which is waiting in
            // our wakeup() call, to return.
            // It's quite possible, by the way, that we'll be back in sleep (above)
            // before the battle thread actually wakes up
            synchronized (peer.getSyncRoot()) {
                peer.getSyncRoot().notifyAll();
            }

            commands.setFireAssistValid(false);

            if (status.isDead()) {
                commands.setStopping(true);
            }

            // Out's counter must be reset before processing event.
            // Otherwise, it will not be reset when printing in the onScannedEvent()
            // before a scan() call, which will potentially cause a new onScannedEvent()
            // and therefore not be able to reset the counter.
            peer.getOut().resetCounter();

            peer.getRobotEventManager().processEvents();
        } finally {
            peer.unlockWrite();
        }
    }
}
