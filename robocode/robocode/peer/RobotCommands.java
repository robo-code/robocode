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


import robocode.Bullet;
import robocode.Rules;

import java.util.List;
import java.util.ArrayList;
import java.awt.*;
import static java.lang.Math.min;
import static java.lang.Math.abs;


/**
 * @author Pavel Savara (original)
 */
public class RobotCommands {
	private double bodyTurnRemaining;
	private double radarTurnRemaining;
	private double gunTurnRemaining;
	private double distanceRemaining;

	private boolean isAdjustGunForBodyTurn;
	private boolean isAdjustRadarForGunTurn;
	private boolean isAdjustRadarForBodyTurn;
	private boolean isAdjustRadarForBodyTurnSet;

	private Color bodyColor;
	private Color gunColor;
	private Color radarColor;
	private Color bulletColor;
	private Color scanColor;
	private double maxTurnRate;
	private double maxVelocity;

	private boolean moved;
	private boolean scan;
	private boolean canFireAssist;
	private List<Bullet> bullets = new ArrayList<Bullet>(2);

	public RobotCommands() {
		setMaxVelocity(Double.MAX_VALUE);
		setMaxTurnRate(Double.MAX_VALUE);
	}

	public RobotCommands(RobotCommands origin, boolean fromBattleToRobot) {
		bodyTurnRemaining = origin.bodyTurnRemaining;
		radarTurnRemaining = origin.radarTurnRemaining;
		gunTurnRemaining = origin.gunTurnRemaining;
		distanceRemaining = origin.distanceRemaining;
		isAdjustGunForBodyTurn = origin.isAdjustGunForBodyTurn;
		isAdjustRadarForGunTurn = origin.isAdjustRadarForGunTurn;
		isAdjustRadarForBodyTurn = origin.isAdjustRadarForBodyTurn;
		isAdjustRadarForBodyTurnSet = origin.isAdjustRadarForBodyTurnSet;
		bodyColor = origin.bodyColor;
		gunColor = origin.gunColor;
		radarColor = origin.radarColor;
		bulletColor = origin.bulletColor;
		scanColor = origin.scanColor;
		maxTurnRate = origin.maxTurnRate;
		maxVelocity = origin.maxVelocity;
		if (fromBattleToRobot) {
			canFireAssist = origin.canFireAssist;
		} else {
			bullets = origin.bullets;
			scan = origin.scan;
			moved = origin.moved;
		}
	}

	public void validate(RobotPeer peer) {
		if (Double.isNaN(maxTurnRate)) {
			peer.getOut().println("You cannot setMaxTurnRate to: " + maxTurnRate);
			return;
		}
		maxTurnRate = min(abs(maxTurnRate), Rules.MAX_TURN_RATE_RADIANS);
        
		if (Double.isNaN(maxVelocity)) {
			peer.getOut().println("You cannot setMaxVelocity to: " + maxVelocity);
			return;
		}
		maxVelocity = min(abs(maxVelocity), Rules.MAX_VELOCITY);
	}

	public Color getBodyColor() {
		return bodyColor;
	}

	public Color getRadarColor() {
		return radarColor;
	}

	public Color getGunColor() {
		return gunColor;
	}

	public Color getBulletColor() {
		return bulletColor;
	}

	public Color getScanColor() {
		return scanColor;
	}

	public void setBodyColor(Color color) {
		bodyColor = color;
	}

	public void setRadarColor(Color color) {
		radarColor = color;
	}

	public void setGunColor(Color color) {
		gunColor = color;
	}

	public void setBulletColor(Color color) {
		bulletColor = color;
	}

	public void setScanColor(Color color) {
		scanColor = color;
	}

	public double getBodyTurnRemaining() {
		return bodyTurnRemaining;
	}

	public void setBodyTurnRemaining(double bodyTurnRemaining) {
		this.bodyTurnRemaining = bodyTurnRemaining;
	}

	public double getRadarTurnRemaining() {
		return radarTurnRemaining;
	}

	public void setRadarTurnRemaining(double radarTurnRemaining) {
		this.radarTurnRemaining = radarTurnRemaining;
	}

	public double getGunTurnRemaining() {
		return gunTurnRemaining;
	}

	public void setGunTurnRemaining(double gunTurnRemaining) {
		this.gunTurnRemaining = gunTurnRemaining;
	}

	public double getDistanceRemaining() {
		return distanceRemaining;
	}

	public void setDistanceRemaining(double distanceRemaining) {
		this.distanceRemaining = distanceRemaining;
	}

	public boolean isAdjustGunForBodyTurn() {
		return isAdjustGunForBodyTurn;
	}

	public void setAdjustGunForBodyTurn(boolean adjustGunForBodyTurn) {
		isAdjustGunForBodyTurn = adjustGunForBodyTurn;
	}

	public boolean isAdjustRadarForGunTurn() {
		return isAdjustRadarForGunTurn;
	}

	public void setAdjustRadarForGunTurn(boolean adjustRadarForGunTurn) {
		isAdjustRadarForGunTurn = adjustRadarForGunTurn;
	}

	public boolean isAdjustRadarForBodyTurn() {
		return isAdjustRadarForBodyTurn;
	}

	public void setAdjustRadarForBodyTurn(boolean adjustRadarForBodyTurn) {
		isAdjustRadarForBodyTurn = adjustRadarForBodyTurn;
	}

	public boolean isAdjustRadarForBodyTurnSet() {
		return isAdjustRadarForBodyTurnSet;
	}

	public void setAdjustRadarForBodyTurnSet(boolean adjustRadarForBodyTurnSet) {
		isAdjustRadarForBodyTurnSet = adjustRadarForBodyTurnSet;
	}

	public double getMaxTurnRate() {
		return maxTurnRate;
	}

	public void setMaxTurnRate(double maxTurnRate) {
		this.maxTurnRate = min(abs(maxTurnRate), Rules.MAX_TURN_RATE_RADIANS);
	}

	public double getMaxVelocity() {
		return maxVelocity;
	}

	public void setMaxVelocity(double maxVelocity) {
		this.maxVelocity = min(abs(maxVelocity), Rules.MAX_VELOCITY);
	}

	public boolean isMoved() {
		return moved;
	}

	public void setMoved(boolean moved) {
		this.moved = moved;
	}

	public boolean isScan() {
		return scan;
	}

	public void setScan(boolean scan) {
		this.scan = scan;
	}

	public List<Bullet> getBullets() {
		return bullets;
	}

    public void setCanFireAssist(boolean value) {
        canFireAssist = value;
    }

	public boolean getCanFireAssist() {
		return canFireAssist;
	}
}

