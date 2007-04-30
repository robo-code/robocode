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
 *     - Optimized for Java 5
 *     - Updated Javadoc
 *     - Removed try-catch(ClassCastException) from compareTo()
 *******************************************************************************/
package robocode;


/**
 * The superclass of all Robocode events.
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public class Event implements Comparable<Event> {
	private int priority = 0;
	private long time = 0;

	/**
	 * Called by the game to create a new Event.
	 */
	public Event() {
		super();
	}

	/**
	 * Compares this event with another event, but only based on the priority
	 * and time of the two events.
	 * <p>
	 * This call is called by the game in order to sort events based on the
	 * priority and time.
	 * 
	 * @param event the event to compare to this event
	 * @return a negative integer, zero, or a positive integer as this event has
	 *    a lower priority, same priority, or greater priority than the
	 *    specified event
	 */
	public int compareTo(Event event) {
		int diff = event.priority - priority;

		if (diff != 0) {
			return diff;
		}
		int timediff = (int) (event.time - time);

		if (timediff != 0) {
			return timediff;
		} else if (event instanceof ScannedRobotEvent && this instanceof ScannedRobotEvent) {
			return (int) (((ScannedRobotEvent) this).getDistance() - ((ScannedRobotEvent) event).getDistance());
		} else if (event instanceof HitRobotEvent && this instanceof HitRobotEvent) {
			int compare1 = ((HitRobotEvent) this).isMyFault() ? -1 : 0;
			int compare2 = ((HitRobotEvent) event).isMyFault() ? -1 : 0;

			return compare1 - compare2;
		}

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
	 * @return the time this event occurred
	 */
	public long getTime() {
		return time;
	}

	/**
	 * Called by the game to set the priority of an event to the priority your
	 * robot specified for this type of event (or the default priority).
	 * <p>
	 * An event priority is a value from 0 - 99. The higher value, the higher
	 * priority. The default priority is 80.
	 *
	 * @param newPriority the new priority of this event
	 *
	 * @see AdvancedRobot#setEventPriority
	 */
	public void setPriority(int newPriority) {
		priority = newPriority;
	}

	/**
	 * Called by the game to set the time this event occurred.
	 * 
	 * @param newTime the time this event occured
	 */
	public void setTime(long newTime) {
		time = newTime;
	}
}
