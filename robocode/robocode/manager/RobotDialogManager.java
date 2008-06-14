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
import robocode.control.BattleSpecification;
import robocode.control.RobotSpecification;

import java.util.*;


/**
 * @author Mathew A. Nelson (orinal)
 * @author Flemming N. Larsen (contributor)
 * @author Robert D. Maupin (contributor)
 */
public class RobotDialogManager {

	private Map<String, RobotDialog> robotDialogMap = new HashMap<String, RobotDialog>();
	private RobocodeManager manager;

	public RobotDialogManager(RobocodeManager manager) {
		super();
		this.manager = manager;
	}

	public void initialize(RobotSpecification[] robots) {

		// new ArrayList in order to prevent ConcurrentModificationException
		for (String robotFullUniqueName : new ArrayList<String>(robotDialogMap.keySet())) {
			boolean found = false;

			for (RobotSpecification robot : robots) {
				if (robot.getUniqueFullClassNameWithVersion().equals(robotFullUniqueName)) {
					found = true;
					break;
				}
			}
			if (!found) {
				RobotDialog dialog = robotDialogMap.get(robotFullUniqueName);

				robotDialogMap.remove(robotFullUniqueName);
				dialog.dispose();
			}
		}
	}

	public void reset() {
		for (String robotFullUniqueName : robotDialogMap.keySet()) {
			RobotDialog dialog = robotDialogMap.get(robotFullUniqueName);

			if (!dialog.isVisible()) {
				robotDialogMap.remove(robotFullUniqueName);
				dialog.dispose();
			}
		}
	}

	public RobotDialog getRobotDialog(String robotFullUniqueName, boolean create) {
		RobotDialog dialog = robotDialogMap.get(robotFullUniqueName);

		if (create && dialog == null) {
			if (robotDialogMap.size() > 10) {
				reset();
			}
			dialog = new RobotDialog(manager);
			robotDialogMap.put(robotFullUniqueName, dialog);
		}
		return dialog;
	}
}
