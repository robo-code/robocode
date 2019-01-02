/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.ui.dialog;


import net.sf.robocode.ui.BrowserManager;
import net.sf.robocode.version.IVersionManager;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;


/**
 * The About box.
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (original)
 */
@SuppressWarnings("serial")
public final class AboutBox extends JDialog {
	// Tag used for background color replacement
	private final static Color BG_COLOR = new Color(0xF0, 0xF0, 0xF0);
	// Tag used for Robocode version replacement
	private final static String TAG_ROBOCODE_VERSION = "\\Q{$robocode-version}\\E";
	// Tag used for Robocode icon source replacement
	private final static String TAG_ROBOCODE_ICON_SRC = "\\Q{$robocode-icon-url}\\E";
	// Tag used for background color replacement
	private final static String TAG_BG_COLOR = "\\Q{$background-color}\\E";
	// Tag used for Java version replacement
	private final static String TAG_JAVA_VERSION = "\\Q{$java-version}\\E";
	// Tag used for Java vendor replacement
	private final static String TAG_JAVA_VENDOR = "\\Q{$java-vendor}\\E";
	// Tag used for transparent.png 1x1 px url replacement
	private final static String TAG_TRANSPARENT = "\\Q{$transparent}\\E";

	// Robocode version
	private final String robocodeVersion;
	// Robocode icon URL
	private final java.net.URL iconURL;
	// Transparent URL
	private final java.net.URL transparentURL;
	
	// Content pane
	private JPanel aboutBoxContentPane;
	// Main panel
	private JEditorPane mainPanel;
	// Button panel
	private JPanel buttonPanel;
	// OK button
	private JButton okButton;
	// HTML text after tag replacements
	private String html;

	private static String getHtmlTemplate() {
		URL url = AboutBox.class.getResource("/net/sf/robocode/ui/html/about.html");

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		try {
			URLConnection connection = url.openConnection();
			int contentLength = connection.getContentLength();
			InputStream in = url.openStream();
			byte[] buf = new byte[contentLength];
			int len;

			while (true) {
				len = in.read(buf);
				if (len == -1) {
					break;
				}
				baos.write(buf, 0, len);
			}
			baos.close();
		} catch (IOException ignore) {}
		return baos.toString();
	}

	// General event handler
	private final transient ActionListener eventHandler = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == AboutBox.this.getOkButton()) {
				AboutBox.this.dispose();
			}
		}
	};

	// Hyperlink event handler
	private final transient HyperlinkListener hyperlinkHandler = new HyperlinkListener() {
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
		transparentURL = AboutBox.class.getResource("/net/sf/robocode/ui/html/transparent.png");

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
			String aaFontSettings = System.getProperty("awt.useSystemAAFontSettings");

			if (aaFontSettings != null) {
				mainPanel = new JEditorPane("text/html; charset=ISO-8859-1", getHtmlText());
				System.out.println(aaFontSettings);
			} else {
				mainPanel = new JEditorPane("text/html; charset=ISO-8859-1", getHtmlText()) {
					@Override
					public void paintComponent(Graphics g) {
						Graphics2D g2 = (Graphics2D) g;

						g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
						super.paintComponent(g);
					}
				};
			}
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
		if (html == null) {
			html = getHtmlTemplate();
			html = html.replaceAll(TAG_ROBOCODE_VERSION, robocodeVersion);
			html = html.replaceAll(TAG_ROBOCODE_ICON_SRC, iconURL.toString());
			html = html.replaceAll(TAG_BG_COLOR, toHtmlColor(BG_COLOR));
			html = html.replaceAll(TAG_JAVA_VERSION, getJavaVersion());
			html = html.replaceAll(TAG_JAVA_VENDOR, System.getProperty("java.vendor"));
			html = html.replaceAll(TAG_TRANSPARENT, transparentURL.toString());
		}
		return html;
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
	
	private static String getJavaVersion() {
		String javaVersion = System.getProperty("java.version");
		String javaArchModel = System.getProperty("sun.arch.data.model");

		if (javaArchModel != null) {
			try {
				int numBits = Integer.parseInt(javaArchModel);

				javaVersion += " (" + numBits + "-bit)";
			} catch (NumberFormatException ignore) {}
		}
		return javaVersion;
	}
}
