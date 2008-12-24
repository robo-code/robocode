/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Matthew Reeder
 *     - Initial API and implementation
 *******************************************************************************/
package net.sf.robocode.ui.editor;


import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import static java.lang.Math.max;
import static java.lang.Math.min;


/**
 * Custom widget for line numbers, meant to be the row header for a JScrollPane
 * that scrolls a JEditorPane.
 *
 * @author Matthew Reeder (original)
 */
@SuppressWarnings("serial")
public class LineNumbers extends JComponent implements DocumentListener, MouseListener, MouseMotionListener,
		CaretListener {

	private final JEditorPane editorPane;
	private int currentLines, lineWidth, anchor, lastIndex, offset, textWidth;

	public LineNumbers(JEditorPane editorPane) {
		this.editorPane = editorPane;
		editorPane.getDocument().addDocumentListener(this);
		setForeground(editorPane.getForeground());
		setBackground(editorPane.getBackground());
		currentLines = 1;
		lastIndex = -1;
		anchor = -1;
		addMouseListener(this);
		addMouseMotionListener(this);
		editorPane.addMouseListener(this);
		setPreferredSize(new Dimension(24, 17));
		editorPane.addCaretListener(this);
	}

	/**
	 * Listens for changes on the Document in its associated text pane.
	 * <p/>
	 * If the number of lines has changed, it updates its view.
	 */
	public void changedUpdate(DocumentEvent e) {
		anchor = lastIndex = -1;
		try {
			checkLines(e.getDocument().getText(0, e.getDocument().getLength()));
		} catch (BadLocationException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Listens for changes on the Document in its associated text pane.
	 * <p/>
	 * If the number of lines has changed, it updates its view.
	 */
	public void insertUpdate(DocumentEvent e) {
		anchor = lastIndex = -1;
		try {
			checkLines(e.getDocument().getText(0, e.getDocument().getLength()));
		} catch (BadLocationException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Listens for changes on the Document in its associated text pane.
	 * <p/>
	 * If the number of lines has changed, it updates its view.
	 */
	public void removeUpdate(DocumentEvent e) {
		anchor = lastIndex = -1;
		try {
			checkLines(e.getDocument().getText(0, e.getDocument().getLength()));
		} catch (BadLocationException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Called by the DocumentListener methods to check if the number of lines
	 * has changed, and if it has, it updates the display.
	 *
	 * @param text the text to compare to the current text
	 */
	protected void checkLines(String text) {
		int lines = 0;
		int index = -1;

		do {
			lines++;
			index = text.indexOf('\n', index + 1);
		} while (index >= 0);
		if (lines != currentLines) {
			currentLines = lines;
			repaint();
		}
	}

	/**
	 * Draws the line numbers.
	 */
	@Override
	public void paint(Graphics g) {
		checkLines(editorPane.getText());
		g.setFont(editorPane.getFont());
		FontMetrics fm = g.getFontMetrics();

		// note: using font metrics for the editor font, using a bold font :-)
		if (lineWidth == 0) {
			lineWidth = fm.getHeight();
			offset = fm.getAscent() - lineWidth;
		}
		g.setFont(editorPane.getFont().deriveFont(Font.BOLD));
		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(editorPane.getSelectionColor());
		int start = max(1, min(anchor, lastIndex));
		int end = min(currentLines, max(anchor, lastIndex));

		g.fillRect(0, (start - 1) * lineWidth - offset, textWidth, (end - start + 1) * lineWidth);
		int maxwidth = max(textWidth, fm.stringWidth("000"));

		for (int i = 1; i <= currentLines; i++) {
			String str = Integer.toString(i);

			maxwidth = max(maxwidth, fm.stringWidth(str));
			if (i >= start && i <= end) {
				g.setColor(editorPane.getSelectedTextColor());
			} else {
				g.setColor(getForeground());
			}
			g.drawString(str, textWidth - fm.stringWidth(str), i * lineWidth + offset);
		}
		textWidth = maxwidth;
		g.setColor(getForeground());
		g.drawLine(maxwidth + 2, 0, maxwidth + 2, getHeight());
		g.drawLine(maxwidth + 1, 0, maxwidth + 1, getHeight());
		Dimension dim = getPreferredSize();

		if (dim.height != lineWidth * currentLines || dim.width != maxwidth + 8) {
			setPreferredSize(new Dimension(maxwidth + 8, lineWidth * currentLines));
			setMinimumSize(new Dimension(maxwidth + 8, lineWidth * currentLines));
			repaint();
		}
	}

	/**
	 * Handles selection in the text pane due to gestures on the line numbers.
	 */
	protected void doSelection() {
		int first = min(anchor, lastIndex);
		int last = max(anchor, lastIndex);

		try {
			String text = editorPane.getDocument().getText(0, editorPane.getDocument().getLength());
			int index = -1, lines = 1;

			while (lines < first) {
				index = text.indexOf('\n', index + 1);
				lines++;
			}
			int firstindex = index + 1;

			do {
				index = text.indexOf('\n', index + 1);
				lines++;
			} while (lines <= last && index > 0);
			int lastindex;

			if (index < 0) {
				lastindex = editorPane.getDocument().getLength();
			} else {
				lastindex = index + 1;
			}
			editorPane.setSelectionStart(firstindex);
			editorPane.setSelectionEnd(lastindex);
		} catch (BadLocationException ignored) {}
	}

	/**
	 * Selects the number that was clicked on and sets it as an "anchor" for
	 * dragging.
	 */
	public void mousePressed(MouseEvent e) {
		if (e.getSource() == this) {
			if (e.getX() < textWidth) {
				anchor = e.getY() / lineWidth + 1;
				lastIndex = anchor;
				doSelection();
				repaint();
				editorPane.requestFocus();
			}
		} else {
			anchor = lastIndex = -1;
			repaint();
		}
	}

	/**
	 * Sets the end anchor and updates the state of the widget to reflect that
	 * the click-and-drag gesture has ended.
	 */
	public void mouseReleased(MouseEvent e) {
		if (e.getSource() == this) {
			if (e.getX() < textWidth) {
				if (lastIndex != e.getY() / lineWidth + 1) {
					lastIndex = e.getY() / lineWidth + 1;
					doSelection();
					repaint();
				}
				editorPane.requestFocus();
			}
		} else {
			anchor = lastIndex = -1;
			repaint();
		}
	}

	/**
	 * Temporarily moves the end anchor of the current selection.
	 */
	public void mouseDragged(MouseEvent e) {
		if (lastIndex != e.getY() / lineWidth + 1) {
			if (e.getX() < textWidth) {
				lastIndex = e.getY() / lineWidth + 1;
				doSelection();
				repaint();
			}
		}
		editorPane.requestFocus();
	}

	/**
	 * Empty - part of the MouseMotionListener interface.
	 */
	public void mouseMoved(MouseEvent e) {}

	/**
	 * Empty - part of the MouseListener interface.
	 */
	public void mouseEntered(MouseEvent e) {}

	/**
	 * Empty - part of the MouseListener interface.
	 */
	public void mouseExited(MouseEvent e) {}

	/**
	 * Empty - part of the MouseListener interface.
	 */
	public void mouseClicked(MouseEvent e) {}

	/**
	 * Listens for changes in caret position on the text pane.
	 * <p/>
	 * Updates the code block display and stuff
	 */
	public void caretUpdate(CaretEvent e) {
		repaint();
	}
}
