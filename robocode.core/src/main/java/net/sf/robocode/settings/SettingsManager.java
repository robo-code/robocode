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
 *     - Added option for visible ground, visible explosions, visible explosion
 *       debris, antialiasing, text antialiasing, rendering method, and method
 *       for getting the combined rendering hints
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
 *     - Updated to use methods from the Logger, which replaces logger methods
 *       that have been (re)moved from the robocode.util.Utils class
 *     - Added file paths to theme, background, and end-of-battle music +
 *       file path for sound effects like gunshot, robot death etc.
 *     - Added SortedProperties class in order to sort the keys/fields of the
 *       Robocode properties file
 *     - Added "Buffer images" Render Option
 *     Nathaniel Troutman
 *     - Added missing removePropertyListener() method
 *******************************************************************************/
package net.sf.robocode.settings;


import net.sf.robocode.io.FileUtil;
import net.sf.robocode.io.Logger;
import static net.sf.robocode.io.Logger.logError;

import java.awt.*;
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
			optionsViewExplosionDebris = true;

	// View Options (Turns Per Second)
	private boolean
			optionsViewTPS = true,
			optionsViewFPS = true;

	// Rendering Options
	private int
			optionsRenderingAntialiasing = 0, // 0 = default, 1 = on, 2 = off
			optionsRenderingTextAntialiasing = 0, // 0 = default, 1 = on, 2 = off
			optionsRenderingMethod = 0, // 0 = default, 1 = speed, 2 = quality
			optionsRenderingNoBuffers = 2, // 1 = single buffering, 2 = double buffering, 3 = tripple buffering
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
	private String optionsDevelopmentPath = "";

	// Common Options
	private boolean
			optionsCommonShowResults = true,
			optionsCommonAppendWhenSavingResults = true,
			optionsCommonDontHideRankings = true,
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

	// Number of Rounds
	private int numberOfRounds = 10;

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
			logError("No " + FileUtil.getRobocodeConfigFile().getName() + ", using defaults.");
		} catch (IOException e) {
			logError("IO Exception reading " + FileUtil.getRobocodeConfigFile().getName() + ": " + e);
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
	 * Gets the optionsViewExplosionDebris.
	 *
	 * @return Returns a boolean
	 */
	public boolean getOptionsViewExplosionDebris() {
		return optionsViewExplosionDebris;
	}

	/**
	 * Sets the optionsViewExplosionDebris.
	 *
	 * @param optionsViewExplosionDebris The optionsViewExplosionDebris to set
	 */
	public void setOptionsViewExplosionDebris(boolean optionsViewExplosionDebris) {
		this.optionsViewExplosionDebris = optionsViewExplosionDebris;
		props.setProperty(OPTIONS_VIEW_EXPLOSION_DEBRIS, "" + optionsViewExplosionDebris);
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
	 * @param optionsRenderingTextAntialiasing
	 *         The optionsRenderingTextAntialiasing to set
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
	 * Gets the optionsRenderingBufferImages
	 *
	 * @return Returns a boolean
	 */
	public boolean getOptionsRenderingBufferImages() {
		return optionsRenderingBufferImages;
	}

	/**
	 * Sets the optionsRenderingBufferImages.
	 *
	 * @param optionsRenderingBufferImages The optionsRenderingBufferImages to set
	 */
	public void setOptionsRenderingBufferImages(boolean optionsRenderingBufferImages) {
		this.optionsRenderingBufferImages = optionsRenderingBufferImages;
		props.setProperty(OPTIONS_RENDERING_BUFFER_IMAGES, "" + optionsRenderingBufferImages);
	}

	/**
	 * Gets the optionsRenderingForceBulletColor
	 *
	 * @return Returns a boolean
	 */
	public boolean getOptionsRenderingForceBulletColor() {
		return optionsRenderingForceBulletColor;
	}

	/**
	 * Sets the optionsRenderingForceBulletColor.
	 *
	 * @param optionsRenderingForceBulletColor
	 *         The optionsRenderingForceBulletColor to set
	 */
	public void setOptionsRenderingForceBulletColor(boolean optionsRenderingForceBulletColor) {
		this.optionsRenderingForceBulletColor = optionsRenderingForceBulletColor;
		props.setProperty(OPTIONS_RENDERING_FORCE_BULLET_COLOR, "" + optionsRenderingForceBulletColor);
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
	 * Gets the optionsSoundEnableGunshot
	 *
	 * @return Returns a boolean
	 */
	public boolean getOptionsSoundEnableGunshot() {
		return optionsSoundEnableGunshot;
	}

	/**
	 * Sets the optionsSoundEnableGunshot.
	 *
	 * @param optionsSoundEnableGunshot The optionsSoundEnableGunshot to set
	 */
	public void setOptionsSoundEnableGunshot(boolean optionsSoundEnableGunshot) {
		this.optionsSoundEnableGunshot = optionsSoundEnableGunshot;
		props.setProperty(OPTIONS_SOUND_ENABLEGUNSHOT, "" + optionsSoundEnableGunshot);
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
	 * @param optionsSoundEnableWallCollision
	 *         The optionsSoundEnableWallCollision to set
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
	 * @param optionsSoundEnableRobotCollision
	 *         The optionsSoundEnableRobotCollision to set
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

	/**
	 * Gets the versionChecked.
	 *
	 * @return Returns a String
	 */
	public Date getVersionChecked() {
		return (versionChecked != null) ? (Date) versionChecked.clone() : null;
	}

	/**
	 * Sets the versionChecked.
	 *
	 * @param versionChecked The versionChecked to set
	 */
	public void setVersionChecked(Date versionChecked) {
		this.versionChecked = (versionChecked != null) ? (Date) versionChecked.clone() : null;
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
	 * @return Returns a long
	 */
	public long getCpuConstant() {
		return cpuConstant;
	}

	/**
	 * Sets the cpuConstant.
	 *
	 * @param cpuConstant The cpuConstant to set
	 */
	public void setCpuConstant(long cpuConstant) {
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
	 * Gets the optionsCommonDontHideRankings.
	 *
	 * @return Returns a boolean
	 */
	public boolean getOptionsCommonDontHideRankings() {
		return optionsCommonDontHideRankings;
	}

	/**
	 * Sets the optionsCommonDontHideRankings.
	 *
	 * @param enable The optionsCommonShowResults to set
	 */
	public void setOptionsCommonDontHideRankings(boolean enable) {
		this.optionsCommonDontHideRankings = enable;
		props.setProperty(OPTIONS_COMMON_DONT_HIDE_RANKINGS, "" + enable);
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
	}

	public int getNumberOfRounds() {
		return numberOfRounds;
	}

	public void setNumberOfRounds(int numberOfRounds) {
		this.numberOfRounds = Math.max(1, numberOfRounds);
		props.setProperty(NUMBER_OF_ROUNDS, "" + this.numberOfRounds);
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

		optionsDevelopmentPath = props.getProperty(OPTIONS_DEVELOPMENT_PATH, "");

		optionsCommonShowResults = Boolean.valueOf(props.getProperty(OPTIONS_COMMON_SHOW_RESULTS, "true"));
		optionsCommonAppendWhenSavingResults = Boolean.valueOf(
				props.getProperty(OPTIONS_COMMON_APPEND_WHEN_SAVING_RESULTS, "true"));
		optionsCommonDontHideRankings = Boolean.valueOf(props.getProperty(OPTIONS_COMMON_DONT_HIDE_RANKINGS, "true"));
		optionsCommonEnableReplayRecording = Boolean.valueOf(
				props.getProperty(OPTIONS_COMMON_ENABLE_REPLAY_RECORDING, "false"));

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

		robotFilesystemQuota = Long.parseLong(props.getProperty(ROBOT_FILESYSTEM_QUOTA, "" + 200000));
		consoleQuota = Long.parseLong(props.getProperty(CONSOLE_QUOTA, "8192"));
		cpuConstant = Long.parseLong(props.getProperty(CPU_CONSTANT, "-1"));

		numberOfRounds = Integer.parseInt(props.getProperty(NUMBER_OF_ROUNDS, "10"));
	}

	public String getLastRunVersion() {
		return lastRunVersion;
	}

	/**
	 * Sets the lastRunVersion.
	 *
	 * @param lastRunVersion The lastRunVersion to set
	 */
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
	 * Sorted properties used for sorting the keys for the properties file.
	 *
	 * @author Flemming N. Larsen
	 */
	private class SortedProperties extends Properties {
		private static final long serialVersionUID = 1L;

		@SuppressWarnings("unchecked")
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
