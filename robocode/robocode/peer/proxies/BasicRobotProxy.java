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
import robocode.Event;
import robocode.robotpaint.Graphics2DProxy;
import robocode.manager.HostManager;
import robocode.util.Utils;
import robocode.peer.*;
import robocode.peer.robot.RobotOutputStream;
import robocode.peer.robot.EventManager;
import robocode.peer.robot.RobotFileSystemManager;
import robocode.exception.*;
import robocode.robotinterfaces.peer.IBasicRobotPeer;

import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author Pavel Savara (original)
 */
public class BasicRobotProxy implements IBasicRobotPeer {
	private static final long
			MAX_SET_CALL_COUNT = 10000,
			MAX_GET_CALL_COUNT = 10000;

	protected HostManager hostManager;
	protected EventManager eventManager;
	protected RobotFileSystemManager robotFileSystemManager; // TODO move to advanced robot ?
	private Graphics2DProxy graphicsProxy;

	protected RobotPeer peer;
	protected RobotStatus status;
	protected RobotCommands commands;
	protected RobotStatics statics;

	private AtomicInteger setCallCount = new AtomicInteger(0);
	private AtomicInteger getCallCount = new AtomicInteger(0);

	protected Condition waitCondition;
	protected boolean testingCondition;

	public BasicRobotProxy(HostManager hostManager, RobotPeer peer, RobotStatics statics) {
		this.peer = peer;
		this.statics = statics;
		this.hostManager = hostManager;

		eventManager = new EventManager(this);

		robotFileSystemManager = new RobotFileSystemManager(this, hostManager.getRobotFilesystemQuota());
		robotFileSystemManager.initializeQuota();

		graphicsProxy = new Graphics2DProxy();
	}

	public void initialize() {
		eventManager.reset();
	}

	public void cleanup() {
		// Remove the file system and the manager
		robotFileSystemManager = null;

		// Cleanup and remove current wait condition
		if (waitCondition != null) {
			waitCondition.cleanup();
			waitCondition = null;
		}

		// Cleanup and remove the event manager
		if (eventManager != null) {
			eventManager.cleanup();
			eventManager = null;
		}

		// Cleanup graphics proxy
		graphicsProxy = null;
	}

	// asynchronous actions
	public Bullet setFire(double power) {
		return setFireImpl(power);
	}

	// blocking actions
	public void execute() {
		executeImpl();
	}

	public void move(double distance) {
		setMoveImpl(distance);
		do {
			execute(); // Always tick at least once
		} while (getDistanceRemaining() != 0);
	}

	public void turnBody(double radians) {
		setTurnBodyImpl(radians);
		do {
			execute(); // Always tick at least once
		} while (getBodyTurnRemaining() != 0);
	}

	public void turnGun(double radians) {
		setTurnGunImpl(radians);
		do {
			execute(); // Always tick at least once
		} while (getGunTurnRemaining() != 0);
	}

	public Bullet fire(double power) {
		Bullet bullet = setFire(power);

		execute();
		return bullet;
	}

	// fast setters
	public void setBodyColor(Color color) {
		setCall();
		commands.setBodyColor(color);
	}

	public void setGunColor(Color color) {
		setCall();
		commands.setGunColor(color);
	}

	public void setRadarColor(Color color) {
		setCall();
		commands.setRadarColor(color);
	}

	public void setBulletColor(Color color) {
		setCall();
		commands.setBulletColor(color);
	}

	public void setScanColor(Color color) {
		setCall();
		commands.setScanColor(color);
	}

	// counters
	public void setCall() {
		final int res = setCallCount.incrementAndGet();

		if (res >= MAX_SET_CALL_COUNT) {
			peer.getOut().println("SYSTEM: You have made " + res + " calls to setXX methods without calling execute()");
			throw new DisabledException("Too many calls to setXX methods");
		}
	}

	public void getCall() {
		final int res = getCallCount.incrementAndGet();

		if (res >= MAX_GET_CALL_COUNT) {
			peer.getOut().println("SYSTEM: You have made " + res + " calls to getXX methods without calling execute()");
			throw new DisabledException("Too many calls to getXX methods");
		}
	}

	public double getDistanceRemaining() {
		getCall();
		return commands.getDistanceRemaining();
	}

	public double getRadarTurnRemaining() {
		getCall();
		return commands.getRadarTurnRemaining();
	}

	public double getBodyTurnRemaining() {
		getCall();
		return commands.getBodyTurnRemaining();
	}

	public double getGunTurnRemaining() {
		getCall();
		return commands.getGunTurnRemaining();
	}

	public double getVelocity() {
		getCall();
		return status.getVelocity();
	}

	public double getGunCoolingRate() {
		getCall();
		return statics.getBattleRules().getGunCoolingRate();
	}

	public String getName() {
		getCall();
		return peer.getName();
	}

	public long getTime() {
		getCall();
		return status.getTime();
	}

	public double getBodyHeading() {
		getCall();
		return status.getHeadingRadians();
	}

	public double getGunHeading() {
		getCall();
		return status.getGunHeadingRadians();
	}

	public double getRadarHeading() {
		getCall();
		return status.getRadarHeadingRadians();
	}

	public double getEnergy() {
		getCall();
		return status.getEnergy();
	}

	public double getGunHeat() {
		getCall();
		return status.getGunHeat();
	}

	public double getX() {
		getCall();
		return status.getX();
	}

	public double getY() {
		getCall();
		return status.getY();
	}

	public int getOthers() {
		getCall();
		return status.getOthers();
	}

	public double getBattleFieldHeight() {
		getCall();
		return statics.getBattleRules().getBattlefieldHeight();
	}

	public double getBattleFieldWidth() {
		getCall();
		return statics.getBattleRules().getBattlefieldWidth();
	}

	public int getNumRounds() {
		getCall();
		return statics.getBattleRules().getNumRounds();
	}

	public int getRoundNum() {
		getCall();
		return status.getRoundNum();
	}

	public Graphics2D getGraphics() {
		getCall();
		return graphicsProxy;
	}

	// -----------
	// implementations
	// -----------

	protected final void executeImpl() {
		// Entering tick
		if (Thread.currentThread() != peer.getRobotThreadManager().getRunThread()) {
			throw new RobotException("You cannot take action in this thread!");
		}
		if (testingCondition) {
			throw new RobotException(
					"You cannot take action inside Condition.test().  You should handle onCustomEvent instead.");
		}

		setSetCallCount(0);
		setGetCallCount(0);

		// This stops autoscan from scanning...
		if (waitCondition != null && waitCondition.test()) {
			waitCondition = null;
			commands.setScan(true);
		}

		ExecResult result = peer.executeImpl(commands);

		updateStatus(result.commands, result.status);

		eventManager.processEvents();
	}

	protected final void setMoveImpl(double distance) {
		if (Double.isNaN(distance)) {
			peer.getOut().println("SYSTEM: You cannot call move(NaN)");
			return;
		}
		if (getEnergy() == 0) {
			return;
		}
		commands.setDistanceRemaining(distance);
		commands.setMoved(true);
	}

	protected final Bullet setFireImpl(double power) {
		if (Double.isNaN(power)) {
			peer.getOut().println("SYSTEM: You cannot call fire(NaN)");
			return null;
		}
		if (getGunHeat() > 0 || getEnergy() == 0) {
			return null;
		}

		Bullet bullet;
		BulletCommand wrapper;
		Event currentTopEvent = eventManager.getCurrentTopEvent();

		if (currentTopEvent != null && currentTopEvent.getTime() == getTime() && !statics.isAdvancedRobot()
				&& getGunHeading() == getRadarHeading()
				&& ScannedRobotEvent.class.isAssignableFrom(currentTopEvent.getClass())) {
			// this is angle assisted bullet
			ScannedRobotEvent e = (ScannedRobotEvent) currentTopEvent;
			double fireAssistAngle = Utils.normalAbsoluteAngle(getBodyHeading() + e.getBearingRadians());

			bullet = new Bullet(fireAssistAngle, getX(), getY(), power, getName());
			wrapper = new BulletCommand(bullet, true, fireAssistAngle);
		} else {
			// this is normal bullet
			bullet = new Bullet(getGunHeading(), getX(), getY(), power, getName());
			wrapper = new BulletCommand(bullet, false, 0);
		}

		commands.getBullets().add(wrapper);

		return bullet;
	}

	protected final void setTurnGunImpl(double radians) {
		commands.setGunTurnRemaining(radians);
	}

	protected final void setTurnBodyImpl(double radians) {
		if (getEnergy() > 0) {
			commands.setBodyTurnRemaining(radians);
		}
	}

	protected final void setTurnRadarImpl(double radians) {
		commands.setRadarTurnRemaining(radians);
	}

	// -----------
	// battle driven methods
	// -----------

	public void updateStatus(RobotCommands commands, RobotStatus status) {
		this.status = status;
		this.commands = commands;
	}

	public void setSetCallCount(int setCallCount) {
		this.setCallCount.set(setCallCount);
	}

	public void setGetCallCount(int getCallCount) {
		this.getCallCount.set(getCallCount);
	}

	public RobotOutputStream getOut() {
		return peer.getOut();
	}

	public EventManager getEventManager() {
		return eventManager;
	}

	// -----------
	// for robot thread
	// -----------

	public void setTestingCondition(boolean testingCondition) {
		this.testingCondition = testingCondition;
	}

	public RobotStatics getRobotStatics() {
		return statics;
	}

	// TODO temporary
	public String getRootPackageDirectory() {
		return peer.getRobotClassManager().getRobotClassLoader().getRootPackageDirectory(); 
	}

	// TODO temporary
	public String getClassDirectory() {
		return peer.getRobotClassManager().getRobotClassLoader().getClassDirectory();
	}

	// TODO temporary
	public RobotFileSystemManager getRobotFileSystemManager() {
		return robotFileSystemManager;
	}

	@Override
	public String toString() {
		return statics.getShortName() + "(" + (int) getEnergy() + ") X" + (int) getX() + " Y" + (int) getY();
	}
}
