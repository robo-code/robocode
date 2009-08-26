/*******************************************************************************
 * Copyright (c) 2001, 2009 Mathew A. Nelson and Robocode contributors
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
package net.sf.robocode.ui.dialog;


import net.sf.robocode.ui.BrowserManager;
import net.sf.robocode.version.IVersionManager;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;


/**
 * The About box.
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
@SuppressWarnings("serial")
public final class AboutBox extends JDialog {
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
	private final static String HTML_TEMPLATE = "<head><style type=\"text/css\">p, td {font-family: sans-serif; font-size: 10px}</style></head>"
			+ "<body bgcolor=\"" + TAG_SYSCOLOR_CTRL_HIGHLIGHT
			+ "\"><table width=\"600 px\"><tr><td valign=\"top\"><img src=\"" + TAG_ROBOCODE_ICON_SRC
			+ "\"></td><td><table width=\"100%\"><tr><td><b>Robocode</b><br><br>"
			+ "&copy;&nbsp;Copyright 2001, 2009<br>Mathew A. Nelson and Robocode contributors</td><td align=\"right\"><b>Version: "
			+ TAG_ROBOCODE_VERSION
			+ "</b><br><br><a href=\"http://robocode.sourceforge.net\">Robocode Home</a>&nbsp;&nbsp;|&nbsp;&nbsp;<a href=\"http://robowiki.net\">RoboWiki</a><br>&nbsp;</td></tr></table><center>"
			+ "<b>Original Author</b><br>Designed and programmed by <b><font color=\"green\">Mathew A. Nelson</font></b><br>"
			+ "Graphics by <b><font color=\"green\">Garett S. Hourihan</font></b><br><br>"
			+ "<b>Featuring the <a href=\"http://robowiki.net/wiki/RoboRumble\">RoboRumble@Home</a> client</b><br>Originally designed and programmed by <b><font color=\"green\">Albert Pérez</font></b><br><br>"
			+ "<b>Main Contributors:</b><br><b><font color=\"green\">Flemming N. Larsen</font></b> (Robocode administrator, developer, integrator, lots of features),<br>"
			+ "<b><font color=\"green\">Pavel Savara</font></b> (Robocode administrator, developer, integrator, robot interfaces, battle events, refactorings),<br><br>"
			+ "<b>Other Contributors:</b><br>"
			+ "<b><font color=\"green\">Cubic Creative</font></b> (the design and ideas for the JuniorRobot class), "
			+ "<b><font color=\"green\">Christian D. Schnell</font></b> (for the Codesize utility), "
			+ "<b><font color=\"green\">Luis Crespo</font></b> (sound engine, single-step debugging, ranking panel), "
			+ "<b><font color=\"green\">Matthew Reeder</font></b> (editor enhancements, keyboard shortcuts, HyperThreading bugfixes), "
			+ "<b><font color=\"green\">Titus Chen</font></b> (bugfixes for robot teleportation, bad wall collision detection, team ranking, "
			+ "replay scores and robot color flickering), "
			+ "<b><font color=\"green\">Robert D. Maupin</font></b> (optimizations with collections and improved CPU constant benchmark), "
			+ "<b><font color=\"green\">Ascander Jr</font></b> (graphics for ground tiles), "
			+ "<b><font color=\"green\">Stefan Westen</font></b> (onPaint method from RobocodeSG), "
			+ "<b><font color=\"green\">Nathaniel Troutman</font></b> (fixing memory leaks due to circular references), "
			+ "<b><font color=\"green\">Aaron Rotenberg</font></b> (for the Robot Cache Cleaner utility), "
			+ "<b><font color=\"green\">Julian Kent</font></b> (nano precision timing of allowed robot time), "
			+ "<b><font color=\"green\">Joachim Hofer</font></b> (fixing problem with wrong results in RoboRumble), "
			+ "<b><font color=\"green\">Endre Palatinus, Eniko Nagy, Attila Csizofszki and Laszlo Vigh</font></b> (score % in results/rankings), "
			+ "<b><font color=\"green\">Jerome Lavigne</font></b> (added \"Smart Battles\" to MeleeRumble), "
			+ "<b><font color=\"green\">Ruben Moreno Montoliu</font></b> (added list paths with buttons to Developement Options), "
			+ "<b><font color=\"green\">Joshua Galecki</font></b> (the implementation of the RateControlRobot), "
			+ "<b><font color=\"green\">Patrick Cupka, Julian Kent, Nat Pavasant and \"Positive\"</font></b> (new robot movement method)<br><br>"
			+ "<b>Java Runtime Environment</b><br>Java " + TAG_JAVA_VERSION + " by " + TAG_JAVA_VENDOR
			+ "</center></td></tr></table></body>";
	
	// Robocode version
	private final String robocodeVersion;

	// Robocode icon URL
	private final java.net.URL iconURL;

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
	private final ActionListener eventHandler = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == AboutBox.this.getOkButton()) {
				AboutBox.this.dispose();
			}
		}
	};

	// Hyperlink event handler
	private final HyperlinkListener hyperlinkHandler = new HyperlinkListener() {
		public void hyperlinkUpdate(HyperlinkEvent event) {
			if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
				try {
					BrowserManager.openURL(event.getURL().toExternalForm());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	};

	public AboutBox(RobocodeFrame owner, IVersionManager versionManager) {
		super(owner, true);

		robocodeVersion = versionManager.getVersion();

		iconURL = AboutBox.class.getResource("/net/sf/robocode/ui/icons/robocode-icon.png");

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("About Robocode");
		setContentPane(getAboutBoxContentPane());
		setResizable(false);
	}

	private JPanel getAboutBoxContentPane() {
		if (aboutBoxContentPane == null) {
			aboutBoxContentPane = new JPanel();
			aboutBoxContentPane.setLayout(new BorderLayout());
			aboutBoxContentPane.add(getButtonPanel(), BorderLayout.SOUTH);
			aboutBoxContentPane.add(getMainPanel(), BorderLayout.CENTER);
		}
		return aboutBoxContentPane;
	}

	private JEditorPane getMainPanel() {
		if (mainPanel == null) {
			mainPanel = new JEditorPane("text/html; charset=ISO-8859-1", getHtmlText());
			mainPanel.setBackground(BG_COLOR);
			mainPanel.setEditable(false);
			mainPanel.addHyperlinkListener(hyperlinkHandler);
		}
		return mainPanel;
	}

	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new JPanel();
			buttonPanel.setBackground(BG_COLOR);
			buttonPanel.setLayout(new FlowLayout());
			buttonPanel.add(getOkButton());
		}
		return buttonPanel;
	}

	private JButton getOkButton() {
		if (okButton == null) {
			okButton = new JButton();
			okButton.setText("OK");
			okButton.addActionListener(eventHandler);
		}
		return okButton;
	}

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
