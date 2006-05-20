/*******************************************************************************
 * Copyright (c) 2001 Mathew Nelson
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew Nelson - initial API and implementation
 *******************************************************************************/
package robocode.manager;


import java.io.*;

import robocode.util.*;


public class RobocodeManager {
	private BattleManager battleManager = null;
	private CpuManager cpuManager = null;
	private ImageManager imageManager = null;
	private RobotDialogManager robotDialogManager = null;
	private RobotRepositoryManager robotRepositoryManager = null;
	private ThreadManager threadManager = null;
	private WindowManager windowManager = null;
	private VersionManager versionManager = null;
	private BrowserManager browserManager = null;
	
	private boolean slave = false;
	
	private RobocodeProperties properties = null;
	private robocode.control.RobocodeListener listener = null;
	
	private void log(String s) {
		Utils.log(s);
	}

	private void log(Throwable e) {
		Utils.log(e);
	}

	// Must use slave constructor
	private RobocodeManager() {}
	
	public RobocodeManager(boolean slave, robocode.control.RobocodeListener listener) {
		this.slave = slave;
		this.listener = listener;
	}
		
	/**
	 * Gets the battleManager.
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
	 * @return Returns a RobotDialogManager
	 */
	public RobotDialogManager getRobotDialogManager() {
		if (robotDialogManager == null) {
			robotDialogManager = new RobotDialogManager(this);
		}
		return robotDialogManager;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/18/2001 3:43:41 PM)
	 */
	public RobocodeProperties getProperties() {
		if (properties == null) {
			properties = new RobocodeProperties(this);
			try {
				FileInputStream in = new FileInputStream(new File(Constants.cwd(), "robocode.properties"));

				properties.load(in);
			} catch (FileNotFoundException e) {
				log("No robocode.properties file, using defaults.");
			} catch (IOException e) {
				log("IO Exception reading robocode.properties" + e);
			}
		}
		return properties;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/18/2001 3:21:22 PM)
	 */
	public void saveProperties() {
		getBattleManager().setOptions();
		if (properties == null) {
			log("Cannot save null robocode properties");
			return;
		}
		try {
			FileOutputStream out = new FileOutputStream(new File(Constants.cwd(), "robocode.properties"));

			properties.store(out, "Robocode Properties");
		} catch (IOException e) {
			log(e);
		}
	
	}

	/**
	 * Gets the imageManager.
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
	 * @return Returns a boolean
	 */
	public boolean isSlave() {
		return slave;
	}
	
	public robocode.control.RobocodeListener getListener() {
		return listener;
	}
	
	public void setListener(robocode.control.RobocodeListener listener) {
		this.listener = listener;
	}
	
	public void runIntroBattle() {
		getBattleManager().setBattleFilename(new File(Constants.cwd(), "battles/intro.battle").getPath());
		getBattleManager().loadBattleProperties();
		setListener(new robocode.control.RobocodeListener() {
			public void battleMessage(String s) {}

			public void battleComplete(robocode.control.BattleSpecification b, robocode.control.RobotResults c[]) {
				setListener(null);
				getWindowManager().getRobocodeFrame().clearRobotButtons();
			}

			public void battleAborted(robocode.control.BattleSpecification b) {
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

