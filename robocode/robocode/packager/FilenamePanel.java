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
 *     - Code cleanup
 *     - Replaced FileSpecificationVector with plain Vector
 *     - Replaced deprecated show() method with setVisible(true)
 *     - Updated to use methods from the FileUtil, which replaces file operations
 *       that have been (re)moved from the robocode.util.Utils class
 *     - Changed to use FileUtil.getRobotsDir()
 *     - Fixed the layout, where the filename field was too high
 *     Robert D. Maupin
 *     - Replaced old collection types like Vector and Hashtable with
 *       synchronized List and HashMap
 *******************************************************************************/
package robocode.packager;


import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.util.List;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Caret;

import robocode.dialog.WizardPanel;
import robocode.io.FileUtil;
import robocode.repository.FileSpecification;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Robert D. Maupin (contributor)
 */
@SuppressWarnings("serial")
public class FilenamePanel extends WizardPanel {
	private RobotPackager robotPackager;

	private EventHandler eventHandler = new EventHandler();
	private boolean robocodeErrorShown;

	private JButton browseButton;
	private JTextField filenameField;

	private class EventHandler implements ActionListener, DocumentListener, ComponentListener {
		public void insertUpdate(DocumentEvent e) {
			fireStateChanged();
		}

		public void changedUpdate(DocumentEvent e) {
			fireStateChanged();
		}

		public void removeUpdate(DocumentEvent e) {
			fireStateChanged();
		}

		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == getBrowseButton()) {
				showFileSelectDialog();
			}
		}

		public void componentMoved(ComponentEvent e) {}

		public void componentResized(ComponentEvent e) {}

		public void componentHidden(ComponentEvent e) {}

		public void componentShown(ComponentEvent e) {
			String fileName = FileUtil.getRobotsDir().getAbsolutePath() + File.separator;
			File outgoingFile = new File(fileName);

			if (!outgoingFile.exists()) {
				outgoingFile.mkdirs();
			}
			String jarName = "myrobots.jar";
			List<FileSpecification> selectedRobots = robotPackager.getRobotSelectionPanel().getSelectedRobots();

			if (selectedRobots != null && selectedRobots.size() == 1) {
				jarName = selectedRobots.get(0).getFullClassName() + "_"
						+ robotPackager.getPackagerOptionsPanel().getVersionField().getText() + ".jar";
			}

			getFilenameField().setText(fileName + jarName);
			Caret caret = getFilenameField().getCaret();

			caret.setDot(fileName.length());
			caret.moveDot(fileName.length() + jarName.length() - 4);

			getFilenameField().requestFocus();
		}
	}

	public FilenamePanel(RobotPackager robotPackager) {
		super();
		this.robotPackager = robotPackager;
		initialize();
	}

	private void initialize() {
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		addComponentListener(eventHandler);

		GridBagLayout layout = new GridBagLayout();

		setLayout(layout);
		GridBagConstraints c = new GridBagConstraints();

		c.insets = new Insets(5, 5, 5, 5);
		c.anchor = GridBagConstraints.NORTHWEST;

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		c.weightx = 1;

		add(new JLabel("Please type in a .jar file to save this robot package to: "), c);

		c.gridy = 1;
		add(getFilenameField(), c);

		c.fill = GridBagConstraints.NONE;
		c.gridy = 2;
		c.insets = new Insets(3, 3, 3, 3);
		add(getBrowseButton(), c);

		c.fill = GridBagConstraints.VERTICAL;
		c.weighty = 1;
		c.gridy = 3;
		add(new JPanel(), c);
	}

	@Override
	public boolean isReady() {
		if (filenameField.getText() == null) {
			return false;
		}
		int robocodeIndex = filenameField.getText().lastIndexOf(File.separatorChar);

		if (robocodeIndex > 0) {
			if (filenameField.getText().substring(robocodeIndex + 1).indexOf("robocode") == 0) {
				if (!robocodeErrorShown) {
					robocodeErrorShown = true;
					new Thread(new Runnable() {
						public void run() {
							JOptionPane.showMessageDialog(FilenamePanel.this, "Filename cannot begin with robocode");
						}
					}).start();
				}
				return false;
			}
		}
		if (filenameField.getText().toLowerCase().indexOf(".jar") > 0) {
			return true;
		}
		return false;
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("options");

		frame.setSize(new Dimension(500, 300));
		frame.getContentPane().add(new FilenamePanel(null));
		frame.setVisible(true);
	}

	public JButton getBrowseButton() {
		if (browseButton == null) {
			browseButton = new JButton("Browse");
			browseButton.addActionListener(eventHandler);
		}
		return browseButton;
	}

	public JTextField getFilenameField() {
		if (filenameField == null) {
			filenameField = new JTextField("(none selected)", 60);
			filenameField.getDocument().addDocumentListener(eventHandler);
		}
		return filenameField;
	}

	public boolean showFileSelectDialog() {
		String fileName = "outgoing" + File.separatorChar;
		String saveDir = fileName;

		File f = new File(saveDir);

		JFileChooser chooser = new JFileChooser(f);

		chooser.setCurrentDirectory(f);

		javax.swing.filechooser.FileFilter filter = new javax.swing.filechooser.FileFilter() {
			@Override
			public boolean accept(File pathname) {
				if (pathname.isDirectory()) {
					return true;
				}
				String fn = pathname.getName();
				int idx = fn.lastIndexOf('.');
				String extension = "";

				if (idx >= 0) {
					extension = fn.substring(idx);
				}
				if (extension.equalsIgnoreCase(".jar")) {
					return true;
				}
				return false;
			}

			@Override
			public String getDescription() {
				return "JAR files";
			}
		};

		chooser.setFileFilter(filter);

		boolean done = false;

		while (!done) {
			done = true;
			int rv = chooser.showSaveDialog(this);
			String robotFileName = null;

			if (rv == JFileChooser.APPROVE_OPTION) {
				robotFileName = chooser.getSelectedFile().getPath();
				if (robotFileName.toLowerCase().indexOf(".jar") < 0) {
					robotFileName += ".jar";
				}
				File outFile = new File(robotFileName);

				if (outFile.exists()) {
					int ok = JOptionPane.showConfirmDialog(this,
							robotFileName + " already exists.  Are you sure you want to replace it?", "Warning",
							JOptionPane.YES_NO_CANCEL_OPTION);

					if (ok == JOptionPane.NO_OPTION) {
						done = false;
						continue;
					}
					if (ok == JOptionPane.CANCEL_OPTION) {
						return false;
					}
				}
				getFilenameField().setText(robotFileName);
				fireStateChanged();
			} else {
				return false;
			}
		}
		return true;
	}
}
