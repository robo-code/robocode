/*******************************************************************************
 * Copyright (c) 2001, 2006 Mathew Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew Nelson - initial API and implementation
 *******************************************************************************/
package robocode.exception;


/**
 * Insert the type's description here.
 * Creation date: (12/21/2000 3:59:58 PM)
 * @author: Mathew A. Nelson
 */
public class EventInterruptedException extends Error {
	int priority = Integer.MIN_VALUE;
	public EventInterruptedException(int priority) {
		this.priority = priority;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (9/25/2001 12:06:28 PM)
	 * @return int
	 */
	public int getPriority() {
		return priority;
	}
}
