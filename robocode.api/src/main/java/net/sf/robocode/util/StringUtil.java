/**
 * Copyright (c) 2001-2020 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.util;


/**
 * Class for utilizing strings.
 * 
 * @author Flemming N. Larsen (original)
 */
public final class StringUtil {

	/**
	 * Returns a string encoded into Basic Latin, where characters that cannot
	 * be represented in Basic Latin are represented as encoded unicode characters.
	 * 
	 * @param sequence
	 * @return
	 */
	public static String toBasicLatin(CharSequence sequence) {
		StringBuilder out = new StringBuilder();

		for (int i = 0; i < sequence.length(); i++) {
			char ch = sequence.charAt(i);

			if (Character.UnicodeBlock.of(ch) == Character.UnicodeBlock.BASIC_LATIN) {
				out.append(ch);
			} else {
				int codepoint = Character.codePointAt(sequence, i);

				// Handle supplementary range chars
				i += Character.charCount(codepoint) - 1;
				// Emit entity as a unicode character
				out.append(String.format("\\u%1$04X", codepoint));
			}
		}
		return out.toString();
	}

	/**
	 * Returns the number of occurrences of a specific character.
	 *
	 * @param str is the string containing the character to look for.
	 * @param chr is the character we look for.
	 * @return the number of occurrences.
	 */
	public static int countChar(String str, char chr) {
		int count = 0;
		for (char c : str.toCharArray()) {
			if (c == chr) {
				count++;
			}
		}
		return count;
	}
}
