/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package robocode.exception;


/**
 * @author Mathew A. Nelson (original)
 */
public class DeathException extends Error { // Must be an Error!
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
