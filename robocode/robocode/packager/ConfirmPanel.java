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
import java.awt.*;
import java.awt.event.*;

import robocode.dialog.*;
import robocode.repository.*;


/**
 * Insert the type's description here.
 * Creation date: (10/19/2001 12:07:51 PM)
 * @author: Administrator
 */
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
		//log("confirm panel shown.");
		if (robotPackager == null)
			return;

		visible = true;
		updateFields();
		fireStateChanged();
		repaint();
	}
}
	public javax.swing.JPanel robotListPanel = null;
/**
 * PackagerOptionsPanel constructor comment.
 */
public ConfirmPanel(RobotPackager robotPackager) {
	super();
	this.robotPackager = robotPackager;
	initialize();
}

/**
 * Insert the method's description here.
 * Creation date: (10/19/2001 1:31:41 PM)
 * @return javax.swing.JLabel
 */
public javax.swing.JPanel getRobotListPanel() {
	if (robotListPanel == null)
	{
		robotListPanel = new JPanel();
		robotListPanel.setLayout(new BoxLayout(robotListPanel,BoxLayout.Y_AXIS));
		//robotListPanel.add(new JLabel("You have selected man.SpinBot for packagin."));
		robotListPanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		//robotListPanel.setMaximumSize(new Dimension(10000,robotListPanel.getPreferredSize().height));
	}
	return robotListPanel;
}
/**
 * Insert the method's description here.
 * Creation date: (10/19/2001 12:09:49 PM)
 */
private void initialize() {
	setName("confirmPanel");
	setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
	setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
	addComponentListener(eventHandler);
	add(new JPanel());
}
/**
 * Insert the method's description here.
 * Creation date: (10/20/2001 11:24:52 AM)
 */
public boolean isReady() {
	return visible;
}

/**
 * Insert the method's description here.
 * Creation date: (10/22/2001 2:24:23 PM)
 * @param selectedRobots java.util.Vector
 */
public void setSelectedRobots(FileSpecificationVector selectedRobots) {
	getRobotListPanel().removeAll();
	
//			String prefix =	robocode.getRobotPath() + File.separator + "outgoing" + File.separator;

	if (selectedRobots == null || selectedRobots.size() == 0)
	{
		getRobotListPanel().add(new JLabel("You have not yet selected any robots."));
	}
	if (selectedRobots.size() == 1)
	{
		String robotName = ((FileSpecification)selectedRobots.elementAt(0)).getFullClassName();
		getRobotListPanel().add(new JLabel("You have selected " + robotName + " for packaging."));
	}
	else
	{
		getRobotListPanel().add(new JLabel("You have selected the following robots for packaging:"));
		for (int i = 0; i < selectedRobots.size(); i++)
		{
			String robotName = ((FileSpecification)selectedRobots.elementAt(i)).getFullClassName();
			getRobotListPanel().add(new JLabel(robotName));
		}
	}
	getRobotListPanel().add(new JLabel(""));
	getRobotListPanel().setMaximumSize(new Dimension(10000,robotListPanel.getPreferredSize().height));
//	getRobotListPanel().setMaximumSize(robotListPanel.getPreferredSize());
	validate();
}

	private boolean visible = false;

/**
 * Insert the method's description here.
 * Creation date: (10/22/2001 5:29:48 PM)
 */
public void updateFields() {
	removeAll();
	setSelectedRobots(robotPackager.getRobotSelectionPanel().getSelectedRobots());
	add(getRobotListPanel());
	add(Box.createVerticalStrut(20));
	if (robotPackager.getPackagerOptionsPanel().getIncludeSource().isSelected() == true)
		add(new JLabel("Java source files will be included."));
	else
		add(new JLabel("Only .class files will be included."));
	add(Box.createVerticalStrut(20));
	add(new JLabel("The package will be saved in " + robotPackager.getFilenamePanel().getFilenameField().getText()));
	add(Box.createVerticalStrut(20));
	add(new JLabel("If all of the above is correct, click the Package button to start packaging."));
	add(new JPanel());
	revalidate();
}
}