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
import org.junit.Test;

import robocode.control.events.TurnEndedEvent;
import robocode.control.snapshot.IRobotSnapshot;
import robocode.control.snapshot.ITurnSnapshot;


/**
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (contributor)
 */
public class TestAcceleration extends RobocodeTestBed {

	private double[] startX = new double[3];
	private double[] startY = new double[3];

	@Test
	public void run() {
		super.run();
	}

	@Override
	public String getRobotNames() {
		return "tested.robots.DecelerationCaveat1,tested.robots.DecelerationCaveat2,tested.robots.DecelerationCaveat3";        
	}

	@Override
	public String getInitialPositions() {
		return "(50,50,0), (100,50,0), (150,50,0)"; // Make sure the robots do not collide!
	}

	@Override
	public void onTurnEnded(TurnEndedEvent event) {
		super.onTurnEnded(event);
		IRobotSnapshot caveat1 = event.getTurnSnapshot().getRobots()[0];
		IRobotSnapshot caveat2 = event.getTurnSnapshot().getRobots()[1];
		IRobotSnapshot caveat3 = event.getTurnSnapshot().getRobots()[2];

		switch (event.getTurnSnapshot().getTurn()) {
		case 1:
			resetStartPositions(event.getTurnSnapshot());

			Assert.assertNear(1, caveat1.getVelocity());
			Assert.assertNear(1, caveat2.getVelocity());
			Assert.assertNear(1, caveat3.getVelocity());

			Assert.assertNear(1 - 1, distance(0, caveat1));
			Assert.assertNear(1 - 1, distance(1, caveat2));
			Assert.assertNear(1 - 1, distance(2, caveat3));
			break;

		case 2:
			Assert.assertNear(2, caveat1.getVelocity());
			Assert.assertNear(2, caveat2.getVelocity());
			Assert.assertNear(2, caveat3.getVelocity());

			Assert.assertNear(3 - 1, distance(0, caveat1));
			Assert.assertNear(3 - 1, distance(1, caveat2));
			Assert.assertNear(3 - 1, distance(2, caveat3));
			break;

		case 3:
			Assert.assertNear(3, caveat1.getVelocity());
			Assert.assertNear(3, caveat2.getVelocity());
			Assert.assertNear(3, caveat3.getVelocity());

			Assert.assertNear(6 - 1, distance(0, caveat1));
			Assert.assertNear(6 - 1, distance(1, caveat2));
			Assert.assertNear(6 - 1, distance(2, caveat3));
			break;

		case 4:
			Assert.assertNear(4, caveat1.getVelocity());
			Assert.assertNear(4, caveat2.getVelocity());
			Assert.assertNear(4, caveat3.getVelocity());

			Assert.assertNear(10 - 1, distance(0, caveat1));
			Assert.assertNear(10 - 1, distance(1, caveat2));
			Assert.assertNear(10 - 1, distance(2, caveat3));
			break;

		case 5:
			Assert.assertNear(5, caveat1.getVelocity());
			Assert.assertNear(5, caveat2.getVelocity());
			Assert.assertNear(5, caveat3.getVelocity());

			Assert.assertNear(15 - 1, distance(0, caveat1));
			Assert.assertNear(15 - 1, distance(1, caveat2));
			Assert.assertNear(15 - 1, distance(2, caveat3));
			break;

		case 6:
			Assert.assertNear(6, caveat1.getVelocity());
			Assert.assertNear(6, caveat2.getVelocity());
			Assert.assertNear(6, caveat3.getVelocity());

			Assert.assertNear(21 - 1, distance(0, caveat1));
			Assert.assertNear(21 - 1, distance(1, caveat2));
			Assert.assertNear(21 - 1, distance(2, caveat3));
			break;

		case 7:
			Assert.assertNear(7, caveat1.getVelocity());
			Assert.assertNear(7, caveat2.getVelocity());
			Assert.assertNear(7, caveat3.getVelocity());

			Assert.assertNear(28 - 1, distance(0, caveat1));
			Assert.assertNear(28 - 1, distance(1, caveat2));
			Assert.assertNear(28 - 1, distance(2, caveat3));
			break;

		case 8:
			Assert.assertNear(8, caveat1.getVelocity());
			Assert.assertNear(8, caveat2.getVelocity());
			Assert.assertNear(8, caveat3.getVelocity());

			Assert.assertNear(36 - 1, distance(0, caveat1));
			Assert.assertNear(36 - 1, distance(1, caveat2));
			Assert.assertNear(36 - 1, distance(2, caveat3));

			resetStartPositions(event.getTurnSnapshot());
			break;

		case 9:
			Assert.assertNear(8, caveat1.getVelocity());
			Assert.assertNear(8, caveat2.getVelocity());

			Assert.assertNear(6, caveat3.getVelocity()); // Caveat 3 (max. speed is set to 6)

			Assert.assertNear(8, distance(0, caveat1));
			Assert.assertNear(8, distance(1, caveat2));

			resetStartPositions(event.getTurnSnapshot());
			break;

		case 10:
			Assert.assertNear(caveat1.getVelocity(), distance(0, caveat1));
			Assert.assertNear(caveat2.getVelocity(), distance(1, caveat2));

			resetStartPositions(event.getTurnSnapshot());
			break;

		case 11:
			Assert.assertNear(caveat1.getVelocity(), distance(0, caveat1));
			Assert.assertNear(caveat2.getVelocity(), distance(1, caveat2));

			resetStartPositions(event.getTurnSnapshot());
			break;

		case 12:
			Assert.assertNear(caveat1.getVelocity(), distance(0, caveat1));
			Assert.assertNear(caveat2.getVelocity(), distance(1, caveat2));

			resetStartPositions(event.getTurnSnapshot());
			break;

		case 13:
			Assert.assertNear(caveat1.getVelocity(), distance(0, caveat1)); // Caveat 1 (distance should be = velocity)
			Assert.assertNear(caveat2.getVelocity(), distance(1, caveat2));
	
			resetStartPositions(event.getTurnSnapshot());
			break;
		
		case 14:
			Assert.assertNear(0, caveat1.getVelocity());
			Assert.assertNear(0, caveat2.getVelocity());

			Assert.assertNear(0, distance(1, caveat2)); // Caveat 2 (distance should be 0)
			break;
		}
	}
	
	private void resetStartPositions(ITurnSnapshot turnSnapshot) {
		for (int i = 0; i < 3; i++) {
			IRobotSnapshot robot = turnSnapshot.getRobots()[i];

			startX[i] = robot.getX();
			startY[i] = robot.getY();
		}
	}

	private double distance(int index, IRobotSnapshot robotSnapshot) {
		return Math.hypot(robotSnapshot.getX() - startX[index], robotSnapshot.getY() - startY[index]);
	}
}
