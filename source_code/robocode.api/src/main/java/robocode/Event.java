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
 *     Flemming N. Larsen
 *     - Optimized for Java 5
 *     - Updated Javadocs
 *     - Removed try-catch(ClassCastException) from compareTo()
 *     - Changed compareTo() to first and foremost compare the events based on
 *       their event times, and secondly to compare the priorities if the event
 *       times are equals. Previously, the priorities were compared first, and
 *       secondly the event times if the priorities were equal.
 *       This change was made to sort the event queues of the robots in
 *       chronological so that the older events are listed before newer events
 *******************************************************************************/
package robocode;


import net.sf.robocode.peer.IRobotStatics;
import net.sf.robocode.security.IHiddenEventHelper;
import net.sf.robocode.io.Logger;
import robocode.robotinterfaces.IBasicRobot;

import java.awt.*;
import java.io.Serializable;
import java.util.Hashtable;


/**
 * The superclass of all Robocode events.
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public abstract class Event implements Comparable<Event>, Serializable {
	private static final long serialVersionUID = 1L;
	private static final int DEFAULT_PRIORITY = 80;

	// time is valid only after adding to event manager on proxy side, we do not update it on Battle side
	private transient boolean addedToQueue;
	private long time;
	private int priority;

	/**
	 * Called by the game to create a new Event.
	 */
	public Event() {
		super();
	}

	/**
	 * Compares this event to another event regarding precedence.
	 * The event precedence is first and foremost determined by the event time,
	 * secondly the event priority, and lastly specific event information.
	 * <p/>
	 * This method will first compare the time of each event. If the event time
	 * is the same for both events, then this method compared the priority of
	 * each event. If the event priorities are equals, then this method will
	 * compare the two event based on specific event information.
	 * <p/>
	 * This method is called by the game in order to sort the event queue of a
	 * robot to make sure the events are listed in chronological order.
	 * <p/>
	 *
	 * @param event the event to compare to this event.
	 * @return a negative value if this event has higher precedence, i.e. must
	 *         be listed before the specified event. A positive value if this event
	 *         has a lower precedence, i.e. must be listed after the specified event.
	 *         0 means that the precedence of the two events are equal.
	 */
	public int compareTo(Event event) {
		// Compare the time difference which has precedence over priority.
		int timeDiff = (int) (time - event.time);

		if (timeDiff != 0) {
			return timeDiff; // Time differ
		}

		// Same time -> Compare the difference in priority
		int priorityDiff = event.getPriority() - getPriority();

		if (priorityDiff != 0) {
			return priorityDiff; // Priority differ
		}

		// Same time and priority -> Compare specific event types
		// look at overrides in ScannedRobotEvent and HitRobotEvent

		// No difference found
		return 0;
	}

	/**
	 * Returns the priority of this event.
	 * An event priority is a value from 0 - 99. The higher value, the higher
	 * priority. The default priority is 80.
	 *
	 * @return the priority of this event
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * Returns the time this event occurred.
	 *
	 * @return the time this event occurred.
	 */
	public final long getTime() {
		return time;
	}

	/**
	 * Called by the game to set the priority of an event to the priority your
	 * robot specified for this type of event (or the default priority).
	 * <p/>
	 * An event priority is a value from 0 - 99. The higher value, the higher
	 * priority. The default priority is 80.
	 *
	 * Could be called by robot on events which are not managed by game.
	 * If the event is added into EventQueue, the time will be overridden.
	 * 
	 * @param newPriority the new priority of this event
	 * @see AdvancedRobot#setEventPriority(String, int)
	 */
	public final void setPriority(int newPriority) {
		if (addedToQueue) {
			Logger.printlnToRobotsConsole("SYSTEM: After the event was added to queue, priority can't be changed.");
			return;
		}
		if (newPriority < 0) {
			Logger.printlnToRobotsConsole("SYSTEM: Priority must be between 0 and 99");
			Logger.printlnToRobotsConsole("SYSTEM: Priority for " + this.getClass().getName() + " will be 0");
			newPriority = 0;
		} else if (newPriority > 99) {
			Logger.printlnToRobotsConsole("SYSTEM: Priority must be between 0 and 99");
			Logger.printlnToRobotsConsole("SYSTEM: Priority for " + this.getClass().getName() + " will be 99");
			newPriority = 99;
		}
		priority = newPriority;
	}

	/**
	 * Sets the time when this event occurred.
	 *
	 * @param time the time when this event occurred. 
	 */
	// this method is invisible on RobotAPI
	private void setTimeHidden(long time) {
		// we do not replace time which is set by robot to the future 
		if (this.time < time) {
			this.time = time;
		}
		addedToQueue = true;
	}

	/**
	 * Could be caled by robot to assign the time to events which are not managed by game.
	 * If the event is added into EventQueue, the time could be overriden
	 *
	 * @param newTime the time this event occurred
	 */
	public void setTime(long newTime) {
		if (!addedToQueue) {
			time = newTime;
		} else {
			Logger.printlnToRobotsConsole("SYSTEM: After the event was added to queue, time can't be changed.");
		}
	}

	/**
	 * Dispatch this event for a robot, it's statistics, and graphics context.
	 *
	 * @param robot the robot to dispatch to.
	 * @param statics the statistics to dispatch to.
	 * @param graphics the graphics to dispatch to.
	 */
	// this method is invisible on RobotAPI
	void dispatch(IBasicRobot robot, IRobotStatics statics, Graphics2D graphics) {}

	/**
	 * Returns the default priority of this event class.
	 * 
	 * @return the default priority of this event class.
	 */
	// this method is invisible on RobotAPI
	int getDefaultPriority() {
		return DEFAULT_PRIORITY;
	}

	/**
	 * Checks if this event must be delivered event after timeout.
	 *
	 * @return {@code true} when this event must be delivered even after timeout; {@code false} otherwise.
	 */
	// this method is invisible on RobotAPI
	boolean isCriticalEvent() {
		return false;
	}

	// this method is invisible on RobotAPI
	byte getSerializationType() {
		throw new Error("Serialization not supported on this event type");
	}

	/**
	 * This method is replacing bullet on event with bullet instance which was passed to robot as result of fire command
	 * @param bullets collection containing all moving bullets known to robot
	 */
	// this method is invisible on RobotAPI
	void updateBullets(Hashtable<Integer, Bullet> bullets) {}

	/**
	 * Creates a hidden event helper for accessing hidden methods on this object.
	 * 
	 * @return a hidden event helper.
	 */
	// this method is invisible on RobotAPI
	static IHiddenEventHelper createHiddenHelper() {
		return new HiddenEventHelper();
	}

	// this class is invisible on RobotAPI
	private static class HiddenEventHelper implements IHiddenEventHelper {

		public void setTime(Event event, long newTime) {
			event.setTimeHidden(newTime);
		}

		public void setDefaultPriority(Event event) {
			event.setPriority(event.getDefaultPriority());
		}

		public void setPriority(Event event, int newPriority) {
			event.setPriority(newPriority);
		}

		public boolean isCriticalEvent(Event event) {
			return event.isCriticalEvent();
		}

		public void dispatch(Event event, IBasicRobot robot, IRobotStatics statics, Graphics2D graphics) {
			event.dispatch(robot, statics, graphics);
		}

		public byte getSerializationType(Event event) {
			return event.getSerializationType();
		}

		public void updateBullets(Event event, Hashtable<Integer, Bullet> bullets) {
			event.updateBullets(bullets);
		}
	}
}
