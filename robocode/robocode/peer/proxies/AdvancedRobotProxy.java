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
 *     Flemming N. Larsen
 *     - Added getPaintEvents()
 *******************************************************************************/
package robocode.peer.proxies;


import robocode.*;
import robocode.peer.RobotPeer;
import robocode.peer.RobotStatics;
import robocode.robotinterfaces.peer.IAdvancedRobotPeer;

import java.io.File;
import java.util.List;


/**
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (contributor)
 */
public class AdvancedRobotProxy extends StandardRobotProxy implements IAdvancedRobotPeer {

	public AdvancedRobotProxy(RobotPeer peer, RobotStatics statics) {
		super(peer, statics);
	}

	public boolean isAdjustGunForBodyTurn() {
		getCall();
		return commands.isAdjustGunForBodyTurn();
	}

	public boolean isAdjustRadarForGunTurn() {
		getCall();
		return commands.isAdjustRadarForGunTurn();
	}

	public boolean isAdjustRadarForBodyTurn() {
		getCall();
		return commands.isAdjustRadarForBodyTurn();
	}

	// asynchronous actions
	public void setResume() {
		setCall();
		setResumeImpl();
	}

	public void setStop(boolean overwrite) {
		setCall();
		setStopImpl(overwrite);
	}

	public void setMove(double distance) {
		setCall();
		setMoveImpl(distance);
	}

	public void setTurnBody(double radians) {
		setCall();
		setTurnBodyImpl(radians);
	}

	public void setTurnGun(double radians) {
		setCall();
		setTurnGunImpl(radians);
	}

	public void setTurnRadar(double radians) {
		setCall();
		setTurnRadarImpl(radians);
	}

	// blocking actions
	public void waitFor(Condition condition) {
		waitCondition = condition;
		do {
			execute(); // Always tick at least once
		} while (!condition.test());

		waitCondition = null;
	}

	// fast setters
	public void setMaxTurnRate(double newTurnRate) {
		setCall();
		if (Double.isNaN(newTurnRate)) {
			peer.getOut().println("You cannot setMaxTurnRate to: " + newTurnRate);
			return;
		}
		commands.setMaxTurnRate(newTurnRate);
	}

	public void setMaxVelocity(double newVelocity) {
		setCall();
		if (Double.isNaN(newVelocity)) {
			peer.getOut().println("You cannot setMaxVelocity to: " + newVelocity);
			return;
		}
		commands.setMaxVelocity(newVelocity);
	}

	// events manipulation
	public void setInterruptible(boolean interruptable) {
		setCall();
		peer.setInterruptible(interruptable);
	}

	public void setEventPriority(String eventClass, int priority) {
		setCall();
		peer.setEventPriority(eventClass, priority);
	}

	public int getEventPriority(String eventClass) {
		getCall();
		return peer.getEventPriority(eventClass);
	}

	public void removeCustomEvent(Condition condition) {
		setCall();
		peer.removeCustomEvent(condition);
	}

	public void addCustomEvent(Condition condition) {
		setCall();
		peer.addCustomEvent(condition);
	}

	public void clearAllEvents() {
		setCall();
		peer.clearAllEvents();
	}

	public List<Event> getAllEvents() {
		getCall();
		return peer.getAllEvents();
	}

	public List<StatusEvent> getStatusEvents() {
		getCall();
		return peer.getStatusEvents();
	}

	public List<BulletMissedEvent> getBulletMissedEvents() {
		getCall();
		return peer.getBulletMissedEvents();
	}

	public List<BulletHitBulletEvent> getBulletHitBulletEvents() {
		getCall();
		return peer.getBulletHitBulletEvents();
	}

	public List<BulletHitEvent> getBulletHitEvents() {
		getCall();
		return peer.getBulletHitEvents();
	}

	public List<HitByBulletEvent> getHitByBulletEvents() {
		getCall();
		return peer.getHitByBulletEvents();
	}

	public List<HitRobotEvent> getHitRobotEvents() {
		getCall();
		return peer.getHitRobotEvents();
	}

	public List<HitWallEvent> getHitWallEvents() {
		getCall();
		return peer.getHitWallEvents();
	}

	public List<RobotDeathEvent> getRobotDeathEvents() {
		getCall();
		return peer.getRobotDeathEvents();
	}

	public List<ScannedRobotEvent> getScannedRobotEvents() {
		getCall();
		return peer.getScannedRobotEvents();
	}

	// data
	public File getDataDirectory() {
		getCall();
		return peer.getDataDirectory();
	}

	public File getDataFile(String filename) {
		getCall();
		return peer.getDataFile(filename);
	}

	public long getDataQuotaAvailable() {
		getCall();
		return peer.getDataQuotaAvailable();
	}
}
