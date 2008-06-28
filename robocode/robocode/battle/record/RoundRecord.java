/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
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


import robocode.BattleResults;

import java.util.ArrayList;
import java.util.List;


/**
 * Round record used for replaying battles containing a list of all turn, which
 * includes robot and bullet states, and the final battle results of the round.
 *
 * @author Flemming N. Larsen (original)
 */
public class RoundRecord {

	// List of turns
	public List<TurnRecord> turns = new ArrayList<TurnRecord>();

	// Results
	public BattleResults[] results;
}
