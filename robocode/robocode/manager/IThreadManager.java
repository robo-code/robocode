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
package robocode.manager;


import robocode.peer.proxies.IHostedThread;


/**
 * @author Pavel Savara (original)
 */
public interface IThreadManager {
	void addThreadGroup(ThreadGroup g, IHostedThread robotProxy);

	IHostedThread getLoadingRobot();

	IHostedThread getLoadingRobotProxy(Thread t);

	IHostedThread getLoadedOrLoadingRobotProxy(Thread t);

	IHostedThread getRobotProxy(Thread t);

	void reset();

	void setLoadingRobot(IHostedThread newLoadingRobotProxy);
}
