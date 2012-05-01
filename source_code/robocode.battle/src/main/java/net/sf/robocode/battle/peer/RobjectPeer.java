/*******************************************************************************
 * Copyright (c) 2001, 2009 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 * 		Joshua Galecki
 * 		-Initial implementation
 *******************************************************************************/

package net.sf.robocode.battle.peer;


import java.awt.geom.Rectangle2D;


/**
 * This class is the basic object used by the Robocode extensibility framework
 * 
 * @author Joshua Galecki (original)
 *
 */
public abstract class RobjectPeer {

	private double x;
	private double y;
	private double width;
	private double height;
	private String type;
	private boolean robotStopper;
	private boolean bulletStopper;
	private boolean scanStopper;
	
	private boolean scannable;
	private boolean robotConscious;
	
	private boolean dynamic;
	protected int teamNumber;
	
	public RobjectPeer(String type, double x, double y, double width, double height, boolean stopsRobots,
			boolean stopsBullets, boolean stopsScans, boolean scannable, boolean robotConscious,
			boolean dynamic) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.type = type;
		robotStopper = stopsRobots;
		bulletStopper = stopsBullets;
		scanStopper = stopsScans;
		this.setRobotConscious(robotConscious);
		this.scannable = scannable;
		this.setDynamic(dynamic);
	}

	public Rectangle2D.Double getBoundaryRect() {
		return new Rectangle2D.Double(x, y, width, height);
	}
	
	/**
	 * Returns a rectangle slightly larger than the boundary rectangle. This will be used
	 * with the scan intersection. If a normal-sized rectangle is used to trim the scan arc, 
	 * the resulting scan arc may or may not intersect with the object 
	 */
	public Rectangle2D.Double getIntersectRect() {
		return new Rectangle2D.Double(x - 2, y - 2, width + 4, height + 4);
	}
	
	public void setX(double x) {
		this.x = x;
	}

	public double getX() {
		return x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getY() {
		return y;
	}

	public void setWidth(double d) {
		this.width = d;
	}

	public double getWidth() {
		return width;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public double getHeight() {
		return height;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setScannable(boolean scannable) {
		this.scannable = scannable;
	}

	public boolean isScannable() {
		return scannable;
	}

	public void setScanStopper(boolean scanStopper) {
		this.scanStopper = scanStopper;
	}

	public boolean isScanStopper() {
		return scanStopper;
	}

	public void setBulletStopper(boolean bulletStopper) {
		this.bulletStopper = bulletStopper;
	}

	public boolean isBulletStopper() {
		return bulletStopper;
	}

	public void setRobotStopper(boolean robotStopper) {
		this.robotStopper = robotStopper;
	}

	public boolean isRobotStopper() {
		return robotStopper;
	}

	public void setRobotConscious(boolean robotConscious) {
		this.robotConscious = robotConscious;
	}

	public boolean isRobotConscious() {
		return robotConscious;
	}

	public void setDynamic(boolean dynamic) {
		this.dynamic = dynamic;
	}

	public boolean isDynamic() {
		return dynamic;
	}
	
	public void setTeam(int teamNumber) {
		this.teamNumber = teamNumber;
	}
	
	public int getTeam() {
		// by default, return 0 to indicate no team ownership
		return 0;
	}
	
	public void hitByRobot(RobotPeer robot) {// do nothing by default
	}
	
	public void hitByBullet() {// do nothing by default
	}

	public void roundStarted() {// do nothing by default
	}
	
	public void turnUpdate() {// do nothing by default
	}
	
	public abstract boolean shouldDraw();
}
