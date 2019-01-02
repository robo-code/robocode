/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package robocode.control;


/**
 * Defines the size of a battlefield, which is a part of the {@link BattleSpecification}.
 *
 * @see BattleSpecification#BattleSpecification(int, BattlefieldSpecification, RobotSpecification[])
 * @see BattleSpecification#BattleSpecification(int, long, double, BattlefieldSpecification, RobotSpecification[])
 * @see BattleSpecification#getBattlefield()
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public class BattlefieldSpecification implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private final int width;
	private final int height;

	/**
	 * Creates a standard 800 x 600 battlefield.
	 */
	public BattlefieldSpecification() {
		this(800, 600);
	}

	/**
	 * Creates a battlefield of the specified width and height.
	 *
	 * @param width  the width of the battlefield, where 400 <= width <= 5000.
	 * @param height the height of the battlefield, where 400 <= height <= 5000.
	 * @throws IllegalArgumentException if the width or height < 400 or > 5000.
	 */
	public BattlefieldSpecification(int width, int height) {
		if (width < 400 || width > 5000) {
			throw new IllegalArgumentException("width must be: 400 <= width <= 5000");
		}
		if (height < 400 || height > 5000) {
			throw new IllegalArgumentException("height must be: 400 <= height <= 5000");
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
