/*******************************************************************************
 * Copyright (c) 2001-2013 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 *******************************************************************************/
package robocode;


/**
 * A SentryBorderRobot is a robot type used for keeping other robots away from the borders.
 * Robots that implement BorderSentryRobot have 400 extra life/energy, but is placed at the border
 * of the battlefield when the game is started.<br>
 * BorderSentryRobots cannot move away from the border array, and they can only hurt robots that are
 * moving into the border area.<br>
 * This class is intended for use in battles where robots should be forced away from the borders.
 * SentryBorderRobots does not get scores, and will not occur in the battle results or rankings.
 *
 * @see JuniorRobot
 * @see Robot
 * @see AdvancedRobot
 * @see TeamRobot
 * @see RateControlRobot
 * @see Droid
 *
 * @author Flemming N. Larsen (original)
 * 
 * @since 1.9.0.0
 */
public interface BorderSentryRobot {}
