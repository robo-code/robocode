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
public class WinException extends Error { // Must be an Error!
	private static final long serialVersionUID = 1L;

	public WinException() {
		super();
	}

	public WinException(String s) {
		super(s);
	}
}
