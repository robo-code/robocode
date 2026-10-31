/*
 * Copyright (c) 2001-2026 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package robocode.util;


/**
 * Used internally by Robocode for making the random numbers of robots deterministic, when Robocode is set up for
 * deterministic battles, e.g. with the RANDOMSEED system property or in tests.
 * <p>
 * When robot classes are loaded, calls to {@link Math#random()} and {@link StrictMath#random()} are replaced with
 * calls to {@link #random()}, which uses the random number generator of Robocode. With Java 8, Robocode replaced the
 * random number generator of the Math class instead, but this is not possible with newer Java versions.
 * Robots should not use this class directly.
 *
 * @author Flemming N. Larsen (original)
 */
public final class RobotMath {

	private RobotMath() {
	}

	/**
	 * Returns a random number like {@link Math#random()}, but from the random number generator of Robocode.
	 *
	 * @return a random number greater than or equal to 0.0 and less than 1.0.
	 */
	public static double random() {
		return Utils.getRandom().nextDouble();
	}
}
