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
package robocode.util;


import javax.swing.text.*;


public class LimitedClassnameDocument extends LimitedDocument {

	/**
	 * LimitedDocument constructor comment.
	 */
	public LimitedClassnameDocument() {
		super();
	}

	/**
	 * LimitedDocument constructor comment.
	 */
	public LimitedClassnameDocument(int maxRows, int maxCols) {
		super(maxRows, maxCols);
	}

	public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
		if (offs == 0 && str.length() > 0) {
			if (!Character.isJavaIdentifierStart(str.charAt(0))) {
				java.awt.Toolkit.getDefaultToolkit().beep();
				return;
			}
		} else {
			for (int i = 0; i < str.length(); i++) {
				if (!Character.isJavaIdentifierPart(str.charAt(i))) {
					java.awt.Toolkit.getDefaultToolkit().beep();
					return;
				}
			}
		}
		if (offs == 0) {
			if (!Character.isUpperCase(str.charAt(0))) {
				str = str.substring(0, 1).toUpperCase() + str.substring(1);
			}
		}
	
		super.insertString(offs, str, a);
	}

}

