/*******************************************************************************
 * Copyright (c) 2001-2012 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 *
 * Contributors:
 *     Flemming N. Larsen
 *     - Initial API and implementation
 *******************************************************************************/
package net.sf.robocode.ui.editor;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.Utilities;

import org.apache.log4j.Logger;


/**
 * Editor panel containing editor pane in a scroll pane, a line number area, and a statusTextField text field.
 *
 * @author Flemming N. Larsen (original)
 */
@SuppressWarnings("serial")
public class EditorPanel extends JPanel {

	private static final Logger logger = Logger.getLogger(EditorPanel.class);
	
	private JTextField statusTextField;
	private final EditorPane editorPane;
	private final LineNumberArea lineNumberArea;

	public EditorPanel() {
		super();

		setLayout(new BorderLayout());

		statusTextField = new JTextField();
		statusTextField.setEditable(false);

		JScrollPane scroll = new JScrollPane();

		editorPane = new EditorPane(scroll.getViewport());

		editorPane.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent e) {
				updateStatus(getRow(e.getDot(), editorPane), getColumn(e.getDot(), editorPane));
			}
		});

		scroll.setViewportView(editorPane);
		scroll.getViewport().setBackground(Color.WHITE);
		
		lineNumberArea = new LineNumberArea(editorPane);
		scroll.setRowHeaderView(lineNumberArea);

		add(scroll, BorderLayout.CENTER);
		add(statusTextField, BorderLayout.SOUTH);

		updateStatus(1, 1);
	}
	
	@Override
	public void requestFocus() {
		super.requestFocus();
		if (editorPane != null) {
			editorPane.requestFocus();
			editorPane.requestFocusInWindow();
		}
	}

	@Override
	public void setFont(Font font) {
		super.setFont(font);
		if (editorPane != null) {
			editorPane.setFont(font);
		}
		if (lineNumberArea != null) {
			lineNumberArea.setFont(font);
		}
	}

	public EditorPane getEditorPane() {
		return editorPane;
	}
	
	private void updateStatus(int linenumber, int columnnumber) {
		statusTextField.setText("Line: " + linenumber + " Column: " + columnnumber);
	}

	private static int getRow(int pos, JTextComponent editor) {
		int rn = (pos == 0) ? 1 : 0;

		try {
			int offs = pos;

			while (offs > 0) {
				offs = Utilities.getRowStart(editor, offs) - 1;
				rn++;
			}
		} catch (BadLocationException e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		return rn;
	}

	private static int getColumn(int pos, JTextComponent editor) {
		try {
			return pos - Utilities.getRowStart(editor, pos) + 1;
		} catch (BadLocationException e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		return -1;
	}
}
