/*******************************************************************************
 * Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 *
 * Contributors:
 *     Philip Johnson
 *     - Idea and adaptation of internal test bed
 *     Pavel Savara
 *     - Included in Robocode as extension
 *******************************************************************************/

package robocode.control.testing;


import robocode.util.Utils;


/**
 * Provides some additional Assert methods for use in Robocode.
 *
 * @author Philip Johnson
 * @author Pavel Savara
 */
public class RobotTestBedAssert extends org.junit.Assert {

	/**
	 * Asserts that the two values are "sufficiently close".
	 * Define sufficiently close using Utils.NEAR_DELTA.
	 *
	 * @param value1 First value.
	 * @param value2 Second value.
	 */
	public static void assertNear(double value1, double value2) {
		org.junit.Assert.assertEquals(value1, value2, Utils.NEAR_DELTA);
	}
}
