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


import robocode.robotinterfaces.peer.IStandardRobotPeer;
import robocode.robotinterfaces.peer.IBasicRobotPeer;


/**
 * @author Pavel Savara (original)
 */
public class StandardRobotPeerProxy extends BasicRobotPeerProxy implements IStandardRobotPeer {

	public StandardRobotPeerProxy(IBasicRobotPeer peer) {
		super(peer);
	}

	// blocking actions
	public void stop(boolean overwrite) {
		((IStandardRobotPeer) peer).stop(overwrite);
	}

	public void resume() {
		((IStandardRobotPeer) peer).resume();
	}

	public void scanReset() {
		((IStandardRobotPeer) peer).scanReset();
	}

	public void turnRadar(double radians) {
		((IStandardRobotPeer) peer).turnRadar(radians);
	}

	// fast setters
	public void setAdjustGunForBodyTurn(boolean newAdjustGunForBodyTurn) {
		peer.setCall();
		((IStandardRobotPeer) peer).setAdjustGunForBodyTurn(newAdjustGunForBodyTurn);
	}

	public void setAdjustRadarForGunTurn(boolean newAdjustRadarForGunTurn) {
		peer.setCall();
		((IStandardRobotPeer) peer).setAdjustRadarForGunTurn(newAdjustRadarForGunTurn);
	}

	public void setAdjustRadarForBodyTurn(boolean newAdjustRadarForBodyTurn) {
		peer.setCall();
		((IStandardRobotPeer) peer).setAdjustRadarForBodyTurn(newAdjustRadarForBodyTurn);
	}
}
