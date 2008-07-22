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
import robocode.peer.RobotPeer;
import robocode.robotinterfaces.peer.IBasicRobotPeer;

import java.awt.*;


/**
 * @author Pavel Savara (original)
 */
public class BasicRobotProxy implements IBasicRobotPeer {
	RobotPeer peer;

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
        setMoveImpl(distance);
        do {
            execute(); // Always tick at least once
        } while (getDistanceRemaining() != 0);
	}

	public void turnBody(double radians) {
        setTurnBodyImpl(radians);
        do {
            execute(); // Always tick at least once
        } while (getBodyTurnRemaining() != 0);
	}

	public void turnGun(double radians) {
        setTurnGunImpl(radians);
        do {
            execute(); // Always tick at least once
        } while (getGunTurnRemaining() != 0);
	}

	public Bullet fire(double power) {
        Bullet bullet = setFire(power);

        execute();
        return bullet;
	}

	// fast setters
	public void setBodyColor(Color color) {
		peer.setCall();
		peer.setBodyColor(color);
	}

	public void setGunColor(Color color) {
		peer.setCall();
		peer.setGunColor(color);
	}

	public void setRadarColor(Color color) {
		peer.setCall();
		peer.setRadarColor(color);
	}

	public void setBulletColor(Color color) {
		peer.setCall();
		peer.setBulletColor(color);
	}

	public void setScanColor(Color color) {
		peer.setCall();
		peer.setScanColor(color);
	}

	// counters
	public void getCall() {
		peer.getCall();
	}

	public void setCall() {
		peer.setCall();
	}

	// AdvancedRobot calls below
	public double getRadarTurnRemaining() {
		peer.getCall();
		return peer.getRadarTurnRemaining();
	}

	public double getDistanceRemaining() {
		peer.getCall();
		return peer.getDistanceRemaining();
	}

	public double getBodyTurnRemaining() {
		peer.getCall();
		return peer.getBodyTurnRemaining();
	}

	// Robot calls below
	public double getVelocity() {
		peer.getCall();
		return peer.getVelocity();
	}

	public double getRadarHeading() {
		peer.getCall();
		return peer.getRadarHeading();
	}

	public double getGunCoolingRate() {
		peer.getCall();
		return peer.getGunCoolingRate();
	}

	public String getName() {
		peer.getCall();
		return peer.getName();
	}

	public long getTime() {
		peer.getCall();
		return peer.getTime();
	}

	// Junior calls below
	public double getBodyHeading() {
		peer.getCall();
		return peer.getBodyHeading();
	}

	public double getGunHeading() {
		peer.getCall();
		return peer.getGunHeading();
	}

	public double getGunTurnRemaining() {
		peer.getCall();
		return peer.getGunTurnRemaining();
	}

	public double getEnergy() {
		peer.getCall();
		return peer.getEnergy();
	}

	public double getGunHeat() {
		peer.getCall();
		return peer.getGunHeat();
	}

	public double getBattleFieldHeight() {
		peer.getCall();
		return peer.getBattleFieldHeight();
	}

	public double getBattleFieldWidth() {
		peer.getCall();
		return peer.getBattleFieldWidth();
	}

	public double getX() {
		peer.getCall();
		return peer.getX();
	}

	public double getY() {
		peer.getCall();
		return peer.getY();
	}

	public int getOthers() {
		peer.getCall();
		return peer.getOthers();
	}

	public int getNumRounds() {
		peer.getCall();
		return peer.getNumRounds();
	}

	public int getRoundNum() {
		peer.getCall();
		return peer.getRoundNum();
	}

	public Graphics2D getGraphics() {
		peer.getCall();
		return peer.getGraphics();
	}

    protected void setMoveImpl(double distance){
        peer.setMove(distance);
    }

    protected void setMaxVelocityImpl(double newVelocity){
        peer.setMaxVelocity(newVelocity);
    }

    protected void setTurnBodyImpl(double radians){
        peer.setTurnBody(radians);
    }

    protected void setTurnGunImpl(double radians){
        peer.setTurnGun(radians);
    }

    protected void setTurnRadarImpl(double radians){
        peer.setTurnRadar(radians);
    }

    protected void setMaxTurnRateImpl(double newTurnRate){
        peer.setMaxTurnRate(newTurnRate);
    }
}
