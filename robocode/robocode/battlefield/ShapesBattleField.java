/*******************************************************************************
 * Copyright (c) 2001-2006 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *******************************************************************************/
package robocode.battlefield;


import robocode.util.*;

import java.awt.geom.*;
import java.awt.*;


/**
 * @author Mathew A. Nelson (original)
 */
public class ShapesBattleField implements BattleField {
	public int width;
	public int height;
	public BoundingRectangle boundingBox;
	public Shape shapes[];

	/**
	 * BattleField constructor comment.
	 */
	public ShapesBattleField(int width, int height) {
		super();
		this.width = width;
		this.height = height;
		this.boundingBox = new BoundingRectangle(0, 0, width, height);
		this.shapes = new Shape[3];
		shapes[0] = new Ellipse2D.Double(200, 200, 260, 100);
		shapes[1] = new BoundingRectangle(400, 450, 200, 20);
		shapes[2] = new BoundingRectangle(580, 470, 20, 130);
	}

	public void drawShapes(Graphics2D osg) {}

	public BoundingRectangle getBoundingBox() {
		return boundingBox;
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	public void setHeight(int newHeight) {
		height = newHeight;
	}

	public void setWidth(int newWidth) {
		width = newWidth;
	}
}
