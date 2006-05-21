/*******************************************************************************
 * Copyright (c) 2001, 2006 Mathew Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew Nelson - initial API and implementation
 *******************************************************************************/
package robocode.editor;


import javax.swing.*;
import java.awt.event.*;
import java.io.*;

import robocode.util.*;


/**
 * Insert the type's description here.
 * Creation date: (10/19/2001 12:07:51 PM)
 * @author: Administrator
 */
public class CompilerPreferencesDialog extends JDialog {

	JButton cancelButton = null;
	JTextField compilerBinaryField = null;
	JTextField compilerClasspathField = null;
	JTextField compilerOptionsField = null;
	JPanel compilerPreferencesContentPane = null;
	CompilerProperties compilerProperties = null;
	JButton okButton = null;

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

	/**
	 * Insert the method's description here.
	 * Creation date: (10/19/2001 12:09:49 PM)
	 */
	private void initialize() {

		setName("packagerOptionsPanel");
		// setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/icons/icon.jpg")));
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

	/**
	 * Insert the method's description here.
	 * Creation date: (11/7/2001 3:03:28 PM)
	 * @return javax.swing.JButton
	 */
	public javax.swing.JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton("Cancel");
			cancelButton.addActionListener(eventHandler);
		}
		return cancelButton;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (11/1/2001 5:05:39 PM)
	 * @return javax.swing.JTextField
	 */
	public javax.swing.JTextField getCompilerBinaryField() {
		if (compilerBinaryField == null) {
			compilerBinaryField = new JTextField(40);
			compilerBinaryField.setText(compilerProperties.getCompilerBinary());
		}
		return compilerBinaryField;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (11/1/2001 5:05:39 PM)
	 * @return javax.swing.JTextField
	 */
	public javax.swing.JTextField getCompilerClasspathField() {
		if (compilerClasspathField == null) {
			compilerClasspathField = new JTextField(40);
			compilerClasspathField.setText(compilerProperties.getCompilerClasspath());
		}
		return compilerClasspathField;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (11/1/2001 5:05:39 PM)
	 * @return javax.swing.JTextField
	 */
	public javax.swing.JTextField getCompilerOptionsField() {
		if (compilerOptionsField == null) {
			compilerOptionsField = new JTextField(40);
			compilerOptionsField.setText(compilerProperties.getCompilerOptions());
		}
		return compilerOptionsField;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (10/19/2001 12:09:49 PM)
	 */
	private JPanel getCompilerPreferencesContentPane() {

		if (compilerPreferencesContentPane == null) {
			compilerPreferencesContentPane = new JPanel();
			setName("packagerOptionsPanel");

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
			// getCompilerOptionsField().setMaximumSize(getCompilerOptionsField().getPreferredSize());
			compilerPreferencesContentPane.add(getCompilerOptionsField());

			label = new JLabel(" ");
			label.setAlignmentX(JLabel.LEFT_ALIGNMENT);
			compilerPreferencesContentPane.add(label);

			label = new JLabel("Compiler Classpath:");
			label.setAlignmentX(JLabel.LEFT_ALIGNMENT);
			compilerPreferencesContentPane.add(label);

			getCompilerClasspathField().setAlignmentX(JTextField.LEFT_ALIGNMENT);
			// getCompilerOptionsField().setMaximumSize(getCompilerOptionsField().getPreferredSize());
			compilerPreferencesContentPane.add(getCompilerClasspathField());

			JPanel panel = new JPanel();

			panel.setAlignmentX(JLabel.LEFT_ALIGNMENT);
			panel.add(getOkButton());
			panel.add(getCancelButton());
			compilerPreferencesContentPane.add(panel);
		}
		return compilerPreferencesContentPane;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (11/7/2001 3:03:32 PM)
	 * @return javax.swing.JButton
	 */
	public javax.swing.JButton getOkButton() {
		if (okButton == null) {
			okButton = new JButton("OK");
			okButton.addActionListener(eventHandler);
		}
		return okButton;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/22/2001 1:41:21 PM)
	 * @param e java.lang.Exception
	 */
	public static void log(String s) {
		Utils.log(s);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/22/2001 1:41:21 PM)
	 * @param e java.lang.Exception
	 */
	public static void log(Throwable e) {
		Utils.log(e);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/18/2001 3:21:22 PM)
	 */
	public void saveCompilerProperties() {
		if (compilerProperties == null) {
			log("Cannot save null compiler properties");
			return;
		}
		try {
			FileOutputStream out = new FileOutputStream(new File(Constants.cwd(), "compiler.properties"));

			compilerProperties.store(out, "Robocode Compiler Properties");
		} catch (IOException e) {
			log(e);
		}
	
	}
}
