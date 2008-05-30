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
		getCall();
		return peer.isAdjustGunForBodyTurn();
	}

	public final boolean isAdjustRadarForGunTurn() {
		getCall();
		return peer.isAdjustRadarForGunTurn();
	}

	public final boolean isAdjustRadarForBodyTurn() {
		getCall();
		return peer.isAdjustRadarForBodyTurn();
	}

	// asynchronous actions
	public final void setResume() {
		setCall();
		peer.setResume();
	}

	public final void setStop(boolean overwrite) {
		setCall();
		peer.setStop(overwrite);
	}

	public final void setMove(double distance) {
		setCall();
		peer.setMove(distance);
	}

	public final void setTurnBody(double radians) {
		setCall();
		peer.setTurnBody(radians);
	}

	public final void setTurnGun(double radians) {
		setCall();
		peer.setTurnGun(radians);
	}

	public final void setTurnRadar(double radians) {
		setCall();
		peer.setTurnRadar(radians);
	}

	// blocking actions
	public final void waitFor(Condition condition) {
		peer.waitFor(condition);
	}

	// fast setters
	public final void setMaxTurnRate(double newTurnRate) {
		setCall();
		peer.setMaxTurnRate(newTurnRate);
	}

	public final void setMaxVelocity(double newVelocity) {
		setCall();
		peer.setMaxVelocity(newVelocity);
	}

	// events manipulation
	public final void setInterruptible(boolean interruptable) {
		setCall();
		peer.getEventManager().setInterruptible(interruptable);
	}

	public final void setEventPriority(String eventClass, int priority) {
		setCall();
		peer.getEventManager().setEventPriority(eventClass, priority);
	}

	public final int getEventPriority(String eventClass) {
		getCall();
		return peer.getEventManager().getEventPriority(eventClass);
	}

	public final void removeCustomEvent(Condition condition) {
		setCall();
		peer.getEventManager().removeCustomEvent(condition);
	}

	public final void addCustomEvent(Condition condition) {
		setCall();
		peer.getEventManager().addCustomEvent(condition);
	}

	public final void clearAllEvents() {
		setCall();
		peer.getEventManager().clearAllEvents(false);
	}

	public final List<Event> getAllEvents() {
		getCall();
		return peer.getEventManager().getAllEvents();
	}

	public final List<StatusEvent> getStatusEvents() {
		getCall();
		return peer.getEventManager().getStatusEvents();
	}

	public final List<BulletMissedEvent> getBulletMissedEvents() {
		getCall();
		return peer.getEventManager().getBulletMissedEvents();
	}

	public final List<BulletHitBulletEvent> getBulletHitBulletEvents() {
		getCall();
		return peer.getEventManager().getBulletHitBulletEvents();
	}

	public final List<BulletHitEvent> getBulletHitEvents() {
		getCall();
		return peer.getEventManager().getBulletHitEvents();
	}

	public final List<HitByBulletEvent> getHitByBulletEvents() {
		getCall();
		return peer.getEventManager().getHitByBulletEvents();
	}

	public final List<HitRobotEvent> getHitRobotEvents() {
		getCall();
		return peer.getEventManager().getHitRobotEvents();
	}

	public final List<HitWallEvent> getHitWallEvents() {
		getCall();
		return peer.getEventManager().getHitWallEvents();
	}

	public final List<RobotDeathEvent> getRobotDeathEvents() {
		getCall();
		return peer.getEventManager().getRobotDeathEvents();
	}

	public final List<ScannedRobotEvent> getScannedRobotEvents() {
		getCall();
		return peer.getEventManager().getScannedRobotEvents();
	}

	public final List<PaintEvent> getPaintEvents() {
		getCall();
		return peer.getEventManager().getPaintEvents();
	}

	// data
	public final File getDataDirectory() {
		getCall();
        peer.setIORobot(true);
        return peer.getRobotFileSystemManager().getWritableDirectory();
	}

	public final File getDataFile(String filename) {
		getCall();
        peer.setIORobot(true);
        return new File(peer.getRobotFileSystemManager().getWritableDirectory(), filename);
	}

	public final long getDataQuotaAvailable() {
		getCall();
        return peer.getRobotFileSystemManager().getMaxQuota() - peer.getRobotFileSystemManager().getQuotaUsed();
	}
}
