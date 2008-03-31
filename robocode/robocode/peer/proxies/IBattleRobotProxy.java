/**
 * ****************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 * <p/>
 * Contributors:
 * Pavel Savara
 * - Initial implementation
 * *****************************************************************************
 */

package robocode.peer.proxies;


import robocode.battle.record.RobotRecord;
import robocode.peer.BulletPeer;
import robocode.peer.robot.*;


public interface IBattleRobotProxy extends IReadingRobotProxy {
	void lockWrite();

	void unlockWrite();

	void cleanup();

	RobotOutputStream getOut();

	IBattleEventManager getBattleEventManager();

	RobotStatistics getRobotStatistics();

	RobotThreadManager getRobotThreadManager();

	RobotMessageManager getBattleMessageManager();

	RobotClassManager getRobotClassManager();

	RobotFileSystemManager getRobotFileSystemManager();

	IDisplayRobotProxy getDisplayView();

	void setupPreInitializeLocked();

	void setupSetDuplicateLocked(int d);

	boolean initializeLocked(double x, double y, double heading, java.util.List<IBattleRobotProxy> battleRobots);

	void battleKill();

	void battleZap(double z);

	void battleScan(java.util.List<IBattleRobotProxy> robots);

	void battleUpdate(java.util.List<IBattleRobotProxy> battleRobots);

	BulletPeer battleGetCurrentBullet();

	void battleSetStopping(boolean h);

	boolean battleIsTeammate(IBattleRobotProxy robot);

	void battleSetScan(boolean value);

	void battleAdjustGunHeat(double diference);

	void battleSetCurrentBullet(BulletPeer currentBullet);

	void battleSetWinner(boolean w);

	void battleSetEnergy(double e);

	void setSkippedTurnsLocked(int s);

	void wakeupLocked();

	void replaySetStatisticsLocked(RobotStatistics newStatistics);

	void replaySetRecordLocked(RobotRecord rr);

	void replaySetStateLocked(int newState);
}
