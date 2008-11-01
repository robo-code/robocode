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


import robocode.peer.RobotStatics;
import robocode.peer.robot.RobotFileSystemManager;
import robocode.peer.robot.RobotOutputStream;


/**
 * @author Pavel Savara (original)
 */
public interface IHostedThread extends Runnable {
	void println(String s);
	void drainEnergy();
	Class getRobotClass();
	RobotStatics getStatics();
	RobotFileSystemManager getRobotFileSystemManager();
	RobotOutputStream getOut();
}
