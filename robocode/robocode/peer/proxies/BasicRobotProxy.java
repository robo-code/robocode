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
import robocode.exception.DisabledException;
import robocode.peer.RobotPeer;
import robocode.robotinterfaces.peer.IBasicRobotPeer;

import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author Pavel Savara (original)
 */
public class BasicRobotProxy implements IBasicRobotPeer {
    public static final long
            MAX_SET_CALL_COUNT = 10000,
            MAX_GET_CALL_COUNT = 10000;

	protected RobotPeer peer;
    private AtomicInteger setCallCount = new AtomicInteger(0);
    private AtomicInteger getCallCount = new AtomicInteger(0);

	public BasicRobotProxy(RobotPeer peer) {
		this.peer = peer;
	}


    // counters
    public final void getCall() {
        // don't care about sync
        int val = getCallCount.incrementAndGet();

        if (val >= MAX_GET_CALL_COUNT) {
            peer.getOut().println("SYSTEM: You have made " + val + " calls to getXX methods without calling execute()");
            throw new DisabledException("Too many calls to getXX methods");
        }
    }

    public final void setCall() {
        // don't care about sync
        int val = setCallCount.incrementAndGet();

        if (val >= MAX_SET_CALL_COUNT) {
            peer.getOut().println("SYSTEM: You have made " + val + " calls to setXX methods without calling execute()");
            throw new DisabledException("Too many calls to setXX methods");
        }
    }

    public final void resetCallCount() {
        getCallCount.set(0);
        setCallCount.set(0);
    }

	// asynchronous actions
	public final Bullet setFire(double power) {
		return peer.setFire(power);
	}

	// blocking actions
	public final void execute() {
		peer.execute();
	}

	public final void move(double distance) {
        peer.setMove(distance);
        do {
            execute(); // Always tick at least once
        } while (peer.getDistanceRemaining() != 0);
	}

	public final void turnBody(double radians) {
        peer.setTurnBody(radians);
        do {
            execute(); // Always tick at least once
        } while (peer.getBodyTurnRemaining() != 0);
	}

	public final void turnGun(double radians) {
        peer.setTurnGun(radians);
        do {
            execute(); // Always tick at least once
        } while (peer.getGunTurnRemaining() != 0);
	}

	public final Bullet fire(double power) {
        Bullet bullet = peer.setFire(power);
        execute();
        return bullet;
	}

	// fast setters
	public final void setBodyColor(Color color) {
		setCall();
		peer.setBodyColor(color);
	}

	public final void setGunColor(Color color) {
		setCall();
		peer.setGunColor(color);
	}

	public final void setRadarColor(Color color) {
		setCall();
		peer.setRadarColor(color);
	}

	public final void setBulletColor(Color color) {
		setCall();
		peer.setBulletColor(color);
	}

	public final void setScanColor(Color color) {
		setCall();
		peer.setScanColor(color);
	}

	// AdvancedRobot calls below
	public final double getRadarTurnRemaining() {
		getCall();
		return peer.getRadarTurnRemaining();
	}

	public final double getDistanceRemaining() {
		getCall();
		return peer.getDistanceRemaining();
	}

	public final double getBodyTurnRemaining() {
		getCall();
		return peer.getBodyTurnRemaining();
	}

	// Robot calls below
	public final double getVelocity() {
		getCall();
		return peer.getVelocity();
	}

	public final double getRadarHeading() {
		getCall();
		return peer.getRadarHeading();
	}

	public final double getGunCoolingRate() {
		getCall();
		return peer.getBattle().getGunCoolingRate();
	}

	public final String getName() {
		getCall();
		return peer.getName();
	}

	public final long getTime() {
		getCall();
		return peer.getBattle().getCurrentTime();
	}

	// Junior calls below
	public final double getBodyHeading() {
		getCall();
		return peer.getBodyHeading();
	}

	public final double getGunHeading() {
		getCall();
		return peer.getGunHeading();
	}

	public final double getGunTurnRemaining() {
		getCall();
		return peer.getGunTurnRemaining();
	}

	public final double getEnergy() {
		getCall();
		return peer.getEnergy();
	}

	public final double getGunHeat() {
		getCall();
		return peer.getGunHeat();
	}

	public final double getBattleFieldHeight() {
		getCall();
		return peer.getBattleFieldHeight();
	}

	public final double getBattleFieldWidth() {
		getCall();
		return peer.getBattleFieldWidth();
	}

	public final double getX() {
		getCall();
		return peer.getX();
	}

	public final double getY() {
		getCall();
		return peer.getY();
	}

	public final int getOthers() {
		getCall();
		return peer.getBattle().getActiveRobots() - (peer.isAlive() ? 1 : 0);
	}

	public final int getNumRounds() {
		getCall();
		return peer.getNumRounds();
	}

	public final int getRoundNum() {
		getCall();
		return peer.getRoundNum();
	}

	public final Graphics2D getGraphics() {
		getCall();
		return peer.getGraphics();
	}
}
