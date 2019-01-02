/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.ui.editor;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;

import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;

import net.sf.robocode.ui.editor.theme.EditorThemePropertiesManager;
import net.sf.robocode.ui.editor.theme.EditorThemePropertyChangeAdapter;


/**
 * Class used for highlighting the current line for a JTextComponent.
 *
 * @author Santhosh Kumar T (original)
 * @author Peter De Bruycker (contributor)
 * @author Flemming N. Larsen (contributor)
 */
public final class LineHighlighter {
	private static final String LINE_HIGHLIGHT = "line-highlight";
	private static final String PREVIOUS_CARET = "previous-caret";

	private static Color color; // Color used for highlighting the line

	private LineHighlighter() {}

	/**
	 * Installs CurrentLineHilighter for the given JTextComponent.
	 * @param c is the text component
	 */
	public static void install(JTextComponent c) {
		try {
			Object obj = c.getHighlighter().addHighlight(0, 0, painter);
			c.putClientProperty(LINE_HIGHLIGHT, obj);
			c.putClientProperty(PREVIOUS_CARET, new Integer(c.getCaretPosition()));
			c.addCaretListener(caretListener);
			c.addMouseListener(mouseListener);
			c.addMouseMotionListener(mouseMotionListener);

			color = EditorThemePropertiesManager.getCurrentEditorThemeProperties().getHighlightedLineColor();
			EditorThemePropertiesManager.addListener(editorThemePropertyChangeListener);
		} catch (BadLocationException ex) {}
	}

	/**
	 * Uninstalls CurrentLineHighligher for the given JTextComponent.
	 * @param c is the text component
	 */
	public static void uninstall(JTextComponent c) {
		c.putClientProperty(LINE_HIGHLIGHT, null);
		c.putClientProperty(PREVIOUS_CARET, null);
		c.removeCaretListener(caretListener);
		c.removeMouseListener(mouseListener);
		c.removeMouseMotionListener(mouseMotionListener);
		EditorThemePropertiesManager.removeListener(editorThemePropertyChangeListener);
	}

	/**
	 * Fetches the previous caret location, stores the current caret location,
	 * If the caret is on another line, repaint the previous line and the current line
	 * @param c is the text component
	 */
	private static void caretUpdate(JTextComponent c) {
		try {
			int previousCaret = ((Integer) c.getClientProperty(PREVIOUS_CARET)).intValue();
			final int actualCaretPosition = c.getCaretPosition();
			c.putClientProperty(PREVIOUS_CARET, new Integer(actualCaretPosition));
			Rectangle prev = c.modelToView(previousCaret);
			Rectangle r = c.modelToView(actualCaretPosition);
			// c.putClientProperty(PREVIOUS_CARET, new Integer(actualCaretPosition));

			if (prev != null && prev.y != r.y) {
				c.repaint(0, prev.y, c.getWidth(), r.height);
				c.repaint(0, r.y, c.getWidth(), r.height);
			}
		} catch (BadLocationException ignore) {}
	}

	private static CaretListener caretListener = new CaretListener() {
		@Override
		public void caretUpdate(CaretEvent e) {
			JTextComponent c = (JTextComponent) e.getSource();
			LineHighlighter.caretUpdate(c);
		}
	};

	private static MouseListener mouseListener = new MouseAdapter() {
		// highlight the line the user clicks on
		@Override
		public void mousePressed(MouseEvent e) {
			JTextComponent c = (JTextComponent) e.getSource();
			caretUpdate(c);
		}
	};

	private static MouseMotionListener mouseMotionListener = new MouseMotionAdapter() {
		@Override
		public void mouseDragged(MouseEvent e) {
			JTextComponent c = (JTextComponent) e.getSource();
			caretUpdate(c);
		}
	};

	private static EditorThemePropertyChangeAdapter editorThemePropertyChangeListener = new EditorThemePropertyChangeAdapter() {
		public void onHighlightedLineColorChanged(Color newColor) {
			if (!color.equals(newColor)) {
				color = newColor;
			}
		}
	};

	private static Highlighter.HighlightPainter painter = new Highlighter.HighlightPainter() {
		@Override
		public void paint(Graphics g, int p0, int p1, Shape bounds, JTextComponent c) {
			try {
				Rectangle r = c.modelToView(c.getCaretPosition());
				g.setColor(color);
				g.fillRect(0, r.y, c.getWidth(), r.height);
			} catch (BadLocationException ignore) {}
		}
	};
}
