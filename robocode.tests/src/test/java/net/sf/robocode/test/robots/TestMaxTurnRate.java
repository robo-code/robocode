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
public class TestMaxTurnRate extends RobocodeTestBed {

	StringBuffer buf = new StringBuffer();
	
	@Override
	public String getRobotNames() {
		return "tested.robots.MaxTurnRate,sample.Target";        
	}

	@Override
	public String getInitialPositions() {
		return "(50,50,0), (150,50,0)"; // Make sure the robots do not collide!
	}

	@Override
	public void onTurnEnded(TurnEndedEvent event) {
		super.onTurnEnded(event);

		buf.append(event.getTurnSnapshot().getRobots()[0].getOutputStreamSnapshot());
		
		if (event.getTurnSnapshot().getTurn() == 26) {
			final String out = buf.toString();

			Assert.assertTrue(out.contains("1: 0.0, 0.0") | out.contains("1: 0.0, -0.0"));
			Assert.assertTrue(out.contains("2: 0.0, -1.0") | out.contains("2: 0.0, -0.999999999"));
			Assert.assertTrue(out.contains("3: 0.0, -2.0") | out.contains("3: 0.0, -1.999999999"));
			Assert.assertTrue(out.contains("4: 0.0, -3.0") | out.contains("4: 0.0, -2.999999999"));
			Assert.assertTrue(out.contains("5: 0.0, -4.0") | out.contains("5: 0.0, -3.999999999"));
			Assert.assertTrue(out.contains("6: 0.0, -5.0") | out.contains("6: 0.0, -4.999999999"));
			Assert.assertTrue(out.contains("7: 0.0, -6.0") | out.contains("7: 0.0, -5.999999999"));
			Assert.assertTrue(out.contains("8: 0.0, -7.0") | out.contains("8: 0.0, -6.999999999"));
			Assert.assertTrue(out.contains("9: 0.0, -8.0") | out.contains("9: 0.0, -7.999999999"));
			Assert.assertTrue(out.contains("10: 0.0, -9.0") | out.contains("10: 0.0, -8.999999999"));
			Assert.assertTrue(out.contains("11: 0.0, -10.0") | out.contains("11: 0.0, -9.999999999"));
			Assert.assertTrue(out.contains("12: 0.0, -10.0") | out.contains("12: 0.0, -9.999999999"));
			Assert.assertTrue(out.contains("13: 0.0, -10.0") | out.contains("13: 0.0, -9.999999999"));

			Assert.assertTrue(out.contains("14: 0.0, 0.0") | out.contains("14: 0.0, -0.0"));
			Assert.assertTrue(out.contains("15: 0.0, 1.0") | out.contains("15: 0.0, 0.999999999"));
			Assert.assertTrue(out.contains("16: 0.0, 2.0") | out.contains("16: 0.0, 1.999999999"));
			Assert.assertTrue(out.contains("17: 0.0, 3.0") | out.contains("17: 0.0, 2.999999999"));
			Assert.assertTrue(out.contains("18: 0.0, 4.0") | out.contains("18: 0.0, 3.999999999"));
			Assert.assertTrue(out.contains("19: 0.0, 5.0") | out.contains("19: 0.0, 4.999999999"));
			Assert.assertTrue(out.contains("20: 0.0, 6.0") | out.contains("20: 0.0, 5.999999999"));
			Assert.assertTrue(out.contains("21: 0.0, 7.0") | out.contains("21: 0.0, 6.999999999"));
			Assert.assertTrue(out.contains("22: 0.0, 8.0") | out.contains("22: 0.0, 7.999999999"));
			Assert.assertTrue(out.contains("23: 0.0, 9.0") | out.contains("23: 0.0, 8.999999999"));
			Assert.assertTrue(out.contains("24: 0.0, 10.0") | out.contains("24: 0.0, 9.999999999"));
			Assert.assertTrue(out.contains("25: 0.0, 10.0") | out.contains("25: 0.0, 9.999999999"));
			Assert.assertTrue(out.contains("26: 0.0, 10.0") | out.contains("26: 0.0, 9.999999999"));
		}
	}
}
