/*******************************************************************************
 * Copyright (c) 2001, 2009 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Code cleanup
 *     - Bugfix: updateMovement() checked for distanceRemaining > 1 instead of
 *       distanceRemaining > 0 if slowingDown and moveDirection == -1
 *     - Bugfix: Substituted wait(10000) with wait() in execute() method, so
 *       that robots do not hang when game is paused
 *     - Bugfix: Teleportation when turning the robot to 0 degrees while forcing
 *       the robot towards the bottom
 *     - Added setPaintEnabled() and isPaintEnabled()
 *     - Added setSGPaintEnabled() and isSGPaintEnabled()
 *     - Replaced the colorIndex with bodyColor, gunColor, and radarColor
 *     - Replaced the setColors() with setBodyColor(), setGunColor(), and
 *       setRadarColor()
 *     - Added bulletColor, scanColor, setBulletColor(), and setScanColor() and
 *       removed getColorIndex()
 *     - Optimizations
 *     - Ported to Java 5
 *     - Bugfix: HitRobotEvent.isMyFault() returned false despite the fact that
 *       the robot was moving toward the robot it collides with. This was the
 *       case when distanceRemaining == 0
 *     - Removed isDead field as the robot state is used as replacement
 *     - Added isAlive() method
 *     - Added constructor for creating a new robot with a name only
 *     - Added the set() that copies a RobotRecord into this robot in order to
 *       support the replay feature
 *     - Fixed synchronization issues with several member fields
 *     - Added features to support the new JuniorRobot class
 *     - Added cleanupStaticFields() for clearing static fields on robots
 *     - Added getMaxTurnRate()
 *     - Added turnAndMove() in order to support the turnAheadLeft(),
 *       turnAheadRight(), turnBackLeft(), and turnBackRight() for the
 *       JuniorRobot, which moves the robot in a perfect curve that follows a
 *       circle
 *     - Changed the behaviour of checkRobotCollision() so that HitRobotEvents
 *       are only created and sent to robot when damage do occur. Previously, a
 *       robot could receive HitRobotEvents even when no damage was done
 *     - Renamed scanReset() to rescan()
 *     - Added getStatusEvents()
 *     - Added getGraphicsProxy(), getPaintEvents()
 *     Luis Crespo
 *     - Added states
 *     Titus Chen
 *     - Bugfix: Hit wall and teleporting problems with checkWallCollision()
 *     Robert D. Maupin
 *     - Replaced old collection types like Vector and Hashtable with
 *       synchronized List and HashMap
 *     Nathaniel Troutman
 *     - Added cleanup() method for cleaning up references to internal classes
 *       to prevent circular references causing memory leaks
 *     Pavel Savara
 *     - Re-work of robot interfaces
 *     - hosting related logic moved to robot proxy
 *     - interlocked synchronization
 *     - (almost) minimized surface between RobotPeer and RobotProxy to serializable messages.
 *     Joshua Galecki
 *     - Adding object and extension framework
 *     - Added radar occlusion
 *******************************************************************************/
package net.sf.robocode.battle.peer;


import net.sf.robocode.battle.Battle;
import net.sf.robocode.battle.BoundingRectangle;
import net.sf.robocode.battle.IContestantStatistics;
import net.sf.robocode.battle.IRobotPeerBattle;
import net.sf.robocode.host.IHostManager;
import net.sf.robocode.host.RobotStatics;
import net.sf.robocode.host.events.EventManager;
import net.sf.robocode.host.events.EventQueue;
import net.sf.robocode.host.proxies.IHostingRobotProxy;
import net.sf.robocode.io.Logger;
import static net.sf.robocode.io.Logger.logMessage;
import net.sf.robocode.peer.*;
import net.sf.robocode.repository.IRobotRepositoryItem;
import net.sf.robocode.security.HiddenAccess;
import net.sf.robocode.serialization.RbSerializer;
import robocode.*;
import robocode.control.RandomFactory;
import robocode.control.RobotSpecification;
import robocode.control.snapshot.RobotState;
import robocode.exception.AbortedException;
import robocode.exception.WinException;
import static robocode.util.Utils.*;

import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
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
 * @author Joshua Galecki (contributor)
 */
public final class RobotPeer implements IRobotPeerBattle, IRobotPeer {

	public static final int
			WIDTH = 40,
			HEIGHT = 40;

	private static final int
			HALF_WIDTH_OFFSET = (WIDTH / 2 - 2),
			HALF_HEIGHT_OFFSET = (HEIGHT / 2 - 2);

	private static final int maxSkippedTurns = 30;
	private static final int maxSkippedTurnsWithIO = 240;

	private Battle battle;
	private IContestantStatistics statistics;
	private TeamPeer teamPeer;
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
	private int skippedTurns;

	private boolean scan;
	private boolean turnedRadarWithGun; // last round

	private boolean isIORobot;
	private boolean isPaintRecorded;
	private boolean isPaintEnabled;
	private boolean sgPaintEnabled;

	// waiting for next tick
	private final AtomicBoolean isSleeping = new AtomicBoolean(false);
	private final AtomicBoolean halt = new AtomicBoolean(false);

	private boolean isExecFinishedAndDisabled;
	private boolean isEnergyDrained;
	private boolean isWinner;
	private boolean inCollision;
	private boolean isOverDriving;

	private RobotState state;
	private final Arc2D scanArc;
	private Area occludedScan = null;
	private final BoundingRectangle boundingBox;

	public RobotPeer(Battle battle, IHostManager hostManager, RobotSpecification robotSpecification, 
			int duplicate, TeamPeer team, int index, int contestantIndex, IContestantStatistics stats) {
		super();
		if (team != null) {
			team.add(this);
		}

		this.battle = battle;
		boundingBox = new BoundingRectangle();
		scanArc = new Arc2D.Double();
		teamPeer = team;
		state = RobotState.ACTIVE;
		battleRules = battle.getBattleRules();

		this.robotSpecification = robotSpecification;

		boolean isLeader = teamPeer != null && teamPeer.size() == 1;
		String teamName = team == null ? null : team.getName();
		List<String> teamMembers = team == null ? null : team.getMemberNames(); 

		statics = new RobotStatics(robotSpecification, duplicate, isLeader, battleRules, teamName, teamMembers, index,
				contestantIndex);
		statistics = stats.fakeConstructor(this, battle.getRobotsCount());

		robotProxy = (IHostingRobotProxy) hostManager.createRobotProxy(robotSpecification, statics, this);
	}

	public void println(String s) {
		synchronized (proxyText) {
			battleText.append(s);
			battleText.append("\n");
		}
	}

	public void print(Throwable ex) {
		println(ex.toString());
		StackTraceElement[] trace = ex.getStackTrace();

		for (StackTraceElement aTrace : trace) {
			println("\tat " + aTrace);
		}

		Throwable ourCause = ex.getCause();

		if (ourCause != null) {
			print(ourCause);
		}
	}

	public void printProxy(String s) {
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

	public IContestantStatistics getRobotStatistics() {
		return statistics;
	}

	public IContestantStatistics getStatistics() {
		return statistics;
	}

	public RobotSpecification getRobotSpecification() {
		return robotSpecification;
	}
	
	public Battle getBattle() {
		return battle;
	}

	// -------------------
	// statics 
	// -------------------

	public boolean isDroid() {
		return statics.isDroid();
	}

	public boolean isJuniorRobot() {
		return statics.isJuniorRobot();
	}

	public boolean isInteractiveRobot() {
		return statics.isInteractiveRobot();
	}

	public boolean isPaintRobot() {
		return statics.isPaintRobot();
	}

	public boolean isAdvancedRobot() {
		return statics.isAdvancedRobot();
	}

	public boolean isTeamRobot() {
		return statics.isTeamRobot();
	}

	public String getName() {
		return statics.getName();
	}

	public String getShortName() {
		return statics.getShortName();
	}

	public String getVeryShortName() {
		return statics.getVeryShortName();
	}

	public int getIndex() {
		return statics.getIndex();
	}

	public int getContestIndex() {
		return statics.getContestIndex();
	}

	// -------------------
	// status 
	// -------------------

	public void setPaintEnabled(boolean enabled) {
		isPaintEnabled = enabled;
	}

	public void setPaintRecorded(boolean enabled) {
		isPaintRecorded = enabled;
	}

	public boolean isPaintRecorded() {
		return isPaintRecorded;
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

	public boolean getHalt() {
		return halt.get();
	}

	public void setHalt(boolean value) {
		halt.set(value);
	}

	public BoundingRectangle getBoundingBox() {
		return boundingBox;
	}

	// TODO: ensure backwards compatibility
	public Arc2D getScanArc() {
		return scanArc;
	}
	
	public Area getScanArea() {
		return occludedScan;
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

	// -----------
	// execute
	// -----------

	public ByteBuffer executeImplSerial(ByteBuffer newCommands) throws IOException {
		ExecCommands commands = RbSerializer.deserializeFromBuffer(newCommands);
		final ExecResults results = executeImpl(commands);

		return RbSerializer.serializeToBuffer(results);
	}

	public ByteBuffer waitForBattleEndImplSerial(ByteBuffer newCommands) throws IOException {
		ExecCommands commands = RbSerializer.deserializeFromBuffer(newCommands);
		final ExecResults results = waitForBattleEndImpl(commands);

		return RbSerializer.serializeToBuffer(results);
	}

	public final ExecResults executeImpl(ExecCommands newCommands) {
		validateCommands(newCommands);

		if (!isExecFinishedAndDisabled) {
			// from robot to battle
			commands.set(new ExecCommands(newCommands, true));
			printProxy(newCommands.getOutputText());
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
			battle.getCustomRules().robotIsDead(this);
		}
		if (getHalt()) {
			isExecFinishedAndDisabled = true;
			if (isWinner) {
				throw new WinException();
			} else {
				throw new AbortedException();
			}
		}

		waitForNextRound();

		// from battle to robot
		final ExecCommands resCommands = new ExecCommands(this.commands.get(), false);
		final RobotStatus resStatus = status.get();

		final boolean shouldWait = battle.isAborted() || (battle.isLastRound() && isWinner());

		return new ExecResults(resCommands, resStatus, readoutEvents(), readoutTeamMessages(), readoutBullets(),
				getHalt(), shouldWait, isPaintEnabled() || isPaintRecorded, battle.getCustomRules().getBattlefieldState());
	}

	public final ExecResults waitForBattleEndImpl(ExecCommands newCommands) {
		if (!getHalt()) {
			// from robot to battle
			commands.set(new ExecCommands(newCommands, true));
			printProxy(newCommands.getOutputText());

			waitForNextRound();
		}
		// from battle to robot
		final ExecCommands resCommands = new ExecCommands(this.commands.get(), false);
		final RobotStatus resStatus = status.get();

		final boolean shouldWait = battle.isAborted() || (battle.isLastRound() && !isWinner());

		readoutTeamMessages(); // throw away
		
		return new ExecResults(resCommands, resStatus, readoutEvents(), new ArrayList<TeamMessage>(), readoutBullets(),
				getHalt(), shouldWait, false, battle.getCustomRules().getBattlefieldState());
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

	private void waitForNextRound() {
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

	public void waitWakeupNoWait() {
		synchronized (isSleeping) {
			if (isSleeping()) {
				// Wake up the thread
				isSleeping.notifyAll();
			}
		}
	}

	public void waitSleeping(long millisWait, int microWait) {
		synchronized (isSleeping) {
			// It's quite possible for simple robots to
			// complete their processing before we get here,
			// so we test if the robot is already asleep.

			if (!isSleeping()) {
				try {
					for (long i = millisWait; i > 0 && !isSleeping() && isRunning(); i--) {
						isSleeping.wait(0, 999999);
					}
					if (!isSleeping()) {
						isSleeping.wait(0, microWait);
					}
				} catch (InterruptedException e) {
					// Immediately reasserts the exception by interrupting the caller thread itself
					Thread.currentThread().interrupt();

					logMessage("Wait for " + getName() + " interrupted.");
				}
			}
		}
	}

	public void setSkippedTurns() {

		if (battle.isDebugging() || isPaintEnabled()) {
			skippedTurns = 0;
		} else {
			println("SYSTEM: you skipped turn");
			skippedTurns++;
			events.get().clear(false);
			if (!isDead()) {
				addEvent(new SkippedTurnEvent());
			}

			if ((!isIORobot && (skippedTurns > maxSkippedTurns))
					|| (isIORobot && (skippedTurns > maxSkippedTurnsWithIO))) {
				println("SYSTEM: " + getName() + " has not performed any actions in a reasonable amount of time.");
				println("SYSTEM: No score will be generated.");
				setHalt(true);
				waitWakeupNoWait();
				punishBadBehavior(BadBehavior.SKIPPED_TOO_MANY_TURNS);
				robotProxy.forceStopThread();
			}
		}
	}

	public void initializeRound(List<RobotPeer> robots, double[][] initialRobotPositions) {
		boolean valid = false;

		if (initialRobotPositions != null) {

			if (statics.getIndex() >= 0 && statics.getIndex() < initialRobotPositions.length) {
				double[] pos = initialRobotPositions[statics.getIndex()];

				x = pos[0];
				y = pos[1];
				bodyHeading = pos[2];
				gunHeading = radarHeading = bodyHeading;
				updateBoundingBox();
				valid = validSpot(robots);
			}
		}

		if (!valid) {
			final Random random = RandomFactory.getRandom();

			for (int j = 0; j < 1000; j++) {
				x = RobotPeer.WIDTH + random.nextDouble() * (battleRules.getBattlefieldWidth() - 2 * RobotPeer.WIDTH);
				y = RobotPeer.HEIGHT + random.nextDouble() * (battleRules.getBattlefieldHeight() - 2 * RobotPeer.HEIGHT);
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

		if (statics.isTeamLeader() && statics.isDroid()) {
			energy = 220;
		} else if (statics.isTeamLeader()) {
			energy = 200;
		} else if (statics.isDroid()) {
			energy = 120;
		} else {
			energy = 100;
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

		skippedTurns = 0;

		status = new AtomicReference<RobotStatus>();
		readoutEvents();
		readoutTeamMessages();
		readoutBullets();
		battleText.setLength(0);
		proxyText.setLength(0);

		// Prepare new execution commands, but copy the colors from the last commands.
		// Bugfix [2628217] - Robot Colors don't stick between rounds.
		ExecCommands newExecCommands = new ExecCommands();

		newExecCommands.copyColors(commands.get());
		commands = new AtomicReference<ExecCommands>(newExecCommands);
	}

	private boolean validSpot(List<RobotPeer> robots) {
		for (RobjectPeer robject : battle.getRobjects()) {
			if (robject.isRobotStopper() && robject.getBoundaryRect().intersects(getBoundingBox())) {
				return false;
			}
		}
		for (RobotPeer otherRobot : robots) {
			if (otherRobot != null && otherRobot != this) {
				if (getBoundingBox().intersects(otherRobot.getBoundingBox())) {
					return false;
				}
			}
		}
		return true;
	}

	public void startRound(long waitTime) {
		synchronized (isSleeping) {
			try {
				Logger.logMessage(".", false);

				ExecCommands newExecCommands = new ExecCommands();

				// Copy the colors from the last commands.
				// Bugfix [2628217] - Robot Colors don't stick between rounds.
				newExecCommands.copyColors(commands.get());

				currentCommands = newExecCommands;
				int others = battle.getActiveRobots() - (isAlive() ? 1 : 0);
				RobotStatus stat = HiddenAccess.createStatus(energy, x, y, bodyHeading, gunHeading, radarHeading,
						velocity, currentCommands.getBodyTurnRemaining(), currentCommands.getRadarTurnRemaining(),
						currentCommands.getGunTurnRemaining(), currentCommands.getDistanceRemaining(), gunHeat, others,
						battle.getRoundNum(), battle.getNumRounds(), battle.getTime());

				status.set(stat);
				robotProxy.startRound(currentCommands, stat);

				if (!battle.isDebugging()) {
					// Wait for the robot to go to sleep (take action)
					isSleeping.wait(waitTime / 1000000, (int) (waitTime % 1000000));
				}
			} catch (InterruptedException e) {
				logMessage("Wait for " + getName() + " interrupted.");

				// Immediately reasserts the exception by interrupting the caller thread itself
				Thread.currentThread().interrupt();
			}
		}
		if (!(isSleeping() || battle.isDebugging())) {
			logMessage("\n" + getName() + " still has not started after " + (waitTime / 100000) + " ms... giving up.");
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
		
		// Next check obstacles
		checkObstacleCollisions();

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

					if (!teamFire) {
						statistics.scoreRammingDamage(otherRobot.getName());
					}

					this.updateEnergy(-Rules.ROBOT_HIT_DAMAGE);
					otherRobot.updateEnergy(-Rules.ROBOT_HIT_DAMAGE);

					if (otherRobot.energy == 0) {
						if (otherRobot.isAlive()) {
							otherRobot.kill();
							if (!teamFire) {
								final double bonus = statistics.scoreRammingKill(otherRobot.getName());

								if (bonus > 0) {
									println(
											"SYSTEM: Ram bonus for killing " + otherRobot.getName() + ": "
											+ (int) (bonus + .5));
								}
							}
						}
					}
					addEvent(
							new HitRobotEvent(otherRobot.getName(), normalRelativeAngle(angle - bodyHeading),
							otherRobot.energy, atFault));
					otherRobot.addEvent(
							new HitRobotEvent(getName(), normalRelativeAngle(PI + angle - otherRobot.getBodyHeading()), energy,
							false));
				}
			}
		}
		if (inCollision) {
			setState(RobotState.HIT_ROBOT);
		}
	}

	private void checkObstacleCollisions() {
		if (battle.getRobjects() == null) {
			return;
		}
		
		double angle = 0;
		
		double movedx = velocity * sin(bodyHeading) / 16;
		double movedy = velocity * cos(bodyHeading) / 16;
		
		for (int i = 0; i < battle.getRobjects().size(); i++) {
			RobjectPeer obj = battle.getRobjects().get(i);

			if (obj.isRobotConscious() && boundingBox.intersects(obj.getBoundaryRect())) {		
				if (obj.isRobotStopper()) {
					// move until the bounding box is clear of the object 
					while (boundingBox.intersects(obj.getBoundaryRect()) && (movedx != 0 || movedy != 0)) {
						x -= movedx;
						y -= movedy;
						
						updateBoundingBox();
					}
					
					// figure out which side of the object was hit, using the 
					// updated, intersect-free boundingBox
					if (boundingBox.x <= obj.getX() + obj.getWidth() && boundingBox.y + boundingBox.height < obj.getY()) {
						// hit the bottom of the object
						angle = normalRelativeAngle(-bodyHeading);
					} else if (boundingBox.x < obj.getX() && boundingBox.y <= obj.getY() + obj.getHeight()) {
						// hit left side of the object
						angle = normalRelativeAngle(Math.PI / 2 - bodyHeading);
					} else if (boundingBox.y < obj.getY() + obj.getHeight()) {
						// hit right side of object
						angle = normalRelativeAngle(Math.PI * 3 / 2 - bodyHeading);
					} else {
						// hit top of object
						angle = normalRelativeAngle(Math.PI - bodyHeading);
					}
					
					addEvent(new HitObstacleEvent(angle, obj.getType()));
					
					// Update energy, but do not reset inactiveTurnCount
					if (statics.isAdvancedRobot()) {
						setEnergy(energy - Rules.getWallHitDamage(velocity), false);
					}
					
					currentCommands.setDistanceRemaining(0);
					velocity = 0;
				} else {
					addEvent(new HitObjectEvent(obj.getType()));
				}
				obj.hitByRobot(this);
			}
		}
	}

	private void checkWallCollision() {
		boolean hitWall = false;
		double fixx = 0, fixy = 0;
		double angle = 0;

		if (x > getBattleFieldWidth() - HALF_WIDTH_OFFSET) {
			hitWall = true;
			fixx = getBattleFieldWidth() - HALF_WIDTH_OFFSET - x;
			angle = normalRelativeAngle(PI / 2 - bodyHeading);
		}

		if (x < HALF_WIDTH_OFFSET) {
			hitWall = true;
			fixx = HALF_WIDTH_OFFSET - x;
			angle = normalRelativeAngle(3 * PI / 2 - bodyHeading);
		}

		if (y > getBattleFieldHeight() - HALF_HEIGHT_OFFSET) {
			hitWall = true;
			fixy = getBattleFieldHeight() - HALF_HEIGHT_OFFSET - y;
			angle = normalRelativeAngle(-bodyHeading);
		}

		if (y < HALF_HEIGHT_OFFSET) {
			hitWall = true;
			fixy = HALF_HEIGHT_OFFSET - y;
			angle = normalRelativeAngle(PI - bodyHeading);
		}

		if (hitWall) {
			addEvent(new HitWallEvent(angle));

			// only fix both x and y values if hitting wall at an angle
			if ((bodyHeading % (Math.PI / 2)) != 0) {
				double tanHeading = tan(bodyHeading);

				// if it hits bottom or top wall
				if (fixx == 0) {
					fixx = fixy * tanHeading;
				} // if it hits a side wall
				else if (fixy == 0) {
					fixy = fixx / tanHeading;
				} // if the robot hits 2 walls at the same time (rare, but just in case)
				else if (abs(fixx / tanHeading) > abs(fixy)) {
					fixy = fixx / tanHeading;
				} else if (abs(fixy * tanHeading) > abs(fixx)) {
					fixx = fixy * tanHeading;
				}
			}
			x += fixx;
			y += fixy;

			x = (HALF_WIDTH_OFFSET >= x)
					? HALF_WIDTH_OFFSET
					: ((getBattleFieldWidth() - HALF_WIDTH_OFFSET < x) ? getBattleFieldWidth() - HALF_WIDTH_OFFSET : x);
			y = (HALF_HEIGHT_OFFSET >= y)
					? HALF_HEIGHT_OFFSET
					: ((getBattleFieldHeight() - HALF_HEIGHT_OFFSET < y) ? getBattleFieldHeight() - HALF_HEIGHT_OFFSET : y);

			// Update energy, but do not reset inactiveTurnCount
			if (statics.isAdvancedRobot()) {
				setEnergy(energy - Rules.getWallHitDamage(velocity), false);
			}

			updateBoundingBox();

			currentCommands.setDistanceRemaining(0);
			velocity = 0;
		}
		if (hitWall) {
			setState(RobotState.HIT_WALL);
		}
	}

	private double getBattleFieldHeight() {
		return battleRules.getBattlefieldHeight();
	}

	private double getBattleFieldWidth() {
		return battleRules.getBattlefieldWidth();
	}

	public void updateBoundingBox() {
		boundingBox.setRect(x - WIDTH / 2 + 2, y - HEIGHT / 2 + 2, WIDTH - 4, HEIGHT - 4);
	}

	public void addEvent(Event event) {
		if (isRunning()) {
			final EventQueue queue = events.get();

			if ((queue.size() > EventManager.MAX_QUEUE_SIZE)
					&& !(event instanceof DeathEvent || event instanceof WinEvent || event instanceof SkippedTurnEvent)) {
				println(
						"Not adding to " + statics.getName() + "'s queue, exceeded " + EventManager.MAX_QUEUE_SIZE
						+ " events in queue.");
				// clean up old stuff                
				queue.clear(battle.getTime() - EventManager.MAX_EVENT_STACK);
				return;
			}
			queue.add(event);
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

	final static double getMaxVelocity(double distance) {
		final double decelTime = Math.max(1, Math.ceil(// sum of 0... decelTime, solving for decelTime using quadratic formula
				(Math.sqrt((4 * 2 / Rules.DECELERATION) * distance + 1) - 1) / 2));

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
		
		// TODO: clean this up
		double addedAngle = startAngle + scanRadians;

		if (addedAngle < -PI) {
			addedAngle = 2 * PI + addedAngle;
		} else if (scanRadians > PI) {
			addedAngle = addedAngle - 2 * PI;
		}
		
		scanArc.setArcByCenter(x, y, Rules.RADAR_SCAN_RADIUS, 180.0 * startAngle / PI, 180.0 * scanRadians / PI,
				Arc2D.PIE);

		occludedScan = (Area) calcScanArc(scanArc);

		for (RobjectPeer robject : battle.getRobjects()) {
			if (robject.isScannable() && occludedScan.intersects(robject.getIntersectRect())) {
				final ScannedObjectEvent event = createScannedObjectEvent(robject, occludedScan); 		

				addEvent(event);
			}
		}
		
		for (RobotPeer otherRobot : robots) {
			if (!(otherRobot == null || otherRobot == this || otherRobot.isDead())
					&& occludedScan.intersects(otherRobot.boundingBox)) {
				double dx = otherRobot.x - x;
				double dy = otherRobot.y - y;
				double angle = atan2(dx, dy);
				double dist = Math.hypot(dx, dy);

				final ScannedRobotEvent event = new ScannedRobotEvent(otherRobot.getName(), otherRobot.energy,
						normalRelativeAngle(angle - getBodyHeading()), dist, otherRobot.getBodyHeading(),
						otherRobot.getVelocity());

				addEvent(event);
			}
		}
	}

	/**
	 * This function will find the minimum distance between the scanning robot
	 * and the area of the object being scanned as well as the corresponding angle.
	 * It creates a new ScannedObjectEven;t and returns it.
	 * 
	 * @param robject the object being scanned
	 * @param scan the scan area
	 * @return a ScannedObjectEvent
	 */
	private ScannedObjectEvent createScannedObjectEvent(RobjectPeer robject, Area scan) {
		// Find minimum distance to the scanned area of the object and the angle
		double minDistance = Double.POSITIVE_INFINITY;
		double angle = 0;
		
		// TODO: This will need some heavy optimization
		// get intersection polygon between the robject and the scan arc
		Area intersection = new Area(robject.getIntersectRect());

		intersection.intersect(scan);
		Rectangle2D box = intersection.getBounds2D();

		// get points for each corner of the intersection box and 
		// return the lowest distance to the robot
		Point2D.Double currentLocation = new Point2D.Double(x, y);

		// check if the robot is vertically or horizontally alligned
		if (x >= box.getMinX() && x <= box.getMaxX()) {
			if (y < box.getMinY()) {
				angle = 0;
				minDistance = box.getMinY() - y;
			} else {
				angle = Math.PI;
				minDistance = y - box.getMaxY();
			}
		}
		if (y >= box.getMinY() && y <= box.getMaxY()) {
			if (minDistance < Double.POSITIVE_INFINITY) {
				// We are inside the object
				angle = 0;
				minDistance = 0;
			} else if (x < box.getMinX()) {
				angle = Math.PI / 2;
				minDistance = box.getMinX() - x;
			} else {
				angle = -Math.PI / 2;
				minDistance = x - box.getMaxX();
			}
		}
		
		for (int cornerIndex = 0; cornerIndex < 4; cornerIndex++) {
			double boxX = (cornerIndex < 2 ? box.getMinX() : box.getMaxX());
			double boxY = (cornerIndex % 2 == 0 ? box.getMinY() : box.getMaxY());
			double dx = boxX - x;
			double dy = boxY - y;
			
			Point2D.Double corner = new Point2D.Double(boxX, boxY);

			if (corner.distance(currentLocation) < minDistance) {
				// get absolute angle
				angle = atan(dx / dy);
				if (dx > 0 && dy < 0) {
					angle = Math.PI + angle; 
				} else if (dy < 0) {
					angle = angle - Math.PI;
				}
				
				minDistance = corner.distance(currentLocation);
			}
		}	
		
		// adjust angle to robot's bearing and keep within (-pi < x <= pi)
		angle -= bodyHeading;
		if (angle < -Math.PI) {
			angle += Math.PI * 2;
		}
		if (angle > Math.PI) {
			angle -= Math.PI * 2;
		}
		
		return new ScannedObjectEvent(robject.getType(), angle, minDistance, robject.isRobotStopper(),
				robject.isBulletStopper(), robject.isScanStopper(), robject.isDynamic());

	}

	private Shape calcScanArc(Arc2D arc) {
		
		Area area = new Area(arc);

		for (RobjectPeer robject : battle.getRobjects()) {
			if (robject.isScanStopper()) {
				if (robject.getBoundaryRect().contains(x, y) || robject.getBoundaryRect().contains(x - 1, y - 1)) {
					return new Area();
				}
				area.subtract(new Area(getRectWithShadowShape(robject.getBoundaryRect(), x, y)));
			}
		}
		return area;
	}

	private Shape getRectWithShadowShape(final Rectangle2D rect, final double px, final double py) {
		Polygon poly = new Polygon();

		final double minX = rect.getMinX();
		final double minY = rect.getMinY();
		final double maxX = rect.getMaxX();
		final double maxY = rect.getMaxY();

		final boolean withinXboundary = (px >= minX && px <= maxX);
		final boolean withinYboundary = (py >= minY && py <= maxY);
		
		if (withinXboundary) {
			if (withinYboundary) {
				poly.addPoint((int) (minX + 0.5), (int) (minY + 0.5));
				poly.addPoint((int) (maxX + 0.5), (int) (minY + 0.5));
				poly.addPoint((int) (maxX + 0.5), (int) (maxY + 0.5));
				poly.addPoint((int) (minX + 0.5), (int) (maxY + 0.5));			

			} else { // outside y boundary

				final double y1 = (py <= minY) ? minY : maxY;

				poly.addPoint((int) (minX + 0.5), (int) (y1 + 0.5));
				poly.addPoint((int) (maxX + 0.5), (int) (y1 + 0.5));
	
				Point2D projection = new Point2D.Double();

				calcProjectedPoint(px, py, maxX, y1, projection);
				poly.addPoint((int) (projection.getX() + 0.5), (int) (projection.getY() + 0.5));
	
				calcProjectedPoint(px, py, minX, y1, projection);
				poly.addPoint((int) (projection.getX() + 0.5), (int) (projection.getY() + 0.5));
			}	
		} else { // outside x boundary

			final double x1 = (px <= minX) ? minX : maxX;

			Point2D projection = new Point2D.Double();

			if (withinYboundary) {

				poly.addPoint((int) (x1 + 0.5), (int) (minY + 0.5));			
				poly.addPoint((int) (x1 + 0.5), (int) (maxY + 0.5));
		
				calcProjectedPoint(px, py, x1, maxY, projection);
				poly.addPoint((int) (projection.getX() + 0.5), (int) (projection.getY() + 0.5));
		
				calcProjectedPoint(px, py, x1, minY, projection);
				poly.addPoint((int) (projection.getX() + 0.5), (int) (projection.getY() + 0.5));

			} else { // outside y boundary too
		
				final double x2 = (px <= minX) ? maxX : minX;

				double y1, y2;

				if (py <= minY) {
					y1 = minY;
					y2 = maxY;
				} else {
					y1 = maxY;
					y2 = minY;
				}

				poly.addPoint((int) (x1 + 0.5), (int) (y2 + 0.5));
				poly.addPoint((int) (x1 + 0.5), (int) (y1 + 0.5));
				poly.addPoint((int) (x2 + 0.5), (int) (y1 + 0.5));

				calcProjectedPoint(px, py, x2, y1, projection);
				poly.addPoint((int) (projection.getX() + 0.5), (int) (projection.getY() + 0.5));

				calcProjectedPoint(px, py, x1, y2, projection);
				poly.addPoint((int) (projection.getX() + 0.5), (int) (projection.getY() + 0.5));					
			}		
		}
		return poly;
	}

	private void calcProjectedPoint(final double x1, final double y1, final double x2, final double y2, Point2D result) {
		final double dx = x2 - x1;
		final double dy = y2 - y1;

		if (dx == 0) {
			result.setLocation(x1, (dy >= 0) ? 10000 : -10000);
		} else if (dy == 0) {
			result.setLocation((dx >= 0) ? 10000 : -10000, y1);
		} else {
			if (Math.abs(dx) < Math.abs(dy)) {
				double d = (dx >= 0) ? 10000 : -10000;

				result.setLocation(x1 + d, y1 + d * dy / dx);
			} else {
				double d = (dy >= 0) ? 10000 : -10000;

				result.setLocation(x1 + d * dx / dy, y1 + d);			
			}
		}
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
		setState(RobotState.DEAD);
		statistics.setInactive();

		final IRobotRepositoryItem repositoryItem = (IRobotRepositoryItem) HiddenAccess.getFileSpecification(
				robotSpecification);

		StringBuffer message = new StringBuffer(repositoryItem.getFullClassNameWithVersion()).append(' ');

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

	public void updateEnergy(double delta) {
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

	// TODO: removed final modifier of teamPeer - consider side effects
	public void setTeamPeer(TeamPeer teamPeer) { 
		this.teamPeer = teamPeer;
	}
	
	public void setX(double x) {
		this.x = x;
	}
	
	public void setY(double y) {
		this.y = y;
	}
	
	public void setWinner(boolean newWinner) {
		isWinner = newWinner;
	}

	public void kill() {
		battle.getCustomRules().robotKill(this);
	}

	public void setIsExecFinishedAndDisabled(boolean value) {
		isExecFinishedAndDisabled = value;
	}
	
	public void waitForStop() {
		robotProxy.waitForStopThread();
	}

	/**
	 * Clean things up removing all references to the robot.
	 */
	public void cleanup() {
		if (statistics != null) {
			statistics.cleanup();
			statistics = null;
		}

		battle = null;

		if (robotProxy != null) {
			robotProxy.cleanup();
		}

		// Cleanup robot proxy
		robotProxy = null;

		status = null;
		commands = null;
		events = null;
		teamMessages = null;
		bulletUpdates = null;
		battleText.setLength(0);
		proxyText.setLength(0);
		statics = null;
		battleRules = null;
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
		int others = battle.getActiveRobots() - (isAlive() ? 1 : 0);
		RobotStatus stat = HiddenAccess.createStatus(energy, x, y, bodyHeading, gunHeading, radarHeading, velocity,
				currentCommands.getBodyTurnRemaining(), currentCommands.getRadarTurnRemaining(),
				currentCommands.getGunTurnRemaining(), currentCommands.getDistanceRemaining(), gunHeat, others,
				battle.getRoundNum(), battle.getNumRounds(), battle.getTime());

		status.set(stat);
	}

	public void addBulletStatus(BulletStatus bulletStatus) {
		if (isAlive()) {
			bulletUpdates.get().add(bulletStatus);
		}
	}

	public int compareTo(ContestantPeer cp) {
		double myScore = statistics.getCombinedScore();
		double hisScore = cp.getStatistics().getCombinedScore();

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
				+ (isSleeping() ? " sleeping " : "") + (isRunning() ? " running" : "") + (getHalt() ? " halted" : "");
	}
}

