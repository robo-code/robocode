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
package robocode.peer.proxies;


import robocode.robotinterfaces.peer.IStandardRobotPeer;
import robocode.peer.RobotPeer;


/**
 * @author Pavel Savara (original)
 */
public class StandardRobotProxy extends BasicRobotProxy implements IStandardRobotPeer {

	public StandardRobotProxy(RobotPeer peer) {
		super(peer);
	}

	// blocking actions
	public void stop(boolean overwrite) {
        peer.setStop(overwrite);
        execute();
	}

	public void resume() {
        peer.setResume();
        execute();
	}

	public void rescan() {
		peer.rescan();
	}

	public void turnRadar(double radians) {
        peer.setTurnRadar(radians);
        do {
            execute(); // Always tick at least once
        } while (getRadarTurnRemaining() != 0);
	}

	// fast setters
	public void setAdjustGunForBodyTurn(boolean newAdjustGunForBodyTurn) {
		setCall();
		peer.setAdjustGunForBodyTurn(newAdjustGunForBodyTurn);
	}

	public void setAdjustRadarForGunTurn(boolean newAdjustRadarForGunTurn) {
		setCall();
		peer.setAdjustRadarForGunTurn(newAdjustRadarForGunTurn);
	}

	public void setAdjustRadarForBodyTurn(boolean newAdjustRadarForBodyTurn) {
		setCall();
		peer.setAdjustRadarForBodyTurn(newAdjustRadarForBodyTurn);
	}
}
