/*******************************************************************************
 * Copyright (c) 2001, 2006 Mathew Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew Nelson - initial API and implementation
 *******************************************************************************/
package robocode.dialog;


import javax.swing.*;
import robocode.util.*;
import java.io.*;


/**
 * Insert the type's description here.
 * Creation date: (4/23/2001 2:22:53 PM)
 * @author: Mathew A. Nelson
 */
public class ConsoleScrollPane extends javax.swing.JScrollPane {
	private javax.swing.JPanel consoleDialogContentPane = null;
	private javax.swing.JScrollPane scrollPane = null;
	private javax.swing.JTextArea textPane = null;
	public java.awt.Rectangle bottomRect = new java.awt.Rectangle(0, 32767, 1, 1);
	
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

	/**
	 * CompilerOutputDialog constructor comment.
	 */
	public ConsoleScrollPane() {
		super();
		initialize();
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (4/23/2001 2:29:23 PM)
	 * @param text java.lang.String
	 */
	public synchronized void append(String text) {
		getTextPane().append(text);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (4/23/2001 2:54:00 PM)
	 */
	public java.awt.Dimension getAreaSize() {
		return getTextPane().getPreferredSize();
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (4/23/2001 2:29:23 PM)
	 * @param text java.lang.String
	 */
	public String getSelectedText() {
		return getTextPane().getSelectedText();
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (4/23/2001 2:29:23 PM)
	 * @param text java.lang.String
	 */
	public String getText() {
		return getTextPane().getText();
	}

	/**
	 * Return the textPane
	 * @return javax.swing.JTextPane
	 */
	public javax.swing.JTextArea getTextPane() {
		if (textPane == null) {
			try {
				textPane = new javax.swing.JTextArea();
				textPane.setName("textPane");
				textPane.setBackground(java.awt.Color.lightGray);
				textPane.setBounds(0, 0, 1000, 1000);
				textPane.setEditable(false);
				// textPane.addKeyListener(eventHandler);
				// textPane.getEditorKit().getViewFactory().
			} catch (java.lang.Throwable e) {
				log(e);
			}
		}
		return textPane;
	}

	/**
	 * Return the scrollPane.
	 * @return javax.swing.JScrollPane
	 */
	private void initialize() {
		setName("scrollPane");
		setViewportView(getTextPane());
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/22/2001 1:41:21 PM)
	 * @param e java.lang.Exception
	 */
	public void log(String s) {
		Utils.log(s);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/22/2001 1:41:21 PM)
	 * @param e java.lang.Exception
	 */
	public void log(Throwable e) {
		Utils.log(e);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (11/7/2001 4:59:58 PM)
	 * @param in java.io.InputStream
	 */
	public void processStream(java.io.InputStream in) {
		new StreamReader(in).start();
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/24/2001 3:26:12 PM)
	 */
	private class Scroller implements Runnable {
		public void run() {
			getViewport().scrollRectToVisible(bottomRect);
			getViewport().repaint();
		}
	}
	private Scroller scroller = new Scroller();
	
	public void scrollToBottom() {
		SwingUtilities.invokeLater(scroller);
		// getViewport().scrollRectToVisible(bottomRect);
		// getViewport().repaint();
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (4/23/2001 2:29:23 PM)
	 * @param text java.lang.String
	 */
	public void setText(String text) {
		getTextPane().setText(text);
	}
}
