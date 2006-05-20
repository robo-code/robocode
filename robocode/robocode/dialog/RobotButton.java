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


import java.awt.Dimension;
import java.awt.Insets;
import robocode.peer.*;
import robocode.util.*;
import robocode.manager.*;


/**
 * Insert the type's description here.
 * Creation date: (1/23/2001 11:04:48 AM)
 * @author: Mathew A. Nelson
 */
public class RobotButton extends javax.swing.JButton implements java.awt.event.ActionListener {
	private RobotPeer robotPeer = null;
	private RobotDialog robotDialog = null;
	private RobotDialogManager robotDialogManager = null;

	/**
	 * RobotButton constructor
	 */
	public RobotButton(RobotDialogManager robotDialogManager, RobotPeer robotPeer) {
		this.robotPeer = robotPeer;
		this.robotDialogManager = robotDialogManager;
		initialize();
		robotDialog = robotDialogManager.getRobotDialog(robotPeer.getName(), false);
		if (robotDialog != null) {
			robotDialog.setRobotPeer(robotPeer);
		}

	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/23/2001 2:51:39 PM)
	 * @param e java.awt.event.ActionEvent
	 */
	public void actionPerformed(java.awt.event.ActionEvent e) {
		if (robotDialog == null) {
			robotDialog = robotDialogManager.getRobotDialog(robotPeer.getName(), true);
			robotDialog.setTitle(robotPeer.getName());
			// getRobotDialog().setIconImage(java.awt.Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/icons/icon.jpg")));
			// robotDialog.setIconImage(robotPeer.getRobotGraphics().iconRobotImage);
			robotDialog.setRobotPeer(robotPeer);
			if (robotDialog.isVisible() && robotDialog.getState() == javax.swing.JFrame.NORMAL) {
				;
			} else {
				Utils.packPlaceShow(robotDialog);
			}
			// robotDialog.pack();
			// robotDialog.setVisible(true);
			// java.awt.Insets insets = getRobotDialog().getInsets();
			// getRobotDialog().setSize(robotDialog.getWidth() + insets.left + insets.right, robotDialog.getHeight() + insets.top + insets.bottom);
			// getRobotDialog().validate();
		} else {
			robotDialog.setVisible(true);
		}
		
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/24/2001 1:45:03 PM)
	 */
	public void cleanup() {/*
		 if (robotDialog != null)
		 {
		 new Thread(new Runnable() {
		 public void run() {
		 RobotDialog d = robotDialog;
		 robotDialog = null;
		 d.dispose();
		 };
		 }).start();
		 }
		 */}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/24/2001 12:32:40 PM)
	 * @return robocode.dialog.RobotDialog
	 */
	public RobotDialog getRobotDialog() {
		return robotDialog;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/23/2001 11:07:58 AM)
	 * @return robocode.JSafeRobot
	 */
	public RobotPeer getRobotPeer() {
		return robotPeer;
	}

	/**
	 * Initialize the class.
	 */
	private void initialize() {
		try {
			setName(robotPeer.getShortName());
			setText(robotPeer.getShortName());
			setToolTipText(robotPeer.getRobotClassManager().getClassNameManager().getUniqueFullClassNameWithVersion());
			addActionListener(this);
			setPreferredSize(new Dimension(110, 25));
			setMinimumSize(new Dimension(110, 25));
			setMaximumSize(new Dimension(110, 25));
			setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			setMargin(new Insets(0, 0, 0, 0));
		
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/22/2001 1:41:21 PM)
	 * @param e java.lang.Exception
	 */
	public void log(Throwable e) {
		Utils.log(e);
	}
}
