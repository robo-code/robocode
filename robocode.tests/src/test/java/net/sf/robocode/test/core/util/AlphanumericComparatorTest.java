/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.test.core.util;


import java.util.Arrays;

import net.sf.robocode.util.AlphanumericComparator;

import org.junit.Assert;
import org.junit.Test;


/**
 * @author Flemming N. Larsen (original)
 */
public class AlphanumericComparatorTest {
	@Test
	public void correctSorting() {
		
		// Our unsorted strings that must be sorted
		final String[] unsortedStrings = {
			null, "", " ", "alpha2", "alpha1", "bEtA", "BeTa", "alpha", "Alpha2", "Alpha1", "Alpha", "1.2", "1.22",
			"1.22A", "1.3", "2.10a.1", "2.1.1", "2.10", "2.1a", "2.10.2", "1c", "1h", "1p", "alpha", "beta", "alPHA",
			"beTA", "ALpha", "BEta", "ALPHA", "BETA"
		};

		// The expected result, when the unsorted strings have been sorted correctly
		final String[] correctlySortedStrings = {
			"1.2", "1.3", "1.22A", "1.22", "1c", "1h", "1p", "2.1.1", "2.1a", "2.10.2", "2.10a.1", "2.10", " ", "ALPHA",
			"ALpha", "Alpha1", "Alpha2", "Alpha", "alPHA", "alpha1", "alpha2", "alpha", "alpha", "BETA", "BEta", "BeTa",
			"bEtA", "beTA", "beta", "", null
		};

		// Make sure the input and expected output string arrays have the same length 
		Assert.assertEquals(unsortedStrings.length, correctlySortedStrings.length);

		// Sort the unsorted strings
		Arrays.sort(unsortedStrings, new AlphanumericComparator());

		// Check the result against our expected result
		boolean sortedCorrectly = true;

		for (int i = 0; i < unsortedStrings.length; i++) {
			String str1 = unsortedStrings[i];
			String str2 = correctlySortedStrings[i];
			
			if (!(str1 == null && str2 == null) && !str1.equals(str2)) {
				sortedCorrectly = false;
				break;
			}
		}
		Assert.assertTrue(sortedCorrectly);
	}
}
