/*******************************************************************************
 * Copyright (c) 2001, 2007 Mathew A. Nelson and Robocode contributors
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

	private Hashtable<String, RobotDialog> robotDialogHashtable = new Hashtable<String, RobotDialog>(); 
	private RobocodeManager manager;

	public RobotDialogManager(RobocodeManager manager) {
		super();
		this.manager = manager;
	}

	public void setActiveBattle(Battle b) {
		Vector<RobotPeer> robots = b.getRobots();

		for (String name : robotDialogHashtable.keySet()) {
			boolean found = false;

			for (RobotPeer r : robots) {
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

	public void reset() {
		for (String name : robotDialogHashtable.keySet()) {
			RobotDialog dialog = robotDialogHashtable.get(name);

			if (!dialog.isVisible()) {
				robotDialogHashtable.remove(dialog);
				dialog.dispose();
			}
		}
	}

	public RobotDialog getRobotDialog(String robotName, boolean create) {
		RobotDialog dialog = robotDialogHashtable.get(robotName);

		if (create && dialog == null) {
			if (robotDialogHashtable.size() > 10) {
				reset();
			}
			dialog = new RobotDialog(manager.isSlave());
			robotDialogHashtable.put(robotName, dialog);
		}
		return dialog;
	}
}
