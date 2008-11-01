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
 *     - Updated Javadocs
 *******************************************************************************/
package robocode;


import robocode.peer.RobotStatics;
import robocode.robotinterfaces.IAdvancedEvents;
import robocode.robotinterfaces.IAdvancedRobot;
import robocode.robotinterfaces.IBasicRobot;

import java.awt.*;


/**
 * This event is sent to {@link AdvancedRobot#onCustomEvent(CustomEvent)
 * onCustomEvent()} when a custom condition is met. Be sure to reset or remove
 * the custom condition to avoid having it recurring repeatedly (see the
 * example for the {@link #getCondition()} method.
 *
 * @author Mathew A. Nelson (original)
 * @see #getCondition()
 */
public class CustomEvent extends Event {
	private final Condition condition;
	private int priority;

	/**
	 * Called by the game to create a new CustomEvent when a condition is met.
	 *
	 * @param condition the condition that must be met
	 */
	public CustomEvent(Condition condition) {
		this.priority = getClassPriorityImpl();
		this.condition = condition;
	}

	/**
	 * Called by the game to create a new CustomEvent when a condition is met.
	 * The event will have the given priority.
	 * An event priority is a value from 0 - 99. The higher value, the higher
	 * priority. The default priority is 80.
	 * <p/>
	 * This is equivalent to calling {@link Condition#setPriority(int)} on the
	 * Condition.
	 *
	 * @param condition the condition that must be met
	 * @param priority  the priority of the condition
	 */
	public CustomEvent(Condition condition, int priority) {
		this.condition = condition;
		if (condition != null) {
			condition.setPriority(priority);
		}
	}

	/**
	 * Returns the condition that fired, causing this event to be generated.
	 * Use this to determine which condition fired, and to remove the custom
	 * event.
	 * <pre>
	 *   public void onCustomEvent(CustomEvent event) {
	 *       if (event.getCondition().getName().equals("mycondition")) {
	 *           removeCustomEvent(event.getCondition());
	 *           <i>// do something else</i>
	 *       }
	 *   }
	 * </pre>
	 *
	 * @return the condition that fired, causing this event to be generated
	 */
	public Condition getCondition() {
		return condition;
	}

	private static int classPriority = 80; // System event -> cannot be changed!;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getPriority() {
		return priority;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPriority(int newPriority) {
		priority = newPriority; 
	}

	@Override
	protected final int getClassPriorityImpl() {
		return classPriority;
	}

	@Override
	protected final void setClassPriorityImpl(int priority) {
		// System event -> cannot be changed!;
		System.out.println("SYSTEM: You may not change the priority of CustomEvent.  setPriority ignored.");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void dispatch(IBasicRobot robot, RobotStatics statics, Graphics2D graphics) {
		if (statics.isAdvancedRobot()) {
			IAdvancedEvents listener = ((IAdvancedRobot) robot).getAdvancedEventListener();

			if (listener != null) {
				listener.onCustomEvent(this);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// final to disable overrides
	public final int compareTo(Event event) {
		return super.compareTo(event);
	}    

	/**
	 * {@inheritDoc}
	 */
	@Override
	// final to disable overrides
	public final boolean isCriricalEvent() {
		return false;
	}
}
