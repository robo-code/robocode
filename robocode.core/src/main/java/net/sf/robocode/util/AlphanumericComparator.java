/*******************************************************************************
 * Copyright (c) 2001, 2009 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Flemming N. Larsen
 *     - Initial implementation
 *******************************************************************************/
package net.sf.robocode.util;


import java.util.Arrays;
import java.util.Comparator;

import static java.lang.Character.isDigit;


/**
 * This is a comparator used for comparing two alphanumeric strings.
 *
 * @author Flemming N. Larsen
 */
public class AlphanumericComparator implements Comparator<String>, java.io.Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Compares two alphanumeric strings.
	 * 
	 * @param str1 the first string to be compared.
	 * @param str2 the second string to be compared.
	 * @return a negative integer, zero, or a positive integer as the first string is less than, equal to, or greater
	 *         than the second. 
	 */
	public int compare(String str1, String str2) {
		if (str1 == null && str2 == null) {
			return 0;
		}
		if (str1 != null && str2 == null) {
			return -1;
		}
		if (str1 == null && str2 != null) {
			return 1;
		}

		final int len1 = str1.length();
		final int len2 = str2.length();

		int index1 = 0;
		int index2 = 0;

		while (index1 < len1 && index2 < len2) {
			char ch1 = str1.charAt(index1);
			char ch2 = str2.charAt(index2);

			char[] ar1 = new char[len1];
			char[] ar2 = new char[len2];

			int loc1 = 0;
			int loc2 = 0;

			do {
				ar1[loc1++] = ch1;
				index1++;

				if (index1 < len1) {
					ch1 = str1.charAt(index1);
				} else {
					break;
				}
			} while (isDigit(ch1) == isDigit(ar1[0]));

			do {
				ar2[loc2++] = ch2;
				index2++;

				if (index2 < len2) {
					ch2 = str2.charAt(index2);
				} else {
					break;
				}
			} while (isDigit(ch2) == isDigit(ar2[0]));

			str1 = new String(ar1);
			str2 = new String(ar2);

			int result;

			if (isDigit(ar1[0]) && isDigit(ar2[0])) {
				result = Integer.parseInt(str1.trim()) - Integer.parseInt(str2.trim());
			} else {
				result = str1.compareTo(str2);
			}

			if (result != 0) {
				return result;
			}
		}
		return len1 - len2;
	}

	/**
	 * Test of the alphanumeric comparator.
	 */
	public static void main(String... args) {
		final String[] strings = { "1.2", "1.22", "1.22A", "1.3", "2.10", "2.1.1" };
		
		Arrays.sort(strings, new AlphanumericComparator());

		for (String str : strings) {
			System.out.println(str);
		}
	}
}
