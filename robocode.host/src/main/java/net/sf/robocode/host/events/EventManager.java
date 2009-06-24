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
 *     - Bugfix: Fixed ConcurrentModificationExceptions due to lack of
 *       synchronization with the event queue. Now all getXXXEvents() methods
 *       are synchronized against the event queue, and the list of customEvents
 *       is a CopyOnWriteArrayList which is fully thread-safe
 *     - Changed the priority of the DeathEvent from 100 to -1 in order to let
 *       robots process events before they die
 *     - Added handling of the new StatusEvent, which is used for calling the new
 *       Robot.onStatus(StatusEvent e) event handler, and added the
 *       getStatusEvents() method
 *     - Added PaintEvent with the onPaint() handler and also getPaintEvents()
 *     Robert D. Maupin
 *     - Replaced old collection types like Vector and Hashtable with
 *       synchronized List and HashMap
 *     Nathaniel Troutman
 *     - Added cleanup() method for cleaning up references to internal classes
 *       to prevent circular references causing memory leaks
 *     Pavel Savara
 *     - Re-work of robot interfaces
 *     - dispatch and priorities redesign
 *******************************************************************************/
package net.sf.robocode.host.events;


import net.sf.robocode.host.proxies.BasicRobotProxy;
import net.sf.robocode.security.HiddenAccess;
import robocode.*;
import robocode.exception.EventInterruptedException;
import robocode.robotinterfaces.IBasicRobot;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Matthew Reeder (contributor)
 * @author Robert D. Maupin (contributor)
 * @author Nathaniel Troutman (contributor)
 * @author Pavel Savara (contributor)
 */
public class EventManager implements IEventManager {
	private BasicRobotProxy robotProxy;
	private final static int MAX_PRIORITY = 100;
	public final static int MAX_EVENT_STACK = 2;

	private int currentTopEventPriority;
	private Event currentTopEvent;

	private final List<Condition> customEvents = new CopyOnWriteArrayList<Condition>();
	private final EventQueue eventQueue;

	private final boolean[] interruptible = new boolean[MAX_PRIORITY + 1];
	private Dictionary<String, Event> namedEvents;
	private ScannedRobotEvent dummyScannedRobotEvent;

	public final static int MAX_QUEUE_SIZE = 256;

	private IBasicRobot robot;

	/**
	 * EventManager constructor comment.
	 *
	 * @param robotProxy robotProxy
	 */
	public EventManager(BasicRobotProxy robotProxy) {
		super();
		registerNamedEvents();
		this.robotProxy = robotProxy;
		eventQueue = new EventQueue();

		reset();
	}

	public void add(Event e) {
		if (!HiddenAccess.isCriticalEvent(e)) {
			HiddenAccess.setEventPriority(e, getEventPriority(e.getClass().getName()));
		}
		HiddenAccess.setEventTime(e, getTime());
		addImpl(e);
	}

	private void addImpl(Event e) {
		if (eventQueue != null) {
			if (eventQueue.size() > MAX_QUEUE_SIZE) {
				robotProxy.println(
						"Not adding to " + robotProxy.getStatics().getName() + "'s queue, exceeded " + MAX_QUEUE_SIZE
						+ " events in queue.");
				return;
			}
			eventQueue.add(e);
		}
	}

	public void addCustomEvent(Condition condition) {
		customEvents.add(condition);
	}

	public void clearAllEvents(boolean includingSystemEvents) {
		eventQueue.clear(includingSystemEvents);
	}

	public void cleanup() {
		// Remove all events
		reset();

		// Remove all references to robots
		robot = null;
		robotProxy = null;
	}

	/**
	 * Returns a list containing all events currently in the robot's queue.
	 * You might, for example, call this while processing another event.
	 * <p/>
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
	 * @see BulletHitEvent
	 * @see BulletMissedEvent
	 * @see HitByBulletEvent
	 * @see HitRobotEvent
	 * @see HitWallEvent
	 * @see SkippedTurnEvent
	 * @see Event
	 * @see List
	 */
	public List<Event> getAllEvents() {
		List<Event> events = Collections.synchronizedList(new ArrayList<Event>());

		synchronized (eventQueue) {
			for (Event e : eventQueue) {
				events.add(e);
			}
		}
		return events;
	}

	/**
	 * Returns a list containing all BulletHitBulletEvents currently in the robot's queue.
	 * You might, for example, call this while processing another event.
	 * <p/>
	 * <P>Example:
	 * <pre>
	 *    for (BulletHitBulletEvent e : getBulletHitBulletEvents()) {
	 *      <i> (do something with e) </i>
	 *    }
	 * </pre>
	 *
	 * @see BulletHitBulletEvent
	 * @see List
	 */
	public List<BulletHitBulletEvent> getBulletHitBulletEvents() {
		List<BulletHitBulletEvent> events = Collections.synchronizedList(new ArrayList<BulletHitBulletEvent>());

		synchronized (eventQueue) {
			for (Event e : eventQueue) {
				if (e instanceof BulletHitBulletEvent) {
					events.add((BulletHitBulletEvent) e);
				}
			}
		}
		return events;
	}

	/**
	 * Returns a list containing all BulletHitEvents currently in the robot's queue.
	 * You might, for example, call this while processing another event.
	 * <p/>
	 * <P>Example:
	 * <pre>
	 *    for (BulletHitEvent e : getBulletHitEvents()) {
	 *      <i> (do something with e) </i>
	 *    }
	 * </pre>
	 *
	 * @see BulletHitEvent
	 * @see List
	 */
	public List<BulletHitEvent> getBulletHitEvents() {
		List<BulletHitEvent> events = Collections.synchronizedList(new ArrayList<BulletHitEvent>());

		synchronized (eventQueue) {
			for (Event e : eventQueue) {
				if (e instanceof BulletHitEvent) {
					events.add((BulletHitEvent) e);
				}
			}
		}
		return events;
	}

	/**
	 * Returns a list containing all BulletMissedEvents currently in the robot's queue.
	 * You might, for example, call this while processing another event.
	 * <p/>
	 * <P>Example:
	 * <pre>
	 *    for (BulletMissedEvent e : getBulletMissedEvents()) {
	 *      <i> (do something with e) </i>
	 *    }
	 * </pre>
	 *
	 * @see BulletMissedEvent
	 * @see List
	 */
	public List<BulletMissedEvent> getBulletMissedEvents() {
		List<BulletMissedEvent> events = Collections.synchronizedList(new ArrayList<BulletMissedEvent>());

		synchronized (eventQueue) {
			for (Event e : eventQueue) {
				if (e instanceof BulletMissedEvent) {
					events.add((BulletMissedEvent) e);
				}
			}
		}
		return events;
	}

	public int getCurrentTopEventPriority() {
		return currentTopEventPriority;
	}

	public Event getCurrentTopEvent() {
		return currentTopEvent;
	}

	/**
	 * Returns a list containing all HitByBulletEvents currently in the robot's queue.
	 * You might, for example, call this while processing another event.
	 * <p/>
	 * <P>Example:
	 * <pre>
	 *    for (HitByBulletEvent e : getHitByBulletEvents()) {
	 *      <i> (do something with e) </i>
	 *    }
	 * </pre>
	 *
	 * @see HitByBulletEvent
	 * @see List
	 */
	public List<HitByBulletEvent> getHitByBulletEvents() {
		List<HitByBulletEvent> events = Collections.synchronizedList(new ArrayList<HitByBulletEvent>());

		synchronized (eventQueue) {
			for (Event e : eventQueue) {
				if (e instanceof HitByBulletEvent) {
					events.add((HitByBulletEvent) e);
				}
			}
		}
		return events;
	}

	/**
	 * Returns a list containing all HitRobotEvents currently in the robot's queue.
	 * You might, for example, call this while processing another event.
	 * <p/>
	 * <P>Example:
	 * <pre>
	 *    for (HitRobotEvent e : getHitRobotEvents()) {
	 *      <i> (do something with e) </i>
	 *    }
	 * </pre>
	 *
	 * @see HitRobotEvent
	 * @see List
	 */
	public List<HitRobotEvent> getHitRobotEvents() {
		List<HitRobotEvent> events = Collections.synchronizedList(new ArrayList<HitRobotEvent>());

		synchronized (eventQueue) {
			for (Event e : eventQueue) {
				if (e instanceof HitRobotEvent) {
					events.add((HitRobotEvent) e);
				}
			}
		}
		return events;
	}

	/**
	 * Returns a list containing all HitWallEvents currently in the robot's queue.
	 * You might, for example, call this while processing another event.
	 * <p/>
	 * <P>Example:
	 * <pre>
	 *    for (HitWallEvent e : getHitWallEvents()) {
	 *      <i> (do something with e) </i>
	 *    }
	 * </pre>
	 *
	 * @see HitWallEvent
	 * @see List
	 */
	public List<HitWallEvent> getHitWallEvents() {
		List<HitWallEvent> events = Collections.synchronizedList(new ArrayList<HitWallEvent>());

		synchronized (eventQueue) {
			for (Event e : eventQueue) {
				if (e instanceof HitWallEvent) {
					events.add((HitWallEvent) e);
				}
			}
		}
		return events;
	}

	public boolean getInterruptible(int priority) {
		return this.interruptible[priority];
	}

	private IBasicRobot getRobot() {
		return robot;
	}

	public void setRobot(IBasicRobot r) {
		this.robot = r;
	}

	/**
	 * Returns a list containing all RobotDeathEvents currently in the robot's queue.
	 * You might, for example, call this while processing another event.
	 * <p/>
	 * <P>Example:
	 * <pre>
	 *    for (RobotDeathEvent e : getRobotDeathEvents()) {
	 *      <i> (do something with e) </i>
	 *    }
	 * </pre>
	 *
	 * @see RobotDeathEvent
	 * @see List
	 */
	public List<RobotDeathEvent> getRobotDeathEvents() {
		List<RobotDeathEvent> events = Collections.synchronizedList(new ArrayList<RobotDeathEvent>());

		synchronized (eventQueue) {
			for (Event e : eventQueue) {
				if (e instanceof RobotDeathEvent) {
					events.add((RobotDeathEvent) e);
				}
			}
		}
		return events;
	}

	public int getScannedRobotEventPriority() {
		return dummyScannedRobotEvent.getPriority();
	}

	/**
	 * Returns a list containing all ScannedRobotEvents currently in the robot's queue.
	 * You might, for example, call this while processing another event.
	 * <p/>
	 * <P>Example:
	 * <pre>
	 *    for (ScannedRobotEvent e : getScannedRobotEvents()) {
	 *      <i> (do something with e) </i>
	 *    }
	 * </pre>
	 *
	 * @see ScannedRobotEvent
	 * @see List
	 */
	public List<ScannedRobotEvent> getScannedRobotEvents() {
		List<ScannedRobotEvent> events = Collections.synchronizedList(new ArrayList<ScannedRobotEvent>());

		synchronized (eventQueue) {
			for (Event e : eventQueue) {
				if (e instanceof ScannedRobotEvent) {
					events.add((ScannedRobotEvent) e);
				}
			}
		}
		return events;
	}

	public long getTime() {
		return robotProxy.getTimeImpl();
	}

	public void processEvents() {
		// remove too old stuff
		eventQueue.clear(getTime() - MAX_EVENT_STACK);

		// Process custom events
		for (Condition customEvent : customEvents) {
			boolean conditionSatisfied = false;

			robotProxy.setTestingCondition(true);
			try {
				conditionSatisfied = customEvent.test();
			} finally {
				robotProxy.setTestingCondition(false);
			}
			if (conditionSatisfied) {
				CustomEvent event = new CustomEvent(customEvent);

				HiddenAccess.setEventTime(event, getTime());
				addImpl(event);
			}
		}

		// Process event queue here
		eventQueue.sort();

		Event currentEvent = null;

		if (eventQueue.size() > 0) {
			currentEvent = eventQueue.get(0);
		}
		while (currentEvent != null && currentEvent.getPriority() >= currentTopEventPriority) {
			if (currentEvent.getPriority() == currentTopEventPriority) {
				if (currentTopEventPriority > Integer.MIN_VALUE && getInterruptible(currentTopEventPriority)) {
					setInterruptible(currentTopEventPriority, false); // we're going to restart it, so reset.

					// We are already in an event handler, took action, and a new event was generated.
					// So we want to break out of the old handler to process it here.
					throw new EventInterruptedException(currentEvent.getPriority());
				}
				break;
			}

			int oldTopEventPriority = currentTopEventPriority;

			currentTopEventPriority = currentEvent.getPriority();
			currentTopEvent = currentEvent;

			eventQueue.remove(currentEvent);
			try {
				dispatch(currentEvent);

				setInterruptible(currentTopEventPriority, false);

			} catch (EventInterruptedException e) {
				currentTopEvent = null;
			} catch (RuntimeException e) {
				currentTopEventPriority = oldTopEventPriority;
				currentTopEvent = null;
				throw e;
			} catch (Error e) {
				currentTopEventPriority = oldTopEventPriority;
				currentTopEvent = null;
				throw e;
			}
			currentTopEventPriority = oldTopEventPriority;
			currentEvent = (eventQueue.size() > 0) ? eventQueue.get(0) : null;
		}
	}

	private void dispatch(Event currentEvent) {
		final IBasicRobot robot = getRobot();

		if (robot != null && currentEvent != null) {
			try {
				// skip too old events
				if ((currentEvent.getTime() > getTime() - MAX_EVENT_STACK) || HiddenAccess.isCriticalEvent(currentEvent)) {
					HiddenAccess.dispatch(currentEvent, robot, robotProxy.getStatics(), robotProxy.getGraphicsImpl());
				}
			} catch (Exception ex) {
				robotProxy.println("SYSTEM: Exception occurred on " + currentEvent.getClass().getName());
				ex.printStackTrace(robotProxy.getOut());
			}
		}
	}

	public void removeCustomEvent(Condition condition) {
		customEvents.remove(condition);
	}

	public void resetCustomEvents() {
		customEvents.clear();
	}

	public synchronized void reset() {
		currentTopEventPriority = Integer.MIN_VALUE;
		clearAllEvents(true);
		customEvents.clear();
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
	 * <p/>
	 * Example:
	 * <pre>
	 *   for (MessageEvent e : getMessageEvents()) {
	 *      <i> (do something with e) </i>
	 *   }
	 * </pre>
	 *
	 * @return a vector containing all MessageEvents currently in the robot's
	 *         queue
	 * @see MessageEvent
	 * @since 1.2.6
	 */
	public List<MessageEvent> getMessageEvents() {
		List<MessageEvent> events = Collections.synchronizedList(new ArrayList<MessageEvent>());

		synchronized (eventQueue) {
			for (Event e : eventQueue) {
				if (e instanceof MessageEvent) {
					events.add((MessageEvent) e);
				}
			}
		}
		return events;
	}

	/**
	 * Returns a vector containing all StatusEvents currently in the robot's
	 * queue. You might, for example, call this while processing another event.
	 * <p/>
	 * Example:
	 * <pre>
	 *   for (StatusEvent e : getStatusEvents()) {
	 *      <i> (do something with e) </i>
	 *   }
	 * </pre>
	 *
	 * @return a vector containing all StatusEvents currently in the robot's
	 *         queue.
	 * @see StatusEvent
	 * @since 1.5
	 */
	public List<StatusEvent> getStatusEvents() {
		List<StatusEvent> events = Collections.synchronizedList(new ArrayList<StatusEvent>());

		synchronized (eventQueue) {
			for (Event e : eventQueue) {
				if (e instanceof StatusEvent) {
					events.add((StatusEvent) e);
				}
			}
		}
		return events;
	}

	public int getEventPriority(String eventClass) {
		if (eventClass == null) {
			return -1;
		}
		final Event event = namedEvents.get(eventClass);

		if (event == null) {
			return -1;
		}
		return event.getPriority();
	}

	public void setEventPriority(String eventClass, int priority) {
		if (eventClass == null) {
			return;
		}
		final Event event = namedEvents.get(eventClass);

		if (event == null) {
			robotProxy.println("SYSTEM: Unknown event class: " + eventClass);
			return;
		}
		if (HiddenAccess.isCriticalEvent(event)) {
			robotProxy.println("SYSTEM: You may not change the priority of system event. setPriority ignored.");
		}

		HiddenAccess.setEventPriority(event, priority);
	}

	private void registerNamedEvents() {
		namedEvents = new Hashtable<String, Event>();
		dummyScannedRobotEvent = new ScannedRobotEvent(null, 0, 0, 0, 0, 0);
		registerNamedEvent(new BattleEndedEvent(false, null));
		registerNamedEvent(new BulletHitBulletEvent(null, null));
		registerNamedEvent(new BulletHitEvent(null, 0, null));
		registerNamedEvent(new BulletMissedEvent(null));
		registerNamedEvent(new DeathEvent());
		registerNamedEvent(new HitByBulletEvent(0, null));
		registerNamedEvent(new HitRobotEvent(null, 0, 0, false));
		registerNamedEvent(new HitWallEvent(0));
		registerNamedEvent(new KeyPressedEvent(null));
		registerNamedEvent(new KeyReleasedEvent(null));
		registerNamedEvent(new KeyTypedEvent(null));
		registerNamedEvent(new MessageEvent(null, null));
		registerNamedEvent(new MouseClickedEvent(null));
		registerNamedEvent(new MouseDraggedEvent(null));
		registerNamedEvent(new MouseEnteredEvent(null));
		registerNamedEvent(new MouseExitedEvent(null));
		registerNamedEvent(new MouseMovedEvent(null));
		registerNamedEvent(new MousePressedEvent(null));
		registerNamedEvent(new MouseReleasedEvent(null));
		registerNamedEvent(new MouseWheelMovedEvent(null));
		registerNamedEvent(new PaintEvent());
		registerNamedEvent(new RobotDeathEvent(null));
		registerNamedEvent(dummyScannedRobotEvent);
		registerNamedEvent(new SkippedTurnEvent());
		registerNamedEvent(new StatusEvent(null));
		registerNamedEvent(new WinEvent());
		registerNamedEvent(new ScannedObjectEvent(null, 0, 0, false, false, false, false));
		registerNamedEvent(new HitObjectEvent(null));
		registerNamedEvent(new HitObstacleEvent(0, null));

		// same as any line above but for custom event
		final DummyCustomEvent custom = new DummyCustomEvent();

		namedEvents.put("robocode.CustomEvent", custom);
		namedEvents.put("CustomEvent", custom);
	}

	private void registerNamedEvent(Event e) {
		final String name = e.getClass().getName();

		if (!HiddenAccess.isCriticalEvent(e)) {
			HiddenAccess.setDefaultPriority(e);
		}
		namedEvents.put(name, e);
		namedEvents.put(name.substring(9), e);
	}

	private static final class DummyCustomEvent extends CustomEvent {
		private static final long serialVersionUID = 1L;

		public DummyCustomEvent() {
			super(null);
		}
	}
}
