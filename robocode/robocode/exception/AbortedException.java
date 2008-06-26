/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package robocode.exception;


/**
 * @author Pavel Savara (original)
 * @since 1.6.1
 */
public class AbortedException extends Error { // Must be error!
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
