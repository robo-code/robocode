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
 *     Matthew Reeder
 *     - Added keyboard mnemonics to buttons
 *     Flemming N. Larsen
 *     - Code cleanup
 *     - Updated to use methods from FileUtil and Logger, which replaces methods
 *       that have been (re)moved from the Utils and Constants class
 *     - Changed to use FileUtil.getWindowConfigFile()
 *     - Added missing close() on FileOutputStream
 *******************************************************************************/
package robocode.editor;


import robocode.io.FileUtil;
import robocode.io.Logger;

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

	private EventHandler eventHandler = new EventHandler();

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

	private void initialize() {
		setTitle("Compiler Preferences");
		setContentPane(getCompilerPreferencesContentPane());
	}

	/**
	 * PackagerOptionsPanel constructor comment.
	 */
	public CompilerPreferencesDialog(JFrame owner) {
		super(owner);
		this.compilerProperties = RobocodeCompilerFactory.getCompilerProperties();
		initialize();
	}

	public JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton("Cancel");
			cancelButton.setMnemonic('C');
			cancelButton.setDisplayedMnemonicIndex(0);
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
			okButton.setDisplayedMnemonicIndex(0);
			okButton.addActionListener(eventHandler);
		}
		return okButton;
	}

	public void saveCompilerProperties() {
		if (compilerProperties == null) {
			Logger.log("Cannot save null compiler properties");
			return;
		}
		FileOutputStream out = null;

		try {
			out = new FileOutputStream(FileUtil.getCompilerConfigFile());

			compilerProperties.store(out, "Robocode Compiler Properties");
		} catch (IOException e) {
			Logger.log(e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {}
			}
		}
	}
}
