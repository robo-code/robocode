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


import robocode.manager.HostManager;
import robocode.manager.ThreadManager;
import robocode.peer.RobotPeer;
import robocode.peer.RobotStatics;
import robocode.peer.robot.RobotFileSystemManager;
import robocode.peer.robot.RobotOutputStream;


/**
 * @author Pavel Savara (original)
 */
public class HostingRobotProxy implements IHostingRobotProxy {
	protected RobotFileSystemManager robotFileSystemManager;
	protected RobotStatics statics;
	protected RobotOutputStream out;
	protected RobotPeer peer;
	protected HostManager hostManager;

	HostingRobotProxy(HostManager hostManager, RobotPeer peer, RobotStatics statics) {
		this.peer = peer;
		this.statics = statics;
		this.hostManager = hostManager;

		robotFileSystemManager = new RobotFileSystemManager(this, hostManager.getRobotFilesystemQuota());
		robotFileSystemManager.initializeQuota();

		out = new RobotOutputStream();        
	}

	public void cleanup() {
		// Remove the file system and the manager
		robotFileSystemManager = null;
		if (out != null) {
			out.close();
			out = null;
		}
	}

	public RobotOutputStream getOut() {
		return out;
	}

	public RobotStatics getStatics() {
		return statics;
	}

	// TODO temporary
	public String getRootPackageDirectory() {
		return peer.getRobotClassManager().getRobotClassLoader().getRootPackageDirectory();
	}

	// TODO temporary
	public String getClassDirectory() {
		return peer.getRobotClassManager().getRobotClassLoader().getClassDirectory();
	}

	// TODO temporary
	public RobotFileSystemManager getRobotFileSystemManager() {
		return robotFileSystemManager;
	}

	// -----------
	// battle driven methods
	// -----------

	public void startThread(ThreadManager tm) {
		tm.addThreadGroup(peer.getRobotThreadManager().getThreadGroup(), peer);
		peer.getRobotThreadManager().start();
	}

	public void forceStopThread() {
		peer.getRobotThreadManager().forceStop();
	}

	public void waitForStopThread() {
		peer.getRobotThreadManager().waitForStop();
	}

}
