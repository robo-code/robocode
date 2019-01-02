/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.settings;


import java.awt.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;


/**
 * @author Pavel Savara (original)
 */
public interface ISettingsManager {
	void saveProperties();

	boolean getOptionsViewRobotNames();

	void setOptionsViewRobotNames(boolean optionsViewRobotNames);

	boolean getOptionsViewScanArcs();

	void setOptionsViewScanArcs(boolean optionsViewScanArcs);

	boolean getOptionsViewRobotEnergy();

	void setOptionsViewRobotEnergy(boolean optionsViewRobotEnergy);

	boolean getOptionsViewGround();

	void setOptionsViewGround(boolean optionsViewGround);

	boolean getOptionsViewTPS();

	void setOptionsViewTPS(boolean optionsViewTPS);

	boolean getOptionsViewFPS();

	void setOptionsViewFPS(boolean optionsViewFPS);

	boolean getOptionsViewExplosions();

	void setOptionsViewExplosions(boolean optionsViewExplosions);

	boolean getOptionsViewExplosionDebris();

	void setOptionsViewExplosionDebris(boolean optionsViewExplosionDebris);

	boolean getOptionsViewSentryBorder();

	void setOptionsViewSentryBorder(boolean optionsViewSentryBorder);

	boolean getOptionsViewPreventSpeedupWhenMinimized();

	void setOptionsViewPreventSpeedupWhenMinimized(boolean preventSpeedupWhenMinimized);

	int getOptionsRenderingAntialiasing();

	void setOptionsRenderingAntialiasing(int optionsRenderingAntialiasing);

	int getOptionsRenderingTextAntialiasing();

	void setOptionsRenderingTextAntialiasing(int optionsRenderingTextAntialiasing);

	int getOptionsRenderingMethod();

	void setOptionsRenderingMethod(int optionsRenderingMethod);

	RenderingHints getRenderingHints();

	int getOptionsRenderingNoBuffers();

	void setOptionsRenderingNoBuffers(int optionsRenderingNoBuffers);

	boolean getOptionsRenderingBufferImages();

	void setOptionsRenderingBufferImages(boolean optionsRenderingBufferImages);

	boolean getOptionsRenderingForceBulletColor();

	void setOptionsRenderingForceBulletColor(boolean optionsRenderingForceBulletColor);

	int getOptionsBattleDesiredTPS();

	void setOptionsBattleDesiredTPS(int optionsBattleDesiredTPS);

	boolean getOptionsSoundEnableSound();

	void setOptionsSoundEnableSound(boolean optionsSoundEnableSound);

	boolean getOptionsSoundEnableGunshot();

	void setOptionsSoundEnableGunshot(boolean optionsSoundEnableGunshot);

	boolean getOptionsSoundEnableBulletHit();

	void setOptionsSoundEnableBulletHit(boolean optionsSoundEnableBulletHit);

	boolean getOptionsSoundEnableRobotDeath();

	void setOptionsSoundEnableRobotDeath(boolean optionsSoundEnableRobotDeath);

	boolean getOptionsSoundEnableWallCollision();

	void setOptionsSoundEnableWallCollision(boolean optionsSoundEnableWallCollision);

	boolean getOptionsSoundEnableRobotCollision();

	void setOptionsSoundEnableRobotCollision(boolean optionsSoundEnableRobotCollision);

	boolean getOptionsSoundEnableMixerVolume();

	void setOptionsSoundMixer(String optionsSoundMixer);

	String getOptionsSoundMixer();

	void setOptionsSoundEnableMixerVolume(boolean optionsSoundEnableMixerVolume);

	boolean getOptionsSoundEnableMixerPan();

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

	Date getVersionChecked();

	void setVersionChecked(Date versionChecked);

	long getRobotFilesystemQuota();

	void setRobotFilesystemQuota(long robotFilesystemQuota);

	long getConsoleQuota();

	void setConsoleQuota(long consoleQuota);

	long getCpuConstant();

	void setCpuConstant(long cpuConstant);

	Collection<String> getOptionsDevelopmentPaths();

	void setOptionsDevelopmentPaths(Collection<String> paths);

	Collection<String> getOptionsExcludedDevelopmentPaths();

	void setOptionsExcludedDevelopmentPaths(Collection<String> excludedPaths);

	Collection<String> getOptionsEnabledDevelopmentPaths();

	boolean getOptionsCommonShowResults();

	boolean getOptionsCommonDontHideRankings();

	void setOptionsCommonDontHideRankings(boolean dontHide);

	void setOptionsCommonAppendWhenSavingResults(boolean enable);

	boolean getOptionsCommonAppendWhenSavingResults();

	void setOptionsCommonShowResults(boolean enable);

	boolean getOptionsCommonEnableReplayRecording();

	boolean getOptionsCommonEnableAutoRecording();

	boolean getOptionsCommonAutoRecordingXML();

	void setOptionsCommonEnableReplayRecording(boolean enable);

	void setOptionsCommonEnableAutoRecording(boolean enable);

	void setOptionsCommonEnableAutoRecordingXML(boolean enable);

	void setOptionsCommonNotifyAboutNewBetaVersions(boolean enable);

	boolean getOptionsCommonNotifyAboutNewBetaVersions();

	int getBattleDefaultBattlefieldWidth();

	void setBattleDefaultBattlefieldWidth(int battlefieldWidth);

	int getBattleDefaultBattlefieldHeight();

	void setBattleDefaultBattlefieldHeight(int battlefieldHeight);

	double getBattleDefaultGunCoolingRate();

	void setBattleDefaultGunCoolingRate(double gunCoolingRate);

	long getBattleDefaultInactivityTime();

	void setBattleDefaultInactivityTime(long inactivityTime);

	int getBattleDefaultSentryBorderSize();

	void setBattleDefaultSentryBorderSize(int sentryBorderSize);

	boolean getBattleDefaultHideEnemyNames();

	void setBattleDefaultHideEnemyNames(boolean hideEnemyNames);

	int getBattleDefaultNumberOfRounds();

	void setBattleDefaultNumberOfRounds(int numberOfRounds);

	void store(FileOutputStream out, String desc) throws IOException;

	void load(FileInputStream in) throws IOException;

	String getLastRunVersion();

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
			OPTIONS_VIEW_SENTRY_BORDER = "robocode.options.view.sentryBorder",

			OPTIONS_BATTLE_DESIREDTPS = "robocode.options.battle.desiredTPS",

			OPTIONS_VIEW_PREVENT_SPEEDUP_WHEN_MINIMIZED = "robocode.options.view.preventSpeedupWhenMinimized",

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
	
			OPTIONS_COMMON_NOTIFY_ABOUT_NEW_BETA_VERSIONS = "robocode.options.common.notifyAboutNewBetaVersions",
			OPTIONS_COMMON_SHOW_RESULTS = "robocode.options.common.showResults",
			OPTIONS_COMMON_DONT_HIDE_RANKINGS = "robocode.options.common.dontHideRankings",
			OPTIONS_COMMON_APPEND_WHEN_SAVING_RESULTS = "robocode.options.common.appendWhenSavingResults",
			OPTIONS_COMMON_ENABLE_REPLAY_RECORDING = "robocode.options.common.enableReplayRecording",
			OPTIONS_COMMON_ENABLE_AUTO_RECORDING = "robocode.options.common.enableAutoRecording",
			OPTIONS_COMMON_AUTO_RECORDING_XML = "robocode.options.common.autoRecordingXML",

			OPTIONS_TEAM_SHOWTEAMROBOTS = "robocode.options.team.showTeamRobots",

			OPTIONS_DEVELOPMENT_PATH = "robocode.options.development.path",
			OPTIONS_DEVELOPMENT_PATH_EXCLUDED = "robocode.options.development.path.excluded",

			FILE_THEME_MUSIC = "robocode.file.music.theme",
			FILE_BACKGROUND_MUSIC = "robocode.file.music.background",
			FILE_END_OF_BATTLE_MUSIC = "robocode.file.music.endOfBattle",

			FILE_GUNSHOT_SFX = "robocode.file.sfx.gunshot",
			FILE_ROBOT_COLLISION_SFX = "robocode.file.sfx.robotCollision",
			FILE_WALL_COLLISION_SFX = "robocode.file.sfx.wallCollision",
			FILE_ROBOT_DEATH_SFX = "robocode.file.sfx.robotDeath",
			FILE_BULLET_HITS_ROBOT_SFX = "robocode.file.sfx.bulletHitsRobot",
			FILE_BULLET_HITS_BULLET_SFX = "robocode.file.sfx.bulletHitsBullet",

			VERSIONCHECKED = "robocode.versionchecked",
			ROBOT_FILESYSTEM_QUOTA = "robocode.robot.filesystem.quota",
			CONSOLE_QUOTA = "robocode.console.quota",
			CPU_CONSTANT = "robocode.cpu.constant",
			LAST_RUN_VERSION = "robocode.version.lastrun",

			BATTLE_DEFAULT_BATTLEFIELD_WIDTH = "robocode.battle.default.battlefieldWidth",
			BATTLE_DEFAULT_BATTLEFIELD_HEIGHT = "robocode.battle.default.battlefieldHeight",
			BATTLE_DEFAULT_NUMBER_OF_ROUNDS = "robocode.battle.default.numberOfBattles",
			BATTLE_DEFAULT_GUN_COOLING_RATE = "robocode.battle.default.gunCoolingRate",
			BATTLE_DEFAULT_INACTIVITY_TIME = "robocode.battle.default.inactivityTime",
			BATTLE_DEFAULT_SENTRY_BORDER_SIZE = "robocode.battle.default.sentryBorderSize",
			BATTLE_DEFAULT_HIDE_ENEMY_NAMES = "robocode.battle.default.hideEnemyNames";
}
