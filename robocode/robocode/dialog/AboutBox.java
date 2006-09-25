/*******************************************************************************
 * Copyright (c) 2001 Mathew A. Nelson
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Rewritten to use a JEditorPane with HTML content
 *******************************************************************************/
package robocode.dialog;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import robocode.manager.*;


/**
 * The AboutBox
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (current)
 */
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
			+ "\"></td><td><table width=\"100%\"><tr><td><b>Robocode</b><br><br>"
			+ "&copy;&nbsp;Copyright 2001-2005 Mathew A. Nelson<br>&copy;&nbsp;Copyright 2006 Robocode contributors</td>"
			+ "<td><b>Version: " + TAG_ROBOCODE_VERSION + "</b><br><br>robocode.sourceforge.net<br>&nbsp;</td></tr></table><center><br>"
			+ "Originally designed and programmed by Mathew A. Nelson<br><br>Graphics by Garett S. Hourihan"
			+ "<br><br><b>Contributors:</b><br><br>Flemming N. Larsen (main developer/administrator),<br>"
			+ "Matthew Reeder (Editor enhancements, keyboard shortcuts, HyperThreading bugfixes),<br>"
			+ "Luis Crespo (Sound engine, single-step debugging),<br>"
			+ "Ascander Jr (graphics for ground tiles),<br>"
			+ "and Stefan Westen (onPaint() method from RobocodeSG)<br>"
			+ "<br>You are using Java " + TAG_JAVA_VERSION + " by " + TAG_JAVA_VENDOR
			+ "</center></td></tr></table></body>";

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

	/**
	 * AboutBox constructor
	 */
	public AboutBox(Frame owner, RobocodeManager manager) {
		super(owner);

		robocodeVersion = manager.getVersionManager().getVersion();
		
		iconURL = getClass().getResource("/resources/icons/largeicon.jpg");	

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
			return (char) ((int) '0' + v);
		} else {
			return (char) ((int) 'A' + (v - 10));
		}
	}
}
