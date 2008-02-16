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


import robocode.robotinterfaces.peer.IBasicRobotPeer;
import robocode.Bullet;

import java.awt.*;


/**
 * @author Pavel Savara (original)
 */
public class BasicRobotPeerProxy implements IBasicRobotPeer {
	IBasicRobotPeer peer;
	
	public BasicRobotPeerProxy(IBasicRobotPeer peer) {
		this.peer = peer;
	}

	// asynchronous actions
	public Bullet setFire(double power) {
		return peer.setFire(power);
	}

	// blocking actions
	public void tick() {
		peer.tick();
	}

	public void move(double distance) {
		peer.move(distance);
	}

	public void turnChassis(double radians) {
		peer.turnChassis(radians);
	}

	public void turnAndMoveChassis(double distance, double radians) {
		peer.turnAndMoveChassis(distance, radians);
	}

	public void turnGun(double radians) {
		peer.turnGun(radians);
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

	public double getTurnRemaining() {
		peer.getCall();
		return peer.getTurnRemaining();
	}

	public boolean isAdjustGunForBodyTurn() {
		peer.getCall();
		return peer.isAdjustGunForBodyTurn();
	}

	public boolean isAdjustRadarForGunTurn() {
		peer.getCall();
		return peer.isAdjustRadarForGunTurn();
	}

	public boolean isAdjustRadarForBodyTurn() {
		peer.getCall();
		return peer.isAdjustRadarForBodyTurn();
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
	public double getHeading() {
		peer.getCall();
		return peer.getHeading();
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
}
