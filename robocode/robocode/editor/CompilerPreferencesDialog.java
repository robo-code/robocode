/*******************************************************************************
 * Copyright (c) 2001-2006 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Matthew Reeder
 *     - Added keyboard mnemonics to buttons
 *     Flemming N. Larsen
 *     - Code cleanup
 *******************************************************************************/
package robocode.editor;


import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import robocode.util.Constants;
import robocode.util.Utils;


/**
 * @author Mathew A. Nelson (original)
 * @author Matthew Reeder, Flemming N. Larsen (current)
 */
public class CompilerPreferencesDialog extends JDialog {

	JButton cancelButton;
	JTextField compilerBinaryField;
	JTextField compilerClasspathField;
	JTextField compilerOptionsField;
	JPanel compilerPreferencesContentPane;
	CompilerProperties compilerProperties;
	JButton okButton;

	EventHandler eventHandler = new EventHandler();

	class EventHandler implements ActionListener {
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

			label.setAlignmentX(JLabel.LEFT_ALIGNMENT);
			compilerPreferencesContentPane.add(label);

			getCompilerBinaryField().setAlignmentX(JLabel.LEFT_ALIGNMENT);
			compilerPreferencesContentPane.add(getCompilerBinaryField());

			label = new JLabel(" ");
			label.setAlignmentX(JLabel.LEFT_ALIGNMENT);
			compilerPreferencesContentPane.add(label);
			label = new JLabel("Compiler Options:");
			label.setAlignmentX(JLabel.LEFT_ALIGNMENT);
			compilerPreferencesContentPane.add(label);
			getCompilerOptionsField().setAlignmentX(JTextField.LEFT_ALIGNMENT);
			compilerPreferencesContentPane.add(getCompilerOptionsField());
			label = new JLabel(" ");
			label.setAlignmentX(JLabel.LEFT_ALIGNMENT);
			compilerPreferencesContentPane.add(label);
			label = new JLabel("Compiler Classpath:");
			label.setAlignmentX(JLabel.LEFT_ALIGNMENT);
			compilerPreferencesContentPane.add(label);
			getCompilerClasspathField().setAlignmentX(JTextField.LEFT_ALIGNMENT);
			compilerPreferencesContentPane.add(getCompilerClasspathField());
			JPanel panel = new JPanel();

			panel.setAlignmentX(JLabel.LEFT_ALIGNMENT);
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
			Utils.log("Cannot save null compiler properties");
			return;
		}
		try {
			FileOutputStream out = new FileOutputStream(new File(Constants.cwd(), "compiler.properties"));

			compilerProperties.store(out, "Robocode Compiler Properties");
		} catch (IOException e) {
			Utils.log(e);
		}
	}
}
