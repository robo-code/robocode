/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.ui;


import net.sf.robocode.gui.IWindowManagerBase;
import robocode.control.events.IBattleListener;
import robocode.control.snapshot.ITurnSnapshot;

import javax.swing.*;


/**
 * @author Pavel Savara (original)
 */
public interface IWindowManager extends IWindowManagerBase {
	void init();

	void setEnableGUI(boolean enable);

	boolean isGUIEnabled();

	boolean isSlave();

	boolean isShowResultsEnabled();

	boolean isIconified();

	void setSlave(boolean slave);

	void showRobocodeFrame(boolean visible, boolean iconified);

	void showSplashScreen();

	void cleanup();

	JFrame getRobocodeFrame();

	void setBusyPointer(boolean enabled);

	void setStatus(String s);

	ITurnSnapshot getLastSnapshot();

	void addBattleListener(IBattleListener listener);

	void removeBattleListener(IBattleListener listener);

	void runIntroBattle();
}
