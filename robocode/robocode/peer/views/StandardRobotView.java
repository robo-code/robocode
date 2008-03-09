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
package robocode.peer.views;


import robocode.peer.IRobotRobotPeer;
import robocode.peer.robot.IRobotEventManager;
import robocode.robotinterfaces.peer.IStandardRobotView;


/**
 * @author Pavel Savara (original)
 */
public class StandardRobotView extends BasicRobotView implements IStandardRobotView {

	public StandardRobotView(IRobotRobotPeer peer) {
		super(peer);
	}

	// blocking actions
	public void stop(boolean overwrite) {
        peer.checkNoLock();

        i_setStop(overwrite);
        execute();
	}

	public void resume() {
        peer.checkNoLock();

        i_setResume();
        execute();
	}

	public void scanReset() {
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

        i_setTurnRadar(radians);
        do {
            execute(); // Always tick at least once
        } while (commands.getRadarTurnRemainingSync() != 0);
	}

	// fast setters
	public void setAdjustGunForBodyTurn(boolean newAdjustGunForBodyTurn) {
		setCall();
        peer.lockWrite();
        try{
            commands.setAdjustGunForBodyTurn(newAdjustGunForBodyTurn);
        }
        finally{
            peer.unlockWrite();
        }
	}

	public void setAdjustRadarForGunTurn(boolean newAdjustRadarForGunTurn) {
		setCall();
        peer.lockWrite();
        try{
            commands.setAdjustRadarForGunTurn(newAdjustRadarForGunTurn);
        }
        finally{
            peer.unlockWrite();
        }
	}

	public void setAdjustRadarForBodyTurn(boolean newAdjustRadarForBodyTurn) {
		setCall();
        peer.lockWrite();
        try{
            commands.setAdjustRadarForBodyTurn(newAdjustRadarForBodyTurn);
        }
        finally{
            peer.unlockWrite();
        }
	}

    // // // //  // // // // // // // // // // // // // // // // // // // // // // // //
    // private implementations
    // // // //  // // // // // // // // // // // // // // // // // // // // // // // //

    protected final void i_setTurnRadar(double radians) {
        peer.lockWrite();
        try{
            commands.setRadarTurnRemaining(radians);
        }
        finally{
            peer.unlockWrite();
        }
    }

    protected final void i_setStop(boolean overwrite) {
        peer.lockWrite();
        try{
            if (!commands.isStopped() || overwrite) {
                commands.setSaveDistanceToGo(commands.getDistanceRemaining());
                commands.setSaveAngleToTurn(commands.getTurnRemaining());
                commands.setSaveGunAngleToTurn(commands.getGunTurnRemaining());
                commands.setSaveRadarAngleToTurn(commands.getRadarTurnRemaining());
            }
            commands.setStopped(true);
            commands.setDistanceRemaining(0);
            commands.setTurnRemaining(0);
            commands.setGunTurnRemaining(0);
            commands.setRadarTurnRemaining(0);
        }
        finally{
            peer.unlockWrite();
        }
    }

    protected final void i_setResume() {
        peer.lockWrite();
        try{
            if (commands.isStopped()) {
                commands.setStopped(false);
                commands.setDistanceRemaining(commands.getSaveDistanceToGo());
                commands.setTurnRemaining(commands.getSaveAngleToTurn());
                commands.setGunTurnRemaining(commands.getSaveGunAngleToTurn());
                commands.setRadarTurnRemaining(commands.getSaveRadarAngleToTurn());
            }
        }
        finally{
            peer.unlockWrite();
        }
    }

}
