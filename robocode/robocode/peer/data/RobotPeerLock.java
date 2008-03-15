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
package robocode.peer.data;


import robocode.peer.IBattleRobotPeer;


/**
 * @author Pavel Savara (original)
 */
public class RobotPeerLock {
	// this implementation is for testing purposes, it could be reimplemented with ReentrantReaderWriterLock, but without check operations

	protected IBattleRobotPeer peer;

	public void setupInfo(IBattleRobotPeer peer) {
		this.peer = peer;
	}

	public final void checkReadLock() {
		peer.checkReadLock();
	}

	public final void checkWriteLock() {
		peer.checkWriteLock();
	}

	public final void checkNoLock() {
		peer.checkNoLock();
	}

}
