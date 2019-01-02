/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.settings;


import net.sf.robocode.io.FileUtil;
import net.sf.robocode.io.Logger;
import static net.sf.robocode.io.Logger.logError;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Nathaniel Troutman (contributor)
 */
// TODO ZAMO, refactor, split by modules
public class SettingsManager implements ISettingsManager {
	// Default SFX files
	private final static String
			DEFAULT_FILE_GUNSHOT_SFX = "/net/sf/robocode/sound/sounds/zap.wav",
			DEFAULT_FILE_ROBOT_COLLISION_SFX = "/net/sf/robocode/sound/sounds/13831_adcbicycle_22.wav",
			DEFAULT_FILE_WALL_COLLISION_SFX = DEFAULT_FILE_ROBOT_COLLISION_SFX,
			DEFAULT_FILE_ROBOT_DEATH_SFX = "/net/sf/robocode/sound/sounds/explode.wav",
			DEFAULT_FILE_BULLET_HITS_ROBOT_SFX = "/net/sf/robocode/sound/sounds/shellhit.wav",
			DEFAULT_FILE_BULLET_HITS_BULLET_SFX = DEFAULT_FILE_BULLET_HITS_ROBOT_SFX;

	// View Options (Arena)
	private boolean
			optionsViewRobotEnergy = true,
			optionsViewRobotNames = true,
			optionsViewScanArcs = false,
			optionsViewExplosions = true,
			optionsViewGround = true,
			optionsViewExplosionDebris = true,
			optionsViewSentryBorder = false;

	// View Options (Turns Per Second)
	private boolean
			optionsViewTPS = true,
			optionsViewFPS = true;

	// Prevent speedup when view is minimized
	private boolean
			optionsViewPreventSpeedupWhenMinimized = false;

	// Rendering Options
	private int
			optionsRenderingAntialiasing = 0, // 0 = default, 1 = on, 2 = off
			optionsRenderingTextAntialiasing = 0, // 0 = default, 1 = on, 2 = off
			optionsRenderingMethod = 0, // 0 = default, 1 = speed, 2 = quality
			optionsRenderingNoBuffers = 2, // 1 = single buffering, 2 = double buffering, 3 = triple buffering
			optionsBattleDesiredTPS = 30;

	private boolean
			optionsRenderingBufferImages = true,
			optionsRenderingForceBulletColor = false;

	// Sound Options (Sound Effects)
	private boolean
			optionsSoundEnableSound = false,
			optionsSoundEnableGunshot = true,
			optionsSoundEnableBulletHit = true,
			optionsSoundEnableRobotDeath = true,
			optionsSoundEnableWallCollision = true,
			optionsSoundEnableRobotCollision = true;

	// Sound Options (Mixer)
	private String optionsSoundMixer = "DirectAudioDevice";
	private boolean
			optionsSoundEnableMixerVolume = true,
			optionsSoundEnableMixerPan = true;

	// Development Options
	private Collection<String>
			optionsDevelopmentPaths = new HashSet<String>(),
			optionsExcludedDevelopmentPaths = new HashSet<String>();

	// Common Options
	private boolean
			optionsCommonNotifyAboutNewBetaVersions = false,
			optionsCommonShowResults = true,
			optionsCommonAppendWhenSavingResults = true,
			optionsCommonDontHideRankings = true,
			optionsCommonEnableAutoRecording = false,
			optionsCommonAutoRecordingXML = false,
			optionsCommonEnableReplayRecording = false;

	// Team Options
	private boolean optionsTeamShowTeamRobots = false;

	// Music files
	private String
			fileThemeMusic = "",
			fileBackgroundMusic = "",
			fileEndOfBattleMusic = "";

	// SFX files
	private String
			fileGunshotSfx = DEFAULT_FILE_GUNSHOT_SFX,
			fileRobotCollisionSfx = DEFAULT_FILE_ROBOT_COLLISION_SFX,
			fileWallCollisionSfx = DEFAULT_FILE_WALL_COLLISION_SFX,
			fileRobotDeathSfx = DEFAULT_FILE_ROBOT_DEATH_SFX,
			fileBulletHitsRobotSfx = DEFAULT_FILE_BULLET_HITS_ROBOT_SFX,
			fileBulletHitsBulletSfx = DEFAULT_FILE_BULLET_HITS_BULLET_SFX;

	// Robocode internals
	private String lastRunVersion = "";
	private Date versionChecked;
	private long robotFilesystemQuota = 200000;
	private long consoleQuota = 8192;
	private long cpuConstant = -1;

	// Battle default settings
	private int battleDefaultBattlefieldWidth = 800;
	private int battleDefaultBattlefieldHeight = 600;
	private double battleDefaultGunCoolingRate = 0.1;
	private long battleDefaultInactivityTime = 450;
	private int battleDefaultSentryBorderSize = 100;
	private boolean battleDefaultHideEnemyNames = false;
	private int battleDefaultNumberOfRounds = 10;

	private final Properties props = new SortedProperties();

	private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy H:mm:ss");

	private final RenderingHints renderingHints = new RenderingHints(new HashMap<RenderingHints.Key, Object>());

	private final List<ISettingsListener> listeners = new ArrayList<ISettingsListener>();

	public SettingsManager() {
		FileInputStream in = null;
		try {
			in = new FileInputStream(FileUtil.getRobocodeConfigFile());
			this.load(in);
		} catch (FileNotFoundException e) {
			logError("No " + FileUtil.getRobocodeConfigFile().getName() + ". Using defaults.");
		} catch (IOException e) {
			logError("Error while reading " + FileUtil.getRobocodeConfigFile().getName() + ": " + e);
		} finally {
			if (in != null) {
				// noinspection EmptyCatchBlock
				try {
					in.close();
				} catch (IOException e) {}
			}
		}
	}

	public void saveProperties() {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(FileUtil.getRobocodeConfigFile());
			this.store(out, "Robocode Properties");
		} catch (IOException e) {
			Logger.logError(e);
		} finally {
			if (out != null) {
				// noinspection EmptyCatchBlock
				try {
					out.close();
				} catch (IOException e) {}
			}
		}
	}

	public boolean getOptionsViewRobotNames() {
		return optionsViewRobotNames;
	}

	public void setOptionsViewRobotNames(boolean optionsViewRobotNames) {
		this.optionsViewRobotNames = optionsViewRobotNames;
		props.setProperty(OPTIONS_VIEW_ROBOTNAMES, "" + optionsViewRobotNames);
	}

	public boolean getOptionsViewScanArcs() {
		return optionsViewScanArcs;
	}

	public void setOptionsViewScanArcs(boolean optionsViewScanArcs) {
		this.optionsViewScanArcs = optionsViewScanArcs;
		props.setProperty(OPTIONS_VIEW_SCANARCS, "" + optionsViewScanArcs);
	}

	public boolean getOptionsViewRobotEnergy() {
		return optionsViewRobotEnergy;
	}

	public void setOptionsViewRobotEnergy(boolean optionsViewRobotEnergy) {
		this.optionsViewRobotEnergy = optionsViewRobotEnergy;
		props.setProperty(OPTIONS_VIEW_ROBOTENERGY, "" + optionsViewRobotEnergy);
	}

	public boolean getOptionsViewGround() {
		return optionsViewGround;
	}

	public void setOptionsViewGround(boolean optionsViewGround) {
		this.optionsViewGround = optionsViewGround;
		props.setProperty(OPTIONS_VIEW_GROUND, "" + optionsViewGround);
	}

	public boolean getOptionsViewTPS() {
		return optionsViewTPS;
	}

	public void setOptionsViewTPS(boolean optionsViewTPS) {
		this.optionsViewTPS = optionsViewTPS;
		props.setProperty(OPTIONS_VIEW_TPS, "" + optionsViewTPS);
	}

	public boolean getOptionsViewFPS() {
		return optionsViewFPS;
	}

	public void setOptionsViewFPS(boolean optionsViewFPS) {
		this.optionsViewFPS = optionsViewFPS;
		props.setProperty(OPTIONS_VIEW_FPS, "" + optionsViewFPS);
	}

	public boolean getOptionsViewExplosions() {
		return optionsViewExplosions;
	}

	public void setOptionsViewExplosions(boolean optionsViewExplosions) {
		this.optionsViewExplosions = optionsViewExplosions;
		props.setProperty(OPTIONS_VIEW_EXPLOSIONS, "" + optionsViewExplosions);
	}

	public boolean getOptionsViewExplosionDebris() {
		return optionsViewExplosionDebris;
	}

	public void setOptionsViewExplosionDebris(boolean optionsViewExplosionDebris) {
		this.optionsViewExplosionDebris = optionsViewExplosionDebris;
		props.setProperty(OPTIONS_VIEW_EXPLOSION_DEBRIS, "" + optionsViewExplosionDebris);
	}

	public boolean getOptionsViewPreventSpeedupWhenMinimized() {
		return optionsViewPreventSpeedupWhenMinimized;
	}

	public void setOptionsViewPreventSpeedupWhenMinimized(boolean preventSpeedupWhenMinimized) {
		this.optionsViewPreventSpeedupWhenMinimized = preventSpeedupWhenMinimized;
		props.setProperty(OPTIONS_VIEW_PREVENT_SPEEDUP_WHEN_MINIMIZED, "" + preventSpeedupWhenMinimized);
	}

	public boolean getOptionsViewSentryBorder() {
		return optionsViewSentryBorder;
	}

	public void setOptionsViewSentryBorder(boolean optionsViewSentryBorder) {
		this.optionsViewSentryBorder = optionsViewSentryBorder;
		props.setProperty(OPTIONS_VIEW_SENTRY_BORDER, "" + optionsViewSentryBorder);
	}

	public int getOptionsRenderingAntialiasing() {
		return optionsRenderingAntialiasing;
	}

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

	public int getOptionsRenderingTextAntialiasing() {
		return optionsRenderingTextAntialiasing;
	}

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

	public int getOptionsRenderingMethod() {
		return optionsRenderingMethod;
	}

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

	public RenderingHints getRenderingHints() {
		return renderingHints;
	}

	public int getOptionsRenderingNoBuffers() {
		return optionsRenderingNoBuffers;
	}

	public void setOptionsRenderingNoBuffers(int optionsRenderingNoBuffers) {
		this.optionsRenderingNoBuffers = optionsRenderingNoBuffers;
		props.setProperty(OPTIONS_RENDERING_NO_BUFFERS, "" + optionsRenderingNoBuffers);
	}

	public boolean getOptionsRenderingBufferImages() {
		return optionsRenderingBufferImages;
	}

	public void setOptionsRenderingBufferImages(boolean optionsRenderingBufferImages) {
		this.optionsRenderingBufferImages = optionsRenderingBufferImages;
		props.setProperty(OPTIONS_RENDERING_BUFFER_IMAGES, "" + optionsRenderingBufferImages);
	}

	public boolean getOptionsRenderingForceBulletColor() {
		return optionsRenderingForceBulletColor;
	}

	public void setOptionsRenderingForceBulletColor(boolean optionsRenderingForceBulletColor) {
		this.optionsRenderingForceBulletColor = optionsRenderingForceBulletColor;
		props.setProperty(OPTIONS_RENDERING_FORCE_BULLET_COLOR, "" + optionsRenderingForceBulletColor);
	}

	public int getOptionsBattleDesiredTPS() {
		return optionsBattleDesiredTPS;
	}

	public void setOptionsBattleDesiredTPS(int optionsBattleDesiredTPS) {
		this.optionsBattleDesiredTPS = optionsBattleDesiredTPS;
		props.setProperty(OPTIONS_BATTLE_DESIREDTPS, "" + optionsBattleDesiredTPS);
	}

	public boolean getOptionsSoundEnableSound() {
		return optionsSoundEnableSound;
	}

	public void setOptionsSoundEnableSound(boolean optionsSoundEnableSound) {
		this.optionsSoundEnableSound = optionsSoundEnableSound;
		props.setProperty(OPTIONS_SOUND_ENABLESOUND, "" + optionsSoundEnableSound);
	}

	public boolean getOptionsSoundEnableGunshot() {
		return optionsSoundEnableGunshot;
	}

	public void setOptionsSoundEnableGunshot(boolean optionsSoundEnableGunshot) {
		this.optionsSoundEnableGunshot = optionsSoundEnableGunshot;
		props.setProperty(OPTIONS_SOUND_ENABLEGUNSHOT, "" + optionsSoundEnableGunshot);
	}

	public boolean getOptionsSoundEnableBulletHit() {
		return optionsSoundEnableBulletHit;
	}

	public void setOptionsSoundEnableBulletHit(boolean optionsSoundEnableBulletHit) {
		this.optionsSoundEnableBulletHit = optionsSoundEnableBulletHit;
		props.setProperty(OPTIONS_SOUND_ENABLEBULLETHIT, "" + optionsSoundEnableBulletHit);
	}

	public boolean getOptionsSoundEnableRobotDeath() {
		return optionsSoundEnableRobotDeath;
	}

	public void setOptionsSoundEnableRobotDeath(boolean optionsSoundEnableRobotDeath) {
		this.optionsSoundEnableRobotDeath = optionsSoundEnableRobotDeath;
		props.setProperty(OPTIONS_SOUND_ENABLEROBOTDEATH, "" + optionsSoundEnableRobotDeath);
	}

	public boolean getOptionsSoundEnableWallCollision() {
		return optionsSoundEnableWallCollision;
	}

	public void setOptionsSoundEnableWallCollision(boolean optionsSoundEnableWallCollision) {
		this.optionsSoundEnableWallCollision = optionsSoundEnableWallCollision;
		props.setProperty(OPTIONS_SOUND_ENABLEWALLCOLLISION, "" + optionsSoundEnableWallCollision);
	}

	public boolean getOptionsSoundEnableRobotCollision() {
		return optionsSoundEnableRobotCollision;
	}

	public void setOptionsSoundEnableRobotCollision(boolean optionsSoundEnableRobotCollision) {
		this.optionsSoundEnableRobotCollision = optionsSoundEnableRobotCollision;
		props.setProperty(OPTIONS_SOUND_ENABLEROBOTCOLLISION, "" + optionsSoundEnableRobotCollision);
	}

	public boolean getOptionsSoundEnableMixerVolume() {
		return optionsSoundEnableMixerVolume;
	}

	public void setOptionsSoundMixer(String optionsSoundMixer) {
		this.optionsSoundMixer = optionsSoundMixer;
		props.setProperty(OPTIONS_SOUND_MIXER, optionsSoundMixer);
	}

	public String getOptionsSoundMixer() {
		return optionsSoundMixer;
	}

	public void setOptionsSoundEnableMixerVolume(boolean optionsSoundEnableMixerVolume) {
		this.optionsSoundEnableMixerVolume = optionsSoundEnableMixerVolume;
		props.setProperty(OPTIONS_SOUND_ENABLEMIXERVOLUME, "" + optionsSoundEnableMixerVolume);
	}

	public boolean getOptionsSoundEnableMixerPan() {
		return optionsSoundEnableMixerPan;
	}

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

	public String getFileThemeMusic() {
		return fileThemeMusic;
	}

	public String getFileBackgroundMusic() {
		return fileBackgroundMusic;
	}

	public String getFileEndOfBattleMusic() {
		return fileEndOfBattleMusic;
	}

	public String getFileGunshotSfx() {
		return fileGunshotSfx;
	}

	public String getBulletHitsRobotSfx() {
		return fileBulletHitsRobotSfx;
	}

	public String getBulletHitsBulletSfx() {
		return fileBulletHitsBulletSfx;
	}

	public String getRobotDeathSfx() {
		return fileRobotDeathSfx;
	}

	public String getRobotCollisionSfx() {
		return fileRobotCollisionSfx;
	}

	public String getWallCollisionSfx() {
		return fileWallCollisionSfx;
	}

	public Date getVersionChecked() {
		return (versionChecked != null) ? (Date) versionChecked.clone() : null;
	}

	public void setVersionChecked(Date versionChecked) {
		this.versionChecked = (versionChecked != null) ? (Date) versionChecked.clone() : null;
		props.setProperty(VERSIONCHECKED, dateFormat.format(new Date()));
	}

	public long getRobotFilesystemQuota() {
		return robotFilesystemQuota;
	}

	public void setRobotFilesystemQuota(long robotFilesystemQuota) {
		this.robotFilesystemQuota = robotFilesystemQuota;
		props.setProperty(ROBOT_FILESYSTEM_QUOTA, "" + robotFilesystemQuota);
	}

	public long getConsoleQuota() {
		return consoleQuota;
	}

	public void setConsoleQuota(long consoleQuota) {
		this.consoleQuota = consoleQuota;
		props.setProperty(CONSOLE_QUOTA, "" + consoleQuota);
	}

	public long getCpuConstant() {
		return cpuConstant;
	}

	public void setCpuConstant(long cpuConstant) {
		this.cpuConstant = cpuConstant;
		props.setProperty(CPU_CONSTANT, "" + cpuConstant);
	}

	public Collection<String> getOptionsDevelopmentPaths() {
		return new HashSet<String>(optionsDevelopmentPaths);
	}

	public void setOptionsDevelopmentPaths(Collection<String> paths) {
		this.optionsDevelopmentPaths = new HashSet<String>(paths);

		props.setProperty(OPTIONS_DEVELOPMENT_PATH, toCommaSeparatedString(paths));
	}

	public Collection<String> getOptionsExcludedDevelopmentPaths() {
		return new HashSet<String>(optionsExcludedDevelopmentPaths);
	}

	public void setOptionsExcludedDevelopmentPaths(Collection<String> paths) {
		this.optionsExcludedDevelopmentPaths = new HashSet<String>(paths);

		props.setProperty(OPTIONS_DEVELOPMENT_PATH_EXCLUDED, toCommaSeparatedString(paths));
	}

	public Collection<String> getOptionsEnabledDevelopmentPaths() {
		Collection<String> paths = getOptionsDevelopmentPaths();

		paths.removeAll(getOptionsExcludedDevelopmentPaths());
		return paths;
	}

	public boolean getOptionsCommonShowResults() {
		return optionsCommonShowResults;
	}

	public void setOptionsCommonAppendWhenSavingResults(boolean enable) {
		this.optionsCommonAppendWhenSavingResults = enable;
		props.setProperty(OPTIONS_COMMON_APPEND_WHEN_SAVING_RESULTS, "" + enable);
	}

	public boolean getOptionsCommonAppendWhenSavingResults() {
		return optionsCommonAppendWhenSavingResults;
	}

	public void setOptionsCommonShowResults(boolean enable) {
		this.optionsCommonShowResults = enable;
		props.setProperty(OPTIONS_COMMON_SHOW_RESULTS, "" + enable);
	}

	public boolean getOptionsCommonDontHideRankings() {
		return optionsCommonDontHideRankings;
	}

	public void setOptionsCommonDontHideRankings(boolean enable) {
		this.optionsCommonDontHideRankings = enable;
		props.setProperty(OPTIONS_COMMON_DONT_HIDE_RANKINGS, "" + enable);
	}

	public boolean getOptionsCommonEnableReplayRecording() {
		return optionsCommonEnableReplayRecording;
	}

	public void setOptionsCommonEnableReplayRecording(boolean enable) {
		this.optionsCommonEnableReplayRecording = enable;
		props.setProperty(OPTIONS_COMMON_ENABLE_REPLAY_RECORDING, "" + enable);
	}

	public boolean getOptionsCommonEnableAutoRecording() {
		return optionsCommonEnableAutoRecording;
	}

	public boolean getOptionsCommonAutoRecordingXML() {
		return optionsCommonAutoRecordingXML;
	}

	public void setOptionsCommonEnableAutoRecording(boolean enable) {
		this.optionsCommonEnableAutoRecording = enable;
		props.setProperty(OPTIONS_COMMON_ENABLE_AUTO_RECORDING, "" + enable);
	}

	public void setOptionsCommonEnableAutoRecordingXML(boolean enable) {
		this.optionsCommonAutoRecordingXML = enable;
		props.setProperty(OPTIONS_COMMON_AUTO_RECORDING_XML, "" + enable);
	}

	public void setOptionsCommonNotifyAboutNewBetaVersions(boolean enable) {
		this.optionsCommonNotifyAboutNewBetaVersions = enable;
		props.setProperty(OPTIONS_COMMON_NOTIFY_ABOUT_NEW_BETA_VERSIONS, "" + enable);
	}

	public boolean getOptionsCommonNotifyAboutNewBetaVersions() {
		return optionsCommonNotifyAboutNewBetaVersions;
	}

	public int getBattleDefaultBattlefieldWidth() {
		return battleDefaultBattlefieldWidth;
	}

	public void setBattleDefaultBattlefieldWidth(int battlefieldWidth) {
		this.battleDefaultBattlefieldWidth = Math.max(400, battlefieldWidth);
		props.setProperty(BATTLE_DEFAULT_BATTLEFIELD_WIDTH, "" + this.battleDefaultBattlefieldWidth);
	}

	public int getBattleDefaultBattlefieldHeight() {
		return battleDefaultBattlefieldHeight;
	}

	public void setBattleDefaultBattlefieldHeight(int battlefieldHeight) {
		this.battleDefaultBattlefieldHeight = Math.max(400, battlefieldHeight);
		props.setProperty(BATTLE_DEFAULT_BATTLEFIELD_HEIGHT, "" + this.battleDefaultBattlefieldHeight);
	}

	public double getBattleDefaultGunCoolingRate() {
		return battleDefaultGunCoolingRate;
	}

	public void setBattleDefaultGunCoolingRate(double gunCoolingRate) {
		this.battleDefaultGunCoolingRate = Math.max(0.1, gunCoolingRate);
		props.setProperty(BATTLE_DEFAULT_GUN_COOLING_RATE, "" + this.battleDefaultGunCoolingRate);
	}

	public long getBattleDefaultInactivityTime() {
		return battleDefaultInactivityTime;
	}

	public void setBattleDefaultInactivityTime(long inactivityTime) {
		this.battleDefaultInactivityTime = Math.max(0, inactivityTime);
		props.setProperty(BATTLE_DEFAULT_INACTIVITY_TIME, "" + this.battleDefaultInactivityTime);
	}

	public int getBattleDefaultSentryBorderSize() {
		return battleDefaultSentryBorderSize;
	}

	public void setBattleDefaultSentryBorderSize(int sentryBorderSize) {
		this.battleDefaultSentryBorderSize = sentryBorderSize;
		props.setProperty(BATTLE_DEFAULT_SENTRY_BORDER_SIZE, "" + this.battleDefaultSentryBorderSize);
	}

	public boolean getBattleDefaultHideEnemyNames() {
		return battleDefaultHideEnemyNames;
	}

	public void setBattleDefaultHideEnemyNames(boolean hideEnemyNames) {
		this.battleDefaultHideEnemyNames = hideEnemyNames;
		props.setProperty(BATTLE_DEFAULT_HIDE_ENEMY_NAMES, "" + this.battleDefaultHideEnemyNames);
	}

	public int getBattleDefaultNumberOfRounds() {
		return battleDefaultNumberOfRounds;
	}

	public void setBattleDefaultNumberOfRounds(int numberOfRounds) {
		this.battleDefaultNumberOfRounds = Math.max(1, numberOfRounds);
		props.setProperty(BATTLE_DEFAULT_NUMBER_OF_ROUNDS, "" + this.battleDefaultNumberOfRounds);
	}

	public void store(FileOutputStream out, String desc) throws IOException {
		props.store(out, desc);
	}

	public void load(FileInputStream in) throws IOException {
		props.load(in);

		optionsViewRobotNames = Boolean.valueOf(props.getProperty(OPTIONS_VIEW_ROBOTNAMES, "true"));
		optionsViewScanArcs = Boolean.valueOf(props.getProperty(OPTIONS_VIEW_SCANARCS, "false"));
		optionsViewRobotEnergy = Boolean.valueOf(props.getProperty(OPTIONS_VIEW_ROBOTENERGY, "true"));
		optionsViewGround = Boolean.valueOf(props.getProperty(OPTIONS_VIEW_GROUND, "true"));
		optionsViewTPS = Boolean.valueOf(props.getProperty(OPTIONS_VIEW_TPS, "true"));
		optionsViewFPS = Boolean.valueOf(props.getProperty(OPTIONS_VIEW_FPS, "true"));
		optionsViewExplosions = Boolean.valueOf(props.getProperty(OPTIONS_VIEW_EXPLOSIONS, "true"));
		optionsViewExplosionDebris = Boolean.valueOf(props.getProperty(OPTIONS_VIEW_EXPLOSION_DEBRIS, "true"));
		optionsViewSentryBorder = Boolean.valueOf(props.getProperty(OPTIONS_VIEW_SENTRY_BORDER, "false"));
		optionsViewPreventSpeedupWhenMinimized = Boolean.valueOf(
				props.getProperty(OPTIONS_VIEW_PREVENT_SPEEDUP_WHEN_MINIMIZED, "false"));

		optionsBattleDesiredTPS = Integer.parseInt(props.getProperty(OPTIONS_BATTLE_DESIREDTPS, "30"));

		// set methods are used here in order to set the rendering hints, which must be rebuild
		setOptionsRenderingAntialiasing(Integer.parseInt(props.getProperty(OPTIONS_RENDERING_ANTIALIASING, "0")));
		setOptionsRenderingTextAntialiasing(
				Integer.parseInt(props.getProperty(OPTIONS_RENDERING_TEXT_ANTIALIASING, "0")));
		setOptionsRenderingMethod(Integer.parseInt(props.getProperty(OPTIONS_RENDERING_METHOD, "0")));
		optionsRenderingNoBuffers = Integer.parseInt(props.getProperty(OPTIONS_RENDERING_NO_BUFFERS, "2"));
		optionsRenderingBufferImages = Boolean.valueOf(props.getProperty(OPTIONS_RENDERING_BUFFER_IMAGES, "true"));
		optionsRenderingForceBulletColor = Boolean.valueOf(
				props.getProperty(OPTIONS_RENDERING_FORCE_BULLET_COLOR, "false"));

		optionsSoundEnableSound = Boolean.valueOf(props.getProperty(OPTIONS_SOUND_ENABLESOUND, "false"));
		optionsSoundEnableGunshot = Boolean.valueOf(props.getProperty(OPTIONS_SOUND_ENABLEGUNSHOT, "true"));
		optionsSoundEnableBulletHit = Boolean.valueOf(props.getProperty(OPTIONS_SOUND_ENABLEBULLETHIT, "true"));
		optionsSoundEnableRobotDeath = Boolean.valueOf(props.getProperty(OPTIONS_SOUND_ENABLEROBOTDEATH, "true"));
		optionsSoundEnableRobotCollision = Boolean.valueOf(props.getProperty(OPTIONS_SOUND_ENABLEROBOTCOLLISION, "true"));
		optionsSoundEnableWallCollision = Boolean.valueOf(props.getProperty(OPTIONS_SOUND_ENABLEWALLCOLLISION, "true"));

		optionsSoundMixer = props.getProperty(OPTIONS_SOUND_MIXER, "DirectAudioDevice");
		optionsSoundEnableMixerVolume = Boolean.valueOf(props.getProperty(OPTIONS_SOUND_ENABLEMIXERVOLUME, "true"));
		optionsSoundEnableMixerPan = Boolean.valueOf(props.getProperty(OPTIONS_SOUND_ENABLEMIXERPAN, "true"));

		optionsDevelopmentPaths = fromCommaSeparatedString(props.getProperty(OPTIONS_DEVELOPMENT_PATH, ""));
		optionsExcludedDevelopmentPaths = fromCommaSeparatedString(
				props.getProperty(OPTIONS_DEVELOPMENT_PATH_EXCLUDED, ""));

		optionsCommonNotifyAboutNewBetaVersions = Boolean.valueOf(
				props.getProperty(OPTIONS_COMMON_NOTIFY_ABOUT_NEW_BETA_VERSIONS, "false"));
		optionsCommonShowResults = Boolean.valueOf(props.getProperty(OPTIONS_COMMON_SHOW_RESULTS, "true"));
		optionsCommonAppendWhenSavingResults = Boolean.valueOf(
				props.getProperty(OPTIONS_COMMON_APPEND_WHEN_SAVING_RESULTS, "true"));
		optionsCommonDontHideRankings = Boolean.valueOf(props.getProperty(OPTIONS_COMMON_DONT_HIDE_RANKINGS, "true"));
		optionsCommonEnableReplayRecording = Boolean.valueOf(
				props.getProperty(OPTIONS_COMMON_ENABLE_REPLAY_RECORDING, "false"));
		optionsCommonEnableAutoRecording = Boolean.valueOf(
				props.getProperty(OPTIONS_COMMON_ENABLE_AUTO_RECORDING, "false"));
		optionsCommonAutoRecordingXML = Boolean.valueOf(props.getProperty(OPTIONS_COMMON_AUTO_RECORDING_XML, "false"));

		optionsTeamShowTeamRobots = Boolean.valueOf(props.getProperty(OPTIONS_TEAM_SHOWTEAMROBOTS, "false"));

		fileThemeMusic = props.getProperty(FILE_THEME_MUSIC);
		fileBackgroundMusic = props.getProperty(FILE_BACKGROUND_MUSIC);
		fileEndOfBattleMusic = props.getProperty(FILE_END_OF_BATTLE_MUSIC);

		fileGunshotSfx = props.getProperty(FILE_GUNSHOT_SFX, DEFAULT_FILE_GUNSHOT_SFX);
		fileRobotCollisionSfx = props.getProperty(FILE_ROBOT_COLLISION_SFX, DEFAULT_FILE_ROBOT_COLLISION_SFX);
		fileWallCollisionSfx = props.getProperty(FILE_WALL_COLLISION_SFX, DEFAULT_FILE_WALL_COLLISION_SFX);
		fileRobotDeathSfx = props.getProperty(FILE_ROBOT_DEATH_SFX, DEFAULT_FILE_ROBOT_DEATH_SFX);
		fileBulletHitsRobotSfx = props.getProperty(FILE_BULLET_HITS_ROBOT_SFX, DEFAULT_FILE_BULLET_HITS_ROBOT_SFX);
		fileBulletHitsBulletSfx = props.getProperty(FILE_BULLET_HITS_BULLET_SFX, DEFAULT_FILE_BULLET_HITS_BULLET_SFX);

		lastRunVersion = props.getProperty(LAST_RUN_VERSION, "");

		props.remove("robocode.cpu.constant.1000");

		try {
			versionChecked = dateFormat.parse(props.getProperty(VERSIONCHECKED));
		} catch (Exception e) {
			Logger.logMessage("Initializing version check date.");
			setVersionChecked(new Date());
		}

		battleDefaultBattlefieldWidth = Integer.parseInt(props.getProperty(BATTLE_DEFAULT_BATTLEFIELD_WIDTH, "800"));
		battleDefaultBattlefieldHeight = Integer.parseInt(props.getProperty(BATTLE_DEFAULT_BATTLEFIELD_HEIGHT, "600"));
		battleDefaultGunCoolingRate = Double.parseDouble(props.getProperty(BATTLE_DEFAULT_GUN_COOLING_RATE, "0.1"));
		battleDefaultInactivityTime = Long.parseLong(props.getProperty(BATTLE_DEFAULT_INACTIVITY_TIME, "450"));
		battleDefaultHideEnemyNames = Boolean.parseBoolean(props.getProperty(BATTLE_DEFAULT_HIDE_ENEMY_NAMES, "false"));
		battleDefaultNumberOfRounds = Integer.parseInt(props.getProperty(BATTLE_DEFAULT_NUMBER_OF_ROUNDS, "10"));

		robotFilesystemQuota = Long.parseLong(props.getProperty(ROBOT_FILESYSTEM_QUOTA, "" + 200000));
		consoleQuota = Long.parseLong(props.getProperty(CONSOLE_QUOTA, "8192"));
		cpuConstant = Long.parseLong(props.getProperty(CPU_CONSTANT, "-1"));
	}

	public String getLastRunVersion() {
		return lastRunVersion;
	}

	public void setLastRunVersion(String lastRunVersion) {
		this.lastRunVersion = lastRunVersion;
		props.setProperty(LAST_RUN_VERSION, "" + lastRunVersion);
	}

	public void addPropertyListener(ISettingsListener listener) {
		listeners.add(listener);
	}

	public void removePropertyListener(ISettingsListener propertyListener) {
		listeners.remove(propertyListener);
	}

	private void notifyPropertyChanged(String name) {
		for (ISettingsListener listener : listeners) {
			try {
				listener.settingChanged(name);
			} catch (Exception e) {
				Logger.logError(e);
			}
		}
	}

	/**
	 * Returns a comma-separated string from a collection of strings.
	 */
	private static String toCommaSeparatedString(Collection<String> strings) {
		if (strings == null || strings.size() == 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder();

		for (String s : strings) {
			sb.append(s).append(',');
		}
		String line = sb.toString();

		return line.substring(0, line.length() - 1);
	}

	/**
	 * Returns a collection of strings from a comma-separated string.
	 */
	private static Collection<String> fromCommaSeparatedString(String line) {
		if (line == null || line.trim().length() == 0) {
			return new HashSet<String>();
		}
		Set<String> set = new HashSet<String>(); 
		String splitExpr = File.pathSeparatorChar == ':' ? "[,:]+" : "[,;]+";
		String[] strings = line.split(splitExpr);

		for (String s : strings) {
			set.add(s);
		}
		return set;
	}

	/**
	 * Sorted properties used for sorting the keys for the properties file.
	 *
	 * @author Flemming N. Larsen
	 */
	private class SortedProperties extends Properties {
		private static final long serialVersionUID = 1L;

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public Enumeration<Object> keys() {
			Enumeration<Object> keysEnum = super.keys();

			Vector<String> keyList = new Vector<String>();

			while (keysEnum.hasMoreElements()) {
				keyList.add((String) keysEnum.nextElement());
			}

			Collections.sort(keyList);

			// noinspection RedundantCast
			return (Enumeration) keyList.elements();
		}

		@Override
		public synchronized Object setProperty(String key, String value) {
			final String old = super.getProperty(key, null);
			boolean notify = (old == null && value != null) || (old != null && !old.equals(value));
			final Object res = super.setProperty(key, value);

			if (notify) {
				notifyPropertyChanged(key);
			}
			return res;
		}
	}
}
