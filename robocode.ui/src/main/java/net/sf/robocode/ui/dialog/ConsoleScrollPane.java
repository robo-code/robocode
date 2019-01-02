/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
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
 * @author Pavel Savara (contributor)
 */
@SuppressWarnings("serial")
public class ConsoleScrollPane extends JScrollPane {

	private static final int MAX_ROWS = 500;
	private static final String TEXT_TRUNCATED_MSG = "^^^ TEXT TRUNCATED ^^^";

	private JTextArea textArea;
	private int lines;
	private int maxRows;

	public ConsoleScrollPane() {
		super();
		setViewportView(getTextPane());
	}

	public JTextArea getTextPane() {
		if (textArea == null) {
			textArea = new JTextArea();

			textArea.setEditable(false);
			textArea.setTabSize(4);
			textArea.setBackground(Color.DARK_GRAY);
			textArea.setForeground(Color.WHITE);
			textArea.setBounds(0, 0, 1000, 1000);
			textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

			// Make sure the caret is not reset every time text is updated, meaning that
			// the view will not reset it's position until we want it to.
			((DefaultCaret) textArea.getCaret()).setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
		}
		return textArea;
	}

	public void append(String str) {
		// Return if the string is null or empty
		if (str == null || str.length() == 0) {
			return;
		}
		// Append the new string to the text pane
		getTextPane().append(str);

		// Count number of new lines, i.e. lines ended with '\n'
		int numNewLines = str.replaceAll("[^\\n]", "").length();

		// Increment the current number of lines with the new lines
		lines += numNewLines;

		// Calculate number lines exceeded compared to the max. number of lines
		int linesExceeded = lines - MAX_ROWS;

		// Check if we exceeded the number of max. lines
		if (linesExceeded > 0) {
			// We use a rolling buffer so that the oldest lines are removed first.
			// Remove the number of lines we are exceeding in the beginning of the contained text.

			// Cut down the number of lines to max. number of lines
			lines = MAX_ROWS;

			// Find the index where to cut the number of exceeding lines in the beginning of the text
			String text = getText();
			int cutIndex = -1;

			for (int c = 0; c < linesExceeded; c++) {
				cutIndex = text.indexOf('\n', cutIndex + 1);
			}
			// Replace the first lines of the contained text till the cut index
			textArea.replaceRange(null, 0, cutIndex + 1);

			// Replace first line with a message that text has been truncated
			textArea.replaceRange(TEXT_TRUNCATED_MSG, 0, getText().indexOf('\n'));
		}

		// Set the max. number of lines text pane
		maxRows = Math.max(maxRows, lines);
		textArea.setRows(maxRows);
	}

	public String getSelectedText() {
		return getTextPane().getSelectedText();
	}

	public String getText() {
		return getTextPane().getText();
	}

	public void setText(String t) {
		// Return if the string is null or empty
		if (t == null || t.length() == 0) {
			t = null;
			lines = 0;
			maxRows = 0;
		} else {
			// Calculate and set the new number of lines for the text pane
			lines = t.replaceAll("[^\\n]", "").length();
	
			// Calculate number lines exceeded compared to the max. number of lines
			int linesExceeded = lines - MAX_ROWS;
	
			// Check if we exceeded the number of max. lines
			if (linesExceeded > 0) {
				// We use a rolling buffer so that the oldest lines are removed first.
				// Remove the number of lines we are exceeding in the beginning of the contained text.
	
				// Cut down the number of lines to max. number of lines
				lines = MAX_ROWS;
	
				// Find the index where to cut the number of exceeding lines in the beginning of the text
				int index = -1;
	
				for (int c = 0; c < linesExceeded; c++) {
					index = t.indexOf('\n', index + 1);
				}
				// Replace the first lines of the contained text till the cut index
				t = t.substring(index + 1);
	
				// Replace first line with a message that text has been truncated
				t = TEXT_TRUNCATED_MSG + t.substring(t.indexOf('\n') + 1);
			}
		}
		// Set the text on the text pane
		textArea.setText(t);

		// Set the max. number of lines text pane
		maxRows = Math.max(maxRows, lines);
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
		textArea.setCaretPosition(textArea.getDocument().getLength());
	}
}
