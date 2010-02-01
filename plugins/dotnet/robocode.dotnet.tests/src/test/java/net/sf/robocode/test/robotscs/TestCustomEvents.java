/*******************************************************************************
 * Copyright (c) 2001, 2009 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package net.sf.robocode.test.robotscs;


import org.junit.Test;
import net.sf.robocode.test.helpers.RobocodeTestBed;
import net.sf.robocode.test.helpers.Assert;
import robocode.control.events.TurnEndedEvent;
import robocode.control.snapshot.IRobotSnapshot;


/**
 * @author Pavel Savara (original)
 */
public class TestCustomEvents extends RobocodeTestBed {

	@Test
	public void run() {
		super.run();
	}

	@Override
	public void onTurnEnded(TurnEndedEvent event) {
		super.onTurnEnded(event);
		IRobotSnapshot gh = event.getTurnSnapshot().getRobots()[1];

		switch (event.getTurnSnapshot().getTurn()) {
		case 130:
			test(gh, "130 onTick99\r\n130 onTick30");
			break;

		case 1451:
			test(gh, "1451 onTick99\r\n1451 onLowEnergy98\r\n1451 onTick30\r\n1451 onScannedRobot10");
            
			break;

		default:
			break;
		}
	}

	private void test(IRobotSnapshot gh, String s) {
		Assert.assertTrue("got: " + gh.getOutputStreamSnapshot() + ", expected: " + s,
				gh.getOutputStreamSnapshot().contains(s));
	}

	@Override
	public String getRobotNames() {
		return "SampleCs.Target,tested.robotscs.CustomEvents";
	}
}
