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
import robocode.robotinterfaces.peer.IAdvancedRobotPeer;

import java.io.File;
import java.util.List;


/**
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (contributor)
 */
public class AdvancedRobotProxy extends StandardRobotProxy implements IAdvancedRobotPeer {

	public AdvancedRobotProxy(RobotPeer peer) {
		super(peer);
	}

	public boolean isAdjustGunForBodyTurn() {
		getCall();
		return peer.isAdjustGunForBodyTurn();
	}

	public boolean isAdjustRadarForGunTurn() {
		getCall();
		return peer.isAdjustRadarForGunTurn();
	}

	public boolean isAdjustRadarForBodyTurn() {
		getCall();
		return peer.isAdjustRadarForBodyTurn();
	}

	// asynchronous actions
	public void setResume() {
		setCall();
		peer.setResume();
	}

	public void setStop(boolean overwrite) {
		setCall();
		peer.setStop(overwrite);
	}

	public void setMove(double distance) {
		setCall();
		peer.setMove(distance);
	}

	public void setTurnBody(double radians) {
		setCall();
		peer.setTurnBody(radians);
	}

	public void setTurnGun(double radians) {
		setCall();
		peer.setTurnGun(radians);
	}

	public void setTurnRadar(double radians) {
		setCall();
		peer.setTurnRadar(radians);
	}

	// blocking actions
	public void waitFor(Condition condition) {
		peer.waitFor(condition);
	}

	// fast setters
	public void setMaxTurnRate(double newTurnRate) {
		setCall();
		peer.setMaxTurnRate(newTurnRate);
	}

	public void setMaxVelocity(double newVelocity) {
		setCall();
		peer.setMaxVelocity(newVelocity);
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
