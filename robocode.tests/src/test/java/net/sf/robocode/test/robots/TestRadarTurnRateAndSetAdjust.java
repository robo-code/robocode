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
public class TestRadarTurnRateAndSetAdjust extends RobocodeTestBed {

	StringBuffer buf = new StringBuffer();
	
	@Override
	public String getRobotNames() {
		return "tested.robots.RadarTurnRateAndSetAdjust,sample.Target";        
	}

	@Override
	public String getInitialPositions() {
		return "(50,50,0), (150,50,0)"; // Make sure the robots do not collide!
	}

	@Override
	public void onTurnEnded(TurnEndedEvent event) {
		super.onTurnEnded(event);

		buf.append(event.getTurnSnapshot().getRobots()[0].getOutputStreamSnapshot());
		
		if (event.getTurnSnapshot().getTurn() == 14) {
			final String out = buf.toString();

			Assert.assertTrue(out.contains("1: 45.0") | out.contains("1: 44.999999999"));
			Assert.assertTrue(out.contains("2: 65.0") | out.contains("2: 64.999999999"));
			Assert.assertTrue(out.contains("3: 75.0") | out.contains("3: 74.999999999"));
			Assert.assertTrue(out.contains("4: -15.0") | out.contains("4: -14.999999999"));
			Assert.assertTrue(out.contains("5: -55.0") | out.contains("5: -54.999999999"));
			Assert.assertTrue(out.contains("6: -75.0") | out.contains("6: -74.999999999"));
			Assert.assertTrue(out.contains("7: 32.0") | out.contains("7: 31.999999999"));
			Assert.assertTrue(out.contains("8: 17.0") | out.contains("8: 16.999999999"));
			Assert.assertTrue(out.contains("9: 35.0") | out.contains("9: 34.999999999"));
			Assert.assertTrue(out.contains("10: 3.0") | out.contains("10: 2.999999999"));
			Assert.assertTrue(out.contains("11: -21.0") | out.contains("11: -20.999999999"));
			Assert.assertTrue(out.contains("12: 75.0") | out.contains("12: 74.999999999"));
			Assert.assertTrue(out.contains("13: -75.0") | out.contains("13: -74.999999999"));
			Assert.assertTrue(out.contains("14: -15.0") | out.contains("14: -14.999999999"));
		}
	}
}
