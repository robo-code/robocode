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
 *     - Changed to render the Robocode logo instead of using a bitmap image
 *     - Replaced isPainted() and painted field with wait/notify
 *******************************************************************************/
package net.sf.robocode.ui.dialog;


import net.sf.robocode.io.FileUtil;
import net.sf.robocode.ui.gfx.RobocodeLogo;
import net.sf.robocode.ui.gfx.VirtualCombatLogo;
import net.sf.robocode.version.IVersionManager;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;


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

	private final WindowListener eventHandler = new WindowAdapter() {

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
		try {
			initialize();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

	private void initialize() throws FileNotFoundException, IOException {
		/***** Himanshu Singh ******/
		String logo_type = null;
		File battle_properties_file = FileUtil.getBattleConfigFile();
		Properties properties = new Properties();
		properties.load(new FileInputStream(battle_properties_file));
		
		logo_type = properties.getProperty("BATTLE_LOGO", "");
		if (logo_type.equalsIgnoreCase("ROBOCODE")) {
			splashImage = new BufferedImage(RobocodeLogo.WIDTH, RobocodeLogo.HEIGHT, BufferedImage.TYPE_INT_RGB);
			new RobocodeLogo().paintLogoWithTanks(splashImage.getGraphics());
		} else if (logo_type.equalsIgnoreCase("VIRTUALCOMBAT")){
			splashImage = new BufferedImage(VirtualCombatLogo.WIDTH, VirtualCombatLogo.HEIGHT, BufferedImage.TYPE_INT_RGB);
			new VirtualCombatLogo().insertVClogo(splashImage.getGraphics());
		} else {
			System.out.println("Configured logo not found. Please check robocode/config/battle.properties file");
		}
		/*************************/

		setContentPane(getSplashScreenContentPane());
		addWindowListener(eventHandler);
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
	}
}
