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
 *     - Code cleanup
 *     Robert D. Maupin
 *     - Replaced old collection types like Vector and Hashtable with
 *       synchronized List and HashMap
 *******************************************************************************/
package net.sf.robocode.ui.packager;


import net.sf.robocode.repository.IRepositoryItem;
import net.sf.robocode.ui.dialog.WizardPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.List;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Robert D. Maupin (contributor)
 */
@SuppressWarnings("serial")
public class ConfirmPanel extends WizardPanel {
	private final RobotPackager robotPackager;
	private final EventHandler eventHandler = new EventHandler();
	private boolean visible;
	private JPanel robotListPanel;

	private class EventHandler implements ComponentListener {
		public void componentMoved(ComponentEvent e) {}

		public void componentResized(ComponentEvent e) {}

		public void componentHidden(ComponentEvent e) {
			visible = false;
			fireStateChanged();
		}

		public void componentShown(ComponentEvent e) {
			// logError("confirm panel shown.");
			if (robotPackager == null) {
				return;
			}

			visible = true;
			updateFields();
			fireStateChanged();
			repaint();
		}
	}

	public ConfirmPanel(RobotPackager robotPackager) {
		super();
		this.robotPackager = robotPackager;
		initialize();
	}

	public JPanel getRobotListPanel() {
		if (robotListPanel == null) {
			robotListPanel = new JPanel();
			robotListPanel.setLayout(new BoxLayout(robotListPanel, BoxLayout.Y_AXIS));
			robotListPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		}
		return robotListPanel;
	}

	private void initialize() {
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		addComponentListener(eventHandler);
		add(new JPanel());
	}

	@Override
	public boolean isReady() {
		return visible;
	}

	public void setSelectedRobots(List<IRepositoryItem> selectedRobots) {
		getRobotListPanel().removeAll();

		if (selectedRobots == null || selectedRobots.size() == 0) {
			getRobotListPanel().add(new JLabel("You have not yet selected any robots."));
		} else if (selectedRobots.size() == 1) {
			String robotName = (selectedRobots.get(0)).getFullClassName();

			getRobotListPanel().add(new JLabel("You have selected " + robotName + " for packaging."));
		} else {
			getRobotListPanel().add(new JLabel("You have selected the following robots for packaging:"));

			for (IRepositoryItem selected : selectedRobots) {
				getRobotListPanel().add(new JLabel(selected.getFullClassName()));
			}
		}
		getRobotListPanel().add(new JLabel(""));
		getRobotListPanel().setMaximumSize(new Dimension(10000, robotListPanel.getPreferredSize().height));

		validate();
	}

	public void updateFields() {
		removeAll();
		setSelectedRobots(robotPackager.getRobotSelectionPanel().getSelectedRobots());
		add(getRobotListPanel());
		add(Box.createVerticalStrut(20));
		if (robotPackager.getPackagerOptionsPanel().getIncludeSource().isSelected()) {
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
