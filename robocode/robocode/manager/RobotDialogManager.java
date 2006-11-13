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
 *     - Totally rewritten for Java 5 and made static
 *******************************************************************************/
package robocode.manager;


import java.util.*;

import robocode.battle.*;
import robocode.peer.RobotPeer;
import robocode.dialog.*;


/**
 * @author Mathew A. Nelson (orinal)
 * @author Flemming N. Larsen (current)
 */
public class RobotDialogManager {

	private static Hashtable<String, RobotDialog> robotDialogHashtable = new Hashtable<String, RobotDialog>(); 

	public static void setActiveBattle(Battle b) {
		for (String name : robotDialogHashtable.keySet()) {
			boolean found = false;

			for (RobotPeer r : b.getRobots()) {
				if (r.getName().equals(name)) {
					found = true;
					break;
				}
			}
			if (!found) {
				RobotDialog dialog = robotDialogHashtable.get(name);

				robotDialogHashtable.remove(dialog);
				dialog.dispose();
			}
		}
	}

	public static void reset() {
		for (String name : robotDialogHashtable.keySet()) {
			RobotDialog dialog = robotDialogHashtable.get(name);

			if (!dialog.isVisible()) {
				robotDialogHashtable.remove(dialog);
				dialog.dispose();
			}
		}
	}

	public static RobotDialog getRobotDialog(String robotName, boolean create) {
		RobotDialog dialog = robotDialogHashtable.get(robotName);

		if (create && dialog == null) {
			if (robotDialogHashtable.size() > 10) {
				reset();
			}
			dialog = new RobotDialog(RobocodeManager.isSlave());
			robotDialogHashtable.put(robotName, dialog);
		}
		return dialog;
	}
}
