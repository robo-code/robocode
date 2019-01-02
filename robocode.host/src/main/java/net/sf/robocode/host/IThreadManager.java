/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.host;


import net.sf.robocode.security.IThreadManagerBase;

import java.io.PrintStream;


/**
 * @author Pavel Savara (original)
 */
public interface IThreadManager extends IThreadManagerBase {
	void addThreadGroup(ThreadGroup g, IHostedThread robotProxy);

	IHostedThread getLoadingRobotProxy(Thread t);

	IHostedThread getLoadedOrLoadingRobotProxy(Thread t);

	IHostedThread getRobotProxy(Thread t);

	void reset();

	void setLoadingRobot(IHostedThread newLoadingRobotProxy);

	void addSafeThread(Thread safeThread);
	
	void addSafeThreadGroup(ThreadGroup safeThreadGroup);

	void removeSafeThread(Thread safeThread);

	boolean isSafeThread(Thread c);

	PrintStream getRobotOutputStream();

	boolean checkRobotFileStream();
}
