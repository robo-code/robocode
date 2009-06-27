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
package net.sf.robocode.host;


import net.sf.robocode.repository.IRobotRepositoryItem;
import net.sf.robocode.repository.RobotType;
import net.sf.robocode.host.proxies.IHostingRobotProxy;
import net.sf.robocode.peer.IRobotPeer;
import robocode.control.RobotSpecification;


/**
 * @author Pavel Savara (original)
 */
public interface IHost {
	IHostingRobotProxy createRobotProxy(IHostManager hostManager, RobotSpecification robotSpecification, RobotStatics statics, IRobotPeer peer);
	String[] getReferencedClasses(IRobotRepositoryItem robotRepositoryItem);
	RobotType getRobotType(IRobotRepositoryItem robotRepositoryItem, boolean resolve, boolean message);
}
