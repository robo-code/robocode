/*******************************************************************************
 * Copyright (c) 2001, 2007 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Mathew Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Rewritten
 *******************************************************************************/
package robocode.battlefield;


import robocode.util.BoundingRectangle;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public class DefaultBattleField implements BattleField {
	private BoundingRectangle boundingBox;

	public DefaultBattleField(int width, int height) {
		super();
		this.boundingBox = new BoundingRectangle(0, 0, width, height);
	}

	public BoundingRectangle getBoundingBox() {
		return boundingBox;
	}

	public int getWidth() {
		return (int) boundingBox.width;
	}

	public void setWidth(int newWidth) {
		boundingBox.width = newWidth;
	}

	public int getHeight() {
		return (int) boundingBox.height;
	}

	public void setHeight(int newHeight) {
		boundingBox.height = newHeight;
	}
}
