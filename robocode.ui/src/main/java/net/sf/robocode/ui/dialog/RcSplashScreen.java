/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.ui.dialog;


import net.sf.robocode.ui.gfx.RobocodeLogo;
import net.sf.robocode.version.IVersionManager;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;


/**
 * The splash screen.
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
@SuppressWarnings("serial")
public class RcSplashScreen extends JWindow {

	private final static Color LABEL_COLOR = Color.WHITE;

	private JLabel splashLabel;
	private JPanel splashPanel;
	private JPanel splashScreenContentPane;
	private Image splashImage;
	private final String version;

	private final transient WindowListener eventHandler = new WindowAdapter() {

		@Override
		public void windowClosing(WindowEvent e) {
			if (e.getSource() == RcSplashScreen.this) {
				RcSplashScreen.this.dispose();
			}
		}
	};

	public RcSplashScreen(IVersionManager versionManager) {
		super();
		this.version = versionManager.getVersion();
		initialize();
	}

	public JLabel getSplashLabel() {
		if (splashLabel == null) {
			splashLabel = new JLabel();
			splashLabel.setText("");
			splashLabel.setForeground(LABEL_COLOR);
			splashLabel.setOpaque(false);
		}
		return splashLabel;
	}

	private JPanel getSplashPanel() {
		if (splashPanel == null) {
			splashPanel = new JPanel() {
				@Override
				public void paintComponent(Graphics g) {
					g.drawImage(splashImage, 0, 0, null);
					g.setColor(LABEL_COLOR);
					g.setFont(new Font("Arial", 1, 12));
					FontMetrics fm = g.getFontMetrics();

					g.drawString("Version: " + version,
							splashImage.getWidth(null) - fm.stringWidth("Version: " + version),
							splashImage.getHeight(null) - 4);
				}

				@Override
				public Dimension getPreferredSize() {
					return new Dimension(splashImage.getWidth(null), splashImage.getHeight(null));
				}
			};
			splashPanel.setLayout(new BorderLayout());
			splashPanel.add(getSplashLabel(), BorderLayout.NORTH);
		}
		return splashPanel;
	}

	private JPanel getSplashScreenContentPane() {
		if (splashScreenContentPane == null) {
			splashScreenContentPane = new JPanel();
			splashScreenContentPane.setBorder(new EtchedBorder());
			splashScreenContentPane.setLayout(new BorderLayout());
			splashScreenContentPane.add(getSplashPanel(), "Center");
		}
		return splashScreenContentPane;
	}

	private void initialize() {
		splashImage = new BufferedImage(RobocodeLogo.WIDTH, RobocodeLogo.HEIGHT, BufferedImage.TYPE_INT_RGB);
		new RobocodeLogo().paintLogoWithTanks(splashImage.getGraphics());

		setContentPane(getSplashScreenContentPane());
		addWindowListener(eventHandler);
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
	}
}
