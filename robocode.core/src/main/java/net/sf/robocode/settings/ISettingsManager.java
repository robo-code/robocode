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
package net.sf.robocode.settings;


import java.awt.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;


/**
 * @author Pavel Savara (original)
 */
public interface ISettingsManager {
	void saveProperties();

	/**
	 * Gets the optionsViewRobotNames.
	 *
	 * @return Returns a boolean
	 */
	boolean getOptionsViewRobotNames();

	/**
	 * Sets the optionsViewRobotNames.
	 *
	 * @param optionsViewRobotNames The optionsViewRobotNames to set
	 */
	void setOptionsViewRobotNames(boolean optionsViewRobotNames);

	/**
	 * Gets the optionsViewScanArcs.
	 *
	 * @return Returns a boolean
	 */
	boolean getOptionsViewScanArcs();

	/**
	 * Sets the optionsViewScanArcs.
	 *
	 * @param optionsViewScanArcs The optionsViewScanArcs to set
	 */
	void setOptionsViewScanArcs(boolean optionsViewScanArcs);

	/**
	 * Gets the optionsViewRobotEnergy.
	 *
	 * @return Returns a boolean
	 */
	boolean getOptionsViewRobotEnergy();

	/**
	 * Sets the optionsViewRobotEnergy.
	 *
	 * @param optionsViewRobotEnergy The optionsViewRobotEnergy to set
	 */
	void setOptionsViewRobotEnergy(boolean optionsViewRobotEnergy);

	/**
	 * Gets the optionsViewGround.
	 *
	 * @return Returns a boolean
	 */
	boolean getOptionsViewGround();

	/**
	 * Sets the optionsViewGround.
	 *
	 * @param optionsViewGround The optionsViewGround to set
	 */
	void setOptionsViewGround(boolean optionsViewGround);

	/**
	 * Gets the optionsViewTPS.
	 *
	 * @return Returns a boolean
	 */
	boolean getOptionsViewTPS();

	/**
	 * Sets the optionsViewTPS.
	 *
	 * @param optionsViewTPS The optionsViewTPS to set
	 */
	void setOptionsViewTPS(boolean optionsViewTPS);

	/**
	 * Gets the optionsViewFPS.
	 *
	 * @return Returns a boolean
	 */
	boolean getOptionsViewFPS();

	/**
	 * Sets the optionsViewFPS.
	 *
	 * @param optionsViewFPS The optionsViewFPS to set
	 */
	void setOptionsViewFPS(boolean optionsViewFPS);

	/**
	 * Gets the optionsViewExplosions.
	 *
	 * @return Returns a boolean
	 */
	boolean getOptionsViewExplosions();

	/**
	 * Sets the optionsViewExplosions.
	 *
	 * @param optionsViewExplosions The optionsViewExplosions to set
	 */
	void setOptionsViewExplosions(boolean optionsViewExplosions);

	/**
	 * Gets the optionsViewExplosionDebris.
	 *
	 * @return Returns a boolean
	 */
	boolean getOptionsViewExplosionDebris();

	/**
	 * Sets the optionsViewExplosionDebris.
	 *
	 * @param optionsViewExplosionDebris The optionsViewExplosionDebris to set
	 */
	void setOptionsViewExplosionDebris(boolean optionsViewExplosionDebris);

	/**
	 * Gets the optionsRenderingAntialiasing.
	 *
	 * @return Returns an int
	 */
	int getOptionsRenderingAntialiasing();

	/**
	 * Sets the optionsRenderingAntialiasing.
	 *
	 * @param optionsRenderingAntialiasing The optionsRenderingAntialiasing to set
	 */
	void setOptionsRenderingAntialiasing(int optionsRenderingAntialiasing);

	/**
	 * Gets the optionsRenderingTextAntialiasing.
	 *
	 * @return Returns an int
	 */
	int getOptionsRenderingTextAntialiasing();

	/**
	 * Sets the optionsRenderingTextAntialiasing.
	 *
	 * @param optionsRenderingTextAntialiasing
	 *         The optionsRenderingTextAntialiasing to set
	 */
	void setOptionsRenderingTextAntialiasing(int optionsRenderingTextAntialiasing);

	/**
	 * Gets the optionsRenderingMethod.
	 *
	 * @return Returns an int
	 */
	int getOptionsRenderingMethod();

	/**
	 * Sets the optionsRenderingMethod.
	 *
	 * @param optionsRenderingMethod The optionsRenderingMethod to set
	 */
	void setOptionsRenderingMethod(int optionsRenderingMethod);

	/**
	 * Gets the combined rendering options as RenderingHints.
	 *
	 * @return Returns an RenderingHints value
	 */
	RenderingHints getRenderingHints();

	/**
	 * Gets the optionsRenderingNoBuffers
	 *
	 * @return Returns an int
	 */
	int getOptionsRenderingNoBuffers();

	/**
	 * Sets the optionsRenderingNoBuffers.
	 *
	 * @param optionsRenderingNoBuffers The optionsRenderingNoBuffers to set
	 */
	void setOptionsRenderingNoBuffers(int optionsRenderingNoBuffers);

	/**
	 * Gets the optionsRenderingBufferImages
	 *
	 * @return Returns a boolean
	 */
	boolean getOptionsRenderingBufferImages();

	/**
	 * Sets the optionsRenderingBufferImages.
	 *
	 * @param optionsRenderingBufferImages The optionsRenderingBufferImages to set
	 */
	void setOptionsRenderingBufferImages(boolean optionsRenderingBufferImages);

	/**
	 * Gets the optionsRenderingForceBulletColor
	 *
	 * @return Returns a boolean
	 */
	boolean getOptionsRenderingForceBulletColor();

	/**
	 * Sets the optionsRenderingForceBulletColor.
	 *
	 * @param optionsRenderingForceBulletColor
	 *         The optionsRenderingForceBulletColor to set
	 */
	void setOptionsRenderingForceBulletColor(boolean optionsRenderingForceBulletColor);

	/**
	 * Gets the optionsBattleDesiredTPS.
	 *
	 * @return Returns a int
	 */
	int getOptionsBattleDesiredTPS();

	/**
	 * Sets the optionsBattleDesiredTPS.
	 *
	 * @param optionsBattleDesiredTPS The optionsBattleDesiredTPS to set
	 */
	void setOptionsBattleDesiredTPS(int optionsBattleDesiredTPS);

	/**
	 * Gets the optionsSoundEnableSound
	 *
	 * @return Returns a boolean
	 */
	boolean getOptionsSoundEnableSound();

	/**
	 * Sets the optionsSoundEnableSound.
	 *
	 * @param optionsSoundEnableSound The optionsSoundEnableSound to set
	 */
	void setOptionsSoundEnableSound(boolean optionsSoundEnableSound);

	/**
	 * Gets the optionsSoundEnableGunshot
	 *
	 * @return Returns a boolean
	 */
	boolean getOptionsSoundEnableGunshot();

	/**
	 * Sets the optionsSoundEnableGunshot.
	 *
	 * @param optionsSoundEnableGunshot The optionsSoundEnableGunshot to set
	 */
	void setOptionsSoundEnableGunshot(boolean optionsSoundEnableGunshot);

	/**
	 * Gets the optionsSoundEnableBulletHit
	 *
	 * @return Returns a boolean
	 */
	boolean getOptionsSoundEnableBulletHit();

	/**
	 * Sets the optionsSoundEnableBulletHit.
	 *
	 * @param optionsSoundEnableBulletHit The optionsSoundEnableBulletHit to set
	 */
	void setOptionsSoundEnableBulletHit(boolean optionsSoundEnableBulletHit);

	/**
	 * Gets the optionsSoundEnableRobotDeath
	 *
	 * @return Returns a boolean
	 */
	boolean getOptionsSoundEnableRobotDeath();

	/**
	 * Sets the optionsSoundEnableRobotDeath.
	 *
	 * @param optionsSoundEnableRobotDeath The optionsSoundEnableRobotDeath to set
	 */
	void setOptionsSoundEnableRobotDeath(boolean optionsSoundEnableRobotDeath);

	/**
	 * Gets the optionsSoundEnableWallCollision
	 *
	 * @return Returns a boolean
	 */
	boolean getOptionsSoundEnableWallCollision();

	/**
	 * Sets the optionsSoundEnableWallCollision.
	 *
	 * @param optionsSoundEnableWallCollision
	 *         The optionsSoundEnableWallCollision to set
	 */
	void setOptionsSoundEnableWallCollision(boolean optionsSoundEnableWallCollision);

	/**
	 * Gets the optionsSoundEnableRobotCollision
	 *
	 * @return Returns a boolean
	 */
	boolean getOptionsSoundEnableRobotCollision();

	/**
	 * Sets the optionsSoundEnableRobotCollision.
	 *
	 * @param optionsSoundEnableRobotCollision
	 *         The optionsSoundEnableRobotCollision to set
	 */
	void setOptionsSoundEnableRobotCollision(boolean optionsSoundEnableRobotCollision);

	/**
	 * Gets the optionsSoundEnableMixerVolume
	 *
	 * @return Returns a boolean
	 */
	boolean getOptionsSoundEnableMixerVolume();

	/**
	 * Sets the optionsSoundMixer
	 *
	 * @param optionsSoundMixer The optionsSoundMixer to set
	 */
	void setOptionsSoundMixer(String optionsSoundMixer);

	/**
	 * Gets the optionsSoundMixer
	 *
	 * @return Returns a String
	 */
	String getOptionsSoundMixer();

	/**
	 * Sets the optionsSoundEnableMixerVolume.
	 *
	 * @param optionsSoundEnableMixerVolume The optionsSoundEnableMixerVolume to set
	 */
	void setOptionsSoundEnableMixerVolume(boolean optionsSoundEnableMixerVolume);

	/**
	 * Gets the optionsSoundEnableMixerPan
	 *
	 * @return Returns a boolean
	 */
	boolean getOptionsSoundEnableMixerPan();

	/**
	 * Sets the optionsSoundEnableMixerPan.
	 *
	 * @param optionsSoundEnableMixerPan The optionsSoundEnableMixerPan to set
	 */
	void setOptionsSoundEnableMixerPan(boolean optionsSoundEnableMixerPan);

	boolean getOptionsTeamShowTeamRobots();

	void setOptionsTeamShowTeamRobots(boolean optionsTeamShowTeamRobots);

	String getFileThemeMusic();

	String getFileBackgroundMusic();

	String getFileEndOfBattleMusic();

	String getFileGunshotSfx();

	String getBulletHitsRobotSfx();

	String getBulletHitsBulletSfx();

	String getRobotDeathSfx();

	String getRobotCollisionSfx();

	String getWallCollisionSfx();

	/**
	 * Gets the versionChecked.
	 *
	 * @return Returns a String
	 */
	Date getVersionChecked();

	/**
	 * Sets the versionChecked.
	 *
	 * @param versionChecked The versionChecked to set
	 */
	void setVersionChecked(Date versionChecked);

	/**
	 * Gets the robotFilesystemQuota.
	 *
	 * @return Returns a long
	 */
	long getRobotFilesystemQuota();

	/**
	 * Sets the robotFilesystemQuota.
	 *
	 * @param robotFilesystemQuota The robotFilesystemQuota to set
	 */
	void setRobotFilesystemQuota(long robotFilesystemQuota);

	/**
	 * Gets the consoleQuota.
	 *
	 * @return Returns a long
	 */
	long getConsoleQuota();

	/**
	 * Sets the consoleQuota.
	 *
	 * @param consoleQuota The consoleQuota to set
	 */
	void setConsoleQuota(long consoleQuota);

	/**
	 * Gets the cpuConstant.
	 *
	 * @return Returns a long
	 */
	long getCpuConstant();

	/**
	 * Sets the cpuConstant.
	 *
	 * @param cpuConstant The cpuConstant to set
	 */
	void setCpuConstant(long cpuConstant);

	/**
	 * Gets the optionsDevelopmentPath
	 *
	 * @return Returns a String
	 */
	String getOptionsDevelopmentPath();

	/**
	 * Sets the optionsDevelopmentPath.
	 *
	 * @param optionsDevelopmentPath The optionsDevelopmentPath to set
	 */
	void setOptionsDevelopmentPath(String optionsDevelopmentPath);

	/**
	 * Gets the optionsCommonShowResults
	 *
	 * @return Returns a boolean
	 */
	boolean getOptionsCommonShowResults();

	/**
	 * Gets the optionsCommonDontHideRankings
	 *
	 * @return Returns a boolean
	 */
	boolean getOptionsCommonDontHideRankings();

	/**
	 * Sets the optionsCommonDontHideRankings.
	 *
	 * @param dontHide true when the Rankings must not be hidden; false otherwise
	 */
	void setOptionsCommonDontHideRankings(boolean dontHide);

	/**
	 * Sets the optionsCommonAppendWhenSavingResults.
	 *
	 * @param enable The optionsCommonAppendWhenSavingResults to set
	 */
	void setOptionsCommonAppendWhenSavingResults(boolean enable);

	/**
	 * Gets the optionsCommonAppendWhenSavingResults
	 *
	 * @return Returns a boolean
	 */
	boolean getOptionsCommonAppendWhenSavingResults();

	/**
	 * Sets the optionsCommonShowResults.
	 *
	 * @param enable The optionsCommonShowResults to set
	 */
	void setOptionsCommonShowResults(boolean enable);

	/**
	 * Gets the optionsCommonEnableReplayRecording
	 *
	 * @return Returns a boolean
	 */
	boolean getOptionsCommonEnableReplayRecording();

	/**
	 * Sets the optionsCommonEnableReplayRecording.
	 *
	 * @param enable The optionsCommonEnableReplayRecording to set
	 */
	void setOptionsCommonEnableReplayRecording(boolean enable);

	int getNumberOfRounds();

	void setNumberOfRounds(int numberOfRounds);

	void store(FileOutputStream out, String desc) throws IOException;

	void load(FileInputStream in) throws IOException;

	String getLastRunVersion();

	/**
	 * Sets the lastRunVersion.
	 *
	 * @param lastRunVersion The lastRunVersion to set
	 */
	void setLastRunVersion(String lastRunVersion);

	void addPropertyListener(ISettingsListener listener);

	void removePropertyListener(ISettingsListener propertyListener);

	public final static String
			OPTIONS_VIEW_ROBOTNAMES = "robocode.options.view.robotNames",
			OPTIONS_VIEW_SCANARCS = "robocode.options.view.scanArcs",
			OPTIONS_VIEW_ROBOTENERGY = "robocode.options.view.robotEnergy",
			OPTIONS_VIEW_GROUND = "robocode.options.view.ground",
			OPTIONS_VIEW_TPS = "robocode.options.view.TPS",
			OPTIONS_VIEW_FPS = "robocode.options.view.FPS",
			OPTIONS_VIEW_EXPLOSIONS = "robocode.options.view.explosions",
			OPTIONS_VIEW_EXPLOSION_DEBRIS = "robocode.options.view.explosionDebris",

			OPTIONS_BATTLE_DESIREDTPS = "robocode.options.battle.desiredTPS",

			OPTIONS_RENDERING_ANTIALIASING = "robocode.options.rendering.antialiasing",
			OPTIONS_RENDERING_TEXT_ANTIALIASING = "robocode.options.rendering.text.antialiasing",
			OPTIONS_RENDERING_METHOD = "robocode.options.rendering.method",
			OPTIONS_RENDERING_NO_BUFFERS = "robocode.options.rendering.noBuffers",
			OPTIONS_RENDERING_BUFFER_IMAGES = "robocode.options.rendering.bufferImages",
			OPTIONS_RENDERING_FORCE_BULLET_COLOR = "robocode.options.rendering.forceBulletColor",

			OPTIONS_SOUND_ENABLESOUND = "robocode.options.sound.enableSound",
			OPTIONS_SOUND_ENABLEGUNSHOT = "robocode.options.sound.enableGunshot",
			OPTIONS_SOUND_ENABLEBULLETHIT = "robocode.options.sound.enableBulletHit",
			OPTIONS_SOUND_ENABLEROBOTDEATH = "robocode.options.sound.enableRobotDeath",
			OPTIONS_SOUND_ENABLEWALLCOLLISION = "robocode.options.sound.enableWallCollision",
			OPTIONS_SOUND_ENABLEROBOTCOLLISION = "robocode.options.sound.enableRobotCollision",

			OPTIONS_SOUND_MIXER = "robocode.options.sound.mixer",
			OPTIONS_SOUND_ENABLEMIXERVOLUME = "robocode.options.sound.enableMixerVolume",
			OPTIONS_SOUND_ENABLEMIXERPAN = "robocode.options.sound.enableMixerPan",

			OPTIONS_COMMON_SHOW_RESULTS = "robocode.options.common.showResults",
			OPTIONS_COMMON_DONT_HIDE_RANKINGS = "robocode.options.common.dontHideRankings",
			OPTIONS_COMMON_APPEND_WHEN_SAVING_RESULTS = "robocode.options.common.appendWhenSavingResults",
			OPTIONS_COMMON_ENABLE_REPLAY_RECORDING = "robocode.options.common.enableReplayRecording",

			OPTIONS_TEAM_SHOWTEAMROBOTS = "robocode.options.team.showTeamRobots",

			FILE_THEME_MUSIC = "robocode.file.music.theme",
			FILE_BACKGROUND_MUSIC = "robocode.file.music.background",
			FILE_END_OF_BATTLE_MUSIC = "robocode.file.music.endOfBattle",

			FILE_GUNSHOT_SFX = "robocode.file.sfx.gunshot",
			FILE_ROBOT_COLLISION_SFX = "robocode.file.sfx.robotCollision",
			FILE_WALL_COLLISION_SFX = "robocode.file.sfx.wallCollision",
			FILE_ROBOT_DEATH_SFX = "robocode.file.sfx.robotDeath",
			FILE_BULLET_HITS_ROBOT_SFX = "robocode.file.sfx.bulletHitsRobot",
			FILE_BULLET_HITS_BULLET_SFX = "robocode.file.sfx.bulletHitsBullet",

			OPTIONS_DEVELOPMENT_PATH = "robocode.options.development.path",
			VERSIONCHECKED = "robocode.versionchecked",
			ROBOT_FILESYSTEM_QUOTA = "robocode.robot.filesystem.quota",
			CONSOLE_QUOTA = "robocode.console.quota",
			CPU_CONSTANT = "robocode.cpu.constant",
			LAST_RUN_VERSION = "robocode.version.lastrun",

			NUMBER_OF_ROUNDS = "robocode.numberOfBattles";
}
