/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.host;


import net.sf.robocode.peer.IRobotStatics;
import net.sf.robocode.repository.IRobotItem;
import net.sf.robocode.repository.RobotType;
import net.sf.robocode.host.proxies.IHostingRobotProxy;
import net.sf.robocode.peer.IRobotPeer;
import robocode.control.RobotSpecification;


/**
 * @author Pavel Savara (original)
 */
public interface IHost { // NO_UCD (use default)
	IHostingRobotProxy createRobotProxy(IHostManager hostManager, RobotSpecification robotSpecification, IRobotStatics statics, IRobotPeer peer);
	String[] getReferencedClasses(IRobotItem robotItem);
	RobotType getRobotType(IRobotItem robotItem, boolean resolve, boolean message);
}
