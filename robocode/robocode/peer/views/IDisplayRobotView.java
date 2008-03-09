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
package robocode.peer.views;

import robocode.peer.robot.RobotOutputStream;

import java.awt.*;

/**
 * @author Pavel Savara (original)
 */
public interface IDisplayRobotView extends IReadingRobotView {
    void lockRead();
    void unlockRead();
    void cleanup();

    RobotOutputStream getOut();

    void setScan(boolean v);
    void setDuplicate(int d);

    void d_setPaintEnabled(boolean enabled);
    void d_setSGPaintEnabled(boolean enabled);
    void d_kill();
    
    void onInteractiveEvent(robocode.Event e);
    void onPaint(Graphics2D g);
}
