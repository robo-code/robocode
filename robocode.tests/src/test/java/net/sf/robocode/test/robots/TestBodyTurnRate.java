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
public class TestBodyTurnRate extends RobocodeTestBed {

	StringBuffer buf = new StringBuffer();
	
	@Override
	public String getRobotNames() {
		return "tested.robots.BodyTurnRate,sample.Target";        
	}

	@Override
	public String getInitialPositions() {
		return "(600,200,0), (50,50,0)"; // Make sure the robots do not collide!
	}

	@Override
	public void onTurnEnded(TurnEndedEvent event) {
		super.onTurnEnded(event);

		buf.append(event.getTurnSnapshot().getRobots()[0].getOutputStreamSnapshot());
		
		if (event.getTurnSnapshot().getTurn() == 60) {
			final String out = buf.toString();

			Assert.assertTrue(out.contains("1: 0.0, -10.0") | out.contains("1: 0.0, -9.9999999999"));
			Assert.assertTrue(out.contains("2: 1.0, -9.25") | out.contains("2: 1.0, -9.2499999999"));
			Assert.assertTrue(out.contains("3: 2.0, -8.5") | out.contains("3: 2.0, -8.4999999999"));
			Assert.assertTrue(out.contains("4: 3.0, -7.75") | out.contains("4: 3.0, -7.7499999999"));
			Assert.assertTrue(out.contains("5: 4.0, -7.0") | out.contains("5: 4.0, -6.9999999999"));
			Assert.assertTrue(out.contains("6: 5.0, -6.25") | out.contains("6: 5.0, -6.2499999999"));
			Assert.assertTrue(out.contains("7: 6.0, -5.5") | out.contains("7: 6.0, -5.4999999999"));
			Assert.assertTrue(out.contains("8: 7.0, -4.75") | out.contains("8: 7.0, -4.7499999999"));
			Assert.assertTrue(out.contains("9: 8.0, -4.0") | out.contains("9: 8.0, -3.9999999999"));

			Assert.assertTrue(out.contains("10: 8.0, 0.0") | out.contains("10: 8.0, -0.0"));
			Assert.assertTrue(out.contains("11: 6.0, 0.0") | out.contains("11: 6.0, -0.0"));
			Assert.assertTrue(out.contains("12: 4.0, 0.0") | out.contains("12: 4.0, -0.0"));
			Assert.assertTrue(out.contains("13: 2.0, 0.0") | out.contains("13: 2.0, -0.0"));
			Assert.assertTrue(
					out.contains("14: 0.0, 0.0") | out.contains("14: 0.0, -0.0") | out.contains("14: -0.0, 0.0")
					| out.contains("14: -0.0, -0.0"));
			Assert.assertTrue(
					out.contains("15: 0.0, 0.0") | out.contains("15: 0.0, -0.0") | out.contains("15: -0.0, 0.0")
					| out.contains("15: -0.0, -0.0"));

			Assert.assertTrue(out.contains("16: 0.0, 10.0") | out.contains("16: 0.0, 9.9999999999"));
			Assert.assertTrue(out.contains("17: 1.0, 9.25") | out.contains("17: 1.0, 9.2499999999"));
			Assert.assertTrue(out.contains("18: 2.0, 8.5") | out.contains("18: 2.0, 8.4999999999"));
			Assert.assertTrue(out.contains("19: 3.0, 7.75") | out.contains("19: 3.0, 7.7499999999"));
			Assert.assertTrue(out.contains("20: 4.0, 7.0") | out.contains("20: 4.0, 6.9999999999"));
			Assert.assertTrue(out.contains("21: 5.0, 6.25") | out.contains("21: 5.0, 6.2499999999"));
			Assert.assertTrue(out.contains("22: 6.0, 5.5") | out.contains("22: 6.0, 5.4999999999"));
			Assert.assertTrue(out.contains("23: 7.0, 4.75") | out.contains("23: 7.0, 4.7499999999"));
			Assert.assertTrue(out.contains("24: 8.0, 4.0") | out.contains("24: 8.0, 3.9999999999"));

			Assert.assertTrue(out.contains("25: 8.0, 0.0") | out.contains("25: 8.0, -0.0"));
			Assert.assertTrue(out.contains("26: 6.0, 0.0") | out.contains("26: 6.0, -0.0"));
			Assert.assertTrue(out.contains("27: 4.0, 0.0") | out.contains("27: 4.0, -0.0"));
			Assert.assertTrue(out.contains("28: 2.0, 0.0") | out.contains("28: 2.0, -0.0"));
			Assert.assertTrue(
					out.contains("29: 0.0, 0.0") | out.contains("29: 0.0, -0.0") | out.contains("29: -0.0, 0.0")
					| out.contains("29: -0.0, -0.0"));
			Assert.assertTrue(
					out.contains("30: 0.0, 0.0") | out.contains("30: 0.0, -0.0") | out.contains("30: -0.0, 0.0")
					| out.contains("30: -0.0, -0.0"));

			Assert.assertTrue(out.contains("31: 0.0, 10.0") | out.contains("31: 0.0, 9.9999999999"));
			Assert.assertTrue(out.contains("32: -1.0, 9.25") | out.contains("32: -1.0, 9.2499999999"));
			Assert.assertTrue(out.contains("33: -2.0, 8.5") | out.contains("33: -2.0, 8.4999999999"));
			Assert.assertTrue(out.contains("34: -3.0, 7.75") | out.contains("34: -3.0, 7.7499999999"));
			Assert.assertTrue(out.contains("35: -4.0, 7.0") | out.contains("35: -4.0, 6.9999999999"));
			Assert.assertTrue(out.contains("36: -5.0, 6.25") | out.contains("36: -5.0, 6.2499999999"));
			Assert.assertTrue(out.contains("37: -6.0, 5.5") | out.contains("37: -6.0, 5.4999999999"));
			Assert.assertTrue(out.contains("38: -7.0, 4.75") | out.contains("38: -7.0, 4.7499999999"));
			Assert.assertTrue(out.contains("39: -8.0, 4.0") | out.contains("39: -8.0, 3.9999999999"));

			Assert.assertTrue(out.contains("40: -8.0, 0.0") | out.contains("40: -8.0, -0.0"));
			Assert.assertTrue(out.contains("41: -6.0, 0.0") | out.contains("41: -6.0, -0.0"));
			Assert.assertTrue(out.contains("42: -4.0, 0.0") | out.contains("42: -4.0, -0.0"));
			Assert.assertTrue(out.contains("43: -2.0, 0.0") | out.contains("43: -2.0, -0.0"));
			Assert.assertTrue(
					out.contains("44: -0.0, 0.0") | out.contains("44: -0.0, -0.0") | out.contains("44: 0.0, 0.0")
					| out.contains("44: 0.0, -0.0"));
			Assert.assertTrue(
					out.contains("45: -0.0, 0.0") | out.contains("45: -0.0, -0.0") | out.contains("45: 0.0, 0.0")
					| out.contains("45: 0.0, -0.0"));

			Assert.assertTrue(out.contains("46: 0.0, -10.0") | out.contains("46: 0.0, -9.9999999999"));
			Assert.assertTrue(out.contains("47: -1.0, -9.25") | out.contains("47: -1.0, -9.2499999999"));
			Assert.assertTrue(out.contains("48: -2.0, -8.5") | out.contains("48: -2.0, -8.4999999999"));
			Assert.assertTrue(out.contains("49: -3.0, -7.75") | out.contains("49: -3.0, -7.7499999999"));
			Assert.assertTrue(out.contains("50: -4.0, -7.0") | out.contains("50: -4.0, -6.9999999999"));
			Assert.assertTrue(out.contains("51: -5.0, -6.25") | out.contains("51: -5.0, -6.2499999999"));
			Assert.assertTrue(out.contains("52: -6.0, -5.5") | out.contains("52: -6.0, -5.4999999999"));
			Assert.assertTrue(out.contains("53: -7.0, -4.75") | out.contains("53: -7.0, -4.7499999999"));
			Assert.assertTrue(out.contains("54: -8.0, -4.0") | out.contains("54: -8.0, -3.9999999999"));

			Assert.assertTrue(out.contains("55: -8.0, 0.0") | out.contains("55: -8.0, -0.0"));
			Assert.assertTrue(out.contains("56: -6.0, 0.0") | out.contains("56: -6.0, -0.0"));
			Assert.assertTrue(out.contains("57: -4.0, 0.0") | out.contains("57: -4.0, -0.0"));
			Assert.assertTrue(out.contains("58: -2.0, 0.0") | out.contains("58: -2.0, -0.0"));
			Assert.assertTrue(
					out.contains("59: -0.0, 0.0") | out.contains("59: -0.0, -0.0") | out.contains("59: 0.0, 0.0")
					| out.contains("59: 0.0, -0.0"));
			Assert.assertTrue(
					out.contains("60: -0.0, 0.0") | out.contains("60: -0.0, -0.0") | out.contains("60: 0.0, 0.0")
					| out.contains("60: 0.0, -0.0"));
		}
	}
}
