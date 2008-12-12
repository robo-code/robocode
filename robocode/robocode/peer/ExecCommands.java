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


import robocode.Rules;
import robocode.peer.robot.TeamMessage;
import robocode.robotpaint.Graphics2DProxy;

import java.io.Serializable;
import static java.lang.Math.abs;
import static java.lang.Math.min;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Pavel Savara (original)
 */
public final class ExecCommands implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final int defaultBodyColor = 0xFF29298C;
	public static final int defaultGunColor = 0xFF29298C;
	public static final int defaultRadarColor = 0xFF29298C;
	public static final int defaultScanColor = 0xFF0000FF;
	public static final int defaultBulletColor = 0xFFFFFFFF;

	private double bodyTurnRemaining;
	private double radarTurnRemaining;
	private double gunTurnRemaining;
	private double distanceRemaining;

	private boolean isAdjustGunForBodyTurn;
	private boolean isAdjustRadarForGunTurn;
	private boolean isAdjustRadarForBodyTurn;
	private boolean isAdjustRadarForBodyTurnSet;

	private int bodyColor = defaultBodyColor;
	private int gunColor = defaultGunColor;
	private int radarColor = defaultRadarColor;
	private int scanColor = defaultScanColor;
	private int bulletColor = defaultBulletColor;
	private double maxTurnRate;
	private double maxVelocity;

	private boolean moved;
	private boolean scan;
	private boolean isIORobot;
	private boolean isTryingToPaint;
	private List<BulletCommand> bullets = new ArrayList<BulletCommand>(2);
	private List<Graphics2DProxy.QueuedCall> graphicsCalls;
	private String outputText;
	private List<TeamMessage> teamMessages = new ArrayList<TeamMessage>();
	private List<DebugProperty> debugProperties;

	public ExecCommands() {
		setMaxVelocity(Double.MAX_VALUE);
		setMaxTurnRate(Double.MAX_VALUE);
	}

	public ExecCommands(ExecCommands origin, boolean fromRobot) {
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
		if (fromRobot) {
			debugProperties = origin.debugProperties; 
			bullets = origin.bullets;
			scan = origin.scan;
			moved = origin.moved;
			graphicsCalls = origin.graphicsCalls;
			outputText = origin.outputText;
			teamMessages = origin.teamMessages;
			isTryingToPaint = origin.isTryingToPaint; 
		}
	}

	public void validate(RobotPeer peer) {
		if (Double.isNaN(maxTurnRate)) {
			peer.println("You cannot setMaxTurnRate to: " + maxTurnRate);
			return;
		}
		maxTurnRate = min(abs(maxTurnRate), Rules.MAX_TURN_RATE_RADIANS);

		if (Double.isNaN(maxVelocity)) {
			peer.println("You cannot setMaxVelocity to: " + maxVelocity);
			return;
		}
		maxVelocity = min(abs(maxVelocity), Rules.MAX_VELOCITY);
	}

	public int getBodyColor() {
		return bodyColor;
	}

	public int getRadarColor() {
		return radarColor;
	}

	public int getGunColor() {
		return gunColor;
	}

	public int getBulletColor() {
		return bulletColor;
	}

	public int getScanColor() {
		return scanColor;
	}

	public void setBodyColor(int color) {
		bodyColor = color;
	}

	public void setRadarColor(int color) {
		radarColor = color;
	}

	public void setGunColor(int color) {
		gunColor = color;
	}

	public void setBulletColor(int color) {
		bulletColor = color;
	}

	public void setScanColor(int color) {
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

	public List<BulletCommand> getBullets() {
		return bullets;
	}

	public List<Graphics2DProxy.QueuedCall> getGraphicsCalls() {
		return graphicsCalls;
	}

	public DebugProperty[] getDebugProperties() {
		return debugProperties != null ? debugProperties.toArray(new DebugProperty[debugProperties.size()]) : null;
	}

	public void setGraphicsCalls(List<Graphics2DProxy.QueuedCall> graphicsCalls) {
		this.graphicsCalls = graphicsCalls;
	}

	public String getOutputText() {
		final String out = outputText;

		outputText = "";
		return out;
	}

	public void setOutputText(String out) {
		outputText = out;
	}

	public List<TeamMessage> getTeamMessages() {
		return teamMessages;
	}

	public boolean isIORobot() {
		return isIORobot;
	}

	public void setIORobot() {
		isIORobot = true;
	}

	public void setDebugProperty(String key, String value) {
		if (debugProperties == null) {
			debugProperties = new ArrayList<DebugProperty>();
		}
		debugProperties.add(new DebugProperty(key, value));
	}

	public boolean isTryingToPaint() {
		return isTryingToPaint;
	}

	public void setTryingToPaint(boolean tryingToPaint) {
		isTryingToPaint = tryingToPaint;
	}
}

