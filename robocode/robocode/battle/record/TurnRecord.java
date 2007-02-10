/*******************************************************************************
 * Copyright (c) 2001-2007 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Flemming N. Larsen
 *     - Initial implementation
 *******************************************************************************/
package robocode.battle.record;


import java.util.List;


/**
 * Round record used for replaying battles containing the current state of all
 * robots and bullets, i.e. a snapshot of a turn.
 *
 * @author Flemming N. Larsen (original)
 */
public class TurnRecord {

	// List of robot states
	public List<RobotRecord> robotStates;

	// List of bullet states
	public List<BulletRecord> bulletStates;
}
