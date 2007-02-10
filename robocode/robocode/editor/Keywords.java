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
 *******************************************************************************/
package robocode.editor;


import javax.swing.text.Segment;


/**
 * @author Mathew A. Nelson (original)
 */
public class Keywords {
	// TODO: Must be updated for Java 5?
	public final static String keywords[] = {
		"abstract", "boolean", "break", "byte", "case", "catch", "char", "class", "const", "continue", "default", "do",
		"double", "else", "extends", "final", "finally", "float", "for", "goto", "if", "implements", "import",
		"instanceof", "int", "interface", "long", "native", "new", "package", "private", "protected", "public", "return",
		"short", "static", "strictfp", "super", "switch", "synchronized", "this", "throw", "throws", "transient", "try",
		"void", "volatile", "while", "true", "false"
	};

	/**
	 * Keywords constructor comment.
	 */
	public Keywords() {
		super();
	}

	public static boolean isKeyword(Segment seg) {
		boolean match = false;

		for (int i = 0; !match && i < keywords.length; i++) {
			if (seg.count == keywords[i].length()) {
				match = true;
				for (int j = 0; match && j < seg.count; j++) {
					if (seg.array[seg.offset + j] != keywords[i].charAt(j)) {
						match = false;
					}
				}
			}
		}
		return match;
	}
}
