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
import net.sf.robocode.host.ICpuManager;
import net.sf.robocode.host.IHostManager;
import net.sf.robocode.io.FileUtil;
import net.sf.robocode.io.Logger;
import static net.sf.robocode.io.Logger.logError;
import net.sf.robocode.repository.IRepositoryManager;
import net.sf.robocode.settings.RobocodeProperties;
import net.sf.robocode.ui.IWindowManager;

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

	private RobocodeProperties properties;

	public RobocodeManager() {
	}


	public RobocodeProperties getProperties() {
		if (properties == null) {
			properties = Container.getComponent(RobocodeProperties.class);

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

}
