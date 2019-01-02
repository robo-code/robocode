/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.test.robots;


import net.sf.robocode.test.helpers.Assert;
import net.sf.robocode.test.helpers.RobocodeTestBed;

import robocode.control.events.TurnEndedEvent;


/**
 * @author Flemming N. Larsen (original)
 */
public class TestGunTurnRate extends RobocodeTestBed {

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

			Assert.assertTrue(out.contains("1: -20.0") | out.contains("1: -19.9999999999"));
			Assert.assertTrue(out.contains("2: -20.0") | out.contains("2: -19.9999999999"));
			Assert.assertTrue(out.contains("3: 20.0") | out.contains("3: 19.9999999999"));
			Assert.assertTrue(out.contains("4: 20.0") | out.contains("4: 19.9999999999"));
			Assert.assertTrue(out.contains("5: -25.0") | out.contains("5: -24.9999999999"));
			Assert.assertTrue(out.contains("6: -25.0") | out.contains("6: -24.9999999999"));
			Assert.assertTrue(out.contains("7: 25.0") | out.contains("7: 24.9999999999"));
			Assert.assertTrue(out.contains("8: 25.0") | out.contains("8: 24.9999999999"));
			Assert.assertTrue(out.contains("9: -30.0") | out.contains("9: -29.9999999999"));
			Assert.assertTrue(out.contains("10: -30.0") | out.contains("10: -29.9999999999"));
			Assert.assertTrue(out.contains("11: 30.0") | out.contains("11: 29.9999999999"));
			Assert.assertTrue(out.contains("12: 30.0") | out.contains("12: 29.9999999999"));
			Assert.assertTrue(out.contains("13: -10.0") | out.contains("13: -9.9999999999"));
			Assert.assertTrue(out.contains("14: -10.0") | out.contains("14: -9.9999999999"));
			Assert.assertTrue(out.contains("15: 10.0") | out.contains("15: 9.9999999999"));
			Assert.assertTrue(out.contains("16: 10.0") | out.contains("16: 9.9999999999"));
		}
	}
}
