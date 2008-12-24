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
 *     - Code cleanup
 *     - Added setEnableGUI() and isGUIEnabled()
 *     - Updated to use methods from FileUtil and Logger, which replaces methods
 *       that have been (re)moved from the robocode.util.Utils class
 *     - Added access for the SoundManager
 *     - Changed to use FileUtil.getRobocodeConfigFile()
 *     - Added missing close() on FileInputStream and FileOutputStream
 *     - Bugfix: When the intro battle has completed or is aborted the battle
 *       properties are now reset to the default battle properties. This fixes
 *       the issue were the initial robot positions are fixed in new battles
 *     Nathaniel Troutman
 *     - Bugfix: Added cleanup() to prevent memory leaks by removing circular
 *       references
 *     Pavel Savara
 *     - uses interfaces of components
 *******************************************************************************/
package robocode.manager;


import net.sf.robocode.IRobocodeManager;
import net.sf.robocode.battle.IBattleManager;
import net.sf.robocode.battle.BattleManager;
import net.sf.robocode.host.HostManager;
import net.sf.robocode.host.ICpuManager;
import net.sf.robocode.host.IHostManager;
import net.sf.robocode.host.CpuManager;
import net.sf.robocode.io.FileUtil;
import net.sf.robocode.io.Logger;
import static net.sf.robocode.io.Logger.logError;
import net.sf.robocode.manager.IRobocodeManagerBase;
import net.sf.robocode.recording.IRecordManager;
import net.sf.robocode.recording.RecordManager;
import net.sf.robocode.repository.IRepositoryManager;
import net.sf.robocode.repository.RepositoryManager;
import net.sf.robocode.security.HiddenAccess;
import net.sf.robocode.settings.RobocodeProperties;
import net.sf.robocode.sound.ISoundManager;
import net.sf.robocode.ui.IWindowManager;
import net.sf.robocode.version.IVersionManager;
import net.sf.robocode.version.VersionManager;
import robocode.sound.SoundManager;

import java.io.*;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Nathaniel Troutman (contributor)
 */
class RobocodeManager implements IRobocodeManager {
	private IBattleManager battleManager;
	private ICpuManager cpuManager;
	private IRepositoryManager repositoryManager;
	private IWindowManager windowManager;
	private IVersionManager versionManager;
	private ISoundManager soundManager;
	private IRecordManager recordManager;
	private IHostManager hostManager;

	private final boolean slave;

	private RobocodeProperties properties;

	private boolean isGUIEnabled = true;
	private boolean isSoundEnabled = true;

	public RobocodeManager(boolean slave) {
		this.slave = slave;
		HiddenAccess.manager = this;
		getHostManager();
	}

	/**
	 * Gets the battleManager.
	 *
	 * @return Returns a BattleManager
	 */
	public IBattleManager getBattleManager() {
		if (battleManager == null) {
			battleManager = new BattleManager(this);
		}
		return battleManager;
	}

	public IHostManager getHostManager() {
		if (hostManager == null) {
			hostManager = new HostManager(this);
		}
		return hostManager;
	}

	/**
	 * Gets the robotManager.
	 *
	 * @return Returns a RobotListManager
	 */
	public IRepositoryManager getRepositoryManager() {
		if (repositoryManager == null) {
			repositoryManager = new RepositoryManager(this);
		}
		return repositoryManager;
	}

	/**
	 * Gets the windowManager.
	 *
	 * @return Returns a WindowManager
	 */
	public IWindowManager getWindowManager() {
		if (windowManager == null) {
			windowManager = new WindowManager(this);
		}
		return windowManager;
	}

	public RobocodeProperties getProperties() {
		if (properties == null) {
			properties = new RobocodeProperties(this);

			FileInputStream in = null;

			try {
				in = new FileInputStream(FileUtil.getRobocodeConfigFile());
				properties.load(in);
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
		return properties;
	}

	public void saveProperties() {
		if (properties == null) {
			logError("Cannot save null robocode properties");
			return;
		}
		FileOutputStream out = null;

		try {
			out = new FileOutputStream(FileUtil.getRobocodeConfigFile());

			properties.store(out, "Robocode Properties");
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
	 * Gets the versionManager.
	 *
	 * @return Returns a VersionManager
	 */
	public IVersionManager getVersionManager() {
		if (versionManager == null) {
			versionManager = new VersionManager();
		}
		return versionManager;
	}

	/**
	 * Gets the cpuManager.
	 *
	 * @return Returns a CpuManager
	 */
	public ICpuManager getCpuManager() {
		if (cpuManager == null) {
			cpuManager = new CpuManager(this);
		}
		return cpuManager;
	}

	/**
	 * Gets the Sound Manager.
	 *
	 * @return Returns a SoundManager
	 */
	public ISoundManager getSoundManager() {
		if (soundManager == null) {
			soundManager = new SoundManager(this);
		}
		return soundManager;
	}

	/**
	 * Gets the Battle Recoder.
	 *
	 * @return Returns a BattleRecoder
	 */
	public IRecordManager getRecordManager() {
		if (recordManager == null) {
			recordManager = new RecordManager(this);
		}
		return recordManager;
	}

	/**
	 * Gets the slave.
	 *
	 * @return Returns a boolean
	 */
	public boolean isSlave() {
		return slave;
	}

	public boolean isGUIEnabled() {
		return isGUIEnabled;
	}

	public void setEnableGUI(boolean enable) {
		isGUIEnabled = enable;
	}

	public boolean isSoundEnabled() {
		return isSoundEnabled && getProperties().getOptionsSoundEnableSound();
	}

	public void setEnableSound(boolean enable) {
		isSoundEnabled = enable;
	}

	public void cleanup() {
		if (battleManager != null) {
			battleManager.cleanup();
			battleManager = null;
		}
		if (hostManager != null) {
			hostManager.cleanup();
			hostManager = null;
		}
	}

	public void setVisibleForRobotEngine(boolean visible) {
		if (visible && !isGUIEnabled()) {
			// The GUI must be enabled in order to show the window
			setEnableGUI(true);

			// Set the Look and Feel (LAF)
			robocode.manager.LookAndFeelManager.setLookAndFeel();
		}

		if (isGUIEnabled()) {
			getWindowManager().showRobocodeFrame(visible, false);
			getProperties().setOptionsCommonShowResults(visible);
		}
	}

	public static IRobocodeManager createRobocodeManager() {
		return new RobocodeManager(false);
	}

	public static IRobocodeManagerBase createRobocodeManagerForRobotEngine(File robocodeHome) {
		try {
			if (robocodeHome == null) {
				robocodeHome = FileUtil.getCwd();
			}
			FileUtil.setCwd(robocodeHome);

			File robotsDir = FileUtil.getRobotsDir();

			if (robotsDir == null) {
				throw new RuntimeException("No valid robot directory is specified");
			} else if (!(robotsDir.exists() && robotsDir.isDirectory())) {
				throw new RuntimeException('\'' + robotsDir.getAbsolutePath() + "' is not a valid robot directory");
			}

		} catch (IOException e) {
			System.err.println(e);
			return null;
		}

		RobocodeManager manager = new RobocodeManager(true);

		manager.setEnableGUI(false);
		manager.getHostManager().initSecurity(true, System.getProperty("EXPERIMENTAL", "false").equals("true"));

		return manager;
	}
}
