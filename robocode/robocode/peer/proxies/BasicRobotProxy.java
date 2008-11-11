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
import robocode.exception.DisabledException;
import robocode.exception.RobotException;
import robocode.manager.IHostManager;
import robocode.peer.*;
import robocode.peer.robot.EventManager;
import robocode.peer.robot.RobotClassManager;
import robocode.peer.robot.TeamMessage;
import robocode.robotinterfaces.peer.IBasicRobotPeer;
import robocode.robotpaint.Graphics2DProxy;
import robocode.util.Utils;

import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author Pavel Savara (original)
 */
public class BasicRobotProxy extends HostingRobotProxy implements IBasicRobotPeer {
	private static final long
			MAX_SET_CALL_COUNT = 10000,
			MAX_GET_CALL_COUNT = 10000;

	private Graphics2DProxy graphicsProxy;

	protected RobotStatus status;
	protected RobotCommands commands;
	private ExecResult execResult;

	private AtomicInteger setCallCount = new AtomicInteger(0);
	private AtomicInteger getCallCount = new AtomicInteger(0);

	protected Condition waitCondition;
	protected boolean testingCondition;
	protected double firedEnergy;
	protected double firedHeat;

	public BasicRobotProxy(RobotClassManager robotClassManager, IHostManager hostManager, IRobotPeer peer, RobotStatics statics) {
		super(robotClassManager, hostManager, peer, statics);

		eventManager = new EventManager(this);

		graphicsProxy = new Graphics2DProxy();

		// dummy
		execResult = new ExecResult(null, null, null, null, false, false);

		setSetCallCount(0);
		setGetCallCount(0);
	}

	protected void initializeRound(RobotCommands commands, RobotStatus status) {
		updateStatus(commands, status);
		eventManager.reset();
		final StatusEvent start = new StatusEvent(status);

		start.setTime(0);
		eventManager.add(start);
	}

	@Override
	public void cleanup() {
		super.cleanup();

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
		execResult = null;
		status = null;
		commands = null;
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
		commands.setBodyColor(color.getRGB());
	}

	public void setGunColor(Color color) {
		setCall();
		commands.setGunColor(color.getRGB());
	}

	public void setRadarColor(Color color) {
		setCall();
		commands.setRadarColor(color.getRGB());
	}

	public void setBulletColor(Color color) {
		setCall();
		commands.setBulletColor(color.getRGB());
	}

	public void setScanColor(Color color) {
		setCall();
		commands.setScanColor(color.getRGB());
	}

	// counters
	public void setCall() {
		final int res = setCallCount.incrementAndGet();

		if (res >= MAX_SET_CALL_COUNT) {
			println("SYSTEM: You have made " + res + " calls to setXX methods without calling execute()");
			throw new DisabledException("Too many calls to setXX methods");
		}
	}

	public void getCall() {
		final int res = getCallCount.incrementAndGet();

		if (res >= MAX_GET_CALL_COUNT) {
			println("SYSTEM: You have made " + res + " calls to getXX methods without calling execute()");
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
		return statics.getName();
	}

	public long getTime() {
		getCall();
		return getTimeImpl();
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
		return getEnergyImpl();
	}

	public double getGunHeat() {
		getCall();
		return getGunHeatImpl();
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
		return getGraphicsImpl();
	}

	// -----------
	// implementations
	// -----------

	public long getTimeImpl() {
		return status.getTime();
	}

	public Graphics2D getGraphicsImpl() {
		return graphicsProxy;
	}

	@Override
	protected final void executeImpl() {
		// Entering tick
		robotThreadManager.checkRunThread();
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

		commands.setOutputText(out.readAndReset());
		commands.setGraphicsCalls(graphicsProxy.getQueuedCalls());
		graphicsProxy.clearQueue();

		// call server
		execResult = peer.executeImpl(commands);

		updateStatus(execResult.commands, execResult.status);
		firedEnergy = 0;
		firedHeat = 0;

		// add new events first
		if (execResult.events != null) {
			for (Event event : execResult.events) {
				eventManager.add(event);
			}
		}

		// add new team messages
		loadTeamMessages(execResult.teamMessages);

		eventManager.processEvents();
	}

	@Override
	protected final void waitForBattleEndImpl() {
		eventManager.clearAllEvents(false);
		do {
			commands.setOutputText(out.readAndReset());
			commands.setGraphicsCalls(graphicsProxy.getQueuedCalls());
			graphicsProxy.clearQueue();

			// call server
			execResult = peer.waitForBattleEndImpl(commands);

			updateStatus(execResult.commands, execResult.status);

			// add new events
			if (execResult.events != null) {
				for (Event event : execResult.events) {
					if (event instanceof BattleEndedEvent) {
						eventManager.add(event);
					}
				}
			}
			eventManager.resetCustomEvents();
			eventManager.processEvents();
		} while (!execResult.halt && execResult.shouldWait);
	}

	private void updateStatus(RobotCommands commands, RobotStatus status) {
		this.status = status;
		this.commands = commands;
	}

	protected void loadTeamMessages(java.util.List<TeamMessage> teamMessages) {}

	protected final double getEnergyImpl() {
		return status.getEnergy() - firedEnergy;
	}

	protected final double getGunHeatImpl() {
		return status.getGunHeat() - firedHeat;
	}

	protected final void setMoveImpl(double distance) {
		if (getEnergyImpl() == 0) {
			return;
		}
		commands.setDistanceRemaining(distance);
		commands.setMoved(true);
	}

	protected final Bullet setFireImpl(double power) {
		if (Double.isNaN(power)) {
			println("SYSTEM: You cannot call fire(NaN)");
			return null;
		}
		if (getGunHeatImpl() > 0 || getEnergyImpl() == 0) {
			return null;
		}

		Bullet bullet;
		BulletCommand wrapper;
		Event currentTopEvent = eventManager.getCurrentTopEvent();

		if (currentTopEvent != null && currentTopEvent.getTime() == status.getTime() && !statics.isAdvancedRobot()
				&& status.getGunHeadingRadians() == status.getRadarHeadingRadians()
				&& ScannedRobotEvent.class.isAssignableFrom(currentTopEvent.getClass())) {
			// this is angle assisted bullet
			ScannedRobotEvent e = (ScannedRobotEvent) currentTopEvent;
			double fireAssistAngle = Utils.normalAbsoluteAngle(status.getHeadingRadians() + e.getBearingRadians());

			bullet = new Bullet(fireAssistAngle, getX(), getY(), power, statics.getName());
			wrapper = new BulletCommand(bullet, true, fireAssistAngle);
		} else {
			// this is normal bullet
			bullet = new Bullet(status.getGunHeadingRadians(), getX(), getY(), power, statics.getName());
			wrapper = new BulletCommand(bullet, false, 0);
		}

		firedEnergy += power;
		firedHeat += Rules.getGunHeat(power);

		commands.getBullets().add(wrapper);

		return bullet;
	}

	protected final void setTurnGunImpl(double radians) {
		commands.setGunTurnRemaining(radians);
	}

	protected final void setTurnBodyImpl(double radians) {
		if (getEnergyImpl() > 0) {
			commands.setBodyTurnRemaining(radians);
		}
	}

	protected final void setTurnRadarImpl(double radians) {
		commands.setRadarTurnRemaining(radians);
	}

	// -----------
	// battle driven methods
	// -----------

	private void setSetCallCount(int setCallCount) {
		this.setCallCount.set(setCallCount);
	}

	private void setGetCallCount(int getCallCount) {
		this.getCallCount.set(getCallCount);
	}

	// -----------
	// for robot thread
	// -----------

	public void setTestingCondition(boolean testingCondition) {
		this.testingCondition = testingCondition;
	}

	@Override
	public String toString() {
		return statics.getShortName() + "(" + (int) status.getEnergy() + ") X" + (int) status.getX() + " Y"
				+ (int) status.getY();
	}
}
