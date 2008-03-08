/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package robocode.peer;

import robocode.exception.DeathException;
import robocode.exception.WinException;
import robocode.exception.DisabledException;
import static robocode.io.Logger.log;
import robocode.robotinterfaces.peer.IBasicRobotPeer;

import java.security.AccessControlException;

/**
 * @author Pavel Savara (original)
 */
public class R70obotPeerRobot extends R60obotPeerRobotCommands {

    public long getTime() {
        return getBattle().getCurrentTime();
    }

	public int getOthers() {
		return getBattle().getActiveRobots() - (info.isAlive() ? 1 : 0);
	}

    public int getNumRounds() {
        return getBattle().getNumRounds();
    }

    public int getRoundNum() {
        return getBattle().getRoundNum();
    }

    public double getGunCoolingRate() {
        return getBattle().getGunCoolingRate();
    }
    
}
