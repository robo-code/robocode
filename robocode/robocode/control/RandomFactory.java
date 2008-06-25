/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *     Flemming N. Larsen
 *     - Javadocs
 *******************************************************************************/
package robocode.control;


import static robocode.io.Logger.logError;

import java.util.Random;
import java.lang.reflect.Field;


/**
 * The RandomFactory is used for controlling the generation of random numbers,
 * and supports generating random numbers that are deterministic, which is
 * useful for testing purposes. 
 *
 * @author Pavel Savara (original)
 *
 * @since 1.6.1
 */
public class RandomFactory {
	private static Random randomNumberGenerator;

	/**
	 * Returns the random number generator used for generating a stream of
	 * random numbers.
	 *
	 * @see java.util.Random
	 *
	 * @return a {@link java.util.Random} instance.
	 */
	public static Random getRandom() {
		if (randomNumberGenerator == null) {
			try {
				Math.random();
				final Field field = Math.class.getDeclaredField("randomNumberGenerator");

				field.setAccessible(true);
				randomNumberGenerator = (Random) field.get(null);
				field.setAccessible(false);
			} catch (NoSuchFieldException e) {
				logError(e);
				randomNumberGenerator = new Random();
			} catch (IllegalAccessException e) {
				logError(e);
				randomNumberGenerator = new Random();
			}
		}
		return randomNumberGenerator;
	}

	/**
	 * Sets the random number generator instance used for generating a
	 * stream of random numbers.
	 *
	 * @see java.util.Random
	 *
	 * @param random a {@link java.util.Random} instance.
	 */
	public static void setRandom(Random random) {
		randomNumberGenerator = random;
		try {
			Math.random();
			final Field field = Math.class.getDeclaredField("randomNumberGenerator");

			field.setAccessible(true);
			field.set(null, randomNumberGenerator);
			field.setAccessible(false);
		} catch (NoSuchFieldException e) {
			logError(e);
		} catch (IllegalAccessException e) {
			logError(e);
		}
        
		// TODO ZAMO using Robot classloader inject seed also for all instances being created by robots
	}

	/**
	 * Resets the random number generator instance to be deterministic when
	 * generating random numbers.
	 *
	 * @param seed the seed to use for the new deterministic random generator.
	 */
	public static void resetDeterministic(long seed) {
		setRandom(new Random(seed));
	}
}
