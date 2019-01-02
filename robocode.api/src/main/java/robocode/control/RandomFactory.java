/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package robocode.control;

import static net.sf.robocode.io.Logger.logWarning;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Random;

/**
 * The RandomFactory is used for controlling the generation of random numbers,
 * and supports generating random numbers that are deterministic, which is
 * useful for testing purposes.
 *
 * @author Pavel Savara (original)
 * @author Xor (fixed for Java 8)
 * @author Flemming N. Larsen (fixed for Java 8)
 *
 * @since 1.6.1
 */
public class RandomFactory {
	private static Random randomNumberGenerator = new Random();

	private static boolean warningNotSupportedLogged;
	private static boolean isDeterministic;

	public boolean isDeterministic() {
		return isDeterministic;
	}

	/**
	 * Returns the random number generator used for generating a stream of random
	 * numbers.
	 *
	 * @return a {@link java.util.Random} instance.
	 * @see java.util.Random
	 */
	public static Random getRandom() {
		if (randomNumberGenerator == null) {
			Field field = getRandomNumberGeneratorField();
			if (field != null) {
				try {
					randomNumberGenerator = getRandomNumberGenerator(field);
				} catch (IllegalAccessException e) {
					logWarningNotSupported();
				}
			}
			if (randomNumberGenerator == null) {
				logWarningNotSupported();
				randomNumberGenerator = new Random();
			}
		}
		return randomNumberGenerator;
	}

	private static Field getRandomNumberGeneratorField() {
		try {
			return Math.class.getDeclaredField("randomNumberGenerator");
		} catch (NoSuchFieldException e) {
			try {
				final Class<?> clazz = Class.forName("java.lang.Math$RandomNumberGeneratorHolder");
				return clazz.getDeclaredField("randomNumberGenerator");
			} catch (NoSuchFieldException e1) {
				logWarningNotSupported();
			} catch (ClassNotFoundException e1) {
				logWarningNotSupported();
			}
		}
		return null;
	}

	private static Random getRandomNumberGenerator(Field field) throws IllegalAccessException {
		Math.random();
		final boolean savedFieldAccessible = field.isAccessible();

		field.setAccessible(true);
		randomNumberGenerator = (Random) field.get(null);
		field.setAccessible(savedFieldAccessible);

		return randomNumberGenerator;
	}

	/**
	 * Sets the random number generator instance used for generating a stream of
	 * random numbers.
	 *
	 * @param random
	 *            a {@link java.util.Random} instance.
	 * @see java.util.Random
	 */
	public static void setRandom(Random random) {
		randomNumberGenerator = random;

		Field field = getRandomNumberGeneratorField();
		if (field != null) {
			try {
				setRandomNumberGenerator(field);
			} catch (Exception e) {
				logWarningNotSupported();
			}
		} else {
			logWarningNotSupported();
		}
	}

	private static void setRandomNumberGenerator(Field field) throws IllegalAccessException, NoSuchFieldException {
		Math.random();
		final boolean savedFieldAccessible = field.isAccessible();
		field.setAccessible(true);

		Field modifiersField = Field.class.getDeclaredField("modifiers");

		final boolean savedModifiersAccessible = modifiersField.isAccessible();
		modifiersField.setAccessible(true);
		try {
			int oldModifiers = field.getModifiers();
			try {
				modifiersField.setInt(field, oldModifiers & ~Modifier.FINAL);

				field.set(null, randomNumberGenerator);
			} finally {
				modifiersField.setInt(field, oldModifiers);
			}
		} finally {
			modifiersField.setAccessible(savedModifiersAccessible);
		}

		field.setAccessible(savedFieldAccessible);
	}

	/**
	 * Resets the random number generator instance to be deterministic when
	 * generating random numbers.
	 *
	 * @param seed
	 *            the seed to use for the new deterministic random generator.
	 */
	public static void resetDeterministic(long seed) {
		setRandom(new Random(seed));
		isDeterministic = true;
	}

	/**
	 * Logs a warning that the deterministic random feature is not supported by the
	 * JVM.
	 */
	private static void logWarningNotSupported() {
		if (!(warningNotSupportedLogged || System.getProperty("RANDOMSEED", "none").equals("none"))) {
			logWarning("The deterministic random generator feature is not supported by this JVM:\n"
					+ System.getProperty("java.vm.vendor") + " " + System.getProperty("java.vm.name") + " "
					+ System.getProperty("java.vm.version"));

			warningNotSupportedLogged = true;
		}
	}
}
