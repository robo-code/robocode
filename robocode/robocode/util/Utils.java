/*******************************************************************************
 * Copyright (c) 2001, 2007 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 * 
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Moved all methods to classes like FileUtil, StringUtil, WindowUtil,
 *       Logger etc. exception for the following methods, which have been kept
 *       here as legacy robots make use of these methods:
 *       - normalAbsoluteAngle()
 *       - normalNearAbsoluteAngle()
 *       - normalRelativeAngle()
 *******************************************************************************/
package robocode.util;


import static java.lang.Math.*;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public class Utils {

	private final static double TWO_PI = 2 * PI;
	private final static double THREE_PI_OVER_TWO = 3 * PI / 2;
	private final static double PI_OVER_TWO = PI / 2;

	public static double normalAbsoluteAngle(double angle) {
		if (angle >= 0 && angle < 2.0 * PI) {
			return angle;
		}
		double fixedAngle = angle;

		while (fixedAngle < 0) {
			fixedAngle += 2 * PI;
		}
		while (fixedAngle >= 2 * PI) {
			fixedAngle -= 2 * PI;
		}
	
		return fixedAngle;
	}

	public static double normalNearAbsoluteAngle(double angle) {
		double fixedAngle = normalAbsoluteAngle(angle);

		if (isNear(fixedAngle, 0)) {
			fixedAngle = 0;
		} else if (isNear(fixedAngle, PI_OVER_TWO)) {
			fixedAngle = PI_OVER_TWO;
		} else if (isNear(fixedAngle, PI)) {
			fixedAngle = PI;
		} else if (isNear(fixedAngle, THREE_PI_OVER_TWO)) {
			fixedAngle = THREE_PI_OVER_TWO;
		} else if (isNear(fixedAngle, TWO_PI)) {
			fixedAngle = 0;
		}
	
		return fixedAngle;
	}

	public static double normalRelativeAngle(double angle) {
		if (angle > -PI && angle <= PI) {
			return angle;
		}
		double fixedAngle = angle;

		while (fixedAngle <= -PI) {
			fixedAngle += 2 * PI;
		}
		while (fixedAngle > PI) {
			fixedAngle -= 2 * PI;
		}
		return fixedAngle;
	}

	private static boolean isNear(double angle1, double angle2) {
		if (abs(angle1 - angle2) < .00001) {
			return true;
		}
		return false;
	}
}
