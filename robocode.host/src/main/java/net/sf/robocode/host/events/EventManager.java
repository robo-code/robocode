/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.host.events;


import net.sf.robocode.host.proxies.BasicRobotProxy;
import net.sf.robocode.security.HiddenAccess;
import robocode.*;
import robocode.exception.EventInterruptedException;
import robocode.robotinterfaces.IBasicRobot;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;


// XXX Remember to update the .NET version whenever a change is made to this class!

/**
 * This class is used for managing the event queue for a robot.
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Matthew Reeder (contributor)
 * @author Robert D. Maupin (contributor)
 * @author Nathaniel Troutman (contributor)
 * @author Pavel Savara (contributor)
 */
public final class EventManager implements IEventManager {

	private final static int MAX_PRIORITY = 100;
	public final static int MAX_EVENT_STACK = 2;
	public final static int MAX_QUEUE_SIZE = 256;

	private final List<Condition> customEvents = new CopyOnWriteArrayList<Condition>();
	private final EventQueue eventQueue;

	private final boolean[] interruptible = new boolean[MAX_PRIORITY + 1];
	private Event currentTopEvent;
	private int currentTopEventPriority;
	private ScannedRobotEvent dummyScannedRobotEvent;
	private Map<String, Event> eventNames;

	private IBasicRobot robot;
	private BasicRobotProxy robotProxy;

	/**
	 * Constructs a new EventManager.
	 *
	 * @param robotProxy the robot proxy that this event manager applies to.
	 */
	public EventManager(BasicRobotProxy robotProxy) {
		this.robotProxy = robotProxy;
		eventQueue = new EventQueue();

		registerEventNames();
		reset();
	}

	/**
	 * Adds an event to the event queue.
	 * @param event is the event to add to the event queue.
	 */
	public void add(Event event) {
		if (!HiddenAccess.isCriticalEvent(event)) {
			final int priority = getEventPriority(event.getClass().getName());
			HiddenAccess.setEventPriority(event, priority);
		}
		addImpl(event);
	}

	/**
	 * Internal method for adding an event to the event queue.
	 * @param event is the event to add to the event queue.
	 */
	private void addImpl(Event event) {
		if (eventQueue != null) {
			if (eventQueue.size() > MAX_QUEUE_SIZE) {
				robotProxy.println(
						"Not adding to " + robotProxy.getStatics().getName() + "'s queue, exceeded " + MAX_QUEUE_SIZE
						+ " events in queue.");
			} else {
				HiddenAccess.setEventTime(event, getTime());
				eventQueue.add(event);
			}
		}
	}

	/**
	 * Adds an custom event to the event queue based on a condition.
	 * @param condition is the condition that must be met in order to trigger the custom event.
	 */
	public void addCustomEvent(Condition condition) {
		customEvents.add(condition);
	}

	/**
	 * Removes all events from the event queue.
	 * @param includingSystemEvents {@code true} if system events must be removed as well;
	 *                              {@code false} if system events should stay on the event queue.
	 */
	public void clearAllEvents(boolean includingSystemEvents) {
		eventQueue.clear(includingSystemEvents);
		// customEvents.clear(); // Custom event should not be cleared here
	}

	/**
	 * Cleans up the event queue.
	 * <p>
	 * This method should be called when the event queue is no longer needed,
	 * i.e. before it must be garbage collected.
	 */
	public void cleanup() {
		// Remove all events
		reset();

		// Remove all references to robots
		robot = null;
		robotProxy = null;
	}

	/**
	 * Returns a list containing all events currently in the robot's queue.
	 */
	public List<Event> getAllEvents() {
		List<Event> events = new ArrayList<Event>();
		synchronized (eventQueue) {
			for (Event e : eventQueue) {
				events.add(e);
			}
		}
		return events;
	}

	/**
	 * Returns a list containing all BulletHitBulletEvents currently in the robot's queue.
	 */
	public List<BulletHitBulletEvent> getBulletHitBulletEvents() {
		List<BulletHitBulletEvent> events = new ArrayList<BulletHitBulletEvent>();
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
	 */
	public List<BulletHitEvent> getBulletHitEvents() {
		List<BulletHitEvent> events = new ArrayList<BulletHitEvent>();
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
	 */
	public List<BulletMissedEvent> getBulletMissedEvents() {
		List<BulletMissedEvent> events = new ArrayList<BulletMissedEvent>();
		synchronized (eventQueue) {
			for (Event e : eventQueue) {
				if (e instanceof BulletMissedEvent) {
					events.add((BulletMissedEvent) e);
				}
			}
		}
		return events;
	}

	/**
	 * Returns a list containing all HitByBulletEvents currently in the robot's queue.
	 */
	public List<HitByBulletEvent> getHitByBulletEvents() {
		List<HitByBulletEvent> events = new ArrayList<HitByBulletEvent>();
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
	 */
	public List<HitRobotEvent> getHitRobotEvents() {
		List<HitRobotEvent> events = new ArrayList<HitRobotEvent>();

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
	 */
	public List<HitWallEvent> getHitWallEvents() {
		List<HitWallEvent> events = new ArrayList<HitWallEvent>();
		synchronized (eventQueue) {
			for (Event e : eventQueue) {
				if (e instanceof HitWallEvent) {
					events.add((HitWallEvent) e);
				}
			}
		}
		return events;
	}

	/**
	 * Returns a list containing all RobotDeathEvents currently in the robot's queue.
	 */
	public List<RobotDeathEvent> getRobotDeathEvents() {
		List<RobotDeathEvent> events = new ArrayList<RobotDeathEvent>();
		synchronized (eventQueue) {
			for (Event e : eventQueue) {
				if (e instanceof RobotDeathEvent) {
					events.add((RobotDeathEvent) e);
				}
			}
		}
		return events;
	}

	/**
	 * Returns a list containing all ScannedRobotEvents currently in the robot's queue.
	 */
	public List<ScannedRobotEvent> getScannedRobotEvents() {
		List<ScannedRobotEvent> events = new ArrayList<ScannedRobotEvent>();
		synchronized (eventQueue) {
			for (Event e : eventQueue) {
				if (e instanceof ScannedRobotEvent) {
					events.add((ScannedRobotEvent) e);
				}
			}
		}
		return events;
	}

	/**
	 * Returns a list containing all MessageEvents currently in the robot's queue.
	 */
	public List<MessageEvent> getMessageEvents() {
		List<MessageEvent> events = new ArrayList<MessageEvent>();
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
	 * Returns a list containing all StatusEvents currently in the robot's queue.
	 */
	public List<StatusEvent> getStatusEvents() {
		List<StatusEvent> events = new ArrayList<StatusEvent>();
		synchronized (eventQueue) {
			for (Event e : eventQueue) {
				if (e instanceof StatusEvent) {
					events.add((StatusEvent) e);
				}
			}
		}
		return events;
	}

	/**
	 * Returns the priority of the current top event.
	 */
	public int getCurrentTopEventPriority() {
		return currentTopEventPriority;
	}

	/**
	 * Returns the current top event.
	 */
	public Event getCurrentTopEvent() {
		return currentTopEvent;
	}

	/**
	 * Checks if events with a specific event priority are interruptible.
	 * @param priority is the event priority that must be checked.
	 * @see #setInterruptible(int, boolean)
	 */
	public boolean isInterruptible(int priority) {
		return interruptible[priority];
	}

	/**
	 * Sets the robot that will receive events dispatched from the event queue.
	 * @param robot is the robot that will receive event dispatched from the event queue.
	 */
	public void setRobot(IBasicRobot robot) {
		this.robot = robot;
	}

	/**
	 * Returns the priority of a ScannedRobotEvent.
	 */
	public int getScannedRobotEventPriority() {
		return dummyScannedRobotEvent.getPriority();
	}

	/**
	 * Returns the current time/turn of the battle round. 
	 */
	public long getTime() {
		return robotProxy.getTimeImpl();
	}

	/**
	 * This is the heart of the event manager, which processes the events for a robot.
	 */
	public void processEvents() {
		// Remove old events
		eventQueue.clear(getTime() - MAX_EVENT_STACK);

		// Process custom events
		for (Condition customEvent : customEvents) {
			boolean conditionSatisfied = callUserCode(customEvent);
			if (conditionSatisfied) {
				addImpl(new CustomEvent(customEvent));
			}
		}

		// Sort the events based on the time and priority of the events
		eventQueue.sort();

		// Process event queue here
		Event currentEvent;
		while ((currentEvent = (eventQueue.size() > 0) ? eventQueue.get(0) : null) != null
				&& currentEvent.getPriority() >= currentTopEventPriority) {

			if (currentEvent.getPriority() == currentTopEventPriority) {
				if (currentTopEventPriority > Integer.MIN_VALUE && isInterruptible(currentTopEventPriority)) {
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
				currentTopEvent = null;
				throw e;
			} catch (Error e) {
				currentTopEvent = null;
				throw e;
			} finally {
				currentTopEventPriority = oldTopEventPriority;				
			}
		}
	}

	/**
	 * Checks if the user's condition for a custom event is satisfied.
	 * @param condition is the condition to check.
	 * @return {@code true} if the condition is satisfied; {@code false} otherwise.
	 */
	private boolean callUserCode(Condition condition) {
		boolean conditionSatisfied;
		robotProxy.setTestingCondition(true);
		try {
			conditionSatisfied = condition.test();
		} finally {
			robotProxy.setTestingCondition(false);
		}
		return conditionSatisfied;
	}

	/**
	 * Dispatches an event for a robot.
	 * <p>
	 * Too old events will not be dispatched and a critical event is always dispatched.
	 *
	 * @param event the event to dispatch to the robot.
	 */
	private void dispatch(Event event) {
		if (robot != null && event != null) {
			try {
				// skip too old events
				if ((event.getTime() > getTime() - MAX_EVENT_STACK) || HiddenAccess.isCriticalEvent(event)) {
					HiddenAccess.dispatch(event, robot, robotProxy.getStatics(), robotProxy.getGraphicsImpl());
				}
			} catch (Exception ex) {
				robotProxy.println("SYSTEM: " + ex.getClass().getName() + " occurred on " + event.getClass().getName());
				ex.printStackTrace(robotProxy.getOut());
			}
		}
	}

	/**
	 * Removes the custom event with the specified condition from the event queue.
	 * @param condition is the condition of the custom event to remove.
	 */
	public void removeCustomEvent(Condition condition) {
		customEvents.remove(condition);
	}

	/**
	 * Removes all custom events from the event queue.
	 */
	public void resetCustomEvents() {
		customEvents.clear();
	}

	/**
	 * Resets this event manager by removing all events from the event queue.
	 */
	public synchronized void reset() {
		currentTopEventPriority = Integer.MIN_VALUE;
		clearAllEvents(true);
		customEvents.clear();
	}

	/**
	 * Changes the interruptible flag for events with a specific priority.
	 * When an event is interrupted, events with the same priority are allowed to restart the event handler. 
	 *
	 * @param priority is the priority of the event to set the interruptible flag for.
	 * @param isInterruptable {@code true} if events with the specified priority must be interruptible
	 *                        allowing events with the same priority to restart the event handler.
	 *                        {@code false} if events with the specified priority must not be interruptible
	 *                        disallowing events with the same priority to restart the event handler. 
	 */
	public void setInterruptible(int priority, boolean isInterruptable) {
		if (priority >= 0 && priority < MAX_PRIORITY) {
			interruptible[priority] = isInterruptable;
		}
	}

	/**
	 * Returns the priority of events belonging to a specific class.
	 * @param eventClass is a string with the full class name of the event type to get the priority from.
	 * @return the event priority of the specified event class.
	 * @see robocode.Event#getPriority()
	 */
	public int getEventPriority(String eventClass) {
		if (eventClass == null) {
			return -1;
		}
		final Event event = eventNames.get(eventClass);
		if (event == null) {
			return -1;
		}
		return event.getPriority();
	}

	/**
	 * Sets the event priority of events belonging to a specific class.
	 * @param eventClass is a string with the full class name of the event type to set the priority for.
	 * @param priority is the new priority
	 */
	public void setEventPriority(String eventClass, int priority) {
		if (eventClass == null) {
			return;
		}
		final Event event = eventNames.get(eventClass);
		if (event == null) {
			robotProxy.println("SYSTEM: Unknown event class: " + eventClass);
			return;
		}
		if (HiddenAccess.isCriticalEvent(event)) {
			robotProxy.println("SYSTEM: You may not change the priority of a system event.");
		}
		HiddenAccess.setEventPriority(event, priority);
	}

	/**
	 * Registers the full and simple class names of all events used by {@link #getEventPriority(String)} and
	 * {@link #setEventPriority(String, int)} and sets the default priority of each event class.
	 */
	private void registerEventNames() {
		eventNames = new HashMap<String, Event>();
		dummyScannedRobotEvent = new ScannedRobotEvent(null, 0, 0, 0, 0, 0, false);
		registerEventNames(new BattleEndedEvent(false, null));
		registerEventNames(new BulletHitBulletEvent(null, null));
		registerEventNames(new BulletHitEvent(null, 0, null));
		registerEventNames(new BulletMissedEvent(null));
		registerEventNames(new DeathEvent());
		registerEventNames(new HitByBulletEvent(0, null));
		registerEventNames(new HitRobotEvent(null, 0, 0, false));
		registerEventNames(new HitWallEvent(0));
		registerEventNames(new KeyPressedEvent(null));
		registerEventNames(new KeyReleasedEvent(null));
		registerEventNames(new KeyTypedEvent(null));
		registerEventNames(new MessageEvent(null, null));
		registerEventNames(new MouseClickedEvent(null));
		registerEventNames(new MouseDraggedEvent(null));
		registerEventNames(new MouseEnteredEvent(null));
		registerEventNames(new MouseExitedEvent(null));
		registerEventNames(new MouseMovedEvent(null));
		registerEventNames(new MousePressedEvent(null));
		registerEventNames(new MouseReleasedEvent(null));
		registerEventNames(new MouseWheelMovedEvent(null));
		registerEventNames(new PaintEvent());
		registerEventNames(new RobotDeathEvent(null));
		registerEventNames(new RoundEndedEvent(0, 0, 0));
		registerEventNames(dummyScannedRobotEvent);
		registerEventNames(new SkippedTurnEvent(0));
		registerEventNames(new StatusEvent(null));
		registerEventNames(new WinEvent());

		// same as any line above but for custom event
		final DummyCustomEvent customEvent = new DummyCustomEvent();
		eventNames.put("robocode.CustomEvent", customEvent); // full name with package name
		eventNames.put("CustomEvent", customEvent); // only the class name
	}

	/**
	 * Registers the full and simple class name of the specified event and sets the default
	 * priority of the event class.
	 * @param event an event belonging to the event class to register the class name for etc.
	 */
	private void registerEventNames(Event event) {
		if (!HiddenAccess.isCriticalEvent(event)) {
			HiddenAccess.setDefaultPriority(event);
		}
		final Class<?> type = event.getClass();
		eventNames.put(type.getName(), event); // full name with package name
		eventNames.put(type.getSimpleName(), event); // only the class name
	}

	/**
	 * A dummy CustomEvent used only for registering the class name.
	 */
	@SuppressWarnings("serial")
	private static final class DummyCustomEvent extends CustomEvent {
		public DummyCustomEvent() {
			super(null);
		}
	}
}
