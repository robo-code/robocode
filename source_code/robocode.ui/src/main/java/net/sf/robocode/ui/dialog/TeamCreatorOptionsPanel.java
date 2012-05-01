/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Replaced FileSpecificationVector with plain Vector
 *     - LimitedClassnameDocument and LimitedDocument was moved to the
 *       robocode.text package
 *     - Code cleanup
 *     Robert D. Maupin
 *     - Replaced old collection types like Vector and Hashtable with
 *       synchronized List and HashMap
 *******************************************************************************/
package net.sf.robocode.ui.dialog;


import net.sf.robocode.repository.IRepositoryItem;
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
	TeamCreator teamCreator;
	net.sf.robocode.ui.packager.RobotPackager teamPackager;
	final EventHandler eventHandler = new EventHandler();

	JLabel authorLabel;
	JTextField authorField;
	JLabel descriptionLabel;
	JTextArea descriptionArea;
	JLabel webpageLabel;
	JTextField webpageField;
	JLabel webpageHelpLabel;

	JLabel teamNameLabel;
	JLabel teamPackageLabel;
	JTextField teamNameField;

	private String teamPackage;

	class EventHandler implements ComponentListener, DocumentListener {
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
			List<IRepositoryItem> selectedRobots;

			if (teamCreator != null) {
				selectedRobots = teamCreator.getRobotSelectionPanel().getSelectedRobots();
			} else {
				selectedRobots = teamPackager.getRobotSelectionPanel().getSelectedRobots();
			}

			if (selectedRobots != null) {
				IRepositoryItem robotSpecification = selectedRobots.get(0);

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

	public TeamCreatorOptionsPanel(net.sf.robocode.ui.packager.RobotPackager teamPackager) {
		super();
		this.teamPackager = teamPackager;
		initialize();
	}

	private void initialize() {
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		getTeamNameLabel().setAlignmentX(Component.LEFT_ALIGNMENT);
		add(getTeamNameLabel());
		JPanel p = new JPanel();

		p.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		p.setAlignmentX(Component.LEFT_ALIGNMENT);
		getTeamNameField().setAlignmentX(Component.LEFT_ALIGNMENT);
		getTeamNameField().setMaximumSize(getTeamNameField().getPreferredSize());
		// getVersionField().setMaximumSize(getVersionField().getPreferredSize());
		p.setMaximumSize(new Dimension(Integer.MAX_VALUE, getTeamNameField().getPreferredSize().height));
		p.add(getTeamPackageLabel());
		p.add(getTeamNameField());
		add(p);

		JLabel label = new JLabel(" ");

		label.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(label);
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
			// descriptionArea.setMaximumSize(descriptionArea.getPreferredScrollableViewportSize());
			// descriptionArea.setLineWrap(true);
			// descriptionArea.setWrapStyleWord(true);
		}
		return descriptionArea;
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
