/******************************************************************************
 * Copyright (c) 2008 Marco Della Vedova, Matteo Foppiano
 * and Pimods contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.pixelinstrument.net/license/cpl-v10.html
 ******************************************************************************/

package net.sf.robocode.bv3d;


import java.awt.BorderLayout;
import java.awt.Color;

import javax.media.opengl.GLCanvas;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;


/**
 * 
 * @author Marco Della Vedova - pixelinstrument.net
 * @author Matteo Foppiano - pixelinstrument.net
 */

//
public class MainFrame extends JFrame {
	private static final long serialVersionUID = -325850950248170936L;

	private JLabel statusBar, pimodsLogo;
	private GLCanvas glCanvas;

	public MainFrame() {
		super();
		setIconImage(new ImageIcon("misc/icon.png").getImage());
		setTitle("PIMODS for Robocode");
		getContentPane().setLayout(new BorderLayout());
		setBounds(0, 0, 600, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// setIconImage( new ImageIcon("misc/logo_pimods.jpg") );

		statusBar = new JLabel();
		statusBar.setBorder(new EmptyBorder(1, 3, 1, 3));
		statusBar.setText("Status Bar");
		getContentPane().add(statusBar, BorderLayout.SOUTH);
		
		pimodsLogo = new JLabel();
		
		pimodsLogo.setIcon(new ImageIcon("misc/logo_pimods.jpg"));
		pimodsLogo.setForeground(Color.CYAN);
		pimodsLogo.setFocusable(false);
		pimodsLogo.setHorizontalAlignment(SwingConstants.CENTER);
		pimodsLogo.setOpaque(true);
		pimodsLogo.setBackground(Color.BLACK);
		// pimodsLogo.setText("Pimods 4 Robocode - Pixel Instrument Moving Objects in a Delimited Space");
		getContentPane().add(pimodsLogo, BorderLayout.CENTER);
	}
	
	public void setGlCanvas(GLCanvas glc) {
		this.glCanvas = glc;
		getContentPane().remove(pimodsLogo);
		getContentPane().add(glCanvas, BorderLayout.CENTER);
		this.setVisible(true);
	}
	
	public void displayMessageInStatusbar(String message) {
		statusBar.setText(message);
	}

}
