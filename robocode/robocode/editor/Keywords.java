/*******************************************************************************
 * Copyright (c) 2001 Mathew Nelson
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew Nelson - initial API and implementation
 *******************************************************************************/
package robocode.editor;

import javax.swing.text.Segment;
/**
 * Insert the type's description here.
 * Creation date: (4/5/2001 1:49:24 PM)
 * @author: Mathew A. Nelson
 */
public class Keywords {
public final static String keywords[] = {
"abstract",
"boolean",
"break",
"byte",
"case",
"catch",
"char",
"class",
"const",
"continue",
"default",
"do",
"double",
"else",
"extends",
"final",
"finally",
"float",
"for",
"goto",
"if",
"implements",
"import",
"instanceof",
"int",
"interface",
"long",
"native",
"new",
"package",
"private",
"protected",
"public",
"return",
"short",
"static",
"strictfp",
"super",
"switch",
"synchronized",
"this",
"throw",
"throws",
"transient",
"try",
"void",
"volatile",
"while",
"true",
"false"
};
/**
 * Keywords constructor comment.
 */
public Keywords() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (4/5/2001 1:36:54 PM)
 * @return boolean
 * @param seg javax.swing.text.Segment
 */
public static boolean isKeyword(Segment seg) {
	boolean match = false;
	for (int i = 0; match == false && i < keywords.length; i++)
	{
		if (seg.count == keywords[i].length())
		{
			match = true;
			for (int j = 0; match == true && j < seg.count; j++)
			{
				if (seg.array[seg.offset+j] != keywords[i].charAt(j))
					match = false;
			}
		}
	}
	return match;
}
}
