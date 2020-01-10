/**
 * Copyright (c) 2001-2020 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package robocode.exception;


/**
 * @author Pavel Savara (original)
 *
 * @since 1.6.1
 */
public class AbortedException extends Error { // Must be an Error!
	// From viewpoint of the Robot, an Error is a JVM error:
	// Robot died, their CPU exploded, the JVM for the robot's brain has an error.
	private static final long serialVersionUID = 1L;

	public AbortedException() {
		super();
	}

	public AbortedException(String message) {
		super(message);
	}
}
