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

package robocode.control.snapshot;


import java.awt.geom.Rectangle2D;


/**
 * This is an interface for a snapshot of a robject (Robot OBJECT)
 * 
 * @author Joshua Galecki (original)
 *
 */
public interface IRobjectSnapshot {
	
	double getX();
	
	double getY();

	double getWidth();
	
	double getHeight();
	
	String getType();

	/**
	 * Returns a rectangle with equal height and width at the origin (0,0).
	 * This should go through an affine transorm to get the correct x and y. 
	 * 
	 * @return a rectangle with equal height and width at the origin (0,0).
	 */
	Rectangle2D getPaintRect();
	
	boolean shouldDraw();
	
	int getTeam();
}
