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
 *******************************************************************************/
package robocode.dialog;


import robocode.manager.IBattleManager;
import robocode.manager.IRobotDialogManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
@SuppressWarnings("serial")
public class RobotButton extends JButton implements ActionListener {

	private final IRobotDialogManager robotDialogManager;
	private RobotDialog robotDialog;
	private final String name;
	private final int index;

	/**
	 * RobotButton constructor
	 */
	public RobotButton(IRobotDialogManager robotDialogManager, IBattleManager battleControl, String name, int index, boolean attach) {
		this.robotDialogManager = robotDialogManager;
		this.name = name;
		this.index = index;

		initialize();
		if (attach) {
			attach();
			robotDialog.reset();
			battleControl.setPaintEnabled(index, robotDialog.isPaintEnabled());
			battleControl.setSGPaintEnabled(index, robotDialog.isSGPaintEnabled());
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (robotDialog == null) {
			attach();
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
		addActionListener(this);
		setPreferredSize(new Dimension(110, 25));
		setMinimumSize(new Dimension(110, 25));
		setMaximumSize(new Dimension(110, 25));
		setHorizontalAlignment(SwingConstants.LEFT);
		setMargin(new Insets(0, 0, 0, 0));
		setToolTipText(name);
	}

	public void attach() {
		if (robotDialog == null) {
			robotDialog = robotDialogManager.getRobotDialog(this, name, true);
		}
		robotDialog.attach();
	}

	public void detach() {
		robotDialog = null;
	}

	public int getRobotIndex() {
		return index;
	}

	public String getRobotName() {
		return name;
	}
}
