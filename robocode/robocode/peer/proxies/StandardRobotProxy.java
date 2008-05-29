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


import robocode.peer.RobotPeer;
import robocode.robotinterfaces.peer.IStandardRobotPeer;


/**
 * @author Pavel Savara (original)
 */
public class StandardRobotProxy extends BasicRobotProxy implements IStandardRobotPeer {

	public StandardRobotProxy(RobotPeer peer) {
		super(peer);
    }

	// blocking actions
	public final void stop(boolean overwrite) {
		peer.stop(overwrite);
	}

	public final void resume() {
		peer.resume();
	}

	public final void rescan() {
		peer.rescan();
	}

	public final void turnRadar(double radians) {
		peer.turnRadar(radians);
	}

	// fast setters
	public final void setAdjustGunForBodyTurn(boolean newAdjustGunForBodyTurn) {
		peer.setCall();
		peer.setAdjustGunForBodyTurn(newAdjustGunForBodyTurn);
	}

	public final void setAdjustRadarForGunTurn(boolean newAdjustRadarForGunTurn) {
		peer.setCall();
		peer.setAdjustRadarForGunTurn(newAdjustRadarForGunTurn);
	}

	public final void setAdjustRadarForBodyTurn(boolean newAdjustRadarForBodyTurn) {
		peer.setCall();
		peer.setAdjustRadarForBodyTurn(newAdjustRadarForBodyTurn);
	}
}
