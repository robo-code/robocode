/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.ui.dialog;


import net.sf.robocode.io.Logger;
import net.sf.robocode.repository.IRepositoryManager;
import net.sf.robocode.repository.TeamProperties;
import net.sf.robocode.ui.IWindowManager;
import static net.sf.robocode.ui.util.ShortcutUtil.MENU_SHORTCUT_KEY_MASK;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * @author Mathew A. Nelson (original)
 * @author Matthew Reeder (contributor)
 * @author Flemming N. Larsen (contributor)
 */
@SuppressWarnings("serial")
public class TeamCreator extends JDialog implements WizardListener {

	private JPanel teamCreatorContentPane;

	private WizardCardPanel wizardPanel;
	private WizardController wizardController;

	private RobotSelectionPanel robotSelectionPanel;
	private TeamCreatorOptionsPanel teamCreatorOptionsPanel;

	private final int minRobots = 2;
	private final int maxRobots = 10;

	private final EventHandler eventHandler = new EventHandler();

	class EventHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("Refresh")) {
				getRobotSelectionPanel().refreshRobotList(true);
			}
		}
	}

	private final IRepositoryManager repositoryManager;

	public TeamCreator(IWindowManager windowManager, IRepositoryManager repositoryManager) {
		super(windowManager.getRobocodeFrame());
		this.repositoryManager = repositoryManager;
		initialize();
	}

	protected TeamCreatorOptionsPanel getTeamCreatorOptionsPanel() {
		if (teamCreatorOptionsPanel == null) {
			teamCreatorOptionsPanel = new TeamCreatorOptionsPanel(this);
		}
		return teamCreatorOptionsPanel;
	}

	private JPanel getTeamCreatorContentPane() {
		if (teamCreatorContentPane == null) {
			teamCreatorContentPane = new JPanel();
			teamCreatorContentPane.setLayout(new BorderLayout());
			teamCreatorContentPane.add(getWizardController(), BorderLayout.SOUTH);
			teamCreatorContentPane.add(getWizardPanel(), BorderLayout.CENTER);
			getWizardPanel().getWizardController().setFinishButtonTextAndMnemonic("Create Team!", 'C', 0);
			teamCreatorContentPane.registerKeyboardAction(eventHandler, "Refresh",
					KeyStroke.getKeyStroke(KeyEvent.VK_R, MENU_SHORTCUT_KEY_MASK),
					JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
			teamCreatorContentPane.registerKeyboardAction(eventHandler, "Refresh",
					KeyStroke.getKeyStroke(KeyEvent.VK_R, MENU_SHORTCUT_KEY_MASK), JComponent.WHEN_FOCUSED);
		}
		return teamCreatorContentPane;
	}

	protected RobotSelectionPanel getRobotSelectionPanel() {
		if (robotSelectionPanel == null) {
			robotSelectionPanel = net.sf.robocode.core.Container.createComponent(RobotSelectionPanel.class);
			robotSelectionPanel.setup(minRobots, maxRobots, false, "Select the robots for this team.", false, true, true,
					false, false, false, null);
		}
		return robotSelectionPanel;
	}

	private WizardCardPanel getWizardPanel() {
		if (wizardPanel == null) {
			wizardPanel = new WizardCardPanel(this);
			wizardPanel.add(getRobotSelectionPanel(), "Select robots");
			wizardPanel.add(getTeamCreatorOptionsPanel(), "Select options");
		}
		return wizardPanel;
	}

	public void initialize() {
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Create a team");
		setContentPane(getTeamCreatorContentPane());
	}

	private WizardController getWizardController() {
		if (wizardController == null) {
			wizardController = getWizardPanel().getWizardController();
		}
		return wizardController;
	}

	public void cancelButtonActionPerformed() {
		dispose();
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
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, e.toString(), "Team Creation Failed", JOptionPane.ERROR_MESSAGE, null);
		}
	}

	public int createTeam() throws IOException {
		File file = new File(repositoryManager.getRobotsDirectory(),
				teamCreatorOptionsPanel.getTeamPackage().replace('.', File.separatorChar)
				+ teamCreatorOptionsPanel.getTeamNameField().getText() + ".team");

		if (file.exists()) {
			int ok = JOptionPane.showConfirmDialog(this, file + " already exists.  Are you sure you want to replace it?",
					"Warning", JOptionPane.YES_NO_CANCEL_OPTION);

			if (ok == JOptionPane.NO_OPTION || ok == JOptionPane.CANCEL_OPTION) {
				return -1;
			}
		}
		if (!file.getParentFile().exists()) {
			if (!file.getParentFile().mkdirs()) {
				Logger.logError("Can't create " + file.getParentFile().toString());
			}
		}

		URL webPageUrl = null;
		String webPageFieldString = teamCreatorOptionsPanel.getWebpageField().getText();

		if (webPageFieldString != null && webPageFieldString.length() > 0) {
			try {
				webPageUrl = new URL(webPageFieldString);
			} catch (MalformedURLException e) {
				try {
					webPageUrl = new URL("http://" + webPageFieldString);
					teamCreatorOptionsPanel.getWebpageField().setText(webPageUrl.toString());
				} catch (MalformedURLException ignored) {}
			}
		}

		String members = robotSelectionPanel.getSelectedRobotsAsString();
		String version = teamCreatorOptionsPanel.getVersionField().getText();
		String author = teamCreatorOptionsPanel.getAuthorField().getText();
		String desc = teamCreatorOptionsPanel.getDescriptionArea().getText();
		
		TeamProperties props = new TeamProperties();
		props.setMembers(members);
		props.setVersion(version);
		props.setAuthor(author);
		props.setDescription(desc);
		props.setWebPage(webPageUrl);

		repositoryManager.createTeam(file, props);
		return 0;
	}
}
