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
 *     Flemming N. Larsen
 *     - Added option for viewing ground, antialiasing, text antialiasing,
 *       rendering method, and method for getting the combined rendering hints
 *******************************************************************************/
package robocode.manager;


import java.io.*;
import java.util.*;
import java.text.*;
import java.awt.RenderingHints;

import robocode.util.Utils;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (current)
 */
public class RobocodeProperties {
	
	private Properties props = new Properties();
	
	private boolean optionsViewRobotNames = true;
	private boolean optionsViewScanArcs = false;
	private boolean optionsViewRobotEnergy = true;
	private boolean optionsViewGround = true;
	private boolean optionsViewFps = true;
	private int optionsRenderingAntialiasing = 0; // 0 = default, 1 = on, 2 = off 
	private int optionsRenderingTextAntialiasing = 0; // 0 = default, 1 = on, 2 = off
	private int optionsRenderingMethod = 0; // 0 = default, 1 = speed, 2 = quality
	private int optionsRenderingNoBuffers = 2; // 1 = single buffering, 2 = double buffering, 3 = tripple buffering 
	private int optionsBattleDesiredFps = 30;
	private boolean optionsBattleAllowColorChanges = false;
	private boolean optionsTeamShowTeamRobots = false;
	private String optionsDevelopmentPath = "";
	private String lastRunVersion = "";
	private Date versionChecked;
	private long robotFilesystemQuota = 200000;
	private long consoleQuota = 8192;
	private int cpuConstant = 200;

	private SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy H:mm:ss");
	
	private final static String OPTIONS_VIEW_ROBOTNAMES = "robocode.options.view.robotNames";
	private final static String OPTIONS_VIEW_SCANARCS = "robocode.options.view.scanArcs";
	private final static String OPTIONS_VIEW_ROBOTENERGY = "robocode.options.view.robotEnergy";
	private final static String OPTIONS_VIEW_GROUND = "robocode.options.view.ground";
	private final static String OPTIONS_VIEW_FPS = "robocode.options.view.FPS";
	private final static String OPTIONS_RENDERING_ANTIALIASING = "robocode.options.rendering.antialiasing";
	private final static String OPTIONS_RENDERING_TEXT_ANTIALIASING = "robocode.options.rendering.text.antialiasing";
	private final static String OPTIONS_RENDERING_METHOD = "robocode.options.rendering.method";
	private final static String OPTIONS_RENDERING_NO_BUFFERS = "robocode.options.rendering.noBuffers";
	private final static String OPTIONS_BATTLE_DESIREDFPS = "robocode.options.battle.desiredFPS";
	private final static String OPTIONS_BATTLE_ALLOWCOLORCHANGES = "robocode.options.battle.allowColorChanges";
	private final static String OPTIONS_TEAM_SHOWTEAMROBOTS = "robocode.options.team.showTeamRobots";
	private final static String OPTIONS_DEVELOPMENT_PATH = "robocode.options.development.path";
	private final static String VERSIONCHECKED = "robocode.versionchecked";
	private final static String ROBOT_FILESYSTEM_QUOTA = "robocode.robot.filesystem.quota";
	private final static String CONSOLE_QUOTA = "robocode.console.quota";
	private final static String CPU_CONSTANT = "robocode.cpu.constant.1000";
	private final static String LAST_RUN_VERSION = "robocode.version.lastrun";

	private RobocodeManager manager;

	private RenderingHints renderingHints = new RenderingHints(new HashMap()); 

	public RobocodeProperties(RobocodeManager manager) {
		this.manager = manager;
	}
	
	/**
	 * Gets the optionsViewRobotNames.
	 * 
	 * @return Returns a boolean
	 */
	public boolean getOptionsViewRobotNames() {
		return optionsViewRobotNames;
	}

	/**
	 * Sets the optionsViewRobotNames.
	 * 
	 * @param optionsViewRobotNames The optionsViewRobotNames to set
	 */
	public void setOptionsViewRobotNames(boolean optionsViewRobotNames) {
		this.optionsViewRobotNames = optionsViewRobotNames;
		props.setProperty(OPTIONS_VIEW_ROBOTNAMES, "" + optionsViewRobotNames);
	}

	/**
	 * Gets the optionsViewScanArcs.
	 * 
	 * @return Returns a boolean
	 */
	public boolean getOptionsViewScanArcs() {
		return optionsViewScanArcs;
	}

	/**
	 * Sets the optionsViewScanArcs.
	 * 
	 * @param optionsViewScanArcs The optionsViewScanArcs to set
	 */
	public void setOptionsViewScanArcs(boolean optionsViewScanArcs) {
		this.optionsViewScanArcs = optionsViewScanArcs;
		props.setProperty(OPTIONS_VIEW_SCANARCS, "" + optionsViewScanArcs);
	}

	/**
	 * Gets the optionsViewRobotEnergy.
	 * 
	 * @return Returns a boolean
	 */
	public boolean getOptionsViewRobotEnergy() {
		return optionsViewRobotEnergy;
	}

	/**
	 * Sets the optionsViewRobotEnergy.
	 * 
	 * @param optionsViewRobotEnergy The optionsViewRobotEnergy to set
	 */
	public void setOptionsViewRobotEnergy(boolean optionsViewRobotEnergy) {
		this.optionsViewRobotEnergy = optionsViewRobotEnergy;
		props.setProperty(OPTIONS_VIEW_ROBOTENERGY, "" + optionsViewRobotEnergy);
	}

	/**
	 * Gets the optionsViewGround.
	 * 
	 * @return Returns a boolean
	 */
	public boolean getOptionsViewGround() {
		return optionsViewGround;
	}

	/**
	 * Sets the optionsViewGround.
	 * 
	 * @param optionsViewGround The optionsViewGround to set
	 */
	public void setOptionsViewGround(boolean optionsViewGround) {
		this.optionsViewGround = optionsViewGround;
		props.setProperty(OPTIONS_VIEW_GROUND, "" + optionsViewGround);
	}

	/**
	 * Gets the optionsViewFps.
	 * 
	 * @return Returns a boolean
	 */
	public boolean getOptionsViewFps() {
		return optionsViewFps;
	}

	/**
	 * Sets the optionsViewFps.
	 * 
	 * @param optionsViewFps The optionsViewFps to set
	 */
	public void setOptionsViewFps(boolean optionsViewFps) {
		this.optionsViewFps = optionsViewFps;
		props.setProperty(OPTIONS_VIEW_FPS, "" + optionsViewFps);
	}

	/**
	 * Gets the optionsRenderingAntialiasing.
	 * 
	 * @return Returns an int
	 */
	public int getOptionsRenderingAntialiasing() {
		return optionsRenderingAntialiasing;
	}

	/**
	 * Sets the optionsRenderingAntialiasing.
	 * 
	 * @param optionsRenderingAntialiasing The optionsRenderingAntialiasing to set
	 */
	public void setOptionsRenderingAntialiasing(int optionsRenderingAntialiasing) {
		this.optionsRenderingAntialiasing = optionsRenderingAntialiasing;
		props.setProperty(OPTIONS_RENDERING_ANTIALIASING, "" + optionsRenderingAntialiasing);

		Object value;

		switch (optionsRenderingAntialiasing) {
		case 1:
			value = RenderingHints.VALUE_ANTIALIAS_ON;
			break;

		case 2:
			value = RenderingHints.VALUE_ANTIALIAS_OFF;
			break;		

		case 0:
		default:
			value = RenderingHints.VALUE_ANTIALIAS_DEFAULT;
		}
		renderingHints.put(RenderingHints.KEY_ANTIALIASING, value);
	}

	/**
	 * Gets the optionsRenderingTextAntialiasing.
	 * 
	 * @return Returns an int
	 */
	public int getOptionsRenderingTextAntialiasing() {
		return optionsRenderingTextAntialiasing;
	}

	/**
	 * Sets the optionsRenderingTextAntialiasing.
	 * 
	 * @param optionsRenderingTextAntialiasing The optionsRenderingTextAntialiasing to set
	 */
	public void setOptionsRenderingTextAntialiasing(int optionsRenderingTextAntialiasing) {
		this.optionsRenderingTextAntialiasing = optionsRenderingTextAntialiasing;
		props.setProperty(OPTIONS_RENDERING_TEXT_ANTIALIASING, "" + optionsRenderingTextAntialiasing);

		Object value;

		switch (optionsRenderingTextAntialiasing) {
		case 1:
			value = RenderingHints.VALUE_TEXT_ANTIALIAS_ON;
			break;

		case 2:
			value = RenderingHints.VALUE_TEXT_ANTIALIAS_OFF;
			break;		

		case 0:
		default:
			value = RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT;
		}
		renderingHints.put(RenderingHints.KEY_TEXT_ANTIALIASING, value);
	}

	/**
	 * Gets the optionsRenderingMethod.
	 * 
	 * @return Returns an int
	 */
	public int getOptionsRenderingMethod() {
		return optionsRenderingMethod;
	}

	/**
	 * Sets the optionsRenderingMethod.
	 * 
	 * @param optionsRenderingMethod The optionsRenderingMethod to set
	 */
	public void setOptionsRenderingMethod(int optionsRenderingMethod) {
		this.optionsRenderingMethod = optionsRenderingMethod;
		props.setProperty(OPTIONS_RENDERING_METHOD, "" + optionsRenderingMethod);

		Object value;

		switch (optionsRenderingMethod) {
		case 1:
			value = RenderingHints.VALUE_RENDER_QUALITY;
			break;

		case 2:
			value = RenderingHints.VALUE_RENDER_SPEED;
			break;		

		case 0:
		default:
			value = RenderingHints.VALUE_RENDER_DEFAULT;
		}
		renderingHints.put(RenderingHints.KEY_RENDERING, value);
	}

	/**
	 * Gets the combined rendering options as RenderingHints.
	 * 
	 * @return Returns an RenderingHints value
	 */
	public RenderingHints getRenderingHints() {
		return renderingHints;
	}

	/**
	 * Gets the optionsRenderingNoBuffers
	 * 
	 * @return Returns an int
	 */
	public int getOptionsRenderingNoBuffers() {
		return optionsRenderingNoBuffers;
	}

	/**
	 * Sets the optionsRenderingNoBuffers.
	 * 
	 * @param optionsRenderingNoBuffers The optionsRenderingNoBuffers to set
	 */
	public void setOptionsRenderingNoBuffers(int optionsRenderingNoBuffers) {
		this.optionsRenderingNoBuffers = optionsRenderingNoBuffers;
		props.setProperty(OPTIONS_RENDERING_NO_BUFFERS, "" + optionsRenderingNoBuffers);
	}

	/**
	 * Gets the optionsBattleDesiredFps.
	 * 
	 * @return Returns a int
	 */
	public int getOptionsBattleDesiredFps() {
		return optionsBattleDesiredFps;
	}

	/**
	 * Sets the optionsBattleDesiredFps.
	 * 
	 * @param optionsBattleDesiredFps The optionsBattleDesiredFps to set
	 */
	public void setOptionsBattleDesiredFps(int optionsBattleDesiredFps) {
		this.optionsBattleDesiredFps = optionsBattleDesiredFps;
		props.setProperty(OPTIONS_BATTLE_DESIREDFPS, "" + optionsBattleDesiredFps);
	}

	public boolean getOptionsBattleAllowColorChanges() {
		return optionsBattleAllowColorChanges;
	}
	
	public void setOptionsBattleAllowColorChanges(boolean optionsBattleAllowColorChanges) {
		this.optionsBattleAllowColorChanges = optionsBattleAllowColorChanges;
		props.setProperty(OPTIONS_BATTLE_ALLOWCOLORCHANGES, "" + optionsBattleAllowColorChanges);
	}
	
	public boolean getOptionsTeamShowTeamRobots() {
		return optionsTeamShowTeamRobots;
	}
	
	public void setOptionsTeamShowTeamRobots(boolean optionsTeamShowTeamRobots) {
		this.optionsTeamShowTeamRobots = optionsTeamShowTeamRobots;
		props.setProperty(OPTIONS_TEAM_SHOWTEAMROBOTS, "" + optionsTeamShowTeamRobots);
	}
	
	/**
	 * Gets the versionChecked.
	 * 
	 * @return Returns a String
	 */
	public Date getVersionChecked() {
		return versionChecked;
	}

	/**
	 * Sets the versionChecked.
	 * 
	 * @param versionChecked The versionChecked to set
	 */
	public void setVersionChecked(Date versionChecked) {
		this.versionChecked = versionChecked;
		props.setProperty(VERSIONCHECKED, dateFormat.format(new Date()));
	}

	/**
	 * Gets the robotFilesystemQuota.
	 * 
	 * @return Returns a long
	 */
	public long getRobotFilesystemQuota() {
		return robotFilesystemQuota;
	}

	/**
	 * Sets the robotFilesystemQuota.
	 * 
	 * @param robotFilesystemQuota The robotFilesystemQuota to set
	 */
	public void setRobotFilesystemQuota(long robotFilesystemQuota) {
		this.robotFilesystemQuota = robotFilesystemQuota;
		props.setProperty(ROBOT_FILESYSTEM_QUOTA, "" + robotFilesystemQuota);
	}

	/**
	 * Gets the consoleQuota.
	 * 
	 * @return Returns a long
	 */
	public long getConsoleQuota() {
		return consoleQuota;
	}

	/**
	 * Sets the consoleQuota.
	 * 
	 * @param consoleQuota The consoleQuota to set
	 */
	public void setConsoleQuota(long consoleQuota) {
		this.consoleQuota = consoleQuota;
		props.setProperty(CONSOLE_QUOTA, "" + consoleQuota);
	}

	/**
	 * Gets the cpuConstant.
	 * 
	 * @return Returns a int
	 */
	public int getCpuConstant() {
		return cpuConstant;
	}

	/**
	 * Sets the cpuConstant.
	 * 
	 * @param cpuConstant The cpuConstant to set
	 */
	public void setCpuConstant(int cpuConstant) {
		this.cpuConstant = cpuConstant;
		props.setProperty(CPU_CONSTANT, "" + cpuConstant);
	}

	/**
	 * Gets the optionsDevelopmentPath
	 * 
	 * @return Returns a String
	 */
	public String getOptionsDevelopmentPath() {
		return optionsDevelopmentPath;
	}

	/**
	 * Sets the optionsDevelopmentPath.
	 * 
	 * @param optionsDevelopmentPath The optionsDevelopmentPath to set
	 */
	public void setOptionsDevelopmentPath(String optionsDevelopmentPath) {
		try {
			if (!optionsDevelopmentPath.equals(this.optionsDevelopmentPath)) {
				manager.getRobotRepositoryManager().clearRobotList();
			}
		} catch (NullPointerException e) { // Just to be safe
			manager.getRobotRepositoryManager().clearRobotList();
		}
		this.optionsDevelopmentPath = optionsDevelopmentPath;
		props.setProperty(OPTIONS_DEVELOPMENT_PATH, optionsDevelopmentPath);
	}

	public void store(FileOutputStream out, String desc) throws IOException {
		props.store(out, desc);
	}
	
	public void load(FileInputStream in) throws IOException {
		props.load(in);
		optionsViewRobotNames = Boolean.valueOf(props.getProperty(OPTIONS_VIEW_ROBOTNAMES, "true")).booleanValue();
		optionsViewScanArcs = Boolean.valueOf(props.getProperty(OPTIONS_VIEW_SCANARCS, "false")).booleanValue();
		optionsViewRobotEnergy = Boolean.valueOf(props.getProperty(OPTIONS_VIEW_ROBOTENERGY, "true")).booleanValue();
		optionsViewGround = Boolean.valueOf(props.getProperty(OPTIONS_VIEW_GROUND, "true")).booleanValue();
		optionsViewFps = Boolean.valueOf(props.getProperty(OPTIONS_VIEW_FPS, "true")).booleanValue();
		optionsRenderingAntialiasing = Integer.parseInt(props.getProperty(OPTIONS_RENDERING_ANTIALIASING, "0"));
		optionsRenderingTextAntialiasing = Integer.parseInt(props.getProperty(OPTIONS_RENDERING_TEXT_ANTIALIASING, "0"));
		optionsRenderingMethod = Integer.parseInt(props.getProperty(OPTIONS_RENDERING_METHOD, "0"));
		optionsRenderingNoBuffers = Integer.parseInt(props.getProperty(OPTIONS_RENDERING_NO_BUFFERS, "2"));
		optionsBattleDesiredFps = Integer.parseInt(props.getProperty(OPTIONS_BATTLE_DESIREDFPS, "30"));
		optionsBattleAllowColorChanges = Boolean.valueOf(props.getProperty(OPTIONS_BATTLE_ALLOWCOLORCHANGES, "false")).booleanValue();
		optionsTeamShowTeamRobots = Boolean.valueOf(props.getProperty(OPTIONS_TEAM_SHOWTEAMROBOTS, "false")).booleanValue();
		optionsDevelopmentPath = props.getProperty(OPTIONS_DEVELOPMENT_PATH, "");
		lastRunVersion = props.getProperty(LAST_RUN_VERSION, "");
		
		try {
			props.remove("robocode.cpu.constant");
		} catch (Exception e) {}
		
		try {
			versionChecked = dateFormat.parse(props.getProperty(VERSIONCHECKED));
		} catch (Exception e) {
			Utils.log("Initializing version check date.");
			setVersionChecked(new Date());
		}
		
		robotFilesystemQuota = Long.parseLong(props.getProperty(ROBOT_FILESYSTEM_QUOTA, "" + 200000));
		consoleQuota = Long.parseLong(props.getProperty(CONSOLE_QUOTA, "8192"));
		cpuConstant = Integer.parseInt(props.getProperty(CPU_CONSTANT, "-1"));
	}

	public String getLastRunVersion() {
		return lastRunVersion;
	}

	/**
	 * Sets the cpuConstant.
	 * 
	 * @param cpuConstant The cpuConstant to set
	 */
	public void setLastRunVersion(String lastRunVersion) {
		this.lastRunVersion = lastRunVersion;
		props.setProperty(LAST_RUN_VERSION, "" + lastRunVersion);
	}
}
