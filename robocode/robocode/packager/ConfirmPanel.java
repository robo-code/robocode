/*******************************************************************************
 * Copyright (c) 2001-2006 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Replaced FileSpecificationVector with plain Vector
 *     - Code cleanup
 *******************************************************************************/
package robocode.packager;


import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import javax.swing.*;

import robocode.dialog.*;
import robocode.repository.*;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (current)
 */
@SuppressWarnings("serial")
public class ConfirmPanel extends WizardPanel {
	RobotPackager robotPackager = null;

	EventHandler eventHandler = new EventHandler();
	
	class EventHandler implements ComponentListener {
		public void componentMoved(ComponentEvent e) {}

		public void componentResized(ComponentEvent e) {}

		public void componentHidden(ComponentEvent e) {
			visible = false;
			fireStateChanged();
		}

		public void componentShown(ComponentEvent e) {
			// log("confirm panel shown.");
			if (robotPackager == null) {
				return;
			}

			visible = true;
			updateFields();
			fireStateChanged();
			repaint();
		}
	}

	public JPanel robotListPanel = null;

	/**
	 * PackagerOptionsPanel constructor comment.
	 */
	public ConfirmPanel(RobotPackager robotPackager) {
		super();
		this.robotPackager = robotPackager;
		initialize();
	}

	public JPanel getRobotListPanel() {
		if (robotListPanel == null) {
			robotListPanel = new JPanel();
			robotListPanel.setLayout(new BoxLayout(robotListPanel, BoxLayout.Y_AXIS));
			robotListPanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		}
		return robotListPanel;
	}

	private void initialize() {
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		addComponentListener(eventHandler);
		add(new JPanel());
	}

	public boolean isReady() {
		return visible;
	}

	public void setSelectedRobots(Vector selectedRobots) { // <FileSpecification>
		getRobotListPanel().removeAll();

		if (selectedRobots == null || selectedRobots.size() == 0) {
			getRobotListPanel().add(new JLabel("You have not yet selected any robots."));
		}
		if (selectedRobots.size() == 1) {
			String robotName = ((FileSpecification) selectedRobots.elementAt(0)).getFullClassName();

			getRobotListPanel().add(new JLabel("You have selected " + robotName + " for packaging."));
		} else {
			getRobotListPanel().add(new JLabel("You have selected the following robots for packaging:"));
			for (int i = 0; i < selectedRobots.size(); i++) {
				String robotName = ((FileSpecification) selectedRobots.elementAt(i)).getFullClassName();

				getRobotListPanel().add(new JLabel(robotName));
			}
		}
		getRobotListPanel().add(new JLabel(""));
		getRobotListPanel().setMaximumSize(new Dimension(10000, robotListPanel.getPreferredSize().height));

		validate();
	}

	private boolean visible = false;

	public void updateFields() {
		removeAll();
		setSelectedRobots(robotPackager.getRobotSelectionPanel().getSelectedRobots());
		add(getRobotListPanel());
		add(Box.createVerticalStrut(20));
		if (robotPackager.getPackagerOptionsPanel().getIncludeSource().isSelected() == true) {
			add(new JLabel("Java source files will be included."));
		} else {
			add(new JLabel("Only .class files will be included."));
		}
		add(Box.createVerticalStrut(20));
		add(new JLabel("The package will be saved in " + robotPackager.getFilenamePanel().getFilenameField().getText()));
		add(Box.createVerticalStrut(20));
		add(new JLabel("If all of the above is correct, click the Package button to start packaging."));
		add(new JPanel());

		revalidate();
	}
}
