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
 *******************************************************************************/
package robocode.robotinterfaces;

/**
 * @author Pavel Savara (original)
 * @since 1.6
 */
public interface IPaintRobot extends IBasicRobot {
    /**
     * This method is called by the game to notify this robot about painting
     * event. Hence, this method must be implemented so it returns your
     * {@link IPaintEvents} listener.
     *
     * @return listener to paint events or {@code null} if this robot
     *         should not receive the notifications.
     * @since 1.6
     */
    IPaintEvents getPaintEventListener();
}
