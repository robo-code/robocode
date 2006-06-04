/*******************************************************************************
 * Copyright (c) 2001-2006 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Code cleanup
 *******************************************************************************/
package robocode.manager;


import java.io.*;
import robocode.util.*;
import robocode.control.*;


/**
 * @author Mathew A. Nelson (original)
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
	private BrowserManager browserManager;
	
	private boolean slave;
	
	private RobocodeProperties properties;
	private RobocodeListener listener;
	
	// Must use slave constructor
	private RobocodeManager() {}
	
	public RobocodeManager(boolean slave, RobocodeListener listener) {
		this.slave = slave;
		this.listener = listener;
	}
		
	/**
	 * Gets the battleManager.
	 * 
	 * @return Returns a BattleManager
	 */
	public BattleManager getBattleManager() {
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
			threadManager = new ThreadManager(this);
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
			try {
				FileInputStream in = new FileInputStream(new File(Constants.cwd(), "robocode.properties"));

				properties.load(in);
			} catch (FileNotFoundException e) {
				Utils.log("No robocode.properties file, using defaults.");
			} catch (IOException e) {
				Utils.log("IO Exception reading robocode.properties" + e);
			}
		}
		return properties;
	}

	public void saveProperties() {
		getBattleManager().setOptions();
		if (properties == null) {
			Utils.log("Cannot save null robocode properties");
			return;
		}
		try {
			FileOutputStream out = new FileOutputStream(new File(Constants.cwd(), "robocode.properties"));

			properties.store(out, "Robocode Properties");
		} catch (IOException e) {
			Utils.log(e);
		}
	}

	/**
	 * Gets the imageManager.
	 * 
	 * @return Returns a ImageManager
	 */
	public ImageManager getImageManager() {
		if (imageManager == null) {
			imageManager = new ImageManager();
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
	 * Gets the slave.
	 * 
	 * @return Returns a boolean
	 */
	public boolean isSlave() {
		return slave;
	}

	public RobocodeListener getListener() {
		return listener;
	}

	public void setListener(RobocodeListener listener) {
		this.listener = listener;
	}

	public void runIntroBattle() {
		getBattleManager().setBattleFilename(new File(Constants.cwd(), "battles/intro.battle").getPath());
		getBattleManager().loadBattleProperties();
		setListener(new RobocodeListener() {
			public void battleMessage(String s) {}

			public void battleComplete(BattleSpecification b, RobotResults c[]) {
				setListener(null);
				getWindowManager().getRobocodeFrame().clearRobotButtons();
			}

			public void battleAborted(BattleSpecification b) {
				setListener(null);
				getWindowManager().getRobocodeFrame().clearRobotButtons();
			}
		});
		getBattleManager().startNewBattle(getBattleManager().getBattleProperties(), false);
		getBattleManager().clearBattleProperties();
	}

	public BrowserManager getBrowserManager() {
		if (browserManager == null) {
			browserManager = new BrowserManager(this);
		}
		return browserManager;
	}
}
