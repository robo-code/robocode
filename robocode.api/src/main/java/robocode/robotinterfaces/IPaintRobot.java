/**
 * Copyright (c) 2001-2020 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package robocode.robotinterfaces;


/**
 * A robot interface that makes it possible for a robot to receive paint events.
 *
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (contributor)
 *
 * @since 1.6
 */
public interface IPaintRobot extends IBasicRobot {

	/**
	 * This method is called by the game to notify this robot about painting
	 * events. Hence, this method must be implemented so it returns your
	 * {@link IPaintEvents} listener.
	 *
	 * @return listener to paint events or {@code null} if this robot should
	 *         not receive the notifications.
	 * @since 1.6
	 */
	IPaintEvents getPaintEventListener();
}
