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

import robocode.exception.RobotException;
import robocode.exception.WinException;
import robocode.exception.DisabledException;
import robocode.exception.DeathException;
import robocode.Rules;
import robocode.Bullet;
import robocode.ScannedRobotEvent;
import robocode.Condition;
import static robocode.io.Logger.log;
import robocode.robotinterfaces.peer.IBasicRobotPeer;
import static robocode.util.Utils.normalAbsoluteAngle;
import static robocode.util.Utils.normalRelativeAngle;
import robocode.peer.robot.IRobotEventManager;

import static java.lang.Math.min;
import static java.lang.Math.max;
import static java.lang.Math.PI;
import static java.lang.Math.atan2;
import static java.lang.Math.abs;
import java.util.List;
import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;
import java.security.AccessControlException;

/**
 * @author Pavel Savara (original)
 */
public class R60obotPeerRobotCommands extends R32obotPeerTeam {

    private IBasicRobotPeer robotProxy;

    public final void run() {
        boolean uncharge=false;
        try {
            Runnable runnable = initializeRun();
            if (runnable != null) {
                runnable.run();
            }
            for (;;) {
                execute();
            }
        } catch (DeathException e) {
            getOut().println("SYSTEM: " + info.getName() + " has died");
        } catch (WinException e) {
            ; // Do nothing
        } catch (DisabledException e) {
            uncharge=true;
            String msg = e.getMessage();

            if (msg == null) {
                msg = "";
            } else {
                msg = ": " + msg;
            }
            getOut().println("SYSTEM: Robot disabled" + msg);
        } catch (Exception e) {
            getOut().println(info.getName() + ": Exception: " + e);
            getOut().printStackTrace(e);
        } catch (Throwable t) {
            if (!(t instanceof ThreadDeath)) {
                getOut().println(info.getName() + ": Throwable: " + t);
                getOut().printStackTrace(t);
            } else {
                log(info.getName() + " stopped successfully.");
            }
        }
        finalizeRun(uncharge);
    }

    private Runnable initializeRun() {
        lockWrite();
        try{
            Runnable runnable = null;
            if (getRobot() != null) {
                getRobot().setOut(getOut());
                getRobot().setPeer(getRobotProxy());

                // Process all events for the first turn.
                // This is done as the first robot status event must occur before the robot
                // has started running.
                getRobotEventManager().processEvents();

                runnable = getRobot().getRobotRunnable();

            }
            info.setRunning(true);
            return runnable;
        }
        finally {
            unlockWrite();
        }
    }

    private void finalizeRun(boolean uncharge) {
        lockWrite();
        try{
            if (uncharge){
                info.setEnergy(0);
            }
            info.setRunning(false);
        }
        finally {
            unlockWrite();
        }

        // If battle is waiting for us, well, all done!
        synchronized (getSyncRoot()){
            getSyncRoot().notifyAll();
        }
    }

    public final void execute() {
        // Entering tick
        if (Thread.currentThread() != getRobotThreadManager().getRunThread()) {
            throw new RobotException("You cannot take action in this thread!");
        }

        checkNoLock();

        initializeExec();

        // Notifying battle that we're asleep
        synchronized (getSyncRoot()){
            getSyncRoot().notifyAll();
        }

        // Sleeping and waiting for battle to wake us up.
        try {
            wait();
        } catch (InterruptedException e) {
            ; // We are expecting this to happen when a round is ended!
        }

        finalizeExcec();
    }

    private void initializeExec() {
        lockWrite();
        try{
            if (info.isTestingCondition()) {
                throw new RobotException(
                        "You cannot take action inside Condition.test().  You should handle onCustomEvent instead.");
            }

            info.setSetCallCount(0);
            info.setGetCallCount(0);

            // This stops autoscan from scanning...
            if (info.getWaitCondition() != null && info.getWaitCondition().test()) {
                info.setWaitCondition(null);
            }

            // If we are stopping, yet the robot took action (in onWin or onDeath), stop now.
            if (info.isStopping()) {
                if (info.isDead()) {
                    death();
                } else if (info.isWinner()) {
                    throw new WinException();
                }
            }

            // Notify the battle that we are now asleep.
            // This ends any pending wait() call in battle.runRound().
            // Should not actually take place until we release the lock in wait(), below.
            info.setSleeping(true);
        }
        finally {
            unlockWrite();
        }
    }

    private void finalizeExcec() {
        lockWrite();
        try{
            info.setSleeping(false);
            // Notify battle thread, which is waiting in
            // our wakeup() call, to return.
            // It's quite possible, by the way, that we'll be back in sleep (above)
            // before the battle thread actually wakes up
            synchronized (getSyncRoot()){
                getSyncRoot().notifyAll();
            }

            info.setFireAssistValid(false);

            if (info.isDead()) {
                info.setStopping(true);
            }

            // Out's counter must be reset before processing event.
            // Otherwise, it will not be reset when printing in the onScannedEvent()
            // before a scan() call, which will potentially cause a new onScannedEvent()
            // and therefore not be able to reset the counter.
            getOut().resetCounter();

            getRobotEventManager().processEvents();
        }
        finally {
            unlockWrite();
        }
    }

    public final void stop(boolean overwrite) {
        checkNoLock();

        setStop(overwrite);
        execute();
    }

    public final void resume() {
        checkNoLock();

        setResume();
        execute();
    }

    public final void waitFor(Condition condition) {
        checkNoLock();

        info.setWaitConditionSync(condition);
        do {
            execute(); // Always tick at least once
        } while (!condition.test());
        info.setWaitConditionSync(null);
    }

    public final void move(double distance) {
        checkNoLock();

        setMove(distance);
        do {
            execute(); // Always tick at least once
        } while (info.getDistanceRemainingSync() != 0);
    }

    public final void scanReset() {
        checkNoLock();

        boolean reset = false;
        boolean resetValue = false;

        IRobotEventManager robotEventManager = getRobotEventManager();
        if (robotEventManager.getCurrentTopEventPriority() == robotEventManager.getScannedRobotEventPriority()) {
            reset = true;
            resetValue = robotEventManager.getInterruptible(robotEventManager.getScannedRobotEventPriority());
            robotEventManager.setInterruptible(robotEventManager.getScannedRobotEventPriority(), true);
        }

        info.setScanSync(true);
        execute();
        if (reset) {
            robotEventManager.setInterruptible(robotEventManager.getScannedRobotEventPriority(), resetValue);
        }
    }

    public final void turnAndMove(double distance, double radians) {
        checkNoLock();

		if (distance == 0) {
			turnBody(radians);
			return;
		}

        int turns;
        final double savedMaxVelocity;
        final double savedMaxTurnRate;
        final double absDistance;

        lockWrite();
        try{

            // Save current max. velocity and max. turn rate so they can be restored
            savedMaxVelocity = info.getMaxVelocity();
            savedMaxTurnRate = info.getMaxTurnRate();

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
            info.setMaxVelocity(maxVelocity);

            // Set the robot to move the specified distance
            setMove(distance);
            // Set the robot to turn its body to the specified amount of radians
            setTurnBody(radians);
        }
        finally {
            unlockWrite();
        }

        // Loop thru the number of turns it will take to move the distance and adjust
		// the max. turn rate so it fit the current velocity of the robot
		for (int t = turns; t >= 0; t--) {
			info.setMaxTurnRate(info.getVelocitySync() * radians / absDistance);
			execute(); // Perform next turn
		}

        lockWrite();
        try{
            // Restore the saved max. velocity and max. turn rate
            info.setMaxVelocity(savedMaxVelocity);
            info.setMaxTurnRate(savedMaxTurnRate);
        }
        finally {
            unlockWrite();
        }
	}

    public final void turnBody(double radians) {
        checkNoLock();

        setTurnBody(radians);
        do {
            execute(); // Always tick at least once
        } while (info.getTurnRemainingSync() != 0);
    }

    public final void turnGun(double radians) {
        checkNoLock();

        setTurnGun(radians);
        do {
            execute(); // Always tick at least once
        } while (info.getGunTurnRemainingSync() != 0);
    }

    public final void turnRadar(double radians) {
        checkNoLock();

        setTurnRadar(radians);
        do {
            execute(); // Always tick at least once
        } while (info.getRadarTurnRemainingSync() != 0);
    }

    public final void setMove(double distance) {
        lockWrite();
        try{
            if (info.getEnergy() == 0) {
                return;
            }
            info.setDistanceRemaining(distance);
            info.setAcceleration(0);

            if (distance == 0) {
                info.setMoveDirection(0);
            } else if (distance > 0) {
                info.setMoveDirection(1);
            } else {
                info.setMoveDirection(-1);
            }
            info.setSlowingDown(false);
        }
        finally{
            unlockWrite();
        }
    }

    public final Bullet setFire(double power) {
        lockWrite();
        try{
            if (Double.isNaN(power)) {
                getOut().println("SYSTEM: You cannot call fire(NaN)");
                return null;
            }
            if (info.getGunHeat() > 0 || info.getEnergy() == 0) {
                return null;
            }

            double firePower = min(info.getEnergy(), min(max(power, Rules.MIN_BULLET_POWER), Rules.MAX_BULLET_POWER));

            this.info.setEnergy(info.getEnergy() - firePower);

            //ZAMO: moved to battle thread: gunHeat += Rules.getGunHeat(firePower); addBullet();
            BulletPeer bullet = new BulletPeer((IBattleRobotPeer)this, getBattle());

            bullet.setPower(firePower);
            bullet.setVelocity(Rules.getBulletSpeed(firePower));
            if (info.isFireAssistValid()) {
                bullet.setHeading(info.getFireAssistAngle());
            } else {
                bullet.setHeading(info.getGunHeading());
            }
            bullet.setX(info.getX());
            bullet.setY(info.getY());

            info.setCurrentBullet(bullet);

            return bullet.getBullet();
        }
        finally{
            unlockWrite();
        }
    }

    public final void setTurnBody(double radians) {
        lockWrite();
        try{
            if (info.getEnergy() > 0) {
                info.setTurnRemaining(radians);
            }
        }
        finally{
            unlockWrite();
        }
    }

    public final void setTurnGun(double radians) {
        lockWrite();
        try{
            info.setGunTurnRemaining(radians);
        }
        finally{
            unlockWrite();
        }
    }

    public final void setTurnRadar(double radians) {
        lockWrite();
        try{
            info.setRadarTurnRemaining(radians);
        }
        finally{
            unlockWrite();
        }
    }

    public final void setResume() {
        lockWrite();
        try{
            if (info.isStopped()) {
                info.setStopped(false);
                info.setDistanceRemaining(info.getSaveDistanceToGo());
                info.setTurnRemaining(info.getSaveAngleToTurn());
                info.setGunTurnRemaining(info.getSaveGunAngleToTurn());
                info.setRadarTurnRemaining(info.getSaveRadarAngleToTurn());
            }
        }
        finally{
            unlockWrite();
        }
    }

    public final void setStop(boolean overwrite) {
        lockWrite();
        try{
            if (!info.isStopped() || overwrite) {
                info.setSaveDistanceToGo(info.getDistanceRemaining());
                info.setSaveAngleToTurn(info.getTurnRemaining());
                info.setSaveGunAngleToTurn(info.getGunTurnRemaining());
                info.setSaveRadarAngleToTurn(info.getRadarTurnRemaining());
            }
            info.setStopped(true);
            info.setDistanceRemaining(0);
            info.setTurnRemaining(0);
            info.setGunTurnRemaining(0);
            info.setRadarTurnRemaining(0);
        }
        finally{
            unlockWrite();
        }
    }

    public void b_setScan(boolean scan) {
        //TODO ZAMO synch
        info.setScan(scan);
    }

    public static final long
            MAX_SET_CALL_COUNT = 10000,
            MAX_GET_CALL_COUNT = 10000;

    public final void getCall() {
        //don't care about sync
        int val = info.incGetCall();
        if (val >= MAX_GET_CALL_COUNT) {
            getOut().println("SYSTEM: You have made " + val + " calls to getXX methods without calling execute()");
            throw new DisabledException("Too many calls to getXX methods");
        }
    }

    public final void setCall() {
        //don't care about sync
        int val = info.incSetCall();
        if (val >= MAX_SET_CALL_COUNT) {
            getOut().println("SYSTEM: You have made " + val + " calls to setXX methods without calling execute()");
            throw new DisabledException("Too many calls to setXX methods");
        }
    }

    public final void death() {
        throw new DeathException();
    }

    /**
     * Creates and returns a new robot peer proxy
     */
    private IBasicRobotPeer getRobotProxy() {
        if (robotProxy==null){
            if (info.isTeamRobot()) {
                robotProxy=new TeamRobotPeerProxy((RobotPeer)this);
            }
            if (info.isAdvancedRobot()) {
                robotProxy=new AdvancedRobotPeerProxy((RobotPeer)this);
            }
            if (info.isInteractiveRobot()) {
                robotProxy=new StandardRobotPeerProxy((RobotPeer)this);
            }
            if (info.isJuniorRobot()) {
                robotProxy=new JuniorRobotPeerProxy((RobotPeer)this);
            }
            throw new AccessControlException("Unknown robot type");
        }
        return robotProxy;
    }
}
