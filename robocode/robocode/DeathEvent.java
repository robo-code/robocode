/*******************************************************************************
 * Copyright (c) 2001 Mathew Nelson
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew Nelson - initial API and implementation
 *******************************************************************************/
package robocode;

/**
 * This event is sent to {@link robocode.Robot#onDeath onDeath}
 * when your robot dies.
 */
public class DeathEvent extends Event {
/**
 * Called by the game to create a new DeathEvent.
 */
public DeathEvent() {
	super();
}
}
