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
 *******************************************************************************/
package robocode.manager;


import robocode.RobocodeFileOutputStream;
import robocode.battle.IBattleManager;
import robocode.io.FileUtil;
import robocode.io.Logger;
import static robocode.io.Logger.logError;
import robocode.security.RobocodeSecurityManager;
import robocode.security.RobocodeSecurityPolicy;
import robocode.security.SecureInputStream;
import robocode.security.SecurePrintStream;
import robocode.sound.SoundManager;

import java.io.*;
import java.security.Policy;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Nathaniel Troutman (contributor)
 */
public class RobocodeManager {
	private BattleManager battleManager;
	private CpuManager cpuManager;
	private ImageManager imageManager;
	private RobotDialogManager robotDialogManager;
	private RobotRepositoryManager robotRepositoryManager;
	private ThreadManager threadManager;
	private WindowManager windowManager;
	private VersionManager versionManager;
	private SoundManager soundManager;

	private boolean slave;

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

	/**
	 * Gets the robotManager.
	 *
	 * @return Returns a RobotListManager
	 */
	public RobotRepositoryManager getRobotRepositoryManager() {
		if (robotRepositoryManager == null) {
			robotRepositoryManager = new RobotRepositoryManager(this);
		}
		return robotRepositoryManager;
	}

	/**
	 * Gets the windowManager.
	 *
	 * @return Returns a WindowManager
	 */
	public WindowManager getWindowManager() {
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
	public ThreadManager getThreadManager() {
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
	public RobotDialogManager getRobotDialogManager() {
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
	public ImageManager getImageManager() {
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
	public VersionManager getVersionManager() {
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
	public CpuManager getCpuManager() {
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
	public SoundManager getSoundManager() {
		if (soundManager == null) {
			soundManager = new SoundManager(this);
		}
		return soundManager;
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
		PrintStream sysout = new SecurePrintStream(System.out, true, "System.out");
		PrintStream syserr = new SecurePrintStream(System.err, true, "System.err");
		InputStream sysin = new SecureInputStream(System.in, "System.in");

		// Secure System.in, System.err, System.out
		SecurePrintStream.realOut = System.out;
		SecurePrintStream.realErr = System.err;
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

		if (securityOn) {
			ThreadGroup tg = Thread.currentThread().getThreadGroup();

			while (tg != null) {
				securityManager.addSafeThreadGroup(tg);
				tg = tg.getParent();
			}
		}
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
	}
}
