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


import robocode.peer.RobotStatics;
import robocode.robotinterfaces.IBasicRobot;

import java.awt.*;


/**
 * The superclass of all Robocode events.
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public abstract class Event implements Comparable<Event> {
	private long time = 0;

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
		return getClassPriorityImpl();
	}

	/**
	 * Returns the time this event occurred.
	 *
	 * @return the time this event occurred
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
	 * @param newPriority the new priority of this event
	 * @see AdvancedRobot#setEventPriority(String, int)
	 */
	public void setPriority(int newPriority) {
		if (newPriority < 0) {
			System.out.println("SYSTEM: Priority must be between 0 and 99.");
			System.out.println("SYSTEM: Priority for " + this.getClass().getName() + " will be 0.");
			newPriority = 0;
		} else if (newPriority > 99) {
			System.out.println("SYSTEM: Priority must be between 0 and 99.");
			System.out.println("SYSTEM: Priority for " + this.getClass().getName() + " will be 99.");
			newPriority = 99;
		}
		setClassPriorityImpl(newPriority);
	}

	/**
	 * Called by the game to set the time this event occurred.
	 *
	 * @param newTime the time this event occurred
	 */
	public void setTime(long newTime) {
		time = newTime;
	}

	/**
	 * Do not call this method! Your robot will simply stop interacting with
	 * the game.
	 *
	 * This method is called by the game. A robot peer is the object that deals
	 * with game mechanics and rules, and makes sure your robot abides by them.
	 * @param robot robot
	 * @param statics statics
	 * @param graphics graphics
	 */
	public abstract void dispatch(IBasicRobot robot, RobotStatics statics, Graphics2D graphics);

	protected abstract int getClassPriorityImpl();

	protected abstract void setClassPriorityImpl(int priority);

	/**
	 * @return True when the event is delivered even after timeout. 
	 */
	public boolean isCriricalEvent() {
		return false;
	}

}
