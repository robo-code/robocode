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
 *     - Totally rewritten so that access to other managers is done thru the
 *       individual manager and so access to all methods is now static
 *     - Added setEnableGUI() and isGUIEnabled()
 *******************************************************************************/
package robocode.manager;


import java.io.*;
import robocode.util.*;
import robocode.control.*;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (current)
 */
public class RobocodeManager {

	private static boolean slave;

	private static boolean isGUIEnabled = true;
	private static boolean isSoundEnabled = true;

	private static RobocodeListener listener;

	public static void setSlave(boolean isSlave) {
		RobocodeManager.slave = isSlave;
	}

	public static boolean isSlave() {
		return slave;
	}

	public static void setListener(RobocodeListener listener) {
		RobocodeManager.listener = listener;
	}

	public static RobocodeListener getListener() {
		return listener;
	}

	public static void runIntroBattle() {
		BattleManager.setBattleFilename(new File(Constants.cwd(), "battles/intro.battle").getPath());
		BattleManager.loadBattleProperties();

		setListener(new RobocodeListener() {
			public void battleMessage(String s) {}

			public void battleComplete(BattleSpecification b, RobotResults c[]) {
				setListener(null);
				WindowManager.getRobocodeFrame().clearRobotButtons();
			}

			public void battleAborted(BattleSpecification b) {
				setListener(null);
				WindowManager.getRobocodeFrame().clearRobotButtons();
			}
		});

		BattleManager.startNewBattle(BattleManager.getBattleProperties(), false);
		BattleManager.clearBattleProperties();
	}

	public static boolean isGUIEnabled() {
		return isGUIEnabled;
	}

	public static void setEnableGUI(boolean enable) {
		isGUIEnabled = enable;
	}

	public static boolean isSoundEnabled() {
		return isSoundEnabled && RobocodeProperties.getOptionsSoundEnableSound();
	}

	public static void setEnableSound(boolean enable) {
		isSoundEnabled = enable;
	}
}
