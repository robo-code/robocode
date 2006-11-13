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
 *     - Changed the FPS methods into TPS methods, but added the "Display FPS in
 *       titlebar" option
 *     - Added sound options
 *     - Added common options for showing battle result and append when saving
 *       results
 *     - Changed to have static access for all methods
 *******************************************************************************/
package robocode.manager;


import java.io.*;
import java.util.*;
import java.text.*;
import java.awt.RenderingHints;

import robocode.util.Constants;
import robocode.util.Utils;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (current)
 */
public class RobocodeProperties {

	private static Properties instance = new Properties();
	
	// View Options (Arena)
	private static boolean optionsViewRobotEnergy = true;
	private static boolean optionsViewRobotNames = true;
	private static boolean optionsViewScanArcs = false;
	private static boolean optionsViewExplosions = true;
	private static boolean optionsViewGround = true;
	private static boolean optionsBattleAllowColorChanges = false;

	// View Options (Turns Per Second)
	private static boolean optionsViewTPS = true;
	private static boolean optionsViewFPS = true;

	// Rendering Options
	private static int optionsRenderingAntialiasing = 0; // 0 = default, 1 = on, 2 = off 
	private static int optionsRenderingTextAntialiasing = 0; // 0 = default, 1 = on, 2 = off
	private static int optionsRenderingMethod = 0; // 0 = default, 1 = speed, 2 = quality
	private static int optionsRenderingNoBuffers = 2; // 1 = single buffering, 2 = double buffering, 3 = tripple buffering 
	private static int optionsBattleDesiredTPS = 30;

	// Sound Options (Sound Effects)
	private static boolean optionsSoundEnableSound = false;
	private static boolean optionsSoundEnableGunShot = true;
	private static boolean optionsSoundEnableBulletHit = true;
	private static boolean optionsSoundEnableRobotDeath = true;
	private static boolean optionsSoundEnableWallCollision = true;
	private static boolean optionsSoundEnableRobotCollision = true;

	// Sound Options (Mixer)
	private static String optionsSoundMixer = "DirectAudioDevice";
	private static boolean optionsSoundEnableMixerVolume = true;
	private static boolean optionsSoundEnableMixerPan = true;
	
	// Development Options
	private static String optionsDevelopmentPath = "";

	// Common Options
	private static boolean optionsCommonShowResults = true;
	private static boolean optionsCommonAppendWhenSavingResults = true; 

	private static boolean optionsTeamShowTeamRobots = false;
	
	private static String lastRunVersion = "";
	private static Date versionChecked;
	private static long robotFilesystemQuota = 200000;
	private static long consoleQuota = 8192;
	private static int cpuConstant = 200;

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy H:mm:ss");
	
	private final static String
	
			OPTIONS_VIEW_ROBOTNAMES = "robocode.options.view.robotNames",
			OPTIONS_VIEW_SCANARCS = "robocode.options.view.scanArcs",
			OPTIONS_VIEW_ROBOTENERGY = "robocode.options.view.robotEnergy",
			OPTIONS_VIEW_GROUND = "robocode.options.view.ground",
			OPTIONS_VIEW_TPS = "robocode.options.view.TPS",
			OPTIONS_VIEW_FPS = "robocode.options.view.FPS",
			OPTIONS_VIEW_EXPLOSIONS = "robocode.options.view.explosions",
			OPTIONS_BATTLE_DESIREDTPS = "robocode.options.battle.desiredTPS",
			OPTIONS_BATTLE_ALLOWCOLORCHANGES = "robocode.options.battle.allowColorChanges",

			OPTIONS_RENDERING_ANTIALIASING = "robocode.options.rendering.antialiasing",
			OPTIONS_RENDERING_TEXT_ANTIALIASING = "robocode.options.rendering.text.antialiasing",
			OPTIONS_RENDERING_METHOD = "robocode.options.rendering.method",
			OPTIONS_RENDERING_NO_BUFFERS = "robocode.options.rendering.noBuffers",

			OPTIONS_SOUND_ENABLESOUND = "robocode.options.sound.enableSound",
			OPTIONS_SOUND_ENABLEGUNSHOT = "robocode.options.sound.enableGunShot",
			OPTIONS_SOUND_ENABLEBULLETHIT = "robocode.options.sound.enableBulletHit",
			OPTIONS_SOUND_ENABLEROBOTDEATH = "robocode.options.sound.enableRobotDeath",
			OPTIONS_SOUND_ENABLEWALLCOLLISION = "robocode.options.sound.enableWallCollision",
			OPTIONS_SOUND_ENABLEROBOTCOLLISION = "robocode.options.sound.enableRobotCollision",

			OPTIONS_SOUND_MIXER = "robocode.options.sound.mixer",
			OPTIONS_SOUND_ENABLEMIXERVOLUME = "robocode.options.sound.enableMixerVolume",
			OPTIONS_SOUND_ENABLEMIXERPAN = "robocode.options.sound.enableMixerPan",

			OPTIONS_COMMON_SHOW_RESULTS = "robocode.options.common.showResults",
			OPTIONS_COMMON_APPEND_WHEN_SAVING_RESULTS = "robocode.options.common.appendWhenSavingResults",

			OPTIONS_TEAM_SHOWTEAMROBOTS = "robocode.options.team.showTeamRobots",
			OPTIONS_DEVELOPMENT_PATH = "robocode.options.development.path",
			VERSIONCHECKED = "robocode.versionchecked",
			ROBOT_FILESYSTEM_QUOTA = "robocode.robot.filesystem.quota",
			CONSOLE_QUOTA = "robocode.console.quota",
			CPU_CONSTANT = "robocode.cpu.constant.1000",
			LAST_RUN_VERSION = "robocode.version.lastrun";

	private static RenderingHints renderingHints = new RenderingHints(new HashMap<RenderingHints.Key, Object>());

	static {
		try {
			load();
		} catch (FileNotFoundException e) {
			Utils.log("No robocode.properties file, using defaults");
		} catch (IOException e) {
			Utils.log("IO Exception reading robocode.properties: " + e);
		}
	}

	/**
	 * Gets the optionsViewRobotNames.
	 * 
	 * @return Returns a boolean
	 */
	public static boolean getOptionsViewRobotNames() {
		return optionsViewRobotNames;
	}

	/**
	 * Sets the optionsViewRobotNames.
	 * 
	 * @param optionsViewRobotNames The optionsViewRobotNames to set
	 */
	public static void setOptionsViewRobotNames(boolean optionsViewRobotNames) {
		RobocodeProperties.optionsViewRobotNames = optionsViewRobotNames;
		instance.setProperty(OPTIONS_VIEW_ROBOTNAMES, "" + optionsViewRobotNames);
	}

	/**
	 * Gets the optionsViewScanArcs.
	 * 
	 * @return Returns a boolean
	 */
	public static boolean getOptionsViewScanArcs() {
		return optionsViewScanArcs;
	}

	/**
	 * Sets the optionsViewScanArcs.
	 * 
	 * @param optionsViewScanArcs The optionsViewScanArcs to set
	 */
	public static void setOptionsViewScanArcs(boolean optionsViewScanArcs) {
		RobocodeProperties.optionsViewScanArcs = optionsViewScanArcs;
		instance.setProperty(OPTIONS_VIEW_SCANARCS, "" + optionsViewScanArcs);
	}

	/**
	 * Gets the optionsViewRobotEnergy.
	 * 
	 * @return Returns a boolean
	 */
	public static boolean getOptionsViewRobotEnergy() {
		return optionsViewRobotEnergy;
	}

	/**
	 * Sets the optionsViewRobotEnergy.
	 * 
	 * @param optionsViewRobotEnergy The optionsViewRobotEnergy to set
	 */
	public static void setOptionsViewRobotEnergy(boolean optionsViewRobotEnergy) {
		RobocodeProperties.optionsViewRobotEnergy = optionsViewRobotEnergy;
		instance.setProperty(OPTIONS_VIEW_ROBOTENERGY, "" + optionsViewRobotEnergy);
	}

	/**
	 * Gets the optionsViewGround.
	 * 
	 * @return Returns a boolean
	 */
	public static boolean getOptionsViewGround() {
		return optionsViewGround;
	}

	/**
	 * Sets the optionsViewGround.
	 * 
	 * @param optionsViewGround The optionsViewGround to set
	 */
	public static void setOptionsViewGround(boolean optionsViewGround) {
		RobocodeProperties.optionsViewGround = optionsViewGround;
		instance.setProperty(OPTIONS_VIEW_GROUND, "" + optionsViewGround);
	}

	/**
	 * Gets the optionsViewTPS.
	 * 
	 * @return Returns a boolean
	 */
	public static boolean getOptionsViewTPS() {
		return optionsViewTPS;
	}

	/**
	 * Sets the optionsViewTPS.
	 * 
	 * @param optionsViewTPS The optionsViewTPS to set
	 */
	public static void setOptionsViewTPS(boolean optionsViewTPS) {
		RobocodeProperties.optionsViewTPS = optionsViewTPS;
		instance.setProperty(OPTIONS_VIEW_TPS, "" + optionsViewTPS);
	}

	/**
	 * Gets the optionsViewFPS.
	 * 
	 * @return Returns a boolean
	 */
	public static boolean getOptionsViewFPS() {
		return optionsViewFPS;
	}

	/**
	 * Sets the optionsViewFPS.
	 * 
	 * @param optionsViewFPS The optionsViewFPS to set
	 */
	public static void setOptionsViewFPS(boolean optionsViewFPS) {
		RobocodeProperties.optionsViewFPS = optionsViewFPS;
		instance.setProperty(OPTIONS_VIEW_FPS, "" + optionsViewFPS);
	}

	/**
	 * Gets the optionsViewExplosions.
	 * 
	 * @return Returns a boolean
	 */
	public static boolean getOptionsViewExplosions() {
		return optionsViewExplosions;
	}

	/**
	 * Sets the optionsViewExplosions.
	 * 
	 * @param optionsViewExplosions The optionsViewExplosions to set
	 */
	public static void setOptionsViewExplosions(boolean optionsViewExplosions) {
		RobocodeProperties.optionsViewExplosions = optionsViewExplosions;
		instance.setProperty(OPTIONS_VIEW_EXPLOSIONS, "" + optionsViewExplosions);
	}

	/**
	 * Gets the optionsRenderingAntialiasing.
	 * 
	 * @return Returns an int
	 */
	public static int getOptionsRenderingAntialiasing() {
		return optionsRenderingAntialiasing;
	}

	/**
	 * Sets the optionsRenderingAntialiasing.
	 * 
	 * @param optionsRenderingAntialiasing The optionsRenderingAntialiasing to set
	 */
	public static void setOptionsRenderingAntialiasing(int optionsRenderingAntialiasing) {
		RobocodeProperties.optionsRenderingAntialiasing = optionsRenderingAntialiasing;
		instance.setProperty(OPTIONS_RENDERING_ANTIALIASING, "" + optionsRenderingAntialiasing);

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
	public static int getOptionsRenderingTextAntialiasing() {
		return optionsRenderingTextAntialiasing;
	}

	/**
	 * Sets the optionsRenderingTextAntialiasing.
	 * 
	 * @param optionsRenderingTextAntialiasing The optionsRenderingTextAntialiasing to set
	 */
	public static void setOptionsRenderingTextAntialiasing(int optionsRenderingTextAntialiasing) {
		RobocodeProperties.optionsRenderingTextAntialiasing = optionsRenderingTextAntialiasing;
		instance.setProperty(OPTIONS_RENDERING_TEXT_ANTIALIASING, "" + optionsRenderingTextAntialiasing);

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
	public static int getOptionsRenderingMethod() {
		return optionsRenderingMethod;
	}

	/**
	 * Sets the optionsRenderingMethod.
	 * 
	 * @param optionsRenderingMethod The optionsRenderingMethod to set
	 */
	public static void setOptionsRenderingMethod(int optionsRenderingMethod) {
		RobocodeProperties.optionsRenderingMethod = optionsRenderingMethod;
		instance.setProperty(OPTIONS_RENDERING_METHOD, "" + optionsRenderingMethod);

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
	public static RenderingHints getRenderingHints() {
		return renderingHints;
	}

	/**
	 * Gets the optionsRenderingNoBuffers
	 * 
	 * @return Returns an int
	 */
	public static int getOptionsRenderingNoBuffers() {
		return optionsRenderingNoBuffers;
	}

	/**
	 * Sets the optionsRenderingNoBuffers.
	 * 
	 * @param optionsRenderingNoBuffers The optionsRenderingNoBuffers to set
	 */
	public static void setOptionsRenderingNoBuffers(int optionsRenderingNoBuffers) {
		RobocodeProperties.optionsRenderingNoBuffers = optionsRenderingNoBuffers;
		instance.setProperty(OPTIONS_RENDERING_NO_BUFFERS, "" + optionsRenderingNoBuffers);
	}

	/**
	 * Gets the optionsBattleDesiredTPS.
	 * 
	 * @return Returns a int
	 */
	public static int getOptionsBattleDesiredTPS() {
		return optionsBattleDesiredTPS;
	}

	/**
	 * Sets the optionsBattleDesiredTPS.
	 * 
	 * @param optionsBattleDesiredTPS The optionsBattleDesiredTPS to set
	 */
	public static void setOptionsBattleDesiredTPS(int optionsBattleDesiredTPS) {
		RobocodeProperties.optionsBattleDesiredTPS = optionsBattleDesiredTPS;
		instance.setProperty(OPTIONS_BATTLE_DESIREDTPS, "" + optionsBattleDesiredTPS);
	}

	public static boolean getOptionsBattleAllowColorChanges() {
		return optionsBattleAllowColorChanges;
	}
	
	public static void setOptionsBattleAllowColorChanges(boolean optionsBattleAllowColorChanges) {
		RobocodeProperties.optionsBattleAllowColorChanges = optionsBattleAllowColorChanges;
		instance.setProperty(OPTIONS_BATTLE_ALLOWCOLORCHANGES, "" + optionsBattleAllowColorChanges);
	}
	
	/**
	 * Gets the optionsSoundEnableSound
	 * 
	 * @return Returns a boolean
	 */
	public static boolean getOptionsSoundEnableSound() {
		return optionsSoundEnableSound;
	}

	/**
	 * Sets the optionsSoundEnableSound.
	 * 
	 * @param optionsSoundEnableSound The optionsSoundEnableSound to set
	 */
	public static void setOptionsSoundEnableSound(boolean optionsSoundEnableSound) {
		RobocodeProperties.optionsSoundEnableSound = optionsSoundEnableSound;
		instance.setProperty(OPTIONS_SOUND_ENABLESOUND, "" + optionsSoundEnableSound);
	}

	/**
	 * Gets the optionsSoundEnableGunShot
	 * 
	 * @return Returns a boolean
	 */
	public static boolean getOptionsSoundEnableGunShot() {
		return optionsSoundEnableGunShot;
	}

	/**
	 * Sets the optionsSoundEnableGunShot.
	 * 
	 * @param optionsSoundEnableGunShot The optionsSoundEnableGunShot to set
	 */
	public static void setOptionsSoundEnableGunShot(boolean optionsSoundEnableGunShot) {
		RobocodeProperties.optionsSoundEnableGunShot = optionsSoundEnableGunShot;
		instance.setProperty(OPTIONS_SOUND_ENABLEGUNSHOT, "" + optionsSoundEnableGunShot);
	}

	/**
	 * Gets the optionsSoundEnableBulletHit
	 * 
	 * @return Returns a boolean
	 */
	public static boolean getOptionsSoundEnableBulletHit() {
		return optionsSoundEnableBulletHit;
	}

	/**
	 * Sets the optionsSoundEnableBulletHit.
	 * 
	 * @param optionsSoundEnableBulletHit The optionsSoundEnableBulletHit to set
	 */
	public static void setOptionsSoundEnableBulletHit(boolean optionsSoundEnableBulletHit) {
		RobocodeProperties.optionsSoundEnableBulletHit = optionsSoundEnableBulletHit;
		instance.setProperty(OPTIONS_SOUND_ENABLEBULLETHIT, "" + optionsSoundEnableBulletHit);
	}

	/**
	 * Gets the optionsSoundEnableRobotDeath
	 * 
	 * @return Returns a boolean
	 */
	public static boolean getOptionsSoundEnableRobotDeath() {
		return optionsSoundEnableRobotDeath;
	}

	/**
	 * Sets the optionsSoundEnableRobotDeath.
	 * 
	 * @param optionsSoundEnableRobotDeath The optionsSoundEnableRobotDeath to set
	 */
	public static void setOptionsSoundEnableRobotDeath(boolean optionsSoundEnableRobotDeath) {
		RobocodeProperties.optionsSoundEnableRobotDeath = optionsSoundEnableRobotDeath;
		instance.setProperty(OPTIONS_SOUND_ENABLEROBOTDEATH, "" + optionsSoundEnableRobotDeath);
	}

	/**
	 * Gets the optionsSoundEnableWallCollision
	 * 
	 * @return Returns a boolean
	 */
	public static boolean getOptionsSoundEnableWallCollision() {
		return optionsSoundEnableWallCollision;
	}

	/**
	 * Sets the optionsSoundEnableWallCollision.
	 * 
	 * @param optionsSoundEnableWallCollision The optionsSoundEnableWallCollision to set
	 */
	public static void setOptionsSoundEnableWallCollision(boolean optionsSoundEnableWallCollision) {
		RobocodeProperties.optionsSoundEnableWallCollision = optionsSoundEnableWallCollision;
		instance.setProperty(OPTIONS_SOUND_ENABLEWALLCOLLISION, "" + optionsSoundEnableWallCollision);
	}

	/**
	 * Gets the optionsSoundEnableRobotCollision
	 * 
	 * @return Returns a boolean
	 */
	public static boolean getOptionsSoundEnableRobotCollision() {
		return optionsSoundEnableRobotCollision;
	}

	/**
	 * Sets the optionsSoundEnableRobotCollision.
	 * 
	 * @param optionsSoundEnableRobotCollision The optionsSoundEnableRobotCollision to set
	 */
	public static void setOptionsSoundEnableRobotCollision(boolean optionsSoundEnableRobotCollision) {
		RobocodeProperties.optionsSoundEnableRobotCollision = optionsSoundEnableRobotCollision;
		instance.setProperty(OPTIONS_SOUND_ENABLEROBOTCOLLISION, "" + optionsSoundEnableRobotCollision);
	}

	/**
	 * Gets the optionsSoundEnableMixerVolume
	 * 
	 * @return Returns a boolean
	 */
	public static boolean getOptionsSoundEnableMixerVolume() {
		return optionsSoundEnableMixerVolume;
	}

	/**
	 * Sets the optionsSoundMixer
	 * 
	 * @param optionsSoundMixer The optionsSoundMixer to set
	 */
	public static void setOptionsSoundMixer(String optionsSoundMixer) {
		RobocodeProperties.optionsSoundMixer = optionsSoundMixer;
		instance.setProperty(OPTIONS_SOUND_MIXER, optionsSoundMixer);
	}

	/**
	 * Gets the optionsSoundMixer
	 * 
	 * @return Returns a String
	 */
	public static String getOptionsSoundMixer() {
		return optionsSoundMixer;
	}

	/**
	 * Sets the optionsSoundEnableMixerVolume.
	 * 
	 * @param optionsSoundEnableMixerVolume The optionsSoundEnableMixerVolume to set
	 */
	public static void setOptionsSoundEnableMixerVolume(boolean optionsSoundEnableMixerVolume) {
		RobocodeProperties.optionsSoundEnableMixerVolume = optionsSoundEnableMixerVolume;
		instance.setProperty(OPTIONS_SOUND_ENABLEMIXERVOLUME, "" + optionsSoundEnableMixerVolume);
	}

	/**
	 * Gets the optionsSoundEnableMixerPan
	 * 
	 * @return Returns a boolean
	 */
	public static boolean getOptionsSoundEnableMixerPan() {
		return optionsSoundEnableMixerPan;
	}

	/**
	 * Sets the optionsSoundEnableMixerPan.
	 * 
	 * @param optionsSoundEnableMixerPan The optionsSoundEnableMixerPan to set
	 */
	public static void setOptionsSoundEnableMixerPan(boolean optionsSoundEnableMixerPan) {
		RobocodeProperties.optionsSoundEnableMixerPan = optionsSoundEnableMixerPan;
		instance.setProperty(OPTIONS_SOUND_ENABLEMIXERPAN, "" + optionsSoundEnableMixerPan);
	}

	public static boolean getOptionsTeamShowTeamRobots() {
		return optionsTeamShowTeamRobots;
	}
	
	public static void setOptionsTeamShowTeamRobots(boolean optionsTeamShowTeamRobots) {
		RobocodeProperties.optionsTeamShowTeamRobots = optionsTeamShowTeamRobots;
		instance.setProperty(OPTIONS_TEAM_SHOWTEAMROBOTS, "" + optionsTeamShowTeamRobots);
	}
	
	/**
	 * Gets the versionChecked.
	 * 
	 * @return Returns a String
	 */
	public static Date getVersionChecked() {
		return versionChecked;
	}

	/**
	 * Sets the versionChecked.
	 * 
	 * @param versionChecked The versionChecked to set
	 */
	public static void setVersionChecked(Date versionChecked) {
		RobocodeProperties.versionChecked = versionChecked;
		instance.setProperty(VERSIONCHECKED, DATE_FORMAT.format(new Date()));
	}

	/**
	 * Gets the robotFilesystemQuota.
	 * 
	 * @return Returns a long
	 */
	public static long getRobotFilesystemQuota() {
		return robotFilesystemQuota;
	}

	/**
	 * Sets the robotFilesystemQuota.
	 * 
	 * @param robotFilesystemQuota The robotFilesystemQuota to set
	 */
	public static void setRobotFilesystemQuota(long robotFilesystemQuota) {
		RobocodeProperties.robotFilesystemQuota = robotFilesystemQuota;
		instance.setProperty(ROBOT_FILESYSTEM_QUOTA, "" + robotFilesystemQuota);
	}

	/**
	 * Gets the consoleQuota.
	 * 
	 * @return Returns a long
	 */
	public static long getConsoleQuota() {
		return consoleQuota;
	}

	/**
	 * Sets the consoleQuota.
	 * 
	 * @param consoleQuota The consoleQuota to set
	 */
	public static void setConsoleQuota(long consoleQuota) {
		RobocodeProperties.consoleQuota = consoleQuota;
		instance.setProperty(CONSOLE_QUOTA, "" + consoleQuota);
	}

	/**
	 * Gets the cpuConstant.
	 * 
	 * @return Returns a int
	 */
	public static int getCpuConstant() {
		return cpuConstant;
	}

	/**
	 * Sets the cpuConstant.
	 * 
	 * @param cpuConstant The cpuConstant to set
	 */
	public static void setCpuConstant(int cpuConstant) {
		RobocodeProperties.cpuConstant = cpuConstant;
		instance.setProperty(CPU_CONSTANT, "" + cpuConstant);
	}

	/**
	 * Gets the optionsDevelopmentPath
	 * 
	 * @return Returns a String
	 */
	public static String getOptionsDevelopmentPath() {
		return optionsDevelopmentPath;
	}

	/**
	 * Sets the optionsDevelopmentPath.
	 * 
	 * @param optionsDevelopmentPath The optionsDevelopmentPath to set
	 */
	public static void setOptionsDevelopmentPath(String optionsDevelopmentPath) {
		RobocodeProperties.optionsDevelopmentPath = optionsDevelopmentPath;
		instance.setProperty(OPTIONS_DEVELOPMENT_PATH, optionsDevelopmentPath);
	}

	/**
	 * Gets the optionsCommonShowResults
	 * 
	 * @return Returns a boolean
	 */
	public static boolean getOptionsCommonShowResults() {
		return optionsCommonShowResults;
	}

	/**
	 * Sets the optionsCommonAppendWhenSavingResults.
	 * 
	 * @param optionsCommonAppendWhenSavingResults The optionsCommonAppendWhenSavingResults to set
	 */
	public static void setOptionsCommonAppendWhenSavingResults(boolean optionsCommonAppendWhenSavingResults) {
		RobocodeProperties.optionsCommonAppendWhenSavingResults = optionsCommonAppendWhenSavingResults;
		instance.setProperty(OPTIONS_COMMON_APPEND_WHEN_SAVING_RESULTS, "" + optionsCommonAppendWhenSavingResults);
	}

	/**
	 * Gets the optionsCommonAppendWhenSavingResults
	 * 
	 * @return Returns a boolean
	 */
	public static boolean getOptionsCommonAppendWhenSavingResults() {
		return optionsCommonAppendWhenSavingResults;
	}

	/**
	 * Sets the optionsCommonShowResults.
	 * 
	 * @param optionsCommonShowResults The optionsCommonShowResults to set
	 */
	public static void setOptionsCommonShowResults(boolean optionsCommonShowResults) {
		RobocodeProperties.optionsCommonShowResults = optionsCommonShowResults;
		instance.setProperty(OPTIONS_COMMON_SHOW_RESULTS, "" + optionsCommonShowResults);
	}

	public static void store(FileOutputStream out, String desc) throws IOException {
		instance.store(out, desc);
	}

	public static void save() {
		BattleManager.setOptions();
		try {
			FileOutputStream out = new FileOutputStream(new File(Constants.cwd(), "robocode.properties"));

			instance.store(out, "Robocode Properties");
		} catch (IOException e) {
			Utils.log(e);
		}
	}

	private static void load() throws IOException {
		instance.load(new FileInputStream(new File(Constants.cwd(), "robocode.properties")));

		optionsViewRobotNames = Boolean.valueOf(instance.getProperty(OPTIONS_VIEW_ROBOTNAMES, "true")).booleanValue();
		optionsViewScanArcs = Boolean.valueOf(instance.getProperty(OPTIONS_VIEW_SCANARCS, "false")).booleanValue();
		optionsViewRobotEnergy = Boolean.valueOf(instance.getProperty(OPTIONS_VIEW_ROBOTENERGY, "true")).booleanValue();
		optionsViewGround = Boolean.valueOf(instance.getProperty(OPTIONS_VIEW_GROUND, "true")).booleanValue();
		optionsViewTPS = Boolean.valueOf(instance.getProperty(OPTIONS_VIEW_TPS, "true")).booleanValue();
		optionsViewFPS = Boolean.valueOf(instance.getProperty(OPTIONS_VIEW_FPS, "true")).booleanValue();
		optionsViewExplosions = Boolean.valueOf(instance.getProperty(OPTIONS_VIEW_EXPLOSIONS, "true")).booleanValue();
		optionsBattleDesiredTPS = Integer.parseInt(instance.getProperty(OPTIONS_BATTLE_DESIREDTPS, "30"));
		optionsBattleAllowColorChanges = Boolean.valueOf(instance.getProperty(OPTIONS_BATTLE_ALLOWCOLORCHANGES, "false")).booleanValue();

		optionsRenderingAntialiasing = Integer.parseInt(instance.getProperty(OPTIONS_RENDERING_ANTIALIASING, "0"));
		optionsRenderingTextAntialiasing = Integer.parseInt(
				instance.getProperty(OPTIONS_RENDERING_TEXT_ANTIALIASING, "0"));
		optionsRenderingMethod = Integer.parseInt(instance.getProperty(OPTIONS_RENDERING_METHOD, "0"));
		optionsRenderingNoBuffers = Integer.parseInt(instance.getProperty(OPTIONS_RENDERING_NO_BUFFERS, "2"));

		optionsSoundEnableSound = Boolean.valueOf(instance.getProperty(OPTIONS_SOUND_ENABLESOUND, "false")).booleanValue();
		optionsSoundEnableGunShot = Boolean.valueOf(instance.getProperty(OPTIONS_SOUND_ENABLEGUNSHOT, "true")).booleanValue();
		optionsSoundEnableBulletHit = Boolean.valueOf(instance.getProperty(OPTIONS_SOUND_ENABLEBULLETHIT, "true")).booleanValue();
		optionsSoundEnableRobotDeath = Boolean.valueOf(instance.getProperty(OPTIONS_SOUND_ENABLEROBOTDEATH, "true")).booleanValue();
		optionsSoundEnableRobotCollision = Boolean.valueOf(instance.getProperty(OPTIONS_SOUND_ENABLEROBOTCOLLISION, "true")).booleanValue();
		optionsSoundEnableWallCollision = Boolean.valueOf(instance.getProperty(OPTIONS_SOUND_ENABLEWALLCOLLISION, "true")).booleanValue();

		optionsSoundMixer = instance.getProperty(OPTIONS_SOUND_MIXER, "DirectAudioDevice");
		optionsSoundEnableMixerVolume = Boolean.valueOf(instance.getProperty(OPTIONS_SOUND_ENABLEMIXERVOLUME, "true")).booleanValue();
		optionsSoundEnableMixerPan = Boolean.valueOf(instance.getProperty(OPTIONS_SOUND_ENABLEMIXERPAN, "true")).booleanValue();

		optionsDevelopmentPath = instance.getProperty(OPTIONS_DEVELOPMENT_PATH, "");

		optionsCommonShowResults = Boolean.valueOf(instance.getProperty(OPTIONS_COMMON_SHOW_RESULTS, "true")).booleanValue();
		optionsCommonAppendWhenSavingResults = Boolean.valueOf(instance.getProperty(OPTIONS_COMMON_APPEND_WHEN_SAVING_RESULTS, "true")).booleanValue();

		optionsTeamShowTeamRobots = Boolean.valueOf(instance.getProperty(OPTIONS_TEAM_SHOWTEAMROBOTS, "false")).booleanValue();
		lastRunVersion = instance.getProperty(LAST_RUN_VERSION, "");
		
		try {
			versionChecked = DATE_FORMAT.parse(instance.getProperty(VERSIONCHECKED));
		} catch (Exception e) {
			Utils.log("Initializing version check date to current time");
			versionChecked = new Date();
		}

		robotFilesystemQuota = Long.parseLong(instance.getProperty(ROBOT_FILESYSTEM_QUOTA, "" + 200000));
		consoleQuota = Long.parseLong(instance.getProperty(CONSOLE_QUOTA, "8192"));
		cpuConstant = Integer.parseInt(instance.getProperty(CPU_CONSTANT, "-1"));
	}

	public static String getLastRunVersion() {
		return lastRunVersion;
	}

	/**
	 * Sets the cpuConstant.
	 * 
	 * @param cpuConstant The cpuConstant to set
	 */
	public static void setLastRunVersion(String lastRunVersion) {
		RobocodeProperties.lastRunVersion = lastRunVersion;
		instance.setProperty(LAST_RUN_VERSION, "" + lastRunVersion);
	}
}
