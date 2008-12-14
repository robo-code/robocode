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


import net.sf.robocode.security.IHiddenRulesHelper;

import java.io.Serializable;


/**
 * @author Pavel Savara (original)
 *         Immutable rules
 */
public final class BattleRules implements Serializable {
	private static final long serialVersionUID = 1L;

	private int battlefieldWidth;
	private int battlefieldHeight;
	private int numRounds;
	private double gunCoolingRate;
	private long inactivityTime;

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

	/* public BattleField getBattleField() {
	 return new DefaultBattleField(battlefieldWidth, battlefieldHeight);
	 }*/

	private BattleRules(int battlefieldWidth, int battlefieldHeight, int numRounds, double gunCoolingRate, long inactivityTime) {
		this.battlefieldWidth = battlefieldWidth;
		this.battlefieldHeight = battlefieldHeight;
		this.numRounds = numRounds;
		this.gunCoolingRate = gunCoolingRate;
		this.inactivityTime = inactivityTime;
	}

	static IHiddenRulesHelper createHiddenHelper() {
		return new HiddenHelper();
	}

	private static class HiddenHelper implements IHiddenRulesHelper {

		public BattleRules createRules(int battlefieldWidth, int battlefieldHeight, int numRounds, double gunCoolingRate, long inactivityTime) {
			return new BattleRules(battlefieldWidth, battlefieldHeight, numRounds, gunCoolingRate, inactivityTime);
		}
	}

}
