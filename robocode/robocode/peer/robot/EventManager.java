/*******************************************************************************
 * Copyright (c) 2001 Mathew Nelson
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew Nelson - initial API and implementation
 *******************************************************************************/
package robocode.peer.robot;

import robocode.*;
import java.util.Vector;
import robocode.peer.RobotPeer;
import robocode.util.*;
import robocode.exception.*;

/**
 * Insert the type's description here.
 * Creation date: (9/10/2001 5:33:06 PM)
 * @author: Administrator
 */
public class EventManager {
	private RobotPeer robotPeer = null;

	private int scannedRobotEventPriority = 10;
	private int hitByBulletEventPriority = 20;
	private int hitWallEventPriority = 30;
	private int hitRobotEventPriority = 40;
	private int bulletHitEventPriority = 50;
	private int bulletHitBulletEventPriority = 50;
	private int bulletMissedEventPriority = 60;
	private int robotDeathEventPriority = 70;
	private int messageEventPriority = 80;
	private int skippedTurnEventPriority = 100;
	private int winEventPriority = 100;
	private int deathEventPriority = 100;

	private int currentTopEventPriority;

	private Vector customEvents = new Vector();
	private java.util.Vector backwardsCompatibilitySavedCustomEvents = new Vector();
	private EventQueue eventQueue;

	private double fireAssistAngle;
	private boolean fireAssistValid = false;
	private boolean useFireAssist = true;

	private int maxPriorities = 101;
	private boolean interruptible[] = new boolean[maxPriorities];
	
	private int MAXQUEUESIZE = 256;
	
	private Robot robot;
	
/**
 * EventManager constructor comment.
 */
public EventManager(RobotPeer r) {
	super();
	this.robotPeer = r;
	eventQueue = new EventQueue(this);
	reset();
}

public void finalize() {
/*	System.out.println("Finalizing " + robotPeer.getName() + " eventmanager.");
	backwardsCompatibilitySavedCustomEvents = null;
	customEvents = null;
	robotPeer = null;
	eventQueue = null;
	*/
}

/**
 * Insert the method's description here.
 * Creation date: (1/25/2001 11:35:22 AM)
 * @param o java.lang.Object
 */
public synchronized boolean add(Object o) {
	if (eventQueue != null)
	{
		if (eventQueue.size() > MAXQUEUESIZE)
		{
			System.out.println("Not adding to " + robotPeer.getName() + "'s queue, exceeded " + MAXQUEUESIZE + " events in queue.");
			return false;
		}
		return eventQueue.add(o);
	}
	return false;
}
/**
 * Insert the method's description here.
 * Creation date: (1/27/2001 10:29:36 AM)
 * @param condition robocode.JCondition
 */
public synchronized void addCustomEvent(Condition condition) {
	customEvents.add(condition);
}

/**
 * Insert the method's description here.
 * Creation date: (9/10/2001 6:14:36 PM)
 */
public synchronized void clearAllEvents(boolean includingSystemEvents) {
// Fixed in 1.0.4
//	currentTopEventPriority = Integer.MIN_VALUE;
	eventQueue.clear(includingSystemEvents);
}
/**
 * Insert the method's description here.
 * Creation date: (9/10/2001 6:14:36 PM)
 */
public synchronized void clear(long clearTime) {
	eventQueue.clear(clearTime);
}
/**
 * Returns a vector containing all events currently in the robot's queue.
 * You might, for example, call this while processing another event.
 *
 * <P>Example:
 * <pre>
 *   Vector v = getAllEvents();
 *   Event e;
 *   for (int i = 0; i < e.size(); i++) {
 *      e = (Event)v.elementAt(i);
 *      if (e instanceof HitByRobotEvent)
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
 * @see #onHitByRobot
 * @see #onHitRobot
 * @see #onHitWall
 * @see #onSkippedTurn
 * @see robocode.BulletHitEvent
 * @see robocode.BulletMissedEvent
 * @see robocode.HitByBulletEvent
 * @see robocode.HitByRobotEvent
 * @see robocode.HitRobotEvent
 * @see robocode.HitWallEvent
 * @see robocode.SkippedTurnEvent
 * @see robocode.Event
 * @see java.util.Vector
 */
public synchronized java.util.Vector getAllEvents() {
	Vector events = new Vector();
	for (int i = 0; i < eventQueue.size(); i++)
	{
	  events.add(eventQueue.elementAt(i));
	}
	return events;
}
/**
 * Returns a vector containing all BulletHitBulletEvents currently in the robot's queue.
 * You might, for example, call this while processing another event.
 *
 * <P>Example:
 * <pre>
 *   Vector v = getBulletHitBulletEvents();
 *   BulletHitBulletEvent e;
 *   for (int i = 0; i < e.size(); i++) {
 *      e = (BulletHitBulletEvent)v.elementAt(i);
 *      <i> (do something with e) </i>
 *   }
 * </pre>
 *
 * @see #onBulletHitBullet
 * @see robocode.BulletHitBulletEvent
 * @see java.util.Vector
 */
public synchronized java.util.Vector getBulletHitBulletEvents() {
	Vector events = new Vector();
	for (int i = 0; i < eventQueue.size(); i++)
	{
		if (eventQueue.elementAt(i) instanceof BulletHitBulletEvent)
		{
			events.add(eventQueue.elementAt(i));
		}
	}
	return events;
}
/**
 * Returns a vector containing all BulletHitEvents currently in the robot's queue.
 * You might, for example, call this while processing another event.
 *
 * <P>Example:
 * <pre>
 *   Vector v = getBulletHitEvents();
 *   BulletHitEvent e;
 *   for (int i = 0; i < e.size(); i++) {
 *      e = (BulletHitEvent)v.elementAt(i);
 *      <i> (do something with e) </i>
 *   }
 * </pre>
 *
 * @see #onBulletHit
 * @see robocode.BulletHitEvent
 * @see java.util.Vector
 */
public synchronized java.util.Vector getBulletHitEvents() {
	Vector events = new Vector();
	for (int i = 0; i < eventQueue.size(); i++)
	{
		if (eventQueue.elementAt(i) instanceof BulletHitEvent)
		{
			events.add(eventQueue.elementAt(i));
		}
	}
	return events;
}
/**
 * Returns a vector containing all BulletMissedEvents currently in the robot's queue.
 * You might, for example, call this while processing another event.
 *
 * <P>Example:
 * <pre>
 *   Vector v = getBulletHitEvents();
 *   BulletMissedEvent e;
 *   for (int i = 0; i < e.size(); i++) {
 *      e = (BulletMissedEvent)v.elementAt(i);
 *      <i> (do something with e) </i>
 *   }
 * </pre>
 *
 * @see #onBulletMissed
 * @see robocode.BulletMissedEvent
 * @see java.util.Vector
 */
public synchronized java.util.Vector getBulletMissedEvents() {
	Vector events = new Vector();
	for (int i = 0; i < eventQueue.size(); i++)
	{
		if (eventQueue.elementAt(i) instanceof BulletMissedEvent)
		{
			events.add(eventQueue.elementAt(i));
		}
	}
	return events;
}
/**
 * Insert the method's description here.
 * Creation date: (9/26/2001 2:36:27 PM)
 * @return int
 */
public int getCurrentTopEventPriority() {
	return currentTopEventPriority;
}
/**
 * Insert the method's description here.
 * Creation date: (1/25/2001 10:25:03 AM)
 * @param c java.lang.Class
 * @param priority int
 */
public int getEventPriority(String eventClass) {
	if (eventClass.equals("robocode.BulletHitEvent") || eventClass.equals("BulletHitEvent"))
		return bulletHitEventPriority;
	else if (eventClass.equals("robocode.BulletMissedEvent") || eventClass.equals("BulletMissedEvent"))
		return bulletMissedEventPriority;
	else if (eventClass.equals("robocode.DeathEvent") || eventClass.equals("DeathEvent"))
		return deathEventPriority;
	else if (eventClass.equals("robocode.HitByBulletEvent") || eventClass.equals("HitByBulletEvent"))
		return hitByBulletEventPriority;
	else if (eventClass.equals("robocode.HitRobotEvent") || eventClass.equals("HitRobotEvent"))
		return hitRobotEventPriority;
	else if (eventClass.equals("robocode.HitWallEvent") || eventClass.equals("HitWallEvent"))
		return hitWallEventPriority;
	else if (eventClass.equals("robocode.RobotDeathEvent") || eventClass.equals("RobotDeathEvent"))
		return robotDeathEventPriority;
	else if (eventClass.equals("robocode.ScannedRobotEvent") || eventClass.equals("ScannedRobotEvent"))
		return scannedRobotEventPriority;
	else if (eventClass.equals("robocode.MessageEvent") || eventClass.equals("MessageEvent"))
		return messageEventPriority;
	else if (eventClass.equals("robocode.SkippedTurnEvent") || eventClass.equals("SkippedTurnEvent"))
		return skippedTurnEventPriority;
	else if (eventClass.equals("robocode.WinEvent") || eventClass.equals("WinEvent"))
		return winEventPriority;
	else
		return -1;
}
/**
 * Insert the method's description here.
 * Creation date: (1/25/2001 10:25:03 AM)
 * @param c java.lang.Class
 * @param priority int
 */
public int getEventPriority(Event e) {
	if (e instanceof ScannedRobotEvent)
		return scannedRobotEventPriority;
	if (e instanceof BulletHitEvent)
		return bulletHitEventPriority;
	if (e instanceof BulletMissedEvent)
		return bulletMissedEventPriority;
	if (e instanceof DeathEvent)
		return deathEventPriority;
	if (e instanceof HitByBulletEvent)
		return hitByBulletEventPriority;
	if (e instanceof HitRobotEvent)
		return hitRobotEventPriority;
	if (e instanceof HitWallEvent)
		return hitWallEventPriority;
	if (e instanceof RobotDeathEvent)
		return robotDeathEventPriority;
	if (e instanceof SkippedTurnEvent)
		return skippedTurnEventPriority;
	if (e instanceof MessageEvent)
		return messageEventPriority;
	if (e instanceof WinEvent)
		return winEventPriority;
	if (e instanceof CustomEvent)
		return ((CustomEvent)e).getCondition().getPriority();
	return 0;
}
/**
 * Insert the method's description here.
 * Creation date: (9/10/2001 7:07:20 PM)
 * @return double
 */
public double getFireAssistAngle() {
	return fireAssistAngle;
}
/**
 * Returns a vector containing all HitByBulletEvents currently in the robot's queue.
 * You might, for example, call this while processing another event.
 *
 * <P>Example:
 * <pre>
 *   Vector v = getHitByBulletEvents();
 *   HitByBulletEvent e;
 *   for (int i = 0; i < e.size(); i++) {
 *      e = (HitByBulletEvent)v.elementAt(i);
 *      <i> (do something with e) </i>
 *   }
 * </pre>
 *
 * @see #onHitByBullet
 * @see robocode.HitByBulletEvent
 * @see java.util.Vector
 */

public synchronized java.util.Vector getHitByBulletEvents() {
	Vector events = new Vector();
	for (int i = 0; i < eventQueue.size(); i++)
	{
		if (eventQueue.elementAt(i) instanceof HitByBulletEvent)
		{
			events.add(eventQueue.elementAt(i));
		}
	}
	return events;
}
/**
 * Returns a vector containing all HitRobotEvents currently in the robot's queue.
 * You might, for example, call this while processing another event.
 *
 * <P>Example:
 * <pre>
 *   Vector v = getHitRobotEvents();
 *   HitRobotEvent e;
 *   for (int i = 0; i < e.size(); i++) {
 *      e = (HitRobotEvent)v.elementAt(i);
 *      <i> (do something with e) </i>
 *   }
 * </pre>
 *
 * @see #onHitRobot
 * @see robocode.HitRobotEvent
 * @see java.util.Vector
 */
public synchronized java.util.Vector getHitRobotEvents() {
	Vector events = new Vector();
	for (int i = 0; i < eventQueue.size(); i++)
	{
		if (eventQueue.elementAt(i) instanceof HitRobotEvent)
		{
			events.add(eventQueue.elementAt(i));
		}
	}
	return events;
}
/**
 * Returns a vector containing all HitWallEvents currently in the robot's queue.
 * You might, for example, call this while processing another event.
 *
 * <P>Example:
 * <pre>
 *   Vector v = getHitWallEvents();
 *   HitWallEvent e;
 *   for (int i = 0; i < e.size(); i++) {
 *      e = (HitWallEvent)v.elementAt(i);
 *      <i> (do something with e) </i>
 *   }
 * </pre>
 *
 * @see #onHitWall
 * @see robocode.HitWallEvent
 * @see java.util.Vector
 */
public synchronized java.util.Vector getHitWallEvents() {
	Vector events = new Vector();
	for (int i = 0; i < eventQueue.size(); i++)
	{
		if (eventQueue.elementAt(i) instanceof HitWallEvent)
		{
			events.add(eventQueue.elementAt(i));
		}
	}
	return events;
}
/**
 * Insert the method's description here.
 * Creation date: (9/28/2001 12:58:47 PM)
 * @param interruptable boolean
 */
public boolean getInterruptible(int priority) {
	return this.interruptible[priority];
}
/**
 * Insert the method's description here.
 * Creation date: (9/10/2001 5:45:01 PM)
 * @return robocode.Robot
 */
private synchronized Robot getRobot() {
	return robot;
}

public synchronized void setRobot(Robot r) {
	this.robot = r;
}

/**
 * Returns a vector containing all RobotDeathEvents currently in the robot's queue.
 * You might, for example, call this while processing another event.
 *
 * <P>Example:
 * <pre>
 *   Vector v = getRobotDeathEvents();
 *   RobotDeathEvent e;
 *   for (int i = 0; i < e.size(); i++) {
 *      e = (RobotDeathEvent)v.elementAt(i);
 *      <i> (do something with e) </i>
 *   }
 * </pre>
 *
 * @see #onRobotDeath
 * @see robocode.RobotDeathEvent
 * @see java.util.Vector
 */

public synchronized java.util.Vector getRobotDeathEvents() {
	Vector events = new Vector();
	for (int i = 0; i < eventQueue.size(); i++)
	{
		if (eventQueue.elementAt(i) instanceof RobotDeathEvent)
		{
			events.add(eventQueue.elementAt(i));
		}
	}
	return events;
}
/**
 * Insert the method's description here.
 * Creation date: (9/26/2001 2:37:09 PM)
 * @return int
 */
public int getScannedRobotEventPriority() {
	return scannedRobotEventPriority;
}
/**
 * Returns a vector containing all ScannedRobotEvents currently in the robot's queue.
 * You might, for example, call this while processing another event.
 *
 * <P>Example:
 * <pre>
 *   Vector v = getScannedRobotEvents();
 *   ScannedRobotEvent e;
 *   for (int i = 0; i < e.size(); i++) {
 *      e = (ScannedRobotEvent)v.elementAt(i);
 *      <i> (do something with e) </i>
 *   }
 * </pre>
 *
 * @see #onScannedRobot
 * @see robocode.ScannedRobotEvent
 * @see java.util.Vector
 */
public synchronized java.util.Vector getScannedRobotEvents() {
	Vector events = new Vector();
	for (int i = 0; i < eventQueue.size(); i++)
	{
		if (eventQueue.elementAt(i) instanceof ScannedRobotEvent)
		{
			events.add(eventQueue.elementAt(i));
		}
	}
	return events;
}
/**
 * Insert the method's description here.
 * Creation date: (12/21/2000 2:24:41 PM)
 * @return long
 */
public synchronized long getTime() {
	return robotPeer.getTime();
}
/**
 * Insert the method's description here.
 * Creation date: (9/10/2001 7:06:41 PM)
 * @return boolean
 */
public boolean isFireAssistValid() {
	return fireAssistValid;
}
	/**
 * Insert the method's description here.
 * Creation date: (1/25/2001 12:40:07 PM)
 * @return int
 * @param e robocode.BulletHitEvent
 */
public void onBulletHit(BulletHitEvent e) {
	if (getRobot() != null) getRobot().onBulletHit(e);
}
/**
 * Insert the method's description here.
 * Creation date: (1/25/2001 12:40:07 PM)
 * @return int
 * @param e robocode.BulletHitBulletEvent
 */
public void onBulletHitBullet(BulletHitBulletEvent e) {
	if (getRobot() != null) getRobot().onBulletHitBullet(e);
}
/**
 * Insert the method's description here.
 * Creation date: (1/25/2001 12:40:36 PM)
 * @return int
 * @param e robocode.BulletMissedEvent
 */
public void onBulletMissed(BulletMissedEvent e) {
	if (getRobot() != null) getRobot().onBulletMissed(e);
}
/**
 * Insert the method's description here.
 * Creation date: (1/25/2001 12:39:43 PM)
 * @return int
 * @param e robocode.CustomEvent
 */
public void onCustomEvent(CustomEvent e) {
	if (getRobot() != null) 
	{
		if (getRobot() instanceof AdvancedRobot)
		{
			((AdvancedRobot)getRobot()).onCustomEvent(e);
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (1/25/2001 12:42:29 PM)
 * @return int
 * @param e robocode.DeathEvent
 */
public void onDeath(DeathEvent e) {
	if (getRobot() != null) getRobot().onDeath(e);
}
/**
 * Insert the method's description here.
 * Creation date: (1/25/2001 12:39:43 PM)
 * @return int
 * @param e robocode.HitByBulletEvent
 */
public void onHitByBullet(HitByBulletEvent e) {
	if (getRobot() != null) getRobot().onHitByBullet(e);
}
/**
 * Insert the method's description here.
 * Creation date: (1/25/2001 12:39:01 PM)
 * @return int
 * @param e robocode.HitRobotEvent
 */
public void onHitRobot(HitRobotEvent e) {
	if (getRobot() != null) getRobot().onHitRobot(e);
}
/**
 * Insert the method's description here.
 * Creation date: (1/25/2001 10:23:24 AM)
 * @param e robocode.HitWallEvent
 */
public void onHitWall(HitWallEvent e) {
	if (getRobot() != null) getRobot().onHitWall(e);
}
/**
 * Insert the method's description here.
 * Creation date: (1/25/2001 12:41:23 PM)
 * @return int
 * @param e robocode.RobotDeathEvent
 */
public void onRobotDeath(RobotDeathEvent e) {
	if (getRobot() != null) getRobot().onRobotDeath(e);
}
/**
 * Insert the method's description here.
 * Creation date: (1/25/2001 12:41:05 PM)
 * @return int
 * @param e robocode.ScannedRobotEvent
 */
public void onScannedRobot(ScannedRobotEvent e) {
	if (getRobot() != null) getRobot().onScannedRobot(e);
}
/**
 * Insert the method's description here.
 * Creation date: (1/25/2001 12:42:29 PM)
 * @return int
 * @param e robocode.SkippedTurnEvent
 */
public void onSkippedTurn(SkippedTurnEvent e) {
	Robot r = getRobot();
	if (r != null && r instanceof AdvancedRobot)
		((AdvancedRobot)r).onSkippedTurn(e);
}
/**
 * Insert the method's description here.
 * Creation date: (1/25/2001 12:42:29 PM)
 * @return int
 * @param e robocode.SkippedTurnEvent
 */
public void onMessageReceived(MessageEvent e) {
	Robot r = getRobot();
	if (r != null && r instanceof TeamRobot)
		((TeamRobot)r).onMessageReceived(e);
}
/**
 * Insert the method's description here.
 * Creation date: (1/25/2001 12:42:56 PM)
 * @return int
 * @param e robocode.WinEvent
 */
public void onWin(WinEvent e) {
	if (getRobot() != null) getRobot().onWin(e);
}
/**
 * Insert the method's description here.
 * Creation date: (9/10/2001 6:56:39 PM)
 */
public void processEvents() {
		// Process custom events
	if (customEvents != null)
	{
		Condition c;
		for (int i = 0; i < customEvents.size(); i++)
		{
			c = (Condition)customEvents.elementAt(i);
			robotPeer.setTestingCondition(true);
			boolean conditionSatisfied = c.test();
			robotPeer.setTestingCondition(false);
			if (conditionSatisfied)
			{
				eventQueue.add(new CustomEvent(c));
			}
		}
	}
	
	// Process event queue here
	int numEvents = eventQueue.size();
	eventQueue.sort();
	Event currentEvent = null;
	if (eventQueue.size() > 0)
	{
		currentEvent = (Event)eventQueue.elementAt(0);
		/*
		if (currentTopEventPriority > Integer.MIN_VALUE &&
			currentEvent.getPriority() == currentTopEventPriority &&
			getInterruptable(currentTopEventPriority))
		{
			setInterruptable(currentTopEventPriority,false); // we're going to restart it, so reset.
			robotPeer.out.println("I am going to interrupt an event.  CurrentTop is "+ currentTopEventPriority + ".  New event has priority " + currentEvent.getPriority());
			// We are already in an event handler, took action, and a new event
			// was generated.  So we want to break out of the old handler to process it here.
			throw new EventInterruptedException(currentEvent.getPriority());
		}
		*/
		
	}
	
	int callingEventPriority = currentTopEventPriority;
	//robotPeer.out.println("Entering event loop with ctep: " + currentTopEventPriority);
	long clearTime = getTime() - 1;

	while (currentEvent != null && currentEvent.getPriority() >= currentTopEventPriority)
	{
		//robotPeer.out.println("processing event of priority: " + currentEvent.getPriority() + " in loop with ctep: " + currentTopEventPriority);
		if (currentTopEventPriority > Integer.MIN_VALUE &&
			currentEvent.getPriority() == currentTopEventPriority &&
			getInterruptible(currentTopEventPriority))
		{
			setInterruptible(currentTopEventPriority,false); // we're going to restart it, so reset.
			//robotPeer.out.println("I am going to interrupt an event.  CurrentTop is "+ currentTopEventPriority + ".  New event has priority " + currentEvent.getPriority());
			// We are already in an event handler, took action, and a new event
			// was generated.  So we want to break out of the old handler to process it here.
			throw new EventInterruptedException(currentEvent.getPriority());
		}
		else if (currentEvent.getPriority() == currentTopEventPriority)
		{
			//robotPeer.out.println("Next event is same priority but not interruptable, ending loop.");
			break;
		}
		
		int oldTopEventPriority = currentTopEventPriority;
		currentTopEventPriority = currentEvent.getPriority();
		
		eventQueue.remove(currentEvent);
		try {
			if (currentEvent instanceof HitWallEvent) {
				onHitWall((HitWallEvent)currentEvent);
			}
			else if (currentEvent instanceof HitRobotEvent) {
				onHitRobot((HitRobotEvent)currentEvent);
			}
			else if (currentEvent instanceof HitByBulletEvent) {
				onHitByBullet((HitByBulletEvent)currentEvent);
			}
			else if (currentEvent instanceof BulletHitEvent) {
				onBulletHit((BulletHitEvent)currentEvent);
			}
			else if (currentEvent instanceof BulletHitBulletEvent) {
				onBulletHitBullet((BulletHitBulletEvent)currentEvent);
			}
			else if (currentEvent instanceof BulletMissedEvent) {
				onBulletMissed((BulletMissedEvent)currentEvent);
			}
			else if (currentEvent instanceof ScannedRobotEvent) {
				if (getTime() == currentEvent.getTime() &&
					robotPeer.getGunHeading() == robotPeer.getRadarHeading() && robotPeer.getLastGunHeading() == robotPeer.getLastRadarHeading() && getRobot() != null && !(getRobot() instanceof AdvancedRobot))
				{
					fireAssistAngle = Utils.normalAbsoluteAngle(robotPeer.getHeading() + ((ScannedRobotEvent)currentEvent).getBearingRadians());
					if (useFireAssist)
						fireAssistValid = true;
				}
				onScannedRobot((ScannedRobotEvent)currentEvent);
				fireAssistValid = false;
			}
			else if (currentEvent instanceof RobotDeathEvent) {
				onRobotDeath((RobotDeathEvent)currentEvent);
			}
			else if (currentEvent instanceof SkippedTurnEvent) {
				onSkippedTurn((SkippedTurnEvent)currentEvent);
			}
			else if (currentEvent instanceof MessageEvent) {
				onMessageReceived((MessageEvent)currentEvent);
			}
			else if (currentEvent instanceof DeathEvent) {
				onDeath((DeathEvent)currentEvent);
				robotPeer.death();
			}
			else if (currentEvent instanceof WinEvent) {
				onWin((WinEvent)currentEvent);
			}
			else if (currentEvent instanceof CustomEvent) {
				onCustomEvent((CustomEvent)currentEvent);
			}
			else robotPeer.out.println("Unknown event: " + currentEvent);
			setInterruptible(currentTopEventPriority,false);
	
		} catch (EventInterruptedException e) {
			fireAssistValid = false;
	 		currentEvent = (Event)eventQueue.elementAt(0);
			//robotPeer.out.println("Event of priority: " + currentTopEventPriority + " interrupted with event of priority " + currentEvent.getPriority());
			//robotPeer.out.println("There are " + eventQueue.size() + " events now... clearing old ones.");
		  	//eventQueue.clear(getTime() - 1);
			//robotPeer.out.println("There are now " + eventQueue.size() + " events.");
		} catch (RuntimeException e) {
		  	//eventQueue.clear(getTime() - 1);
			currentTopEventPriority = oldTopEventPriority;
			throw e;
		} catch (Error e) {
//			System.out.println("Caught: " + e);
		  	//eventQueue.clear(getTime() - 1);
			currentTopEventPriority = oldTopEventPriority;
			throw e;
		}
		currentTopEventPriority = oldTopEventPriority;
		//robotPeer.out.println("End of loop, resetting ctep to: " + currentTopEventPriority);
		if (eventQueue.size() > 0)
	 		currentEvent = (Event)eventQueue.elementAt(0);
	 	else
	 		currentEvent = null;
	}
  	//eventQueue.clear(clearTime);
}
/**
 * Insert the method's description here.
 * Creation date: (1/27/2001 10:29:36 AM)
 * @param condition robocode.JCondition
 */
public void removeCustomEvent(Condition condition) {
	customEvents.remove(condition);
}
/**
 * Insert the method's description here.
 * Creation date: (9/10/2001 6:14:36 PM)
 */
public synchronized void reset() {
	currentTopEventPriority = Integer.MIN_VALUE;
	clearAllEvents(true);
	customEvents.clear();
}
/**
 * Insert the method's description here.
 * Creation date: (1/25/2001 10:25:03 AM)
 * @param c java.lang.Class
 * @param priority int
 */
public void setEventPriority(String eventClass, int priority) {
	if (priority < 0)
	{
		robotPeer.out.println("SYSTEM: Priority must be between 0 and 99.");
		robotPeer.out.println("SYSTEM: Priority for " + eventClass + " will be 0.");
		priority = 0;
	}
	else if (priority > 99)
	{
		robotPeer.out.println("SYSTEM: Priority must be between 0 and 99.");
		robotPeer.out.println("SYSTEM: Priority for " + eventClass + " will be 99.");
		priority = 99;
	}
	if (eventClass.equals("robocode.BulletHitEvent") || eventClass.equals("BulletHitEvent"))
		bulletHitEventPriority = priority;
	else if (eventClass.equals("robocode.BulletHitBulletEvent") || eventClass.equals("BulletHitBulletEvent"))
		bulletHitBulletEventPriority = priority;
	else if (eventClass.equals("robocode.BulletMissedEvent") || eventClass.equals("BulletMissedEvent"))
		bulletMissedEventPriority = priority;
	else if (eventClass.equals("robocode.HitByBulletEvent") || eventClass.equals("HitByBulletEvent"))
		hitByBulletEventPriority = priority;
	else if (eventClass.equals("robocode.HitRobotEvent") || eventClass.equals("HitRobotEvent"))
		hitRobotEventPriority = priority;
	else if (eventClass.equals("robocode.HitWallEvent") || eventClass.equals("HitWallEvent"))
		hitWallEventPriority = priority;
	else if (eventClass.equals("robocode.RobotDeathEvent") || eventClass.equals("RobotDeathEvent"))
		robotDeathEventPriority = priority;
	else if (eventClass.equals("robocode.ScannedRobotEvent") || eventClass.equals("ScannedRobotEvent"))
		scannedRobotEventPriority = priority;
	else if (eventClass.equals("robocode.CustomEvent") || eventClass.equals("CustomEvent"))
		robotPeer.out.println("SYSTEM: To change the priority of a CustomEvent, set it in the Condition.  setPriority ignored.");
	else if (eventClass.equals("robocode.SkippedTurnEvent") || eventClass.equals("SkippedTurnEvent"))
		robotPeer.out.println("SYSTEM: You may not change the priority of SkippedTurnEvent.  setPriority ignored.");
	else if (eventClass.equals("robocode.WinEvent") || eventClass.equals("WinEvent"))
		robotPeer.out.println("SYSTEM: You may not change the priority of WinEvent.  setPriority ignored.");
	else if (eventClass.equals("robocode.DeathEvent") || eventClass.equals("DeathEvent"))
		robotPeer.out.println("SYSTEM: You may not change the priority of DeathEvent.  setPriority ignored.");
	else
		robotPeer.out.println("SYSTEM: Unknown event class: " + eventClass);
}
/**
 * Insert the method's description here.
 * Creation date: (9/10/2001 7:06:41 PM)
 * @param newFireAssistValid boolean
 */
public void setFireAssistValid(boolean newFireAssistValid) {
	fireAssistValid = newFireAssistValid;
}
/**
 * Insert the method's description here.
 * Creation date: (9/28/2001 12:58:47 PM)
 * @param interruptable boolean
 */
public void setInterruptible(int priority, boolean interruptable) {
	if (priority < 0 || priority > 99)
		return;
		
	this.interruptible[priority] = interruptable;
}
}
