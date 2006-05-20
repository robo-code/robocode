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


/**
 * Insert the type's description here.
 * Creation date: (10/25/2001 10:42:52 PM)
 * @author: Administrator
 */
public class LimitedDocument extends javax.swing.text.PlainDocument {
	int maxRows = Integer.MAX_VALUE;
	int maxCols = Integer.MAX_VALUE;

	/**
	 * LimitedDocument constructor comment.
	 */
	public LimitedDocument() {
		super();
	}

	/**
	 * LimitedDocument constructor comment.
	 */
	public LimitedDocument(int maxRows, int maxCols) {
		super();
		this.maxRows = maxRows;
		this.maxCols = maxCols;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (10/25/2001 10:53:26 PM)
	 * @param offs int
	 * @param str java.lang.String
	 * @param a javax.swing.text.AttributeSet
	 */
	public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
	
		Element rootElement = getDefaultRootElement();
		int i = str.indexOf("\n");
		int newlines = 0;

		while (i < str.length() && i >= 0) {
			newlines++;
			i = str.indexOf("\n", i + 1);
		}
	
		int currentLines = rootElement.getElementCount();

		if (newlines > 0 && currentLines + newlines > maxRows) {
			java.awt.Toolkit.getDefaultToolkit().beep();
			return;
		}

		int lineIndex = rootElement.getElementIndex(offs);
		boolean done = false;

		int carry = rootElement.getElement(lineIndex).getEndOffset() - offs - 1;
		int lineStart = 0;

		while (!done) {
			int lineEnd = str.indexOf("\n", lineStart);

			if (lineEnd == -1 || lineEnd == str.length()) {
				if (lineStart == 0) {
					carry = 0;
					lineEnd = str.length();
				} else {
					lineEnd = str.length() + carry;
				}
				done = true;
			}
			int lineLen = lineEnd - lineStart;
			// Increment for last line...
			int currentLen;

			if (lineStart > 0 && done == false) {
				currentLen = 0;
			} else {
				if (lineStart > 0 && done == true) {
					lineIndex++;
				}
				Element currentLine = rootElement.getElement(lineIndex);

				if (currentLine != null) {
					currentLen = currentLine.getEndOffset() - currentLine.getStartOffset();
				} else {
					currentLen = 1;
				}
				if (lineStart == 0) {
					currentLen -= carry;
				}
			}
			// System.out.println("Test: linelen: " + lineLen + " currentLen: " + currentLen);
			if (lineLen + currentLen > maxCols + 1) {
				java.awt.Toolkit.getDefaultToolkit().beep();
				return;
			}
			
			lineStart = lineEnd + 1;
		}

		super.insertString(offs, str, a);
	
	}
}
