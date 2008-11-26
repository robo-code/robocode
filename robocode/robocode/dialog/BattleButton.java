/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package robocode.dialog;


import robocode.manager.IRobotDialogManager;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;


/**
 * @author Pavel Savara (original)
 */
public class BattleButton extends JButton implements ActionListener {
	private static final long serialVersionUID = 1L;

	private final IRobotDialogManager robotDialogManager;
	private BattleDialog battleDialog;

	public BattleButton(IRobotDialogManager robotDialogManager, boolean attach) {
		this.robotDialogManager = robotDialogManager;

		initialize();
		if (attach) {
			attach();
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (battleDialog == null) {
			attach();
			if (!battleDialog.isVisible() || battleDialog.getState() != Frame.NORMAL) {
				WindowUtil.packPlaceShow(battleDialog);
			}
		} else {
			battleDialog.setVisible(true);
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
		setHorizontalAlignment(SwingConstants.CENTER);
		setMargin(new Insets(0, 0, 0, 0));
		setText("Main battle log");
		setToolTipText("Main battle log");
	}

	public void attach() {
		if (battleDialog == null) {
			battleDialog = robotDialogManager.getBattleDialog(this, true);
		}
		battleDialog.attach();
	}

	public void detach() {
		battleDialog = null;
	}
}
