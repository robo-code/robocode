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
package robocodeui.manager;


import robocode.battle.Battle;
import robocode.manager.RobocodeManager;
import robocode.peer.proxies.IDisplayRobotProxy;
import robocode.ui.IRobotDialogManager;
import robocodeui.dialog.RobotDialog;

import java.util.*;


/**
 * @author Mathew A. Nelson (orinal)
 * @author Flemming N. Larsen (contributor)
 * @author Robert D. Maupin (contributor)
 */
public class RobotDialogManager implements IRobotDialogManager {

    private Map<String, RobotDialog> robotDialogHashMap = new HashMap<String, RobotDialog>();
    private RobocodeManager manager;

    public RobotDialogManager() {
        super();
    }

    public void setActiveBattle(Battle b) {
        List<IDisplayRobotProxy> robots = b.getDisplayRobots();

        Set<String> keys = new HashSet<String>(robotDialogHashMap.keySet());

        for (String name : keys) {
            boolean found = false;

            for (IDisplayRobotProxy robotView : robots) {
                if (robotView.getName().equals(name)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                RobotDialog dialog = robotDialogHashMap.get(name);

                robotDialogHashMap.remove(name);
                dialog.dispose();
            }
        }
    }

    public void reset() {
        Set<String> keys = new HashSet<String>(robotDialogHashMap.keySet());

        for (String name : keys) {
            RobotDialog dialog = robotDialogHashMap.get(name);

            if (!dialog.isVisible()) {
                robotDialogHashMap.remove(name);
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
            dialog = new RobotDialog(manager);
            robotDialogHashMap.put(robotName, dialog);
        }
        return dialog;
    }

    public void setRobocodeManager(RobocodeManager robocodeManager) {
        manager = robocodeManager;
    }
}
