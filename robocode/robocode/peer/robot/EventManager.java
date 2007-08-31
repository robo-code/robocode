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
 *     Matthew Reeder
 *     - Fix for HyperThreading hang issue with the getTime() method that was
 *       synchronized before, which sometimes caused a deadlock to occur in the
 *       code processing the hitWall event.
 *     Flemming N. Larsen
 *     - Ported to Java 5.0
 *     - Bugfix: Added setting and getting the priority of BulletHitBulletEvent
 *     - Added missing getMessageEvents()
 *     - Code cleanup
 *     - Added features to support the new JuniorRobot class
 *     Robert D. Maupin
 *     - Replaced old collection types like Vector and Hashtable with
 *       synchronized List and HashMap
 *     Nathaniel Troutman
 *     - Added cleanup() method for cleaning up references to internal classes
 *       to prevent circular references causing memory leaks
 *******************************************************************************/
package robocode.peer.robot;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import robocode.*;
import robocode.exception.EventInterruptedException;
import robocode.peer.RobotPeer;
import robocode.util.Utils;


/**
 * @author Mathew A. Nelson (original)
 * @author Matthew Reeder (contributor)
 * @author Flemming N. Larsen (contributor)
 * @author Robert D. Maupin (contributor)
 * @author Nathaniel Troutman (contributor)
 */
public class EventManager {
	private RobotPeer robotPeer = null;

	private int scannedRobotEventPriority = 10;
	private int hitByBulletEventPriority = 20;
	private int hitWallEventPriority = 30;
	private int hitRobotEventPriority = 40;
	private int bulletHitEventPriority = 50;
	private int bulletHitBulletEventPriority = 55;
	private int bulletMissedEventPriority = 60;
	private int robotDeathEventPriority = 70;
	private int messageEventPriority = 75;
	// custom events defaults to 80
	private int skippedTurnEventPriority = 100;
	private int winEventPriority = 100;
	private int deathEventPriority = 100;

	private int currentTopEventPriority;

	private List<Condition> customEvents = Collections.synchronizedList(new ArrayList<Condition>());
	private EventQueue eventQueue;

	private double fireAssistAngle;
	private boolean fireAssistValid = false;
	private boolean useFireAssist = true;

	private int maxPriorities = 101;
	private boolean interruptible[] = new boolean[maxPriorities];

	private final static int MAX_QUEUE_SIZE = 256;

	private _RobotBase robot;

	/**
	 * EventManager constructor comment.
	 */
	public EventManager(RobotPeer r) {
		super();
		this.robotPeer = r;
		eventQueue = new EventQueue(this);
		reset();
	}

	public boolean add(Event e) {
		if (eventQueue != null) {
			if (eventQueue.size() > MAX_QUEUE_SIZE) {
				System.out.println(
						"Not adding to " + robotPeer.getName() + "'s queue, exceeded " + MAX_QUEUE_SIZE + " events in queue.");
				return false;
			}
			return eventQueue.add(e);
		}
		return false;
	}

	public void addCustomEvent(Condition condition) {
		customEvents.add(condition);
	}

	public void clearAllEvents(boolean includingSystemEvents) {
		eventQueue.clear(includingSystemEvents);
	}

	public void clear(long clearTime) {
		eventQueue.clear(clearTime);
	}
        
	public void cleanup() {
		// Remove all events
		reset();

		// Remove all references to robots
		robot = null;
		robotPeer = null;
	}

	/**
	 * Returns a list containing all events currently in the robot's queue.
	 * You might, for example, call this while processing another event.
	 *
	 * <P>Example:
	 * <pre>
	 *    for (Event e : getAllEvents()) {
	 *       if (e instanceof HitByRobotEvent)
	 *        <i> (do something with e) </i>
	 *       else if (e instanceof HitByBulletEvent)
	 *        <i> (so something else with e) </i>
	 *    }
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
	 * @see List
	 */
	public List<Event> getAllEvents() {
		List<Event> events = Collections.synchronizedList(new ArrayList<Event>());

		for (Event e : eventQueue) {
			events.add(e);
		}
		return events;
	}

	/**
	 * Returns a list containing all BulletHitBulletEvents currently in the robot's queue.
	 * You might, for example, call this while processing another event.
	 *
	 * <P>Example:
	 * <pre>
	 *    for (BulletHitBulletEvent e : getBulletHitBulletEvents()) {
	 *      <i> (do something with e) </i>
	 *    }
	 * </pre>
	 *
	 * @see #onBulletHitBullet
	 * @see robocode.BulletHitBulletEvent
	 * @see List
	 */
	public List<BulletHitBulletEvent> getBulletHitBulletEvents() {
		List<BulletHitBulletEvent> events = Collections.synchronizedList(new ArrayList<BulletHitBulletEvent>());

		for (Event e : eventQueue) {
			if (e instanceof BulletHitBulletEvent) {
				events.add((BulletHitBulletEvent) e);
			}
		}
		return events;
	}

	/**
	 * Returns a list containing all BulletHitEvents currently in the robot's queue.
	 * You might, for example, call this while processing another event.
	 *
	 * <P>Example:
	 * <pre>
	 *    for (BulletHitEvent e : getBulletHitEvents()) {
	 *      <i> (do something with e) </i>
	 *    }
	 * </pre>
	 *
	 * @see #onBulletHit
	 * @see robocode.BulletHitEvent
	 * @see List
	 */
	public List<BulletHitEvent> getBulletHitEvents() {
		List<BulletHitEvent> events = Collections.synchronizedList(new ArrayList<BulletHitEvent>());

		for (Event e : eventQueue) {
			if (e instanceof BulletHitEvent) {
				events.add((BulletHitEvent) e);
			}
		}
		return events;
	}

	/**
	 * Returns a list containing all BulletMissedEvents currently in the robot's queue.
	 * You might, for example, call this while processing another event.
	 *
	 * <P>Example:
	 * <pre>
	 *    for (BulletMissedEvent e : getBulletMissedEvents()) {
	 *      <i> (do something with e) </i>
	 *    }
	 * </pre>
	 *
	 * @see #onBulletMissed
	 * @see robocode.BulletMissedEvent
	 * @see List
	 */
	public List<BulletMissedEvent> getBulletMissedEvents() {
		List<BulletMissedEvent> events = Collections.synchronizedList(new ArrayList<BulletMissedEvent>());

		for (Event e : eventQueue) {
			if (e instanceof BulletMissedEvent) {
				events.add((BulletMissedEvent) e);
			}
		}
		return events;
	}

	public int getCurrentTopEventPriority() {
		return currentTopEventPriority;
	}

	public int getEventPriority(String eventClass) {
		if (eventClass.equals("robocode.BulletHitEvent") || eventClass.equals("BulletHitEvent")) {
			return bulletHitEventPriority;
		} else if (eventClass.equals("robocode.BulletMissedEvent") || eventClass.equals("BulletMissedEvent")) {
			return bulletMissedEventPriority;
		} else if (eventClass.equals("robocode.DeathEvent") || eventClass.equals("DeathEvent")) {
			return deathEventPriority;
		} else if (eventClass.equals("robocode.HitByBulletEvent") || eventClass.equals("HitByBulletEvent")) {
			return hitByBulletEventPriority;
		} else if (eventClass.equals("robocode.HitRobotEvent") || eventClass.equals("HitRobotEvent")) {
			return hitRobotEventPriority;
		} else if (eventClass.equals("robocode.HitWallEvent") || eventClass.equals("HitWallEvent")) {
			return hitWallEventPriority;
		} else if (eventClass.equals("robocode.RobotDeathEvent") || eventClass.equals("RobotDeathEvent")) {
			return robotDeathEventPriority;
		} else if (eventClass.equals("robocode.ScannedRobotEvent") || eventClass.equals("ScannedRobotEvent")) {
			return scannedRobotEventPriority;
		} else if (eventClass.equals("robocode.MessageEvent") || eventClass.equals("MessageEvent")) {
			return messageEventPriority;
		} else if (eventClass.equals("robocode.SkippedTurnEvent") || eventClass.equals("SkippedTurnEvent")) {
			return skippedTurnEventPriority;
		} else if (eventClass.equals("robocode.WinEvent") || eventClass.equals("WinEvent")) {
			return winEventPriority;
		} else if (eventClass.equals("robocode.BulletHitBulletEvent") || eventClass.equals("BulletHitBulletEvent")) {
			return bulletHitBulletEventPriority;
		} else {
			return -1;
		}
	}

	public int getEventPriority(Event e) {
		if (e instanceof ScannedRobotEvent) {
			return scannedRobotEventPriority;
		}
		if (e instanceof BulletHitEvent) {
			return bulletHitEventPriority;
		}
		if (e instanceof BulletMissedEvent) {
			return bulletMissedEventPriority;
		}
		if (e instanceof DeathEvent) {
			return deathEventPriority;
		}
		if (e instanceof HitByBulletEvent) {
			return hitByBulletEventPriority;
		}
		if (e instanceof HitRobotEvent) {
			return hitRobotEventPriority;
		}
		if (e instanceof HitWallEvent) {
			return hitWallEventPriority;
		}
		if (e instanceof RobotDeathEvent) {
			return robotDeathEventPriority;
		}
		if (e instanceof SkippedTurnEvent) {
			return skippedTurnEventPriority;
		}
		if (e instanceof MessageEvent) {
			return messageEventPriority;
		}
		if (e instanceof WinEvent) {
			return winEventPriority;
		}
		if (e instanceof CustomEvent) {
			return ((CustomEvent) e).getCondition().getPriority();
		}
		return 0;
	}

	public double getFireAssistAngle() {
		return fireAssistAngle;
	}

	/**
	 * Returns a list containing all HitByBulletEvents currently in the robot's queue.
	 * You might, for example, call this while processing another event.
	 *
	 * <P>Example:
	 * <pre>
	 *    for (HitByBulletEvent e : getHitByBulletEvents()) {
	 *      <i> (do something with e) </i>
	 *    }
	 * </pre>
	 *
	 * @see #onHitByBullet
	 * @see robocode.HitByBulletEvent
	 * @see List
	 */
	public List<HitByBulletEvent> getHitByBulletEvents() {
		List<HitByBulletEvent> events = Collections.synchronizedList(new ArrayList<HitByBulletEvent>());

		for (Event e : eventQueue) {
			if (e instanceof HitByBulletEvent) {
				events.add((HitByBulletEvent) e);
			}
		}
		return events;
	}

	/**
	 * Returns a list containing all HitRobotEvents currently in the robot's queue.
	 * You might, for example, call this while processing another event.
	 *
	 * <P>Example:
	 * <pre>
	 *    for (HitRobotEvent e : getHitRobotEvents()) {
	 *      <i> (do something with e) </i>
	 *    }
	 * </pre>
	 *
	 * @see #onHitRobot
	 * @see robocode.HitRobotEvent
	 * @see List
	 */
	public List<HitRobotEvent> getHitRobotEvents() {
		List<HitRobotEvent> events = Collections.synchronizedList(new ArrayList<HitRobotEvent>());

		for (Event e : eventQueue) {
			if (e instanceof HitRobotEvent) {
				events.add((HitRobotEvent) e);
			}
		}
		return events;
	}

	/**
	 * Returns a list containing all HitWallEvents currently in the robot's queue.
	 * You might, for example, call this while processing another event.
	 *
	 * <P>Example:
	 * <pre>
	 *    for (HitWallEvent e : getHitWallEvents()) {
	 *      <i> (do something with e) </i>
	 *    }
	 * </pre>
	 *
	 * @see #onHitWall
	 * @see robocode.HitWallEvent
	 * @see List
	 */
	public List<HitWallEvent> getHitWallEvents() {
		List<HitWallEvent> events = Collections.synchronizedList(new ArrayList<HitWallEvent>());

		for (Event e : eventQueue) {
			if (e instanceof HitWallEvent) {
				events.add((HitWallEvent) e);
			}
		}
		return events;
	}

	public boolean getInterruptible(int priority) {
		return this.interruptible[priority];
	}

	private _RobotBase getRobot() {
		return robot;
	}

	public void setRobot(_RobotBase r) {
		this.robot = r;
		if (r instanceof AdvancedRobot) {
			useFireAssist = false;
		}
	}

	/**
	 * Returns a list containing all RobotDeathEvents currently in the robot's queue.
	 * You might, for example, call this while processing another event.
	 *
	 * <P>Example:
	 * <pre>
	 *    for (RobotDeathEvent e : getRobotDeathEvents()) {
	 *      <i> (do something with e) </i>
	 *    }
	 * </pre>
	 *
	 * @see #onRobotDeath
	 * @see robocode.RobotDeathEvent
	 * @see List
	 */
	public List<RobotDeathEvent> getRobotDeathEvents() {
		List<RobotDeathEvent> events = Collections.synchronizedList(new ArrayList<RobotDeathEvent>());

		for (Event e : eventQueue) {
			if (e instanceof RobotDeathEvent) {
				events.add((RobotDeathEvent) e);
			}
		}
		return events;
	}

	public int getScannedRobotEventPriority() {
		return scannedRobotEventPriority;
	}

	/**
	 * Returns a list containing all ScannedRobotEvents currently in the robot's queue.
	 * You might, for example, call this while processing another event.
	 *
	 * <P>Example:
	 * <pre>
	 *    for (ScannedRobotEvent e : getScannedRobotEvents()) {
	 *      <i> (do something with e) </i>
	 *    }
	 * </pre>
	 *
	 * @see #onScannedRobot
	 * @see robocode.ScannedRobotEvent
	 * @see List
	 */
	public List<ScannedRobotEvent> getScannedRobotEvents() {
		List<ScannedRobotEvent> events = Collections.synchronizedList(new ArrayList<ScannedRobotEvent>());

		for (Event e : eventQueue) {
			if (e instanceof ScannedRobotEvent) {
				events.add((ScannedRobotEvent) e);
			}
		}
		return events;
	}

	public long getTime() {
		return robotPeer.getTime();
	}

	public boolean isFireAssistValid() {
		return fireAssistValid;
	}

	public void onBulletHit(BulletHitEvent e) {
		_RobotBase robot = getRobot();

		if (robot != null && robot instanceof Robot) {
			((Robot) robot).onBulletHit(e);
		}
	}

	public void onBulletHitBullet(BulletHitBulletEvent e) {
		_RobotBase robot = getRobot();

		if (robot != null && robot instanceof Robot) {
			((Robot) robot).onBulletHitBullet(e);
		}
	}

	public void onBulletMissed(BulletMissedEvent e) {
		_RobotBase robot = getRobot();

		if (robot != null && robot instanceof Robot) {
			((Robot) robot).onBulletMissed(e);
		}
	}

	public void onCustomEvent(CustomEvent e) {
		_RobotBase robot = getRobot();

		if (robot != null) {
			if (robot instanceof AdvancedRobot) {
				((AdvancedRobot) robot).onCustomEvent(e);

			} else if (robot instanceof JuniorRobot) {
				Condition c = e.getCondition();

				if (c instanceof RobotPeer.GunReadyCondition) {
					((JuniorRobot) robot).gunReady = true;

				} else if (c instanceof RobotPeer.GunFireCondition) {
					double firePower = robotPeer.getJuniorFirePower(); 

					if (firePower > 0) {
						if (robotPeer.setFire(firePower) != null) {
							((JuniorRobot) robot).gunReady = false;
						}
						robotPeer.setJuniorFire(0);
					}
				}
			}
		}
	}

	public void onDeath(DeathEvent e) {
		_RobotBase robot = getRobot();

		if (robot != null && robot instanceof Robot) {
			((Robot) robot).onDeath(e);
		}
	}

	public void onHitByBullet(HitByBulletEvent e) {
		_RobotBase robot = getRobot();

		if (robot != null) {
			if (robot instanceof JuniorRobot) {
				JuniorRobot jr = ((JuniorRobot) robot);

				robotPeer.updateJuniorRobotFields();

				jr.hitByBulletAngle = (int) (Math.toDegrees(
						Utils.normalAbsoluteAngle(robotPeer.getHeading() + e.getBearingRadians()))
								+ 0.5);
				jr.hitByBulletBearing = (int) (e.getBearing() + 0.5);
				jr.onHitByBullet();
			} else {
				((Robot) robot).onHitByBullet(e);
			}
		}
	}

	public void onHitRobot(HitRobotEvent e) {
		_RobotBase robot = getRobot();

		if (robot != null) {
			if (robot instanceof JuniorRobot) {
				JuniorRobot jr = ((JuniorRobot) robot);

				robotPeer.updateJuniorRobotFields();

				jr.hitRobotAngle = (int) (Math.toDegrees(
						Utils.normalAbsoluteAngle(robotPeer.getHeading() + e.getBearingRadians()))
								+ 0.5);
				jr.hitRobotBearing = (int) (e.getBearing() + 0.5);
				jr.onHitRobot();
			} else {
				((Robot) robot).onHitRobot(e);
			}
		}
	}

	public void onHitWall(HitWallEvent e) {
		_RobotBase robot = getRobot();

		if (robot != null) {
			if (robot instanceof JuniorRobot) {
				JuniorRobot jr = ((JuniorRobot) robot);

				robotPeer.updateJuniorRobotFields();

				jr.hitWallAngle = (int) (Math.toDegrees(
						Utils.normalAbsoluteAngle(robotPeer.getHeading() + e.getBearingRadians()))
								+ 0.5);
				jr.hitWallBearing = (int) (e.getBearing() + 0.5);
				jr.onHitWall();
			} else {
				((Robot) robot).onHitWall(e);
			}
		}
	}

	public void onRobotDeath(RobotDeathEvent e) {
		_RobotBase robot = getRobot();

		if (robot != null) {
			if (robot instanceof JuniorRobot) {
				JuniorRobot jr = ((JuniorRobot) robot);

				jr.others = robotPeer.getOthers();
			} else {
				((Robot) robot).onRobotDeath(e);
			}
		}
	}

	public void onScannedRobot(ScannedRobotEvent e) {
		_RobotBase robot = getRobot();

		if (robot != null) {
			if (robot instanceof JuniorRobot) {
				JuniorRobot jr = ((JuniorRobot) robot);

				jr.scannedDistance = (int) (e.getDistance() + 0.5);
				jr.scannedEnergy = Math.max(1, (int) (e.getEnergy() + 0.5));
				jr.scannedAngle = (int) (Math.toDegrees(
						Utils.normalAbsoluteAngle(robotPeer.getHeading() + e.getBearingRadians()))
								+ 0.5);
				jr.scannedBearing = (int) (e.getBearing() + 0.5);
				jr.scannedHeading = (int) (e.getHeading() + 0.5);
				jr.scannedVelocity = (int) (e.getVelocity() + 0.5);
				jr.onScannedRobot();
			} else {
				((Robot) robot).onScannedRobot(e);
			}
		}
	}

	public void onSkippedTurn(SkippedTurnEvent e) {
		_RobotBase robot = getRobot();

		if (robot != null && robot instanceof AdvancedRobot) {
			((AdvancedRobot) robot).onSkippedTurn(e);
		}
	}

	public void onMessageReceived(MessageEvent e) {
		_RobotBase robot = getRobot();

		if (robot != null && robot instanceof TeamRobot) {
			((TeamRobot) robot).onMessageReceived(e);
		}
	}

	public void onWin(WinEvent e) {
		_RobotBase robot = getRobot();

		if (robot != null && robot instanceof Robot) {
			((Robot) robot).onWin(e);
		}
	}

	public void processEvents() {
		// Process custom events
		if (customEvents != null) {
			Condition c;
			boolean conditionSatisfied;

			// Do not turn this into a "for each" loop as this will cause a
			// ConcurrentModificationException!
			for (int i = 0; i < customEvents.size(); i++) {
				c = customEvents.get(i);
				robotPeer.setTestingCondition(true);
				conditionSatisfied = c.test();
				robotPeer.setTestingCondition(false);
				if (conditionSatisfied) {
					eventQueue.add(new CustomEvent(c));
				}
			}
		}

		// Process event queue here
		eventQueue.sort();
		Event currentEvent = null;

		if (eventQueue.size() > 0) {
			currentEvent = eventQueue.get(0);
		}
		while (currentEvent != null && currentEvent.getPriority() >= currentTopEventPriority) {
			// robotPeer.out.println("processing event of priority: " + currentEvent.getPriority() + " in loop with ctep: " + currentTopEventPriority);
			if (currentTopEventPriority > Integer.MIN_VALUE && currentEvent.getPriority() == currentTopEventPriority
					&& getInterruptible(currentTopEventPriority)) {
				setInterruptible(currentTopEventPriority, false); // we're going to restart it, so reset.

				// We are already in an event handler, took action, and a new event was generated.
				// So we want to break out of the old handler to process it here.
				throw new EventInterruptedException(currentEvent.getPriority());
			} else if (currentEvent.getPriority() == currentTopEventPriority) {
				// robotPeer.out.println("Next event is same priority but not interruptable, ending loop.");
				break;
			}

			int oldTopEventPriority = currentTopEventPriority;

			currentTopEventPriority = currentEvent.getPriority();

			eventQueue.remove(currentEvent);
			try {
				if (currentEvent instanceof HitWallEvent) {
					onHitWall((HitWallEvent) currentEvent);
				} else if (currentEvent instanceof HitRobotEvent) {
					onHitRobot((HitRobotEvent) currentEvent);
				} else if (currentEvent instanceof HitByBulletEvent) {
					onHitByBullet((HitByBulletEvent) currentEvent);
				} else if (currentEvent instanceof BulletHitEvent) {
					onBulletHit((BulletHitEvent) currentEvent);
				} else if (currentEvent instanceof BulletHitBulletEvent) {
					onBulletHitBullet((BulletHitBulletEvent) currentEvent);
				} else if (currentEvent instanceof BulletMissedEvent) {
					onBulletMissed((BulletMissedEvent) currentEvent);
				} else if (currentEvent instanceof ScannedRobotEvent) {
					if (getTime() == currentEvent.getTime() && robotPeer.getGunHeading() == robotPeer.getRadarHeading()
							&& robotPeer.getLastGunHeading() == robotPeer.getLastRadarHeading() && getRobot() != null
							&& !(getRobot() instanceof AdvancedRobot)) {
						fireAssistAngle = Utils.normalAbsoluteAngle(
								robotPeer.getHeading() + ((ScannedRobotEvent) currentEvent).getBearingRadians());
						if (useFireAssist) {
							fireAssistValid = true;
						}
					}
					onScannedRobot((ScannedRobotEvent) currentEvent);
					fireAssistValid = false;
				} else if (currentEvent instanceof RobotDeathEvent) {
					onRobotDeath((RobotDeathEvent) currentEvent);
				} else if (currentEvent instanceof SkippedTurnEvent) {
					onSkippedTurn((SkippedTurnEvent) currentEvent);
				} else if (currentEvent instanceof MessageEvent) {
					onMessageReceived((MessageEvent) currentEvent);
				} else if (currentEvent instanceof DeathEvent) {
					onDeath((DeathEvent) currentEvent);
					robotPeer.death();
				} else if (currentEvent instanceof WinEvent) {
					onWin((WinEvent) currentEvent);
				} else if (currentEvent instanceof CustomEvent) {
					onCustomEvent((CustomEvent) currentEvent);
				} else {
					robotPeer.out.println("Unknown event: " + currentEvent);
				}
				setInterruptible(currentTopEventPriority, false);

			} catch (EventInterruptedException e) {
				fireAssistValid = false;
			} catch (RuntimeException e) {
				currentTopEventPriority = oldTopEventPriority;
				throw e;
			} catch (Error e) {
				currentTopEventPriority = oldTopEventPriority;
				throw e;
			}
			currentTopEventPriority = oldTopEventPriority;
			if (eventQueue.size() > 0) {
				currentEvent = eventQueue.get(0);
			} else {
				currentEvent = null;
			}
		}
	}

	public void removeCustomEvent(Condition condition) {
		customEvents.remove(condition);
	}

	public synchronized void reset() {
		currentTopEventPriority = Integer.MIN_VALUE;
		clearAllEvents(true);
		customEvents.clear();
	}

	public void setEventPriority(String eventClass, int priority) {
		if (priority < 0) {
			robotPeer.out.println("SYSTEM: Priority must be between 0 and 99.");
			robotPeer.out.println("SYSTEM: Priority for " + eventClass + " will be 0.");
			priority = 0;
		} else if (priority > 99) {
			robotPeer.out.println("SYSTEM: Priority must be between 0 and 99.");
			robotPeer.out.println("SYSTEM: Priority for " + eventClass + " will be 99.");
			priority = 99;
		}
		if (eventClass.equals("robocode.BulletHitEvent") || eventClass.equals("BulletHitEvent")) {
			bulletHitEventPriority = priority;
		} else if (eventClass.equals("robocode.BulletHitBulletEvent") || eventClass.equals("BulletHitBulletEvent")) {
			bulletHitBulletEventPriority = priority;
		} else if (eventClass.equals("robocode.BulletMissedEvent") || eventClass.equals("BulletMissedEvent")) {
			bulletMissedEventPriority = priority;
		} else if (eventClass.equals("robocode.HitByBulletEvent") || eventClass.equals("HitByBulletEvent")) {
			hitByBulletEventPriority = priority;
		} else if (eventClass.equals("robocode.HitRobotEvent") || eventClass.equals("HitRobotEvent")) {
			hitRobotEventPriority = priority;
		} else if (eventClass.equals("robocode.HitWallEvent") || eventClass.equals("HitWallEvent")) {
			hitWallEventPriority = priority;
		} else if (eventClass.equals("robocode.RobotDeathEvent") || eventClass.equals("RobotDeathEvent")) {
			robotDeathEventPriority = priority;
		} else if (eventClass.equals("robocode.ScannedRobotEvent") || eventClass.equals("ScannedRobotEvent")) {
			scannedRobotEventPriority = priority;
		} else if (eventClass.equals("robocode.MessageEvent") || eventClass.equals("MessageEvent")) {
			messageEventPriority = priority;
		} else if (eventClass.equals("robocode.CustomEvent") || eventClass.equals("CustomEvent")) {
			robotPeer.out.println(
					"SYSTEM: To change the priority of a CustomEvent, set it in the Condition.  setPriority ignored.");
		} else if (eventClass.equals("robocode.SkippedTurnEvent") || eventClass.equals("SkippedTurnEvent")) {
			robotPeer.out.println("SYSTEM: You may not change the priority of SkippedTurnEvent.  setPriority ignored.");
		} else if (eventClass.equals("robocode.WinEvent") || eventClass.equals("WinEvent")) {
			robotPeer.out.println("SYSTEM: You may not change the priority of WinEvent.  setPriority ignored.");
		} else if (eventClass.equals("robocode.DeathEvent") || eventClass.equals("DeathEvent")) {
			robotPeer.out.println("SYSTEM: You may not change the priority of DeathEvent.  setPriority ignored.");
		} else {
			robotPeer.out.println("SYSTEM: Unknown event class: " + eventClass);
		}
	}

	public void setFireAssistValid(boolean newFireAssistValid) {
		fireAssistValid = newFireAssistValid;
	}

	public void setInterruptible(int priority, boolean interruptable) {
		if (priority < 0 || priority > 99) {
			return;
		}
		this.interruptible[priority] = interruptable;
	}

	/**
	 * Returns a vector containing all MessageEvents currently in the robot's
	 * queue. You might, for example, call this while processing another event.
	 * <p>
	 * Example:
	 * <pre>
	 *   for (MessageEvent e : getMessageEvents()) {
	 *      // do something with e
	 *   }
	 * </pre>
	 *
	 * @return a vector containing all MessageEvents currently in the robot's
	 *    queue
	 *
	 * @see #onMessageReceived
	 * @see MessageEvent
	 *
	 * @since 1.2.6
	 */
	public List<MessageEvent> getMessageEvents() {
		List<MessageEvent> events = Collections.synchronizedList(new ArrayList<MessageEvent>());

		for (Event e : eventQueue) {
			if (e instanceof MessageEvent) {
				events.add((MessageEvent) e);
			}
		}
		return events;
	}
}
