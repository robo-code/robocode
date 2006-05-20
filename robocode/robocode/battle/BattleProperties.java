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
package robocode.battle;


import java.util.Properties;
import java.io.*;


public class BattleProperties {
	
	private Properties props = new Properties();
	
	private int battlefieldWidth = 800;
	private int battlefieldHeight = 600;
	private int numRounds = 10;
	private double gunCoolingRate = 0.1;
	private long inactivityTime = 450;
	private String selectedRobots;
	
	public final static String BATTLEFIELD_WIDTH = "robocode.battleField.width";
	public final static String BATTLEFIELD_HEIGHT = "robocode.battleField.height";
	public final static String BATTLE_NUMROUNDS = "robocode.battle.numRounds";
	public final static String BATTLE_GUNCOOLINGRATE = "robocode.battle.gunCoolingRate";
	public final static String BATTLE_RULES_INACTIVITYTIME = "robocode.battle.rules.inactivityTime";
	public final static String BATTLE_SELECTEDROBOTS = "robocode.battle.selectedRobots";

	/**
	 * Gets the battlefieldWidth.
	 * @return Returns a int
	 */
	public int getBattlefieldWidth() {
		return battlefieldWidth;
	}

	/**
	 * Sets the battlefieldWidth.
	 * @param battlefieldWidth The battlefieldWidth to set
	 */
	public void setBattlefieldWidth(int battlefieldWidth) {
		this.battlefieldWidth = battlefieldWidth;
		props.setProperty(BATTLEFIELD_WIDTH, "" + battlefieldWidth);
	}

	/**
	 * Gets the battlefieldHeight.
	 * @return Returns a int
	 */
	public int getBattlefieldHeight() {
		return battlefieldHeight;
	}

	/**
	 * Sets the battlefieldHeight.
	 * @param battlefieldHeight The battlefieldHeight to set
	 */
	public void setBattlefieldHeight(int battlefieldHeight) {
		this.battlefieldHeight = battlefieldHeight;
		props.setProperty(BATTLEFIELD_HEIGHT, "" + battlefieldHeight);
	}

	/**
	 * Gets the numRounds.
	 * @return Returns a int
	 */
	public int getNumRounds() {
		return numRounds;
	}

	/**
	 * Sets the numRounds.
	 * @param numRounds The numRounds to set
	 */
	public void setNumRounds(int numRounds) {
		this.numRounds = numRounds;
		props.setProperty(BATTLE_NUMROUNDS, "" + numRounds);
	}

	/**
	 * Gets the gunCoolingRate.
	 * @return Returns a double
	 */
	public double getGunCoolingRate() {
		return gunCoolingRate;
	}

	/**
	 * Sets the gunCoolingRate.
	 * @param gunCoolingRate The gunCoolingRate to set
	 */
	public void setGunCoolingRate(double gunCoolingRate) {
		this.gunCoolingRate = gunCoolingRate;
		props.setProperty(BATTLE_GUNCOOLINGRATE, "" + gunCoolingRate);
	}

	/**
	 * Gets the inactivityTime.
	 * @return Returns a int
	 */
	public long getInactivityTime() {
		return inactivityTime;
	}

	/**
	 * Sets the inactivityTime.
	 * @param inactivityTime The inactivityTime to set
	 */
	public void setInactivityTime(long inactivityTime) {
		this.inactivityTime = inactivityTime;
		props.setProperty(BATTLE_RULES_INACTIVITYTIME, "" + inactivityTime);
	}

	/**
	 * Gets the selectedRobots.
	 * @return Returns a String
	 */
	public String getSelectedRobots() {
		return selectedRobots;
	}

	/**
	 * Sets the selectedRobots.
	 * @param selectedRobots The selectedRobots to set
	 */
	public void setSelectedRobots(String selectedRobots) {
		this.selectedRobots = selectedRobots;
		props.setProperty(BATTLE_SELECTEDROBOTS, "" + selectedRobots);
	}
	
	public void store(FileOutputStream out, String desc) throws IOException {
		props.store(out, desc);
	}
	
	public void load(FileInputStream in) throws IOException {
		props.load(in);
		battlefieldWidth = Integer.parseInt(props.getProperty(BATTLEFIELD_WIDTH, "800"));
		battlefieldHeight = Integer.parseInt(props.getProperty(BATTLEFIELD_HEIGHT, "600"));
		gunCoolingRate = Double.parseDouble(props.getProperty(BATTLE_GUNCOOLINGRATE, "0.1"));
		inactivityTime = Long.parseLong(props.getProperty(BATTLE_RULES_INACTIVITYTIME, "450"));
		numRounds = Integer.parseInt(props.getProperty(BATTLE_NUMROUNDS, "10"));
		selectedRobots = props.getProperty(BATTLE_SELECTEDROBOTS, "");
	}

}

