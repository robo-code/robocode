/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.host;


import net.sf.robocode.host.io.RobotFileSystemManager;
import net.sf.robocode.host.io.RobotOutputStream;


/**
 * @author Pavel Savara (original)
 */
public interface IHostedThread extends Runnable {
	void println(String s);

	void drainEnergy();

	void punishSecurityViolation(String message);

	RobotStatics getStatics();

	RobotFileSystemManager getRobotFileSystemManager();

	RobotOutputStream getOut();

	ClassLoader getRobotClassloader();
}
