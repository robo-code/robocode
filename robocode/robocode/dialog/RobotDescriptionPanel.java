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
package robocode.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.util.*;
import java.io.*;

import java.net.*;

import robocode.repository.*;
import robocode.util.*;
import robocode.manager.RobocodeManager;

/**
 * Insert the type's description here.
 * Creation date: (10/15/2001 5:14:58 PM)
 * @author: Administrator
 */
public class RobotDescriptionPanel extends javax.swing.JPanel {
	JLabel robotNameLabel = null;
	JLabel descriptionLabel[] = new JLabel[3];
	JPanel descriptionPanel = null;
	JButton detailsButton = null;
	JLabel authorNameLabel = null;
	JLabel authorEmailLabel = null;
	JLabel authorWebsiteLabel = null;
	JLabel javaSourceIncludedLabel = null;
	JLabel robotVersionLabel = null;
	JLabel robocodeVersionLabel = null;
	JLabel filePathLabel = null;
	String blankString;
	public FileSpecification currentRobotSpecification = null;
	private RobocodeManager manager = null;

	EventManager eventManager = new EventManager();

	class EventManager implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == getDetailsButton())
			{
				if (currentRobotSpecification != null)
				{
					URL htmlFile = currentRobotSpecification.getWebpage();
					if (htmlFile != null && !htmlFile.equals(""))
					{
						try {
							manager.getBrowserManager().openURL(htmlFile.toString());
						} catch (IOException ex) {
						}
					}
				}
			}
		}
	}
/**
 * NewBattleRobotsTabDescriptionPanel constructor.
 */
public RobotDescriptionPanel(RobocodeManager manager) {
	super();
	this.manager = manager;
	initialize();
	blankString = "";
	for (int i = 0; i < 72; i++)
		blankString += " ";
}
/**
 * Insert the method's description here.
 * Creation date: (8/26/2001 8:41:29 PM)
 */
public JLabel getAuthorEmailLabel() {
	if (authorEmailLabel == null) {
		try {
			authorEmailLabel = new javax.swing.JLabel();
			authorEmailLabel.setName("authorEmailLabel");
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return authorEmailLabel;
}
public JLabel getFilePathLabel() {
	if (filePathLabel == null) {
		try {
			filePathLabel = new javax.swing.JLabel();
			filePathLabel.setName("filePathLabel");
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return filePathLabel;
}
/**
 * Insert the method's description here.
 * Creation date: (8/26/2001 8:41:29 PM)
 */
public JLabel getAuthorNameLabel() {
	if (authorNameLabel == null) {
		try {
			authorNameLabel = new javax.swing.JLabel();
			authorNameLabel.setName("authorNameLabel");
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return authorNameLabel;
}
/**
 * Insert the method's description here.
 * Creation date: (8/26/2001 8:41:29 PM)
 */
public JLabel getAuthorWebsiteLabel() {
	if (authorWebsiteLabel == null) {
		try {
			authorWebsiteLabel = new javax.swing.JLabel();
			authorWebsiteLabel.setName("authorWebsiteLabel");
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return authorWebsiteLabel;
}
/**
 * Insert the method's description here.
 * Creation date: (8/26/2001 8:41:29 PM)
 */
public JLabel getDescriptionLabel(int index) {
	if (descriptionLabel[index] == null) {
		try {
			descriptionLabel[index] = new javax.swing.JLabel();
			descriptionLabel[index].setName("descriptionLabel" + index);
			descriptionLabel[index].setFont(new Font("Monospaced",Font.PLAIN,10));
			descriptionLabel[index].setHorizontalAlignment(JLabel.LEFT);
			descriptionLabel[index].setText(blankString);
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return descriptionLabel[index];
}
/**
 * Insert the method's description here.
 * Creation date: (11/1/2001 2:23:34 PM)
 * @return javax.swing.JPanel
 */
public javax.swing.JPanel getDescriptionPanel() {
	if (descriptionPanel == null)
	{
		descriptionPanel = new JPanel();
		descriptionPanel.setName("descriptionPanel");
		descriptionPanel.setAlignmentY(JPanel.CENTER_ALIGNMENT);
		descriptionPanel.setLayout(new BoxLayout(descriptionPanel,BoxLayout.Y_AXIS));
		descriptionPanel.setBorder(BorderFactory.createEtchedBorder());
		for (int i = 0; i < 3; i++)
			descriptionPanel.add(getDescriptionLabel(i));
	}
	return descriptionPanel;
}
/**
 * Insert the method's description here.
 * Creation date: (11/1/2001 2:58:51 PM)
 * @return javax.swing.JButton
 */
public javax.swing.JButton getDetailsButton() {
	if (detailsButton == null)
	{
		detailsButton = new JButton("Webpage");
		detailsButton.setVisible(false);
		detailsButton.setAlignmentY(JButton.CENTER_ALIGNMENT);
		detailsButton.addActionListener(eventManager);
	}
	return detailsButton;
}
/**
 * Insert the method's description here.
 * Creation date: (8/26/2001 8:41:29 PM)
 */
public JLabel getJavaSourceIncludedLabel() {
	if (javaSourceIncludedLabel == null) {
		try {
			javaSourceIncludedLabel = new javax.swing.JLabel();
			javaSourceIncludedLabel.setName("javaSourceIncludedLabel");
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return javaSourceIncludedLabel;
}
/**
 * Insert the method's description here.
 * Creation date: (8/26/2001 8:41:29 PM)
 */
public JLabel getRobocodeVersionLabel() {
	if (robocodeVersionLabel == null) {
		try {
			robocodeVersionLabel = new javax.swing.JLabel();
			robocodeVersionLabel.setName("robocodeVersionLabel");
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return robocodeVersionLabel;
}
/**
 * Insert the method's description here.
 * Creation date: (8/26/2001 8:41:29 PM)
 */
public JLabel getRobotNameLabel() {
	if (robotNameLabel == null) {
		try {
			robotNameLabel = new javax.swing.JLabel();
			robotNameLabel.setName("robotNameLabel");
			robotNameLabel.setHorizontalAlignment(JLabel.CENTER);
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return robotNameLabel;
}
/**
 * Insert the method's description here.
 * Creation date: (8/26/2001 8:41:29 PM)
 */
public JLabel getRobotVersionLabel() {
	if (robotVersionLabel == null) {
		try {
			robotVersionLabel = new javax.swing.JLabel();
			robotVersionLabel.setName("robotVersionLabel");
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return robotVersionLabel;
}
/**
 * Return the Page property value.
 * @return javax.swing.JPanel
 */
private void initialize() {
	try {
		setName("DescriptionPanel");
		setLayout(new BorderLayout());
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		p.add(getRobotNameLabel(),BorderLayout.CENTER);


		JPanel q = new JPanel();
		//q.setPreferredSize(new Dimension(80,10));
		q.setLayout(new FlowLayout(FlowLayout.CENTER,1,1));
		q.add(getRobocodeVersionLabel());
			
		p.add(q,BorderLayout.EAST);
		q = new JPanel();
		//q.setPreferredSize(new Dimension(80,10));
		q.setLayout(new FlowLayout(FlowLayout.CENTER,1,1));
		p.add(q,BorderLayout.WEST);
		
		add(p,BorderLayout.NORTH);

		p = new JPanel();
		p.setLayout(new BoxLayout(p,BoxLayout.X_AXIS));
		//p.setPreferredSize(new Dimension(80,10));
		p.add(getDetailsButton());
		add(p,BorderLayout.WEST);

		p = new JPanel();
		p.setLayout(new FlowLayout(FlowLayout.CENTER,1,1));
		p.add(getDescriptionPanel()); //,BorderLayout.CENTER);
		add(p,BorderLayout.CENTER);
		
		p = new JPanel();
		p.setLayout(new FlowLayout(FlowLayout.CENTER,1,1));
		p.add(getFilePathLabel());
		add(p,BorderLayout.SOUTH);
		
		p = new JPanel();
		p.setLayout(new BoxLayout(p,BoxLayout.X_AXIS));//  new FlowLayout(FlowLayout.CENTER,1,1));
		//p.setPreferredSize(new Dimension(80,10));
		add(p,BorderLayout.EAST);
	} catch (java.lang.Throwable e) {
		log(e);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (8/22/2001 1:41:21 PM)
 * @param e java.lang.Exception
 */
public void log(Throwable e) {
	Utils.log(e);
}
/**
 * Insert the method's description here.
 * Creation date: (10/15/2001 5:11:50 PM)
 * @param classManager robocode.peer.robot.RobotClassManager
 */
public void showDescription(FileSpecification robotSpecification) {

	this.currentRobotSpecification = robotSpecification;
	if (robotSpecification == null)
	{
		//descriptionPanel.setBorder(null);
		getRobotNameLabel().setText(" ");
		//getRobotVersionLabel().setText("");
		//getJavaSourceIncludedLabel().setText("");
		//getRobocodeVersionLabel().setText("");
		//getAuthorNameLabel().setText("");
		//getAuthorEmailLabel().setText("");
		//getAuthorWebsiteLabel().setText("");
		for (int i = 0; i < 3; i++)
			getDescriptionLabel(i).setText(blankString);
		getDetailsButton().setVisible(false);
		getRobocodeVersionLabel().setText("");
		getFilePathLabel().setText("");
	}
	else
	{
		String name = robotSpecification.getNameManager().getUniqueFullClassNameWithVersion();
		if (name.charAt(name.length() - 1) == '*')
		{
			name = name.substring(0,name.length()-1) + " (development version)";
		}

		String s = robotSpecification.getAuthorName();
		if (s != null && !s.equals(""))
			name += " by " + s;
		getRobotNameLabel().setText(name);
		if (robotSpecification.getJarFile() != null)
			getFilePathLabel().setText(robotSpecification.getJarFile().getPath());
		else
			getFilePathLabel().setText(robotSpecification.getFilePath());

//		if (classManager.getProperty(RobotProperties.ROBOCODE_VERSION) != null)
//			line1 += " built for Robocode " + classManager.getProperty(RobotProperties.ROBOCODE_VERSION);
/*
		if (classManager.getProperty(RobotProperties.ROBOT_JAVA_SOURCE_INCLUDED) != null)
		{
			if (classManager.getProperty(RobotProperties.ROBOT_JAVA_SOURCE_INCLUDED).equalsIgnoreCase("true") ||
				classManager.getProperty(RobotProperties.ROBOT_JAVA_SOURCE_INCLUDED).equalsIgnoreCase("yes"))
			{
				line1 += " - source included";
			}
			else
				line1 += " - class file only";
		}
		*/
		/*
		String line2 = "";
		if (classManager.getProperty(RobotProperties.ROBOT_AUTHOR_EMAIL) != null)
			line2 += " (" + classManager.getProperty(RobotProperties.ROBOT_AUTHOR_EMAIL) + ")";
			
		if (classManager.getProperty(RobotProperties.ROBOT_AUTHOR_WEBSITE) != null)
			line2 += " - " + classManager.getProperty(RobotProperties.ROBOT_AUTHOR_WEBSITE);
*/

		String desc = robotSpecification.getDescription();
		int count = 0;
		if (desc != null)
		{
			StringTokenizer tok = new StringTokenizer(desc,"\n");
			while (tok.hasMoreTokens() && count < 3)
			{
				String line = tok.nextToken();
				if (line != null)
				{
					if (line.length() > 72)
						line = line.substring(0,72);
					for (int i = line.length(); i < 72; i++)
						line += " ";
					getDescriptionLabel(count).setText(line);
				}
				count++;
			}
		}
		else
		{
			//descriptionPanel.setBorder(null);
		}
		for (int i = count; i < 3; i++)
		{
			getDescriptionLabel(i).setText(blankString);
		}
		
		/*	
		getRobotNameLabel().setText("Name: " + classManager.getFullClassName());
		getRobotVersionLabel().setText("Version: " + classManager.getProperty(RobotProperties.ROBOT_VERSION));
		getJavaSourceIncludedLabel().setText("Source available: " + classManager.getProperty(RobotProperties.ROBOT_JAVA_SOURCE_INCLUDED));
		getRobocodeVersionLabel().setText("Compiled for Robocode version " + classManager.getProperty(RobotProperties.ROBOCODE_VERSION));
		getAuthorNameLabel().setText("Author: " + classManager.getProperty(RobotProperties.ROBOT_AUTHOR_NAME));
		getAuthorEmailLabel().setText("Email: " + classManager.getProperty(RobotProperties.ROBOT_AUTHOR_EMAIL));
		getAuthorWebsiteLabel().setText("Website: " + classManager.getProperty(RobotProperties.ROBOT_AUTHOR_WEBSITE));
		getDescriptionLabel().setText("Description: " + classManager.getProperty(RobotProperties.ROBOT_DESCRIPTION));
		*/

		URL u = robotSpecification.getWebpage();
		if (u != null && !u.equals(""))
		{
			getDetailsButton().setVisible(true);
		}
		else
			getDetailsButton().setVisible(false);

		String v = robotSpecification.getRobocodeVersion();
		if (v == null)
			getRobocodeVersionLabel().setText("");
		else
			getRobocodeVersionLabel().setText("Built for " + v);
			

	}
	getDescriptionPanel().setMaximumSize(getDescriptionPanel().getPreferredSize());
}
}
