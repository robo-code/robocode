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
 *     - Renamed 'enum' variables to allow compiling with Java 1.5
 *     - Replaced RobotPeerVector with plain Vector
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
	Hashtable robotDialogHashtable = new Hashtable(); // <String, RobotDialog>
	private RobocodeManager manager;

	public RobotDialogManager(RobocodeManager manager) {
		super();
		this.manager = manager;
	}

	public void setActiveBattle(Battle b) {
		Vector v = b.getRobots(); // <RobotPeer>
		Enumeration keys = robotDialogHashtable.keys();

		while (keys.hasMoreElements()) {
			String name = (String) keys.nextElement();
			RobotPeer r;
			boolean found = false;

			for (int i = 0; i < v.size(); i++) {
				r = (RobotPeer) v.elementAt(i);
				if (r.getName().equals(name)) {
					found = true;
					break;
				}
			}
			if (!found) {
				RobotDialog o = (RobotDialog) robotDialogHashtable.get(name);

				robotDialogHashtable.remove(o);
				o.dispose();
			}
		}
	}

	public void reset() {
		Enumeration keys = robotDialogHashtable.keys();

		while (keys.hasMoreElements()) {
			String name = (String) keys.nextElement();
			RobotDialog o = (RobotDialog) robotDialogHashtable.get(name);

			if (!o.isVisible()) {
				robotDialogHashtable.remove(o);
				o.dispose();
			}
		}
	}

	public RobotDialog getRobotDialog(String robotName, boolean create) {
		RobotDialog d = (RobotDialog) robotDialogHashtable.get(robotName);

		if (d == null && create == true) {
			if (robotDialogHashtable.size() > 10) {
				reset();
			}
			d = new RobotDialog(manager.isSlave());
			robotDialogHashtable.put(robotName, d);
		}
		return d;
	}
}
