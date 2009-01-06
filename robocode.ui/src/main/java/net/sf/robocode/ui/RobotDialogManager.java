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
 *     - Renamed 'enum' variables to allow compiling with Java 1.5
 *     - Replaced RobotPeerVector with plain Vector
 *     - Ported to Java 5.0
 *     - Fixed possible ConcurrentModificationException issues
 *     - Bugfixed setActiveBattle() and reset() which removed dialogs via
 *       remove(dialog) instead of remove(name)
 *     Robert D. Maupin
 *     - Replaced old collection types like Vector and Hashtable with
 *       synchronized List and HashMap
 *******************************************************************************/
package net.sf.robocode.ui;


import net.sf.robocode.core.Container;
import net.sf.robocode.ui.dialog.*;
import robocode.control.snapshot.IRobotSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author Mathew A. Nelson (orinal)
 * @author Flemming N. Larsen (contributor)
 * @author Robert D. Maupin (contributor)
 */
public class RobotDialogManager implements IRobotDialogManager {

	public static final int MAX_PRE_ATTACHED = 25;

	private final Map<String, RobotDialog> robotDialogMap = new ConcurrentHashMap<String, RobotDialog>();
	private BattleDialog battleDialog = null;

	public RobotDialogManager() {
		super();
	}

	public void trim(List<IRobotSnapshot> robots) {

		// new ArrayList in order to prevent ConcurrentModificationException
		for (String name : new ArrayList<String>(robotDialogMap.keySet())) {
			boolean found = false;

			for (IRobotSnapshot robot : robots) {
				if (robot.getName().equals(name)) {
					found = true;
					break;
				}
			}
			if (!found) {
				RobotDialog dialog = robotDialogMap.get(name);

				robotDialogMap.remove(name);
				dialog.dispose();
				dialog.detach();
			}
		}
	}

	public void reset() {
		for (String name : robotDialogMap.keySet()) {
			RobotDialog dialog = robotDialogMap.get(name);

			if (!dialog.isVisible()) {
				robotDialogMap.remove(name);
				dialog.detach();
				dialog.dispose();
			}
		}
	}

	public RobotDialog getRobotDialog(RobotButton robotButton, String name, boolean create) {
		RobotDialog robotDialog = robotDialogMap.get(name);

		if (create && robotDialog == null) {
			if (robotDialogMap.size() > MAX_PRE_ATTACHED) {
				reset();
			}
			robotDialog = Container.createComponent(RobotDialog.class);
			robotDialog.setup(robotButton);
			robotDialog.pack();
			WindowUtil.place(robotDialog);
			robotDialogMap.put(name, robotDialog);
		}
		return robotDialog;
	}

	public BattleDialog getBattleDialog(BattleButton battleButton, boolean create) {

		if (create && battleDialog == null) {
			battleDialog = Container.getComponent(BattleDialog.class);
			battleDialog.pack();
			WindowUtil.place(battleDialog);
		}
		return battleDialog;
	}

}
