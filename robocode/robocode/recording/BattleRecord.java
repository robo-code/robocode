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
package robocode.recording;


import robocode.battle.BattleProperties;
import robocode.BattleResults;
import robocode.BattleRules;

import java.io.Serializable;


/**
 * @author Pavel Savara (original)
 */
public class BattleRecord implements Serializable {
	private static final long serialVersionUID = 1L;

	public int robotCount;
	public int rounds;
	public BattleRules battleRules;
	public int[] recordsInTurns;
	public byte[] records;
	public BattleResults[] results;
}
