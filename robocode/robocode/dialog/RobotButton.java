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
 *     - Code cleanup
 *     - Added setPaintEnabled() and setSGPaintEnabled() in constructor
 *     - Removed cleanup(), getRobotDialog(), and getRobotPeer() methods, which
 *       did nothing or was not being used
 *     - Updated to use methods from the WindowUtil, which replaces window methods
 *       that have been (re)moved from the robocode.util.Utils class
 *     Pavel Savara
 *     - Refactored use of RobotPeer into IDisplayRobotProxy
 *******************************************************************************/
package robocode.dialog;


import robocode.manager.RobotDialogManager;
import robocode.peer.proxies.IDisplayRobotProxy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Pavel Savara (contributor)
 */
@SuppressWarnings("serial")
public class RobotButton extends JButton implements ActionListener {
	private IDisplayRobotProxy robotView;
	private RobotDialog robotDialog;
	private RobotDialogManager robotDialogManager;
	private String robotName;

	/**
	 * RobotButton constructor
	 */
	public RobotButton(RobotDialogManager robotDialogManager, IDisplayRobotProxy robotView) {
		this.robotView = robotView;
		this.robotDialogManager = robotDialogManager;
		robotName = robotView.getName();

		initialize();
		robotDialog = robotDialogManager.getRobotDialog(robotName, false);
		if (robotDialog != null) {
			robotDialog.setRobotPeer(robotView);
			robotView.displaySetPaintEnabled(robotDialog.isPaintEnabled());
			robotView.displaySetSGPaintEnabled(robotDialog.isSGPaintEnabled());
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (robotDialog == null) {
			robotDialog = robotDialogManager.getRobotDialog(robotName, true);
			robotDialog.setTitle(robotName);
			robotDialog.setRobotPeer(robotView);
			if (!robotDialog.isVisible() || robotDialog.getState() != Frame.NORMAL) {
				WindowUtil.packPlaceShow(robotDialog);
			}
		} else {
			robotDialog.setVisible(true);
		}
	}

	/**
	 * Initialize the class.
	 */
	private void initialize() {
		setText(robotView.getShortName());
		setToolTipText(robotView.getUniqueFullClassNameWithVersion());
		addActionListener(this);
		setPreferredSize(new Dimension(110, 25));
		setMinimumSize(new Dimension(110, 25));
		setMaximumSize(new Dimension(110, 25));
		setHorizontalAlignment(SwingConstants.LEFT);
		setMargin(new Insets(0, 0, 0, 0));
	}
}
