/**
 * Copyright (c) 2001-2020 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.security;


import robocode.RobotStatus;


/**
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (contributor)
 */
public interface IHiddenStatusHelper {
	RobotStatus createStatus(double energy, double x, double y, double bodyHeading, double gunHeading, double radarHeading,
			double velocity, double bodyTurnRemaining, double radarTurnRemaining, double gunTurnRemaining,
			double distanceRemaining, double gunHeat, int others, int numSentries, int roundNum, int numRounds, long time);
}
