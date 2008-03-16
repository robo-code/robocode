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


import robocode.peer.IRobotRobotPeer;
import robocode.peer.robot.IRobotEventManager;
import robocode.robotinterfaces.peer.IStandardRobotPeer;


/**
 * @author Pavel Savara (original)
 */
public class StandardRobotProxy extends BasicRobotProxy implements IStandardRobotPeer {

	public StandardRobotProxy(IRobotRobotPeer peer) {
		super(peer);
	}

	// blocking actions
	public void stop(boolean overwrite) {
		peer.checkNoLock();

		setStopImplementation(overwrite);
		execute();
	}

	public void resume() {
		peer.checkNoLock();

		setResumeImplementation();
		execute();
	}

	public void rescan() {
		peer.checkNoLock();

		boolean reset = false;
		boolean resetValue = false;

		IRobotEventManager robotEventManager = peer.getRobotEventManager();

		if (robotEventManager.getCurrentTopEventPriority() == robotEventManager.getScannedRobotEventPriority()) {
			reset = true;
			resetValue = robotEventManager.getInterruptible(robotEventManager.getScannedRobotEventPriority());
			robotEventManager.setInterruptible(robotEventManager.getScannedRobotEventPriority(), true);
		}

		status.setScanSync(true);
		execute();
		if (reset) {
			robotEventManager.setInterruptible(robotEventManager.getScannedRobotEventPriority(), resetValue);
		}
	}

	public void turnRadar(double radians) {
		peer.checkNoLock();

		setTurnRadarImplementation(radians);
		do {
			execute(); // Always tick at least once
		} while (commands.getRadarTurnRemainingSync() != 0);
	}

	// fast setters
	public void setAdjustGunForBodyTurn(boolean newAdjustGunForBodyTurn) {
		setCall();
		peer.lockWrite();
		try {
			commands.setAdjustGunForBodyTurn(newAdjustGunForBodyTurn);
		} finally {
			peer.unlockWrite();
		}
	}

	public void setAdjustRadarForGunTurn(boolean newAdjustRadarForGunTurn) {
		setCall();
		peer.lockWrite();
		try {
			commands.setAdjustRadarForGunTurn(newAdjustRadarForGunTurn);
		} finally {
			peer.unlockWrite();
		}
	}

	public void setAdjustRadarForBodyTurn(boolean newAdjustRadarForBodyTurn) {
		setCall();
		peer.lockWrite();
		try {
			commands.setAdjustRadarForBodyTurn(newAdjustRadarForBodyTurn);
		} finally {
			peer.unlockWrite();
		}
	}

	// // // //  // // // // // // // // // // // // // // // // // // // // // // // //
	// private implementations
	// // // //  // // // // // // // // // // // // // // // // // // // // // // // //

	protected final void setTurnRadarImplementation(double radians) {
		peer.lockWrite();
		try {
			commands.setRadarTurnRemaining(radians);
		} finally {
			peer.unlockWrite();
		}
	}

	protected final void setStopImplementation(boolean overwrite) {
		peer.lockWrite();
		try {
			if (!commands.isStopped() || overwrite) {
				commands.setSaveDistanceToGo(commands.getDistanceRemaining());
				commands.setSaveAngleToTurn(commands.getBodyTurnRemaining());
				commands.setSaveGunAngleToTurn(commands.getGunTurnRemaining());
				commands.setSaveRadarAngleToTurn(commands.getRadarTurnRemaining());
			}
			commands.setStopped(true);
			commands.setDistanceRemaining(0);
			commands.setBodyTurnRemaining(0);
			commands.setGunTurnRemaining(0);
			commands.setRadarTurnRemaining(0);
		} finally {
			peer.unlockWrite();
		}
	}

	protected final void setResumeImplementation() {
		peer.lockWrite();
		try {
			if (commands.isStopped()) {
				commands.setStopped(false);
				commands.setDistanceRemaining(commands.getSaveDistanceToGo());
				commands.setBodyTurnRemaining(commands.getSaveAngleToTurn());
				commands.setGunTurnRemaining(commands.getSaveGunAngleToTurn());
				commands.setRadarTurnRemaining(commands.getSaveRadarAngleToTurn());
			}
		} finally {
			peer.unlockWrite();
		}
	}

}
