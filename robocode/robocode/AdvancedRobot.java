/*******************************************************************************
 * Copyright (c) 2001, 2007 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Ported to Java 5
 *     - Updated Javadoc
 *     Robert D. Maupin
 *     - Replaced old collection types like Vector and Hashtable with
 *       synchronized List and HashMap
 *******************************************************************************/
package robocode;


import java.io.File;
import java.util.Vector;


/**
 * A more advanced type of robot than Robot that allows non-blocking calls,
 * custom events, and writes to the filesystem.
 * <p>
 * If you have not already, you should create a {@link Robot} first.
 *
 * @see Robot
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Robert D. Maupin (contributor)
 */
public class AdvancedRobot extends _AdvancedRadiansRobot {

	/**
	 * Returns the distance remaining in the robot's current move measured in
	 * pixels.
	 * <p>
	 * This call returns both positive and negative values. Positive values
	 * means that the robot is currently moving forwards. Negative values means
	 * that the robot is currently moving backwards.
	 *
	 * @return the distance remaining in the robot's current move measured in
	 *    pixels.
	 */
	public double getDistanceRemaining() {
		if (peer != null) {
			peer.getCall();
			return peer.getDistanceRemaining();
		}
		uninitializedException("getDistanceRemaining");
		return 0; // never called
	}

	/**
	 * Sets the robot to move ahead (forward) by distance measured in pixels
	 * when the next execution takes place.
	 * <p>
	 * This call returns immediately, and will not execute until you call
	 * execute() or take an action that executes.
	 * <p>
	 * Note that both positive and negative values can be given as input,
	 * where negative values means that the robot is set to move backward
	 * instead of forward.
	 * <p>
	 * Example:
	 * <pre>
	 *   // Set the robot to move 50 pixels ahead
	 *   setAhead(50);
	 *
	 *   // Set the robot to move 100 pixels back
	 *   // (overrides the previous order)
	 *   setAhead(-100);
	 *
	 *   ...
	 *   // Executes the last setAhead()
	 *   execute();
	 * </pre>
	 *
	 * @param distance the distance to move ahead measured in pixels.
	 *    If this value is negative, the robot will move back instead of ahead.
	 */
	public void setAhead(double distance) {
		if (peer != null) {
			peer.setCall();
			peer.setMove(distance);
		} else {
			uninitializedException("setAhead");
		}
	}

	/**
	 * Sets the robot to move back by distance measured in pixels when the next
	 * execution takes place.
	 * <p>
	 * This call returns immediately, and will not execute until you call
	 * execute() or take an action that executes.
	 * <p>
	 * Note that both positive and negative values can be given as input,
	 * where negative values means that the robot is set to move forward
	 * instead of backward.
	 * <p>
	 * Example:
	 * <pre>
	 *   // Set the robot to move 50 pixels back
	 *   setBack(50);
	 *
	 *   // Set the robot to move 100 pixels ahead
	 *   // (overrides the previous order)
	 *   setBack(-100);
	 *
	 *   ...
	 *   // Executes the last setBack()
	 *   execute();
	 * </pre>
	 *
	 * @param distance the distance to move back measured in pixels.
	 *    If this value is negative, the robot will move ahead instead of back.
	 */
	public void setBack(double distance) {
		if (peer != null) {
			peer.setCall();
			peer.setMove(-distance);
		} else {
			uninitializedException("setBack");
		}
	}

	/**
	 * Sets the robot's body to turn left by degrees when the next execution
	 * takes place.
	 * <p>
	 * This call returns immediately, and will not execute until you call
	 * execute() or take an action that executes.
	 * <p>
	 * Note that both positive and negative values can be given as input,
	 * where negative values means that the robot's body is set to turn right
	 * instead of left.
	 * <p>
	 * Example:
	 * <pre>
	 *   // Set the robot to turn 180 degrees to the left
	 *   setTurnLeft(180);
	 *
	 *   // Set the robot to turn 90 degrees to the right instead of left
	 *   // (overrides the previous order)
	 *   setTurnLeft(-90);
	 *
	 *   ...
	 *   // Executes the last setTurnLeft()
	 *   execute();
	 * </pre>
	 *
	 * @param degrees the amount of degrees to turn the robot's body to the left
	 *    If this value is negative, the robot's body is set to turn to the right
	 */
	public void setTurnLeft(double degrees) {
		if (peer != null) {
			peer.setCall();
			peer.setTurnChassis(-Math.toRadians(degrees));
		} else {
			uninitializedException("setTurnLeft");
		}
	}

	/**
	 * Sets the robot's body to turn right by degrees when the next execution
	 * takes place.
	 * <p>
	 * This call returns immediately, and will not execute until you call
	 * execute() or take an action that executes.
	 * <p>
	 * Note that both positive and negative values can be given as input,
	 * where negative values means that the robot's body is set to turn left
	 * instead of right.
	 * <p>
	 * Example:
	 * <pre>
	 *   // Set the robot to turn 180 degrees to the right
	 *   setTurnRight(180);
	 *
	 *   // Set the robot to turn 90 degrees to the left instead of right
	 *   // (overrides the previous order)
	 *   setTurnRight(-90);
	 *
	 *   ...
	 *   // Executes the last setTurnRight()
	 *   execute();
	 * </pre>
	 *
	 * @param degrees the amount of degrees to turn the robot's body to the right
	 *    If this value is negative, the robot's body is set to turn to the left
	 */
	public void setTurnRight(double degrees) {
		if (peer != null) {
			peer.setCall();
			peer.setTurnChassis(Math.toRadians(degrees));
		} else {
			uninitializedException("setTurnRight");
		}
	}

	/**
	 * Sets the gun to fire a bullet when the next execution takes place.
	 * The bullet will travel in the direction the gun is pointing.
	 * <p>
	 * This call returns immediately, and will not execute until you call
	 * execute() or take an action that executes.
	 * <p>
	 * The specified bullet power is an amount of energy that will be taken from
	 * the robot's energy. Hence, the more power you want to spend on the
	 * bullet, the more energy is taken from your robot.
	 * <p>
	 * The bullet will do (4 * power) damage if it hits another robot. If power
	 * is greater than 1, it will do an additional 2 * (power - 1) damage.
	 * You will get (3 * power) back if you hit the other robot. You can call
	 * Rules#getBulletDamage(double)} for getting the damage that a
	 * bullet with a specific bullet power will do.
	 * <p>
	 * The specified bullet power should be between
	 * {@link Rules#MIN_BULLET_POWER} and {@link Rules#MAX_BULLET_POWER}.
	 * <p>
	 * Note that the gun cannot fire if the gun is overheated, meaning that
	 * {@link Robot#getGunHeat()} returns a value > 0.
	 * <p>
	 * An event is generated when the bullet hits a robot, wall, or another
	 * bullet.
	 * <p>
	 * Example:
	 * <pre>
	 *   // Fire a bullet with maximum power if the gun is ready
	 *   if (getGunHeat() == 0) {
	 *       setFire(Rules.MAX_BULLET_POWER);
	 *   }
	 *   ...
	 *   execute();
	 * </pre>
	 *
	 * @param power the amount of energy given to the bullet, and subtracted
	 *     from the robot's energy.
	 *
	 * @see #setFireBullet
	 * @see Robot#fire
	 * @see Robot#fireBullet
	 * @see Robot#getGunHeat
	 * @see Robot#getGunCoolingRate
	 * @see Robot#onBulletHit
	 * @see Robot#onBulletHitBullet
	 * @see Robot#onBulletMissed
	 */
	public void setFire(double power) {
		if (peer != null) {
			peer.setCall();
			peer.setFire(power);
		} else {
			uninitializedException("setFire");
		}
	}

	/**
	 * Sets the gun to fire a bullet when the next execution takes place.
	 * The bullet will travel in the direction the gun is pointing.
	 * <p>
	 * This call returns immediately, and will not execute until you call
	 * execute() or take an action that executes.
	 * <p>
	 * The specified bullet power is an amount of energy that will be taken from
	 * the robot's energy. Hence, the more power you want to spend on the
	 * bullet, the more energy is taken from your robot.
	 * <p>
	 * The bullet will do (4 * power) damage if it hits another robot. If power
	 * is greater than 1, it will do an additional 2 * (power - 1) damage.
	 * You will get (3 * power) back if you hit the other robot. You can call
	 * {@link Rules#getBulletDamage(double)} for getting the damage that a
	 * bullet with a specific bullet power will do.
	 * <p>
	 * The specified bullet power should be between
	 * {@link Rules#MIN_BULLET_POWER} and {@link Rules#MAX_BULLET_POWER}.
	 * <p>
	 * Note that the gun cannot fire if the gun is overheated, meaning that
	 * {@link Robot#getGunHeat()} returns a value > 0.
	 * <p>
	 * An event is generated when the bullet hits a robot, wall, or another
	 * bullet.
	 * <p>
	 * Example:
	 * <pre>
	 *   // Fire a bullet with maximum power if the gun is ready
	 *   if (getGunHeat() == 0) {
	 *       Bullet bullet = setFireBullet(Rules.MAX_BULLET_POWER);
	 *   }
	 *   ...
	 *   execute();
	 *   ...
	 *   // Get the velocity of the bullet
	 *   double bulletVelocity = bullet.getVelocity();
	 * </pre>
	 *
	 * @param power the amount of energy given to the bullet, and subtracted
	 *     from the robot's energy.
	 * @return a {@link Bullet} that contains information about the bullet if it
	 *    was actually fired, which can be used for tracking the bullet after it
	 *    has been fired. If the bullet was not fired, {@code null} is returned.
	 *
	 * @see #setFire
	 * @see Bullet
	 * @see Robot#fire
	 * @see Robot#fireBullet
	 * @see Robot#getGunHeat
	 * @see Robot#getGunCoolingRate
	 * @see Robot#onBulletHit
	 * @see Robot#onBulletHitBullet
	 * @see Robot#onBulletMissed
	 */
	public Bullet setFireBullet(double power) {
		if (peer != null) {
			peer.setCall();
			return peer.setFire(power);
		}
		uninitializedException("setFireBullet");
		return null;
	}

	/**
	 * Registers a custom event to be called when a condition is met.
	 * <p>
	 * Example:
	 * <pre>
	 *   addCustomEvent(
	 *       new Condition("triggerhit") {
	 *           public boolean test() {
	 *               return (getEnergy() <= trigger);
	 *           };
	 *       }
	 *   );
	 * </pre>
	 *
	 * @param condition The condition that must be met.
	 *
	 * @see Condition
	 */
	public void addCustomEvent(Condition condition) {
		if (peer != null) {
			peer.setCall();
			peer.getEventManager().addCustomEvent(condition);
		} else {
			uninitializedException("addCustomEvent");
		}
	}

	/**
	 * Clears out any pending events immediately.
	 */
	public void clearAllEvents() {
		if (peer != null) {
			peer.setCall();
			peer.getEventManager().clearAllEvents(false);
		} else {
			uninitializedException("clearAllEvents");
		}
	}

	/**
	 * Executes any pending actions, or continues executing actions that are
	 * in process. This call returns after the actions have been started.
	 * <p>
	 * Note that Advanced robots <em>must</em> call this function in order to
	 * execute pending set<em>XXX</em> calls like e.g.
	 * {@link #setAhead(double)}, {@link #setFire(double)},
	 * {@link #setTurnLeft(double)} etc. Otherwise, these calls will never get
	 * executed.
	 * <p>
	 * In this example the robot will move while turning:
	 * <pre>
	 *   setTurnRight(90);
	 *   setAhead(100);
	 *   execute();
	 *
	 *   while (getDistanceRemaining() > 0 && getTurnRemaining() > 0) {
	 *       execute();
	 *   }
	 * </pre>
	 */
	public void execute() {
		if (peer != null) {
			peer.tick();
		} else {
			uninitializedException("execute");
		}
	}

	/**
	 * Returns a vector containing all events currently in the robot's queue.
	 * You might, for example, call this while processing another event.
	 * <p>
	 * Example:
	 * <pre>
	 *   for (Event e : getAllEvents()) {
	 *       if (e instanceof HitRobotEvent) {
	 *           // do something with e
	 *       } else if (e instanceof HitByBulletEvent) {
	 *           // do something else with e
	 *       }
	 *   }
	 * </pre>
	 *
	 * @return a vector containing all events currently in the robot's queue
	 *
	 * @see Event
	 * @see ScannedRobotEvent
	 * @see BulletHitBulletEvent
	 * @see BulletMissedEvent
	 * @see HitByBulletEvent
	 * @see HitRobotEvent
	 * @see HitWallEvent
	 * @see SkippedTurnEvent
	 * @see CustomEvent
	 * @see DeathEvent
	 * @see WinEvent
	 * @see MessageEvent
	 * @see #onScannedRobot
	 * @see #onBulletHit
	 * @see #onBulletHitBullet
	 * @see #onBulletMissed
	 * @see #onHitByBullet
	 * @see #onHitRobot
	 * @see #onHitWall
	 * @see #onSkippedTurn
	 * @see #onCustomEvent
	 * @see #onDeath
	 * @see #onRobotDeath
	 * @see #onWin
	 * @see TeamRobot#onMessageReceived
	 */
	public Vector<Event> getAllEvents() {
		if (peer != null) {
			peer.getCall();
			return new Vector<Event>(peer.getEventManager().getAllEvents());
		}
		uninitializedException("getAllEvents");
		return null; // never called
	}

	/**
	 * Returns a vector containing all BulletHitBulletEvents currently in the
	 * robot's queue. You might, for example, call this while processing another
	 * event.
	 * <p>
	 * Example:
	 * <pre>
	 *   for (BulletHitBulletEvent e : getBulletHitBulletEvents()) {
	 *      // do something with e
	 *   }
	 * </pre>
	 *
	 * @return a vector containing all BulletHitBulletEvents currently in the
	 *    robot's queue
	 *
	 * @see #onBulletHitBullet
	 * @see BulletHitBulletEvent
	 */
	public Vector<BulletHitBulletEvent> getBulletHitBulletEvents() {
		if (peer != null) {
			peer.getCall();
			return new Vector<BulletHitBulletEvent>(peer.getEventManager().getBulletHitBulletEvents());
		}
		uninitializedException("getBulletHitBulletEvents");
		return null; // never called
	}

	/**
	 * Returns a vector containing all BulletHitEvents currently in the robot's
	 * queue. You might, for example, call this while processing another event.
	 * <p>
	 * Example:
	 * <pre>
	 *   for (BulletHitEvent : getBulletHitEvents()) {
	 *      // do something with e
	 *   }
	 * </pre>
	 *
	 * @return a vector containing all BulletHitEvents currently in the robot's
	 *    queue
	 *
	 * @see #onBulletHit
	 * @see BulletHitEvent
	 */
	public Vector<BulletHitEvent> getBulletHitEvents() {
		if (peer != null) {
			peer.getCall();
			return new Vector<BulletHitEvent>(peer.getEventManager().getBulletHitEvents());
		}
		uninitializedException("getBulletHitEvents");
		return null; // never called
	}

	/**
	 * Returns a vector containing all BulletMissedEvents currently in the
	 * robot's queue. You might, for example, call this while processing another
	 * event.
	 * <p>
	 * Example:
	 * <pre>
	 *   for (BulletMissedEvent e : getBulletMissedEvents()) {
	 *      // do something with e
	 *   }
	 * </pre>
	 *
	 * @return a vector containing all BulletMissedEvents currently in the
	 *    robot's queue
	 *
	 * @see #onBulletMissed
	 * @see BulletMissedEvent
	 */
	public Vector<BulletMissedEvent> getBulletMissedEvents() {
		if (peer != null) {
			peer.getCall();
			return new Vector<BulletMissedEvent>(peer.getEventManager().getBulletMissedEvents());
		}
		uninitializedException("getBulletMissedEvents");
		return null; // never called
	}

	/**
	 * Returns a file representing a data directory for the robot, which can be
	 * written to using {@link RobocodeFileOutputStream} or
	 * {@link RobocodeFileWriter}.
	 * <p>
	 * The system will automatically create the directory for you, so you do not
	 * need to create it by yourself.
	 *
	 * @return a file representing the data directory for your robot
	 *
	 * @see #getDataFile
	 * @see RobocodeFileOutputStream
	 */
	public File getDataDirectory() {
		if (peer != null) {
			peer.getCall();
			peer.setIORobot(true);
			return peer.getRobotFileSystemManager().getWritableDirectory();
		}
		uninitializedException("getDataDirectory");
		return null; // never called
	}

	/**
	 * Returns a file in your data directory that you can write to using
	 * {@link RobocodeFileOutputStream} or {@link RobocodeFileWriter}.
	 * <p>
	 * The system will automatically create the directory for you, so you do not
	 * need to create it by yourself.
	 * <p>
	 * Please notice that the max. size of your data file is set to 200000
	 * (~195 KB).
	 * <p>
	 * See the sample robots for examples.
	 *
	 * @param filename the file name of the data file
	 * @return a file representing the data file for your robot
	 *
	 * @see #getDataDirectory
	 * @see RobocodeFileOutputStream
	 * @see RobocodeFileWriter
	 */
	public File getDataFile(String filename) {
		if (peer != null) {
			peer.getCall();
			peer.setIORobot(true);
			return new File(peer.getRobotFileSystemManager().getWritableDirectory(), filename);
		}
		uninitializedException("getDataFile");
		return null; // never called
	}

	/**
	 * Returns the data quota available in your data directory, i.e. the amount
	 * of bytes left in the data directory for the robot.
	 *
	 * @return the amount of bytes left in the robot's data directory
	 *
	 * @see #getDataDirectory
	 */
	public long getDataQuotaAvailable() {
		if (peer != null) {
			peer.getCall();
			return peer.getRobotFileSystemManager().getMaxQuota() - peer.getRobotFileSystemManager().getQuotaUsed();
		}
		uninitializedException("getDataQuotaAvailable");
		return 0; // never called
	}

	/**
	 * Returns the current priority of a class of events.
	 * An event priority is a value from 0 - 99. The higher value, the higher
	 * priority. The default priority is 80.
	 * <p>
	 * Example:
	 * <pre>
	 *   int myHitRobotPriority = getEventPriority("HitRobotEvent");
	 * </pre>
	 *
	 * @param eventClass the name of the event class (string)
	 * @return the current priority of a class of events
	 *
	 * @see #setEventPriority
	 */
	public int getEventPriority(String eventClass) {
		if (peer != null) {
			peer.getCall();
			return peer.getEventManager().getEventPriority(eventClass);
		}
		uninitializedException("getEventPriority");
		return 0; // never called
	}

	/**
	 * Returns the angle remaining in the gun's turn, in degrees.
	 * <p>
	 * This call returns both positive and negative values. Positive values
	 * means that the gun is currently turning to the right. Negative values
	 * means that the gun is currently turning to the left.
	 *
	 * @return the angle remaining in the gun's turn, in degrees
	 */
	public double getGunTurnRemaining() {
		if (peer != null) {
			return Math.toDegrees(peer.getGunTurnRemaining());
		}
		uninitializedException("getGunTurnRemaining");
		return 0; // never called
	}

	/**
	 * Returns a vector containing all HitByBulletEvents currently in the
	 * robot's queue. You might, for example, call this while processing
	 * another event.
	 * <p>
	 * Example:
	 * <pre>
	 *   for (HitByBulletEvent e : getHitByBulletEvents()) {
	 *      // do something with e
	 *   }
	 * </pre>
	 *
	 * @return a vector containing all HitByBulletEvents currently in the
	 *    robot's queue
	 *
	 * @see #onHitByBullet
	 * @see HitByBulletEvent
	 */
	public Vector<HitByBulletEvent> getHitByBulletEvents() {
		if (peer != null) {
			peer.getCall();
			return new Vector<HitByBulletEvent>(peer.getEventManager().getHitByBulletEvents());
		}
		uninitializedException("getHitByBulletEvents");
		return null; // never called
	}

	/**
	 * Returns a vector containing all HitRobotEvents currently in the robot's
	 * queue. You might, for example, call this while processing another event.
	 * <p>
	 * Example:
	 * <pre>
	 *   for (HitRobotEvent e : getHitRobotEvents()) {
	 *      // do something with e
	 *   }
	 * </pre>
	 *
	 * @return a vector containing all HitRobotEvents currently in the robot's
	 *    queue
	 *
	 * @see #onHitRobot
	 * @see HitRobotEvent
	 */
	public Vector<HitRobotEvent> getHitRobotEvents() {
		if (peer != null) {
			peer.getCall();
			return new Vector<HitRobotEvent>(peer.getEventManager().getHitRobotEvents());
		}
		uninitializedException("getHitRobotEvents");
		return null; // never called
	}

	/**
	 * Returns a vector containing all HitWallEvents currently in the robot's
	 * queue. You might, for example, call this while processing another event.
	 * <p>
	 * Example:
	 * <pre>
	 *   for (HitWallEvent e : getHitWallEvents()) {
	 *      // do something with e
	 *   }
	 * </pre>
	 *
	 * @return a vector containing all HitWallEvents currently in the robot's
	 *    queue
	 *
	 * @see #onHitWall
	 * @see HitWallEvent
	 */
	public Vector<HitWallEvent> getHitWallEvents() {
		if (peer != null) {
			peer.getCall();
			return new Vector<HitWallEvent>(peer.getEventManager().getHitWallEvents());
		}
		uninitializedException("getHitWallEvents");
		return null; // never called
	}

	/**
	 * Returns the angle remaining in the radar's turn, in degrees.
	 * <p>
	 * This call returns both positive and negative values. Positive values
	 * means that the radar is currently turning to the right. Negative values
	 * means that the radar is currently turning to the left.
	 *
	 * @return the angle remaining in the radar's turn, in degrees
	 */
	public double getRadarTurnRemaining() {
		if (peer != null) {
			peer.getCall();
			return Math.toDegrees(peer.getRadarTurnRemaining());
		}
		uninitializedException("getRadarTurnRemaining");
		return 0; // never called
	}

	/**
	 * Returns a vector containing all RobotDeathEvents currently in the robot's
	 * queue. You might, for example, call this while processing another event.
	 * <p>
	 * Example:
	 * <pre>
	 *   for (RobotDeathEvent e : getRobotDeathEvents()) {
	 *      // do something with e
	 *   }
	 * </pre>
	 *
	 * @return a vector containing all RobotDeathEvents currently in the robot's
	 *    queue
	 *
	 * @see #onRobotDeath
	 * @see RobotDeathEvent
	 */
	public Vector<RobotDeathEvent> getRobotDeathEvents() {
		if (peer != null) {
			peer.getCall();
			return new Vector<RobotDeathEvent>(peer.getEventManager().getRobotDeathEvents());
		}
		uninitializedException("getRobotDeathEvents");
		return null; // never called
	}

	/**
	 * Returns a vector containing all ScannedRobotEvents currently in the
	 * robot's queue. You might, for example, call this while processing another
	 * event.
	 * <p>
	 * Example:
	 * <pre>
	 *   for (ScannedRobotEvent e : getScannedRobotEvents()) {
	 *      // do something with e
	 *   }
	 * </pre>
	 *
	 * @return a vector containing all ScannedRobotEvents currently in the
	 *    robot's queue
	 *
	 * @see #onScannedRobot
	 * @see ScannedRobotEvent
	 */
	public Vector<ScannedRobotEvent> getScannedRobotEvents() {
		if (peer != null) {
			peer.getCall();
			return new Vector<ScannedRobotEvent>(peer.getEventManager().getScannedRobotEvents());
		}
		uninitializedException("getScannedRobotEvents");
		return null; // never called
	}

	/**
	 * Returns the angle remaining in the robots's turn, in degrees.
	 * <p>
	 * This call returns both positive and negative values. Positive values
	 * means that the robot is currently turning to the right. Negative values
	 * means that the robot is currently turning to the left.
	 *
	 * @return the angle remaining in the robots's turn, in degrees
	 */
	public double getTurnRemaining() {
		if (peer != null) {
			peer.getCall();
			return Math.toDegrees(peer.getTurnRemaining());
		}
		uninitializedException("getTurnRemaining");
		return 0; // never called
	}

	/**
	 * Checks if the gun is set to adjust for the robot turning, i.e. to turn
	 * independent from the robot's body turn.
	 * <p>
	 * This call returns {@code true} if the gun is set to turn independent of
	 * the turn of the robot's body. Otherwise, {@code false} is returned,
	 * meaning that the gun is set to turn with the robot's body turn.
	 *
	 * @return {@code true} if the gun is set to turn independent of the robot
	 *    turning; {@code false} if the gun is set to turn with the robot
	 *    turning
	 *
	 * @see #setAdjustGunForRobotTurn
	 */
	public boolean isAdjustGunForRobotTurn() {
		if (peer != null) {
			peer.getCall();
			return peer.isAdjustGunForBodyTurn();
		}
		uninitializedException("isAdjustGunForRobotTurn");
		return false; // never called
	}

	/**
	 * Checks if the radar is set to adjust for the gun turning, i.e. to turn
	 * independent from the gun's turn.
	 * <p>
	 * This call returns {@code true} if the radar is set to turn independent of
	 * the turn of the gun. Otherwise, {@code false} is returned, meaning that
	 * the radar is set to turn with the gun's turn.
	 *
	 * @return {@code true} if the radar is set to turn independent of the gun
	 *    turning; {@code false} if the radar is set to turn with the gun
	 *    turning
	 *
	 * @see #setAdjustRadarForGunTurn
	 */
	public boolean isAdjustRadarForGunTurn() {
		if (peer != null) {
			peer.getCall();
			return peer.isAdjustRadarForGunTurn();
		}
		uninitializedException("isAdjustRadarForGunTurn");
		return false; // never called
	}

	/**
	 * This method is called when a custom condition is met.
	 * <p>
	 * See the sample robots for examples of use.
	 *
	 * @param event the custom event that occured
	 *
	 * @see #addCustomEvent
	 * @see CustomEvent
	 * @see Event
	 */
	public void onCustomEvent(CustomEvent event) {}

	/**
	 * Removes a custom event (specified by condition).
	 * <p>
	 * See the sample robots for examples of use.
	 *
	 * @param condition the custom event to remove
	 *
	 * @see #addCustomEvent
	 */
	public void removeCustomEvent(Condition condition) {
		if (peer != null) {
			peer.setCall();
			peer.getEventManager().removeCustomEvent(condition);
		} else {
			uninitializedException("removeCustomEvent");
		}
	}

	/**
	 * Sets the priority of a class of events.
	 * <p>
	 * Events are sent to the onXXX handlers in order of priority.
	 * Higher priority events can interrupt lower priority events.
	 * For events with the same priority, newer events are always sent first.
	 * Valid priorities are 0 - 99, where 100 is reserved and 80 is the default
	 * priority.
	 * <p>
	 * Example:
	 * <pre>
	 *   setEventPriority("RobotDeathEvent", 15);
	 * </pre>
	 *
	 * The default priorities are, from lowest to highest:
	 * <pre>
	 *	 {@link ScannedRobotEvent}:     10
	 *	 {@link HitRobotEvent}:         20
	 *	 {@link HitWallEvent}:          30
	 *	 {@link HitByBulletEvent}:      40
	 *	 {@link BulletHitEvent}:        50
	 *	 {@link BulletHitBulletEvent}:  55
	 *	 {@link BulletMissedEvent}:     60
	 *	 {@link RobotDeathEvent}:       70
	 *	 {@link MessageEvent}:          75
	 *	 {@link CustomEvent}:           80
	 *	 {@link SkippedTurnEvent}:     100 (reserved)
	 *	 {@link WinEvent}:             100 (reserved)
	 *	 {@link DeathEvent}:           100 (reserved)
	 * </pre>
	 *
	 * Note that you cannot change the priority for events with the special
	 * priority value 100 (reserved) as these event are system events. Also note
	 * that you cannot change the priority of CustomEvent. Instead you must
	 * change the priority of the condition(s) for your custom event(s). 
	 *
	 * @param eventClass the name of the event class (string) to set the
	 *    priority for
	 * @param priority the new priority for that event class
	 *
	 * @see Robot#setInterruptible
	 */
	public void setEventPriority(String eventClass, int priority) {
		if (peer != null) {
			peer.setCall();
			peer.getEventManager().setEventPriority(eventClass, priority);
		} else {
			uninitializedException("setEventPriority");
		}
	}

	/**
	 * Call this during an event handler to allow new events of the same
	 * priority to restart the event handler.
	 *
	 * <p>Example:
	 * <pre>
	 *   onScannedRobot(ScannedRobotEvent e) {
	 *       fire(1);
	 *       setInterruptible(true);
	 *       ahead(100); // If you see a robot while moving ahead,
	 *                   // this handler will start from the top
	 *                   // Without setInterruptible, we wouldn't
	 *                   // receive scan events at all!
	 *       // We'll only get here if we don't see a robot during the move.
	 *       out.println("Ok, I can't see anyone");
	 *   }
	 * </pre>
	 *
	 * @param interruptible {@code true} if the event handler should be
	 *    interrupted if new events of the same priority occurs; {@code false}
	 *    otherwise
	 */
	@Override
	public void setInterruptible(boolean interruptible) {
		if (peer != null) {
			peer.setCall();
			peer.setInterruptible(interruptible);
		} else {
			uninitializedException("setInterruptible");
		}
	}

	/**
	 * Sets the maximum turn rate of the robot measured in degrees if the robot
	 * should turn slower than {@link Rules#MAX_TURN_RATE} (10 degress/turn).
	 *
	 * @param newMaxTurnRate the new maximum turn rate of the robot measured in
	 *    degrees. Valid values are 0 - {@link Rules#MAX_TURN_RATE}
	 */
	public void setMaxTurnRate(double newMaxTurnRate) {
		if (peer != null) {
			peer.setCall();
			peer.setMaxTurnRate(newMaxTurnRate);
		} else {
			uninitializedException("setMaxTurnRate");
		}
	}

	/**
	 * Sets the maximum velocity of the robot measured in pixels/turn if the robot
	 * should move slower than {@link Rules#MAX_VELOCITY} (8 pixels/turn).
	 *
	 * @param newMaxVelocity the new maximum turn rate of the robot measured in
	 *    pixels/turn. Valid values are 0 - {@link Rules#MAX_VELOCITY}
	 */
	public void setMaxVelocity(double newMaxVelocity) {
		if (peer != null) {
			peer.setCall();
			peer.setMaxVelocity(newMaxVelocity);
		} else {
			uninitializedException("setMaxVelocity");
		}
	}

	/**
	 * Sets the robot to resume the movement stopped by stop() or setStop(),
	 * if any.
	 * <p>
	 * This call returns immediately, and will not execute until you call
	 * execute() or take an action that executes.
	 *
	 * @see #setStop
	 * @see Robot#stop
	 */
	public void setResume() {
		if (peer != null) {
			peer.setCall();
			peer.setResume();
		} else {
			uninitializedException("setResume");
		}
	}

	/**
	 * This call is identical to {@link Robot#stop()}, but returns immediately,
	 * and will not execute until you call execute() or take an action that
	 * executes.
	 * <p>
	 * If there is already movement saved from a previous stop, this will have
	 * no effect.
	 * <p>
	 * This call is equivalent to calling setStop(false);
	 *
	 * @see #setStop(boolean)
	 * @see #setResume
	 * @see Robot#stop()
	 * @see Robot#resume
	 */
	public void setStop() {
		setStop(false);
	}

	/**
	 * This call is identical to {@link Robot#stop(boolean)}, but returns
	 * immediately, and will not execute until you call execute() or take an
	 * action that executes.
	 * <p>
	 * If there is already movement saved from a previous stop, you can
	 * overwrite it by calling setStop(true).
	 *
	 * @param overwrite {@code true} if the movement saved from a previous stop
	 *    should be owerwritten; {@code false} otherwise
	 *
	 * @see #setStop()
	 * @see #setResume
	 * @see Robot#stop()
	 * @see Robot#resume
	 */
	public void setStop(boolean overwrite) {
		if (peer != null) {
			peer.setCall();
			peer.setStop(overwrite);
		} else {
			uninitializedException("setStop");
		}
	}

	/**
	 * Sets the robot's gun to turn left by degrees when the next execution
	 * takes place.
	 * <p>
	 * This call returns immediately, and will not execute until you call
	 * execute() or take an action that executes.
	 * <p>
	 * Note that both positive and negative values can be given as input,
	 * where negative values means that the robot's gun is set to turn right
	 * instead of left.
	 * <p>
	 * Example:
	 * <pre>
	 *   // Set the gun to turn 180 degrees to the left
	 *   setTurnGunLeft(180);
	 *
	 *   // Set the gun to turn 90 degrees to the right instead of left
	 *   // (overrides the previous order)
	 *   setTurnGunLeft(-90);
	 *
	 *   ...
	 *   // Executes the last setTurnGunLeft()
	 *   execute();
	 * </pre>
	 *
	 * @param degrees the amount of degrees to turn the robot's gun to the left
	 *    If this value is negative, the robot's gun is set to turn to the right
	 *
	 * @see Robot#setAdjustGunForRobotTurn
	 */
	public void setTurnGunLeft(double degrees) {
		if (peer != null) {
			peer.setCall();
			peer.setTurnGun(-Math.toRadians(degrees));
		} else {
			uninitializedException("setTurnGunLeft");
		}
	}

	/**
	 * Sets the robot's gun to turn right by degrees when the next execution
	 * takes place.
	 * <p>
	 * This call returns immediately, and will not execute until you call
	 * execute() or take an action that executes.
	 * <p>
	 * Note that both positive and negative values can be given as input,
	 * where negative values means that the robot's gun is set to turn left
	 * instead of right.
	 * <p>
	 * Example:
	 * <pre>
	 *   // Set the gun to turn 180 degrees to the right
	 *   setTurnGunRight(180);
	 *
	 *   // Set the gun to turn 90 degrees to the left instead of right
	 *   // (overrides the previous order)
	 *   setTurnGunRight(-90);
	 *
	 *   ...
	 *   // Executes the last setTurnGunRight()
	 *   execute();
	 * </pre>
	 *
	 * @param degrees the amount of degrees to turn the robot's gun to the right
	 *    If this value is negative, the robot's gun is set to turn to the left
	 *
	 * @see Robot#setAdjustGunForRobotTurn
	 */
	public void setTurnGunRight(double degrees) {
		if (peer != null) {
			peer.setCall();
			peer.setTurnGun(Math.toRadians(degrees));
		} else {
			uninitializedException("setTurnGunRight");
		}
	}

	/**
	 * Sets the robot's radar to turn left by degrees when the next execution
	 * takes place.
	 * <p>
	 * This call returns immediately, and will not execute until you call
	 * execute() or take an action that executes.
	 * <p>
	 * Note that both positive and negative values can be given as input,
	 * where negative values means that the robot's radar is set to turn right
	 * instead of left.
	 * <p>
	 * Example:
	 * <pre>
	 *   // Set the radar to turn 180 degrees to the left
	 *   setTurnRadarLeft(180);
	 *
	 *   // Set the radar to turn 90 degrees to the right instead of left
	 *   // (overrides the previous order)
	 *   setTurnRadarLeft(-90);
	 *
	 *   ...
	 *   // Executes the last setTurnRadarLeft()
	 *   execute();
	 * </pre>
	 *
	 * @param degrees the amount of degrees to turn the robot's radar to the left
	 *    If this value is negative, the robot's radar is set to turn to the right
	 *
	 * @see Robot#setAdjustRadarForRobotTurn
	 * @see Robot#setAdjustRadarForGunTurn
	 */
	public void setTurnRadarLeft(double degrees) {
		if (peer != null) {
			peer.setCall();
			peer.setTurnRadar(-Math.toRadians(degrees));
		} else {
			uninitializedException("setTurnRadarLeft");
		}
	}

	/**
	 * Sets the robot's radar to turn right by degrees when the next execution
	 * takes place.
	 * <p>
	 * This call returns immediately, and will not execute until you call
	 * execute() or take an action that executes.
	 * <p>
	 * Note that both positive and negative values can be given as input,
	 * where negative values means that the robot's radar is set to turn left
	 * instead of right.
	 * <p>
	 * Example:
	 * <pre>
	 *   // Set the radar to turn 180 degrees to the right
	 *   setTurnRadarRight(180);
	 *
	 *   // Set the radar to turn 90 degrees to the right instead of right
	 *   // (overrides the previous order)
	 *   setTurnRadarRight(-90);
	 *
	 *   ...
	 *   // Executes the last setTurnRadarRight()
	 *   execute();
	 * </pre>
	 *
	 * @param degrees the amount of degrees to turn the robot's radar to the right
	 *    If this value is negative, the robot's radar is set to turn to the left
	 *
	 * @see Robot#setAdjustRadarForRobotTurn
	 * @see Robot#setAdjustRadarForGunTurn
	 */
	public void setTurnRadarRight(double degrees) {
		if (peer != null) {
			peer.setCall();
			peer.setTurnRadar(Math.toRadians(degrees));
		} else {
			uninitializedException("setTurnRadarRight");
		}
	}

	/**
	 * Does not return until a {@link Condition#test()} returns {@code true}.
	 * <p>
	 * This call executes immediately.
	 * <p>
	 * See the example robots for usage.
	 *
	 * @param condition the condition that must be met before this call returns
	 */
	public void waitFor(Condition condition) {
		if (peer != null) {
			peer.waitFor(condition);
		} else {
			uninitializedException("waitFor");
		}
	}

	/**
	 * Checks if the radar is set to adjust for the robot turning, i.e. to turn
	 * independent from the robot's body turn.
	 * <p>
	 * This call returns {@code true} if the radar is set to turn independent of
	 * the turn of the robot. Otherwise, {@code false} is returned, meaning that
	 * the radar is set to turn with the robot's turn.
	 *
	 * @return {@code true} if the radar is set to turn independent of the robot
	 *    turning; {@code false} if the radar is set to turn with the robot
	 *    turning
	 *
	 * @see #setAdjustRadarForRobotTurn
	 */
	public boolean isAdjustRadarForRobotTurn() {
		if (peer != null) {
			peer.getCall();
			return peer.isAdjustRadarForBodyTurn();
		}
		uninitializedException("isAdjustRadarForRobotTurn");
		return false; // never called
	}

	/**
	 * This method is called if your robot dies.
	 * <p>
	 * You should override it in your robot if you want to be informed of this
	 * event. Actions will have no effect if called from this section. The
	 * intent is to allow you to perform calculations or print something out
	 * when the robot is killed.
	 *
	 * @param event the death event set by the game
	 *
	 * @see DeathEvent
	 * @see Event
	 */
	@Override
	public void onDeath(DeathEvent event) {}

	/**
	 * This method is called if the robot is using too much time between
	 * actions. When this event occur, the robot's turn is skipped, meaning that
	 * it cannot take action anymore in this turn.
	 * <p>
	 * If you receive 30 skipped turn event, your robot will be removed from the
	 * round and loose the round.
	 * <p>
	 * You will only receive this event after taking an action. So a robot in an
	 * infinite loop will not receive any events, and will simply be stopped.
	 * <p>
	 * No correctly working, reasonable robot should ever receive this event.
	 *
	 * @param event the skipped turn event set by the game
	 *
	 * @see SkippedTurnEvent
	 * @see Event
	 */
	public void onSkippedTurn(SkippedTurnEvent event) {}
}
