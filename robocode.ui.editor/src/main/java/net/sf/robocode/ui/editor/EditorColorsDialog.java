/*******************************************************************************
 * Copyright (c) 2001-2013 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 *******************************************************************************/
package net.sf.robocode.ui.editor;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.border.BevelBorder;

/**
 * Color theme dialog used for selecting the various colors of the editor.
 *
 * @author Flemming N. Larsen (original)
 */
@SuppressWarnings("serial")
public class EditorColorsDialog extends Dialog {

	public EditorColorsDialog(JFrame owner) {
		super(owner, true);
		initialize();
	}

	private void initialize() {
		setTitle("Editor Color Theme");

		setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.anchor = GridBagConstraints.WEST;

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		JComboBox themeComboBox = new JComboBox(new String[] { "Theme 1", "Theme 2", "Theme 3" });
		add(themeComboBox, gbc);
		
		gbc.gridwidth = 1;

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.NONE;
		JButton colorPanel1 = createColorPanel();
		colorPanel1.setBackground(Color.RED);
		add(colorPanel1, gbc);

		gbc.gridx = 1;
		add(new JLabel("Normal text"), gbc);		

		gbc.gridx = 0;
		gbc.gridy = 2;
		JButton colorPanel2 = createColorPanel();
		colorPanel2.setBackground(Color.BLUE);
		add(colorPanel2, gbc);

		gbc.gridx = 1;
		add(new JLabel("Quoted text"), gbc);		

		gbc.gridx = 0;
		gbc.gridy = 3;
		JButton colorPanel3 = createColorPanel();
		colorPanel3.setBackground(Color.GREEN);
		add(colorPanel3, gbc);

		gbc.gridx = 1;
		add(new JLabel("Java keywords"), gbc);		

		gbc.gridx = 0;
		gbc.gridy = 4;
		JButton colorPanel4 = createColorPanel();
		colorPanel4.setBackground(Color.YELLOW);
		add(colorPanel4, gbc);

		gbc.gridx = 1;
		add(new JLabel("Predefined keywords"), gbc);		

		gbc.gridx = 0;
		gbc.gridy = 5;
		JButton colorPanel5 = createColorPanel();
		colorPanel5.setBackground(Color.CYAN);
		add(colorPanel5, gbc);

		gbc.gridx = 1;
		add(new JLabel("Annotations"), gbc);		

		gbc.gridx = 0;
		gbc.gridy = 6;
		JButton colorPanel6 = createColorPanel();
		colorPanel6.setBackground(Color.MAGENTA);
		add(colorPanel6, gbc);

		gbc.gridx = 1;
		add(new JLabel("Comments"), gbc);		
	}

	private JButton createColorPanel() {
		JButton colorPanel = new JButton();
		colorPanel.setContentAreaFilled(false);
		colorPanel.setOpaque(true);
		colorPanel.setPreferredSize(new Dimension(24, 24));
		colorPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		return colorPanel;
	}
}
