/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package robocode.ui;


import robocode.peer.RobotPeer;

import java.io.File;


/**
 * @author Pavel Savara (original)
 */
public interface IRobocodeFrame {
	void setStatus(String status);
	void messageError(String message);
	void messageWarning(String message);

	boolean isIconified();
	void setIconified(boolean value);
	void clearRobotButtons();
	void addRobotButton(IRobotDialogManager robotDialogManager, RobotPeer robotPeer);
	void validate();
    void setTitle(String title);
	void setEnableStopButton(boolean value);
	void setEnableReplayButton(boolean value);
	void setEnableRestartButton(boolean value);
	void setEnableBattleSaveAsMenuItem(boolean value);
	void setEnableBattleSaveMenuItem(boolean value);
	IBattleView getBattleView();
	String saveBattleDialog(String file);
	void dispose();
}
