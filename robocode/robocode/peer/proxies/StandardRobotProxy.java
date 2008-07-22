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
	public void stop(boolean overwrite) {
        setStopImpl(overwrite);
        execute();
	}

	public void resume() {
        setResumeImpl();
        execute();
	}

	public void rescan() {
		peer.rescan();
	}

	public void turnRadar(double radians) {
        setTurnRadarImpl(radians);
        do {
            execute(); // Always tick at least once
        } while (getRadarTurnRemaining() != 0);
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

    private void setStopImpl(boolean overwrite){
        peer.setStop(overwrite);
    }

    private void setResumeImpl(){
        peer.setResume();
    }
}
