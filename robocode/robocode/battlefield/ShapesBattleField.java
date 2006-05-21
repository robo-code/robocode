/*******************************************************************************
 * Copyright (c) 2001, 2006 Mathew Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew Nelson - initial API and implementation
 *******************************************************************************/
package robocode.battlefield;


import robocode.util.*;

/**
 * Insert the type's description here.
 * Creation date: (12/14/2000 2:41:50 PM)
 * @author: Mathew A. Nelson
 */
import java.awt.geom.*;
import java.awt.*;


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

	/**
	 * Insert the method's description here.
	 * Creation date: (10/5/2001 5:02:52 PM)
	 * @param osg java.awt.Graphics
	 */
	public void drawShapes(Graphics2D osg) {
		return; /*
		 osg.setColor(Color.red);
		 for (int i = 0; i < shapes.length; i++)
		 osg.fill(shapes[i]);
		 */
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/23/2000 4:47:23 PM)
	 * @return BoundingRectangle
	 */
	public BoundingRectangle getBoundingBox() {
		return boundingBox;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/14/2000 2:42:43 PM)
	 * @return int
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/14/2000 2:42:34 PM)
	 * @return int
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/14/2000 2:42:43 PM)
	 * @param newHeight int
	 */
	public void setHeight(int newHeight) {
		height = newHeight;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/14/2000 2:42:34 PM)
	 * @param newWidth int
	 */
	public void setWidth(int newWidth) {
		width = newWidth;
	}
}
