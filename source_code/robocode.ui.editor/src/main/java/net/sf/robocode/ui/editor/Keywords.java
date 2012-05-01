/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - This class was made final
 *     - The constructor was removed
 *     - The string constant 'keywords' was renamed into 'KEYWORDS'
 *******************************************************************************/
package net.sf.robocode.ui.editor;


import javax.swing.text.Segment;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public final class Keywords {
	// TODO: Must be updated for Java 5?
	private final static String KEYWORDS[] = {
		"abstract", "boolean", "break", "byte", "case", "catch", "char", "class", "const", "continue", "default", "do",
		"double", "else", "extends", "final", "finally", "float", "for", "goto", "if", "implements", "import",
		"instanceof", "int", "interface", "long", "native", "new", "package", "private", "protected", "public", "return",
		"short", "static", "strictfp", "super", "switch", "synchronized", "this", "throw", "throws", "transient", "try",
		"void", "volatile", "while", "true", "false"
	};

	public static boolean isKeyword(Segment seg) {
		boolean match = false;

		for (int i = 0; !match && i < KEYWORDS.length; i++) {
			if (seg.count == KEYWORDS[i].length()) {
				match = true;
				for (int j = 0; match && j < seg.count; j++) {
					if (seg.array[seg.offset + j] != KEYWORDS[i].charAt(j)) {
						match = false;
					}
				}
			}
		}
		return match;
	}
}
