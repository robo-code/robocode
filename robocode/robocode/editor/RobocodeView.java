/*******************************************************************************
 * Copyright (c) 2001, 2006 Mathew Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew Nelson - initial API and implementation
 *******************************************************************************/
package robocode.editor;


import javax.swing.text.*;
import javax.swing.event.*;
import java.awt.*;


/**
 * Insert the type's description here.
 * Creation date: (4/4/2001 4:03:43 PM)
 * @author: Mathew A. Nelson
 */
public class RobocodeView extends javax.swing.text.PlainView {
	public final static Color commentColor = new Color(0, 150, 0);
	public final static Color stringColor = new Color(0, 150, 150);
	public final static Color keywordColor = new Color(0, 0, 150);
	public final static Color textColor = Color.black;
	public final static int TEXT = 0;
	public final static int KEYWORD = 1;
	public final static int COMMENT = 2;
	public final static int STRING = 3;
	public final static int MULTILINECOMMENT = 4;

	/**
	 * RobocodeView constructor
	 * @param elem javax.swing.text.Element
	 */
	public RobocodeView(javax.swing.text.Element elem) {
		super(elem);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (4/6/2001 2:18:50 PM)
	 * @param e javax.swing.event.DocumentEvent
	 * @param a java.awt.Shape
	 * @param f javax.swing.text.ViewFactory
	 */
	public void changedUpdate(DocumentEvent e, Shape a, ViewFactory f) {
		super.changedUpdate(e, a, f);
		JavaDocument d = (JavaDocument) e.getDocument();

		if (d.isNeedsRedraw()) {
			getContainer().repaint();
			d.setNeedsRedraw(false);
			// log("View received a repaint request. (change)");
		}
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (4/4/2001 4:51:07 PM)
	 * @return int
	 * @param g java.awt.Graphics
	 * @param x int
	 * @param y int
	 * @param p0 int
	 * @param p1 int
	 * @exception javax.swing.text.BadLocationException The exception description.
	 */
	protected int drawUnselectedText(java.awt.Graphics g, int x, int y, int p0, int p1) throws javax.swing.text.BadLocationException {
		Document doc = getDocument();
		Segment segment = new Segment();
		Segment token = getLineBuffer();

		doc.getText(p0, p1 - p0, segment);

		int count = p1 - p0;
		int left = 0;

		int state = TEXT;

		int elementIndex = doc.getDefaultRootElement().getElementIndex(p0);
		// System.err.print("redraw line " + elementIndex);
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
					;
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
					// i--;
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
		// log("Flushing from " + (p0+left) + " for length " + (p1-p0-left) + "in state " + state);
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

		// Multiline comment debugging:
		/* char inC[] = new char[1]; inC[0] = 'I';
		 char startC[] = new char[1]; startC[0] = 'S';
		 char endC[] = new char[1]; endC[0] = 'E';
		 Segment inSeg = new Segment(inC,0,1);
		 Segment startSeg = new Segment(startC,0,1);
		 Segment endSeg = new Segment(endC,0,1);
		 if (lineAttributes.isDefined("inComment"))
		 x=Utilities.drawTabbedText(inSeg,x,y,g,this,0);
		 if (lineAttributes.isDefined("startsComment"))
		 x=Utilities.drawTabbedText(startSeg,x,y,g,this,0);
		 if (lineAttributes.isDefined("endsComment"))
		 x=Utilities.drawTabbedText(endSeg,x,y,g,this,0);
		 */
	
		/* SegmentInputStream is = new SegmentInputStream(lineSegment);
		 Reader r = new BufferedReader(new InputStreamReader(is));
		 StreamTokenizer st = new StreamTokenizer(r);

		 int ttype;
		 try {
		 ttype = st.nextToken();
		 } catch (IOException e) {
		 log("Exception tokenizing: " + e);
		 ttype = StreamTokenizer.TT_EOF;
		 }
		 while (ttype != StreamTokenizer.TT_EOF)
		 {
		 log("Got token: " + st.sval);
		 try {
		 ttype = st.nextToken();
		 } catch (IOException e) {
		 log("Exception tokenizing: " + e);
		 ttype = StreamTokenizer.TT_EOF;
		 }
		 }
		 */
		// doc.getText(p0,p1-p0,text);
		// Utilities.drawTabbedText(text, x, y, g, this, p0);
		// log("done redrawing.");
	
		return x;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (4/4/2001 4:48:03 PM)
	 * @return int
	 */
	protected int getTabSize() {
		return 4;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (4/4/2001 4:21:03 PM)
	 * @param changes javax.swing.event.DocumentEvent
	 * @param a java.awt.Shape
	 * @param f javax.swing.text.ViewFactory
	 */
	public void insertUpdate(DocumentEvent e, Shape a, ViewFactory f) {
		super.insertUpdate(e, a, f);
		JavaDocument d = (JavaDocument) e.getDocument();

		if (d.isNeedsRedraw()) {
			getContainer().repaint();
			d.setNeedsRedraw(false);
			// log("View received a repaint request (insert).");
		}
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (4/4/2001 4:21:03 PM)
	 * @param e javax.swing.event.DocumentEvent
	 * @param a java.awt.Shape
	 * @param f javax.swing.text.ViewFactory
	 */
	public void removeUpdate(DocumentEvent e, Shape a, ViewFactory f) {
		super.removeUpdate(e, a, f);
		JavaDocument d = (JavaDocument) e.getDocument();

		if (d.isNeedsRedraw()) {
			getContainer().repaint();
			d.setNeedsRedraw(false);
			// log("View received a repaint request (remove).");
		}
	}
}
