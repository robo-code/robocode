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
 *     Robert D. Maupin
 *     - Replaced old collection types like Vector and Hashtable with
 *       synchronized List and HashMap
 *******************************************************************************/
package robocode.manager;


import java.util.*;

import robocode.battle.*;
import robocode.peer.RobotPeer;
import robocode.dialog.*;


/**
 * @author Mathew A. Nelson (orinal)
 * @author Flemming N. Larsen (contributor)
 * @author Robert D. Maupin (contributor)
 */
public class RobotDialogManager {

	private Map<String, RobotDialog> robotDialogHashMap = new HashMap<String, RobotDialog>(); 
	private RobocodeManager manager;

	public RobotDialogManager(RobocodeManager manager) {
		super();
		this.manager = manager;
	}

	public void setActiveBattle(Battle b) {
		List<RobotPeer> robots = b.getRobots();

		for (String name : robotDialogHashMap.keySet()) {
			boolean found = false;

			for (RobotPeer r : robots) {
				if (r.getName().equals(name)) {
					found = true;
					break;
				}
			}
			if (!found) {
				RobotDialog dialog = robotDialogHashMap.get(name);

				robotDialogHashMap.remove(dialog);
				dialog.dispose();
			}
		}
	}

	public void reset() {
		for (String name : robotDialogHashMap.keySet()) {
			RobotDialog dialog = robotDialogHashMap.get(name);

			if (!dialog.isVisible()) {
				robotDialogHashMap.remove(dialog);
				dialog.dispose();
			}
		}
	}

	public RobotDialog getRobotDialog(String robotName, boolean create) {
		RobotDialog dialog = robotDialogHashMap.get(robotName);

		if (create && dialog == null) {
			if (robotDialogHashMap.size() > 10) {
				reset();
			}
			dialog = new RobotDialog(manager.isSlave());
			robotDialogHashMap.put(robotName, dialog);
		}
		return dialog;
	}
}
