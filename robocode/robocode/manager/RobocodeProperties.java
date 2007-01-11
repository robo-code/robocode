/*******************************************************************************
 * Copyright (c) 2001, 2007 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
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
 *     - Added PropertyListener allowing listeners to retrieve events when a
 *       property is changed
 *     - Removed "Allow color changes" option as this is always possible with
 *       the current rendering engine
 *     - Added common option for enabling replay recording
 *******************************************************************************/
package robocode.manager;


import java.io.*;
import java.util.*;
import java.text.*;
import java.awt.RenderingHints;

import robocode.util.Utils;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public class RobocodeProperties {
	
	private Properties props = new Properties();
	
	// View Options (Arena)
	private boolean optionsViewRobotEnergy = true;
	private boolean optionsViewRobotNames = true;
	private boolean optionsViewScanArcs = false;
	private boolean optionsViewExplosions = true;
	private boolean optionsViewGround = true;

	// View Options (Turns Per Second)
	private boolean optionsViewTPS = true;
	private boolean optionsViewFPS = true;

	// Rendering Options
	private int optionsRenderingAntialiasing = 0; // 0 = default, 1 = on, 2 = off 
	private int optionsRenderingTextAntialiasing = 0; // 0 = default, 1 = on, 2 = off
	private int optionsRenderingMethod = 0; // 0 = default, 1 = speed, 2 = quality
	private int optionsRenderingNoBuffers = 2; // 1 = single buffering, 2 = double buffering, 3 = tripple buffering 
	private int optionsBattleDesiredTPS = 30;

	// Sound Options (Sound Effects)
	private boolean optionsSoundEnableSound = false;
	private boolean optionsSoundEnableGunShot = true;
	private boolean optionsSoundEnableBulletHit = true;
	private boolean optionsSoundEnableRobotDeath = true;
	private boolean optionsSoundEnableWallCollision = true;
	private boolean optionsSoundEnableRobotCollision = true;

	// Sound Options (Mixer)
	private String optionsSoundMixer = "DirectAudioDevice";
	private boolean optionsSoundEnableMixerVolume = true;
	private boolean optionsSoundEnableMixerPan = true;
	
	// Development Options
	private String optionsDevelopmentPath = "";

	// Common Options
	private boolean optionsCommonShowResults = true;
	private boolean optionsCommonAppendWhenSavingResults = true; 
	private boolean optionsCommonEnableReplayRecording = true;

	private boolean optionsTeamShowTeamRobots = false;
	
	private String lastRunVersion = "";
	private Date versionChecked;
	private long robotFilesystemQuota = 200000;
	private long consoleQuota = 8192;
	private int cpuConstant = 200;

	private SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy H:mm:ss");
	
	private final static String
	
			OPTIONS_VIEW_ROBOTNAMES = "robocode.options.view.robotNames",
			OPTIONS_VIEW_SCANARCS = "robocode.options.view.scanArcs",
			OPTIONS_VIEW_ROBOTENERGY = "robocode.options.view.robotEnergy",
			OPTIONS_VIEW_GROUND = "robocode.options.view.ground",
			OPTIONS_VIEW_TPS = "robocode.options.view.TPS",
			OPTIONS_VIEW_FPS = "robocode.options.view.FPS",
			OPTIONS_VIEW_EXPLOSIONS = "robocode.options.view.explosions",
			OPTIONS_BATTLE_DESIREDTPS = "robocode.options.battle.desiredTPS",

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
			OPTIONS_COMMON_ENABLE_REPLAY_RECORDING = "robocode.options.common.enableReplayRecording",

			OPTIONS_TEAM_SHOWTEAMROBOTS = "robocode.options.team.showTeamRobots",
			OPTIONS_DEVELOPMENT_PATH = "robocode.options.development.path",
			VERSIONCHECKED = "robocode.versionchecked",
			ROBOT_FILESYSTEM_QUOTA = "robocode.robot.filesystem.quota",
			CONSOLE_QUOTA = "robocode.console.quota",
			CPU_CONSTANT = "robocode.cpu.constant.1000",
			LAST_RUN_VERSION = "robocode.version.lastrun";

	private RobocodeManager manager;

	private RenderingHints renderingHints = new RenderingHints(new HashMap<RenderingHints.Key, Object>());

	private List<PropertyListener> listeners = new ArrayList<PropertyListener>();

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
	 * Gets the optionsViewTPS.
	 * 
	 * @return Returns a boolean
	 */
	public boolean getOptionsViewTPS() {
		return optionsViewTPS;
	}

	/**
	 * Sets the optionsViewTPS.
	 * 
	 * @param optionsViewTPS The optionsViewTPS to set
	 */
	public void setOptionsViewTPS(boolean optionsViewTPS) {
		this.optionsViewTPS = optionsViewTPS;
		props.setProperty(OPTIONS_VIEW_TPS, "" + optionsViewTPS);
	}

	/**
	 * Gets the optionsViewFPS.
	 * 
	 * @return Returns a boolean
	 */
	public boolean getOptionsViewFPS() {
		return optionsViewFPS;
	}

	/**
	 * Sets the optionsViewFPS.
	 * 
	 * @param optionsViewFPS The optionsViewFPS to set
	 */
	public void setOptionsViewFPS(boolean optionsViewFPS) {
		this.optionsViewFPS = optionsViewFPS;
		props.setProperty(OPTIONS_VIEW_FPS, "" + optionsViewFPS);
	}

	/**
	 * Gets the optionsViewExplosions.
	 * 
	 * @return Returns a boolean
	 */
	public boolean getOptionsViewExplosions() {
		return optionsViewExplosions;
	}

	/**
	 * Sets the optionsViewExplosions.
	 * 
	 * @param optionsViewExplosions The optionsViewExplosions to set
	 */
	public void setOptionsViewExplosions(boolean optionsViewExplosions) {
		this.optionsViewExplosions = optionsViewExplosions;
		props.setProperty(OPTIONS_VIEW_EXPLOSIONS, "" + optionsViewExplosions);
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
	 * Gets the optionsBattleDesiredTPS.
	 * 
	 * @return Returns a int
	 */
	public int getOptionsBattleDesiredTPS() {
		return optionsBattleDesiredTPS;
	}

	/**
	 * Sets the optionsBattleDesiredTPS.
	 * 
	 * @param optionsBattleDesiredTPS The optionsBattleDesiredTPS to set
	 */
	public void setOptionsBattleDesiredTPS(int optionsBattleDesiredTPS) {
		this.optionsBattleDesiredTPS = optionsBattleDesiredTPS;
		props.setProperty(OPTIONS_BATTLE_DESIREDTPS, "" + optionsBattleDesiredTPS);
		
		notifyDesiredTpsChanged();
	}

	/**
	 * Gets the optionsSoundEnableSound
	 * 
	 * @return Returns a boolean
	 */
	public boolean getOptionsSoundEnableSound() {
		return optionsSoundEnableSound;
	}

	/**
	 * Sets the optionsSoundEnableSound.
	 * 
	 * @param optionsSoundEnableSound The optionsSoundEnableSound to set
	 */
	public void setOptionsSoundEnableSound(boolean optionsSoundEnableSound) {
		this.optionsSoundEnableSound = optionsSoundEnableSound;
		props.setProperty(OPTIONS_SOUND_ENABLESOUND, "" + optionsSoundEnableSound);
	}

	/**
	 * Gets the optionsSoundEnableGunShot
	 * 
	 * @return Returns a boolean
	 */
	public boolean getOptionsSoundEnableGunShot() {
		return optionsSoundEnableGunShot;
	}

	/**
	 * Sets the optionsSoundEnableGunShot.
	 * 
	 * @param optionsSoundEnableGunShot The optionsSoundEnableGunShot to set
	 */
	public void setOptionsSoundEnableGunShot(boolean optionsSoundEnableGunShot) {
		this.optionsSoundEnableGunShot = optionsSoundEnableGunShot;
		props.setProperty(OPTIONS_SOUND_ENABLEGUNSHOT, "" + optionsSoundEnableGunShot);
	}

	/**
	 * Gets the optionsSoundEnableBulletHit
	 * 
	 * @return Returns a boolean
	 */
	public boolean getOptionsSoundEnableBulletHit() {
		return optionsSoundEnableBulletHit;
	}

	/**
	 * Sets the optionsSoundEnableBulletHit.
	 * 
	 * @param optionsSoundEnableBulletHit The optionsSoundEnableBulletHit to set
	 */
	public void setOptionsSoundEnableBulletHit(boolean optionsSoundEnableBulletHit) {
		this.optionsSoundEnableBulletHit = optionsSoundEnableBulletHit;
		props.setProperty(OPTIONS_SOUND_ENABLEBULLETHIT, "" + optionsSoundEnableBulletHit);
	}

	/**
	 * Gets the optionsSoundEnableRobotDeath
	 * 
	 * @return Returns a boolean
	 */
	public boolean getOptionsSoundEnableRobotDeath() {
		return optionsSoundEnableRobotDeath;
	}

	/**
	 * Sets the optionsSoundEnableRobotDeath.
	 * 
	 * @param optionsSoundEnableRobotDeath The optionsSoundEnableRobotDeath to set
	 */
	public void setOptionsSoundEnableRobotDeath(boolean optionsSoundEnableRobotDeath) {
		this.optionsSoundEnableRobotDeath = optionsSoundEnableRobotDeath;
		props.setProperty(OPTIONS_SOUND_ENABLEROBOTDEATH, "" + optionsSoundEnableRobotDeath);
	}

	/**
	 * Gets the optionsSoundEnableWallCollision
	 * 
	 * @return Returns a boolean
	 */
	public boolean getOptionsSoundEnableWallCollision() {
		return optionsSoundEnableWallCollision;
	}

	/**
	 * Sets the optionsSoundEnableWallCollision.
	 * 
	 * @param optionsSoundEnableWallCollision The optionsSoundEnableWallCollision to set
	 */
	public void setOptionsSoundEnableWallCollision(boolean optionsSoundEnableWallCollision) {
		this.optionsSoundEnableWallCollision = optionsSoundEnableWallCollision;
		props.setProperty(OPTIONS_SOUND_ENABLEWALLCOLLISION, "" + optionsSoundEnableWallCollision);
	}

	/**
	 * Gets the optionsSoundEnableRobotCollision
	 * 
	 * @return Returns a boolean
	 */
	public boolean getOptionsSoundEnableRobotCollision() {
		return optionsSoundEnableRobotCollision;
	}

	/**
	 * Sets the optionsSoundEnableRobotCollision.
	 * 
	 * @param optionsSoundEnableRobotCollision The optionsSoundEnableRobotCollision to set
	 */
	public void setOptionsSoundEnableRobotCollision(boolean optionsSoundEnableRobotCollision) {
		this.optionsSoundEnableRobotCollision = optionsSoundEnableRobotCollision;
		props.setProperty(OPTIONS_SOUND_ENABLEROBOTCOLLISION, "" + optionsSoundEnableRobotCollision);
	}

	/**
	 * Gets the optionsSoundEnableMixerVolume
	 * 
	 * @return Returns a boolean
	 */
	public boolean getOptionsSoundEnableMixerVolume() {
		return optionsSoundEnableMixerVolume;
	}

	/**
	 * Sets the optionsSoundMixer
	 * 
	 * @param optionsSoundMixer The optionsSoundMixer to set
	 */
	public void setOptionsSoundMixer(String optionsSoundMixer) {
		this.optionsSoundMixer = optionsSoundMixer;
		props.setProperty(OPTIONS_SOUND_MIXER, optionsSoundMixer);
	}

	/**
	 * Gets the optionsSoundMixer
	 * 
	 * @return Returns a String
	 */
	public String getOptionsSoundMixer() {
		return optionsSoundMixer;
	}

	/**
	 * Sets the optionsSoundEnableMixerVolume.
	 * 
	 * @param optionsSoundEnableMixerVolume The optionsSoundEnableMixerVolume to set
	 */
	public void setOptionsSoundEnableMixerVolume(boolean optionsSoundEnableMixerVolume) {
		this.optionsSoundEnableMixerVolume = optionsSoundEnableMixerVolume;
		props.setProperty(OPTIONS_SOUND_ENABLEMIXERVOLUME, "" + optionsSoundEnableMixerVolume);
	}

	/**
	 * Gets the optionsSoundEnableMixerPan
	 * 
	 * @return Returns a boolean
	 */
	public boolean getOptionsSoundEnableMixerPan() {
		return optionsSoundEnableMixerPan;
	}

	/**
	 * Sets the optionsSoundEnableMixerPan.
	 * 
	 * @param optionsSoundEnableMixerPan The optionsSoundEnableMixerPan to set
	 */
	public void setOptionsSoundEnableMixerPan(boolean optionsSoundEnableMixerPan) {
		this.optionsSoundEnableMixerPan = optionsSoundEnableMixerPan;
		props.setProperty(OPTIONS_SOUND_ENABLEMIXERPAN, "" + optionsSoundEnableMixerPan);
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

	/**
	 * Gets the optionsCommonShowResults
	 * 
	 * @return Returns a boolean
	 */
	public boolean getOptionsCommonShowResults() {
		return optionsCommonShowResults;
	}

	/**
	 * Sets the optionsCommonAppendWhenSavingResults.
	 * 
	 * @param enable The optionsCommonAppendWhenSavingResults to set
	 */
	public void setOptionsCommonAppendWhenSavingResults(boolean enable) {
		this.optionsCommonAppendWhenSavingResults = enable;
		props.setProperty(OPTIONS_COMMON_APPEND_WHEN_SAVING_RESULTS, "" + enable);
	}

	/**
	 * Gets the optionsCommonAppendWhenSavingResults
	 * 
	 * @return Returns a boolean
	 */
	public boolean getOptionsCommonAppendWhenSavingResults() {
		return optionsCommonAppendWhenSavingResults;
	}

	/**
	 * Sets the optionsCommonShowResults.
	 * 
	 * @param enable The optionsCommonShowResults to set
	 */
	public void setOptionsCommonShowResults(boolean enable) {
		this.optionsCommonShowResults = enable;
		props.setProperty(OPTIONS_COMMON_SHOW_RESULTS, "" + enable);
	}

	/**
	 * Gets the optionsCommonEnableReplayRecording
	 * 
	 * @return Returns a boolean
	 */
	public boolean getOptionsCommonEnableReplayRecording() {
		return optionsCommonEnableReplayRecording;
	}

	/**
	 * Sets the optionsCommonEnableReplayRecording.
	 * 
	 * @param enable The optionsCommonEnableReplayRecording to set
	 */
	public void setOptionsCommonEnableReplayRecording(boolean enable) {
		this.optionsCommonEnableReplayRecording = enable;
		props.setProperty(OPTIONS_COMMON_ENABLE_REPLAY_RECORDING, "" + enable);

		notifyReplayRecordingChanged();
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
		optionsViewTPS = Boolean.valueOf(props.getProperty(OPTIONS_VIEW_TPS, "true")).booleanValue();
		optionsViewFPS = Boolean.valueOf(props.getProperty(OPTIONS_VIEW_FPS, "true")).booleanValue();
		optionsViewExplosions = Boolean.valueOf(props.getProperty(OPTIONS_VIEW_EXPLOSIONS, "true")).booleanValue();
		optionsBattleDesiredTPS = Integer.parseInt(props.getProperty(OPTIONS_BATTLE_DESIREDTPS, "30"));

		optionsRenderingAntialiasing = Integer.parseInt(props.getProperty(OPTIONS_RENDERING_ANTIALIASING, "0"));
		optionsRenderingTextAntialiasing = Integer.parseInt(props.getProperty(OPTIONS_RENDERING_TEXT_ANTIALIASING, "0"));
		optionsRenderingMethod = Integer.parseInt(props.getProperty(OPTIONS_RENDERING_METHOD, "0"));
		optionsRenderingNoBuffers = Integer.parseInt(props.getProperty(OPTIONS_RENDERING_NO_BUFFERS, "2"));

		optionsSoundEnableSound = Boolean.valueOf(props.getProperty(OPTIONS_SOUND_ENABLESOUND, "false")).booleanValue();
		optionsSoundEnableGunShot = Boolean.valueOf(props.getProperty(OPTIONS_SOUND_ENABLEGUNSHOT, "true")).booleanValue();
		optionsSoundEnableBulletHit = Boolean.valueOf(props.getProperty(OPTIONS_SOUND_ENABLEBULLETHIT, "true")).booleanValue();
		optionsSoundEnableRobotDeath = Boolean.valueOf(props.getProperty(OPTIONS_SOUND_ENABLEROBOTDEATH, "true")).booleanValue();
		optionsSoundEnableRobotCollision = Boolean.valueOf(props.getProperty(OPTIONS_SOUND_ENABLEROBOTCOLLISION, "true")).booleanValue();
		optionsSoundEnableWallCollision = Boolean.valueOf(props.getProperty(OPTIONS_SOUND_ENABLEWALLCOLLISION, "true")).booleanValue();

		optionsSoundMixer = props.getProperty(OPTIONS_SOUND_MIXER, "DirectAudioDevice");
		optionsSoundEnableMixerVolume = Boolean.valueOf(props.getProperty(OPTIONS_SOUND_ENABLEMIXERVOLUME, "true")).booleanValue();
		optionsSoundEnableMixerPan = Boolean.valueOf(props.getProperty(OPTIONS_SOUND_ENABLEMIXERPAN, "true")).booleanValue();

		optionsDevelopmentPath = props.getProperty(OPTIONS_DEVELOPMENT_PATH, "");

		optionsCommonShowResults = Boolean.valueOf(props.getProperty(OPTIONS_COMMON_SHOW_RESULTS, "true")).booleanValue();
		optionsCommonAppendWhenSavingResults = Boolean.valueOf(props.getProperty(OPTIONS_COMMON_APPEND_WHEN_SAVING_RESULTS, "true")).booleanValue();
		optionsCommonEnableReplayRecording = Boolean.valueOf(props.getProperty(OPTIONS_COMMON_ENABLE_REPLAY_RECORDING, "true")).booleanValue();

		optionsTeamShowTeamRobots = Boolean.valueOf(props.getProperty(OPTIONS_TEAM_SHOWTEAMROBOTS, "false")).booleanValue();
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

	public void addPropertyListener(PropertyListener listener) {
		listeners.add(listener);
	}

	private void notifyDesiredTpsChanged() {
		for (PropertyListener listener : listeners) {
			listener.desiredTpsChanged(optionsBattleDesiredTPS);
		}
	}

	private void notifyReplayRecordingChanged() {
		for (PropertyListener listener : listeners) {
			listener.enableReplayRecordingChanged(optionsCommonEnableReplayRecording);
		}
	}

	/**
	 * Property listener.
	 *
	 * @author Flemming N. Larsen
	 */
	public class PropertyListener {
		public void desiredTpsChanged(int tps) {}
		
		public void enableReplayRecordingChanged(boolean enabled) {}
	}
}
