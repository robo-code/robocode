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
 *******************************************************************************/
package robocode;


/**
 * The basic superclass of all Robocode events
 *
 * @author Mathew A. Nelson
 */
public class Event implements Comparable {
	private int priority = 0;
	private long time = 0;

	/**
	 * Called by the game to create a new Event.
	 */
	public Event() {
		super();
	}

	/**
	 * Used for sorting events based on priority and time.
	 */
	public int compareTo(Object o) {
		int diff = 0;

		try {
			diff = ((Event) o).priority - priority;
			if (diff != 0) {
				return diff;
			} else {
				int timediff = (int) (((Event) o).time - time);

				if (timediff != 0) {
					return timediff;
				} else if (o instanceof ScannedRobotEvent && this instanceof ScannedRobotEvent) {
					return (int) (((ScannedRobotEvent) this).getDistance() - ((ScannedRobotEvent) o).getDistance());
				} else if (o instanceof HitRobotEvent && this instanceof HitRobotEvent) {
					int compare1 = ((HitRobotEvent) this).isMyFault() ? -1 : 0;
					int compare2 = ((HitRobotEvent) o).isMyFault() ? -1 : 0;

					return compare1 - compare2;
				} else {
					return 0;
				}
			}
		} catch (ClassCastException e) {
			return 0;
		}
	}

	/**
	 * Returns the priority of the event.
	 * 
	 * @return the priority of the event.
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * Returns the time the event occurred.
	 * 
	 * @return the time the event occurred.
	 */
	public long getTime() {
		return time;
	}

	/**
	 * Called by the game to set the priority of an event to the priority your robot
	 * specified for this type of event (or the default priority)
	 * @see robocode.AdvancedRobot#setEventPriority
	 */
	public void setPriority(int newPriority) {
		priority = newPriority;
	}

	/**
	 * Called by the game to set the time an event occurred.
	 */
	public void setTime(long newTime) {
		time = newTime;
	}
}
