/*******************************************************************************
 * Copyright (c) 2001, 2007 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Updated Javadoc
 *******************************************************************************/
package robocode;


/**
 * This event is sent to {@link Robot#onDeath onDeath} when your robot dies.
 *
 * @author Mathew A. Nelson (original)
 */
public class DeathEvent extends Event {

	/**
	 * Called by the game to create a new DeathEvent.
	 */
	public DeathEvent() {
		super();
	}
}
