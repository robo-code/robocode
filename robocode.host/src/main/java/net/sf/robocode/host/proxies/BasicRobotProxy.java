/*******************************************************************************
 * Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *     - hosting related logic moved here from robot peer
 *     - interlocked synchronization
 *     - (almost) minimized surface between RobotPeer and RobotProxy to serializable messages.
 *******************************************************************************/
package net.sf.robocode.host.proxies;


import net.sf.robocode.host.RobotStatics;
import net.sf.robocode.host.IHostManager;
import net.sf.robocode.host.events.EventManager;
import net.sf.robocode.peer.*;
import net.sf.robocode.repository.IRobotRepositoryItem;
import net.sf.robocode.robotpaint.Graphics2DSerialized;
import net.sf.robocode.robotpaint.IGraphicsProxy;
import net.sf.robocode.security.HiddenAccess;
import robocode.*;
import robocode.Event;
import robocode.exception.AbortedException;
import robocode.exception.DeathException;
import robocode.exception.DisabledException;
import robocode.exception.RobotException;
import robocode.exception.WinException;
import robocode.robotinterfaces.peer.IBasicRobotPeer;
import robocode.util.Utils;

import java.awt.*;
import static java.lang.Math.max;
import static java.lang.Math.min;
import java.util.Hashtable;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author Pavel Savara (original)
 */
public class BasicRobotProxy extends HostingRobotProxy implements IBasicRobotPeer {
	private static final long
			MAX_SET_CALL_COUNT = 10000,
			MAX_GET_CALL_COUNT = 10000;

	private IGraphicsProxy graphicsProxy;

	private RobotStatus status;
	protected ExecCommands commands;
	private ExecResults execResults;
	private final Hashtable<Integer, Bullet> bullets = new Hashtable<Integer, Bullet>();
	private int bulletCounter; 

	private final AtomicInteger setCallCount = new AtomicInteger(0);
	private final AtomicInteger getCallCount = new AtomicInteger(0);

	protected Condition waitCondition;
	private boolean testingCondition;
	private double firedEnergy;
	private double firedHeat;
	private boolean isDisabled;

	public BasicRobotProxy(IRobotRepositoryItem specification, IHostManager hostManager, IRobotPeer peer, RobotStatics statics) {
		super(specification, hostManager, peer, statics);

		eventManager = new EventManager(this);

		graphicsProxy = new Graphics2DSerialized();

		// dummy
		execResults = new ExecResults(null, null, null, null, null, false, false, false);

		setSetCallCount(0);
		setGetCallCount(0);
	}

	protected void initializeRound(ExecCommands commands, RobotStatus status) {
		updateStatus(commands, status);
		eventManager.reset();
		final StatusEvent start = new StatusEvent(status);

		eventManager.add(start);
		setSetCallCount(0);
		setGetCallCount(0);
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
		execResults = null;
		status = null;
		commands = null;
	}

	// asynchronous actions
	public Bullet setFire(double power) {
		setCall();
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
		commands.setBodyColor(color != null ? color.getRGB() : ExecCommands.defaultBodyColor);
	}

	public void setGunColor(Color color) {
		setCall();
		commands.setGunColor(color != null ? color.getRGB() : ExecCommands.defaultGunColor);
	}

	public void setRadarColor(Color color) {
		setCall();
		commands.setRadarColor(color != null ? color.getRGB() : ExecCommands.defaultRadarColor);
	}

	public void setBulletColor(Color color) {
		setCall();
		commands.setBulletColor(color != null ? color.getRGB() : ExecCommands.defaultBulletColor);
	}

	public void setScanColor(Color color) {
		setCall();
		commands.setScanColor(color != null ? color.getRGB() : ExecCommands.defaultScanColor);
	}

	// counters
	public void setCall() {
		if (!isDisabled) {
			final int res = setCallCount.incrementAndGet();

			if (res >= MAX_SET_CALL_COUNT) {
				isDisabled = true;
				println("SYSTEM: You have made " + res + " calls to setXX methods without calling execute()");
				throw new DisabledException("Too many calls to setXX methods");
			}
		}
	}

	public void getCall() {
		if (!isDisabled) {
			final int res = getCallCount.incrementAndGet();

			if (res >= MAX_GET_CALL_COUNT) {
				isDisabled = true;
				println("SYSTEM: You have made " + res + " calls to getXX methods without calling execute()");
				throw new DisabledException("Too many calls to getXX methods");
			}
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
		commands.setTryingToPaint(true);
		return getGraphicsImpl();
	}

	public void setDebugProperty(String key, String value) {
		setCall();
		commands.setDebugProperty(key, value);
	}

	public void rescan() {
		boolean reset = false;
		boolean resetValue = false;

		if (eventManager.getCurrentTopEventPriority() == eventManager.getScannedRobotEventPriority()) {
			reset = true;
			resetValue = eventManager.getInterruptible(eventManager.getScannedRobotEventPriority());
			eventManager.setInterruptible(eventManager.getScannedRobotEventPriority(), true);
		}

		commands.setScan(true);
		executeImpl();
		if (reset) {
			eventManager.setInterruptible(eventManager.getScannedRobotEventPriority(), resetValue);
		}
	}
	
	// -----------
	// implementations
	// -----------

	public long getTimeImpl() {
		return status.getTime();
	}

	public Graphics2D getGraphicsImpl() {
		return (Graphics2D) graphicsProxy;
	}

	@Override
	protected final void executeImpl() {
		if (execResults == null) {
			// this is to slow down undead robot after cleanup, from fast exception-loop
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {// just swalow here
			}
		}

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
		commands.setGraphicsCalls(graphicsProxy.readoutQueuedCalls());

		// call server
		callBattle();

		updateStatus(execResults.getCommands(), execResults.getStatus());
		graphicsProxy.setPaintingEnabled(execResults.isPaintEnabled());
		firedEnergy = 0;
		firedHeat = 0;

		// add new events
		eventManager.add(new StatusEvent(execResults.getStatus()));
		if (statics.isPaintRobot() && (execResults.isPaintEnabled())) {
			// Add paint event, if robot is a paint robot and its painting is enabled
			eventManager.add(new PaintEvent());
		}

		// add other events
		if (execResults.getEvents() != null) {
			for (Event event : execResults.getEvents()) {
				eventManager.add(event);
				HiddenAccess.updateBullets(event, bullets);
			}
		}

		if (execResults.getBulletUpdates() != null) {
			for (BulletStatus s : execResults.getBulletUpdates()) {
				final Bullet bullet = bullets.get(s.bulletId);

				if (bullet != null) {
					HiddenAccess.update(bullet, s.x, s.y, s.victimName, s.isActive);
					if (!s.isActive) {
						bullets.remove(s.bulletId);
					}
				}
			}
		}

		// add new team messages
		loadTeamMessages(execResults.getTeamMessages());

		eventManager.processEvents();
	}

	private void callBattle() {
		execResults = peer.executeImpl(commands);
	}

	@Override
	protected final void waitForBattleEndImpl() {
		eventManager.clearAllEvents(false);
		graphicsProxy.setPaintingEnabled(false);
		do {
			// Make sure remaining system events like e.g. are processed this round
			try {
				eventManager.processEvents();

				// The exceptions below are expected to occur, and has already been logged in the robot console,
				// but still exists in the robot's event queue. Hence we just ignore these!
				// Look in the HostingRobotProxy.run() to see which robot errors that are already handled.
			} catch (DeathException ignore) {} catch (WinException ignore) {// Bug fix [2952549]
			} catch (AbortedException ignore) {} catch (DisabledException ignore) {// Bug fix [2976258]
			}

			commands.setOutputText(out.readAndReset());
			commands.setGraphicsCalls(graphicsProxy.readoutQueuedCalls());

			// call server
			execResults = peer.waitForBattleEndImpl(commands);

			updateStatus(execResults.getCommands(), execResults.getStatus());

			// add new events
			if (execResults.getEvents() != null) {
				for (Event event : execResults.getEvents()) {
					if (event instanceof BattleEndedEvent) {
						eventManager.add(event);
					}
				}
			}
			eventManager.resetCustomEvents();
		} while (!execResults.isHalt() && execResults.isShouldWait());
	}

	private void updateStatus(ExecCommands commands, RobotStatus status) {
		this.status = status;
		this.commands = commands;
	}

	protected void loadTeamMessages(java.util.List<TeamMessage> teamMessages) {}

	protected final double getEnergyImpl() {
		return status.getEnergy() - firedEnergy;
	}

	protected final double getGunHeatImpl() {
		return status.getGunHeat() + firedHeat;
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

		power = min(getEnergyImpl(), min(max(power, Rules.MIN_BULLET_POWER), Rules.MAX_BULLET_POWER));

		Bullet bullet;
		BulletCommand wrapper;
		Event currentTopEvent = eventManager.getCurrentTopEvent();

		bulletCounter++;

		if (currentTopEvent != null && currentTopEvent.getTime() == status.getTime() && !statics.isAdvancedRobot()
				&& status.getGunHeadingRadians() == status.getRadarHeadingRadians()
				&& ScannedRobotEvent.class.isAssignableFrom(currentTopEvent.getClass())) {
			// this is angle assisted bullet
			ScannedRobotEvent e = (ScannedRobotEvent) currentTopEvent;
			double fireAssistAngle = Utils.normalAbsoluteAngle(status.getHeadingRadians() + e.getBearingRadians());

			bullet = new Bullet(fireAssistAngle, getX(), getY(), power, statics.getName(), null, true, bulletCounter);
			wrapper = new BulletCommand(power, true, fireAssistAngle, bulletCounter);
		} else {
			// this is normal bullet
			bullet = new Bullet(status.getGunHeadingRadians(), getX(), getY(), power, statics.getName(), null, true,
					bulletCounter);
			wrapper = new BulletCommand(power, false, 0, bulletCounter);
		}

		firedEnergy += power;
		firedHeat += Rules.getGunHeat(power);

		commands.getBullets().add(wrapper);

		bullets.put(bulletCounter, bullet);

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
