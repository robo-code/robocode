/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package robocode.robotinterfaces;


/**
 * A robot interface for creating an interactive type of robot like
 * {@link robocode.Robot} and {@link robocode.AdvancedRobot} that is able to
 * receive interactive events from the keyboard or mouse.
 * If a robot is directly inherited from this class it will behave as similar to
 * a {@link IBasicRobot}. If you need it to behave similar to a
 * {@link IAdvancedRobot} or {@link ITeamRobot}, you should inherit from these
 * interfaces instead, as these are inherited from this interface.
 *
 * @see robocode.Robot
 * @see robocode.AdvancedRobot
 * @see IBasicRobot
 * @see IJuniorRobot
 * @see IAdvancedRobot
 * @see ITeamRobot
 *
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (javadoc)
 *
 * @since 1.6
 */
public interface IInteractiveRobot extends IBasicRobot {

	/**
	 * This method is called by the game to notify this robot about interactive
	 * events, i.e. keyboard and mouse events. Hence, this method must be
	 * implemented so it returns your {@link IInteractiveEvents} listener.
	 *
	 * @return listener to interactive events or {@code null} if this robot
	 *         should not receive the notifications.
	 * @since 1.6
	 */
	IInteractiveEvents getInteractiveEventListener();
}
