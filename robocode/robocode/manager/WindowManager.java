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
package robocode.manager;

import java.io.*;
import javax.swing.*;

import robocode.dialog.*;
import robocode.battle.*;
import robocode.editor.*;
import robocode.packager.*;
import robocode.util.*;

public class WindowManager {
	
	private RobocodeEditor robocodeEditor = null;
	private RobotPackager robotPackager = null;
	private RobotExtractor robotExtractor = null;
	private RobocodeFrame robocodeFrame = null;
	private RobocodeManager manager = null;
	private TeamCreator teamCreator = null;

/**
 * Insert the method's description here.
 * Creation date: (8/22/2001 2:26:17 PM)
 * @param newRobocodeFrame robocode.RoboCodeFrame
 */
public void setRobocodeFrame(RobocodeFrame newRobocodeFrame) {
	robocodeFrame = newRobocodeFrame;
}

public WindowManager(RobocodeManager manager)
{
	this.manager = manager;
	Utils.setLocationFix();
}
	
/**
 * Insert the method's description here.
 * Creation date: (8/22/2001 2:26:17 PM)
 * @return robocode.RoboCodeFrame
 */
public RobocodeFrame getRobocodeFrame() {
	if (robocodeFrame == null)
	{
		// Create the frame
		robocodeFrame = new RobocodeFrame(manager);
	}
	return robocodeFrame;
}


public void showRobocodeFrame()
{
	// Pack frame to size all components
	Utils.packCenterShow(getRobocodeFrame());
	
	// Do it yet again to fix a bug in some JREs
	robocodeFrame.setVisible(true);

	Utils.setStatusLabel(getRobocodeFrame().getStatusLabel());
}

public void showAboutBox() {
	AboutBox aboutBox = new AboutBox(manager);
	Utils.packCenterShow(robocodeFrame,aboutBox);
/*	Dimension dialogSize = aboutBox.getPreferredSize();
	Dimension frameSize = robocodeFrame.getSize();
	Point loc = robocodeFrame.getLocation();
	aboutBox.setLocation((frameSize.width - dialogSize.width) / 2 + loc.x, (frameSize.height - dialogSize.height) / 2 + loc.y);
	aboutBox.setModal(true);
	aboutBox.show();
	*/
}

/**
 * Insert the method's description here.
 * Creation date: (8/22/2001 2:23:11 PM)
 */
public void showBattleOpenDialog() {
	manager.getBattleManager().pauseBattle();
	File f = new File(manager.getBattleManager().getBattlePath());

	JFileChooser chooser;
	chooser = new JFileChooser(f);
		
	javax.swing.filechooser.FileFilter filter = new javax.swing.filechooser.FileFilter()
	{
		public boolean accept(File pathname)
		{
			if (pathname.isDirectory())
			  return true;
			String fn = pathname.getName();
			int idx = fn.lastIndexOf('.');
			String extension = "";
			if (idx >= 0)
			  extension = fn.substring(idx);
			if (extension.equalsIgnoreCase(".battle"))
			  return true;
			return false;
		}
		public String getDescription() {
			return "Battles";
		}
	};

	chooser.setFileFilter(filter);
	int rv = chooser.showOpenDialog(robocodeFrame);
	if (rv == JFileChooser.APPROVE_OPTION)
	{
		manager.getBattleManager().setBattleFilename(chooser.getSelectedFile().getPath());
		manager.getBattleManager().loadBattleProperties();
		showNewBattleDialog(manager.getBattleManager().getBattleProperties());
	}

	manager.getBattleManager().resumeBattle();	
}

/**
 * Insert the method's description here.
 * Creation date: (8/22/2001 2:53:17 PM)
 */
public void showVersionsTxt() {
		String helpurl = "file:" + new File(Constants.cwd(),"").getAbsoluteFile() //System.getProperty("user.dir")
		+ System.getProperty("file.separator")
		+ "versions.txt";
		
	//BrowserControl.displayURL(helpurl);
	try {
		manager.getBrowserManager().openURL(helpurl);
	} catch (IOException e) {
			JOptionPane.showMessageDialog(robocodeFrame,
				"Sorry, I was unable to open a browser for you.  Please visit the following url: " + helpurl,
				"Unable to open browser!",
				JOptionPane.INFORMATION_MESSAGE);
	}
}

/**
 * Insert the method's description here.
 * Creation date: (8/22/2001 2:53:17 PM)
 */
public void showHelpApi() {
		String helpurl = "file:" + new File(Constants.cwd(),"").getAbsoluteFile()
		+ System.getProperty("file.separator")
		+ "javadoc" + System.getProperty("file.separator") + "index.html";
		
	try {
		manager.getBrowserManager().openURL(helpurl);
	} catch (IOException e) {
			JOptionPane.showMessageDialog(robocodeFrame,
				"Sorry, I was unable to open a browser for you.  Please visit the following url: " + helpurl,
				"Unable to open browser!",
				JOptionPane.INFORMATION_MESSAGE);
	}

}

/**
 * Insert the method's description here.
 * Creation date: (8/22/2001 2:54:09 PM)
 */
public void showFaq() {
	
	String helpurl = "http://robocode.alphaworks.ibm.com/help/robocode.faq.txt";
	try {
		manager.getBrowserManager().openURL(helpurl);
	} catch (IOException e) {
			JOptionPane.showMessageDialog(robocodeFrame,
				"Sorry, I was unable to open a browser for you.  Please visit the following url: " + helpurl,
				"Unable to open browser!",
				JOptionPane.INFORMATION_MESSAGE);
	}

}

/**
 * Insert the method's description here.
 * Creation date: (8/22/2001 2:54:09 PM)
 */
public void showOnlineHelp() {
	
//	String helpurl = "http://robocode.alphaworks.ibm.com/help/help.html?version=" + manager.getVersionManager().getVersion();
	String helpurl = "http://robocode.alphaworks.ibm.com/help/index.html";
	try {
		manager.getBrowserManager().openURL(helpurl);
	} catch (IOException e) {
			JOptionPane.showMessageDialog(robocodeFrame,
				"Sorry, I was unable to open a browser for you.  Please visit the following url: " + helpurl,
				"Unable to open browser!",
				JOptionPane.INFORMATION_MESSAGE);
	}

}

/**
 * Insert the method's description here.
 * Creation date: (8/22/2001 2:52:21 PM)
 */
public void showOptionsPreferences() {

	manager.getBattleManager().pauseBattle();
	
	// Create the preferencesDialog
	PreferencesDialog preferencesDialog = new PreferencesDialog(manager);

	// Show it
	Utils.packCenterShow(robocodeFrame,preferencesDialog);

}

/**
 * Insert the method's description here.
 * Creation date: (1/16/2001 3:25:34 PM)
 */
public void showResultsDialog(Battle battle) {
	ResultsDialog resultsDialog = new ResultsDialog(robocodeFrame,battle);
	resultsDialog.setSize(0,0);
	Utils.packCenterShow(robocodeFrame,resultsDialog);
}

/**
 * Insert the method's description here.
 * Creation date: (8/22/2001 2:50:26 PM)
 */
public void showRobocodeEditor() {
	if (robocodeEditor == null)
	{
		robocodeEditor = new robocode.editor.RobocodeEditor(manager);
		// Pack, center, and show it
		Utils.packCenterShow(robocodeEditor);
	}
	else
		robocodeEditor.setVisible(true);
}

/**
 * Insert the method's description here.
 * Creation date: (8/22/2001 2:50:26 PM)
 */
public void showRobotPackager() {
	if (robotPackager != null)
	{
		robotPackager.dispose();
		robotPackager = null;
	}
		
	robotPackager = new robocode.packager.RobotPackager(manager.getRobotRepositoryManager(),false);
	// Pack, center, and show it
	Utils.packCenterShow(robotPackager);
	return;
}

/**
 * Insert the method's description here.
 * Creation date: (8/22/2001 2:50:26 PM)
 */
public void showRobotExtractor(JFrame owner) {
	if (robotExtractor != null)
	{
		robotExtractor.dispose();
		robotExtractor = null;
		//System.gc();
	}
		
	robotExtractor = new robocode.dialog.RobotExtractor(owner,manager.getRobotRepositoryManager());
	// Pack, center, and show it
	Utils.packCenterShow(robotExtractor);
}

/*
public void showTeamPackager() {
	if (teamPackager != null)
	{
		teamPackager.dispose();
		teamPackager = null;
		System.gc();
	}
		
	teamPackager = new robocode.packager.RobotPackager(manager.getRobotRepositoryManager(),true);
	// Pack, center, and show it
	Utils.packCenterShow(teamPackager);
}
*/
/**
 * Insert the method's description here.
 * Creation date: (9/3/2001 12:52:13 PM)
 */
public void showSplashScreen() {
	//System.out.println("Creating.");
	// Create the splash screen 
	SplashScreen splashScreen = new SplashScreen(manager);

	//System.out.println("PCS");
	// Pack, center, and show it
	Utils.packCenterShow(splashScreen);

	for (int i = 0; i < 5 * 20 && !splashScreen.isPainted(); i++)
	{
		//System.out.println("waiting...");
		try {
			Thread.sleep(200);
		} catch (InterruptedException ie) {};
	}
	//System.out.println("done waiting.");

	Utils.setStatusLabel(splashScreen.getSplashLabel());
	try {
		long starttime = System.currentTimeMillis();
		manager.getRobotRepositoryManager().getRobotRepository();
		Utils.setStatus("Loading graphics...");
		manager.getImageManager().initialize(splashScreen);
		Utils.setStatus("");
		manager.getCpuManager().getCpuConstant();
		long diff = 4000 - (System.currentTimeMillis() - starttime);
		if (diff > 0)
			Thread.sleep(diff);
	} catch (InterruptedException ie) {};
	Utils.setStatusLabel(null);
	splashScreen.dispose();

}

/**
 * Insert the method's description here.
 * Creation date: (1/22/2001 1:49:03 PM)
 * @param battleProperties java.util.Properties
 */
public void showNewBattleDialog(BattleProperties battleProperties) {
	manager.getBattleManager().pauseBattle();
	
	NewBattleDialog newBattleDialog = new NewBattleDialog(manager, battleProperties);

	// Pack, center, and show it
	Utils.packCenterShow(robocodeFrame,newBattleDialog);
	return;
}

/**
 * Insert the method's description here.
 * Creation date: (8/22/2001 3:11:43 PM)
 * @return boolean
 */
public boolean closeRobocodeEditor() {
	if (robocodeEditor == null)
		return true;

	if (!robocodeEditor.isVisible())
		return true;

	return robocodeEditor.close();
	
}


/**
 * Gets the manager.
 * @return Returns a RobocodeManager
 */
public RobocodeManager getManager() {
	return manager;
}

public void showCreateTeamDialog()
{
	teamCreator = new robocode.dialog.TeamCreator(manager.getRobotRepositoryManager());
	Utils.packCenterShow(teamCreator);
}

public void showImportRobotDialog()
{
	JFileChooser chooser;

	chooser = new JFileChooser();
		
	javax.swing.filechooser.FileFilter filter = new javax.swing.filechooser.FileFilter()
	{
		public boolean accept(File pathname)
		{
			if (pathname.isHidden())
				return false;
			if (pathname.isDirectory())
				return true;
			String fn = pathname.getName();
			if (fn.equals("robocode.jar"))
				return false;
			int idx = fn.lastIndexOf('.');
			String extension = "";
			if (idx >= 0)
			  extension = fn.substring(idx);
			if (extension.equalsIgnoreCase(".jar"))
			  return true;
			if (extension.equalsIgnoreCase(".zip"))
			  return true;
			return false;
		}
		public String getDescription() {
			return "Jar Files";
		}
	};

	chooser.setFileFilter(filter);
	chooser.setDialogTitle("Select the robot .jar file to copy to " + manager.getRobotRepositoryManager().getRobotsDirectory());
	int rv = chooser.showDialog(getRobocodeFrame(),"Import");
	if (rv == JFileChooser.APPROVE_OPTION)
	{
		File inputFile = chooser.getSelectedFile();
		String fileName = chooser.getSelectedFile().getName();
		int idx = fileName.lastIndexOf('.');
		String extension = "";
		if (idx >= 0)
			extension = fileName.substring(idx);
		if (!extension.equalsIgnoreCase(".jar"))
			fileName += ".jar";
		File outputFile = new File(manager.getRobotRepositoryManager().getRobotsDirectory(),fileName);
		if (inputFile.equals(outputFile))
		{
			JOptionPane.showMessageDialog(getRobocodeFrame(),outputFile.getName() + " is already in the robots directory!");
			return;
		}
		if (outputFile.exists())
		{
			if (JOptionPane.showConfirmDialog(getRobocodeFrame(),outputFile + " already exists.  Overwrite?","Warning",JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
			{
//				JOptionPane.showMessageDialog(getRobocodeFrame(),"Import cancelled.");
				return;
			}
		}
		if (JOptionPane.showConfirmDialog(getRobocodeFrame(),"Robocode will now copy " + inputFile.getName() + " to " + outputFile.getParent(),"Import robot",JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION)
		{
			try {
				Utils.copy(inputFile,outputFile);
				manager.getRobotRepositoryManager().clearRobotList();
				JOptionPane.showMessageDialog(getRobocodeFrame(),"Robot imported successfully.");
			} catch (IOException e) {
				JOptionPane.showMessageDialog(getRobocodeFrame(),"Import failed: " + e);
			}
		}
	}

}

}

