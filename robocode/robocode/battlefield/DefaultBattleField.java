/*******************************************************************************
 * Copyright (c) 2001-2006 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Rewritten
 *******************************************************************************/
package robocode.battlefield;


import java.awt.geom.Rectangle2D;


/**
 * @author Mathew A. Nelson (original)
 */
public class DefaultBattleField implements BattleField {

	private int width;
	private int height;
	private Rectangle2D.Float boundingBox;

	/**
	 * BattleField constructor.
	 */
	public DefaultBattleField(int width, int height) {
		super();
		this.width = width;
		this.height = height;
		this.boundingBox = new Rectangle2D.Float(0, 0, width, height);
	}

	public Rectangle2D getBoundingBox() {
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
		boundingBox.height = newHeight;
	}

	public void setWidth(int newWidth) {
		width = newWidth;
		boundingBox.width = newWidth;
	}
}
