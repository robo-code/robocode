/*******************************************************************************
 * Copyright (c) 2001, 2009 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Joshua Galecki
 *     - Initial implementation
 *******************************************************************************/

package robocode.robotinterfaces;


/**
 * A robot interface that makes it possible for a robot to receive object events.
 *
 * @author Joshua Galecki (original)
 */
public interface IObjectRobot extends IBasicRobot {
	
	/**
	 * This method is called by the game to notify this robot about object
	 * events. Hence, this method must be implemented so it returns your
	 * {@link IObjectEvents} listener.
	 *
	 * @return listener to object events or {@code null} if this robot should
	 *         not receive the notifications.
	 */
	IObjectEvents getObjectEventListener();

}
