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
package robocode.peer.robot;


import robocode.robotinterfaces.IBasicRobot;
import robocode.peer.RobotStatics;
import robocode.Event;
import robocode.Bullet;

import java.awt.*;
import java.util.Hashtable;


/**
 * @author Pavel Savara (original)
 */
public interface IHiddenEventHelper {
	void setDefaultPriority(Event event);
	void setPriority(Event event, int newPriority);
	void setTime(Event event, long newTime);
	boolean isCriticalEvent(Event event);
	void dispatch(Event event, IBasicRobot robot, RobotStatics statics, Graphics2D graphics);
	void updateBullets(Event event, Hashtable<Integer, Bullet> bullets);
	byte getSerializationType(Event event);
}
