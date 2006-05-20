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
package robocode.exception;


/**
 * Insert the type's description here.
 * Creation date: (12/18/2000 7:24:28 PM)
 * @author: Mathew A. Nelson
 */
public class DeathException extends Error {

	/**
	 * DeathException constructor comment.
	 */
	public DeathException() {
		super();
	}

	/**
	 * DeathException constructor comment.
	 * @param message java.lang.String
	 */
	public DeathException(String message) {
		super(message);
	}
}
