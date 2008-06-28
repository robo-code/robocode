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


import robocode.battle.snapshot.RobotSnapshot;
import robocode.dialog.RobotButton;
import robocode.dialog.RobotDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author Mathew A. Nelson (orinal)
 * @author Flemming N. Larsen (contributor)
 * @author Robert D. Maupin (contributor)
 */
public class RobotDialogManager {

	public static final int MAX_PRE_ATTACHED = 10;

	private Map<String, RobotDialog> robotDialogMap = new ConcurrentHashMap<String, RobotDialog>();
	private RobocodeManager manager;

	public RobotDialogManager(RobocodeManager manager) {
		super();
		this.manager = manager;
	}

	public void trim(List<RobotSnapshot> robots) {

		// new ArrayList in order to prevent ConcurrentModificationException
		for (String name : new ArrayList<String>(robotDialogMap.keySet())) {
			boolean found = false;

			for (RobotSnapshot robot : robots) {
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
		RobotDialog dialog = robotDialogMap.get(name);

		if (create && dialog == null) {
			if (robotDialogMap.size() > MAX_PRE_ATTACHED) {
				reset();
			}
			dialog = new RobotDialog(manager, robotButton);
			robotDialogMap.put(name, dialog);
		}
		return dialog;
	}
}
