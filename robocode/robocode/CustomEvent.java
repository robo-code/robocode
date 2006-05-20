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
package robocode;


/**
 * This event is sent to {@link robocode.AdvancedRobot#onCustomEvent onCustomEvent}
 * when a custom condition is met.  Be sure to reset or remove the custom condition
 * to avoid having it fire repeatedly.
 * @see #getCondition
 */
public class CustomEvent extends Event {
	private Condition condition;

	/**
	 * Called by the game to create a new CustomEvent when a condition is met.
	 */
	public CustomEvent(Condition condition) {
		this.condition = condition;
	}

	/**
	 * Called by the game to create a new CustomEvent when a condition is met.
	 * The event will have the given priority.
	 * This is equivalent to calling setPriority() on the Condition.
	 */
	public CustomEvent(Condition condition, int priority) {
		this.condition = condition;
		if (condition != null) {
			condition.setPriority(priority);
		}
	}

	/**
	 * Returns the condition that fired, causing this event to be generated.
	 * Use this to determine which condition fired, and to remove the custom event.
	 * <PRE>
	 *   public void onCustomEvent(CustomEvent event) {
	 *        if (event.getCondition().getName().equals("mycondition")) {
	 *            removeCustomEvent(event.getCondition());
	 *            doSomethingElse();
	 *        }
	 *   }
	 * </PRE>  
	 * @return the condition that fired, causing this event to be generated.
	 */
	public Condition getCondition() {
		return condition;
	}
}
