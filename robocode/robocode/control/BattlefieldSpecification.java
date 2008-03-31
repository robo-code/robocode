/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - This class now implements java.io.Serializable
 *     - Updated Javadocs
 *******************************************************************************/
package robocode.control;


/**
 * Defines the size of a battlefield.
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public class BattlefieldSpecification implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private int width;
	private int height;

	/**
	 * Creates a standard 800 x 600 battlefield.
	 */
	public BattlefieldSpecification() {
		this(800, 600);
	}

	/**
	 * Creates a battlefield of the specified width and height.
	 *
	 * @param width  the width of the battlefield, where 400 >= width < 5000.
	 * @param height the height of the battlefield, where 400 >= height < 5000.
	 * @throws IllegalArgumentException if the width or height is less than 400
	 *                                  or greater than 5000
	 */
	public BattlefieldSpecification(int width, int height) {
		if (width < 400 || width > 5000) {
			throw new IllegalArgumentException("width must be: 400 >= width < 5000");
		}
		if (height < 400 || height > 5000) {
			throw new IllegalArgumentException("height must be: 400 >= height < 5000");
		}

		this.width = width;
		this.height = height;
	}

	/**
	 * Returns the width of this battlefield.
	 *
	 * @return the width of this battlefield.
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Returns the height of this battlefield.
	 *
	 * @return the height of this battlefield.
	 */
	public int getHeight() {
		return height;
	}
}
