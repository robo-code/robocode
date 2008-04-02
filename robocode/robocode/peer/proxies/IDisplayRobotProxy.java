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
package robocode.peer.proxies;


import robocode.peer.robot.RobotOutputStream;

import java.awt.*;


/**
 * @author Pavel Savara (original)
 */
public interface IDisplayRobotProxy extends IReadingRobotProxy {
    void lockRead();

    void unlockRead();

    void cleanup();

    RobotOutputStream getOut();

    void displaySetPaintEnabled(boolean enabled);

    void displaySetSGPaintEnabled(boolean enabled);

    void displayKill();

    void onInteractiveEvent(robocode.Event e);

    void onPaint(Graphics2D g);
}
