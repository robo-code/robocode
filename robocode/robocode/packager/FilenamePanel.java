/*******************************************************************************
 * Copyright (c) 2001 Mathew Nelson
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew Nelson - initial API and implementation
 *******************************************************************************/
package robocode.packager;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

import robocode.dialog.*;
import robocode.repository.*;
import robocode.util.*;

import javax.swing.text.*;

/**
 * Insert the type's description here.
 * Creation date: (10/19/2001 12:07:51 PM)
 * @author: Administrator
 */
public class FilenamePanel extends WizardPanel {
	RobotPackager robotPackager = null;

	EventHandler eventHandler = new EventHandler();
	private boolean robocodeErrorShown = false;
	
class EventHandler implements ActionListener, DocumentListener, ComponentListener {
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
		if (e.getSource() == getBrowseButton())
			showFileSelectDialog();
	}
	public void componentMoved(ComponentEvent e) {}
	public void componentResized(ComponentEvent e) {}
	public void componentHidden(ComponentEvent e) {
	}
	public void componentShown(ComponentEvent e) {
		if (true) //getFilenameField().getText().equals("(none selected)"))
		{
			String fileName = new File(Constants.cwd(),"robots").getAbsolutePath() + File.separator;
			File outgoingFile = new File(fileName);
			if (!outgoingFile.exists())
				outgoingFile.mkdirs();
			String jarName = "myrobots.jar";
//			if (!robotPackager.isTeamPackager())
//			{
				FileSpecificationVector selectedRobots = robotPackager.getRobotSelectionPanel().getSelectedRobots();
				if (selectedRobots != null && selectedRobots.size() == 1)
				{
					jarName = ((FileSpecification)selectedRobots.elementAt(0)).getFullClassName() + "_" + robotPackager.getPackagerOptionsPanel().getVersionField().getText() + ".jar";
				}
//			}
/*			else
			{
				jarName = robotPackager.getTeamOptionsPanel().getTeamPackage() +
					robotPackager.getTeamOptionsPanel().getTeamNameField().getText() + "_" +
					robotPackager.getTeamOptionsPanel().getVersionField().getText() + ".jar";
			
			//robotPackager.getTeamOptionsPanel().getTeamName() + "_" + robotPackager.getTeamOptionsPanel().getVersion() + ".jar";
			}
		*/
			getFilenameField().setText(fileName + jarName);
			Caret caret = getFilenameField().getCaret();
			caret.setDot(fileName.length()); //robocode.getWriteRobotPath().length() + 10);
			caret.moveDot(fileName.length() + jarName.length() - 4); //caret.getDot() + 16);
		}
		getFilenameField().requestFocus();
	}
}
	public javax.swing.JPanel robotListPanel = null;
/**
 * PackagerOptionsPanel constructor comment.
 */
public FilenamePanel(RobotPackager robotPackager) {
	super();
	this.robotPackager = robotPackager;
	initialize();
}


/**
 * Insert the method's description here.
 * Creation date: (10/19/2001 12:09:49 PM)
 */
private void initialize() {
	setName("filenamePanel");
	setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
	setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
	add(new JLabel("Please type in a .jar file to save this robot package to: "));
	//add(new JLabel("Example:  robots" + File.separator + "myrobot.jar"));
	add(getFilenameField());
	add(getBrowseButton());
	add(new JPanel());
	addComponentListener(eventHandler);
}
/**
 * Insert the method's description here.
 * Creation date: (10/20/2001 11:24:52 AM)
 */
public boolean isReady() {
	if (filenameField.getText() == null)
		return false;
	int robocodeIndex = filenameField.getText().lastIndexOf(File.separatorChar);
	if (robocodeIndex > 0)
	{
		if (filenameField.getText().substring(robocodeIndex+1).indexOf("robocode") == 0)
		{
			if (!robocodeErrorShown)
			{
				robocodeErrorShown = true;
				new Thread(new Runnable() {
					public void run() {
						JOptionPane.showMessageDialog(FilenamePanel.this,"Filename cannot begin with robocode");
					}
				}).start();
			}
			return false;
		}
	}
	if (filenameField.getText().toLowerCase().indexOf(".jar") > 0)
		return true;
	return false;
}
/**
 * Insert the method's description here.
 * Creation date: (10/19/2001 2:27:51 PM)
 * @param args java.lang.String[]
 */
public static void main(String[] args) {
	JFrame frame = new JFrame("options");
	frame.setSize(new Dimension(500,300));
	frame.getContentPane().add(new FilenamePanel(null));
	frame.show();
}


	private JButton browseButton = null;
	private JTextField filenameField = null;

/**
 * Insert the method's description here.
 * Creation date: (10/22/2001 3:28:23 PM)
 * @return javax.swing.JButton
 */
public javax.swing.JButton getBrowseButton() {
	if (browseButton == null)
	{
		browseButton = new JButton("Browse");
		browseButton.addActionListener(eventHandler);
	}
	return browseButton;
}

/**
 * Insert the method's description here.
 * Creation date: (10/22/2001 3:12:22 PM)
 * @return javax.swing.JLabel
 */
public javax.swing.JTextField getFilenameField() {
	if (filenameField == null)
	{
		filenameField = new JTextField("(none selected)",60);
		filenameField.setMaximumSize(filenameField.getPreferredSize());
		filenameField.getDocument().addDocumentListener(eventHandler);
	}
	return filenameField;
}

/**
 * Insert the method's description here.
 * Creation date: (10/22/2001 3:16:43 PM)
 */
public boolean showFileSelectDialog() {
	String fileName = "outgoing" + File.separatorChar;
	String saveDir = fileName;

	JFileChooser chooser;

	File f = new File(saveDir);
	String jv = System.getProperty("java.version");
	chooser = new JFileChooser(f); //.getAbsoluteFile().toString());
	chooser.setCurrentDirectory(f);
		
	javax.swing.filechooser.FileFilter filter = new javax.swing.filechooser.FileFilter()
	{
		public boolean accept(java.io.File pathname)
		{
			if (pathname.isDirectory())
			  return true;
			String fn = pathname.getName();
			int idx = fn.lastIndexOf('.');
			String extension = "";
			if (idx >= 0)
			  extension = fn.substring(idx);
			if (extension.equalsIgnoreCase(".jar"))
			  return true;
			return false;
		}
		public String getDescription() {
			return "JAR files";
		}
	};

	chooser.setFileFilter(filter);

	boolean done = false;
	while (!done)
	{
		done = true;
		int rv = chooser.showSaveDialog(this);
		String robotFileName = null;
		if (rv == JFileChooser.APPROVE_OPTION)
		{
			robotFileName = chooser.getSelectedFile().getPath();
			if (robotFileName.toLowerCase().indexOf(".jar") < 0)
				robotFileName += ".jar";
			File outFile = new File(robotFileName);
			if (outFile.exists())
			{
				int ok = JOptionPane.showConfirmDialog(this,
					robotFileName + " already exists.  Are you sure you want to replace it?",
					"Warning", JOptionPane.YES_NO_CANCEL_OPTION);
				if (ok == JOptionPane.NO_OPTION)
				{
					done = false;
					continue;
				}
				if (ok == JOptionPane.CANCEL_OPTION)
				{
					return false;
				}
			}
			getFilenameField().setText(robotFileName);
			fireStateChanged();
		}
		else
			return false;
	}
	return true;
}
}