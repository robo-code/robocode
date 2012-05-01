/*******************************************************************************
 * Copyright (c) 2001, 2009 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Flemming N. Larsen
 *     - Initial implementation
 *******************************************************************************/
package net.sf.robocode.test.robots;


import net.sf.robocode.io.Logger;
import net.sf.robocode.test.helpers.Assert;
import net.sf.robocode.test.helpers.RobotTestBed;

import javax.swing.*;


/**
 * @author Flemming N. Larsen (original)
 */
public class TestConstructorAwtAttack extends RobotTestBed {
	// in case: boolean messagedAttack;
	boolean messagedBreakthru;

	JFrame frame;

	@Override
	public String getRobotNames() {
		return "tested.robots.ConstructorAwtAttack,tested.robots.BattleLost";
	}

	/* in case that we don't block JFrame by classloader
	 @Override
	 public void onTurnEnded(TurnEndedEvent event) {
	 super.onTurnEnded(event);
	 final String out = event.getTurnSnapshot().getRobots()[0].getOutputStreamSnapshot();

	 if (out.contains("Hacked!!!")) {
	 messagedBreakthru = true;
	 }
	 if (out.contains("Robots are not allowed to reference javax.swing package")) {
	 messagedAttack = true;
	 }
	 } */

	@Override
	protected void runSetup() {
		frame = new JFrame();
		frame.setVisible(true);
	}

	@Override
	protected int getExpectedErrors() {
		return 2;
	}

	@Override
	public int getExpectedRobotCount(String list) {
		return 1;
	}

	@Override
	protected void runTeardown() {
		Runnable doCheck = new Runnable() {
			public void run() {
				Logger.logMessage("works still!!!");
			}
		};

		javax.swing.SwingUtilities.invokeLater(doCheck);

		frame.setVisible(false);
		Assert.assertFalse(messagedBreakthru);
		// in case: Assert.assertTrue(messagedAttack);
	}
}
