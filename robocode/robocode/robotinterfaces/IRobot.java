package robocode.robotinterfaces;

import robocode.StatusEvent;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * ****************************************************************************
 * Copyright (c) 2001, 2007 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 * <p/>
 * Contributors:
 * Pavel Savara
 * - Refactoring
 * *****************************************************************************
 */
public interface IRobot extends IRobotBase {

    IRobotEvents getRobotEventListener();
    ISystemEvents getSystemEventListener();

}
