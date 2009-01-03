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
package net.sf.robocode.core;


import net.sf.robocode.IRobocodeManager;
import net.sf.robocode.battle.IBattleManager;
import net.sf.robocode.battle.IBattleManagerBase;
import net.sf.robocode.host.ICpuManager;
import net.sf.robocode.host.IHostManager;
import net.sf.robocode.io.FileUtil;
import net.sf.robocode.io.Logger;
import static net.sf.robocode.io.Logger.logError;
import net.sf.robocode.manager.IVersionManagerBase;
import net.sf.robocode.repository.IRepositoryManager;
import net.sf.robocode.repository.IRepositoryManagerBase;
import net.sf.robocode.security.HiddenAccess;
import net.sf.robocode.settings.RobocodeProperties;
import net.sf.robocode.ui.IWindowManager;
import net.sf.robocode.version.IVersionManager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Nathaniel Troutman (contributor)
 */
public final class RobocodeManager implements IRobocodeManager {
	private boolean slave;

	private RobocodeProperties properties;

	private boolean isGUIEnabled = true;
	private boolean isSoundEnabled = true;

	public RobocodeManager() {
		HiddenAccess.manager = this;
	}

	public void setSlave(boolean value) {
		slave = value;
	}

	public RobocodeProperties getProperties() {
		if (properties == null) {
			properties = Container.cache.getComponent(RobocodeProperties.class);

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

	public IVersionManagerBase getVersionManagerBase() {
		return Container.cache.getComponent(IVersionManager.class);
	}

	public IBattleManagerBase getBattleManagerBase() {
		return Container.cache.getComponent(IBattleManager.class);
	}

	public IRepositoryManagerBase getRepositoryManagerBase() {
		return Container.cache.getComponent(IRepositoryManager.class);
	}

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
		if (isGUIEnabled) {
			Container.cache.getComponent(IWindowManager.class).cleanup();
		}
		Container.cache.getComponent(IBattleManager.class).cleanup();
		Container.cache.getComponent(IHostManager.class).cleanup();
	}

	public void initForRobotEngine() {
		setSlave(true);
		setEnableGUI(false);
		final boolean experimental = System.getProperty("EXPERIMENTAL", "false").equals("true");

		Container.cache.getComponent(IHostManager.class).initSecurity(true, experimental);
		Container.cache.getComponent(ICpuManager.class).getCpuConstant();
		Container.cache.getComponent(IRepositoryManager.class).loadRobotRepository();
	}

	public void setVisibleForRobotEngine(boolean visible) {
		if (visible && !isGUIEnabled()) {
			// The GUI must be enabled in order to show the window
			setEnableGUI(true);

			// Set the Look and Feel (LAF)
			Container.cache.getComponent(IWindowManager.class).setLookAndFeel();
		}

		if (isGUIEnabled()) {
			Container.cache.getComponent(IWindowManager.class).showRobocodeFrame(visible, false);
			getProperties().setOptionsCommonShowResults(visible);
		}
	}
}
