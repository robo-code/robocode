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
public class R60obotPeerRobotCommands extends R52obotPeerLast {

    private IBasicRobotPeer robotProxy;

    public void run() {
        setRunning(true);

        try {
            if (getRobot() != null) {
                getRobot().setOut(getOut());
                getRobot().setPeer(getRobotProxy());

                // Process all events for the first turn.
                // This is done as the first robot status event must occur before the robot
                // has started running.
                getRobotEventManager().processEvents();

                Runnable runnable = getRobot().getRobotRunnable();

                if (runnable != null) {
                    runnable.run();
                }
            }
            for (;;) {
                execute();
            }
        } catch (DeathException e) {
            getOut().println("SYSTEM: " + getName() + " has died");
        } catch (WinException e) {
            ; // Do nothing
        } catch (DisabledException e) {
            setEnergy(0);
            String msg = e.getMessage();

            if (msg == null) {
                msg = "";
            } else {
                msg = ": " + msg;
            }
            getOut().println("SYSTEM: Robot disabled" + msg);
        } catch (Exception e) {
            getOut().println(getName() + ": Exception: " + e);
            getOut().printStackTrace(e);
        } catch (Throwable t) {
            if (!(t instanceof ThreadDeath)) {
                getOut().println(getName() + ": Throwable: " + t);
                getOut().printStackTrace(t);
            } else {
                log(getName() + " stopped successfully.");
            }
        }

        // If battle is waiting for us, well, all done!
        synchronized (this) {
            setRunning(false);
            notifyAll();
        }
    }

    public final void execute() {
        if (getNewBullet() != null) {
            getBattle().addBullet(getNewBullet());
            setNewBullet(null);
        }

        // Entering tick
        if (Thread.currentThread() != getRobotThreadManager().getRunThread()) {
            throw new RobotException("You cannot take action in this thread!");
        }
        if (isTestingCondition()) {
            throw new RobotException(
                    "You cannot take action inside Condition.test().  You should handle onCustomEvent instead.");
        }

        setSetCallCount(0);
        setGetCallCount(0);

        // This stops autoscan from scanning...
        if (getWaitCondition() != null && getWaitCondition().test()) {
            setWaitCondition(null);
        }

        // If we are stopping, yet the robot took action (in onWin or onDeath), stop now.
        if (isHalt()) {
            if (isDead()) {
                death();
            } else if (isWinner()) {
                throw new WinException();
            }
        }

        synchronized (this) {
            // Notify the battle that we are now asleep.
            // This ends any pending wait() call in battle.runRound().
            // Should not actually take place until we release the lock in wait(), below.
            setSleeping(true);
            notifyAll();
            // Notifying battle that we're asleep
            // Sleeping and waiting for battle to wake us up.
            try {
                wait();
            } catch (InterruptedException e) {
                ; // We are expecting this to happen when a round is ended!
            }
            setSleeping(false);
            // Notify battle thread, which is waiting in
            // our wakeup() call, to return.
            // It's quite possible, by the way, that we'll be back in sleep (above)
            // before the battle thread actually wakes up
            notifyAll();
        }

        getRobotEventManager().setFireAssistValid(false);

        if (isDead()) {
            setHalt(true);
        }

        // Out's counter must be reset before processing event.
        // Otherwise, it will not be reset when printing in the onScannedEvent()
        // before a scan() call, which will potentially cause a new onScannedEvent()
        // and therefore not be able to reset the counter.
        getOut().resetCounter();

        getRobotEventManager().processEvents();
    }

    public final void stop(boolean overwrite) {
        setStop(overwrite);
        execute();
    }

    public final void waitFor(Condition condition) {
        setWaitCondition(condition);
        do {
            execute(); // Always tick at least once
        } while (!condition.test());

        setWaitCondition(null);
    }

    public final void move(double distance) {
        setMove(distance);
        do {
            execute(); // Always tick at least once
        } while (getDistanceRemaining() != 0);
    }

    public final void scanReset() {
        boolean reset = false;
        boolean resetValue = false;

        IRobotEventManager robotEventManager = getRobotEventManager();
        if (robotEventManager.getCurrentTopEventPriority() == robotEventManager.getScannedRobotEventPriority()) {
            reset = true;
            resetValue = robotEventManager.getInterruptible(robotEventManager.getScannedRobotEventPriority());
            robotEventManager.setInterruptible(robotEventManager.getScannedRobotEventPriority(), true);
        }

        setScan(true);
        execute();
        if (reset) {
            robotEventManager.setInterruptible(robotEventManager.getScannedRobotEventPriority(), resetValue);
        }
    }

    public final void scan(List<IBattleRobotPeer> robots) {
        if (isDroid()) {
            return;
        }

        double startAngle = getLastRadarHeading();
        double scanRadians = getRadarHeading() - startAngle;

        // Check if we passed through 360
        if (scanRadians < -PI) {
            scanRadians = 2 * PI + scanRadians;
        } else if (scanRadians > PI) {
            scanRadians = scanRadians - 2 * PI;
        }

        // In our coords, we are scanning clockwise, with +y up
        // In java coords, we are scanning counterclockwise, with +y down
        // All we need to do is adjust our angle by -90 for this to work.
        startAngle -= PI / 2;

        startAngle = normalAbsoluteAngle(startAngle);

        getScanArc().setArc(getX() - Rules.RADAR_SCAN_RADIUS, getY() - Rules.RADAR_SCAN_RADIUS, 2 * Rules.RADAR_SCAN_RADIUS,
                2 * Rules.RADAR_SCAN_RADIUS, 180.0 * startAngle / PI, 180.0 * scanRadians / PI, Arc2D.PIE);

        for (IBattleRobotPeer robotPeer : robots) {
            if (!(robotPeer == null || robotPeer == this || robotPeer.isDead()) && intersects(getScanArc(), robotPeer.getBoundingBox())) {
                double dx = robotPeer.getX() - getX();
                double dy = robotPeer.getY() - getY();
                double angle = atan2(dx, dy);
                double dist = Math.hypot(dx, dy);

                getBattleEventManager().add(
                        new ScannedRobotEvent(robotPeer.getName(), robotPeer.getEnergy(), normalRelativeAngle(angle - getHeading()), dist,
                        robotPeer.getHeading(), robotPeer.getVelocity()));
            }
        }
    }

    public final void turnAndMove(double distance, double radians) {
		if (distance == 0) {
			turnBody(radians);
			return;
		}

		// Save current max. velocity and max. turn rate so they can be restored
		final double savedMaxVelocity = getMaxVelocity();
		final double savedMaxTurnRate = getMaxTurnRate();

		final double absDegrees = Math.abs(Math.toDegrees(radians));
		final double absDistance = Math.abs(distance);

		// -- Calculate max. velocity for moving perfect in a circle --

		// maxTurnRate = 10 * 0.75 * velocity  (Robocode rule), and
		// maxTurnRate = velocity * degrees / distance  (curve turn rate)
		//
		// Hence, max. velocity = 10 / (degrees / distance + 0.75)

		final double maxVelocity = Math.min(Rules.MAX_VELOCITY, 10 / (absDegrees / absDistance + 0.75));

		// -- Calculate number of turns for acceleration + deceleration --

		double accDist = 0; // accumulated distance during acceleration
		double decDist = 0; // accumulated distance during deceleration

		int turns = 0; // number of turns to it will take to move the distance

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
		setMaxVelocity(maxVelocity);

		// Set the robot to move the specified distance
		setMove(distance);
		// Set the robot to turn its body to the specified amount of radians
		setTurnBody(radians);

		// Loop thru the number of turns it will take to move the distance and adjust
		// the max. turn rate so it fit the current velocity of the robot
		for (int t = turns; t >= 0; t--) {
			setMaxTurnRate(getVelocity() * radians / absDistance);
			execute(); // Perform next turn
		}

		// Restore the saved max. velocity and max. turn rate
		setMaxVelocity(savedMaxVelocity);
		setMaxTurnRate(savedMaxTurnRate);
	}

    public final void turnBody(double radians) {
        setTurnBody(radians);
        do {
            execute(); // Always tick at least once
        } while (getTurnRemaining() != 0);
    }

    public final void turnGun(double radians) {
        setTurnGun(radians);
        do {
            execute(); // Always tick at least once
        } while (getGunTurnRemaining() != 0);
    }

    public final void turnRadar(double radians) {
        setTurnRadar(radians);
        do {
            execute(); // Always tick at least once
        } while (getRadarTurnRemaining() != 0);
    }

    public final void setMove(double distance) {
        if (getEnergy() == 0) {
            return;
        }
        setDistanceRemaining(distance);
        setAcceleration(0);

        if (distance == 0) {
            setMoveDirection(0);
        } else if (distance > 0) {
            setMoveDirection(1);
        } else {
            setMoveDirection(-1);
        }
        setSlowingDown(false);
    }

    public final Bullet setFire(double power) {
        if (Double.isNaN(power)) {
            getOut().println("SYSTEM: You cannot call fire(NaN)");
            return null;
        }
        if (getGunHeat() > 0 || getEnergy() == 0) {
            return null;
        }

        double firePower = min(getEnergy(), min(max(power, Rules.MIN_BULLET_POWER), Rules.MAX_BULLET_POWER));

        this.setEnergy(getEnergy() - firePower);

        //TODO ZAMO move to battle gunHeat += Rules.getGunHeat(firePower);

        //TODO zamo battle peer from robot call ?
        BulletPeer bullet = new BulletPeer((IBattleRobotPeer)this, getBattle());

        bullet.setPower(firePower);
        bullet.setVelocity(Rules.getBulletSpeed(firePower));
        if (getRobotEventManager().isFireAssistValid()) {
            bullet.setHeading(getRobotEventManager().getFireAssistAngle());
        } else {
            bullet.setHeading(getGunHeading());
        }
        bullet.setX(getX());
        bullet.setY(getY());

        setNewBullet(bullet);

        return bullet.getBullet();
    }

    public final void setTurnBody(double radians) {
        if (getEnergy() > 0) {
            setTurnRemaining(radians);
        }
    }

    public final void setTurnGun(double radians) {
        this.setGunTurnRemaining(radians);
    }

    public final void setTurnRadar(double radians) {
        this.setRadarTurnRemaining(radians);
    }

    public final void setAdjustRadarForBodyTurn(boolean newAdjustRadarForBodyTurn) {
        isAdjustRadarForBodyTurn = newAdjustRadarForBodyTurn;
        setAdjustRadarForBodyTurnSet(true);
    }

    public final void setAdjustGunForBodyTurn(boolean newAdjustGunForBodyTurn) {
        isAdjustGunForBodyTurn = newAdjustGunForBodyTurn;
    }

    public final void setAdjustRadarForGunTurn(boolean newAdjustRadarForGunTurn) {
        isAdjustRadarForGunTurn = newAdjustRadarForGunTurn;
        if (!isAdjustRadarForBodyTurnSet()) {
            isAdjustRadarForBodyTurn = newAdjustRadarForGunTurn;
        }
    }

    public final void resume() {
        setResume();
        execute();
    }

    public final void setMaxTurnRate(double newTurnRate) {
        if (Double.isNaN(newTurnRate)) {
            getOut().println("You cannot setMaxTurnRate to: " + newTurnRate);
            return;
        }
        setMaxTurnRate(min(abs(newTurnRate), Rules.MAX_TURN_RATE_RADIANS));
    }

    public final void setMaxVelocity(double newVelocity) {
        if (Double.isNaN(newVelocity)) {
            getOut().println("You cannot setMaxVelocity to: " + newVelocity);
            return;
        }
        setMaxVelocity(min(abs(newVelocity), Rules.MAX_VELOCITY));
    }

    public final void setResume() {
        if (isStopped()) {
            setStopped(false);
            setDistanceRemaining(getSaveDistanceToGo());
            setTurnRemaining(getSaveAngleToTurn());
            setGunTurnRemaining(getSaveGunAngleToTurn());
            setRadarTurnRemaining(getSaveRadarAngleToTurn());
        }
    }

    public final void setStop(boolean overwrite) {
        if (!isStopped() || overwrite) {
            this.setSaveDistanceToGo(getDistanceRemaining());
            this.setSaveAngleToTurn(getTurnRemaining());
            this.setSaveGunAngleToTurn(getGunTurnRemaining());
            this.setSaveRadarAngleToTurn(getRadarTurnRemaining());
        }
        setStopped(true);

        this.setDistanceRemaining(0);
        this.setTurnRemaining(0);
        this.setGunTurnRemaining(0);
        this.setRadarTurnRemaining(0);
    }

    public final void setCall() {
        if (incSetCall()) {
            getOut().println("SYSTEM: You have made " + getSetCallCount() + " calls to setXX methods without calling execute()");
            throw new DisabledException("Too many calls to setXX methods");
        }
    }

    public final void getCall() {
        if (incGetCall()) {
            getOut().println("SYSTEM: You have made " + getGetCallCount() + " calls to getXX methods without calling execute()");
            throw new DisabledException("Too many calls to getXX methods");
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
            if (isTeamRobot()) {
                robotProxy=new TeamRobotPeerProxy((RobotPeer)this);
            }
            if (isAdvancedRobot()) {
                robotProxy=new AdvancedRobotPeerProxy((RobotPeer)this);
            }
            if (isInteractiveRobot()) {
                robotProxy=new StandardRobotPeerProxy((RobotPeer)this);
            }
            if (isJuniorRobot()) {
                robotProxy=new JuniorRobotPeerProxy((RobotPeer)this);
            }
            throw new AccessControlException("Unknown robot type");
        }
        return robotProxy;
    }

    private boolean intersects(Arc2D arc, Rectangle2D rect) {
        return (rect.intersectsLine(arc.getCenterX(), arc.getCenterY(), arc.getStartPoint().getX(),
                arc.getStartPoint().getY()))
                ? true
                : arc.intersects(rect);
    }
}
