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
package robocode.peer;


import robocode.robotinterfaces.peer.IAdvancedRobotPeer;
import robocode.robotinterfaces.peer.IBasicRobotPeer;
import robocode.*;

import java.util.List;
import java.io.File;


/**
 * @author Pavel Savara (original)
 */
public class AdvancedRobotPeerProxy extends StandardRobotPeerProxy implements IAdvancedRobotPeer {

	public AdvancedRobotPeerProxy(IRobotRobotPeer peer) {
		super(peer);
	}

	public boolean isAdjustGunForBodyTurn() {
		peer.getCall();
		return peer.isAdjustGunForBodyTurn();
	}

	public boolean isAdjustRadarForGunTurn() {
		peer.getCall();
		return peer.isAdjustRadarForGunTurn();
	}

	public boolean isAdjustRadarForBodyTurn() {
		peer.getCall();
		return peer.isAdjustRadarForBodyTurn();
	}

	// asynchronous actions
	public void setResume() {
		peer.setCall();
		peer.setResume();
	}

	public void setStop(boolean overwrite) {
		peer.setCall();
		peer.setStop(overwrite);
	}

	public void setMove(double distance) {
		peer.setCall();
		peer.setMove(distance);
	}

	public void setTurnBody(double radians) {
		peer.setCall();
		peer.setTurnBody(radians);
	}

	public void setTurnGun(double radians) {
		peer.setCall();
		peer.setTurnGun(radians);
	}

	public void setTurnRadar(double radians) {
		peer.setCall();
		peer.setTurnRadar(radians);
	}

	// blockig actions
	public void waitFor(Condition condition) {
		peer.waitFor(condition);
	}

	// fast setters
	public void setMaxTurnRate(double newTurnRate) {
		peer.setCall();
		peer.setMaxTurnRate(newTurnRate);
	}

	public void setMaxVelocity(double newVelocity) {
		peer.setCall();
		peer.setMaxVelocity(newVelocity);
	}

	// events manipulation
	public void setInterruptible(boolean interruptable) {
		peer.setCall();
		peer.setInterruptible(interruptable);
	}

	public void setEventPriority(String eventClass, int priority) {
		peer.setCall();
		peer.setEventPriority(eventClass, priority);
	}

	public int getEventPriority(String eventClass) {
		peer.getCall();
		return peer.getEventPriority(eventClass);
	}

	public void removeCustomEvent(Condition condition) {
		peer.setCall();
		peer.removeCustomEvent(condition);
	}

	public void addCustomEvent(Condition condition) {
		peer.setCall();
		peer.addCustomEvent(condition);
	}

	public void clearAllEvents() {
		peer.setCall();
		peer.clearAllEvents();
	}

	public List<Event> getAllEvents() {
		peer.getCall();
		return peer.getAllEvents();
	}

	public List<BulletMissedEvent> getBulletMissedEvents() {
		peer.getCall();
		return peer.getBulletMissedEvents();
	}

	public List<BulletHitBulletEvent> getBulletHitBulletEvents() {
		peer.getCall();
		return peer.getBulletHitBulletEvents();
	}

	public List<BulletHitEvent> getBulletHitEvents() {
		peer.getCall();
		return peer.getBulletHitEvents();
	}

	public List<HitByBulletEvent> getHitByBulletEvents() {
		peer.getCall();
		return peer.getHitByBulletEvents();
	}

	public List<HitRobotEvent> getHitRobotEvents() {
		peer.getCall();
		return peer.getHitRobotEvents();
	}

	public List<HitWallEvent> getHitWallEvents() {
		peer.getCall();
		return peer.getHitWallEvents();
	}

	public List<RobotDeathEvent> getRobotDeathEvents() {
		peer.getCall();
		return peer.getRobotDeathEvents();
	}

	public List<ScannedRobotEvent> getScannedRobotEvents() {
		peer.getCall();
		return peer.getScannedRobotEvents();
	}

	// data
	public File getDataDirectory() {
		peer.getCall();
		return peer.getDataDirectory();
	}

	public File getDataFile(String filename) {
		peer.getCall();
		return peer.getDataFile(filename);
	}

	public long getDataQuotaAvailable() {
		peer.getCall();
		return peer.getDataQuotaAvailable();
	}
}
