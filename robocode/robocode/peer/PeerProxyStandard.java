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

import robocode.robotinterfaces.peer.IRobotPeerStandard;
import robocode.robotinterfaces.peer.IRobotPeer;

/**
 * @author Pavel Savara (original)
 */
public class PeerProxyStandard extends PeerProxy implements IRobotPeerStandard {

	public PeerProxyStandard(IRobotPeer peer) {
		super(peer);
	}
	//blocking actions
	public void stop(boolean overwrite) {
		((IRobotPeerStandard)peer).stop(overwrite);
	}

	public void resume() {
		((IRobotPeerStandard)peer).resume();
	}

	public void scanReset() {
		((IRobotPeerStandard)peer).scanReset();
	}

	public void turnRadar(double radians) {
		((IRobotPeerStandard)peer).turnRadar(radians);
	}

	//fast setters
	public void setAdjustGunForBodyTurn(boolean newAdjustGunForBodyTurn) {
		peer.setCall();
		((IRobotPeerStandard)peer).setAdjustGunForBodyTurn(newAdjustGunForBodyTurn);
	}

	public void setAdjustRadarForGunTurn(boolean newAdjustRadarForGunTurn) {
		peer.setCall();
		((IRobotPeerStandard)peer).setAdjustRadarForGunTurn(newAdjustRadarForGunTurn);
	}

	public void setAdjustRadarForBodyTurn(boolean newAdjustRadarForBodyTurn) {
		peer.setCall();
		((IRobotPeerStandard)peer).setAdjustRadarForBodyTurn(newAdjustRadarForBodyTurn);
	}
}
