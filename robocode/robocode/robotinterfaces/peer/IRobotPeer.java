/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * This is private interface. You should build any external component (or robot)
 * based on it's current methods because it will change in the future.
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package robocode.robotinterfaces.peer;

/**
 * @author Pavel Savara (original)
 */
public interface IRobotPeer extends IRobotPeerGetters, IRobotPeerBlockingAdvanced, IRobotPeerSettersAdvanced, IRobotPeerAsyncAdvanced, IRobotPeerTeam, IRobotPeerEventsTeam, IRobotPeerData {
}

