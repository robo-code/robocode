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
package robocode.peer.data;


import robocode.Condition;
import robocode.Rules;
import robocode.peer.BulletPeer;

import static java.lang.Math.abs;
import static java.lang.Math.min;


/**
 * @author Pavel Savara (original)
 */
public class RobotPeerCommands extends RobotPeerLock {
	private double maxVelocity = Rules.MAX_VELOCITY; // Can be changed by robot
	private double maxTurnRate = Rules.MAX_TURN_RATE_RADIANS; // Can be changed by robot

	private double turnRemaining;
	private double radarTurnRemaining;
	private double gunTurnRemaining;
	private double distanceRemaining;

	private boolean isAdjustGunForBodyTurn;
	private boolean isAdjustRadarForGunTurn;
	private boolean isAdjustRadarForBodyTurn;
	private boolean isAdjustRadarForBodyTurnSet;

	private double saveAngleToTurn;
	private double saveDistanceToGo;
	private double saveGunAngleToTurn;
	private double saveRadarAngleToTurn;

	private double acceleration;
	private boolean slowingDown;
	private int moveDirection;
	private boolean isStopped;
	private boolean isStopping;

	private BulletPeer currentBullet;

	private Condition waitCondition;
	private boolean testingCondition;

	private boolean fireAssistValid = false;
	private double fireAssistAngle;

	private double lastGunHeading;
	private double lastHeading;
	private double lastRadarHeading;
	private double lastX;
	private double lastY;

	public final void cleanup() {
		if (waitCondition != null) {
			waitCondition.cleanup();
			waitCondition = null;
		}
		if (currentBullet != null) {
			currentBullet.cleanup();
			currentBullet = null;
		}
	}

	public final boolean isAdjustRadarForGunTurn() {
		checkReadLock();
		return isAdjustRadarForGunTurn;
	}

	public final double getMaxVelocity() {
		checkReadLock();
		return maxVelocity;
	}

	public final double getMaxTurnRate() {
		checkReadLock();
		return maxTurnRate;
	}

	public final double getBodyTurnRemaining() {
		checkReadLock();
		return turnRemaining;
	}

	public final double getRadarTurnRemaining() {
		checkReadLock();
		return radarTurnRemaining;
	}

	public final double getGunTurnRemaining() {
		checkReadLock();
		return gunTurnRemaining;
	}

	public final double getDistanceRemaining() {
		checkReadLock();
		return distanceRemaining;
	}

	public final double getTurnRemainingSync() {
		peer.lockRead();
		try {
			return getBodyTurnRemaining();
		} finally {
			peer.unlockRead();
		}
	}

	public final double getRadarTurnRemainingSync() {
		peer.lockRead();
		try {
			return getRadarTurnRemaining();
		} finally {
			peer.unlockRead();
		}
	}

	public final double getGunTurnRemainingSync() {
		peer.lockRead();
		try {
			return getGunTurnRemaining();
		} finally {
			peer.unlockRead();
		}
	}

	public final double getDistanceRemainingSync() {
		peer.lockRead();
		try {
			return getDistanceRemaining();
		} finally {
			peer.unlockRead();
		}
	}

	public final boolean isAdjustGunForBodyTurn() {
		checkReadLock();
		return isAdjustGunForBodyTurn;
	}

	public final boolean isAdjustRadarForBodyTurn() {
		checkReadLock();
		return isAdjustRadarForBodyTurn;
	}

	public final boolean isAdjustRadarForBodyTurnSet() {
		checkReadLock();
		return isAdjustRadarForBodyTurnSet;
	}

	public final double getSaveAngleToTurn() {
		checkReadLock();
		return saveAngleToTurn;
	}

	public final double getSaveDistanceToGo() {
		checkReadLock();
		return saveDistanceToGo;
	}

	public final double getSaveGunAngleToTurn() {
		checkReadLock();
		return saveGunAngleToTurn;
	}

	public final double getSaveRadarAngleToTurn() {
		checkReadLock();
		return saveRadarAngleToTurn;
	}

	public final double getAcceleration() {
		checkReadLock();
		return acceleration;
	}

	public final boolean isSlowingDown() {
		checkReadLock();
		return slowingDown;
	}

	public final int getMoveDirection() {
		checkReadLock();
		return moveDirection;
	}

	public final boolean isStopped() {
		checkReadLock();
		return isStopped;
	}

	public final boolean isStopping() {
		checkReadLock();
		return isStopping;
	}

	public BulletPeer getCurrentBullet() {
		checkReadLock();
		return currentBullet;
	}

	public Condition getWaitCondition() {
		checkReadLock();
		return waitCondition;
	}

	public final boolean isTestingCondition() {
		checkReadLock();
		return testingCondition;
	}

	public final double getFireAssistAngle() {
		checkReadLock();
		return fireAssistAngle;
	}

	public final boolean isFireAssistValid() {
		checkReadLock();
		return fireAssistValid;
	}

	public final void resetIntentions() {
		checkWriteLock();
		gunTurnRemaining = 0;
		radarTurnRemaining = 0;
		distanceRemaining = 0;
		turnRemaining = 0;
		isStopped = true;
		isStopping = false;
	}

	public final void setFireAssistValid(boolean newFireAssistValid) {
		checkWriteLock();
		fireAssistValid = newFireAssistValid;
	}

	public final void setFireAssistAngle(double angle) {
		checkWriteLock();
		fireAssistAngle = angle;
	}

	public final void setAdjustRadarForBodyTurn(boolean newAdjustRadarForBodyTurn) {
		checkWriteLock();
		isAdjustRadarForBodyTurn = newAdjustRadarForBodyTurn;
		setAdjustRadarForBodyTurnSet(true);
	}

	public final void setAdjustGunForBodyTurn(boolean newAdjustGunForBodyTurn) {
		checkWriteLock();
		isAdjustGunForBodyTurn = newAdjustGunForBodyTurn;
	}

	public final void setAdjustRadarForGunTurn(boolean newAdjustRadarForGunTurn) {
		checkWriteLock();
		isAdjustRadarForGunTurn = newAdjustRadarForGunTurn;
		if (!isAdjustRadarForBodyTurnSet()) {
			isAdjustRadarForBodyTurn = newAdjustRadarForGunTurn;
		}
	}

	public final void setMaxVelocity(double maxVelocity) {
		checkWriteLock();
		if (Double.isNaN(maxVelocity)) {
			peer.getOut().println("You cannot setMaxVelocity to: " + maxVelocity);
			return;
		}
		this.maxVelocity = min(abs(maxVelocity), Rules.MAX_VELOCITY);
	}

	public final void setMaxTurnRate(double maxTurnRate) {
		checkWriteLock();
		if (Double.isNaN(maxTurnRate)) {
			peer.getOut().println("You cannot setMaxTurnRate to: " + maxTurnRate);
			return;
		}
		this.maxTurnRate = min(abs(maxTurnRate), Rules.MAX_TURN_RATE_RADIANS);
	}

	public final void setBodyTurnRemaining(double turnRemaining) {
		checkWriteLock();
		this.turnRemaining = turnRemaining;
	}

	public final void setRadarTurnRemaining(double radarTurnRemaining) {
		checkWriteLock();
		this.radarTurnRemaining = radarTurnRemaining;
	}

	public final void setGunTurnRemaining(double gunTurnRemaining) {
		checkWriteLock();
		this.gunTurnRemaining = gunTurnRemaining;
	}

	public final void setDistanceRemaining(double distanceRemaining) {
		checkWriteLock();
		this.distanceRemaining = distanceRemaining;
	}

	public final void setAdjustRadarForBodyTurnSet(boolean adjustRadarForBodyTurnSet) {
		checkWriteLock();
		isAdjustRadarForBodyTurnSet = adjustRadarForBodyTurnSet;
	}

	public final void setSaveAngleToTurn(double saveAngleToTurn) {
		checkWriteLock();
		this.saveAngleToTurn = saveAngleToTurn;
	}

	public final void setSaveDistanceToGo(double saveDistanceToGo) {
		checkWriteLock();
		this.saveDistanceToGo = saveDistanceToGo;
	}

	public final void setSaveGunAngleToTurn(double saveGunAngleToTurn) {
		checkWriteLock();
		this.saveGunAngleToTurn = saveGunAngleToTurn;
	}

	public final void setSaveRadarAngleToTurn(double saveRadarAngleToTurn) {
		checkWriteLock();
		this.saveRadarAngleToTurn = saveRadarAngleToTurn;
	}

	public final void setAcceleration(double acceleration) {
		checkWriteLock();
		this.acceleration = acceleration;
	}

	public final void setSlowingDown(boolean slowingDown) {
		checkWriteLock();
		this.slowingDown = slowingDown;
	}

	public final void setMoveDirection(int moveDirection) {
		checkWriteLock();
		this.moveDirection = moveDirection;
	}

	public final void setStopped(boolean stopped) {
		checkWriteLock();
		isStopped = stopped;
	}

	public final void setStopping(boolean stopping) {
		checkWriteLock();
		this.isStopping = stopping;
	}

	public final void setCurrentBullet(BulletPeer currentBullet) {
		checkWriteLock();
		this.currentBullet = currentBullet;
	}

	public final void setWaitCondition(Condition waitCondition) {
		checkWriteLock();
		this.waitCondition = waitCondition;
	}

	public final void setWaitConditionSync(Condition waitCondition) {
		peer.lockWrite();
		try {
			setWaitCondition(waitCondition);
		} finally {
			peer.unlockWrite();
		}
	}

	public final void setTestingCondition(boolean testingCondition) {
		checkWriteLock();
		this.testingCondition = testingCondition;
	}

	public final double getLastHeading() {
		checkReadLock();
		return lastHeading;
	}

	public final double getLastGunHeading() {
		checkReadLock();
		return lastGunHeading;
	}

	public final double getLastRadarHeading() {
		checkReadLock();
		return lastRadarHeading;
	}

	public final void updateScan() {
		checkWriteLock();
		RobotPeerStatus status = peer.getStatus();

		if (!status.getScan()) {
			status.setScan(
					(lastHeading != status.getBodyHeading() || lastGunHeading != status.getGunHeading()
					|| lastRadarHeading != status.getRadarHeading() || lastX != status.getX() || lastY != status.getY()
					|| getWaitCondition() != null));
		}
	}

	public final void updateLast() {
		checkWriteLock();
		RobotPeerStatus status = peer.getStatus();

		lastGunHeading = status.getGunHeading();
		lastHeading = status.getBodyHeading();
		lastRadarHeading = status.getRadarHeading();
		lastX = status.getX();
		lastY = status.getY();
	}
}
