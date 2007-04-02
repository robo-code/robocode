/*******************************************************************************
 * Copyright (c) 2001 Mathew A. Nelson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Rewritten to use a JEditorPane with HTML content
 *******************************************************************************/
package robocode.dialog;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import robocode.manager.RobocodeManager;


/**
 * The About box
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
@SuppressWarnings("serial")
public class AboutBox extends JDialog {
	private final static Color BG_COLOR = SystemColor.controlHighlight;

	// Tag used for Robocode version replacement
	private final static String TAG_ROBOCODE_VERSION = "<robocode:version>";

	// Tag used for Robocode icon source replacement
	private final static String TAG_ROBOCODE_ICON_SRC = "<robocode:icon-src>";

	// Tag used for Robocode icon source replacement
	private final static String TAG_SYSCOLOR_CTRL_HIGHLIGHT = "<syscolor:ctrl-highlight>";

	// Tag used for Java version replacement
	private final static String TAG_JAVA_VERSION = "<java:version>";

	// Tag used for Java vendor replacement
	private final static String TAG_JAVA_VENDOR = "<java:vendor>";

	// HTML template containing text for the AboutBox
	private final static String HTML_TEMPLATE = "<head><style type=\"text/css\">p, td {font-family: sans-serif;"
			+ "font-size: 10px}</style></head>" + "<body bgcolor=\"" + TAG_SYSCOLOR_CTRL_HIGHLIGHT
			+ "\"><table><tr><td valign=\"top\"><img src=\"" + TAG_ROBOCODE_ICON_SRC
			+ "\"></td><td><table width=\"100%\"><tr><td width=\"100%\"><b>Robocode</b><br><br>"
			+ "&copy;&nbsp;Copyright 2001, 2007<br>Mathew A. Nelson and Robocode contributors</td>" + "<td><b>Version: "
			+ TAG_ROBOCODE_VERSION
			+ "</b><br><br><a href=\"http://robocode.sourceforge.net\">robocode.sourceforge.net</a><br>&nbsp;</td></tr></table><center><br>"
			+ "Originally designed and programmed by Mathew A. Nelson<br><br>Graphics by Garett S. Hourihan<br><br>"
			+ "<b>Featuring RoboRumble@Home</b><br><br>Originally designed and programmed by Albert Pérez<br><br>"
			+ "<b>Contributors:</b><br><br>Flemming N. Larsen (main developer, integrator and admin),<br>"
			+ "Christian D. Schnell (for the codesize utility),<br>"
			+ "Luis Crespo (sound engine, single-step debugging, ranking panel),<br>"
			+ "Matthew Reeder (editor enhancements, keyboard shortcuts, HyperThreading bugfixes),<br>"
			+ "Titus Chen (bugfixes for robot teleportation, bad wall collision detection, team ranking,<br>"
			+ "replay scores and robot color flickering),<br>"
			+ "Robert D. Maupin (code optimizations regarding newer Java collections),<br>"
			+ "Ascander Jr (graphics for ground tiles),<br>"
			+ "and Stefan Westen (onPaint method from RobocodeSG)<br>"
			+ "<br>You are using Java " + TAG_JAVA_VERSION + " by " + TAG_JAVA_VENDOR + "</center></td></tr></table></body>";

	// Robocode version
	private String robocodeVersion;

	// Robocode icon URL
	private java.net.URL iconURL;

	// Content pane
	private JPanel aboutBoxContentPane;
	// Main panel
	private JEditorPane mainPanel;
	// Button panel
	private JPanel buttonPanel;
	// OK button
	private JButton okButton;
	// HTML text after tag replacements
	private String htmlText;

	// General event handler
	private ActionListener eventHandler = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == AboutBox.this.getOkButton()) {
				AboutBox.this.dispose();
			}
		}
	};

	// Hyperlink event handler
	private HyperlinkListener hyperlinkHandler = new HyperlinkListener() {
		public void hyperlinkUpdate(HyperlinkEvent event) {
			if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
				try {
					robocode.manager.BrowserManager.openURL(event.getURL().toExternalForm());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	};

	/**
	 * AboutBox constructor
	 */
	public AboutBox(Frame owner, RobocodeManager manager) {
		super(owner);

		robocodeVersion = manager.getVersionManager().getVersion();

		iconURL = getClass().getResource("/resources/icons/robocode-icon.png");

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("About Robocode");
		setContentPane(getAboutBoxContentPane());
		setResizable(false);
	}

	/**
	 * Returns the content pane
	 *
	 * @return the content pane
	 */
	private JPanel getAboutBoxContentPane() {
		if (aboutBoxContentPane == null) {
			aboutBoxContentPane = new JPanel();
			aboutBoxContentPane.setLayout(new BorderLayout());
			aboutBoxContentPane.add(getButtonPanel(), BorderLayout.SOUTH);
			aboutBoxContentPane.add(getMainPanel(), BorderLayout.CENTER);
		}
		return aboutBoxContentPane;
	}

	/**
	 * Returns the main panel
	 *
	 * @return the main panel
	 */
	private JEditorPane getMainPanel() {
		if (mainPanel == null) {
			mainPanel = new JEditorPane("text/html", getHtmlText());
			mainPanel.setBackground(BG_COLOR);
			mainPanel.setEditable(false);
			mainPanel.addHyperlinkListener(hyperlinkHandler);
		}
		return mainPanel;
	}

	/**
	 * Returns the button panel
	 *
	 * @return the button panel
	 */
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new JPanel();
			buttonPanel.setBackground(BG_COLOR);
			buttonPanel.setLayout(new FlowLayout());
			buttonPanel.add(getOkButton());
		}
		return buttonPanel;
	}

	/**
	 * Returns the OK button
	 *
	 * @return the OK button
	 */
	private JButton getOkButton() {
		if (okButton == null) {
			okButton = new JButton();
			okButton.setText("OK");
			okButton.addActionListener(eventHandler);
		}
		return okButton;
	}

	/**
	 * Returns the HTML text
	 *
	 * @return the HTML text
	 */
	private String getHtmlText() {
		if (htmlText == null) {
			htmlText = HTML_TEMPLATE.replaceAll(TAG_ROBOCODE_VERSION, robocodeVersion).replaceAll(TAG_ROBOCODE_ICON_SRC, iconURL.toString()).replaceAll(TAG_SYSCOLOR_CTRL_HIGHLIGHT, toHtmlColor(BG_COLOR)).replaceAll(TAG_JAVA_VERSION, System.getProperty("java.version")).replaceAll(
					TAG_JAVA_VENDOR, System.getProperty("java.vendor"));
		}
		return htmlText;
	}

	private static String toHtmlColor(Color color) {
		return "#" + toHexDigits(color.getRed()) + toHexDigits(color.getGreen()) + toHexDigits(color.getBlue());
	}

	private static String toHexDigits(int value) {
		return "" + toHexDigit(value >> 4) + toHexDigit(value & 0x0f);
	}

	private static char toHexDigit(int value) {
		int v = (value & 0xf);

		if (v < 10) {
			return (char) ('0' + v);
		}
		return (char) ('A' + (v - 10));
	}
}
