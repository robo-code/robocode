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
import robocode.peer.robot.EventManager;
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
        peer.setStop(overwrite);
        execute();
	}

	public final void resume() {
        peer.setResume();
        execute();
	}

	public final void rescan() {
        boolean reset = false;
        boolean resetValue = false;
        final EventManager eventManager = peer.getEventManager();
        
        if (eventManager.getCurrentTopEventPriority() == eventManager.getScannedRobotEventPriority()) {
            reset = true;
            resetValue = eventManager.getInterruptible(eventManager.getScannedRobotEventPriority());
            eventManager.setInterruptible(eventManager.getScannedRobotEventPriority(), true);
        }

        peer.setScan(true);
        execute();
        if (reset) {
            eventManager.setInterruptible(eventManager.getScannedRobotEventPriority(), resetValue);
        }
	}

	public final void turnRadar(double radians) {
        peer.setTurnRadar(radians);
        do {
            execute(); // Always tick at least once
        } while (peer.getRadarTurnRemaining() != 0);
	}

	// fast setters
	public final void setAdjustGunForBodyTurn(boolean newAdjustGunForBodyTurn) {
		setCall();
		peer.setAdjustGunForBodyTurn(newAdjustGunForBodyTurn);
	}

	public final void setAdjustRadarForGunTurn(boolean newAdjustRadarForGunTurn) {
		setCall();
		peer.setAdjustRadarForGunTurn(newAdjustRadarForGunTurn);
	}

	public final void setAdjustRadarForBodyTurn(boolean newAdjustRadarForBodyTurn) {
		setCall();
		peer.setAdjustRadarForBodyTurn(newAdjustRadarForBodyTurn);
	}
}
