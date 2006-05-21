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
package robocode.dialog;


import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.net.*;

import robocode.util.Utils;
import robocode.manager.*;
import robocode.repository.*;


public class TeamCreator extends JDialog implements WizardListener {

	private JPanel teamCreatorContentPane = null;

	private WizardCardPanel wizardPanel = null;
	private WizardController wizardController = null;
	
	// private FilenamePanel filenamePanel = null;
	// private ConfirmPanel confirmPanel = null;
	
	private RobotSelectionPanel robotSelectionPanel = null;
	private TeamCreatorOptionsPanel teamCreatorOptionsPanel = null;

	private int minRobots = 2;
	private int maxRobots = 10;
	
	private RobotRepositoryManager robotRepositoryManager = null;
	private RobocodeManager manager = null;
	
	private EventHandler eventHandler = new EventHandler();
	class EventHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("Refresh")) {
				getRobotSelectionPanel().refreshRobotList();
			}
		}
		;
	}
	
	public TeamCreator(RobotRepositoryManager robotRepositoryManager) {
		super(robotRepositoryManager.getManager().getWindowManager().getRobocodeFrame());
		this.robotRepositoryManager = robotRepositoryManager;
		this.manager = robotRepositoryManager.getManager();
		initialize();
	}

	protected TeamCreatorOptionsPanel getTeamCreatorOptionsPanel() {
		if (teamCreatorOptionsPanel == null) {
			try {
				teamCreatorOptionsPanel = new TeamCreatorOptionsPanel(this);
			} catch (java.lang.Throwable e) {
				log(e);
			}
		}
		return teamCreatorOptionsPanel;
	}

	private javax.swing.JPanel getTeamCreatorContentPane() {
		if (teamCreatorContentPane == null) {
			try {
				teamCreatorContentPane = new javax.swing.JPanel();
				teamCreatorContentPane.setName("robotPackagerContentPane");
				teamCreatorContentPane.setLayout(new java.awt.BorderLayout());
				teamCreatorContentPane.add(getWizardController(), java.awt.BorderLayout.SOUTH);
				teamCreatorContentPane.add(getWizardPanel(), java.awt.BorderLayout.CENTER);
				getWizardPanel().getWizardController().setFinishButtonText("Create Team!");
				teamCreatorContentPane.registerKeyboardAction(eventHandler, "Refresh",
						KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
				teamCreatorContentPane.registerKeyboardAction(eventHandler, "Refresh",
						KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), JComponent.WHEN_FOCUSED);
			} catch (java.lang.Throwable e) {
				log(e);
			}
		}
		return teamCreatorContentPane;
	}

	/**
	 * Return the Page property value.
	 * @return javax.swing.JPanel
	 */
	protected RobotSelectionPanel getRobotSelectionPanel() {
		if (robotSelectionPanel == null) {
			try {
				robotSelectionPanel = new RobotSelectionPanel(robotRepositoryManager, minRobots, maxRobots, false,
						"Select the robots for this team.", false, true, true, false, false, false, null);
				robotSelectionPanel.setName("Robots available for team");
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
				wizardPanel.add(getRobotSelectionPanel(), "Select robots");
				wizardPanel.add(getTeamCreatorOptionsPanel(), "Select options");
				// wizardPanel.add(getFilenamePanel(),"Select filename");
				// wizardPanel.add(getConfirmPanel(),"Confirm");
				// getWizardPanelLayout().addLayoutComponent(getRobotSelectionPanel(),"Select robot");
				// getWizardPanelLayout().addLayoutComponent(getPackagerOptionsPanel(),"Select options");
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
			setName("Team Creator");
			setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			// setSize(550, 380);
			setTitle("Create a team");
			setContentPane(getTeamCreatorContentPane());
			// addComponentListener(eventHandler);
			// addWindowListener(eventHandler);
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

	private WizardController getWizardController() {
		if (wizardController == null) {
			try {
				wizardController = getWizardPanel().getWizardController();
				wizardController.setName("wizardController");
			} catch (java.lang.Throwable e) {
				log(e);
			}
		}
		return wizardController;
	}

	public void cancelButtonActionPerformed() {
		java.awt.AWTEvent evt = new java.awt.event.WindowEvent(this, java.awt.event.WindowEvent.WINDOW_CLOSING);

		this.dispatchEvent(evt);
		return;
	}
	
	public void finishButtonActionPerformed() {
		try {
			int rc = createTeam();

			if (rc == 0) {
				JOptionPane.showMessageDialog(this, "Team created successfully.", "Success",
						JOptionPane.INFORMATION_MESSAGE, null);
				this.dispose();
			} else {
				JOptionPane.showMessageDialog(this, "Team creation cancelled", "Cancelled",
						JOptionPane.INFORMATION_MESSAGE, null);
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, e.toString(), "Team Creation Failed", JOptionPane.ERROR_MESSAGE, null);
		}
	}
	
	public int createTeam() throws IOException {
		File f = new File(robotRepositoryManager.getRobotsDirectory(),
				teamCreatorOptionsPanel.getTeamPackage().replace('.', File.separatorChar)
				+ teamCreatorOptionsPanel.getTeamNameField().getText() + ".team");

		if (f.exists()) {
			int ok = JOptionPane.showConfirmDialog(this, f + " already exists.  Are you sure you want to replace it?",
					"Warning", JOptionPane.YES_NO_CANCEL_OPTION);

			if (ok == JOptionPane.NO_OPTION) {
				return -1;
			}
			if (ok == JOptionPane.CANCEL_OPTION) {
				return -1;
			}
		}
		if (!f.getParentFile().exists()) {
			f.getParentFile().mkdirs();
		}

		TeamSpecification teamSpec = new TeamSpecification();
		URL u = null;
		String w = teamCreatorOptionsPanel.getWebpageField().getText();

		if (w == null || w.equals("")) {
			u = null;
		} else {
			try {
				u = new URL(w);
			} catch (MalformedURLException e) {
				try {
					u = new URL("http://" + w);
					teamCreatorOptionsPanel.getWebpageField().setText(u.toString());
				} catch (MalformedURLException e2) {
					u = null;
				}
			}
		}
		teamSpec.setTeamWebpage(u);
		teamSpec.setTeamDescription(teamCreatorOptionsPanel.getDescriptionArea().getText());
		teamSpec.setTeamAuthorName(teamCreatorOptionsPanel.getAuthorField().getText());
		teamSpec.setMembers(robotSelectionPanel.getSelectedRobotsAsString());
		teamSpec.setRobocodeVersion(manager.getVersionManager().getVersion());

		// authorEmail = props.getProperty(TEAM_AUTHOR_EMAIL);
		// authorWebsite = props.getProperty(TEAM_AUTHOR_WEBSITE);

		FileOutputStream out = new FileOutputStream(f);

		teamSpec.store(out, "Robocode robot team");
		out.close();
		
		robotRepositoryManager.clearRobotList();

		return 0;
	}
}

