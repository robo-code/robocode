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
import java.awt.event.*;
import java.util.*;
import java.io.*;
import java.util.jar.*;
import java.net.*;

import robocode.peer.robot.RobotClassManager;
import robocode.repository.*;
import robocode.dialog.*;
import robocode.util.*;
import robocode.manager.*;

/**
 * Insert the type's description here.
 * Creation date: (10/11/2001 2:12:31 PM)
 * @author: Administrator
 */
public class RobotPackager extends JDialog implements WizardListener{
	String unusedrobotPath = null;

	private int minRobots = 1;
	private int maxRobots = 1; //250;

	private JPanel robotPackagerContentPane = null;

	private WizardCardPanel wizardPanel = null;
	private WizardController buttonsPanel = null;
	private FilenamePanel filenamePanel = null;
	private ConfirmPanel confirmPanel = null;
	private RobotSelectionPanel robotSelectionPanel = null;
	private PackagerOptionsPanel packagerOptionsPanel = null;
	//private robocode.dialog.TeamCreatorOptionsPanel teamOptionsPanel = null;
	//private boolean teamPackager = false;
	
	public byte buf[] = new byte[4096];
	private StringWriter output = null;
	private RobotRepositoryManager robotManager;

	private EventHandler eventHandler = new EventHandler();
	class EventHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("Refresh"))
			{
				getRobotSelectionPanel().refreshRobotList();
			}
		};
	}
/**
 * Packager constructor comment.
 */
public RobotPackager(RobotRepositoryManager robotManager, boolean isTeamPackager) {
	super(robotManager.getManager().getWindowManager().getRobocodeFrame());
	this.robotManager = robotManager;
	//this.teamPackager = isTeamPackager;
	//if (isTeamPackager)
	//	maxRobots = 10;
	initialize();
}
/**
 * Insert the method's description here.
 * Creation date: (10/18/2001 2:04:48 PM)
 */
public void cancelButtonActionPerformed() {
	java.awt.AWTEvent evt = new java.awt.event.WindowEvent(this,java.awt.event.WindowEvent.WINDOW_CLOSING);
	this.dispatchEvent(evt);
	return;
}
/**
 * Insert the method's description here.
 * Creation date: (10/22/2001 8:16:29 PM)
 * @param in java.io.FileInputStream
 * @param out robocode.util.NoDuplicateJarOutputStream
 */
public void copy(FileInputStream in, NoDuplicateJarOutputStream out) throws IOException {
	while (in.available() > 0)
	{
		int count = in.read(buf,0,4096);
		out.write(buf,0,count);
	}
		
}
/**
 * Insert the method's description here.
 * Creation date: (10/18/2001 2:04:48 PM)
 */
public void finishButtonActionPerformed() {
	String resultsString;
	
	int rc = packageRobots();
	ConsoleDialog d;
	d = new ConsoleDialog(robotManager.getManager().getWindowManager().getRobocodeFrame(),"Packaging results",false);
	if (rc == 0)
		resultsString = "Robots Packaged Successfully.\n" + output.toString();
	else if (rc == 4)
		resultsString = "Robots Packaged, but with warnings.\n" + output.toString();
	else if (rc == 8)
		resultsString = "Robots Packaging failed.\n" + output.toString();
	else resultsString = "FATAL: Unknown return code " + rc + " from packager.\n" + output.toString();
	d.setText(resultsString);
	d.pack();
	d.pack();
	Utils.packCenterShow(this,d);
	if (rc < 8)
		this.dispose();
	
}
/**
 * Return the buttonsPanel
 * @return javax.swing.JButton
 */
private WizardController getButtonsPanel() {
	if (buttonsPanel == null) {
		try {
			buttonsPanel = getWizardPanel().getWizardController();
			buttonsPanel.setName("buttonsPanel");
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return buttonsPanel;
}
/**
 * Insert the method's description here.
 * Creation date: (10/11/2001 2:13:04 PM)
 * @param name java.lang.String
 */
public Enumeration getClasses(RobotClassManager robotClassManager) throws ClassNotFoundException {
/*	RobotPeer r = new RobotPeer(classManager,robotManager,0);
	RobocodeClassLoader cl = new RobocodeClassLoader(getClass().getClassLoader(),classManager.getRobotProperties(),r);
	r.getRobotClassManager().setRobotClassLoader(cl);
	Class c = cl.loadRobotClass(r.getRobotClassManager().getFullClassName(),true);
	return classManager.getReferencedClasses();
*/	
	Class c = robotClassManager.getRobotClassLoader().loadRobotClass(robotClassManager.getFullClassName(),true);
	return robotClassManager.getReferencedClasses();
}
/**
 * Return the buttonsPanel
 * @return javax.swing.JButton
 */
private ConfirmPanel getConfirmPanel() {
	if (confirmPanel == null) {
		try {
			confirmPanel = new ConfirmPanel(this);
			confirmPanel.setName("confirmPanel");
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return confirmPanel;
}
/**
 * Return the optionsPanel
 * @return robocode.packager.PackagerOptionsPanel
 */
protected FilenamePanel getFilenamePanel() {
	if (filenamePanel == null) {
		try {
			filenamePanel = new FilenamePanel(this);
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return filenamePanel;
}
/**
 * Return the optionsPanel
 * @return robocode.packager.PackagerOptionsPanel
 */
protected PackagerOptionsPanel getPackagerOptionsPanel() {
	if (packagerOptionsPanel == null) {
		try {
			packagerOptionsPanel = new PackagerOptionsPanel(this);
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return packagerOptionsPanel;
}

/**
 * Return the optionsPanel
 * @return robocode.dialog.TeamCreatorOptionsPanel
 */
/*protected TeamCreatorOptionsPanel getTeamOptionsPanel() {
	if (teamOptionsPanel == null) {
		try {
			teamOptionsPanel = new TeamCreatorOptionsPanel(this);
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return teamOptionsPanel;
}
*/

/**
 * Return the newBattleDialogContentPane
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getRobotPackagerContentPane() {
	if (robotPackagerContentPane == null) {
		try {
			robotPackagerContentPane = new javax.swing.JPanel();
			robotPackagerContentPane.setName("robotPackagerContentPane");
			robotPackagerContentPane.setLayout(new java.awt.BorderLayout());
			robotPackagerContentPane.add(getButtonsPanel(),java.awt.BorderLayout.SOUTH);
			robotPackagerContentPane.add(getWizardPanel(),java.awt.BorderLayout.CENTER);
			getWizardPanel().getWizardController().setFinishButtonText("Package!");
			robotPackagerContentPane.registerKeyboardAction(eventHandler,"Refresh",KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0),JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
			robotPackagerContentPane.registerKeyboardAction(eventHandler,"Refresh",KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0),JComponent.WHEN_FOCUSED);
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return robotPackagerContentPane;
}
/**
 * Return the Page property value.
 * @return javax.swing.JPanel
 */
public RobotSelectionPanel getRobotSelectionPanel() {
	if (robotSelectionPanel == null) {
		try {
			//if (!isTeamPackager())
				robotSelectionPanel = new RobotSelectionPanel(robotManager, minRobots, maxRobots, false,
					"Select the robot or team you would like to package.", /*true*/false, false, false /*true*/, true, false, true, null);
			//else
			//	robotSelectionPanel = new RobotSelectionPanel(robotManager, minRobots, maxRobots, false,
			//		"Select the robots for the team you would like to package.", /*true*/false, true, true, false, true, null);
			robotSelectionPanel.setName("Robots available for packaging");
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return robotSelectionPanel;
}
/**
 * Return the tabbedPane.
 * @return javax.swing.JTabbedPane
 */
private WizardCardPanel getWizardPanel() {
	if (wizardPanel == null) {
		try {
			wizardPanel = new WizardCardPanel(this);
			wizardPanel.setName("wizardPanel");
			wizardPanel.add(getRobotSelectionPanel(),"Select robot");
//			if (!isTeamPackager())
				wizardPanel.add(getPackagerOptionsPanel(),"Select options");
//			else
//				wizardPanel.add(getTeamOptionsPanel(),"Select options");
			wizardPanel.add(getFilenamePanel(),"Select filename");
			wizardPanel.add(getConfirmPanel(),"Confirm");
			//getWizardPanelLayout().addLayoutComponent(getRobotSelectionPanel(),"Select robot");
			//getWizardPanelLayout().addLayoutComponent(getPackagerOptionsPanel(),"Select options");
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return wizardPanel;
}
/**
 * Insert the method's description here.
 * Creation date: (10/18/2001 11:25:12 AM)
 */
private void initialize() {
	try {
		setName("Robot Packager");
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Robot Packager");
		setContentPane(getRobotPackagerContentPane());
	} catch (java.lang.Throwable e) {
		log(e);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (8/22/2001 1:41:21 PM)
 * @param e java.lang.Exception
 */
public void log(String s) {
	Utils.log(s);
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
 * Creation date: (10/11/2001 2:13:04 PM)
 * @param name java.lang.String
 */
/*
public void packageRobot(String name) throws ClassNotFoundException {
	RobotProperties robotProperties = new RobotProperties();
	RobotClassManager robotClassManager = new RobotClassManager(robotProperties);
	RobotPeer r = new RobotPeer(robotClassManager,robotManager,0);
	RobocodeClassLoader cl = new RobocodeClassLoader(getClass().getClassLoader(),robotClassManager.getRobotProperties(),r);
	r.getRobotClassManager().setRobotClassLoader(cl);
	Class c = cl.loadRobotClass(r.getRobotClassManager().getFullClassName(),true);
}*/
/**
 * Insert the method's description here.
 * Creation date: (10/22/2001 7:33:04 PM)
 */
private int packageRobots() {
	robotManager.clearRobotList();
	int rv = 0;
	output = new StringWriter();
	PrintWriter out = new PrintWriter(output);

	out.println("Robot Packager");	
	FileSpecificationVector robotSpecificationsVector = robotManager.getRobotRepository().getRobotSpecificationsVector(false,false,false,false, false, false);
	String jarFilename = getFilenamePanel().getFilenameField().getText();
	File f = new File(jarFilename);
	if (f.exists())
	{
		int ok = JOptionPane.showConfirmDialog(this,
			jarFilename + " already exists.  Are you sure you want to replace it?",
			"Warning", JOptionPane.YES_NO_CANCEL_OPTION);
		if (ok == JOptionPane.NO_OPTION)
		{
			out.println("Cancelled by user.");
			return -1;
		}
		if (ok == JOptionPane.CANCEL_OPTION)
		{
			out.println("Cancelled by user.");
			return -1;
		}
		out.println("Overwriting " + jarFilename);
	}

	FileSpecificationVector selectedRobots = getRobotSelectionPanel().getSelectedRobots();

	// Create the jar file
	Manifest manifest = new Manifest();
	manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION,"1.0");
	String robots = "";
	for (int i = 0; i < selectedRobots.size(); i++)
	{
		robots += selectedRobots.elementAt(i).getFullClassName();
		if (i < selectedRobots.size() - 1)
			robots += ",";
	}
	manifest.getMainAttributes().put(new Attributes.Name("robots"),robots);
	//robotSpecification.getFullClassName());
	NoDuplicateJarOutputStream jarout;
	try {
		out.println("Creating Jar file: " + f.getName());
		jarout = new NoDuplicateJarOutputStream(new FileOutputStream(f));
		jarout.setComment(robotManager.getManager().getVersionManager().getVersion() + " - Robocode version");	} catch (Exception e) {
		out.println(e);
		return 8;
	}
	
//	TeamSpecification teamSpecification = null;
//	if (isTeamPackager())
//		teamSpecification = new TeamSpecification();
		
	for (int i = 0; i < selectedRobots.size(); i++)
	{
		FileSpecification fileSpecification = (FileSpecification)selectedRobots.elementAt(i);

//		RobotSpecification newSpecification;

		if (fileSpecification instanceof RobotSpecification)
		{
			RobotSpecification robotSpecification = (RobotSpecification)fileSpecification;
			if (robotSpecification.isDevelopmentVersion())
			{
				robotSpecification.setRobotDescription(getPackagerOptionsPanel().getDescriptionArea().getText());
				robotSpecification.setName(robotSpecification.getFullClassName());
				robotSpecification.setRobotJavaSourceIncluded(getPackagerOptionsPanel().getIncludeSource().isSelected());
				robotSpecification.setRobotAuthorName(getPackagerOptionsPanel().getAuthorField().getText());
				URL u = null;
				String w= getPackagerOptionsPanel().getWebpageField().getText();
				if (w.equals(""))
					u = null;
				else
				{
					try {
						u = new URL(w);
					} catch (MalformedURLException e) {
						try {
							u = new URL("http://" + w);
							getPackagerOptionsPanel().getWebpageField().setText(u.toString());
						} catch (MalformedURLException e2) {
							u = null;
						}
					}
				}
				robotSpecification.setRobotWebpage(u);
				robotSpecification.setRobocodeVersion(robotManager.getManager().getVersionManager().getVersion());
				try {
					robotSpecification.store(new FileOutputStream(new File(robotSpecification.getThisFileName())),"Robot Properties");
				} catch (IOException e) {
					rv = 4;
					out.println("Unable to save properties: " + e);
					out.println("Attempting to continue...");
				}
				// Create clone with version for jar
				robotSpecification = (RobotSpecification)robotSpecification.clone();
				robotSpecification.setRobotVersion(getPackagerOptionsPanel().getVersionField().getText());
				addRobotSpecification(out,jarout,robotSpecification);
			}
			else
			{
				out.println("You Cannot package a packaged robot!");
			}
		}
		else if (fileSpecification instanceof TeamSpecification)
		{
			TeamSpecification teamSpecification = (TeamSpecification)fileSpecification;
				URL u = null;
				String w= getPackagerOptionsPanel().getWebpageField().getText();
				if (w.equals(""))
					u = null;
				else
				{
					try {
						u = new URL(w);
					} catch (MalformedURLException e) {
						try {
							u = new URL("http://" + w);
							getPackagerOptionsPanel().getWebpageField().setText(u.toString());
						} catch (MalformedURLException e2) {
							u = null;
						}
					}
				}
			teamSpecification.setTeamWebpage(u);
			teamSpecification.setTeamDescription(getPackagerOptionsPanel().getDescriptionArea().getText());
			teamSpecification.setTeamAuthorName(getPackagerOptionsPanel().getAuthorField().getText());
			teamSpecification.setRobocodeVersion(robotManager.getManager().getVersionManager().getVersion());
			
			try {
				teamSpecification.store(new FileOutputStream(new File(teamSpecification.getThisFileName())),"Team Properties");
			} catch (IOException e) {
				rv = 4;
				out.println("Unable to save .team file: " + e);
				out.println("Attempting to continue...");
			}
			
			teamSpecification = (TeamSpecification)teamSpecification.clone();
			teamSpecification.setTeamVersion(getPackagerOptionsPanel().getVersionField().getText());

			StringTokenizer teamTokenizer;
			String bot;
			FileSpecification currentFileSpecification;
			teamTokenizer = new StringTokenizer(teamSpecification.getMembers(),",");
			String newMembers = "";
			while (teamTokenizer.hasMoreTokens())
			{
				if (!(newMembers.equals("")))
					newMembers += ",";
				bot = teamTokenizer.nextToken();
				for (int j = 0; j < robotSpecificationsVector.size();j++)
				{
					currentFileSpecification = (FileSpecification)robotSpecificationsVector.elementAt(j);
					// Teams cannot include teams
					if (currentFileSpecification instanceof TeamSpecification)
						continue;
						
					if (currentFileSpecification.getNameManager().getUniqueFullClassNameWithVersion().equals(bot))
					{
						// Found team member
						RobotSpecification current = (RobotSpecification)currentFileSpecification;
						// Skip nonversioned packaged
						if (!current.isDevelopmentVersion() && (current.getVersion() == null || current.getVersion().equals("")))
						{
//							log("valid: " + current.getValid());
							continue;
						}
						if (current.isDevelopmentVersion() && (current.getVersion() == null || current.getVersion().equals("")))
						{
							current = (RobotSpecification)current.clone();
							current.setRobotVersion("[" + getPackagerOptionsPanel().getVersionField().getText() + "]");
						}
						// Package this member
						newMembers += addRobotSpecification(out,jarout,current);
						break;
					}
				}
			}
			teamSpecification.setMembers(newMembers);
			try {
				try {
			
					JarEntry entry = new JarEntry(teamSpecification.getFullClassName().replace('.','/') + ".team");
					jarout.putNextEntry(entry);
					teamSpecification.store(jarout,"Robocode Robot Team");
					jarout.closeEntry();
					out.println("Added: " + entry);
				} catch (java.util.zip.ZipException e) {
					if (e.getMessage().indexOf("duplicate entry") < 0)
						throw e;
					// ignore duplicate entry, fine, it's already there.
				}
			} catch (Throwable e) {
				rv = 8;
				out.println(e);
			}
			
		}

	}
	try {
		jarout.close();
	} catch (IOException e) {
		out.println(e);
		return 8;
	}
	robotManager.clearRobotList();
	out.println("Packaging complete.");
	return rv;
}

public String addRobotSpecification(PrintWriter out, NoDuplicateJarOutputStream jarout, RobotSpecification robotSpecification)
{
	int rv = 0;
	String propsfn = robotSpecification.getThisFileName();
	String lastVersion = null;
	if (!robotSpecification.isDevelopmentVersion())
	{
		//log("I should look in: " + robotSpecification.getRootDir());
		try {
			File inputJar = robotSpecification.getJarFile();
/*			if (robotSpecification.getRootDir().getParentFile().equals(robotManager.getRobotCache()))
				inputJar = new File(robotManager.getRobotsDirectory(),robotSpecification.getRootDir().getName().substring(0,robotSpecification.getRootDir().getName().length()-1));
			else
				inputJar = new File(robotSpecification.getRootDir().getParentFile(),robotSpecification.getRootDir().getName().substring(0,robotSpecification.getRootDir().getName().length()-1));
				*/
			try {	
				JarEntry entry = new JarEntry(inputJar.getName());
				jarout.putNextEntry(entry);
				FileInputStream input = new FileInputStream(inputJar);
				copy(input,jarout);
				jarout.closeEntry();
				out.println("Added: " + entry);
			} catch (java.util.zip.ZipException e) {
				if (e.getMessage().indexOf("duplicate entry") < 0)
					throw e;
				// ignore duplicate entry, fine, it's already there.
			}			
		} catch (Throwable e) {
			rv = 8;
			log(e);
			out.println(e);
		}
	}
	else
		addToJar(out, jarout, robotSpecification);

	String name = robotSpecification.getName() + " " + robotSpecification.getVersion();
	if (rv != 0)
		return null;
	return name;
}

public int  addToJar(PrintWriter out, NoDuplicateJarOutputStream jarout, RobotSpecification robotSpecification)
{
	int rv = 0;
	RobotClassManager classManager = new RobotClassManager((RobotSpecification)robotSpecification);
	try {
		Enumeration enum = getClasses(classManager);
		String rootDirectory = classManager.getRobotClassLoader().getRootDirectory();
		//log("I will look in: " + rootDirectory);
		// Save props:
		try {
			JarEntry entry = new JarEntry(classManager.getFullClassName().replace('.','/') + ".properties");
			jarout.putNextEntry(entry);
			robotSpecification.store(jarout,"Robot Properties");
			jarout.closeEntry();
			out.println("Added: " + entry);
		} catch (java.util.zip.ZipException e) {
			if (e.getMessage().indexOf("duplicate entry") < 0)
				throw e;
			// ignore duplicate entry, fine, it's already there.
		}			
		
		File html = new File(rootDirectory,classManager.getFullClassName().replace('.','/') + ".html");
		if (html.exists())
		{
			try {
				JarEntry entry = new JarEntry(classManager.getFullClassName().replace('.','/') + ".html");
				jarout.putNextEntry(entry);
				FileInputStream input = new FileInputStream(html);
				copy(input,jarout);
				jarout.closeEntry();
				out.println("Added: " + entry);
			} catch (java.util.zip.ZipException e) {
				if (e.getMessage().indexOf("duplicate entry") < 0)
					throw e;
				// ignore duplicate entry, fine, it's already there.
			}			
		}
	
		while (enum.hasMoreElements())
		{
			String className = (String)enum.nextElement();
			//log("Processing: " + className);
			// Add source file if selected (not inner classes of course)
			if (getPackagerOptionsPanel().getIncludeSource().isSelected())
			{
				if (className.indexOf("$") < 0)
				{
					File javaFile = new File(rootDirectory,className.replace('.',File.separatorChar) + ".java");
					if (javaFile.exists())
					{
						try {
							JarEntry entry = new JarEntry(className.replace('.','/') + ".java");
							jarout.putNextEntry(entry);
							FileInputStream input = new FileInputStream(javaFile);
							copy(input,jarout);
							jarout.closeEntry();
							out.println("Added: " + entry);
						} catch (java.util.zip.ZipException e) {
							if (e.getMessage().indexOf("duplicate entry") < 0)
								throw e;
							// ignore duplicate entry, fine, it's already there.
						}			
					}
					else
						out.println(className.replace('.','/') + ".java does not exist.");
				}
			}
			// Add class file
			try {
				JarEntry entry = new JarEntry(className.replace('.','/') + ".class");
				jarout.putNextEntry(entry);
				FileInputStream input = new FileInputStream(new File(rootDirectory,className.replace('.',File.separatorChar) + ".class"));
				copy(input,jarout);
				jarout.closeEntry();
				out.println("Added: " + entry);
			} catch (java.util.zip.ZipException e) {
				if (e.getMessage().indexOf("duplicate entry") < 0)
					throw e;
				// ignore duplicate entry, fine, it's already there.
			}			
		}
	
		File dataDirectory = new File(rootDirectory,classManager.getFullClassName().replace('.','/') + ".data");
		if (dataDirectory.exists())
		{
			File files[] = dataDirectory.listFiles();
			for (int j = 0; j < files.length; j++)
			{
				try {
					JarEntry entry = new JarEntry(classManager.getFullClassName().replace('.','/') + ".data/" + files[j].getName());
					jarout.putNextEntry(entry);
					FileInputStream input = new FileInputStream(files[j]);
					copy(input,jarout);
					jarout.closeEntry();
					out.println("Added: " + entry);
				} catch (java.util.zip.ZipException e) {
					if (e.getMessage().indexOf("duplicate entry") < 0)
						throw e;
					// ignore duplicate entry, fine, it's already there.
				}
			}
		}
	} catch (Throwable e) {
		rv = 8;
		out.println(e);
	}
	return rv;
}

//protected boolean isTeamPackager()
//{
//	return teamPackager;
//}
}
