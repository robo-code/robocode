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


import robocode.battle.record.RobotRecord;
import static robocode.gfx.ColorUtil.toColor;
import robocode.peer.robot.RobotStatistics;
import robocode.util.BoundingRectangle;
import static robocode.util.Utils.normalAbsoluteAngle;
import static robocode.util.Utils.normalNearAbsoluteAngle;

import java.awt.*;
import java.awt.geom.Arc2D;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author Pavel Savara (original)
 */
public class RobotPeerStatus extends RobotPeerLock {
	// Robot States: all states last one turn, except ALIVE and DEAD
	public static final int
			STATE_ACTIVE = 0,
			STATE_HIT_WALL = 1,
			STATE_HIT_ROBOT = 2,
			STATE_DEAD = 3;

	private boolean isRunning;
	private boolean isSleeping;
	private boolean isWinner;
	private boolean inCollision;
	private int state;

	private double energy;
	private double velocity;
	private double gunHeat;
	private double heading;
	private double radarHeading;
	private double gunHeading;
	private double x;
	private double y;
	private Arc2D scanArc = new Arc2D.Double();
	private boolean scan;

	private AtomicInteger setCallCount = new AtomicInteger(0);
	private AtomicInteger getCallCount = new AtomicInteger(0);

	private int skippedTurns;

	private Color bodyColor;
	private Color gunColor;
	private Color radarColor;
	private Color bulletColor;
	private Color scanColor;

	private BoundingRectangle boundingBox;
	private RobotStatistics statistics;

	public final void cleanup() {
		if (statistics != null) {
			statistics.cleanup();
		}
		statistics = null;
		boundingBox = null;
		setCallCount = null;
		getCallCount = null;
	}

	public final Color getBodyColor() {
		checkReadLock();
		return bodyColor;
	}

	public final Color getRadarColor() {
		checkReadLock();
		return radarColor;
	}

	public final Color getGunColor() {
		checkReadLock();
		return gunColor;
	}

	public final Color getBulletColor() {
		checkReadLock();
		return bulletColor;
	}

	public final Color getScanColor() {
		checkReadLock();
		return scanColor;
	}

	public final boolean isSleeping() {
		checkReadLock();
		return isSleeping;
	}

	public final boolean isRunning() {
		checkReadLock();
		return isRunning;
	}

	public final boolean isInCollision() {
		checkReadLock();
		return inCollision;
	}

	public final int getState() {
		checkReadLock();
		return state;
	}

	public final boolean isDead() {
		checkReadLock();
		return state == STATE_DEAD;
	}

	public final boolean isAlive() {
		checkReadLock();
		return state != STATE_DEAD;
	}

	public final double getEnergy() {
		checkReadLock();
		return energy;
	}

	public final Arc2D getScanArc() {
		checkReadLock();
		return scanArc;
	}

	public final double getVelocity() {
		checkReadLock();
		return velocity;
	}

	public final double getVelocitySync() {
		peer.lockRead();
		try {
			return getVelocity();
		} finally {
			peer.unlockRead();
		}
	}

	public final boolean isWinner() {
		checkReadLock();
		return isWinner;
	}

	public final double getGunHeading() {
		checkReadLock();
		return gunHeading;
	}

	public final double getHeading() {
		checkReadLock();
		return heading;
	}

	public final double getRadarHeading() {
		checkReadLock();
		return radarHeading;
	}

	public final double getX() {
		checkReadLock();
		return x;
	}

	public final double getY() {
		checkReadLock();
		return y;
	}

	public final double getGunHeat() {
		checkReadLock();
		return gunHeat;
	}

	public final boolean getScan() {
		checkReadLock();
		return scan;
	}

	public final int getSkippedTurns() {
		checkReadLock();
		return skippedTurns;
	}

	public RobotStatistics getStatistics() {
		// checkReadLock();
		// intentionaly not synchronized to prevent block from user code
		return statistics;
	}

	public BoundingRectangle getBoundingBox() {
		checkReadLock();
		return boundingBox;
	}

	public final int getSetCallCount() {
		// don't worry about synchronization
		return setCallCount.get();
	}

	public final int getGetCallCount() {
		// don't worry about synchronization
		return getCallCount.get();
	}

	public final void setRecord(RobotRecord rr) {
		checkWriteLock();
		x = rr.x;
		y = rr.y;
		energy = (double) rr.energy / 10;
		heading = Math.PI * rr.heading / 128;
		radarHeading = Math.PI * rr.radarHeading / 128;
		gunHeading = Math.PI * rr.gunHeading / 128;
		state = rr.state;
		setBodyColor(toColor(rr.bodyColor));
		setGunColor(toColor(rr.gunColor));
		setRadarColor(toColor(rr.radarColor));
		setScanColor(toColor(rr.scanColor));
	}

	public final int incSetCall() {
		// don't worry about locking
		return setCallCount.incrementAndGet();
	}

	public final int incGetCall() {
		// don't worry about locking
		return getCallCount.incrementAndGet();
	}

	public final void setSetCallCount(int v) {
		// don't worry about locking
		setCallCount.set(v);
	}

	public final void setGetCallCount(int v) {
		// don't worry about locking
		getCallCount.set(v);
	}

	public final void setSkippedTurns(int newSkippedTurns) {
		checkWriteLock();
		skippedTurns = newSkippedTurns;
	}

	public final void setX(double newX) {
		checkWriteLock();
		x = newX;
	}

	public final void setY(double newY) {
		checkWriteLock();
		y = newY;
	}

	public final void adjustGunHeat(double difference) {
		checkWriteLock();
		gunHeat += difference;
	}

	public final void setGunHeat(double newGunHeat) {
		checkWriteLock();
		gunHeat = newGunHeat;
	}

	public final void setHeading(double heading) {
		checkWriteLock();
		this.heading = heading;
	}

	public final void setGunHeading(double newGunHeading) {
		checkWriteLock();
		gunHeading = newGunHeading;
	}

	public final void setRadarHeading(double newRadarHeading) {
		checkWriteLock();
		radarHeading = newRadarHeading;
	}

	public final void adjustHeading(double difference, boolean near) {
		checkWriteLock();
		if (near) {
			heading = normalNearAbsoluteAngle(heading + difference);
		} else {
			heading = normalAbsoluteAngle(heading + difference);
		}
	}

	public final void adjustGunHeading(double difference) {
		checkWriteLock();
		gunHeading = normalAbsoluteAngle(gunHeading + difference);
	}

	public final void adjustRadarHeading(double difference) {
		checkWriteLock();
		radarHeading = normalAbsoluteAngle(radarHeading + difference);
	}

	public final void setVelocity(double newVelocity) {
		checkWriteLock();
		velocity = newVelocity;
	}

	public final void setState(int newState) {
		checkWriteLock();
		state = newState;
	}

    public final void uncharge() {
        energy = 0;
        peer.getCommands().resetIntentions();
    }

	public final void setEnergy(double newEnergy) {
		setEnergy(newEnergy, true);
	}

	public final void setEnergy(double newEnergy, boolean resetInactiveTurnCount) {
		checkWriteLock();
		if (resetInactiveTurnCount && (energy != newEnergy)) {
			peer.getBattle().resetInactiveTurnCount(energy - newEnergy);
		}
		energy = newEnergy;
		if (energy < .01) {
			energy = 0;
			// ZAMO: changed from distanceRemaining = 0; turnRemaining = 0;
			peer.getCommands().resetIntentions();
		}
	}

	public final void setWinner(boolean newWinner) {
		checkWriteLock();
		isWinner = newWinner;
	}

	public void setSleeping(boolean sleeping) {
		checkWriteLock();
		this.isSleeping = sleeping;
	}

	public final void setRunning(boolean running) {
		// checkWriteLock();
		// intentionaly not synchronized to prevent block from user code
		this.isRunning = running;
	}

	public final void setInCollision(boolean inCollision) {
		checkWriteLock();
		this.inCollision = inCollision;
	}

	public final void setScan(boolean scan) {
		checkWriteLock();
		this.scan = scan;
	}

	public final void setScanSync(boolean scan) {
		peer.lockWrite();
		try {
			setScan(scan);
		} finally {
			peer.unlockWrite();
		}
	}

	public final void setBodyColor(Color color) {
		checkWriteLock();
		bodyColor = color;
	}

	public final void setRadarColor(Color color) {
		checkWriteLock();
		radarColor = color;
	}

	public final void setGunColor(Color color) {
		checkWriteLock();
		gunColor = color;
	}

	public final void setBulletColor(Color color) {
		checkWriteLock();
		bulletColor = color;
	}

	public final void setScanColor(Color color) {
		checkWriteLock();
		scanColor = color;
	}

	public void setBoundingBox(BoundingRectangle boundingBox) {
		checkWriteLock();
		this.boundingBox = boundingBox;
	}

	public void setStatistics(RobotStatistics statistics) {
		checkWriteLock();
		this.statistics = statistics;
	}
}
