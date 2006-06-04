/*******************************************************************************
 * Copyright (c) 2001-2006 Mathew A. Nelson and Robocode contributors
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
 * @author Mathew A. Nelson (original)
 */
public class DefaultBattleField implements BattleField {
	public int width;
	public int height;
	public BoundingRectangle boundingBox;

	/**
	 * BattleField constructor.
	 */
	public DefaultBattleField(int width, int height) {
		super();
		this.width = width;
		this.height = height;
		this.boundingBox = new BoundingRectangle(0, 0, width, height);
	}

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
