/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.ui.util;


import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;


/**
 * @author Mathew A. Nelson (original)
 */
@SuppressWarnings("serial")
public class LimitedClassnameDocument extends LimitedDocument {

	public LimitedClassnameDocument() {
		super();
	}

	public LimitedClassnameDocument(int maxRows, int maxCols) {
		super(maxRows, maxCols);
	}

	@Override
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
