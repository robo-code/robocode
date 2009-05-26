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


import net.sf.robocode.test.helpers.Assert;
import net.sf.robocode.test.helpers.RobotTestBed;

import robocode.control.events.TurnEndedEvent;


/**
 * @author Flemming N. Larsen (original)
 */
public class TestGunTurnRate extends RobotTestBed {

	StringBuffer buf = new StringBuffer();
	
	@Override
	public String getRobotNames() {
		return "tested.robots.GunTurnRate,sample.Target";        
	}

	@Override
	public String getInitialPositions() {
		return "(50,50,0), (150,50,0)"; // Make sure the robots do not collide!
	}

	@Override
	public void onTurnEnded(TurnEndedEvent event) {
		super.onTurnEnded(event);

		buf.append(event.getTurnSnapshot().getRobots()[0].getOutputStreamSnapshot());
		
		if (event.getTurnSnapshot().getTurn() == 16) {
			final String out = buf.toString();

			Assert.assertTrue(out.contains("1: 0.0, -20.0") | out.contains("1: 0.0, -19.9999999999"));
			Assert.assertTrue(out.contains("2: 0.0, -20.0") | out.contains("2: 0.0, -19.9999999999"));
			Assert.assertTrue(out.contains("3: 0.0, 20.0") | out.contains("3: 0.0, 19.9999999999"));
			Assert.assertTrue(out.contains("4: 0.0, 20.0") | out.contains("4: 0.0, 19.9999999999"));
			Assert.assertTrue(out.contains("5: 0.0, -25.0") | out.contains("5: 0.0, -24.9999999999"));
			Assert.assertTrue(out.contains("6: 0.0, -25.0") | out.contains("6: 0.0, -24.9999999999"));
			Assert.assertTrue(out.contains("7: 0.0, 25.0") | out.contains("7: 0.0, 24.9999999999"));
			Assert.assertTrue(out.contains("8: 0.0, 25.0") | out.contains("8: 0.0, 24.9999999999"));
			Assert.assertTrue(out.contains("9: 0.0, -30.0") | out.contains("9: 0.0, -29.9999999999"));
			Assert.assertTrue(out.contains("10: 0.0, -30.0") | out.contains("10: 0.0, -29.9999999999"));
			Assert.assertTrue(out.contains("11: 0.0, 30.0") | out.contains("11: 0.0, 29.9999999999"));
			Assert.assertTrue(out.contains("12: 0.0, 30.0") | out.contains("12: 0.0, 29.9999999999"));
			Assert.assertTrue(out.contains("13: 0.0, -10.0") | out.contains("13: 0.0, -9.9999999999"));
			Assert.assertTrue(out.contains("14: 0.0, -10.0") | out.contains("14: 0.0, -9.9999999999"));
			Assert.assertTrue(out.contains("15: 0.0, 10.0") | out.contains("15: 0.0, 9.9999999999"));
			Assert.assertTrue(out.contains("16: 0.0, 10.0") | out.contains("16: 0.0, 9.9999999999"));
		}
	}
}
