/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.test.helpers;


import robocode.util.Utils;


/**
 * @author Pavel Savara (original)
 */
public class Assert extends org.junit.Assert {
	public static void assertNear(double v1, double v2) {
		org.junit.Assert.assertEquals(v1, v2, Utils.NEAR_DELTA);
	}

	public static void allAssertNear(double v1, double v2) {
		try {
			assertNear(v1, v2);
		} catch (Throwable ex) {
			ex.printStackTrace(System.err);
		}
	}

	public static <T> void allAssertThat(T t, org.hamcrest.Matcher<T> tMatcher) {
		try {
			org.junit.Assert.assertThat(t, tMatcher);
		} catch (Throwable ex) {
			ex.printStackTrace(System.err);
		}
	}

}
