/*
 * Copyright (c) 2001-2026 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.util;

import org.junit.jupiter.api.Test;

import java.text.NumberFormat;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StringUtilTest {

	@Test
	void keepsBasicLatin() {
		assertEquals("1st: sample.Walls\t1256 (36 %)", StringUtil.toBasicLatin("1st: sample.Walls\t1256 (36 %)"));
	}

	@Test
	void encodesOtherCharacters() {
		assertEquals("Rob\\u00F8t", StringUtil.toBasicLatin("Robøt"));
	}

	@Test
	void writesSpacesAsNormalSpaces() {
		// The no-break space and the narrow no-break space
		assertEquals("36 % 36 %", StringUtil.toBasicLatin("36 % 36 %"));
	}

	@Test
	void percentagesHaveNoEncodedCharactersInAnyLanguage() {
		for (Locale locale : new Locale[] { Locale.US, new Locale("da", "DK"), Locale.GERMANY, Locale.FRANCE }) {
			String percent = NumberFormat.getPercentInstance(locale).format(0.36);

			assertEquals(percent.replaceAll("\\p{Zs}", " "), StringUtil.toBasicLatin(percent), locale.toString());
		}
	}
}
