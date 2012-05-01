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
	private JTextArea textPane;
	private int lines;
	private final Rectangle bottomRect = new Rectangle(0, 32767, 1, 1);

	public ConsoleScrollPane() {
		super();
		initialize();
	}

	public void append(String text) {
		lines++;
		getTextPane().append(text);
		if (lines > MAX_ROWS) {
			lines = 0;
			final String[] rows = getTextPane().getText().split("\n");
			StringBuilder sb = new StringBuilder();
			final int from = Math.min(rows.length, Math.max((MAX_ROWS / 2), rows.length - (MAX_ROWS / 2)));

			for (int i = from; i < rows.length; i++) {
				sb.append(rows[i]);
				sb.append('\n');
				lines++;
			}
			getTextPane().setText(sb.toString());
		}
	}

	public Dimension getAreaSize() {
		return getTextPane().getPreferredSize();
	}

	public String getSelectedText() {
		return getTextPane().getSelectedText();
	}

	public String getText() {
		return getTextPane().getText();
	}

	public JTextArea getTextPane() {
		if (textPane == null) {
			textPane = new JTextArea();
			textPane.setBackground(Color.lightGray);
			textPane.setBounds(0, 0, 1000, 1000);
			textPane.setEditable(false);
		}
		return textPane;
	}

	private void initialize() {
		lines = 0;
		setViewportView(getTextPane());
	}

	public void processStream(InputStream input) {
		BufferedReader br = new BufferedReader(new InputStreamReader(input));
		String line;

		try {
			while ((line = br.readLine()) != null) {
				int tabIndex = line.indexOf("\t");

				while (tabIndex >= 0) {
					line = line.substring(0, tabIndex) + "    " + line.substring(tabIndex + 1);
					tabIndex = line.indexOf("\t");
				}
				append(line + "\n");
			}
		} catch (IOException e) {
			append("IOException: " + e);
		}
		scrollToBottom();
	}

	private final Runnable scroller = new Runnable() {
		public void run() {
			getViewport().scrollRectToVisible(bottomRect);
			getViewport().repaint();
		}
	};

	public void scrollToBottom() {
		SwingUtilities.invokeLater(scroller);
	}

	public void setText(String text) {
		getTextPane().setText(text);
	}
}
