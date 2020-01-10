/**
 * Copyright (c) 2001-2020 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package robocode;


import net.sf.robocode.peer.IRobotStatics;
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
 * @see #getCondition()
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public class CustomEvent extends Event {
	private static final long serialVersionUID = 1L;
	private static final int DEFAULT_PRIORITY = 80;

	private transient final Condition condition;

	/**
	 * Called by the game to create a new CustomEvent when a condition is met.
	 *
	 * @param condition the condition that must be met
	 */
	public CustomEvent(Condition condition) {
		this.condition = condition;
		if (condition != null) {
			setPriority(condition.getPriority());
		}
	}

	/**
	 * Called by the game to create a new CustomEvent when a condition is met.
	 * The event will have the given priority.
	 * An event priority is a value from 0 - 99. The higher value, the higher
	 * priority. The default priority is 80.
	 * <p>
	 * This is equivalent to calling {@link Condition#setPriority(int)} on the
	 * Condition.
	 *
	 * @param condition the condition that must be met
	 * @param priority  the priority of the condition
	 */
	public CustomEvent(Condition condition, int priority) {
		this.condition = condition;
		setPriority(priority);
		if (condition != null) {
			condition.setPriority(getPriority());
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	final int getDefaultPriority() {
		return DEFAULT_PRIORITY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	final void dispatch(IBasicRobot robot, IRobotStatics statics, Graphics2D graphics) {
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
	final boolean isCriticalEvent() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// final to disable overrides
	public final int getPriority() {
		return super.getPriority();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	byte getSerializationType() {
		throw new Error("Serialization not supported on this event type");
	}
}
