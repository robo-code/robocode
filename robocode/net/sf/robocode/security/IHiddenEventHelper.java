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
package net.sf.robocode.security;


import net.sf.robocode.peer.IRobotStatics;
import robocode.Bullet;
import robocode.Event;
import robocode.robotinterfaces.IBasicRobot;

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
	void dispatch(Event event, IBasicRobot robot, IRobotStatics statics, Graphics2D graphics);
	void updateBullets(Event event, Hashtable<Integer, Bullet> bullets);
	byte getSerializationType(Event event);
}
