/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.ui.editor;


import net.sf.robocode.io.FileUtil;
import net.sf.robocode.io.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * @author Mathew A. Nelson (original)
 * @author Matthew Reeder (contributor)
 * @author Flemming N. Larsen (contributor)
 */
@SuppressWarnings("serial")
public class CompilerPreferencesDialog extends JDialog {

	private JButton cancelButton;
	private JTextField compilerBinaryField;
	private JTextField compilerClasspathField;
	private JTextField compilerOptionsField;
	private JPanel compilerPreferencesContentPane;
	private CompilerProperties compilerProperties;
	private JButton okButton;

	private final EventHandler eventHandler = new EventHandler();

	private class EventHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource().equals(getOkButton())) {
				if (compilerProperties == null) {
					compilerProperties = new CompilerProperties();
				}
				compilerProperties.setCompilerBinary(getCompilerBinaryField().getText());
				compilerProperties.setCompilerOptions(getCompilerOptionsField().getText());
				compilerProperties.setCompilerClasspath(getCompilerClasspathField().getText());
				saveCompilerProperties();
				dispose();
			}
			if (e.getSource().equals(getCancelButton())) {
				dispose();
			}
		}
	}

	public CompilerPreferencesDialog(JFrame owner) {
		super(owner, true);
		this.compilerProperties = net.sf.robocode.core.Container.getComponent(RobocodeCompilerFactory.class).getCompilerProperties();
		initialize();
	}

	private void initialize() {
		setTitle("Compiler Preferences");
		setContentPane(getCompilerPreferencesContentPane());
	}

	public JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton("Cancel");
			cancelButton.setMnemonic('C');
			cancelButton.addActionListener(eventHandler);
		}
		return cancelButton;
	}

	public JTextField getCompilerBinaryField() {
		if (compilerBinaryField == null) {
			compilerBinaryField = new JTextField(40);
			compilerBinaryField.setText(compilerProperties.getCompilerBinary());
		}
		return compilerBinaryField;
	}

	public JTextField getCompilerClasspathField() {
		if (compilerClasspathField == null) {
			compilerClasspathField = new JTextField(40);
			compilerClasspathField.setText(compilerProperties.getCompilerClasspath());
		}
		return compilerClasspathField;
	}

	public JTextField getCompilerOptionsField() {
		if (compilerOptionsField == null) {
			compilerOptionsField = new JTextField(40);
			compilerOptionsField.setText(compilerProperties.getCompilerOptions());
		}
		return compilerOptionsField;
	}

	private JPanel getCompilerPreferencesContentPane() {
		if (compilerPreferencesContentPane == null) {
			compilerPreferencesContentPane = new JPanel();
			compilerPreferencesContentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			compilerPreferencesContentPane.setLayout(new BoxLayout(compilerPreferencesContentPane, BoxLayout.Y_AXIS));
			JLabel label = new JLabel("Compiler Binary:");

			label.setAlignmentX(Component.LEFT_ALIGNMENT);
			compilerPreferencesContentPane.add(label);

			getCompilerBinaryField().setAlignmentX(Component.LEFT_ALIGNMENT);
			compilerPreferencesContentPane.add(getCompilerBinaryField());

			label = new JLabel(" ");
			label.setAlignmentX(Component.LEFT_ALIGNMENT);
			compilerPreferencesContentPane.add(label);
			label = new JLabel("Compiler Options:");
			label.setAlignmentX(Component.LEFT_ALIGNMENT);
			compilerPreferencesContentPane.add(label);
			getCompilerOptionsField().setAlignmentX(Component.LEFT_ALIGNMENT);
			compilerPreferencesContentPane.add(getCompilerOptionsField());
			label = new JLabel(" ");
			label.setAlignmentX(Component.LEFT_ALIGNMENT);
			compilerPreferencesContentPane.add(label);
			label = new JLabel("Compiler Classpath:");
			label.setAlignmentX(Component.LEFT_ALIGNMENT);
			compilerPreferencesContentPane.add(label);
			getCompilerClasspathField().setAlignmentX(Component.LEFT_ALIGNMENT);
			compilerPreferencesContentPane.add(getCompilerClasspathField());
			JPanel panel = new JPanel();

			panel.setAlignmentX(Component.LEFT_ALIGNMENT);
			panel.add(getOkButton());
			panel.add(getCancelButton());
			compilerPreferencesContentPane.add(panel);
		}
		return compilerPreferencesContentPane;
	}

	public JButton getOkButton() {
		if (okButton == null) {
			okButton = new JButton("OK");
			okButton.setMnemonic('O');
			okButton.addActionListener(eventHandler);
		}
		return okButton;
	}

	public void saveCompilerProperties() {
		if (compilerProperties == null) {
			Logger.logError("Cannot save null compiler properties");
			return;
		}
		FileOutputStream out = null;

		try {
			out = new FileOutputStream(FileUtil.getCompilerConfigFile());

			compilerProperties.store(out, "Robocode Compiler Properties");
		} catch (IOException e) {
			Logger.logError(e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException ignored) {}
			}
		}
	}
}
