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
package net.sf.robocode.ui;


import net.sf.robocode.ui.dialog.BattleButton;
import net.sf.robocode.ui.dialog.BattleDialog;
import net.sf.robocode.ui.dialog.RobotButton;
import net.sf.robocode.ui.dialog.RobotDialog;
import robocode.control.snapshot.IRobotSnapshot;

import java.util.List;


/**
 * @author Pavel Savara (original)
 */
public interface IRobotDialogManager {
	void trim(List<IRobotSnapshot> robots);

	void reset();

	RobotDialog getRobotDialog(RobotButton robotButton, String name, boolean create);
	BattleDialog getBattleDialog(BattleButton battleButton, boolean create);
}
