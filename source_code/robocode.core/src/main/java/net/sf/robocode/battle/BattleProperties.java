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
 *     Flemming N. Larsen
 *     - Added setSelectedRobots(RobotSpecification[])
 *     - Added property for specifying initial positions and headings of the
 *       selected robots
 *******************************************************************************/
package net.sf.robocode.battle;


import robocode.AdvancedRobot;
import robocode.Robot;
import robocode.control.RobotSpecification;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public class BattleProperties implements Serializable {

	private static final long serialVersionUID = 1L;

	private final static String
			BATTLEFIELD_WIDTH = "robocode.battleField.width",
			BATTLEFIELD_HEIGHT = "robocode.battleField.height",
			BATTLE_NUMROUNDS = "robocode.battle.numRounds",
			BATTLE_GUNCOOLINGRATE = "robocode.battle.gunCoolingRate",
			BATTLE_RULES_INACTIVITYTIME = "robocode.battle.rules.inactivityTime",
			BATTLE_SELECTEDROBOTS = "robocode.battle.selectedRobots",
			BATTLE_INITIAL_POSITIONS = "robocode.battle.initialPositions",
			BATTLE_EXTENSION_FILENAME = "robocode.battle.extensionFile",
			BATTLE_EXTENSION_PACKAGE = "robocode.battle.extensionPackage";
	
	private int battlefieldWidth = 800;
	private int battlefieldHeight = 600;
	private int numRounds = 10;
	private double gunCoolingRate = 0.1;
	private long inactivityTime = 450;
	private String selectedRobots;
	private String initialPositions;
	private String extensionFilename = "";
	private String extensionPackage = ""; 
	
	private final Properties props = new Properties();

	/**
	 * Gets the battlefieldWidth.
	 *
	 * @return Returns a int
	 */
	public int getBattlefieldWidth() {
		return battlefieldWidth;
	}

	/**
	 * Sets the battlefieldWidth.
	 *
	 * @param battlefieldWidth The battlefieldWidth to set
	 */
	public void setBattlefieldWidth(int battlefieldWidth) {
		this.battlefieldWidth = battlefieldWidth;
		props.setProperty(BATTLEFIELD_WIDTH, "" + battlefieldWidth);
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
	 * Sets the battlefieldHeight.
	 *
	 * @param battlefieldHeight The battlefieldHeight to set
	 */
	public void setBattlefieldHeight(int battlefieldHeight) {
		this.battlefieldHeight = battlefieldHeight;
		props.setProperty(BATTLEFIELD_HEIGHT, "" + battlefieldHeight);
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
	 * Sets the numRounds.
	 *
	 * @param numRounds The numRounds to set
	 */
	public void setNumRounds(int numRounds) {
		this.numRounds = numRounds;
		props.setProperty(BATTLE_NUMROUNDS, "" + numRounds);
	}

	/**
	 * Returns the rate at which the gun will cool down, i.e. the amount of heat the gun heat will drop per turn.
	 * <p/>
	 * The gun cooling rate is default 0.1 per turn, but can be changed by the battle setup.
	 * So don't count on the cooling rate being 0.1!
	 *
	 * @return the gun cooling rate
	 * @see #setGunCoolingRate(double)
	 * @see Robot#getGunHeat()
	 * @see Robot#fire(double)
	 * @see Robot#fireBullet(double)
	 * @see robocode.BattleRules#getGunCoolingRate()
	 */
	public double getGunCoolingRate() {
		return gunCoolingRate;
	}

	/**
	 * Sets the rate at which the gun will cool down, i.e. the amount of heat the gun heat will drop per turn.
	 *
	 * @param gunCoolingRate the new gun cooling rate
	 * @see #getGunCoolingRate
	 * @see Robot#getGunHeat()
	 * @see Robot#fire(double)
	 * @see Robot#fireBullet(double)
	 * @see robocode.BattleRules#getGunCoolingRate()
	 */
	public void setGunCoolingRate(double gunCoolingRate) {
		this.gunCoolingRate = gunCoolingRate;
		props.setProperty(BATTLE_GUNCOOLINGRATE, "" + gunCoolingRate);
	}

	/**
	 * Returns the allowed inactivity time, where the robot is not taking any action, before will begin to be zapped.
	 * The inactivity time is measured in turns, and is the allowed time that a robot is allowed to omit taking
	 * action before being punished by the game by zapping.
	 * <p/>
	 * When a robot is zapped by the game, it will loose 0.1 energy points per turn. Eventually the robot will be
	 * killed by zapping until the robot takes action. When the robot takes action, the inactivity time counter is
	 * reset. 
	 * <p/>
	 * The allowed inactivity time is per default 450 turns, but can be changed by the battle setup.
	 * So don't count on the inactivity time being 450 turns!
	 *
	 * @return the allowed inactivity time.
	 * @see robocode.BattleRules#getInactivityTime()
	 * @see Robot#doNothing()
	 * @see AdvancedRobot#execute()
	 */
	public long getInactivityTime() {
		return inactivityTime;
	}

	/**
	 * Sets the allowed inactivity time, where the robot is not taking any action, before will begin to be zapped.
	 *
	 * @param inactivityTime the new allowed inactivity time.
	 * @see robocode.BattleRules#getInactivityTime()
	 * @see Robot#doNothing()
	 * @see AdvancedRobot#execute()
	 */
	public void setInactivityTime(long inactivityTime) {
		this.inactivityTime = inactivityTime;
		props.setProperty(BATTLE_RULES_INACTIVITYTIME, "" + inactivityTime);
	}

	/**
	 * Gets the selectedRobots.
	 *
	 * @return Returns a String
	 */
	public String getSelectedRobots() {
		return selectedRobots;
	}

	/**
	 * Sets the selectedRobots.
	 *
	 * @param selectedRobots The selectedRobots to set
	 */
	public void setSelectedRobots(String selectedRobots) {
		this.selectedRobots = selectedRobots;
		props.setProperty(BATTLE_SELECTEDROBOTS, "" + selectedRobots);
	}

	/**
	 * Sets the selectedRobots.
	 *
	 * @param robots The robots to set
	 */
	public void setSelectedRobots(RobotSpecification[] robots) {
		String robotString = "";
		RobotSpecification robot;

		for (int i = 0; i < robots.length; i++) {
			robot = robots[i];
			if (robot == null) {
				continue;
			}

			robotString += robot.getClassName();

			if (!(robot.getVersion() == null || robot.getVersion().length() == 0)) {
				robotString += " " + robot.getVersion();
			}

			if (i < robots.length - 1) {
				robotString += ",";
			}
		}
		setSelectedRobots(robotString);
	}

	/**
	 * Gets the initial robot positions and heading.
	 *
	 * @return a comma-separated string
	 */
	public String getInitialPositions() {
		return initialPositions;
	}

	public void setInitialPositions(String initialPositions) {
		this.initialPositions = initialPositions; 
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
		initialPositions = props.getProperty(BATTLE_INITIAL_POSITIONS, "");
		extensionFilename = props.getProperty(BATTLE_EXTENSION_FILENAME, "");
		extensionPackage = props.getProperty(BATTLE_EXTENSION_PACKAGE, "");
	}

	public void setExtensionPackage(String extensionPackage) {
		this.extensionPackage = extensionPackage;
	}

	public String getExtensionPackage() {
		if (extensionPackage == null) {
			return "";
		}
		return extensionPackage;
	}
	
	public void setExtensionFilename(String extensionFilename) {
		this.extensionFilename = extensionFilename;
	}
	
	public String getExtensionFilename() {
		if (extensionFilename == null) {
			return "";
		}
		return extensionFilename;
	}
}
