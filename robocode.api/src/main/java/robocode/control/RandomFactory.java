/**
 * Copyright (c) 2001-2014 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 */
package robocode.control;


import static net.sf.robocode.io.Logger.logError;
import static net.sf.robocode.io.Logger.logWarning;

import java.lang.reflect.Field;
import java.util.Random;


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

	private static boolean warningNotSupportedLogged;
	private static boolean isDeterministic;

	static {
		randomNumberGenerator = new Random();
	}

	public boolean isDeterministic() {
		return isDeterministic;
	}

	/**
	 * Returns the random number generator used for generating a stream of
	 * random numbers.
	 *
	 * @return a {@link java.util.Random} instance.
	 * @see java.util.Random
	 */
	public static Random getRandom() {
		if (randomNumberGenerator == null) {
			try {
				Math.random();
				final Field field = Math.class.getDeclaredField("randomNumberGenerator");
				final boolean savedFieldAccessible = field.isAccessible();

				field.setAccessible(true);
				randomNumberGenerator = (Random) field.get(null);
				field.setAccessible(savedFieldAccessible);
			} catch (NoSuchFieldException e) {
				logWarningNotSupported();
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
	 * @param random a {@link java.util.Random} instance.
	 * @see java.util.Random
	 */
	public static void setRandom(Random random) {
		randomNumberGenerator = random;
		try {
			Math.random();
			final Field field = Math.class.getDeclaredField("randomNumberGenerator");
			final boolean savedFieldAccessible = field.isAccessible();

			field.setAccessible(true);
			field.set(null, randomNumberGenerator);
			field.setAccessible(savedFieldAccessible);
		} catch (NoSuchFieldException e) {
			logWarningNotSupported();
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
		isDeterministic = true;
	}

	/**
	 * Logs a warning that the deterministic random feature is not supported by the JVM.
	 */
	private static void logWarningNotSupported() {
		if (!(warningNotSupportedLogged || System.getProperty("RANDOMSEED", "none").equals("none"))) {
			logWarning(
					"The deterministic random generator feature is not supported by this JVM:\n"
							+ System.getProperty("java.vm.vendor") + " " + System.getProperty("java.vm.name") + " "
							+ System.getProperty("java.vm.version"));

			warningNotSupportedLogged = true;
		}
	}
}
