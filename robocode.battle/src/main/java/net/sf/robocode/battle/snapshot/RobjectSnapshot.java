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

package net.sf.robocode.battle.snapshot;


import java.awt.geom.Rectangle2D;

import net.sf.robocode.battle.peer.RobjectPeer;

import robocode.control.snapshot.IRobjectSnapshot;


/**
 * This is class transmits information about a robject
 * 
 * @author Joshua Galecki (original)
 *
 */
public class RobjectSnapshot implements IRobjectSnapshot {

	private double x;
	private double y;
	private double width;
	private double height;
	private String type;
	private boolean draw;
	private int teamNumber;

	/**
	 * {@inheritDoc}
	 */
	public double getHeight() {
		return height;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getType() {
		return type; 
	}

	/**
	 * {@inheritDoc}
	 */
	public double getWidth() {
		return width;
	}

	/**
	 * {@inheritDoc}
	 */
	public double getX() {
		return x;
	}

	/**
	 * {@inheritDoc}
	 */
	public double getY() {
		return y;
	}
	
	public RobjectSnapshot() {}

	public RobjectSnapshot(RobjectPeer robject) {
		this.x = robject.getX();
		this.y = robject.getY();
		this.width = robject.getWidth();
		this.height = robject.getHeight();
		this.type = robject.getType();
		this.draw = robject.shouldDraw();
		this.teamNumber = robject.getTeam();
	}

	public Rectangle2D getPaintRect() {
		return new Rectangle2D.Double(0, 0, width, height);
	}

	public boolean shouldDraw() {
		return draw;
	}

	public int getTeam() {
		return teamNumber;
	}
}
