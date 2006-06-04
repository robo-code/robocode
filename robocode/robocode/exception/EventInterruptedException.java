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
package robocode.exception;


/**
 * @author Mathew A. Nelson (original)
 */
public class EventInterruptedException extends Error {
	int priority = Integer.MIN_VALUE;

	public EventInterruptedException(int priority) {
		this.priority = priority;
	}

	public int getPriority() {
		return priority;
	}
}
