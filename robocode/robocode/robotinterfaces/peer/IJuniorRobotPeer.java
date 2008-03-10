/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *     Flemming N. Larsen
 *     - Javadocs
 *******************************************************************************/
package robocode.robotinterfaces.peer;


/**
 * The junior robot peer for junior robot types like {@link robocode.JuniorRobot}.
 * <p>
 * A robot peer is the object that deals with game mechanics and rules, and
 * makes sure your robot abides by them.
 *
 * @see IBasicRobotPeer
 * @see IStandardRobotPeer
 * @see IAdvancedRobotPeer
 * @see ITeamRobotPeer
 *
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (javadoc)
 *
 * @since 1.6
*/
public interface IJuniorRobotPeer extends IBasicRobotPeer {

	void turnAndMove(double distance, double radians);
}
