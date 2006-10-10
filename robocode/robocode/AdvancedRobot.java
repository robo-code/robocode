/*******************************************************************************
 * Copyright (c) 2001-2006 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Updated getXxxEvents() to Java 5 including new examples regarding loops
 *******************************************************************************/
package robocode;


import java.io.File;
import java.util.Vector;


/**
 * A more advanced type of robot that allows non-blocking calls, custom events, and writes to the filesystem.
 * <P>If you have not already, you should create a {@link robocode.Robot Robot} first.
 *
 * @see robocode.Robot
 *
 * @author Mathew A. Nelson
 */
public class AdvancedRobot extends _AdvancedRadiansRobot {

	/**
	 * Gets distance left in the robot's current move.
	 */
	public double getDistanceRemaining() {
		if (peer != null) {
			peer.getCall();
			return peer.getDistanceRemaining();
		} else {
			uninitializedException("getDistanceRemaining");
			return 0; // never called
		}
	}

	/**
	 * Sets the robot to move ahead by distance
	 * This call returns immediately, and will not execute until you call execute() or take an action that executes.
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
	 * Sets the robot to move back by distance.
	 * This call returns immediately, and will not execute until you call execute() or take an action that executes.
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
	 * Sets the robot to turn left by degrees.
	 * This call returns immediately, and will not execute until you call execute() or take an action that executes.
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
	 * Sets the robot to turn right by degrees.
	 * This call returns immediately, and will not execute until you call execute() or take an action that executes.
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
	 * Sets the gun to fire a bullet.
	 * This call returns immediately, and will not execute until you call execute() or take an action that executes.
	 * @param power The energy given to the bullet, and subtracted from your energy.
	 * @see robocode.Robot#fire
	 * @see robocode.Robot#fireBullet
	 * @see #setFireBullet
	 * @see robocode.Robot#fireBullet
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
	 * Fires a bullet.  This call is exactly like setFire(double),
	 * but returns the Bullet object you fired.
	 * This call returns immediately, and will not execute until you call execute() or take an action that executes.
	 *
	 * @see robocode.Robot#fire
	 * @see robocode.Robot#fireBullet
	 * @see #setFire
	 */
	public Bullet setFireBullet(double power) {
		if (peer != null) {
			peer.setCall();
			return peer.setFire(power);
		} else {
			uninitializedException("setFireBullet");
			return null;
		}
	}

	/**
	 * Registers a custom event to be called when a condition is met.
	 * See the sample robots for examples.
	 * @param condition The condition that must be met.
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
	 * Executes any pending actions, or continues executing actions that are in process.
	 * This returns after the actions have been started.
	 * Advanced robots will probably call this function repeatedly...
	 *
	 * <P>In this example the robot will move while turning
	 * <PRE>
	 *   setTurnRight(90);
	 *   setAhead(100);
	 *   execute();
	 *   while (getDistanceRemaining() != 0 && getTurnRemaining() != 0) {
	 *     execute();
	 *   }
	 * </PRE>
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
	 *
	 * <P>Example:
	 * <pre>
	 *   for (Event e : getAllEvents()) {
	 *      if (e instanceof HitRobotEvent)
	 *        <i> (do something with e) </i>
	 *      else if (e instanceof HitByBulletEvent)
	 *        <i> (so something else with e) </i>
	 *   }
	 * </pre>
	 *
	 * @see #onBulletHit
	 * @see #onBulletHitBullet
	 * @see #onBulletMissed
	 * @see #onHitByBullet
	 * @see #onHitRobot
	 * @see #onHitWall
	 * @see #onSkippedTurn
	 * @see robocode.BulletHitEvent
	 * @see robocode.BulletMissedEvent
	 * @see robocode.HitByBulletEvent
	 * @see robocode.HitRobotEvent
	 * @see robocode.HitWallEvent
	 * @see robocode.SkippedTurnEvent
	 * @see robocode.Event
	 * @see java.util.Vector
	 */
	public Vector<Event> getAllEvents() {
		if (peer != null) {
			peer.getCall();
			return peer.getEventManager().getAllEvents();
		} else {
			uninitializedException("getAllEvents");
			return null; // never called
		}
	}

	/**
	 * Returns a vector containing all BulletHitBulletEvents currently in the robot's queue.
	 * You might, for example, call this while processing another event.
	 *
	 * <P>Example:
	 * <pre>
	 *   for (BulletHitBulletEvent e : getBulletHitBulletEvents()) {
	 *      <i> (do something with e) </i>
	 *   }
	 * </pre>
	 *
	 * @see #onBulletHitBullet
	 * @see robocode.BulletHitBulletEvent
	 * @see java.util.Vector
	 */
	public Vector<BulletHitBulletEvent> getBulletHitBulletEvents() {
		if (peer != null) {
			peer.getCall();
			return peer.getEventManager().getBulletHitBulletEvents();
		} else {
			uninitializedException("getBulletHitBulletEvents");
			return null; // never called
		}
	}

	/**
	 * Returns a vector containing all BulletHitEvents currently in the robot's queue.
	 * You might, for example, call this while processing another event.
	 *
	 * <P>Example:
	 * <pre>
	 *   for (BulletHitEvent : getBulletHitEvents()) {
	 *      <i> (do something with e) </i>
	 *   }
	 * </pre>
	 *
	 * @see #onBulletHit
	 * @see robocode.BulletHitEvent
	 * @see java.util.Vector
	 */
	public Vector<BulletHitEvent> getBulletHitEvents() {
		if (peer != null) {
			peer.getCall();
			return peer.getEventManager().getBulletHitEvents();
		} else {
			uninitializedException("getBulletHitEvents");
			return null; // never called
		}
	}

	/**
	 * Returns a vector containing all BulletMissedEvents currently in the robot's queue.
	 * You might, for example, call this while processing another event.
	 *
	 * <P>Example:
	 * <pre>
	 *   for (BulletMissedEvent e : getBulletHitEvents()) {
	 *      <i> (do something with e) </i>
	 *   }
	 * </pre>
	 *
	 * @see #onBulletMissed
	 * @see robocode.BulletMissedEvent
	 * @see java.util.Vector
	 */
	public Vector<BulletMissedEvent> getBulletMissedEvents() {
		if (peer != null) {
			peer.getCall();
			return peer.getEventManager().getBulletMissedEvents();
		} else {
			uninitializedException("getBulletMissedEvents");
			return null; // never called
		}
	}

	/**
	 * Returns a file representing a directory you can write to using RobocodeOutputStream.
	 * The system will create the directory for you, you do not need to create it.
	 * @see #getDataFile
	 */
	public File getDataDirectory() {
		if (peer != null) {
			peer.getCall();
			peer.setIORobot(true);
			return peer.getRobotFileSystemManager().getWritableDirectory();
		} else {
			uninitializedException("getDataDirectory");
			return null; // never called
		}
	}

	/**
	 * Returns a file in your data directory that you can write to RobocodeOutputStream.
	 * The system will create the directory for you, you do not need to create it.
	 * See the sample robots for examples.
	 * @see #getDataDirectory
	 */
	public File getDataFile(String filename) {
		if (peer != null) {
			peer.getCall();
			peer.setIORobot(true);
			return new File(peer.getRobotFileSystemManager().getWritableDirectory(), filename);
		} else {
			uninitializedException("getDataFile");
			return null; // never called
		}
	}

	/**
	 * Returns the quota available in your data directory, in bytes.
	 * 
	 * @see #getDataDirectory
	 */
	public long getDataQuotaAvailable() {
		if (peer != null) {
			peer.getCall();
			return peer.getRobotFileSystemManager().getMaxQuota() - peer.getRobotFileSystemManager().getQuotaUsed();
		} else {
			uninitializedException("getDataQuotaAvailable");
			return 0; // never called
		}
	}

	/**
	 * Returns the current priority of a class of events.
	 * Example:
	 * <PRE>
	 *   int myHitRobotPriority = getEventPriority("HitRobotEvent");
	 * </PRE>
	 * @see #setEventPriority
	 * @param eventClass The name of the event class (string)
	 */
	public int getEventPriority(String eventClass) {
		if (peer != null) {
			peer.getCall();
			return peer.getEventManager().getEventPriority(eventClass);
		} else {
			uninitializedException("getEventPriority");
			return 0; // never called
		}
	}

	/**
	 * Gets angle remaining in the gun's turn, in degrees
	 * 
	 * @return angle remaining in the gun's turn, in degrees
	 */
	public double getGunTurnRemaining() {
		if (peer != null) {
			return Math.toDegrees(peer.getGunTurnRemaining());
		} else {
			uninitializedException("getGunTurnRemaining");
			return 0; // never called
		}
	}

	/**
	 * Returns a vector containing all HitByBulletEvents currently in the robot's queue.
	 * You might, for example, call this while processing another event.
	 *
	 * <P>Example:
	 * <pre>
	 *   for (HitByBulletEvent e : getHitByBulletEvents()) {
	 *      <i> (do something with e) </i>
	 *   }
	 * </pre>
	 *
	 * @see #onHitByBullet
	 * @see robocode.HitByBulletEvent
	 * @see java.util.Vector
	 */
	public Vector<HitByBulletEvent> getHitByBulletEvents() {
		if (peer != null) {
			peer.getCall();
			return peer.getEventManager().getHitByBulletEvents();
		} else {
			uninitializedException("getHitByBulletEvents");
			return null; // never called
		}
	}

	/**
	 * Returns a vector containing all HitRobotEvents currently in the robot's queue.
	 * You might, for example, call this while processing another event.
	 *
	 * <P>Example:
	 * <pre>
	 *   for (HitRobotEvent e : getHitRobotEvents()) {
	 *      <i> (do something with e) </i>
	 *   }
	 * </pre>
	 *
	 * @see #onHitRobot
	 * @see robocode.HitRobotEvent
	 * @see java.util.Vector
	 */
	public Vector<HitRobotEvent> getHitRobotEvents() {
		if (peer != null) {
			peer.getCall();
			return peer.getEventManager().getHitRobotEvents();
		} else {
			uninitializedException("getHitRobotEvents");
			return null; // never called
		}
	}

	/**
	 * Returns a vector containing all HitWallEvents currently in the robot's queue.
	 * You might, for example, call this while processing another event.
	 *
	 * <P>Example:
	 * <pre>
	 *   for (HitWallEvent e : getHitWallEvents()) {
	 *      <i> (do something with e) </i>
	 *   }
	 * </pre>
	 *
	 * @see #onHitWall
	 * @see robocode.HitWallEvent
	 * @see java.util.Vector
	 */
	public Vector<HitWallEvent> getHitWallEvents() {
		if (peer != null) {
			peer.getCall();
			return peer.getEventManager().getHitWallEvents();
		} else {
			uninitializedException("getHitWallEvents");
			return null; // never called
		}
	}

	/**
	 * Gets angle remaining in the radar's turn, in degrees.
	 * 
	 * @return angle remaining in the radar's turn
	 */
	public double getRadarTurnRemaining() {
		if (peer != null) {
			peer.getCall();
			return Math.toDegrees(peer.getRadarTurnRemaining());
		} else {
			uninitializedException("getRadarTurnRemaining");
			return 0; // never called
		}
	}

	/**
	 * Returns a vector containing all RobotDeathEvents currently in the robot's queue.
	 * You might, for example, call this while processing another event.
	 *
	 * <P>Example:
	 * <pre>
	 *   for (RobotDeathEvent e : getRobotDeathEvents()) {
	 *      <i> (do something with e) </i>
	 *   }
	 * </pre>
	 *
	 * @see #onRobotDeath
	 * @see robocode.RobotDeathEvent
	 * @see java.util.Vector
	 */
	public Vector<RobotDeathEvent> getRobotDeathEvents() {
		if (peer != null) {
			peer.getCall();
			return peer.getEventManager().getRobotDeathEvents();
		} else {
			uninitializedException("getRobotDeathEvents");
			return null; // never called
		}
	}

	/**
	 * Returns a vector containing all ScannedRobotEvents currently in the robot's queue.
	 * You might, for example, call this while processing another event.
	 *
	 * <P>Example:
	 * <pre>
	 *   for (ScannedRobotEvent e : getScannedRobotEvents()) {
	 *      <i> (do something with e) </i>
	 *   }
	 * </pre>
	 *
	 * @see #onScannedRobot
	 * @see robocode.ScannedRobotEvent
	 * @see java.util.Vector
	 */
	public Vector<ScannedRobotEvent> getScannedRobotEvents() {
		if (peer != null) {
			peer.getCall();
			return peer.getEventManager().getScannedRobotEvents();
		} else {
			uninitializedException("getScannedRobotEvents");
			return null; // never called
		}
	}

	/**
	 * Gets angle remaining in the robot's turn, in degrees.
	 * 
	 * @return angle remaining in the robot's turn, in degrees
	 */
	public double getTurnRemaining() {
		if (peer != null) {
			peer.getCall();
			return Math.toDegrees(peer.getTurnRemaining());
		} else {
			uninitializedException("getTurnRemaining");
			return 0; // never called
		}
	}

	/**
	 * Checks if the gun is set to adjust for the robot turning.
	 * 
	 * @see #setAdjustGunForRobotTurn
	 * @return if the gun is set to adjust for the robot turning.
	 */
	public boolean isAdjustGunForRobotTurn() {
		if (peer != null) {
			peer.getCall();
			return peer.isAdjustGunForBodyTurn();
		} else {
			uninitializedException("isAdjustGunForRobotTurn");
			return false; // never called
		}
	}

	/**
	 * Checks if the radar is set to adjust for the gun turning.
	 * 
	 * @see #setAdjustRadarForGunTurn
	 * @return if the radar is set to adjust for the gun turning.
	 */
	public boolean isAdjustRadarForGunTurn() {
		if (peer != null) {
			peer.getCall();
			return peer.isAdjustRadarForGunTurn();
		} else {
			uninitializedException("isAdjustRadarForGunTurn");
			return false; // never called
		}
	}

	/**
	 * This method will be called when a custom condition is met
	 * See the sample robots for examples of use.
	 *
	 * @param event The event set by the game
	 * @see #addCustomEvent
	 * @see robocode.CustomEvent
	 * @see robocode.Event
	 */
	public void onCustomEvent(CustomEvent event) {}

	/**
	 * Removes a custom event (specified by condition).
	 * See the sample robots for examples of use.
	 * @see #addCustomEvent
	 * @param condition The condition to remove
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
	 * Set the priority of a class of events.
	 * Events are sent to the onXxxxxx handlers in order of priority,
	 * and higher priority events can interrupt lower priority events.
	 * For events with the same priority, newer events are always sent first.
	 * Valid priorities are 0-99.  100 is reserved.
	 *
	 * Example:
	 * <PRE>
	 *  setEventPriority("RobotDeathEvent",15);
	 * </PRE>
	 *
	 * The default priorities are, from lowest to highest:
	 * <PRE>
	 *	ScannedRobotEvent: 		10
	 *	HitRobotEvent:     		20
	 *	HitWallEvent:      		30
	 *	HitByBulletEvent:  		40
	 *	BulletHitEvent:    		50
	 *	BulletHitBulletEvent:	50
	 *	BulletMissedEvent: 		60
	 *	RobotDeathEvent:   		70
	 *	CustomEvent:       		80
	 *	SkippedTurnEvent:  		100
	 *	WinEvent:          		100
	 *	DeathEvent:        		100
	 * </PRE>
	 * @param eventClass String containing name of the event class you wish to set priority for
	 * @param priority the new priority for that class
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
	 * Call this during an event handler to allow new events of the same priority,
	 * generated following this call, to restart the event handler.
	 *
	 * <P>Example
	 * <PRE>
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
	 * </PRE>
	 * @param interruptible Whether this event handler should be interrupted on new events of same priority.
	 */
	public void setInterruptible(boolean interruptible) {
		if (peer != null) {
			peer.setCall();
			peer.setInterruptible(interruptible);
		} else {
			uninitializedException("setInterruptible");
		}
	}

	/**
	 * If you would like to turn slower than 10 degrees / tick,
	 * call this method to set it.
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
	 * If you would like to limit your robot's speed to less than 8,
	 * call this method.
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
	 * Sets the robot to resume the movement you stopped in stop() or setStop(), if any.
	 * This call returns immediately, and will not execute until you call execute() or take an action that executes.
	 * @see #setStop
	 * @see robocode.Robot#stop
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
	 * This call is identical to {@link robocode.Robot#stop stop()}, 
	 * but returns immediately, and will not execute until you call execute() or take an action that executes.
	 * If there is already movement saved from a previous stop, this will have no effect.
	 * This call is equivalent to calling setStop(false);
	 * @see #setStop(boolean)
	 * @see #setResume
	 * @see robocode.Robot#stop()
	 * @see robocode.Robot#resume
	 */
	public void setStop() {
		setStop(false);
	}

	/**
	 * This call is identical to {@link robocode.Robot#stop(boolean) stop(boolean)},
	 * but returns immediately, and will not execute until you call execute() or take an action that executes.
	 * If there is already movement saved from a previous stop, you can overwrite it
	 * by calling setStop(true).  
	 * @see #setStop()
	 * @see #setResume
	 * @see robocode.Robot#stop()
	 * @see robocode.Robot#resume
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
	 * Sets the gun to turn left by degrees
	 * This call returns immediately, and will not execute until you call execute() or take an action that executes.
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
	 * Sets the gun to turn right by degrees.
	 * This call returns immediately, and will not execute until you call execute() or take an action that executes.
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
	 * Sets the radar to turn left by degrees.
	 * This call returns immediately, and will not execute until you call execute() or take an action that executes.
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
	 * Sets the radar to turn right by degrees.
	 * This call returns immediately, and will not execute until you call execute() or take an action that executes.
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
	 * Does not return until condition.test() returns true.
	 * This call executes immediately.
	 * See the example robots for usage.
	 */
	public void waitFor(Condition condition) {
		if (peer != null) {
			peer.waitFor(condition);
		} else {
			uninitializedException("waitFor");
		}
	}

	/**
	 * Checks if the radar is set to adjust for the robot turning.
	 * 
	 * @see #setAdjustRadarForRobotTurn
	 * @return if the radar is set to adjust for the robot turning.
	 */
	public boolean isAdjustRadarForRobotTurn() {
		if (peer != null) {
			peer.getCall();
			return peer.isAdjustRadarForBodyTurn();
		} else {
			uninitializedException("isAdjustRadarForRobotTurn");
			return false; // never called
		}
	}

	/**
	 * This method will be called if your robot dies
	 * You should override it in your robot if you want to be informed of this event.
	 * Actions will have no effect if called from this section.
	 * The intent is to allow you to perform calculations or print something out when you lose.
	 *
	 * @param event The event set by the game
	 * @see robocode.DeathEvent
	 * @see robocode.Event
	 */
	public void onDeath(DeathEvent event) {}

	/**
	 * This method will be called if you are taking an extremely long time between actions.
	 * If you receive 30 of these, your robot will be removed from the round.
	 * You will only receive this event after taking an action... so a robot in an infinite loop
	 * will not receive any events, and will simply be stopped.
	 *
	 * No correctly working, reasonable robot should ever receive this event.
	 *
	 * @param event The event set by the game
	 * @see robocode.SkippedTurnEvent
	 * @see robocode.Event
	 */
	public void onSkippedTurn(SkippedTurnEvent event) {}
}
