/*******************************************************************************
 * Copyright (c) 2001-2013 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 *******************************************************************************/
package net.sf.robocode.ui.editor;


import java.awt.Color;
import java.awt.Rectangle;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

import net.sf.robocode.ui.editor.theme.EditorThemePropertiesManager;
import net.sf.robocode.ui.editor.theme.IEditorThemeProperties;


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

		setColors(EditorThemePropertiesManager.getCurrentEditorThemeProperties());

		textComponent.getDocument().addDocumentListener(documentListener);

		EditorThemePropertiesManager.addListener(new IEditorPropertyChangeListener() {
			@Override
			public void onChange(IEditorThemeProperties properties) {
				setColors(properties);
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

		private String generateLinesText(int numLines) {
			StringBuilder lines = new StringBuilder();
			int i = 1;

			while (i <= numLines) {
				lines.append(i++).append('\n');
			}
			lines.append(i);
			return lines.toString();
		}
		
		private int getNumLines(Document doc) {
			return doc.getDefaultRootElement().getElementIndex(doc.getLength());
		}
	}

	private void setColors(IEditorThemeProperties properties) {
		Color textColor = properties.getLineNumberTextColor();
		setForeground(textColor);
		setSelectedTextColor(textColor);

		Color backgroundColor = properties.getLineNumberBackgroundColor();
		setBackground(backgroundColor);
		setSelectionColor(backgroundColor);
	}
}
