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

import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

import robocode.peer.robot.RobotClassManager;
import robocode.repository.*;
import robocode.util.*;
import robocode.manager.*;

/**
 * Insert the type's description here.
 * Creation date: (10/11/2001 2:12:31 PM)
 * @author: Administrator
 */
public class RobotExtractor extends JDialog implements WizardListener{
	String unusedrobotPath = null;

	private int minRobots = 1;
	private int maxRobots = 1; //250;

	private JPanel robotImporterContentPane = null;

	private WizardCardPanel wizardPanel = null;
	private WizardController buttonsPanel = null;
	private RobotSelectionPanel robotSelectionPanel = null;
	
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
public RobotExtractor(JFrame owner, RobotRepositoryManager robotManager)
{
	super(owner);
	this.robotManager = robotManager;
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
 * Creation date: (10/18/2001 2:04:48 PM)
 */
public void finishButtonActionPerformed() {
	String resultsString;
	
	int rc = extractRobot();
	ConsoleDialog d;
	d = new ConsoleDialog(robotManager.getManager().getWindowManager().getRobocodeFrame(),"Extract results",false);
	d.setText(output.toString());
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
private javax.swing.JPanel getRobotImporterContentPane() {
	if (robotImporterContentPane == null) {
		try {
			robotImporterContentPane = new javax.swing.JPanel();
			robotImporterContentPane.setName("robotExtractorContentPane");
			robotImporterContentPane.setLayout(new java.awt.BorderLayout());
			robotImporterContentPane.add(getButtonsPanel(),java.awt.BorderLayout.SOUTH);
			robotImporterContentPane.add(getWizardPanel(),java.awt.BorderLayout.CENTER);
			getWizardPanel().getWizardController().setFinishButtonText("Extract!");
			robotImporterContentPane.registerKeyboardAction(eventHandler,"Refresh",KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0),JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
			robotImporterContentPane.registerKeyboardAction(eventHandler,"Refresh",KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0),JComponent.WHEN_FOCUSED);
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return robotImporterContentPane;
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
					"Select the robot you would like to extract to the robots directory.  Robots not shown do not include source.", true, true, true, false, true
					, true, null);
			//else
			//	robotSelectionPanel = new RobotSelectionPanel(robotManager, minRobots, maxRobots, false,
			//		"Select the robots for the team you would like to package.", /*true*/false, true, true, false, true, null);
			robotSelectionPanel.setName("Robots available for extract");
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
public void initialize() {
	try {
		setName("Robot Extractor");
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Robot Extract");
		setContentPane(getRobotImporterContentPane());
	} catch (java.lang.Throwable e) {
		log(e);
	}
}
private void log(String s) {
	Utils.log(s);
}
private void log(Throwable e) {
	Utils.log(e);
}

private int extractRobot()
{
	robotManager.clearRobotList();
	int rv = 0;
	output = new StringWriter();
	PrintWriter out = new PrintWriter(output);

	out.println("Robot Extract");	
	FileSpecificationVector selectedRobots = getRobotSelectionPanel().getSelectedRobots();
	FileSpecification spec = selectedRobots.elementAt(0);
	try {
		Utils.setStatusWriter(out);
		rv = robotManager.extractJar(spec.getJarFile(),robotManager.getRobotsDirectory(), "Extracting to " + robotManager.getRobotsDirectory(), false, true, false);
		Utils.setStatusWriter(null);
		Utils.setStatus("");
		if (rv == 0)
		{
			out.println("Robot extracted successfully.");
		}
		else if (rv == -1)
			out.println("Cancelled.");
	} catch (Exception e) {
		out.println(e);
		rv = 8;
	}
	return rv;
	
}
}
