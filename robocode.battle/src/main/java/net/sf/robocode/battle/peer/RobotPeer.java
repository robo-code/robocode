/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.battle.peer;


import static net.sf.robocode.io.Logger.logMessage;
import net.sf.robocode.battle.Battle;
import net.sf.robocode.battle.BoundingRectangle;
import net.sf.robocode.host.IHostManager;
import net.sf.robocode.host.RobotStatics;
import net.sf.robocode.host.events.EventManager;
import net.sf.robocode.host.events.EventQueue;
import net.sf.robocode.host.proxies.IHostingRobotProxy;
import net.sf.robocode.io.Logger;
import net.sf.robocode.peer.*;
import net.sf.robocode.repository.IRobotItem;
import net.sf.robocode.security.HiddenAccess;
import net.sf.robocode.serialization.RbSerializer;
import robocode.*;
import robocode.control.RandomFactory;
import robocode.control.RobotSetup;
import robocode.control.RobotSpecification;
import robocode.control.snapshot.BulletState;
import robocode.control.snapshot.RobotState;
import robocode.exception.AbortedException;
import robocode.exception.DeathException;
import robocode.exception.WinException;
import static robocode.util.Utils.*;

import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

import static java.lang.Math.*;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;


/**
 * RobotPeer is an object that deals with game mechanics and rules, and makes
 * sure that robots abides the rules.
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Luis Crespo (contributor)
 * @author Titus Chen (contributor)
 * @author Robert D. Maupin (contributor)
 * @author Nathaniel Troutman (contributor)
 * @author Pavel Savara (contributor)
 * @author Patrick Cupka (contributor)
 * @author Julian Kent (contributor)
 * @author "Positive" (contributor)
 * @author "BD123" (contributor)
 */
public final class RobotPeer implements IRobotPeerBattle, IRobotPeer {

	public static final int
			WIDTH = 36,
			HEIGHT = 36;

	private static final int
			HALF_WIDTH_OFFSET = WIDTH / 2,
			HALF_HEIGHT_OFFSET = HEIGHT / 2;

	private static final int MAX_SKIPPED_TURNS = 30;
	private static final int MAX_SKIPPED_TURNS_WITH_IO = 240;

	private Battle battle;
	private RobotStatistics statistics;
	private final TeamPeer teamPeer;
	private final RobotSpecification robotSpecification;

	private IHostingRobotProxy robotProxy;
	private AtomicReference<RobotStatus> status = new AtomicReference<RobotStatus>();
	private AtomicReference<ExecCommands> commands = new AtomicReference<ExecCommands>();
	private AtomicReference<EventQueue> events = new AtomicReference<EventQueue>(new EventQueue());
	private AtomicReference<List<TeamMessage>> teamMessages = new AtomicReference<List<TeamMessage>>(
			new ArrayList<TeamMessage>());
	private AtomicReference<List<BulletStatus>> bulletUpdates = new AtomicReference<List<BulletStatus>>(
			new ArrayList<BulletStatus>());

	// thread is running
	private final AtomicBoolean isRunning = new AtomicBoolean(false);

	private final StringBuilder battleText = new StringBuilder(1024);
	private final StringBuilder proxyText = new StringBuilder(1024);
	private RobotStatics statics;
	private BattleRules battleRules;

	// for battle thread, during robots processing
	private ExecCommands currentCommands;
	private double lastHeading;
	private double lastGunHeading;
	private double lastRadarHeading;

	private double energy;
	private double velocity;
	private double bodyHeading;
	private double radarHeading;
	private double gunHeading;
	private double gunHeat;
	private double x;
	private double y;

	private boolean scan;
	private boolean turnedRadarWithGun; // last round

	private boolean isIORobot;
	private boolean isPaintEnabled;
	private boolean sgPaintEnabled;

	// waiting for next tick
	private final AtomicBoolean isSleeping = new AtomicBoolean(false);
	private final AtomicBoolean halt = new AtomicBoolean(false);

	// last and current execution time and detecting skipped turns
	private int lastExecutionTime = -1;
	private int currentExecutionTime;

	private boolean isExecFinishedAndDisabled;
	private boolean isEnergyDrained;
	private boolean isWinner;
	private boolean inCollision;
	private boolean isOverDriving;

	private RobotState state;
	private final Arc2D scanArc;
	private final BoundingRectangle boundingBox;
	private final RbSerializer rbSerializer;

	public RobotPeer(Battle battle, IHostManager hostManager, RobotSpecification robotSpecification, int duplicate, TeamPeer team, int robotIndex) {
		super();

		this.battle = battle;
		this.robotSpecification = robotSpecification;

		this.rbSerializer = new RbSerializer();

		this.boundingBox = new BoundingRectangle();
		this.scanArc = new Arc2D.Double();
		this.teamPeer = team;
		this.state = RobotState.ACTIVE;
		this.battleRules = battle.getBattleRules();

		if (team != null) {
			team.add(this);
		}
		String teamName;
		List<String> teamMembers; 
		boolean isTeamLeader;
		int teamIndex;

		if (teamPeer == null) {
			teamName = null;
			teamMembers = null;
			isTeamLeader = false;
			teamIndex = -1; // Must be set to -1 when robot is not in a team
		} else {
			teamName = team.getName();
			teamMembers = team.getMemberNames();
			isTeamLeader = team.size() == 1; // That is current team size, more might follow later. First robot is leader
			teamIndex = team.getTeamIndex();
		}

		this.statics = new RobotStatics(robotSpecification, duplicate, isTeamLeader, battleRules, teamName, teamMembers,
				robotIndex, teamIndex);
		this.statistics = new RobotStatistics(this, battle.getRobotsCount());

		this.robotProxy = (IHostingRobotProxy) hostManager.createRobotProxy(robotSpecification, statics, this);
	}

	public void println(String s) {
		synchronized (proxyText) {
			battleText.append(s);
			battleText.append("\n");
		}
	}

	private void print(String s) {
		synchronized (proxyText) {
			proxyText.append(s);
		}
	}

	public String readOutText() {
		synchronized (proxyText) {
			final String robotText = battleText.toString() + proxyText.toString();

			battleText.setLength(0);
			proxyText.setLength(0);
			return robotText;
		}
	}

	public RobotStatistics getRobotStatistics() {
		return statistics;
	}

	public ContestantStatistics getStatistics() {
		return statistics;
	}

	public RobotSpecification getRobotSpecification() {
		return robotSpecification;
	}

	// -------------------
	// statics 
	// -------------------

	public boolean isJuniorRobot() {
		return statics.isJuniorRobot();
	}

	public boolean isAdvancedRobot() {
		return statics.isAdvancedRobot();
	}

	public boolean isTeamRobot() {
		return statics.isTeamRobot();
	}

	public boolean isDroid() {
		return statics.isDroid();
	}

	public boolean isSentryRobot() {
		return statics.isSentryRobot();
	}

	public boolean isInteractiveRobot() {
		return statics.isInteractiveRobot();
	}

	public boolean isPaintRobot() {
		return statics.isPaintRobot();
	}

	public String getName() {
		return statics.getName();
	}

	public String getAnnonymousName() {
		return statics.getAnnonymousName();
	}

	public String getShortName() {
		return statics.getShortName();
	}

	public String getVeryShortName() {
		return statics.getVeryShortName();
	}

	public int getRobotIndex() {
		return statics.getRobotIndex();
	}

	public int getTeamIndex() {
		return statics.getTeamIndex();
	}

	public int getContestantIndex() {
		return getTeamIndex() >= 0 ? getTeamIndex() : getRobotIndex();
	}

	// -------------------
	// status 
	// -------------------

	public void setPaintEnabled(boolean enabled) {
		isPaintEnabled = enabled;
	}

	public boolean isPaintEnabled() {
		return isPaintEnabled;
	}

	public void setSGPaintEnabled(boolean enabled) {
		sgPaintEnabled = enabled;
	}

	public boolean isSGPaintEnabled() {
		return sgPaintEnabled;
	}

	public RobotState getState() {
		return state;
	}

	public void setState(RobotState state) {
		this.state = state;
	}

	public boolean isDead() {
		return state == RobotState.DEAD;
	}

	public boolean isAlive() {
		return state != RobotState.DEAD;
	}

	public boolean isWinner() {
		return isWinner;
	}

	public boolean isRunning() {
		return isRunning.get();
	}

	public boolean isSleeping() {
		return isSleeping.get();
	}

	public boolean isHalt() {
		return halt.get();
	}

	public void setHalt(boolean value) {
		halt.set(value);
	}

	public BoundingRectangle getBoundingBox() {
		return boundingBox;
	}

	public Arc2D getScanArc() {
		return scanArc;
	}

	// -------------------
	// robot space
	// -------------------

	public double getGunHeading() {
		return gunHeading;
	}

	public double getBodyHeading() {
		return bodyHeading;
	}

	public double getRadarHeading() {
		return radarHeading;
	}

	public double getVelocity() {
		return velocity;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getEnergy() {
		return energy;
	}

	public double getGunHeat() {
		return gunHeat;
	}

	public int getBodyColor() {
		return commands.get().getBodyColor();
	}

	public int getRadarColor() {
		return commands.get().getRadarColor();
	}

	public int getGunColor() {
		return commands.get().getGunColor();
	}

	public int getBulletColor() {
		return commands.get().getBulletColor();
	}

	public int getScanColor() {
		return commands.get().getScanColor();
	}

	// ------------
	// team
	// ------------

	public TeamPeer getTeamPeer() {
		return teamPeer;
	}

	public String getTeamName() {
		return statics.getTeamName();
	}

	public boolean isTeamLeader() {
		return statics.isTeamLeader();
	}

	boolean isTeamMate(RobotPeer otherRobot) {
		if (getTeamPeer() != null) {
			for (RobotPeer mate : getTeamPeer()) {
				if (otherRobot == mate) {
					return true;
				}
			}	
		}
		return false;
	}
	
	// -----------
	// execute
	// -----------

	private ByteBuffer bidirectionalBuffer;

	public void setupBuffer(ByteBuffer bidirectionalBuffer) {
		this.bidirectionalBuffer = bidirectionalBuffer;
	}

	public void setupThread() {
		Thread.currentThread().setName(getName());
	}

	public void executeImplSerial() throws IOException {
		ExecCommands commands = (ExecCommands) rbSerializer.deserialize(bidirectionalBuffer);

		final ExecResults results = executeImpl(commands);

		bidirectionalBuffer.clear();
		rbSerializer.serializeToBuffer(bidirectionalBuffer, RbSerializer.ExecResults_TYPE, results);
	}

	public void waitForBattleEndImplSerial() throws IOException {
		ExecCommands commands = (ExecCommands) rbSerializer.deserialize(bidirectionalBuffer);

		final ExecResults results = waitForBattleEndImpl(commands);

		bidirectionalBuffer.clear();
		rbSerializer.serializeToBuffer(bidirectionalBuffer, RbSerializer.ExecResults_TYPE, results);
	}

	public final ExecResults executeImpl(ExecCommands newCommands) {
		validateCommands(newCommands);

		if (!isExecFinishedAndDisabled) {
			// from robot to battle
			commands.set(new ExecCommands(newCommands, true));
			print(newCommands.getOutputText());
		} else {
			// slow down spammer
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}

		// If we are stopping, yet the robot took action (in onWin or onDeath), stop now.
		if (battle.isAborted()) {
			isExecFinishedAndDisabled = true;
			throw new AbortedException();
		}
		if (isDead()) {
			isExecFinishedAndDisabled = true;
			throw new DeathException();
		}
		if (isHalt()) {
			isExecFinishedAndDisabled = true;
			if (isWinner) {
				throw new WinException();
			} else {
				throw new AbortedException();
			}
		}

		waitForNextTurn();

		checkSkippedTurn();

		// from battle to robot
		final ExecCommands resCommands = new ExecCommands(this.commands.get(), false);
		final RobotStatus resStatus = status.get();

		final boolean shouldWait = battle.isAborted() || (battle.isLastRound() && isWinner());

		return new ExecResults(resCommands, resStatus, readoutEvents(), readoutTeamMessages(), readoutBullets(),
				isHalt(), shouldWait, isPaintEnabled());
	}

	public final ExecResults waitForBattleEndImpl(ExecCommands newCommands) {
		if (!isHalt()) {
			// from robot to battle
			commands.set(new ExecCommands(newCommands, true));
			print(newCommands.getOutputText());

			waitForNextTurn();
		}
		// from battle to robot
		final ExecCommands resCommands = new ExecCommands(this.commands.get(), false);
		final RobotStatus resStatus = status.get();

		final boolean shouldWait = battle.isAborted() || (battle.isLastRound() && !isWinner());

		readoutTeamMessages(); // throw away
		
		return new ExecResults(resCommands, resStatus, readoutEvents(), new ArrayList<TeamMessage>(), readoutBullets(),
				isHalt(), shouldWait, false);
	}

	private void validateCommands(ExecCommands newCommands) {
		if (Double.isNaN(newCommands.getMaxTurnRate())) {
			println("You cannot setMaxTurnRate to: " + newCommands.getMaxTurnRate());
		}
		newCommands.setMaxTurnRate(Math.min(abs(newCommands.getMaxTurnRate()), Rules.MAX_TURN_RATE_RADIANS));

		if (Double.isNaN(newCommands.getMaxVelocity())) {
			println("You cannot setMaxVelocity to: " + newCommands.getMaxVelocity());
		}
		newCommands.setMaxVelocity(Math.min(abs(newCommands.getMaxVelocity()), Rules.MAX_VELOCITY));
	}

	private List<Event> readoutEvents() {
		return events.getAndSet(new EventQueue());
	}

	private List<TeamMessage> readoutTeamMessages() {
		return teamMessages.getAndSet(new ArrayList<TeamMessage>());
	}

	private List<BulletStatus> readoutBullets() {
		return bulletUpdates.getAndSet(new ArrayList<BulletStatus>());
	}

	private void waitForNextTurn() {
		synchronized (isSleeping) {
			// Notify the battle that we are now asleep.
			// This ends any pending wait() call in battle.runRound().
			// Should not actually take place until we release the lock in wait(), below.
			isSleeping.set(true);
			isSleeping.notifyAll();
			// Notifying battle that we're asleep
			// Sleeping and waiting for battle to wake us up.
			try {
				isSleeping.wait();
			} catch (InterruptedException e) {
				// We are expecting this to happen when a round is ended!

				// Immediately reasserts the exception by interrupting the caller thread itself
				Thread.currentThread().interrupt();
			}
			isSleeping.set(false);
			// Notify battle thread, which is waiting in
			// our wakeup() call, to return.
			// It's quite possible, by the way, that we'll be back in sleep (above)
			// before the battle thread actually wakes up
			isSleeping.notifyAll();
		}
	}

	// -----------
	// called on battle thread
	// -----------

	public void waitWakeup() {
		synchronized (isSleeping) {
			if (isSleeping()) {
				// Wake up the thread
				isSleeping.notifyAll();
				try {
					isSleeping.wait(10000);
				} catch (InterruptedException e) {
					// Immediately reasserts the exception by interrupting the caller thread itself
					Thread.currentThread().interrupt();
				}
			}
		}
	}

	private void waitWakeupNoWait() {
		synchronized (isSleeping) {
			if (isSleeping()) {
				// Wake up the thread
				isSleeping.notifyAll();
			}
		}
	}

	public void waitSleeping(long millisWait, int nanosWait) {
		synchronized (isSleeping) {
			// It's quite possible for simple robots to
			// complete their processing before we get here,
			// so we test if the robot is already asleep.

			if (!isSleeping()) {
				try {
					for (long i = millisWait; i > 0 && !isSleeping() && isRunning(); i--) {
						isSleeping.wait(0, 999999);
					}
					if (!isSleeping() && isRunning()) {
						isSleeping.wait(0, nanosWait);
					}
				} catch (InterruptedException e) {
					// Immediately reasserts the exception by interrupting the caller thread itself
					Thread.currentThread().interrupt();

					logMessage("Wait for " + getName() + " interrupted.");
				}
			}
		}
	}

	public void checkSkippedTurn() {
		// Store last and current execution time for detecting skipped turns
		lastExecutionTime = currentExecutionTime;
		currentExecutionTime = battle.getTime();

		int numSkippedTurns = (currentExecutionTime - lastExecutionTime) - 1;
		if (numSkippedTurns >= 1) {
			events.get().clear(false);

			if (isAlive()) {
				for (int skippedTurn = lastExecutionTime + 1; skippedTurn < currentExecutionTime; skippedTurn++) {
					addEvent(new SkippedTurnEvent(skippedTurn));
					println("SYSTEM: " + getShortName() + " skipped turn " + skippedTurn);
				}
			}

			if ((!isIORobot && (numSkippedTurns > MAX_SKIPPED_TURNS))
					|| (isIORobot && (numSkippedTurns > MAX_SKIPPED_TURNS_WITH_IO))) {
				println("SYSTEM: " + getShortName() + " has not performed any actions in a reasonable amount of time.");
				println("SYSTEM: No score will be generated.");
				setHalt(true);
				waitWakeupNoWait();
				punishBadBehavior(BadBehavior.SKIPPED_TOO_MANY_TURNS);
				robotProxy.forceStopThread();
			}
		}
	}

	public void initializeRound(List<RobotPeer> robots, RobotSetup[] initialRobotSetups) {
		boolean valid = false;

		if (initialRobotSetups != null) {
			int robotIndex = statics.getRobotIndex();

			if (robotIndex >= 0 && robotIndex < initialRobotSetups.length) {
				RobotSetup setup = initialRobotSetups[robotIndex];
				if (setup != null) {
					x = setup.getX();
					y = setup.getY();
					bodyHeading = gunHeading = radarHeading = setup.getHeading();

					updateBoundingBox();
					valid = validSpot(robots);
				}
			}
		}

		if (!valid) {
			final Random random = RandomFactory.getRandom();

			double maxWidth = battleRules.getBattlefieldWidth() - RobotPeer.WIDTH;
			double maxHeight = battleRules.getBattlefieldHeight() - RobotPeer.HEIGHT;

			double halfRobotWidth = RobotPeer.WIDTH / 2;
			double halfRobotHeight = RobotPeer.HEIGHT / 2;

			int sentryBorderSize = battle.getBattleRules().getSentryBorderSize();
			int sentryBorderMoveWidth = sentryBorderSize - RobotPeer.WIDTH;
			int sentryBorderMoveHeight = sentryBorderSize - RobotPeer.HEIGHT;

			for (int j = 0; j < 1000; j++) {
				double rndX = random.nextDouble();
				double rndY = random.nextDouble();

				if (isSentryRobot()) {
					boolean placeOnHorizontalBar = random.nextDouble()
							<= ((double) battleRules.getBattlefieldWidth()
									/ (battleRules.getBattlefieldWidth() + battleRules.getBattlefieldHeight()));

					if (placeOnHorizontalBar) {
						x = halfRobotWidth + rndX * maxWidth;
						y = halfRobotHeight + (sentryBorderMoveHeight * (rndY * 2.0 - 1.0) + maxHeight) % maxHeight;
					} else {
						y = halfRobotHeight + rndY * maxHeight;
						x = halfRobotWidth + (sentryBorderMoveWidth * (rndX * 2.0 - 1.0) + maxWidth) % maxWidth;
					}
				} else {
					if (battle.countActiveSentries() > 0) {
						int safeZoneWidth = battleRules.getBattlefieldWidth() - 2 * sentryBorderSize;
						int safeZoneHeight = battleRules.getBattlefieldHeight() - 2 * sentryBorderSize;

						x = sentryBorderSize + RobotPeer.WIDTH + rndX * (safeZoneWidth - 2 * RobotPeer.WIDTH);
						y = sentryBorderSize + RobotPeer.HEIGHT + rndY * (safeZoneHeight - 2 * RobotPeer.HEIGHT);
					} else {				
						x = RobotPeer.WIDTH + rndX * (battleRules.getBattlefieldWidth() - 2 * RobotPeer.WIDTH);
						y = RobotPeer.HEIGHT + rndY * (battleRules.getBattlefieldHeight() - 2 * RobotPeer.HEIGHT);
					}
				}

				bodyHeading = 2 * Math.PI * random.nextDouble();
				gunHeading = radarHeading = bodyHeading;
				updateBoundingBox();

				if (validSpot(robots)) {
					break;
				}
			}
		}

		setState(RobotState.ACTIVE);

		isWinner = false;
		velocity = 0;

		energy = 100;
		if (statics.isSentryRobot()) {
			energy += 400;
		}
		if (statics.isTeamLeader()) {
			energy += 100;
		}
		if (statics.isDroid()) {
			energy += 20;
		}

		gunHeat = 3;

		setHalt(false);
		isExecFinishedAndDisabled = false;
		isEnergyDrained = false;

		scan = false;

		inCollision = false;

		scanArc.setAngleStart(0);
		scanArc.setAngleExtent(0);
		scanArc.setFrame(-100, -100, 1, 1);

		lastExecutionTime = -1;

		status = new AtomicReference<RobotStatus>();

		readoutEvents();
		readoutTeamMessages();
		readoutBullets();

		synchronized (proxyText) { // Bug fix #387
			battleText.setLength(0);
			proxyText.setLength(0);
		}

		// Prepare new execution commands, but copy the colors from the last commands.
		// Bugfix [2628217] - Robot Colors don't stick between rounds.
		ExecCommands newExecCommands = new ExecCommands();

		newExecCommands.copyColors(commands.get());
		commands = new AtomicReference<ExecCommands>(newExecCommands);
	}

	private boolean validSpot(List<RobotPeer> robots) {
		for (RobotPeer otherRobot : robots) {
			if (otherRobot != null && otherRobot != this) {
				if (getBoundingBox().intersects(otherRobot.getBoundingBox())) {
					return false;
				}
			}
		}
		return true;
	}

	public void startRound(long waitMillis, int waitNanos) {
		Logger.logMessage(".", false);

		statistics.reset();

		ExecCommands newExecCommands = new ExecCommands();

		// Copy the colors from the last commands.
		// Bugfix [2628217] - Robot Colors don't stick between rounds.
		newExecCommands.copyColors(commands.get());

		currentCommands = newExecCommands;

		int others = battle.countActiveParticipants() - (isAlive() ? 1 : 0);
		int numSentries = battle.countActiveSentries();

		RobotStatus stat = HiddenAccess.createStatus(energy, x, y, bodyHeading, gunHeading, radarHeading, velocity,
				currentCommands.getBodyTurnRemaining(), currentCommands.getRadarTurnRemaining(),
				currentCommands.getGunTurnRemaining(), currentCommands.getDistanceRemaining(), gunHeat, others, numSentries,
				battle.getRoundNum(), battle.getNumRounds(), battle.getTime());

		status.set(stat);
		robotProxy.startRound(currentCommands, stat);

		synchronized (isSleeping) {
			try {
				// Wait for the robot to go to sleep (take action)
				isSleeping.wait(waitMillis, waitNanos);
			} catch (InterruptedException e) {
				logMessage("Wait for " + getName() + " interrupted.");

				// Immediately reasserts the exception by interrupting the caller thread itself
				Thread.currentThread().interrupt();
			}
		}
		if (!isSleeping() && !battle.isDebugging()) {
			logMessage("\n" + getName() + " still has not started after " + waitMillis + " ms... giving up.");
		}
	}

	public void performLoadCommands() {
		currentCommands = commands.get();

		fireBullets(currentCommands.getBullets());

		if (currentCommands.isScan()) {
			scan = true;
		}

		if (currentCommands.isIORobot()) {
			isIORobot = true;
		}

		if (currentCommands.isMoved()) {
			currentCommands.setMoved(false);
		}
	}

	private void fireBullets(List<BulletCommand> bulletCommands) {
		BulletPeer newBullet = null;

		for (BulletCommand bulletCmd : bulletCommands) {
			if (Double.isNaN(bulletCmd.getPower())) {
				println("SYSTEM: You cannot call fire(NaN)");
				continue;
			}
			if (gunHeat > 0 || energy == 0) {
				return;
			}

			double firePower = min(energy,
					min(max(bulletCmd.getPower(), Rules.MIN_BULLET_POWER), Rules.MAX_BULLET_POWER));

			updateEnergy(-firePower);

			gunHeat += Rules.getGunHeat(firePower);

			newBullet = new BulletPeer(this, battleRules, bulletCmd.getBulletId());

			newBullet.setPower(firePower);
			if (!turnedRadarWithGun || !bulletCmd.isFireAssistValid() || statics.isAdvancedRobot()) {
				newBullet.setHeading(gunHeading);
			} else {
				newBullet.setHeading(bulletCmd.getFireAssistAngle());
			}
			newBullet.setX(x);
			newBullet.setY(y);
		}
		// there is only last bullet in one turn
		if (newBullet != null) {
			// newBullet.update(robots, bullets);
			battle.addBullet(newBullet);
		}
	}

	public final void performMove(List<RobotPeer> robots, double zapEnergy) {

		// Reset robot state to active if it is not dead
		if (isDead()) {
			return;
		}

		setState(RobotState.ACTIVE);

		updateGunHeat();

		lastHeading = bodyHeading;
		lastGunHeading = gunHeading;
		lastRadarHeading = radarHeading;
		final double lastX = x;
		final double lastY = y;

		if (!inCollision) {
			updateHeading();
		}

		updateGunHeading();
		updateRadarHeading();
		updateMovement();

		// At this point, robot has turned then moved.
		// We could be touching a wall or another bot...

		// First and foremost, we can never go through a wall:
		checkWallCollision();

		// If this robot is a border sentry robot then check if it hits its "range border"
		if (isSentryRobot()) {
			checkSentryOutsideBorder();
		}

		// Now check for robot collision
		checkRobotCollision(robots);
		
		// Scan false means robot did not call scan() manually.
		// But if we're moving, scan
		if (!scan) {
			scan = (lastHeading != bodyHeading || lastGunHeading != gunHeading || lastRadarHeading != radarHeading
					|| lastX != x || lastY != y);
		}

		if (isDead()) {
			return;
		}

		// zap
		if (zapEnergy != 0) {
			zap(zapEnergy);
		}
	}

	public void performScan(List<RobotPeer> robots) {
		if (isDead()) {
			return;
		}

		turnedRadarWithGun = false;
		// scan
		if (scan) {
			scan(lastRadarHeading, robots);
			turnedRadarWithGun = (lastGunHeading == lastRadarHeading) && (gunHeading == radarHeading);
			scan = false;
		}

		// dispatch messages
		if (statics.isTeamRobot() && teamPeer != null) {
			for (TeamMessage teamMessage : currentCommands.getTeamMessages()) {
				for (RobotPeer member : teamPeer) {
					if (checkDispatchToMember(member, teamMessage.recipient)) {
						member.addTeamMessage(teamMessage);
					}
				}
			}
		}
		currentCommands = null;
		lastHeading = -1;
		lastGunHeading = -1;
		lastRadarHeading = -1;
	}

	private void addTeamMessage(TeamMessage message) {
		final List<TeamMessage> queue = teamMessages.get();

		queue.add(message);
	}

	private boolean checkDispatchToMember(RobotPeer member, String recipient) {
		if (member.isAlive()) {
			if (recipient == null) {
				if (member != this) {
					return true;
				}
			} else {
				final int nl = recipient.length();
				final String currentName = member.statics.getName();

				if ((currentName.length() >= nl && currentName.substring(0, nl).equals(recipient))) {
					return true;
				}

				final String currentClassName = member.statics.getFullClassName();

				if ((currentClassName.length() >= nl && currentClassName.substring(0, nl).equals(recipient))) {
					return true;
				}

			}
		}
		return false;
	}

	public String getNameForEvent(RobotPeer otherRobot) {
		if (battleRules.getHideEnemyNames() && !isTeamMate(otherRobot)) {
			return otherRobot.getAnnonymousName();
		}
		return otherRobot.getName();
	}		

	private void checkRobotCollision(List<RobotPeer> robots) {
		inCollision = false;

		for (RobotPeer otherRobot : robots) {
			if (!(otherRobot == null || otherRobot == this || otherRobot.isDead())
					&& boundingBox.intersects(otherRobot.boundingBox)) {
				// Bounce back
				double angle = atan2(otherRobot.x - x, otherRobot.y - y);

				double movedx = velocity * sin(bodyHeading);
				double movedy = velocity * cos(bodyHeading);

				boolean atFault;
				double bearing = normalRelativeAngle(angle - bodyHeading);

				if ((velocity > 0 && bearing > -PI / 2 && bearing < PI / 2)
						|| (velocity < 0 && (bearing < -PI / 2 || bearing > PI / 2))) {

					inCollision = true;
					atFault = true;
					velocity = 0;
					currentCommands.setDistanceRemaining(0);
					x -= movedx;
					y -= movedy;

					boolean teamFire = (teamPeer != null && teamPeer == otherRobot.teamPeer);

					if (!teamFire && !otherRobot.isSentryRobot()) {
						statistics.scoreRammingDamage(otherRobot.getName());
					}

					this.updateEnergy(-Rules.ROBOT_HIT_DAMAGE);
					otherRobot.updateEnergy(-Rules.ROBOT_HIT_DAMAGE);

					if (otherRobot.energy == 0) {
						if (otherRobot.isAlive()) {
							otherRobot.kill();
							if (!teamFire && !otherRobot.isSentryRobot()) {
								final double bonus = statistics.scoreRammingKill(otherRobot.getName());

								if (bonus > 0) {
									println(
											"SYSTEM: Ram bonus for killing " + this.getNameForEvent(otherRobot) + ": "
											+ (int) (bonus + .5));
								}
							}
						}
					}
					addEvent(
							new HitRobotEvent(getNameForEvent(otherRobot), normalRelativeAngle(angle - bodyHeading),
							otherRobot.energy, atFault));
					otherRobot.addEvent(
							new HitRobotEvent(getNameForEvent(this),
							normalRelativeAngle(PI + angle - otherRobot.getBodyHeading()), energy, false));
				}
			}
		}
		if (inCollision) {
			setState(RobotState.HIT_ROBOT);
		}
	}

	private void checkWallCollision() {
		int minX = 0 + HALF_WIDTH_OFFSET;
		int minY = 0 + HALF_HEIGHT_OFFSET;
		int maxX = (int) getBattleFieldWidth() - HALF_WIDTH_OFFSET;
		int maxY = (int) getBattleFieldHeight() - HALF_HEIGHT_OFFSET;

		boolean hitWall = false;
		double adjustX = 0, adjustY = 0;
		double angle = 0;

		if (x < minX) {
			hitWall = true;
			adjustX = minX - x;
			angle = normalRelativeAngle(3 * PI / 2 - bodyHeading);

		} else if (x > maxX) {
			hitWall = true;
			adjustX = maxX - x;
			angle = normalRelativeAngle(PI / 2 - bodyHeading);

		} else if (y < minY) {
			hitWall = true;
			adjustY = minY - y;
			angle = normalRelativeAngle(PI - bodyHeading);

		} else if (y > maxY) {
			hitWall = true;
			adjustY = maxY - y;
			angle = normalRelativeAngle(-bodyHeading);
		}

		if (hitWall) {
			addEvent(new HitWallEvent(angle));

			// only fix both x and y values if hitting wall at an angle
			if ((bodyHeading % (Math.PI / 2)) != 0) {
				double tanHeading = tan(bodyHeading);

				// if it hits bottom or top wall
				if (adjustX == 0) {
					adjustX = adjustY * tanHeading;
				} // if it hits a side wall
				else if (adjustY == 0) {
					adjustY = adjustX / tanHeading;
				} // if the robot hits 2 walls at the same time (rare, but just in case)
				else if (abs(adjustX / tanHeading) > abs(adjustY)) {
					adjustY = adjustX / tanHeading;
				} else if (abs(adjustY * tanHeading) > abs(adjustX)) {
					adjustX = adjustY * tanHeading;
				}
			}
			x += adjustX;
			y += adjustY;

			if (x < minX) {
				x = minX;
			} else if (x > maxX) {
				x = maxX;
			}
			if (y < minY) {
				y = minY;
			} else if (y > maxY) {
				y = maxY;
			}

			// Update energy, but do not reset inactiveTurnCount
			if (statics.isAdvancedRobot()) {
				setEnergy(energy - Rules.getWallHitDamage(velocity), false);
			}

			updateBoundingBox();

			currentCommands.setDistanceRemaining(0);
			velocity = 0;

			setState(RobotState.HIT_WALL);
		}
	}

	private void checkSentryOutsideBorder() {
		int range = battle.getBattleRules().getSentryBorderSize();

		int minX = range - HALF_WIDTH_OFFSET;
		int minY = range - HALF_HEIGHT_OFFSET;
		int maxX = (int) getBattleFieldWidth() - range + HALF_WIDTH_OFFSET;
		int maxY = (int) getBattleFieldHeight() - range + HALF_HEIGHT_OFFSET;

		boolean hitWall = false;
		double adjustX = 0, adjustY = 0;
		double angle = 0;

		boolean isOutsideBorder = x > minX && x < maxX && y > minY && y < maxY;
		
		if (isOutsideBorder) {
			if ((x - minX) <= Rules.MAX_VELOCITY) {
				hitWall = true;
				adjustX = minX - x;
				angle = normalRelativeAngle(PI / 2 - bodyHeading);

			} else if ((maxX - x) <= Rules.MAX_VELOCITY) {
				hitWall = true;
				adjustX = maxX - x;
				angle = normalRelativeAngle(3 * PI / 2 - bodyHeading);

			} else if ((y - minY) <= Rules.MAX_VELOCITY) {
				hitWall = true;
				adjustY = minY - y;
				angle = normalRelativeAngle(-bodyHeading);

			} else if ((maxY - y) <= Rules.MAX_VELOCITY) {
				hitWall = true;
				adjustY = maxY - y;
				angle = normalRelativeAngle(PI - bodyHeading);
			}
		}

		if (hitWall) {
			addEvent(new HitWallEvent(angle));

			// only fix both x and y values if hitting wall at an angle
			if ((bodyHeading % (Math.PI / 2)) != 0) {
				double tanHeading = tan(bodyHeading);

				// if it hits bottom or top wall
				if (adjustX == 0) {
					adjustX = adjustY * tanHeading;
				} // if it hits a side wall
				else if (adjustY == 0) {
					adjustY = adjustX / tanHeading;
				} // if the robot hits 2 walls at the same time (rare, but just in case)
				else if (abs(adjustX / tanHeading) > abs(adjustY)) {
					adjustY = adjustX / tanHeading;
				} else if (abs(adjustY * tanHeading) > abs(adjustX)) {
					adjustX = adjustY * tanHeading;
				}
			}

			x += adjustX;
			y += adjustY;

			if (isOutsideBorder) {
				if ((x - minX) <= Rules.MAX_VELOCITY) {
					x = minX;
				} else if ((maxX - x) <= Rules.MAX_VELOCITY) {
					x = maxX;
				}
				if ((y - minY) <= Rules.MAX_VELOCITY) {
					y = minY;
				} else if ((maxY - y) <= Rules.MAX_VELOCITY) {
					y = maxY;
				}
			}

			// Update energy, but do not reset inactiveTurnCount
			if (statics.isAdvancedRobot()) {
				setEnergy(energy - Rules.getWallHitDamage(velocity), false);
			}

			updateBoundingBox();

			currentCommands.setDistanceRemaining(0);
			velocity = 0;

			setState(RobotState.HIT_WALL);
		}
	}

	private double getBattleFieldHeight() {
		return battleRules.getBattlefieldHeight();
	}

	private double getBattleFieldWidth() {
		return battleRules.getBattlefieldWidth();
	}

	private void updateBoundingBox() {
		boundingBox.setRect(x - HALF_WIDTH_OFFSET, y - HALF_HEIGHT_OFFSET, WIDTH, HEIGHT);
	}

	// TODO: Only add events to robots that are alive? + Remove checks if the Robot is alive before adding the event?
	public void addEvent(Event event) {
		if (isRunning()) {
			final EventQueue queue = events.get();

			if ((queue.size() > EventManager.MAX_QUEUE_SIZE)
					&& !(event instanceof DeathEvent || event instanceof WinEvent || event instanceof SkippedTurnEvent)) {
				println(
						"Not adding to " + statics.getShortName() + "'s queue, exceeded " + EventManager.MAX_QUEUE_SIZE
						+ " events in queue.");
				// clean up old stuff
				queue.clear(battle.getTime() - EventManager.MAX_EVENT_STACK);
			} else {
				queue.add(event);
			}
		}
	}

	private void updateGunHeading() {
		if (currentCommands.getGunTurnRemaining() > 0) {
			if (currentCommands.getGunTurnRemaining() < Rules.GUN_TURN_RATE_RADIANS) {
				gunHeading += currentCommands.getGunTurnRemaining();
				radarHeading += currentCommands.getGunTurnRemaining();
				if (currentCommands.isAdjustRadarForGunTurn()) {
					currentCommands.setRadarTurnRemaining(
							currentCommands.getRadarTurnRemaining() - currentCommands.getGunTurnRemaining());
				}
				currentCommands.setGunTurnRemaining(0);
			} else {
				gunHeading += Rules.GUN_TURN_RATE_RADIANS;
				radarHeading += Rules.GUN_TURN_RATE_RADIANS;
				currentCommands.setGunTurnRemaining(currentCommands.getGunTurnRemaining() - Rules.GUN_TURN_RATE_RADIANS);
				if (currentCommands.isAdjustRadarForGunTurn()) {
					currentCommands.setRadarTurnRemaining(
							currentCommands.getRadarTurnRemaining() - Rules.GUN_TURN_RATE_RADIANS);
				}
			}
		} else if (currentCommands.getGunTurnRemaining() < 0) {
			if (currentCommands.getGunTurnRemaining() > -Rules.GUN_TURN_RATE_RADIANS) {
				gunHeading += currentCommands.getGunTurnRemaining();
				radarHeading += currentCommands.getGunTurnRemaining();
				if (currentCommands.isAdjustRadarForGunTurn()) {
					currentCommands.setRadarTurnRemaining(
							currentCommands.getRadarTurnRemaining() - currentCommands.getGunTurnRemaining());
				}
				currentCommands.setGunTurnRemaining(0);
			} else {
				gunHeading -= Rules.GUN_TURN_RATE_RADIANS;
				radarHeading -= Rules.GUN_TURN_RATE_RADIANS;
				currentCommands.setGunTurnRemaining(currentCommands.getGunTurnRemaining() + Rules.GUN_TURN_RATE_RADIANS);
				if (currentCommands.isAdjustRadarForGunTurn()) {
					currentCommands.setRadarTurnRemaining(
							currentCommands.getRadarTurnRemaining() + Rules.GUN_TURN_RATE_RADIANS);
				}
			}
		}
		gunHeading = normalAbsoluteAngle(gunHeading);
	}

	private void updateHeading() {
		boolean normalizeHeading = true;

		double turnRate = min(currentCommands.getMaxTurnRate(),
				(.4 + .6 * (1 - (abs(velocity) / Rules.MAX_VELOCITY))) * Rules.MAX_TURN_RATE_RADIANS);

		if (currentCommands.getBodyTurnRemaining() > 0) {
			if (currentCommands.getBodyTurnRemaining() < turnRate) {
				bodyHeading += currentCommands.getBodyTurnRemaining();
				gunHeading += currentCommands.getBodyTurnRemaining();
				radarHeading += currentCommands.getBodyTurnRemaining();
				if (currentCommands.isAdjustGunForBodyTurn()) {
					currentCommands.setGunTurnRemaining(
							currentCommands.getGunTurnRemaining() - currentCommands.getBodyTurnRemaining());
				}
				if (currentCommands.isAdjustRadarForBodyTurn()) {
					currentCommands.setRadarTurnRemaining(
							currentCommands.getRadarTurnRemaining() - currentCommands.getBodyTurnRemaining());
				}
				currentCommands.setBodyTurnRemaining(0);
			} else {
				bodyHeading += turnRate;
				gunHeading += turnRate;
				radarHeading += turnRate;
				currentCommands.setBodyTurnRemaining(currentCommands.getBodyTurnRemaining() - turnRate);
				if (currentCommands.isAdjustGunForBodyTurn()) {
					currentCommands.setGunTurnRemaining(currentCommands.getGunTurnRemaining() - turnRate);
				}
				if (currentCommands.isAdjustRadarForBodyTurn()) {
					currentCommands.setRadarTurnRemaining(currentCommands.getRadarTurnRemaining() - turnRate);
				}
			}
		} else if (currentCommands.getBodyTurnRemaining() < 0) {
			if (currentCommands.getBodyTurnRemaining() > -turnRate) {
				bodyHeading += currentCommands.getBodyTurnRemaining();
				gunHeading += currentCommands.getBodyTurnRemaining();
				radarHeading += currentCommands.getBodyTurnRemaining();
				if (currentCommands.isAdjustGunForBodyTurn()) {
					currentCommands.setGunTurnRemaining(
							currentCommands.getGunTurnRemaining() - currentCommands.getBodyTurnRemaining());
				}
				if (currentCommands.isAdjustRadarForBodyTurn()) {
					currentCommands.setRadarTurnRemaining(
							currentCommands.getRadarTurnRemaining() - currentCommands.getBodyTurnRemaining());
				}
				currentCommands.setBodyTurnRemaining(0);
			} else {
				bodyHeading -= turnRate;
				gunHeading -= turnRate;
				radarHeading -= turnRate;
				currentCommands.setBodyTurnRemaining(currentCommands.getBodyTurnRemaining() + turnRate);
				if (currentCommands.isAdjustGunForBodyTurn()) {
					currentCommands.setGunTurnRemaining(currentCommands.getGunTurnRemaining() + turnRate);
				}
				if (currentCommands.isAdjustRadarForBodyTurn()) {
					currentCommands.setRadarTurnRemaining(currentCommands.getRadarTurnRemaining() + turnRate);
				}
			}
		} else {
			normalizeHeading = false;
		}

		if (normalizeHeading) {
			if (currentCommands.getBodyTurnRemaining() == 0) {
				bodyHeading = normalNearAbsoluteAngle(bodyHeading);
			} else {
				bodyHeading = normalAbsoluteAngle(bodyHeading);
			}
		}
		if (Double.isNaN(bodyHeading)) {
			Logger.realErr.println("HOW IS HEADING NAN HERE");
		}
	}

	private void updateRadarHeading() {
		if (currentCommands.getRadarTurnRemaining() > 0) {
			if (currentCommands.getRadarTurnRemaining() < Rules.RADAR_TURN_RATE_RADIANS) {
				radarHeading += currentCommands.getRadarTurnRemaining();
				currentCommands.setRadarTurnRemaining(0);
			} else {
				radarHeading += Rules.RADAR_TURN_RATE_RADIANS;
				currentCommands.setRadarTurnRemaining(
						currentCommands.getRadarTurnRemaining() - Rules.RADAR_TURN_RATE_RADIANS);
			}
		} else if (currentCommands.getRadarTurnRemaining() < 0) {
			if (currentCommands.getRadarTurnRemaining() > -Rules.RADAR_TURN_RATE_RADIANS) {
				radarHeading += currentCommands.getRadarTurnRemaining();
				currentCommands.setRadarTurnRemaining(0);
			} else {
				radarHeading -= Rules.RADAR_TURN_RATE_RADIANS;
				currentCommands.setRadarTurnRemaining(
						currentCommands.getRadarTurnRemaining() + Rules.RADAR_TURN_RATE_RADIANS);
			}
		}

		radarHeading = normalAbsoluteAngle(radarHeading);
	}

	/**
	 * Updates the robots movement.
	 *
	 * This is Nat Pavasants method described here:
	 *   http://robowiki.net/wiki/User:Positive/Optimal_Velocity#Nat.27s_updateMovement
	 */
	private void updateMovement() {
		double distance = currentCommands.getDistanceRemaining();

		if (Double.isNaN(distance)) {
			distance = 0;
		}

		velocity = getNewVelocity(velocity, distance);

		// If we are over-driving our distance and we are now at velocity=0
		// then we stopped.
		if (isNear(velocity, 0) && isOverDriving) {
			currentCommands.setDistanceRemaining(0);
			distance = 0;
			isOverDriving = false;
		}

		// If we are moving normally and the breaking distance is more
		// than remaining distance, enabled the overdrive flag.
		if (Math.signum(distance * velocity) != -1) {
			if (getDistanceTraveledUntilStop(velocity) > Math.abs(distance)) {
				isOverDriving = true;
			} else {
				isOverDriving = false;
			}
		}

		currentCommands.setDistanceRemaining(distance - velocity);

		if (velocity != 0) {
			x += velocity * sin(bodyHeading);
			y += velocity * cos(bodyHeading);
			updateBoundingBox();
		}
	}

	private double getDistanceTraveledUntilStop(double velocity) {
		double distance = 0;

		velocity = Math.abs(velocity);
		while (velocity > 0) {
			distance += (velocity = getNewVelocity(velocity, 0));
		}
		return distance;
	}

	/**
	 * Returns the new velocity based on the current velocity and distance to move.
	 *
	 * @param velocity the current velocity
	 * @param distance the distance to move
	 * @return the new velocity based on the current velocity and distance to move
	 * 
	 * This is Patrick Cupka (aka Voidious), Julian Kent (aka Skilgannon), and Positive's method described here:
	 *   http://robowiki.net/wiki/User:Voidious/Optimal_Velocity#Hijack_2
	 */
	private double getNewVelocity(double velocity, double distance) {
		if (distance < 0) {
			// If the distance is negative, then change it to be positive
			// and change the sign of the input velocity and the result
			return -getNewVelocity(-velocity, -distance);
		}

		final double goalVel;

		if (distance == Double.POSITIVE_INFINITY) {
			goalVel = currentCommands.getMaxVelocity();
		} else {
			goalVel = Math.min(getMaxVelocity(distance), currentCommands.getMaxVelocity());
		}

		if (velocity >= 0) {
			return Math.max(velocity - Rules.DECELERATION, Math.min(goalVel, velocity + Rules.ACCELERATION));
		}
		// else
		return Math.max(velocity - Rules.ACCELERATION, Math.min(goalVel, velocity + maxDecel(-velocity)));
	}

	private final static double getMaxVelocity(double distance) {
		final double decelTime = Math.max(1, Math.ceil(// sum of 0... decelTime, solving for decelTime using quadratic formula
				(Math.sqrt((4 * 2 / Rules.DECELERATION) * distance + 1) - 1) / 2));

		if (decelTime == Double.POSITIVE_INFINITY) {
			return Rules.MAX_VELOCITY;
		}

		final double decelDist = (decelTime / 2.0) * (decelTime - 1) // sum of 0..(decelTime-1)
				* Rules.DECELERATION;

		return ((decelTime - 1) * Rules.DECELERATION) + ((distance - decelDist) / decelTime);
	}

	private static double maxDecel(double speed) {
		double decelTime = speed / Rules.DECELERATION;
		double accelTime = (1 - decelTime);

		return Math.min(1, decelTime) * Rules.DECELERATION + Math.max(0, accelTime) * Rules.ACCELERATION;
	}

	private void updateGunHeat() {
		gunHeat -= battleRules.getGunCoolingRate();
		if (gunHeat < 0) {
			gunHeat = 0;
		}
	}

	private void scan(double lastRadarHeading, List<RobotPeer> robots) {
		if (statics.isDroid()) {
			return;
		}

		double startAngle = lastRadarHeading;
		double scanRadians = getRadarHeading() - startAngle;

		// Check if we passed through 360
		if (scanRadians < -PI) {
			scanRadians = 2 * PI + scanRadians;
		} else if (scanRadians > PI) {
			scanRadians = scanRadians - 2 * PI;
		}

		// In our coords, we are scanning clockwise, with +y up
		// In java coords, we are scanning counterclockwise, with +y down
		// All we need to do is adjust our angle by -90 for this to work.
		startAngle -= PI / 2;

		startAngle = normalAbsoluteAngle(startAngle);

		scanArc.setArc(x - Rules.RADAR_SCAN_RADIUS, y - Rules.RADAR_SCAN_RADIUS, 2 * Rules.RADAR_SCAN_RADIUS,
				2 * Rules.RADAR_SCAN_RADIUS, 180.0 * startAngle / PI, 180.0 * scanRadians / PI, Arc2D.PIE);

		for (RobotPeer otherRobot : robots) {
			if (!(otherRobot == null || otherRobot == this || otherRobot.isDead())
					&& intersects(scanArc, otherRobot.boundingBox)) {
				double dx = otherRobot.x - x;
				double dy = otherRobot.y - y;
				double angle = atan2(dx, dy);
				double dist = Math.hypot(dx, dy);

				final ScannedRobotEvent event = new ScannedRobotEvent(getNameForEvent(otherRobot), otherRobot.energy,
						normalRelativeAngle(angle - getBodyHeading()), dist, otherRobot.getBodyHeading(),
						otherRobot.getVelocity(), otherRobot.isSentryRobot());

				addEvent(event);
			}
		}
	}

	private boolean intersects(Arc2D arc, Rectangle2D rect) {
		return (rect.intersectsLine(arc.getCenterX(), arc.getCenterY(), arc.getStartPoint().getX(),
				arc.getStartPoint().getY()))
				|| arc.intersects(rect);
	}

	private void zap(double zapAmount) {
		if (energy == 0) {
			kill();
			return;
		}
		energy -= abs(zapAmount);
		if (energy < .1) {
			energy = 0;
			currentCommands.setDistanceRemaining(0);
			currentCommands.setBodyTurnRemaining(0);
		}
	}

	public void setRunning(boolean value) {
		isRunning.set(value);
	}

	public void drainEnergy() {
		setEnergy(0, true);
		isEnergyDrained = true;
	}

	public void punishBadBehavior(BadBehavior badBehavior) {
		kill(); // Bug fix [2828479] - Missed onRobotDeath events

		statistics.setInactive();

		final IRobotItem repositoryItem = (IRobotItem) HiddenAccess.getFileSpecification(robotSpecification);

		StringBuffer message = new StringBuffer(getName()).append(' ');

		boolean disableInRepository = false; // Per default, robots are not disabled in the repository

		switch (badBehavior) {
		case CANNOT_START:
			message.append("could not be started or loaded.");
			disableInRepository = true; // Disable in repository when it cannot be started anyways
			break;

		case UNSTOPPABLE:
			message.append("cannot be stopped.");
			break;

		case SKIPPED_TOO_MANY_TURNS:
			message.append("has skipped too many turns.");
			break;

		case SECURITY_VIOLATION:
			message.append("has caused a security violation.");
			disableInRepository = true; // No mercy here!
			break;
		}

		if (disableInRepository) {
			repositoryItem.setValid(false);			
			message.append(" This ").append(repositoryItem.isTeam() ? "team" : "robot").append(
					" has been banned and will not be allowed to participate in battles.");
		}

		logMessage(message.toString());
	}

	void updateEnergy(double delta) {
		if ((!isExecFinishedAndDisabled && !isEnergyDrained) || delta < 0) {
			setEnergy(energy + delta, true);
		}
	}

	private void setEnergy(double newEnergy, boolean resetInactiveTurnCount) {
		if (resetInactiveTurnCount && (energy != newEnergy)) {
			battle.resetInactiveTurnCount(energy - newEnergy);
		}
		energy = newEnergy;
		if (energy < .01) {
			energy = 0;
			ExecCommands localCommands = commands.get();

			localCommands.setDistanceRemaining(0);
			localCommands.setBodyTurnRemaining(0);
		}
	}

	public void setWinner(boolean newWinner) {
		isWinner = newWinner;
	}

	public void kill() {
		battle.resetInactiveTurnCount(10.0);
		if (isAlive()) {
			addEvent(new DeathEvent());
			if (statics.isTeamLeader()) {
				for (RobotPeer teammate : teamPeer) {
					if (teammate.isAlive() && teammate != this) {
						teammate.updateEnergy(-30);

						BulletPeer sBullet = new BulletPeer(this, battleRules, 0);
						sBullet.setState(BulletState.HIT_VICTIM);
						sBullet.setX(teammate.x);
						sBullet.setY(teammate.y);
						sBullet.setVictim(teammate);
						sBullet.setPower(4);
						battle.addBullet(sBullet);
					}
				}
			}
			battle.registerDeathRobot(this);

			// 'fake' bullet for explosion on self
			final ExplosionPeer fake = new ExplosionPeer(this, battleRules);

			battle.addBullet(fake);
		}
		updateEnergy(-energy);

		setState(RobotState.DEAD);
	}

	public void waitForStop() {
		robotProxy.waitForStopThread();
	}

	/**
	 * Clean things up removing all references to the robot.
	 */
	public void cleanup() {
		battle = null;

		if (robotProxy != null) {
			robotProxy.cleanup();
			robotProxy = null;
		}

		if (statistics != null) {
			statistics.cleanup();
			statistics = null;
		}

		status = null;
		commands = null;
		events = null;
		teamMessages = null;
		bulletUpdates = null;
		statics = null;
		battleRules = null;

		synchronized (proxyText) { // Bug fix #387
			battleText.setLength(0);
			proxyText.setLength(0);
		}
	}

	public Object getGraphicsCalls() {
		return commands.get().getGraphicsCalls();
	}

	public boolean isTryingToPaint() {
		return commands.get().isTryingToPaint();
	}

	public List<DebugProperty> getDebugProperties() {
		return commands.get().getDebugProperties();
	}

	public void publishStatus(long currentTurn) {

		final ExecCommands currentCommands = commands.get();

		int others = battle.countActiveParticipants() - (isDead() || isSentryRobot() ? 0 : 1);
		int numSentries = battle.countActiveSentries();

		RobotStatus stat = HiddenAccess.createStatus(energy, x, y, bodyHeading, gunHeading, radarHeading, velocity,
				currentCommands.getBodyTurnRemaining(), currentCommands.getRadarTurnRemaining(),
				currentCommands.getGunTurnRemaining(), currentCommands.getDistanceRemaining(), gunHeat, others, numSentries,
				battle.getRoundNum(), battle.getNumRounds(), battle.getTime());

		status.set(stat);
	}

	void addBulletStatus(BulletStatus bulletStatus) {
		if (isAlive()) {
			bulletUpdates.get().add(bulletStatus);
		}
	}

	public int compareTo(ContestantPeer cp) {
		double myScore = statistics.getTotalScore();
		double hisScore = cp.getStatistics().getTotalScore();

		if (statistics.isInRound()) {
			myScore += statistics.getCurrentScore();
			hisScore += cp.getStatistics().getCurrentScore();
		}
		if (myScore < hisScore) {
			return -1;
		}
		if (myScore > hisScore) {
			return 1;
		}
		return 0;
	}

	@Override
	public String toString() {
		return statics.getShortName() + "(" + (int) energy + ") X" + (int) x + " Y" + (int) y + " " + state.toString()
				+ (isSleeping() ? " sleeping " : "") + (isRunning() ? " running" : "") + (isHalt() ? " halted" : "");
	}
}
