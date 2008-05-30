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
	protected RobotPeer peer;

	public BasicRobotProxy(RobotPeer peer) {
		this.peer = peer;
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
		peer.move(distance);
	}

	public final void turnBody(double radians) {
		peer.turnBody(radians);
	}

	public final void turnGun(double radians) {
		peer.turnGun(radians);
	}

	public final Bullet fire(double power) {
		return peer.fire(power);
	}

	// fast setters
	public final void setBodyColor(Color color) {
		peer.setCall();
		peer.setBodyColor(color);
	}

	public final void setGunColor(Color color) {
		peer.setCall();
		peer.setGunColor(color);
	}

	public final void setRadarColor(Color color) {
		peer.setCall();
		peer.setRadarColor(color);
	}

	public final void setBulletColor(Color color) {
		peer.setCall();
		peer.setBulletColor(color);
	}

	public final void setScanColor(Color color) {
		peer.setCall();
		peer.setScanColor(color);
	}

	// counters
	public final void getCall() {
		peer.getCall();
	}

	public final void setCall() {
		peer.setCall();
	}

	// AdvancedRobot calls below
	public final double getRadarTurnRemaining() {
		peer.getCall();
		return peer.getRadarTurnRemaining();
	}

	public final double getDistanceRemaining() {
		peer.getCall();
		return peer.getDistanceRemaining();
	}

	public final double getBodyTurnRemaining() {
		peer.getCall();
		return peer.getBodyTurnRemaining();
	}

	// Robot calls below
	public final double getVelocity() {
		peer.getCall();
		return peer.getVelocity();
	}

	public final double getRadarHeading() {
		peer.getCall();
		return peer.getRadarHeading();
	}

	public final double getGunCoolingRate() {
		peer.getCall();
		return peer.getBattle().getGunCoolingRate();
	}

	public final String getName() {
		peer.getCall();
		return peer.getName();
	}

	public final long getTime() {
		peer.getCall();
		return peer.getBattle().getCurrentTime();
	}

	// Junior calls below
	public final double getBodyHeading() {
		peer.getCall();
		return peer.getBodyHeading();
	}

	public final double getGunHeading() {
		peer.getCall();
		return peer.getGunHeading();
	}

	public final double getGunTurnRemaining() {
		peer.getCall();
		return peer.getGunTurnRemaining();
	}

	public final double getEnergy() {
		peer.getCall();
		return peer.getEnergy();
	}

	public final double getGunHeat() {
		peer.getCall();
		return peer.getGunHeat();
	}

	public final double getBattleFieldHeight() {
		peer.getCall();
		return peer.getBattleFieldHeight();
	}

	public final double getBattleFieldWidth() {
		peer.getCall();
		return peer.getBattleFieldWidth();
	}

	public final double getX() {
		peer.getCall();
		return peer.getX();
	}

	public final double getY() {
		peer.getCall();
		return peer.getY();
	}

	public final int getOthers() {
		peer.getCall();
		return peer.getBattle().getActiveRobots() - (peer.isAlive() ? 1 : 0);
	}

	public final int getNumRounds() {
		peer.getCall();
		return peer.getNumRounds();
	}

	public final int getRoundNum() {
		peer.getCall();
		return peer.getRoundNum();
	}

	public final Graphics2D getGraphics() {
		peer.getCall();
		return peer.getGraphics();
	}
}
