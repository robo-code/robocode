/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
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
