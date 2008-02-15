/*******************************************************************************
 * Copyright (c) 2001, 2007 Mathew A. Nelson and Robocode contributors
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
 *******************************************************************************/
package robocode.dialog;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
@SuppressWarnings("serial")
public class ConsoleScrollPane extends JScrollPane {

	private JTextArea textPane;
	private Rectangle bottomRect = new Rectangle(0, 32767, 1, 1);

	class StreamReader implements Runnable {
		private InputStream in;

		public StreamReader(InputStream in) {
			this.in = in;
		}

		public void start() {
			new Thread(this).start();
		}

		public void run() {
			BufferedReader in = new BufferedReader(new InputStreamReader(this.in));
			String line;

			try {
				line = in.readLine();
				while (line != null) {
					int tabIndex = line.indexOf("\t");

					while (tabIndex >= 0) {
						line = line.substring(0, tabIndex) + "    " + line.substring(tabIndex + 1);
						tabIndex = line.indexOf("\t");
					}
					append(line + "\n");
					scrollToBottom();
					line = in.readLine();
				}
			} catch (IOException e) {
				append("IOException: " + e);
			}
		}
	}

	public ConsoleScrollPane() {
		super();
		initialize();
	}

	public void append(String text) {
		getTextPane().append(text);
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

	/**
	 * Return the textPane
	 *
	 * @return JTextPane
	 */
	public JTextArea getTextPane() {
		if (textPane == null) {
			textPane = new JTextArea();
			textPane.setBackground(Color.lightGray);
			textPane.setBounds(0, 0, 1000, 1000);
			textPane.setEditable(false);
		}
		return textPane;
	}

	/**
	 * Return the scrollPane.
	 *
	 * @return JScrollPane
	 */
	private void initialize() {
		setViewportView(getTextPane());
	}

	public void processStream(InputStream in) {
		new StreamReader(in).start();
	}

	private Runnable scroller = new Runnable() {
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
