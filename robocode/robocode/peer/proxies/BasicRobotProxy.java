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
import robocode.RobotStatus;
import robocode.peer.RobotPeer;
import robocode.exception.DisabledException;
import robocode.robotinterfaces.peer.IBasicRobotPeer;

import java.awt.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author Pavel Savara (original)
 */
public class BasicRobotProxy implements IBasicRobotPeer {
    private static final long
            MAX_SET_CALL_COUNT = 10000,
            MAX_GET_CALL_COUNT = 10000;

	RobotPeer peer;
    AtomicReference<RobotStatus> status=new AtomicReference<RobotStatus>();
    private AtomicInteger setCallCount = new AtomicInteger(0);
    private AtomicInteger getCallCount = new AtomicInteger(0);

    public BasicRobotProxy(RobotPeer peer) {
		this.peer = peer;
	}

	// asynchronous actions
	public Bullet setFire(double power) {
		return peer.setFire(power);
	}

	// blocking actions
	public void execute() {
		peer.execute();
	}

	public void move(double distance) {
		peer.move(distance);
	}

	public void turnBody(double radians) {
		peer.turnBody(radians);
	}

	public void turnGun(double radians) {
		peer.turnGun(radians);
	}

	public Bullet fire(double power) {
		return peer.fire(power);
	}

	// fast setters
	public void setBodyColor(Color color) {
		setCall();
		peer.setBodyColor(color);
	}

	public void setGunColor(Color color) {
		setCall();
		peer.setGunColor(color);
	}

	public void setRadarColor(Color color) {
		setCall();
		peer.setRadarColor(color);
	}

	public void setBulletColor(Color color) {
		setCall();
		peer.setBulletColor(color);
	}

	public void setScanColor(Color color) {
		setCall();
		peer.setScanColor(color);
	}

	// counters
    public void setCall() {
        final int res = setCallCount.incrementAndGet();
        if (res >= MAX_SET_CALL_COUNT) {
            peer.getOut().println("SYSTEM: You have made " + res + " calls to setXX methods without calling execute()");
            throw new DisabledException("Too many calls to setXX methods");
        }
    }

    public void getCall() {
        final int res = getCallCount.incrementAndGet();
        if (res >= MAX_GET_CALL_COUNT) {
            peer.getOut().println("SYSTEM: You have made " + res + " calls to getXX methods without calling execute()");
            throw new DisabledException("Too many calls to getXX methods");
        }
    }

	// AdvancedRobot calls below
	public double getRadarTurnRemaining() {
		getCall();
		return peer.getRadarTurnRemaining();
	}

	public double getDistanceRemaining() {
		getCall();
		return peer.getDistanceRemaining();
	}

	public double getBodyTurnRemaining() {
		getCall();
		return peer.getBodyTurnRemaining();
	}

	// Robot calls below
	public double getVelocity() {
		getCall();
		return peer.getVelocity();
	}

	public double getRadarHeading() {
		getCall();
		return peer.getRadarHeading();
	}

	public double getGunCoolingRate() {
		getCall();
		return peer.getGunCoolingRate();
	}

	public String getName() {
		getCall();
		return peer.getName();
	}

	public long getTime() {
		getCall();
		return peer.getTime();
	}

	// Junior calls below
	public double getBodyHeading() {
		getCall();
		return peer.getBodyHeading();
	}

	public double getGunHeading() {
		getCall();
		return peer.getGunHeading();
	}

	public double getGunTurnRemaining() {
		getCall();
		return peer.getGunTurnRemaining();
	}

	public double getEnergy() {
		getCall();
		return peer.getEnergy();
	}

	public double getGunHeat() {
		getCall();
		return peer.getGunHeat();
	}

	public double getBattleFieldHeight() {
		getCall();
		return peer.getBattleFieldHeight();
	}

	public double getBattleFieldWidth() {
		getCall();
		return peer.getBattleFieldWidth();
	}

	public double getX() {
		getCall();
		return peer.getX();
	}

	public double getY() {
		getCall();
		return peer.getY();
	}

	public int getOthers() {
		getCall();
		return peer.getOthers();
	}

	public int getNumRounds() {
		getCall();
		return peer.getNumRounds();
	}

	public int getRoundNum() {
		getCall();
		return peer.getRoundNum();
	}

	public Graphics2D getGraphics() {
		getCall();
		return peer.getGraphics();
	}

    //battle driven methods
    
    public void updateStatus(RobotStatus status){
        this.status.set(status);
    }

    public synchronized void setSetCallCount(int setCallCount) {
        this.setCallCount.set(setCallCount);
    }

    public synchronized void setGetCallCount(int getCallCount) {
        this.getCallCount.set(getCallCount);
    }
}
