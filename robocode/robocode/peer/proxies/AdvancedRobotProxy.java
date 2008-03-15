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


import robocode.*;
import robocode.peer.IRobotRobotPeer;
import robocode.peer.robot.IRobotEventManager;
import robocode.peer.robot.RobotFileSystemManager;
import robocode.robotinterfaces.peer.IAdvancedRobotPeer;

import java.io.File;
import java.util.List;


/**
 * @author Pavel Savara (original)
 */
public class AdvancedRobotProxy extends StandardRobotProxy implements IAdvancedRobotPeer {

	private RobotFileSystemManager robotFileSystemManager;
	private IRobotEventManager eventManager;

	public AdvancedRobotProxy(IRobotRobotPeer peer) {
		super(peer);
		robotFileSystemManager = peer.getRobotFileSystemManager();
		eventManager = peer.getRobotEventManager();
	}

	public boolean isAdjustGunForBodyTurn() {
		getCall();
		peer.lockRead();
		try {
			return commands.isAdjustGunForBodyTurn();
		} finally {
			peer.unlockRead();
		}
	}

	public boolean isAdjustRadarForGunTurn() {
		getCall();
		peer.lockRead();
		try {
			return commands.isAdjustRadarForGunTurn();
		} finally {
			peer.unlockRead();
		}
	}

	public boolean isAdjustRadarForBodyTurn() {
		getCall();
		peer.lockRead();
		try {
			return commands.isAdjustRadarForBodyTurn();
		} finally {
			peer.unlockRead();
		}
	}

	// asynchronous actions
	public void setResume() {
		setCall();
		setResumeImplementation();
	}

	public void setStop(boolean overwrite) {
		setCall();
		setStopImplementation(overwrite);
	}

	public void setMove(double distance) {
		setCall();
		setMoveImplementation(distance);
	}

	public void setTurnBody(double radians) {
		setCall();
		setTurnBodyImplementation(radians);
	}

	public void setTurnGun(double radians) {
		setCall();
		setTurnGunImplementation(radians);
	}

	public void setTurnRadar(double radians) {
		setCall();
		setTurnRadarImplementation(radians);
	}

	// blockig actions
	public void waitFor(Condition condition) {
		peer.checkNoLock();

		commands.setWaitConditionSync(condition);
		do {
			execute(); // Always tick at least once
		} while (!condition.test());
		commands.setWaitConditionSync(null);
	}

	// fast setters
	public void setMaxTurnRate(double newTurnRate) {
		setCall();
		peer.lockWrite();
		try {
			commands.setMaxTurnRate(newTurnRate);
		} finally {
			peer.unlockWrite();
		}
	}

	public void setMaxVelocity(double newVelocity) {
		setCall();
		peer.lockWrite();
		try {
			commands.setMaxVelocity(newVelocity);
		} finally {
			peer.unlockWrite();
		}
	}

	// read from events
	public int getEventPriority(String eventClass) {
		getCall();
		try {
			return eventManager.getEventPriority(eventClass);
		} finally {
			peer.unlockRead();
		}
	}

	public List<Event> getAllEvents() {
		getCall();
		try {
			return eventManager.getAllEvents();
		} finally {
			peer.unlockRead();
		}
	}

	public List<BulletMissedEvent> getBulletMissedEvents() {
		getCall();
		try {
			return eventManager.getBulletMissedEvents();
		} finally {
			peer.unlockRead();
		}
	}

	public List<BulletHitBulletEvent> getBulletHitBulletEvents() {
		getCall();
		try {
			return eventManager.getBulletHitBulletEvents();
		} finally {
			peer.unlockRead();
		}
	}

	public List<BulletHitEvent> getBulletHitEvents() {
		getCall();
		try {
			return eventManager.getBulletHitEvents();
		} finally {
			peer.unlockRead();
		}
	}

	public List<HitByBulletEvent> getHitByBulletEvents() {
		getCall();
		try {
			return eventManager.getHitByBulletEvents();
		} finally {
			peer.unlockRead();
		}
	}

	public List<HitRobotEvent> getHitRobotEvents() {
		getCall();
		try {
			return eventManager.getHitRobotEvents();
		} finally {
			peer.unlockRead();
		}
	}

	public List<HitWallEvent> getHitWallEvents() {
		getCall();
		try {
			return eventManager.getHitWallEvents();
		} finally {
			peer.unlockRead();
		}
	}

	public List<RobotDeathEvent> getRobotDeathEvents() {
		getCall();
		try {
			return eventManager.getRobotDeathEvents();
		} finally {
			peer.unlockRead();
		}
	}

	public List<ScannedRobotEvent> getScannedRobotEvents() {
		getCall();
		peer.lockRead();
		try {
			return eventManager.getScannedRobotEvents();
		} finally {
			peer.unlockRead();
		}
	}

	// write to events
	public void setInterruptible(boolean interruptable) {
		setCall();
		peer.lockWrite();
		try {
			eventManager.setInterruptible(eventManager.getCurrentTopEventPriority(), interruptable);
		} finally {
			peer.unlockWrite();
		}
	}

	public void setEventPriority(String eventClass, int priority) {
		setCall();
		peer.lockWrite();
		try {
			eventManager.setEventPriority(eventClass, priority);
		} finally {
			peer.unlockWrite();
		}
	}

	public void removeCustomEvent(Condition condition) {
		setCall();
		peer.lockWrite();
		try {
			eventManager.removeCustomEvent(condition);
		} finally {
			peer.unlockWrite();
		}
	}

	public void addCustomEvent(Condition condition) {
		setCall();
		peer.lockWrite();
		try {
			eventManager.addCustomEvent(condition);
		} finally {
			peer.unlockWrite();
		}
	}

	public void clearAllEvents() {
		setCall();
		peer.lockWrite();
		try {
			eventManager.clearAllEvents(false);
		} finally {
			peer.unlockWrite();
		}
	}

	// data
	public File getDataDirectory() {
		getCall();
		peer.lockWrite();
		try {
			info.setIORobot(true);
			return robotFileSystemManager.getWritableDirectory();
		} finally {
			peer.unlockWrite();
		}
	}

	public File getDataFile(String filename) {
		getCall();
		peer.lockWrite();
		try {
			info.setIORobot(true);
			return new File(robotFileSystemManager.getWritableDirectory(), filename);
		} finally {
			peer.unlockWrite();
		}
	}

	public long getDataQuotaAvailable() {
		getCall();
		peer.lockRead();
		try {
			return robotFileSystemManager.getMaxQuota() - robotFileSystemManager.getQuotaUsed();
		} finally {
			peer.unlockRead();
		}
	}
}
