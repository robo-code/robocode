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
package robocode;

import robocode.robotinterfaces.peer.IJuniorRobotPeer;

/**
 * @author Pavel Savara (refactoring)
 */
public class GunReadyCondition extends Condition {
	private IJuniorRobotPeer peer;

	public GunReadyCondition(IJuniorRobotPeer peer) {
		this.peer=peer;
	}

	@Override
	public boolean test() {
		return (peer.getGunHeat() <= 0);
	}
}
