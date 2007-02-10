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
 *     - Changed to render the Robocode logo instead of using a bitmap image
 *     - Code cleanup
 *******************************************************************************/
package robocode.dialog;


import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.border.EtchedBorder;

import robocode.manager.RobocodeManager;
import robocode.render.RobocodeLogo;


/**
 * The splash screen.
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
@SuppressWarnings("serial")
public class SplashScreen extends JWindow {
	private final static Color LABEL_COLOR = Color.WHITE;

	private JLabel splashLabel;
	private JPanel splashPanel;
	private JPanel splashScreenContentPane;
	private Image splashImage;
	private boolean painted;
	private String version;

	private WindowListener eventHandler = new WindowAdapter() {

		@Override
		public void windowClosing(WindowEvent e) {
			if (e.getSource() == SplashScreen.this) {
				SplashScreen.this.dispose();
			}
		}
	};

	/**
	 * SplashScreen constructor
	 */
	public SplashScreen(RobocodeManager manager) {
		super();
		this.version = manager.getVersionManager().getVersion();
		initialize();
	}

	/**
	 * Returns the splash label
	 *
	 * @return the splash label
	 */
	public JLabel getSplashLabel() {
		if (splashLabel == null) {
			splashLabel = new JLabel();
			splashLabel.setText("");
			splashLabel.setForeground(LABEL_COLOR);
			splashLabel.setOpaque(false);
		}
		return splashLabel;
	}

	/**
	 * Return the splash panel
	 *
	 * @return the splash panel
	 */
	@SuppressWarnings("serial")
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

	/**
	 * Return the splash screen's content pane
	 *
	 * @return the splash screen's content pane
	 */
	private JPanel getSplashScreenContentPane() {
		if (splashScreenContentPane == null) {
			splashScreenContentPane = new JPanel();
			splashScreenContentPane.setBorder(new EtchedBorder());
			splashScreenContentPane.setLayout(new BorderLayout());
			splashScreenContentPane.add(getSplashPanel(), "Center");
		}
		return splashScreenContentPane;
	}

	/**
	 * Initialize the splash screen
	 */
	private void initialize() {
		splashImage = new BufferedImage(RobocodeLogo.WIDTH, RobocodeLogo.HEIGHT, BufferedImage.TYPE_INT_RGB);
		new RobocodeLogo().paintLogoWithTanks(splashImage.getGraphics());

		setContentPane(getSplashScreenContentPane());
		addWindowListener(eventHandler);
	}

	public boolean isPainted() {
		return painted;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		painted = true;
	}
}
