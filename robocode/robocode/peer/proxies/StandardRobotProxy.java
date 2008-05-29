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


import robocode.Bullet;
import robocode.robotinterfaces.peer.IStandardRobotPeer;


/**
 * @author Pavel Savara (original)
 */
public class StandardRobotProxy extends BasicRobotProxy implements IStandardRobotPeer {

    private IStandardRobotPeer peer;

	public StandardRobotProxy(IStandardRobotPeer peer) {
		super(peer);
        this.peer=peer;
    }

	// blocking actions
	public void stop(boolean overwrite) {
		peer.stop(overwrite);
	}

	public void resume() {
		peer.resume();
	}

	public void rescan() {
		peer.rescan();
	}

	public void turnRadar(double radians) {
		peer.turnRadar(radians);
	}

	public Bullet fire(double power) {
		return peer.fire(power);
	}

	// fast setters
	public void setAdjustGunForBodyTurn(boolean newAdjustGunForBodyTurn) {
		peer.setCall();
		peer.setAdjustGunForBodyTurn(newAdjustGunForBodyTurn);
	}

	public void setAdjustRadarForGunTurn(boolean newAdjustRadarForGunTurn) {
		peer.setCall();
		peer.setAdjustRadarForGunTurn(newAdjustRadarForGunTurn);
	}

	public void setAdjustRadarForBodyTurn(boolean newAdjustRadarForBodyTurn) {
		peer.setCall();
		peer.setAdjustRadarForBodyTurn(newAdjustRadarForBodyTurn);
	}
}
