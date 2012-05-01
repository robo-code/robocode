/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
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
package net.sf.robocode.ui.battleview;


import net.sf.robocode.battle.BoundingRectangle;

import java.io.Serializable;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public class BattleField implements Serializable {
	private static final long serialVersionUID = 1L;

	private final BoundingRectangle boundingBox;

	public BattleField(int width, int height) {
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
