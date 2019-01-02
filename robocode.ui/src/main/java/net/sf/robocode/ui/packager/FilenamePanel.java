/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.ui.packager;


import net.sf.robocode.io.FileUtil;
import net.sf.robocode.io.Logger;
import net.sf.robocode.repository.IRobotSpecItem;
import net.sf.robocode.ui.dialog.WizardPanel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Caret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.List;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Robert D. Maupin (contributor)
 */
@SuppressWarnings("serial")
public class FilenamePanel extends WizardPanel {
	private final RobotPackager robotPackager;

	private final EventHandler eventHandler = new EventHandler();
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
				if (!outgoingFile.mkdirs()) {
					Logger.logError("Cannot create: " + outgoingFile);
				}
			}
			String jarName = "myrobots.jar";
			List<IRobotSpecItem> selectedRobots = robotPackager.getRobotSelectionPanel().getSelectedRobots();

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
		return (filenameField.getText().toLowerCase().indexOf(".jar") != 0);
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("options");

		frame.setSize(new Dimension(500, 300));
		frame.getContentPane().add(new FilenamePanel(null));

		try {
			SwingUtilities.invokeAndWait(new ShowFrameWorker(frame));
		} catch (InterruptedException e) {
			// Immediately reasserts the exception by interrupting the caller thread itself
			Thread.currentThread().interrupt();

			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
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

	public void showFileSelectDialog() {
		File f = new File("outgoing" + File.separatorChar);

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
				return extension.equalsIgnoreCase(".jar");
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
			String robotFileName;

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
						return;
					}
				}
				getFilenameField().setText(robotFileName);
				fireStateChanged();
			}
		}
	}

	static class ShowFrameWorker implements Runnable {
		final JFrame frame;

		public ShowFrameWorker(JFrame frame) {
			this.frame = frame;
		}

		public void run() {
			if (frame != null) {
				frame.setVisible(true);
			}
		}
	}
}
