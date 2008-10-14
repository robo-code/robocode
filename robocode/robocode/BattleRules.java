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
package robocode;


import robocode.battle.BattleProperties;
import robocode.battlefield.BattleField;
import robocode.battlefield.DefaultBattleField;

import java.io.Serializable;


/**
 * @author Pavel Savara (original)
 * Immutable rules
 */
public final class BattleRules implements Serializable {
	private static final long serialVersionUID = 1L;

	private final int battlefieldWidth;
	private final int battlefieldHeight;
	private final int numRounds;
	private final double gunCoolingRate;
	private final long inactivityTime;

	public BattleRules(BattleProperties source) {
		this.battlefieldWidth = source.getBattlefieldWidth();
		this.battlefieldHeight = source.getBattlefieldHeight();
		this.numRounds = source.getNumRounds();
		this.gunCoolingRate = source.getGunCoolingRate();
		this.inactivityTime = source.getInactivityTime();
	}

	/**
	 * Gets the battlefieldWidth.
	 *
	 * @return Returns a int
	 */
	public int getBattlefieldWidth() {
		return battlefieldWidth;
	}

	/**
	 * Gets the battlefieldHeight.
	 *
	 * @return Returns a int
	 */
	public int getBattlefieldHeight() {
		return battlefieldHeight;
	}

	/**
	 * Gets the numRounds.
	 *
	 * @return Returns a int
	 */
	public int getNumRounds() {
		return numRounds;
	}

	/**
	 * Gets the gunCoolingRate.
	 *
	 * @return Returns a double
	 */
	public double getGunCoolingRate() {
		return gunCoolingRate;
	}

	/**
	 * Gets the inactivityTime.
	 *
	 * @return Returns a int
	 */
	public long getInactivityTime() {
		return inactivityTime;
	}

	public BattleField getBattleField() {
		return new DefaultBattleField(battlefieldWidth, battlefieldHeight);
	}

}
