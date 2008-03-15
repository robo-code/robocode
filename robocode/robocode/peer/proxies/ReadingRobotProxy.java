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


import robocode.peer.IRobotPeer;
import robocode.peer.TeamPeer;
import robocode.peer.data.RobotPeerCommands;
import robocode.peer.data.RobotPeerInfo;
import robocode.peer.data.RobotPeerStatus;
import robocode.peer.robot.RobotOutputStream;
import robocode.peer.robot.RobotStatistics;
import robocode.util.BoundingRectangle;

import java.awt.*;
import java.awt.geom.Arc2D;


/**
 * @author Pavel Savara (original)
 */
public class ReadingRobotProxy implements IReadingRobotProxy {
	private IRobotPeer peer;
	protected RobotPeerStatus status;
	protected RobotPeerInfo info;
	protected RobotPeerCommands commands;
	protected IRobotRunnableProxy view;

	public ReadingRobotProxy(IRobotPeer peer) {
		this.peer = peer;
		this.info = peer.getInfo();
		this.view = peer.getRobotRunnableView();
		this.status = peer.getStatus();
		this.commands = peer.getCommands();
	}

	public void cleanup() {
		this.peer = null;
		this.info = null;
		this.status = null;
		this.commands = null;
	}

	public IRobotPeer getPeer() {
		return peer;
	}

	// // // //  // // // // // // // // // // // // // // // // // // // // // // // //
	// info
	// // // //  // // // // // // // // // // // // // // // // // // // // // // // //

	public final int getState() {
		peer.lockRead();
		try {
			return status.getState();
		} finally {
			peer.unlockRead();
		}
	}

	public final boolean isDroid() {
		peer.lockRead();
		try {
			return info.isDroid();
		} finally {
			peer.unlockRead();
		}
	}

	public boolean isTeamRobot() {
		peer.lockRead();
		try {
			return info.isTeamRobot();
		} finally {
			peer.unlockRead();
		}
	}

	public boolean isAdvancedRobot() {
		peer.lockRead();
		try {
			return info.isAdvancedRobot();
		} finally {
			peer.unlockRead();
		}
	}

	public final boolean isInteractiveRobot() {
		peer.lockRead();
		try {
			return info.isInteractiveRobot();
		} finally {
			peer.unlockRead();
		}
	}

	public final boolean isInteractiveListener() {
		peer.lockRead();
		try {
			return info.isInteractiveListener();
		} finally {
			peer.unlockRead();
		}
	}

	public final boolean isDuplicate() {
		peer.lockRead();
		try {
			return info.isDuplicate();
		} finally {
			peer.unlockRead();
		}
	}

	public final boolean isPaintEnabled() {
		peer.lockRead();
		try {
			return info.isPaintEnabled();
		} finally {
			peer.unlockRead();
		}
	}

	public final boolean isSGPaintEnabled() {
		peer.lockRead();
		try {
			return info.isSGPaintEnabled();
		} finally {
			peer.unlockRead();
		}
	}

	public final boolean isTeamLeader() {
		peer.lockRead();
		try {
			return info.isTeamLeader();
		} finally {
			peer.unlockRead();
		}
	}

	public final boolean isIORobot() {
		peer.lockRead();
		try {
			return info.isIORobot();
		} finally {
			peer.unlockRead();
		}
	}

	public final String getName() {
		peer.lockRead();
		try {
			return info.getName();
		} finally {
			peer.unlockRead();
		}
	}

	public final String getShortName() {
		peer.lockRead();
		try {
			return info.getShortName();
		} finally {
			peer.unlockRead();
		}
	}

	public final String getVeryShortName() {
		peer.lockRead();
		try {
			return info.getVeryShortName();
		} finally {
			peer.unlockRead();
		}
	}

	public final String getFullClassNameWithVersion() {
		peer.lockRead();
		try {
			return info.getFullClassNameWithVersion();
		} finally {
			peer.unlockRead();
		}
	}

	public String getNonVersionedName() {
		peer.lockRead();
		try {
			return info.getNonVersionedName();
		} finally {
			peer.unlockRead();
		}
	}

	public final String getUniqueFullClassNameWithVersion() {
		peer.lockRead();
		try {
			return info.getUniqueFullClassNameWithVersion();
		} finally {
			peer.unlockRead();
		}
	}

	public final TeamPeer getTeamPeer() {
		peer.lockRead();
		try {
			return info.getTeamPeer();
		} finally {
			peer.unlockRead();
		}
	}

	// // // //  // // // // // // // // // // // // // // // // // // // // // // // //
	// status
	// // // //  // // // // // // // // // // // // // // // // // // // // // // // //

	public final Arc2D getScanArc() {
		peer.lockRead();
		try {
			return status.getScanArc();
		} finally {
			peer.unlockRead();
		}
	}

	public final boolean isDead() {
		peer.lockRead();
		try {
			return status.isDead();
		} finally {
			peer.unlockRead();
		}
	}

	public final boolean isAlive() {
		peer.lockRead();
		try {
			return status.isAlive();
		} finally {
			peer.unlockRead();
		}
	}

	public final boolean isRunning() {
		peer.lockRead();
		try {
			return status.isRunning();
		} finally {
			peer.unlockRead();
		}
	}

	public final boolean isSleeping() {
		peer.lockRead();
		try {
			return status.isSleeping();
		} finally {
			peer.unlockRead();
		}
	}

	public final boolean isWinner() {
		peer.lockRead();
		try {
			return status.isWinner();
		} finally {
			peer.unlockRead();
		}
	}

	public final Color getBodyColor() {
		peer.lockRead();
		try {
			return status.getBodyColor();
		} finally {
			peer.unlockRead();
		}
	}

	public final Color getGunColor() {
		peer.lockRead();
		try {
			return status.getGunColor();
		} finally {
			peer.unlockRead();
		}
	}

	public final Color getRadarColor() {
		peer.lockRead();
		try {
			return status.getRadarColor();
		} finally {
			peer.unlockRead();
		}
	}

	public final Color getScanColor() {
		peer.lockRead();
		try {
			return status.getScanColor();
		} finally {
			peer.unlockRead();
		}
	}

	public final Color getBulletColor() {
		peer.lockRead();
		try {
			return status.getBulletColor();
		} finally {
			peer.unlockRead();
		}
	}

	public final double getX() {
		peer.lockRead();
		try {
			return status.getX();
		} finally {
			peer.unlockRead();
		}
	}

	public final double getY() {
		peer.lockRead();
		try {
			return status.getY();
		} finally {
			peer.unlockRead();
		}
	}

	public final double getBattleFieldWidth() {
		peer.lockRead();
		try {
			return view.getBattleFieldWidth();
		} finally {
			peer.unlockRead();
		}
	}

	public final double getBattleFieldHeight() {
		peer.lockRead();
		try {
			return view.getBattleFieldHeight();
		} finally {
			peer.unlockRead();
		}
	}

	public final double getEnergy() {
		peer.lockRead();
		try {
			return status.getEnergy();
		} finally {
			peer.unlockRead();
		}
	}

	public final double getHeading() {
		peer.lockRead();
		try {
			return status.getHeading();
		} finally {
			peer.unlockRead();
		}
	}

	public final double getRadarHeading() {
		peer.lockRead();
		try {
			return status.getRadarHeading();
		} finally {
			peer.unlockRead();
		}
	}

	public final double getGunHeading() {
		peer.lockRead();
		try {
			return status.getGunHeading();
		} finally {
			peer.unlockRead();
		}
	}

	public final double getVelocity() {
		peer.lockRead();
		try {
			return status.getVelocity();
		} finally {
			peer.unlockRead();
		}
	}

	public final double getGunHeat() {
		peer.lockRead();
		try {
			return status.getGunHeat();
		} finally {
			peer.unlockRead();
		}
	}

	public final int getSkippedTurns() {
		peer.lockRead();
		try {
			return status.getSkippedTurns();
		} finally {
			peer.unlockRead();
		}
	}

	public final boolean getScan() {
		peer.lockRead();
		try {
			return status.getScan();
		} finally {
			peer.unlockRead();
		}
	}

	public final BoundingRectangle getBoundingBox() {
		peer.lockRead();
		try {
			return status.getBoundingBox();
		} finally {
			peer.unlockRead();
		}
	}

	// // // //  // // // // // // // // // // // // // // // // // // // // // // // //
	// commands
	// // // //  // // // // // // // // // // // // // // // // // // // // // // // //

	public final double getTurnRemaining() {
		peer.lockRead();
		try {
			return commands.getTurnRemaining();
		} finally {
			peer.unlockRead();
		}
	}

	public final double getRadarTurnRemaining() {
		peer.lockRead();
		try {
			return commands.getRadarTurnRemaining();
		} finally {
			peer.unlockRead();
		}
	}

	public final double getGunTurnRemaining() {
		peer.lockRead();
		try {
			return commands.getGunTurnRemaining();
		} finally {
			peer.unlockRead();
		}
	}

	public final double getDistanceRemaining() {
		peer.lockRead();
		try {
			return commands.getDistanceRemaining();
		} finally {
			peer.unlockRead();
		}
	}
}
