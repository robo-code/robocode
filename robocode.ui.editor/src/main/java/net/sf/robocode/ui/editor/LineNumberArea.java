/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.ui.editor;


import java.awt.Color;
import java.awt.Rectangle;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

import net.sf.robocode.ui.editor.theme.EditorThemeProperties;
import net.sf.robocode.ui.editor.theme.EditorThemePropertiesManager;
import net.sf.robocode.ui.editor.theme.EditorThemePropertyChangeAdapter;


/**
 * A text area containing line numbers for the editor pane and editor panel.
 *
 * @author Flemming N. Larsen (original)
 */
@SuppressWarnings("serial")
public class LineNumberArea extends JTextArea {

	private final DocumentListener documentListener = new TextDocumentListener();

	public LineNumberArea(JTextComponent textComponent) {
		super("1");

		setEditable(false);
		setLineWrap(false);

		EditorThemeProperties themeProps = EditorThemePropertiesManager.getCurrentEditorThemeProperties();
		setBackgroundColor(themeProps.getLineNumberBackgroundColor());
		setTextColor(themeProps.getLineNumberTextColor());
		
		textComponent.getDocument().addDocumentListener(documentListener);

		EditorThemePropertiesManager.addListener(new EditorThemePropertyChangeAdapter() {
			@Override
			public void onLineNumberBackgroundColorChanged(Color newColor) {
				setBackgroundColor(newColor);
			}

			@Override
			public void onLineNumberTextColorChanged(Color newColor) {
				setTextColor(newColor);
			}
		});
	}

	private class TextDocumentListener implements DocumentListener {
		int lastNumLines = 1;

		public void insertUpdate(DocumentEvent e) {
			updateText(e.getDocument());
		}

		public void removeUpdate(DocumentEvent e) {
			updateText(e.getDocument());
		}

		public void changedUpdate(DocumentEvent e) {
			updateText(e.getDocument());
		}		

		private void updateText(final Document doc) {
			
			final int numLines = getNumLines(doc);

			if (numLines == lastNumLines) {
				return;
			}
			lastNumLines = numLines;

			SwingUtilities.invokeLater(new Runnable() {			
				public void run() {
					final Rectangle visibleRect = getVisibleRect();
					
					setIgnoreRepaint(true); // avoid flickering
					setText(generateLinesText(numLines));

					// Must be done this way to keep aligned with scroll bar!
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							scrollRectToVisible(visibleRect);							
							setIgnoreRepaint(false); // avoid flickering
						}
					});
				}
			});
		}

		/**
		 * Returns a string containing text lines for all line numbers.
		 * Each line number is right-aligned and ended a space + a new-line character.
		 *
		 * @param numLines is the number of lines to create the line number text for.
		 * @return a string containing line numbers.
		 */
		private String generateLinesText(int numLines) {			
			String format = "%" + ("" + (numLines + 1)).length() + "s ";

			StringBuilder lines = new StringBuilder();
			int i = 1;
			while (i <= numLines) {
				String s = String.format(format, "" + i++);
				lines.append(s).append('\n');
			}
			lines.append(String.format(format, "" + i));
			return lines.toString();
		}
		
		private int getNumLines(Document doc) {
			return doc.getDefaultRootElement().getElementIndex(doc.getLength());
		}
	}

	private void setBackgroundColor(Color newBackgroundColor) {
		setBackground(newBackgroundColor);
		setSelectionColor(newBackgroundColor);		
	}

	private void setTextColor(Color newTextColor) {
		setForeground(newTextColor);
		setSelectedTextColor(newTextColor);		
	}
}
