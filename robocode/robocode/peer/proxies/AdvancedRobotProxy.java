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

	public final boolean isAdjustGunForBodyTurn() {
		peer.getCall();
		return peer.isAdjustGunForBodyTurn();
	}

	public final boolean isAdjustRadarForGunTurn() {
		peer.getCall();
		return peer.isAdjustRadarForGunTurn();
	}

	public final boolean isAdjustRadarForBodyTurn() {
		peer.getCall();
		return peer.isAdjustRadarForBodyTurn();
	}

	// asynchronous actions
	public final void setResume() {
		peer.setCall();
		peer.setResume();
	}

	public final void setStop(boolean overwrite) {
		peer.setCall();
		peer.setStop(overwrite);
	}

	public final void setMove(double distance) {
		peer.setCall();
		peer.setMove(distance);
	}

	public final void setTurnBody(double radians) {
		peer.setCall();
		peer.setTurnBody(radians);
	}

	public final void setTurnGun(double radians) {
		peer.setCall();
		peer.setTurnGun(radians);
	}

	public final void setTurnRadar(double radians) {
		peer.setCall();
		peer.setTurnRadar(radians);
	}

	// blocking actions
	public final void waitFor(Condition condition) {
		peer.waitFor(condition);
	}

	// fast setters
	public final void setMaxTurnRate(double newTurnRate) {
		peer.setCall();
		peer.setMaxTurnRate(newTurnRate);
	}

	public final void setMaxVelocity(double newVelocity) {
		peer.setCall();
		peer.setMaxVelocity(newVelocity);
	}

	// events manipulation
	public final void setInterruptible(boolean interruptable) {
		peer.setCall();
		peer.getEventManager().setInterruptible(interruptable);
	}

	public final void setEventPriority(String eventClass, int priority) {
		peer.setCall();
		peer.getEventManager().setEventPriority(eventClass, priority);
	}

	public final int getEventPriority(String eventClass) {
		peer.getCall();
		return peer.getEventManager().getEventPriority(eventClass);
	}

	public final void removeCustomEvent(Condition condition) {
		peer.setCall();
		peer.getEventManager().removeCustomEvent(condition);
	}

	public final void addCustomEvent(Condition condition) {
		peer.setCall();
		peer.getEventManager().addCustomEvent(condition);
	}

	public final void clearAllEvents() {
		peer.setCall();
		peer.getEventManager().clearAllEvents(false);
	}

	public final List<Event> getAllEvents() {
		peer.getCall();
		return peer.getEventManager().getAllEvents();
	}

	public final List<StatusEvent> getStatusEvents() {
		peer.getCall();
		return peer.getEventManager().getStatusEvents();
	}

	public final List<BulletMissedEvent> getBulletMissedEvents() {
		peer.getCall();
		return peer.getEventManager().getBulletMissedEvents();
	}

	public final List<BulletHitBulletEvent> getBulletHitBulletEvents() {
		peer.getCall();
		return peer.getEventManager().getBulletHitBulletEvents();
	}

	public final List<BulletHitEvent> getBulletHitEvents() {
		peer.getCall();
		return peer.getEventManager().getBulletHitEvents();
	}

	public final List<HitByBulletEvent> getHitByBulletEvents() {
		peer.getCall();
		return peer.getEventManager().getHitByBulletEvents();
	}

	public final List<HitRobotEvent> getHitRobotEvents() {
		peer.getCall();
		return peer.getEventManager().getHitRobotEvents();
	}

	public final List<HitWallEvent> getHitWallEvents() {
		peer.getCall();
		return peer.getEventManager().getHitWallEvents();
	}

	public final List<RobotDeathEvent> getRobotDeathEvents() {
		peer.getCall();
		return peer.getEventManager().getRobotDeathEvents();
	}

	public final List<ScannedRobotEvent> getScannedRobotEvents() {
		peer.getCall();
		return peer.getEventManager().getScannedRobotEvents();
	}

	public final List<PaintEvent> getPaintEvents() {
		peer.getCall();
		return peer.getEventManager().getPaintEvents();
	}

	// data
	public final File getDataDirectory() {
		peer.getCall();
        peer.setIORobot(true);
        return peer.getRobotFileSystemManager().getWritableDirectory();
	}

	public final File getDataFile(String filename) {
		peer.getCall();
        peer.setIORobot(true);
        return new File(peer.getRobotFileSystemManager().getWritableDirectory(), filename);
	}

	public final long getDataQuotaAvailable() {
		peer.getCall();
        return peer.getRobotFileSystemManager().getMaxQuota() - peer.getRobotFileSystemManager().getQuotaUsed();
	}
}
