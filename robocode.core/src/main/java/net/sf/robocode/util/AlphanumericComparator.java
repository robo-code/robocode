/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.util;


import java.util.Comparator;

import static java.lang.Character.isDigit;


/**
 * This is a comparator used for comparing two alphanumeric strings.
 * Normally strings are sorted one character at a time, meaning that e.g. "1.22" comes before "1.3".
 * However, with alphanumeric sorting, "1.3" comes before "1.22", as 3 is lesser than 22.
 * This comparator also others letter in this order "A", "a", "B", "b" instead of the normal character
 * order "A", "B", "a", "b".
 * 
 * <b>Example of usage:</b>
 * <pre>
 *    String[] strings = new String[] { "1.22", "1.3", "A", "B", "a", "b" };
 *
 *    Arrays.sort(strings, new AlphanumericComparator());
 *	
 *    for (String s : strings) {
 *        System.out.println(s);
 *    }
 * </pre>
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
	public int compare(final String str1, final String str2) {

		// Checks against null strings		
		if (str1 == null) {
			return (str2 == null) ? 0 : 1;
		}
		if (str2 == null) {
			return -1;
		}
		if (str1.equals(str2)) {
			return 0;
		}

		// Main loop where we keep read tokens from both input strings until the tokens differ 

		String tok1;
		int result = 0;

		for (int index = 0;; index += tok1.length()) {
			tok1 = readToken(str1, index);

			result = compareTokens(tok1, readToken(str2, index));
			if (result != 0) {
				return result;
			}
		}
	}

	/**
	 * Reads a token from the specified input string from a start index.
	 *
	 * @param input the input string.
	 * @param startIndex the start index of the token to read.
	 * @return the read token or null if there is no token could be read.
	 */
	private static String readToken(final String input, final int startIndex) {
		
		// Return null if the input string is null or the startIndex is out of bounds
		if (input == null || startIndex >= input.length()) {
			return null;
		}
		
		int index = startIndex + 1;

		// Check if the next token is numeric or not
		if (isDigit(input.charAt(startIndex))) {
			// Handle numeric token, which contains only digits in one continuous sequence
			for (; index < input.length() && isDigit(input.charAt(index)); index++) {
				;
			}
		} else {
			// Handle all other tokens that does not contain any digits
			for (; index < input.length() && !isDigit(input.charAt(index)); index++) {
				;
			}
		}

		// Return our token
		return input.substring(startIndex, index);
	}

	/**
	 * Compares two tokens.
	 * 
	 * @param tok1 the first token to be compared.
	 * @param tok2 the second token to be compared.
	 * @return a negative integer, zero, or a positive integer as the first token is less than, equal to, or greater
	 *         than the second.
	 */
	private static int compareTokens(final String tok1, final String tok2) {

		// Checks against null strings		
		if (tok1 == null) {
			return (tok2 == null) ? 0 : 1;
		}
		if (tok2 == null) {
			return -1;
		}

		// A numeric token has precedence over other tokens

		if (isDigit(tok1.charAt(0))) {
			if (isDigit(tok2.charAt(0))) {
				// Two numeric tokens are compared as two long values
				return (int) (Long.parseLong(tok1) - Long.parseLong(tok2));
			}
			return -1;
		} else if (isDigit(tok2.charAt(0))) {
			return 1;
		}

		// Compare two non-numeric tokens in alphabetic order.
		// That is, we want "A", "a", "B", "b" instead of "A", "B", "a", "b"

		int result = tok1.toUpperCase().compareTo(tok2.toUpperCase());

		if (result != 0) {
			return result;
		}
		return tok1.compareTo(tok2);
	}

	/* Example application where output must be:
	 "1.2"
	 "1.3"
	 "1.22A"
	 "1.22"
	 "1c"
	 "1h"
	 "1p"
	 "2.1.1"
	 "2.1a"
	 "2.10.2"
	 "2.10a.1"
	 "2.10"
	 " "
	 "ALPHA"
	 "ALpha"
	 "Alpha1"
	 "Alpha2"
	 "Alpha"
	 "alPHA"
	 "alpha1"
	 "alpha2"
	 "alpha"
	 "alpha"
	 "BETA"
	 "BEta"
	 "BeTa"
	 "bEtA"
	 "beTA"
	 "beta"
	 ""
	 <null>
	 */
	public static void main(String... args) {
		// Our unsorted strings that must be sorted
		final String[] strings = {
			null, "", " ", "alpha2", "alpha1", "bEtA", "BeTa", "alpha", "Alpha2", "Alpha1", "Alpha", "1.2", "1.22",
			"1.22A", "1.3", "2.10a.1", "2.1.1", "2.10", "2.1a", "2.10.2", "1c", "1h", "1p", "alpha", "beta", "alPHA",
			"beTA", "ALpha", "BEta", "ALPHA", "BETA"
		};

		java.util.Arrays.sort(strings, new AlphanumericComparator());
		
		for (String s : strings) {
			System.out.println(s == null ? "<null>" : '"' + s + '"');
		}
	}
}
