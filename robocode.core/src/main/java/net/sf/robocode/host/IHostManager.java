/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.host;


import net.sf.robocode.peer.IRobotPeer;
import net.sf.robocode.repository.IRobotItem;
import net.sf.robocode.repository.RobotType;
import robocode.control.RobotSpecification;

import java.io.PrintStream;


/**
 * @author Pavel Savara (original)
 */
public interface IHostManager {

	void initSecurity();

	long getRobotFilesystemQuota();

	void resetThreadManager();

	void addSafeThread(Thread safeThread);
	
	void removeSafeThread(Thread safeThread);

	PrintStream getRobotOutputStream();

	Object createRobotProxy(RobotSpecification robotSpecification, RobotStatics statics, IRobotPeer peer);

	void cleanup();

	String[] getReferencedClasses(IRobotItem robotItem);

	RobotType getRobotType(IRobotItem robotItem, boolean resolve, boolean message);
}
