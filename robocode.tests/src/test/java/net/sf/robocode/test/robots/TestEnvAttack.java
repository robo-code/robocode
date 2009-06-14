/* Copyright (C) 2009 by Pavel Savara
This file is part of of jni4net - bridge between Java and .NET
http://jni4net.sourceforge.net/

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package net.sf.robocode.test.robots;

import net.sf.robocode.test.helpers.Assert;
import net.sf.robocode.test.helpers.RobotTestBed;
import org.junit.Test;
import robocode.control.events.TurnEndedEvent;

/**
 * @author Pavel Savara (original)
 */
public class TestEnvAttack extends RobotTestBed {
	boolean messagedAttack;

	@Test
	public void run() {
		super.run();
	}

	@Override
	public String getRobotNames() {
		return "tested.robots.BattleLost,tested.robots.EnvAttack";
	}

	@Override
	protected int getExpectedErrors() {
		return 1;
	}

	@Override
	public void onTurnEnded(TurnEndedEvent event) {
		super.onTurnEnded(event);
		final String out = event.getTurnSnapshot().getRobots()[1].getOutputStreamSnapshot();

		if (out.contains("AccessControlException: access denied")) {
			messagedAttack = true;
		}
	}

	@Override
	protected void runTeardown() {
		Assert.assertTrue(messagedAttack);
	}
}
