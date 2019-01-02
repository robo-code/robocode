/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.ui.dialog;


import net.sf.robocode.repository.IRobotSpecItem;
import net.sf.robocode.ui.packager.RobotPackager;
import net.sf.robocode.ui.util.LimitedClassnameDocument;
import net.sf.robocode.ui.util.LimitedDocument;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.net.URL;
import java.util.List;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Robert D. Maupin (contributor)
 */
@SuppressWarnings("serial")
public class TeamCreatorOptionsPanel extends WizardPanel {
	private TeamCreator teamCreator;
	private RobotPackager teamPackager;
	private final EventHandler eventHandler = new EventHandler();

	private JLabel authorLabel;
	private JTextField authorField;
	private JLabel descriptionLabel;
	private JTextArea descriptionArea;
	private JLabel versionLabel;
	private JTextField versionField;
	private JLabel webpageLabel;
	private JTextField webpageField;
	private JLabel webpageHelpLabel;

	private JLabel teamNameLabel;
	private JLabel teamPackageLabel;
	private JTextField teamNameField;

	private String teamPackage;

	private class EventHandler implements ComponentListener, DocumentListener {
		public void insertUpdate(DocumentEvent e) {
			fireStateChanged();
		}

		public void changedUpdate(DocumentEvent e) {
			fireStateChanged();
		}

		public void removeUpdate(DocumentEvent e) {
			fireStateChanged();
		}

		public void componentMoved(ComponentEvent e) {}

		public void componentHidden(ComponentEvent e) {}

		public void componentShown(ComponentEvent e) {
			List<IRobotSpecItem> selectedRobots;

			if (teamCreator != null) {
				selectedRobots = teamCreator.getRobotSelectionPanel().getSelectedRobots();
			} else {
				selectedRobots = teamPackager.getRobotSelectionPanel().getSelectedRobots();
			}

			if (selectedRobots != null) {
				IRobotSpecItem robotSpecification = selectedRobots.get(0);

				getTeamNameLabel().setText("Please choose a name for your team: (Must be a valid Java classname)");
				getTeamNameField().setText(robotSpecification.getShortClassName() + "Team");
				getTeamPackageLabel().setText(robotSpecification.getFullPackage() + ".");
				teamPackage = robotSpecification.getFullPackage();
				if (teamPackage != null) {
					teamPackage += ".";
				}

				String d = robotSpecification.getDescription();

				if (d == null) {
					d = "";
				}
				getDescriptionArea().setText(d);
				String a = robotSpecification.getAuthorName();

				if (a == null) {
					a = "";
				}
				getAuthorField().setText(a);
				URL u = robotSpecification.getWebpage();

				if (u == null) {
					getWebpageField().setText("");
				} else {
					getWebpageField().setText(u.toString());
				}

				getVersionLabel().setVisible(true);
				getVersionField().setVisible(true);
				getAuthorLabel().setVisible(true);
				getAuthorField().setVisible(true);
				getWebpageLabel().setVisible(true);
				getWebpageField().setVisible(true);
				getWebpageHelpLabel().setVisible(true);
				getDescriptionLabel().setText(
						"Please enter a short description of this team (up to 3 lines of 72 chars each).");
			}
		}

		public void componentResized(ComponentEvent e) {}
	}

	public JPanel robotListPanel;

	public TeamCreatorOptionsPanel(TeamCreator teamCreator) {
		super();
		this.teamCreator = teamCreator;
		initialize();
	}

	public TeamCreatorOptionsPanel(RobotPackager teamPackager) {
		super();
		this.teamPackager = teamPackager;
		initialize();
	}

	private void initialize() {
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		getTeamNameLabel().setAlignmentX(Component.LEFT_ALIGNMENT);
		add(getTeamNameLabel());

		JPanel teamNamePanel = new JPanel();
		teamNamePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		teamNamePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		getTeamNameField().setAlignmentX(Component.LEFT_ALIGNMENT);
		getTeamNameField().setMaximumSize(getTeamNameField().getPreferredSize());
		// getVersionField().setMaximumSize(getVersionField().getPreferredSize());
		teamNamePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, getTeamNameField().getPreferredSize().height));
		teamNamePanel.add(getTeamPackageLabel());
		teamNamePanel.add(getTeamNameField());
		add(teamNamePanel);
		JLabel label = new JLabel(" ");
		label.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(label);

		add(getVersionLabel());

		JPanel versionPanel = new JPanel();
		versionPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		versionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		getVersionField().setAlignmentX(Component.LEFT_ALIGNMENT);
		getVersionField().setMaximumSize(getVersionField().getPreferredSize());
		versionPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, getVersionField().getPreferredSize().height));
		versionPanel.add(getVersionField());
		add(versionPanel);

		label = new JLabel(" ");
		label.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(label);

		add(getDescriptionLabel());

		JScrollPane scrollPane = new JScrollPane(getDescriptionArea(), ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

		scrollPane.setMaximumSize(scrollPane.getPreferredSize());
		scrollPane.setMinimumSize(new Dimension(100, scrollPane.getPreferredSize().height));
		scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(scrollPane);

		label = new JLabel(" ");
		label.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(label);

		add(getAuthorLabel());

		getAuthorField().setAlignmentX(Component.LEFT_ALIGNMENT);
		getAuthorField().setMaximumSize(getAuthorField().getPreferredSize());
		add(getAuthorField());

		label = new JLabel(" ");
		label.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(label);

		add(getWebpageLabel());

		getWebpageField().setAlignmentX(Component.LEFT_ALIGNMENT);
		getWebpageField().setMaximumSize(getWebpageField().getPreferredSize());
		add(getWebpageField());

		getWebpageHelpLabel().setAlignmentX(Component.LEFT_ALIGNMENT);
		add(getWebpageHelpLabel());

		JPanel panel = new JPanel();

		panel.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(panel);
		addComponentListener(eventHandler);
	}

	@Override
	public boolean isReady() {
		return getTeamNameField().getText().length() != 0 && getDescriptionArea().getText().length() != 0;
	}

	private JLabel getAuthorLabel() {
		if (authorLabel == null) {
			authorLabel = new JLabel("Please enter your name. (optional)");
			authorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		}
		return authorLabel;
	}

	public JTextField getAuthorField() {
		if (authorField == null) {
			authorField = new JTextField(40);
		}
		return authorField;
	}

	public JLabel getDescriptionLabel() {
		if (descriptionLabel == null) {
			descriptionLabel = new JLabel("");
			descriptionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		}
		return descriptionLabel;
	}

	public JTextArea getDescriptionArea() {
		if (descriptionArea == null) {
			LimitedDocument doc = new LimitedDocument(3, 72);

			descriptionArea = new JTextArea(doc, null, 3, 72);
			doc.addDocumentListener(eventHandler);
		}
		return descriptionArea;
	}

	private JLabel getVersionLabel() {
		if (versionLabel == null) {
			versionLabel = new JLabel(
					"Please enter a version number for this robot (up to 10 word chars: letters, digits, dots, but no spaces).");
			versionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		}
		return versionLabel;
	}

	public JTextField getVersionField() {
		if (versionField == null) {
			LimitedDocument doc = new LimitedDocument(1, 10);

			versionField = new JTextField(doc, null, 10);
			doc.addDocumentListener(eventHandler);
		}
		return versionField;
	}

	public JLabel getWebpageLabel() {
		if (webpageLabel == null) {
			webpageLabel = new JLabel("Please enter a URL for your team's webpage (optional)");
			webpageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		}
		return webpageLabel;
	}

	public JTextField getWebpageField() {
		if (webpageField == null) {
			webpageField = new JTextField(40);
		}
		return webpageField;
	}

	public JLabel getWebpageHelpLabel() {
		if (webpageHelpLabel == null) {
			webpageHelpLabel = new JLabel("");
		}
		return webpageHelpLabel;
	}

	public JTextField getTeamNameField() {
		if (teamNameField == null) {
			LimitedDocument doc = new LimitedClassnameDocument(1, 32);

			teamNameField = new JTextField(doc, null, 32);
			doc.addDocumentListener(eventHandler);
		}
		return teamNameField;
	}

	public JLabel getTeamNameLabel() {
		if (teamNameLabel == null) {
			teamNameLabel = new JLabel("");
		}
		return teamNameLabel;
	}

	public JLabel getTeamPackageLabel() {
		if (teamPackageLabel == null) {
			teamPackageLabel = new JLabel("");
		}
		return teamPackageLabel;
	}

	/**
	 * Gets the teamPackage.
	 *
	 * @return Returns a String
	 */
	public String getTeamPackage() {
		return (teamPackage != null) ? teamPackage : ".";
	}
}
