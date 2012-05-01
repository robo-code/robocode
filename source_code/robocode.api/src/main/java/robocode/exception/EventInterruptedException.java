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
 *******************************************************************************/
package robocode.exception;


/**
 * @author Mathew A. Nelson (original)
 */
public class EventInterruptedException extends Error { // Must be error!
	private static final long serialVersionUID = 1L;

	private int priority = Integer.MIN_VALUE;

	public EventInterruptedException(int priority) {
		this.priority = priority;
	}

	public int getPriority() {
		return priority;
	}
}
