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
 *     Flemming N. Larsen
 *     - Code cleanup
 *     Pavel Savara
 *     - number of rows limited
 *******************************************************************************/
package net.sf.robocode.ui.dialog;


import javax.swing.*;
import javax.swing.text.DefaultCaret;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
@SuppressWarnings("serial")
public class ConsoleScrollPane extends JScrollPane {
	private final int MAX_ROWS = 500;
	private JTextArea textArea;
	private int lines;
	private int maxRows;

	public ConsoleScrollPane() {
		super();
		setViewportView(getTextPane());
		lines = 0;
	}

	public JTextArea getTextPane() {
		if (textArea == null) {
			textArea = new JTextArea();

			textArea.setEditable(false);
			textArea.setTabSize(4);
			textArea.setBackground(Color.DARK_GRAY);
			textArea.setForeground(Color.WHITE);
			textArea.setBounds(0, 0, 1000, 1000);

			// Make sure the caret is not reset every time text is updated, meaning that
			// the view will not reset it's position until we want it to.
			((DefaultCaret) textArea.getCaret()).setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
		}
		return textArea;
	}

	public void append(String text) {
		lines++;
		getTextPane().append(text);
		if (lines > MAX_ROWS) {
			lines = 0;
			final String[] rows = getText().split("\n");
			StringBuilder sb = new StringBuilder();
			final int from = Math.min(rows.length, Math.max((MAX_ROWS / 2), rows.length - (MAX_ROWS / 2)));

			for (int i = from; i < rows.length; i++) {
				sb.append(rows[i]);
				sb.append('\n');
				lines++;
			}
			setText(sb.toString());
		}
	}

	public String getSelectedText() {
		return getTextPane().getSelectedText();
	}

	public String getText() {
		return getTextPane().getText();
	}

	public void setText(String text) {
		final JTextArea textArea = getTextPane();

		textArea.setText(text);

		maxRows = Math.max(maxRows, textArea.getLineCount());
		textArea.setRows(maxRows);
	}

	public void processStream(InputStream input) {
		BufferedReader br = new BufferedReader(new InputStreamReader(input));
		String line;

		try {
			while ((line = br.readLine()) != null) {
				append(line + "\n");
			}
		} catch (IOException e) {
			append("SYSTEM: IOException: " + e);
		}
		scrollToBottom();
	}

	public void scrollToBottom() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				getViewport().scrollRectToVisible(new Rectangle(0, 32767, 1, 1));
				getViewport().repaint();
			}
		});
	}
}
