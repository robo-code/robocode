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


import robocode.robotinterfaces.peer.IAdvancedRobotPeer;
import robocode.robotinterfaces.peer.IBasicRobotPeer;
import robocode.*;

import java.util.List;
import java.io.File;


/**
 * @author Pavel Savara (original)
 */
public class AdvancedRobotProxy extends StandardRobotProxy implements IAdvancedRobotPeer {

	public AdvancedRobotProxy(IBasicRobotPeer peer) {
		super(peer);
	}

	public boolean isAdjustGunForBodyTurn() {
		peer.getCall();
		return ((IAdvancedRobotPeer) peer).isAdjustGunForBodyTurn();
	}

	public boolean isAdjustRadarForGunTurn() {
		peer.getCall();
		return ((IAdvancedRobotPeer) peer).isAdjustRadarForGunTurn();
	}

	public boolean isAdjustRadarForBodyTurn() {
		peer.getCall();
		return ((IAdvancedRobotPeer) peer).isAdjustRadarForBodyTurn();
	}

	// asynchronous actions
	public void setResume() {
		peer.setCall();
		((IAdvancedRobotPeer) peer).setResume();
	}

	public void setStop(boolean overwrite) {
		peer.setCall();
		((IAdvancedRobotPeer) peer).setStop(overwrite);
	}

	public void setMove(double distance) {
		peer.setCall();
		((IAdvancedRobotPeer) peer).setMove(distance);
	}

	public void setTurnBody(double radians) {
		peer.setCall();
		((IAdvancedRobotPeer) peer).setTurnBody(radians);
	}

	public void setTurnGun(double radians) {
		peer.setCall();
		((IAdvancedRobotPeer) peer).setTurnGun(radians);
	}

	public void setTurnRadar(double radians) {
		peer.setCall();
		((IAdvancedRobotPeer) peer).setTurnRadar(radians);
	}

	// blockig actions
	public void waitFor(Condition condition) {
		((IAdvancedRobotPeer) peer).waitFor(condition);
	}

	// fast setters
	public void setMaxTurnRate(double newTurnRate) {
		peer.setCall();
		((IAdvancedRobotPeer) peer).setMaxTurnRate(newTurnRate);
	}

	public void setMaxVelocity(double newVelocity) {
		peer.setCall();
		((IAdvancedRobotPeer) peer).setMaxVelocity(newVelocity);
	}

	// events manipulation
	public void setInterruptible(boolean interruptable) {
		peer.setCall();
		((IAdvancedRobotPeer) peer).setInterruptible(interruptable);
	}

	public void setEventPriority(String eventClass, int priority) {
		peer.setCall();
		((IAdvancedRobotPeer) peer).setEventPriority(eventClass, priority);
	}

	public int getEventPriority(String eventClass) {
		peer.getCall();
		return ((IAdvancedRobotPeer) peer).getEventPriority(eventClass);
	}

	public void removeCustomEvent(Condition condition) {
		peer.setCall();
		((IAdvancedRobotPeer) peer).removeCustomEvent(condition);
	}

	public void addCustomEvent(Condition condition) {
		peer.setCall();
		((IAdvancedRobotPeer) peer).addCustomEvent(condition);
	}

	public void clearAllEvents() {
		peer.setCall();
		((IAdvancedRobotPeer) peer).clearAllEvents();
	}

	public List<Event> getAllEvents() {
		peer.getCall();
		return ((IAdvancedRobotPeer) peer).getAllEvents();
	}

	public List<BulletMissedEvent> getBulletMissedEvents() {
		peer.getCall();
		return ((IAdvancedRobotPeer) peer).getBulletMissedEvents();
	}

	public List<BulletHitBulletEvent> getBulletHitBulletEvents() {
		peer.getCall();
		return ((IAdvancedRobotPeer) peer).getBulletHitBulletEvents();
	}

	public List<BulletHitEvent> getBulletHitEvents() {
		peer.getCall();
		return ((IAdvancedRobotPeer) peer).getBulletHitEvents();
	}

	public List<HitByBulletEvent> getHitByBulletEvents() {
		peer.getCall();
		return ((IAdvancedRobotPeer) peer).getHitByBulletEvents();
	}

	public List<HitRobotEvent> getHitRobotEvents() {
		peer.getCall();
		return ((IAdvancedRobotPeer) peer).getHitRobotEvents();
	}

	public List<HitWallEvent> getHitWallEvents() {
		peer.getCall();
		return ((IAdvancedRobotPeer) peer).getHitWallEvents();
	}

	public List<RobotDeathEvent> getRobotDeathEvents() {
		peer.getCall();
		return ((IAdvancedRobotPeer) peer).getRobotDeathEvents();
	}

	public List<ScannedRobotEvent> getScannedRobotEvents() {
		peer.getCall();
		return ((IAdvancedRobotPeer) peer).getScannedRobotEvents();
	}

	// data
	public File getDataDirectory() {
		peer.getCall();
		return ((IAdvancedRobotPeer) peer).getDataDirectory();
	}

	public File getDataFile(String filename) {
		peer.getCall();
		return ((IAdvancedRobotPeer) peer).getDataFile(filename);
	}

	public long getDataQuotaAvailable() {
		peer.getCall();
		return ((IAdvancedRobotPeer) peer).getDataQuotaAvailable();
	}
}
