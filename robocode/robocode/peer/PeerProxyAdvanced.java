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

import robocode.robotinterfaces.peer.IRobotPeerAdvanced;
import robocode.robotinterfaces.peer.IRobotPeer;
import robocode.*;

import java.util.List;
import java.io.File;

/**
 * @author Pavel Savara (original)
 */
public class PeerProxyAdvanced extends PeerProxyStandard implements IRobotPeerAdvanced {

	public PeerProxyAdvanced(IRobotPeer peer) {
		super(peer);
	}

	//asynchronous actions
	public void setResume() {
		peer.setCall();
		((IRobotPeerAdvanced)peer).setResume();
	}

	public void setStop(boolean overwrite) {
		peer.setCall();
		((IRobotPeerAdvanced)peer).setStop(overwrite);
	}

	public void setMove(double distance) {
		peer.setCall();
		((IRobotPeerAdvanced)peer).setMove(distance);
	}

	public void setTurnChassis(double radians) {
		peer.setCall();
		((IRobotPeerAdvanced)peer).setTurnChassis(radians);
	}

	public void setTurnGun(double radians) {
		peer.setCall();
		((IRobotPeerAdvanced)peer).setTurnGun(radians);
	}

	public void setTurnRadar(double radians) {
		peer.setCall();
		((IRobotPeerAdvanced)peer).setTurnRadar(radians);
	}

	//blockig actions
	public void waitFor(Condition condition) {
		((IRobotPeerAdvanced)peer).waitFor(condition);
	}

	//fast setters
	public void setMaxTurnRate(double newTurnRate) {
		peer.setCall();
		((IRobotPeerAdvanced)peer).setMaxTurnRate(newTurnRate);
	}

	public void setMaxVelocity(double newVelocity) {
		peer.setCall();
		((IRobotPeerAdvanced)peer).setMaxVelocity(newVelocity);
	}

	//events manipulation
	public void setInterruptible(boolean interruptable) {
		peer.setCall();
		((IRobotPeerAdvanced)peer).setInterruptible(interruptable);
	}

	public void setEventPriority(String eventClass, int priority) {
		peer.setCall();
		((IRobotPeerAdvanced)peer).setEventPriority(eventClass, priority);
	}

	public int getEventPriority(String eventClass) {
		peer.getCall();
		return ((IRobotPeerAdvanced)peer).getEventPriority(eventClass);
	}

	public void removeCustomEvent(Condition condition) {
		peer.setCall();
		((IRobotPeerAdvanced)peer).removeCustomEvent(condition);
	}

	public void addCustomEvent(Condition condition) {
		peer.setCall();
		((IRobotPeerAdvanced)peer).addCustomEvent(condition);
	}

	public void clearAllEvents() {
		peer.setCall();
		((IRobotPeerAdvanced)peer).clearAllEvents();
	}

	public List<Event> getAllEvents() {
		peer.getCall();
		return ((IRobotPeerAdvanced)peer).getAllEvents();
	}

	public List<BulletMissedEvent> getBulletMissedEvents() {
		peer.getCall();
		return ((IRobotPeerAdvanced)peer).getBulletMissedEvents();
	}

	public List<BulletHitBulletEvent> getBulletHitBulletEvents() {
		peer.getCall();
		return ((IRobotPeerAdvanced)peer).getBulletHitBulletEvents();
	}

	public List<BulletHitEvent> getBulletHitEvents() {
		peer.getCall();
		return ((IRobotPeerAdvanced)peer).getBulletHitEvents();
	}

	public List<HitByBulletEvent> getHitByBulletEvents() {
		peer.getCall();
		return ((IRobotPeerAdvanced)peer).getHitByBulletEvents();
	}

	public List<HitRobotEvent> getHitRobotEvents() {
		peer.getCall();
		return ((IRobotPeerAdvanced)peer).getHitRobotEvents();
	}

	public List<HitWallEvent> getHitWallEvents() {
		peer.getCall();
		return ((IRobotPeerAdvanced)peer).getHitWallEvents();
	}

	public List<RobotDeathEvent> getRobotDeathEvents() {
		peer.getCall();
		return ((IRobotPeerAdvanced)peer).getRobotDeathEvents();
	}

	public List<ScannedRobotEvent> getScannedRobotEvents() {
		peer.getCall();
		return ((IRobotPeerAdvanced)peer).getScannedRobotEvents();
	}

	//data
	public File getDataDirectory() {
		peer.getCall();
		return ((IRobotPeerAdvanced)peer).getDataDirectory();
	}

	public File getDataFile(String filename) {
		peer.getCall();
		return ((IRobotPeerAdvanced)peer).getDataFile(filename);
	}

	public long getDataQuotaAvailable() {
		peer.getCall();
		return ((IRobotPeerAdvanced)peer).getDataQuotaAvailable();
	}
}
