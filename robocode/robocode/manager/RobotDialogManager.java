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
package robocode.manager;


import robocode.battle.IRobotControl;
import robocode.dialog.RobotDialog;

import java.util.*;


/**
 * @author Mathew A. Nelson (orinal)
 * @author Flemming N. Larsen (contributor)
 * @author Robert D. Maupin (contributor)
 */
public class RobotDialogManager {

	private Map<IRobotControl, RobotDialog> robotDialogMap = new HashMap<IRobotControl, RobotDialog>();
	private RobocodeManager manager;

	public RobotDialogManager(RobocodeManager manager) {
		super();
		this.manager = manager;
	}

	public void initialize() {
		List<IRobotControl> robotControls = manager.getBattleManager().getRobotControls();

		// new ArrayList in order to prevent ConcurrentModificationException
		for (IRobotControl robotControlInMap : new ArrayList<IRobotControl>(robotDialogMap.keySet())) {
			boolean found = false;

			for (IRobotControl robotControl : robotControls) {
				if (robotControl.getName().equals(robotControlInMap.getName())) {
					found = true;
					break;
				}
			}
			if (!found) {
				RobotDialog dialog = robotDialogMap.get(robotControlInMap);

				robotDialogMap.remove(robotControlInMap);
				dialog.dispose();
			}
		}
	}

	public void reset() {
		for (IRobotControl robotControl : robotDialogMap.keySet()) {
			RobotDialog dialog = robotDialogMap.get(robotControl);

			if (!dialog.isVisible()) {
				robotDialogMap.remove(robotControl);
				dialog.dispose();
			}
		}
	}

	public RobotDialog getRobotDialog(IRobotControl robotControl, boolean create) {
		RobotDialog dialog = robotDialogMap.get(robotControl);

		if (create && dialog == null) {
			if (robotDialogMap.size() > 10) {
				reset();
			}
			dialog = new RobotDialog(manager);
			robotDialogMap.put(robotControl, dialog);
		}
		return dialog;
	}
}
