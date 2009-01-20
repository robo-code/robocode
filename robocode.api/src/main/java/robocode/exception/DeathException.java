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
public class DeathException extends Error { // Must be error!
	// From viewpoint of the Robot, an Error is a JVM error:
	// Robot died, their CPU exploded, the JVM for the robot's brain has an error.
	private static final long serialVersionUID = 1L;

	public DeathException() {
		super();
	}

	public DeathException(String message) {
		super(message);
	}
}
