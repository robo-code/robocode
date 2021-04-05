/**
 * Copyright (c) 2001-2021 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package tested.robots;

import robocode.AdvancedRobot;
import robocode.StatusEvent;

/**
 * @author Pavel Savara (original)
 */
public class Throwing extends AdvancedRobot {
    Double notThere = null;

    @Override
    public void run() {
        ahead(100);
    }

    @Override
    public void onStatus(StatusEvent e) {
        if (e.getTime() > 5) {
            // this will throw NullPointerException
            notThere++;
        }
    }
}
