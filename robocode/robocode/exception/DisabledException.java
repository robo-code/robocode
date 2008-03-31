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
@SuppressWarnings("serial")
public class DisabledException extends Error {

	public DisabledException() {
		super();
	}

	public DisabledException(String s) {
		super(s);
	}
}
