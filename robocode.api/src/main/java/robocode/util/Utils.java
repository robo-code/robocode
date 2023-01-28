/*
 * Copyright (c) 2001-2023 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package robocode.util;


import robocode.control.RandomFactory;

import static java.lang.Math.PI;
import java.util.Random;


/**
 * Utility class that provide methods for normalizing angles.
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public class Utils {

	private final static double TWO_PI = 2 * PI;
	private final static double THREE_PI_OVER_TWO = 3 * PI / 2;
	private final static double PI_OVER_TWO = PI / 2;
    private final static double PI_OVER_FOUR = PI / 4;
    private final static double PI_OVER_EIGHT = PI / 8;
	public static final double NEAR_DELTA = .00001;

    private final static double NORTH = 0 * PI_OVER_FOUR;
    private final static double NORTH_EAST = 1 * PI_OVER_FOUR;
    private final static double EAST = 2 * PI_OVER_FOUR;
    private final static double SOUTH_EAST = 3 * PI_OVER_FOUR;
    private final static double SOUTH = 4 * PI_OVER_FOUR;
    private final static double SOUTH_WEST = 5 * PI_OVER_FOUR;
    private final static double WEST = 6 * PI_OVER_FOUR;
    private final static double NORTH_WEST = 7 * PI_OVER_FOUR;

	// Hide the default constructor as this class only provides static method
	private Utils() {}

	/**
	 * Normalizes an angle to an absolute angle.
	 * The normalized angle will be in the range from 0 to 2*PI, where 2*PI
	 * itself is not included.
	 *
	 * @param angle the angle to normalize
	 * @return the normalized angle that will be in the range of [0,2*PI[
	 */
	public static double normalAbsoluteAngle(double angle) {
		return (angle %= TWO_PI) >= 0 ? angle : (angle + TWO_PI);
	}

	/**
	 * Normalizes an angle to an absolute angle.
	 * The normalized angle will be in the range from 0 to 360, where 360
	 * itself is not included.
	 *
	 * @param angle the angle to normalize
	 * @return the normalized angle that will be in the range of [0,360[
	 */
	public static double normalAbsoluteAngleDegrees(double angle) {
		return (angle %= 360) >= 0 ? angle : (angle + 360);
	}

	/**
	 * Normalizes an angle to a relative angle.
	 * The normalized angle will be in the range from -PI to PI, where PI
	 * itself is not included.
	 *
	 * @param angle the angle to normalize
	 * @return the normalized angle that will be in the range of [-PI,PI[
	 */
	public static double normalRelativeAngle(double angle) {
		return (angle %= TWO_PI) >= 0 ? (angle < PI) ? angle : angle - TWO_PI : (angle >= -PI) ? angle : angle + TWO_PI;
	}

	/**
	 * Normalizes an angle to a relative angle.
	 * The normalized angle will be in the range from -180 to 180, where 180
	 * itself is not included.
	 *
	 * @param angle the angle to normalize
	 * @return the normalized angle that will be in the range of [-180,180[
	 */
	public static double normalRelativeAngleDegrees(double angle) {
		return (angle %= 360) >= 0 ? (angle < 180) ? angle : angle - 360 : (angle >= -180) ? angle : angle + 360;
	}

	/**
	 * Normalizes an angle to be near an absolute angle.
	 * The normalized angle will be in the range from 0 to 360, where 360
	 * itself is not included.
	 * If the normalized angle is near to 0, 90, 180, 270 or 360, that
	 * angle will be returned. The {@link #isNear(double, double) isNear}
	 * method is used for defining when the angle is near one of angles listed
	 * above.
	 *
	 * @param angle the angle to normalize
	 * @return the normalized angle that will be in the range of [0,360[
	 * @see #normalAbsoluteAngle(double)
	 * @see #isNear(double, double)
	 */
	public static double normalNearAbsoluteAngleDegrees(double angle) {
		angle = (angle %= 360) >= 0 ? angle : (angle + 360);

		if (isNear(angle, 180)) {
			return 180;
		} else if (angle < 180) {
			if (isNear(angle, 0)) {
				return 0;
			} else if (isNear(angle, 90)) {
				return 90;
			}
		} else {
			if (isNear(angle, 270)) {
				return 270;
			} else if (isNear(angle, 360)) {
				return 0;
			}
		}
		return angle;
	}

	/**
	 * Normalizes an angle to be near an absolute angle.
	 * The normalized angle will be in the range from 0 to 2*PI, where 2*PI
	 * itself is not included.
	 * If the normalized angle is near to 0, PI/2, PI, 3*PI/2 or 2*PI, that
	 * angle will be returned. The {@link #isNear(double, double) isNear}
	 * method is used for defining when the angle is near one of angles listed
	 * above.
	 *
	 * @param angle the angle to normalize
	 * @return the normalized angle that will be in the range of [0,2*PI[
	 * @see #normalAbsoluteAngle(double)
	 * @see #isNear(double, double)
	 */
	public static double normalNearAbsoluteAngle(double angle) {
		angle = (angle %= TWO_PI) >= 0 ? angle : (angle + TWO_PI);

		if (isNear(angle, PI)) {
			return PI;
		} else if (angle < PI) {
			if (isNear(angle, 0)) {
				return 0;
			} else if (isNear(angle, PI_OVER_TWO)) {
				return PI_OVER_TWO;
			}
		} else {
			if (isNear(angle, THREE_PI_OVER_TWO)) {
				return THREE_PI_OVER_TWO;
			} else if (isNear(angle, TWO_PI)) {
				return 0;
			}
		}
		return angle;
	}

	/**
	 * Tests if the two {@code double} values are near to each other.
	 * It is recommended to use this method instead of testing if the two
	 * doubles are equal using an this expression: {@code value1 == value2}.
	 * The reason being, that this expression might never become
	 * {@code true} due to the precision of double values.
	 * Whether or not the specified doubles are near to each other is defined by
	 * the following expression:
	 * {@code (Math.abs(value1 - value2) &lt; .00001)}
	 *
	 * @param value1 the first double value
	 * @param value2 the second double value
	 * @return {@code true} if the two doubles are near to each other;
	 *         {@code false} otherwise.
	 */
	public static boolean isNear(double value1, double value2) {
		return (Math.abs(value1 - value2) < NEAR_DELTA);
	}

	/**
	 * Returns random number generator. It might be configured for repeatable behavior by setting -DRANDOMSEED option.
	 *
	 * @return random number generator
	 */
	public static Random getRandom() {
		return RandomFactory.getRandom();
	}

	/**
	 * Throws AssertionError when the param value is null. It could be used to express validation of invariant.
	 * @param message of the eventual error
	 * @param value tested value
	 */
    public static void assertNotNull(String message, Object value) {
        if (value == null) {
            throw new AssertionError(message);
        }
    }

	/**
	 * Throws AssertionError when the params expected and actual do not equal each other. It could be used to express validation of invariant.
	 * @param message of the eventual error
	 * @param expected expected value
	 * @param actual tested value
	 */
    public static void assertEquals(String message, Object expected, Object actual) {
        if (expected == null && actual == null) {
            return;
        }
        if (expected == null || actual == null) {
            throw new AssertionError(message);
        }
        if (!expected.equals(actual)) {
            throw new AssertionError(message);
        }
    }

	/**
	 * Throws AssertionError when the assertion is false. It could be used to express validation of invariant.
	 * @param message of the eventual error
	 * @param assertion expected to be true
	 */
	public static void assertTrue(String message, boolean assertion) {
		if (!assertion) {
			throw new AssertionError(message);
		}
	}

	/**
	 * Throws AssertionError when the params expected and actual do not within .00001 difference. It could be used to express validation of invariant.
	 * @param message of the eventual error
	 * @param expected expected value
	 * @param actual tested value
	 */
    public static void assertNear(String message, double expected, double actual) {
        if (!isNear(expected, actual)) {
            throw new AssertionError(message + " expected:" + expected + " actual:" + actual);
        }
    }

	/**
	 * Returns approximate cardinal direction for absolute angle in radians, like N,NE,E,SE,S,SW,W,NW
	 * @param angle absolute angle in radians
	 * @return N,NE,E,SE,S,SW,W,NW
	 */
	public static String angleToApproximateDirection(double angle) {
		double absoluteAngle = normalAbsoluteAngle(angle);
		if (absoluteAngle < NORTH + PI_OVER_EIGHT) {
			return "N";
		} else if (absoluteAngle < NORTH_EAST + PI_OVER_EIGHT) {
			return "NE";
		} else if (absoluteAngle < EAST + PI_OVER_EIGHT) {
			return "E";
		} else if (absoluteAngle < SOUTH_EAST + PI_OVER_EIGHT) {
			return "SE";
		} else if (absoluteAngle < SOUTH + PI_OVER_EIGHT) {
			return "S";
		} else if (absoluteAngle < SOUTH_WEST + PI_OVER_EIGHT) {
			return "SW";
		} else if (absoluteAngle < WEST + PI_OVER_EIGHT) {
			return "W";
		} else if (absoluteAngle < NORTH_WEST + PI_OVER_EIGHT) {
			return "NW";
		} else {
			return "N";
		}
	}
}
