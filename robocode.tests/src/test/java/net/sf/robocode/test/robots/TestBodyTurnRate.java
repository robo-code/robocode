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
public class TestBodyTurnRate extends RobotTestBed {

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
		final String out = buf.toString();
		
		if (event.getTurnSnapshot().getTurn() == 55) {
			Assert.assertTrue(out.contains("1: -10.0") | out.contains("1: -9.9999999999"));
			Assert.assertTrue(out.contains("2: -9.25") | out.contains("2: -9.2499999999"));
			Assert.assertTrue(out.contains("3: -8.5") | out.contains("3: -8.4999999999"));
			Assert.assertTrue(out.contains("4: -7.75") | out.contains("4: -7.7499999999"));
			Assert.assertTrue(out.contains("5: -7.0") | out.contains("5: -6.9999999999"));
			Assert.assertTrue(out.contains("6: -6.25") | out.contains("6: -6.2499999999"));
			Assert.assertTrue(out.contains("7: -5.5") | out.contains("7: -5.4999999999"));
			Assert.assertTrue(out.contains("8: -4.75") | out.contains("8: -4.7499999999"));
			Assert.assertTrue(out.contains("9: -4.0") | out.contains("9: -3.9999999999"));

			Assert.assertTrue(out.contains("10: 0.0") | out.contains("10: -0.0"));

			Assert.assertTrue(out.contains("15: 10.0") | out.contains("15: 9.9999999999"));
			Assert.assertTrue(out.contains("16: 9.25") | out.contains("16: 9.2499999999"));
			Assert.assertTrue(out.contains("17: 8.5") | out.contains("17: 8.4999999999"));
			Assert.assertTrue(out.contains("18: 7.75") | out.contains("18: 7.7499999999"));
			Assert.assertTrue(out.contains("19: 7.0") | out.contains("19: 6.9999999999"));
			Assert.assertTrue(out.contains("20: 6.25") | out.contains("20: 6.2499999999"));
			Assert.assertTrue(out.contains("21: 5.5") | out.contains("21: 5.4999999999"));
			Assert.assertTrue(out.contains("22: 4.75") | out.contains("22: 4.7499999999"));
			Assert.assertTrue(out.contains("23: 4.0") | out.contains("23: 3.9999999999"));

			Assert.assertTrue(out.contains("24: 0.0") | out.contains("24: -0.0"));

			Assert.assertTrue(out.contains("29: 10.0") | out.contains("29: 9.9999999999"));
			Assert.assertTrue(out.contains("30: 9.25") | out.contains("30: 9.2499999999"));
			Assert.assertTrue(out.contains("31: 8.5") | out.contains("31: 8.4999999999"));
			Assert.assertTrue(out.contains("32: 7.75") | out.contains("32: 7.7499999999"));
			Assert.assertTrue(out.contains("33: 7.0") | out.contains("33: 6.9999999999"));
			Assert.assertTrue(out.contains("34: 6.25") | out.contains("34: 6.2499999999"));
			Assert.assertTrue(out.contains("35: 5.5") | out.contains("35: 5.4999999999"));
			Assert.assertTrue(out.contains("36: 4.75") | out.contains("36: 4.7499999999"));
			Assert.assertTrue(out.contains("37: 4.0") | out.contains("37: 3.9999999999"));

			Assert.assertTrue(out.contains("38: 0.0") | out.contains("38: -0.0"));

			Assert.assertTrue(out.contains("43: -10.0") | out.contains("43: -9.9999999999"));
			Assert.assertTrue(out.contains("44: -9.25") | out.contains("44: -9.2499999999"));
			Assert.assertTrue(out.contains("45: -8.5") | out.contains("45: -8.4999999999"));
			Assert.assertTrue(out.contains("46: -7.75") | out.contains("46: -7.7499999999"));
			Assert.assertTrue(out.contains("47: -7.0") | out.contains("47: -6.9999999999"));
			Assert.assertTrue(out.contains("48: -6.25") | out.contains("48: -6.2499999999"));
			Assert.assertTrue(out.contains("49: -5.5") | out.contains("49: -5.4999999999"));
			Assert.assertTrue(out.contains("50: -4.75") | out.contains("50: -4.7499999999"));
			Assert.assertTrue(out.contains("51: -4.0") | out.contains("51: -3.9999999999"));

			Assert.assertTrue(out.contains("52: 0.0") | out.contains("52: -0.0"));
		}
	}
}
