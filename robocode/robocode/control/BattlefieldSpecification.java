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
package robocode.control;


/**
 * Defines a battlefield
 * @author Mathew A. Nelson
 */
public class BattlefieldSpecification {
	private int width;
	private int height;
	
	/**
	 * Creates a standard 800 x 600 battlefield
	 */
	public BattlefieldSpecification() {
		this(800, 600);
	}
	
	/**
	 * Creates a battlefield of any width and height.
	 * @param width Width of this battlefield
	 * @param height Height of this battlefield
	 */
	public BattlefieldSpecification(int width, int height) {
		if (width >= 400 && width <= 5000) {
			this.width = width;
		}
		if (height >= 400 && height <= 5000) {
			this.height = height;
		}
	}

	/**
	 * Gets the height of this battlefield.
	 * @return Returns the height of this battlefield.
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Gets the width of this battlefield.
	 * @return Returns the width of this battlefield.
	 */
	public int getWidth() {
		return width;
	}

}

