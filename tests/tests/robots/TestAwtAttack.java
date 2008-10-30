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
package robots;


import helpers.RobotTestBed;
import helpers.Assert;
import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import robocode.battle.events.TurnEndedEvent;

import javax.swing.*;
import java.io.File;
import java.awt.*;

import sun.awt.AppContext;


/**
 * @author Pavel Savara (original)
 */
public class TestAwtAttack extends RobotTestBed {
	boolean messagedAttack;
	boolean messagedBreakthru;

	@Test
	public void run() {
		super.run();
	}

	public void onTurnEnded(TurnEndedEvent event) {
		super.onTurnEnded(event);
		final String out = event.getTurnSnapshot().getRobots().get(1).getOutputStreamSnapshot();

		if (out.contains("Hacked!!!")) {
			messagedBreakthru = true;
		}
		if (out.contains("Preventing testing.AwtAttack from access to threadgroup")) {
			messagedAttack = true;
		}
	}

	@Override
	public String getRobotNames() {
		return "testing.BattleLost,testing.AwtAttack";
	}

	JFrame frame;
	@Before
	public void setupAttack() {
		frame = new JFrame();
		frame.setVisible(true);
	}

	@After
	public void tearDownAttack() {

		Runnable doHack = new Runnable() {
			public void run() {
				System.out.println("works still!!!");
			}
		};

		javax.swing.SwingUtilities.invokeLater(doHack);

		frame.setVisible(false);
		frame.dispose();
		Assert.assertFalse(messagedBreakthru);
		Assert.assertTrue(messagedAttack);
	}
}
