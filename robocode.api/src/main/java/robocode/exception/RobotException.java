/**
 * Copyright (c) 2001-2020 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package robocode.exception;


/**
 * @author Mathew A. Nelson (original)
 */
public class RobotException extends Error { // Must be an Error!
	private static final long serialVersionUID = 1L;

	public RobotException() {
		super();
	}

	public RobotException(String s) {
		super(s);
	}
}
