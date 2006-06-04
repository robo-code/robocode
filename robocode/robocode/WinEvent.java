/*******************************************************************************
 * Copyright (c) 2001-2006 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *******************************************************************************/
package robocode;


/**
 * This event is sent to {@link robocode.Robot#onWin onWin}
 * when your robot wins the round.
 *
 * @author Mathew A. Nelson
 */
public class WinEvent extends Event {

	/**
	 * Called by the game to create a new WinEvent.
	 */
	public WinEvent() {
		super();
	}
}
