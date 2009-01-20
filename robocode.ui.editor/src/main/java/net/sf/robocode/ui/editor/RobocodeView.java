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
 *******************************************************************************/
package net.sf.robocode.ui.editor;


import javax.swing.event.DocumentEvent;
import javax.swing.text.*;
import java.awt.*;


/**
 * @author Mathew A. Nelson (original)
 */
public class RobocodeView extends PlainView {
	public final static Color commentColor = new Color(0, 150, 0);
	public final static Color stringColor = new Color(0, 150, 150);
	public final static Color keywordColor = new Color(0, 0, 150);
	public final static Color textColor = Color.black;
	public final static int TEXT = 0;
	public final static int KEYWORD = 1;
	public final static int COMMENT = 2;
	public final static int STRING = 3;
	public final static int MULTILINECOMMENT = 4;

	public RobocodeView(Element elem) {
		super(elem);
	}

	@Override
	public void changedUpdate(DocumentEvent e, Shape a, ViewFactory f) {
		super.changedUpdate(e, a, f);
		JavaDocument d = (JavaDocument) e.getDocument();

		if (d.isNeedsRedraw()) {
			getContainer().repaint();
			d.setNeedsRedraw(false);
		}
	}

	@Override
	protected int drawUnselectedText(java.awt.Graphics g, int x, int y, int p0, int p1) throws BadLocationException {
		Document doc = getDocument();
		Segment segment = new Segment();
		Segment token = getLineBuffer();

		doc.getText(p0, p1 - p0, segment);

		int count = p1 - p0;
		int left = 0;

		int state = TEXT;

		int elementIndex = doc.getDefaultRootElement().getElementIndex(p0);

		AttributeSet lineAttributes = doc.getDefaultRootElement().getElement(elementIndex).getAttributes();

		if (lineAttributes.isDefined("inComment")) {
			state = MULTILINECOMMENT;
		}

		for (int i = 0; i < count; i++) {
			// Starting in default text state.
			if (state == TEXT) {
				if (Character.isLetter(segment.array[i + segment.offset])
						&& Character.isLowerCase(segment.array[i + segment.offset])) {
					// flush now
					g.setColor(textColor);
					doc.getText(p0 + left, i - left, token);
					x = Utilities.drawTabbedText(token, x, y, g, this, p0 + left);
					left = i;
					state = KEYWORD;
				} // Do nothing
				else {
					if (segment.array[i + segment.offset] == '/') {
						// flush now
						g.setColor(textColor);
						doc.getText(p0 + left, i - left, token);
						x = Utilities.drawTabbedText(token, x, y, g, this, p0 + left);
						left = i;
						state = COMMENT;
					} else if (segment.array[i + segment.offset] == '"') {
						// flush now
						g.setColor(textColor);
						doc.getText(p0 + left, i - left, token);
						x = Utilities.drawTabbedText(token, x, y, g, this, p0 + left);
						left = i;
						state = STRING;
					}
				}
			} else if (state == KEYWORD) {
				// Still
				if (Character.isLetter(segment.array[i + segment.offset])) {// && Character.isLowerCase(segment.array[i+segment.offset]))
				} else {
					// flush now
					doc.getText(p0 + left, i - left, token);
					if (Keywords.isKeyword(token)) {
						g.setColor(keywordColor);
					} else {
						g.setColor(textColor);
					}
					x = Utilities.drawTabbedText(token, x, y, g, this, p0 + left);
					left = i;
					state = TEXT;
					if (segment.array[i + segment.offset] == '/') {
						state = COMMENT;
					} else if (segment.array[i + segment.offset] == '"') {
						state = STRING;
					}
				}
			} else if (state == COMMENT) {
				if (segment.array[i + segment.offset] == '/') {
					break;
				} else if (segment.array[i + segment.offset] == '*') {
					state = MULTILINECOMMENT;
				} else {
					state = TEXT;
				}
			} else if (state == MULTILINECOMMENT) {
				if (i > 0 && segment.array[i + segment.offset] == '/' && segment.array[i + segment.offset - 1] == '*') {
					// flush now
					doc.getText(p0 + left, i + 1 - left, token);
					g.setColor(commentColor);
					x = Utilities.drawTabbedText(token, x, y, g, this, p0 + left);
					left = i + 1;
					state = TEXT;
				}
			} else if (state == STRING) {
				if (segment.array[i + segment.offset] == '"') {
					// flush now
					doc.getText(p0 + left, i + 1 - left, token);
					g.setColor(stringColor);
					x = Utilities.drawTabbedText(token, x, y, g, this, p0 + left);
					left = i + 1;
					state = TEXT;
				}
			}
			// Starting not in token
		} // end loop
		// Flush last
		doc.getText(p0 + left, p1 - p0 - left, token);
		if (state == KEYWORD) {
			if (Keywords.isKeyword(token)) {
				g.setColor(keywordColor);
			} else {
				g.setColor(textColor);
			}
		} else if (state == STRING) {
			g.setColor(stringColor);
		} else if (state == COMMENT && ((p1 - p0 - left) > 1)) {
			g.setColor(commentColor);
		} else if (state == MULTILINECOMMENT) {
			g.setColor(commentColor);
		} else {
			g.setColor(textColor);
		}
		x = Utilities.drawTabbedText(token, x, y, g, this, p0 + left);

		return x;
	}

	@Override
	protected int getTabSize() {
		return 4;
	}

	@Override
	public void insertUpdate(DocumentEvent e, Shape a, ViewFactory f) {
		super.insertUpdate(e, a, f);
		JavaDocument d = (JavaDocument) e.getDocument();

		if (d.isNeedsRedraw()) {
			getContainer().repaint();
			d.setNeedsRedraw(false);
		}
	}

	@Override
	public void removeUpdate(DocumentEvent e, Shape a, ViewFactory f) {
		super.removeUpdate(e, a, f);
		JavaDocument d = (JavaDocument) e.getDocument();

		if (d.isNeedsRedraw()) {
			getContainer().repaint();
			d.setNeedsRedraw(false);
		}
	}
}
