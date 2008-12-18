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


import robocode.RobocodeFileOutputStream;
import robocode.io.FileUtil;
import robocode.io.Logger;
import static robocode.io.Logger.logError;
import robocode.recording.IRecordManager;
import robocode.recording.RecordManager;
import robocode.security.RobocodeSecurityManager;
import robocode.security.RobocodeSecurityPolicy;
import robocode.security.SecureInputStream;
import robocode.security.SecurePrintStream;
import robocode.sound.ISoundManager;
import robocode.sound.SoundManager;

import java.io.*;
import java.security.Policy;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Nathaniel Troutman (contributor)
 */
public class RobocodeManager {
	private IBattleManager battleManager;
	private ICpuManager cpuManager;
	private IImageManager imageManager;
	private IRobotDialogManager robotDialogManager;
	private IRepositoryManager repositoryManager;
	private IThreadManager threadManager;
	private IWindowManager windowManager;
	private IVersionManager versionManager;
	private ISoundManager soundManager;
	private IRecordManager recordManager;
	private IHostManager hostManager;

	private final boolean slave;

	private RobocodeProperties properties;

	private boolean isGUIEnabled = true;
	private boolean isSoundEnabled = true;

	static {
		RobocodeManager.initStreams();
	}

	public RobocodeManager(boolean slave) {
		this.slave = slave;
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
	public IRepositoryManager getRobotRepositoryManager() {
		if (repositoryManager == null) {
			repositoryManager = new RobotRepositoryManager(this);
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

	/**
	 * Gets the threadManager.
	 *
	 * @return Returns a ThreadManager
	 */
	public IThreadManager getThreadManager() {
		if (threadManager == null) {
			threadManager = new ThreadManager();
		}
		return threadManager;
	}

	/**
	 * Gets the robotDialogManager.
	 *
	 * @return Returns a RobotDialogManager
	 */
	public IRobotDialogManager getRobotDialogManager() {
		if (robotDialogManager == null) {
			robotDialogManager = new RobotDialogManager(this);
		}
		return robotDialogManager;
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
	 * Gets the imageManager.
	 *
	 * @return Returns a ImageManager
	 */
	public IImageManager getImageManager() {
		if (imageManager == null) {
			imageManager = new ImageManager(this);
		}
		return imageManager;
	}

	/**
	 * Gets the versionManager.
	 *
	 * @return Returns a VersionManager
	 */
	public IVersionManager getVersionManager() {
		if (versionManager == null) {
			versionManager = new VersionManager(this);
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

	private static void initStreams() {
		PrintStream sysout = new SecurePrintStream(System.out, true);
		PrintStream syserr = new SecurePrintStream(System.err, true);
		InputStream sysin = new SecureInputStream(System.in);

		System.setOut(sysout);
		if (!System.getProperty("debug", "false").equals("true")) {
			System.setErr(syserr);
		}
		System.setIn(sysin);
	}

	public void initSecurity(boolean securityOn, boolean experimentalOn) {
		Thread.currentThread().setName("Application Thread");

		RobocodeSecurityPolicy securityPolicy = new RobocodeSecurityPolicy(Policy.getPolicy());

		Policy.setPolicy(securityPolicy);

		RobocodeSecurityManager securityManager = new RobocodeSecurityManager(Thread.currentThread(), getThreadManager(),
				securityOn, experimentalOn);

		System.setSecurityManager(securityManager);

		RobocodeFileOutputStream.setThreadManager(getThreadManager());
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
}
