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
import robocode.battle.IRobotControl;
import robocode.dialog.RobotDialog;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author Mathew A. Nelson (orinal)
 * @author Flemming N. Larsen (contributor)
 * @author Robert D. Maupin (contributor)
 */
public class RobotDialogManager {

	private Map<String, RobotDialog> robotDialogMap = new ConcurrentHashMap<String, RobotDialog>();
	private RobocodeManager manager;

	public RobotDialogManager(RobocodeManager manager) {
		super();
		this.manager = manager;
	}

	public void initialize(List<RobotSnapshot> robots, List<IRobotControl> controls) {

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

        for(int index=0;index<robots.size();index++){
            final IRobotControl control = controls.get(index);
            final RobotSnapshot robot = robots.get(index);
            final String name = robot.getName();
            final RobotDialog robotDialog = getRobotDialog(name, true);
            robotDialog.reset(control, index, name);
            control.setPaintEnabled(robotDialog.isPaintEnabled());
            control.setSGPaintEnabled(robotDialog.isSGPaintEnabled());
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

	public RobotDialog getRobotDialog(String name, boolean create) {
		RobotDialog dialog = robotDialogMap.get(name);

		if (create && dialog == null) {
			if (robotDialogMap.size() > 10) {
				reset();
			}
			dialog = new RobotDialog(manager);
			robotDialogMap.put(name, dialog);
		}
		return dialog;
	}
}
