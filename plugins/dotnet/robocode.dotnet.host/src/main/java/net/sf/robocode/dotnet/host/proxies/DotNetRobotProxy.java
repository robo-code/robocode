/*******************************************************************************
 * Copyright (c) 2001, 2009 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package net.sf.robocode.dotnet.host.proxies;


import net.sf.robocode.host.IHostedThread;
import net.sf.robocode.host.RobotStatics;
import net.sf.robocode.host.proxies.IHostingRobotProxy;
import net.sf.robocode.host.io.RobotFileSystemManager;
import net.sf.robocode.host.io.RobotOutputStream;
import net.sf.robocode.peer.ExecCommands;
import robocode.RobotStatus;


/**
 * @author Pavel Savara (original)
 */
public class DotNetRobotProxy implements IHostedThread, IHostingRobotProxy {
	public void println(String s) {// TODO ZAMO
	}

	public void drainEnergy() {// TODO ZAMO
	}

	public RobotStatics getStatics() {
		return null; // TODO ZAMO
	}

	public RobotFileSystemManager getRobotFileSystemManager() {
		return null; // TODO ZAMO
	}

	public RobotOutputStream getOut() {
		return null; // TODO ZAMO
	}

	public ClassLoader getRobotClassloader() {
		return null; // TODO ZAMO
	}

	public void run() {// TODO ZAMO
	}

	public void startRound(ExecCommands commands, RobotStatus status) {// TODO ZAMO
	}

	public void forceStopThread() {// TODO ZAMO
	}

	public void waitForStopThread() {// TODO ZAMO
	}

	public void cleanup() {// TODO ZAMO
	}
}
