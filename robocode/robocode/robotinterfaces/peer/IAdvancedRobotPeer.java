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
package robocode.robotinterfaces.peer;


import robocode.*;

import java.util.List;
import java.io.File;


/**
 * @author Pavel Savara (original)
 */
public interface IAdvancedRobotPeer extends IStandardRobotPeer {

	// asynchronous actions
	void setResume();
	void setStop(boolean overwrite);
	void setMove(double distance);
	void setTurnChassis(double radians);
	void setTurnGun(double radians);
	void setTurnRadar(double radians);

	// blocking actions
	void waitFor(Condition condition);

	// fast setters
	void setMaxTurnRate(double newTurnRate);
	void setMaxVelocity(double newVelocity);

	// events manipulation
	void setInterruptible(boolean interruptable);
	void setEventPriority(String eventClass, int priority);
	int getEventPriority(String eventClass);
	void removeCustomEvent(Condition condition);
	void addCustomEvent(Condition condition);
	void clearAllEvents();
	java.util.List<Event> getAllEvents();
	List<BulletMissedEvent> getBulletMissedEvents();
	List<BulletHitBulletEvent> getBulletHitBulletEvents();
	List<BulletHitEvent> getBulletHitEvents();
	List<HitByBulletEvent> getHitByBulletEvents();
	List<HitRobotEvent> getHitRobotEvents();
	List<HitWallEvent> getHitWallEvents();
	List<RobotDeathEvent> getRobotDeathEvents();
	List<ScannedRobotEvent> getScannedRobotEvents();

	// data
	File getDataDirectory();
	File getDataFile(String filename);
	long getDataQuotaAvailable();
}
