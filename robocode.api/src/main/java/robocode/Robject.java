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

package robocode;

import java.awt.geom.Rectangle2D;

/**
 * This class is the basic object used by the Robocode extensibility framework
 * 
 * @author Joshua Galecki (original)
 *
 */
public abstract class Robject {

	private int x;
	private int y;
	private int width;
	private int height;
	private String type;
	private boolean robotStopper;
	private boolean bulletStopper;
	private boolean scanStopper;
	
	private boolean scannable;
	private boolean robotConscious;
	
	public Robject(String type, int x, int y, int width, int height, boolean stopsTanks,
			boolean stopsBullets, boolean stopsScans, boolean scannable, boolean tankConscious)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.type = type;
		robotStopper = stopsTanks;
		bulletStopper = stopsBullets;
		scanStopper = stopsScans;
		this.setRobotConscious(tankConscious);
		this.scannable = scannable;
	}

	public Rectangle2D.Float getBoundaryRect()
	{
		return new Rectangle2D.Float(x, y, width, height);
	}
	
	public void setX(int x) {
		this.x = x;
	}
	public int getX() {
		return x;
	}
	public void setY(int y) {
		this.y = y;
	}
	public int getY() {
		return y;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getWidth() {
		return width;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public int getHeight() {
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
	
	public void hitByRobot() {
		//do nothing by default
	}
	
	public void hitByBullet() {
		//do nothing by default
	}
	
	public abstract boolean shouldDraw();
}
