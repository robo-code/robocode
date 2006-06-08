/*******************************************************************************
 * Copyright (c) 2001-2006 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Code cleanup
 *******************************************************************************/
package robocode.dialog;


import java.awt.*;
import javax.swing.*;
import java.io.*;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (current)
 */
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

	public synchronized void append(String text) {
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

	private class Scroller implements Runnable {
		public void run() {
			getViewport().scrollRectToVisible(bottomRect);
			getViewport().repaint();
		}
	}

	private Scroller scroller = new Scroller();

	public void scrollToBottom() {
		SwingUtilities.invokeLater(scroller);
	}

	public void setText(String text) {
		getTextPane().setText(text);
	}
}
