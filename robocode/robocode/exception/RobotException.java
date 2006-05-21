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
public class RobotException extends RuntimeException {

	/**
	 * RobotException constructor.
	 */
	public RobotException() {
		super();
	}

	/**
	 * RobotException constructor
	 * @param s java.lang.String
	 */
	public RobotException(String s) {
		super(s);
	}
}
