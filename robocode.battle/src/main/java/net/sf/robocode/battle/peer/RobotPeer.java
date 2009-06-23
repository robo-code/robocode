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
import robocode.control.snapshot.BulletState;
import robocode.control.snapshot.RobotState;
import robocode.exception.AbortedException;
import robocode.exception.DeathException;
import robocode.exception.WinException;
import robocode.util.Utils;
import static robocode.util.Utils.*;

import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import static java.lang.Math.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
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
	private RobotState state;
	private final Arc2D scanArc;
	private List<Arc2D> occludedScan = null;
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
	
	public IExtensionApi getExtensionApi()
	{
		return battle.getCustomRules().getExtensionApi();
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

	//TODO: ensure backwards compatibility
	public Arc2D getScanArc() {
		return scanArc;
	}
	
	public List<Arc2D> getScanArcs() {
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
				getHalt(), shouldWait, isPaintEnabled() || isPaintRecorded);
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
				getHalt(), shouldWait, false);
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

	public void waitSleeping(int millisWait, int microWait) {
		synchronized (isSleeping) {
			// It's quite possible for simple robots to
			// complete their processing before we get here,
			// so we test if the robot is already asleep.

			if (!isSleeping()) {
				try {
					for (int i = millisWait; i > 0 && !isSleeping() && isRunning(); i--) {
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
				punishBadBehavior();
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
		for (RobjectPeer robject : battle.getRobjects())
		{
			if (robject.isRobotStopper() && robject.getBoundaryRect().intersects(getBoundingBox()))	{
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

		for (int i = 0; i < robots.size(); i++) {
			RobotPeer otherRobot = robots.get(i);

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
						statistics.scoreRammingDamage(i);
					}

					this.updateEnergy(-Rules.ROBOT_HIT_DAMAGE);
					otherRobot.updateEnergy(-Rules.ROBOT_HIT_DAMAGE);

					if (otherRobot.energy == 0) {
						if (otherRobot.isAlive()) {
							otherRobot.kill();
							if (!teamFire) {
								final double bonus = statistics.scoreRammingKill(i);

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
		if (battle.getRobjects() == null)
			return;
		
		double angle = 0;
		
		double movedx = velocity * sin(bodyHeading) / 16;
		double movedy = velocity * cos(bodyHeading) / 16;
		
		for (int i = 0; i < battle.getRobjects().size(); i++)
		{
			RobjectPeer obj = battle.getRobjects().get(i);
			if (obj.isRobotConscious() && boundingBox.intersects(obj.getBoundaryRect()))
			{		
				if (obj.isRobotStopper())
				{
					//move until the bounding box is clear of the object 
					while (boundingBox.intersects(obj.getBoundaryRect()) && (movedx != 0 || movedy != 0))
					{
						x -= movedx;
						y -= movedy;
						
						updateBoundingBox();
					}
					
					//figure out which side of the object was hit, using the 
					//updated, intersect-free boundingBox
					if (boundingBox.x <= obj.getX() + obj.getWidth() && 
							boundingBox.y + boundingBox.height < obj.getY())
					{
						//hit the bottom of the object
						angle = normalRelativeAngle(-bodyHeading);
					}
					else if (boundingBox.x < obj.getX() && 
							boundingBox.y <= obj.getY() + obj.getHeight())
					{
						//hit left side of the object
						angle = normalRelativeAngle(Math.PI / 2 - bodyHeading);
					}
					else if (boundingBox.y < obj.getY() + obj.getHeight())
					{
						//hit right side of object
						angle = normalRelativeAngle(Math.PI * 3/2 - bodyHeading);
					}
					else
					{
						//hit top of object
						angle = normalRelativeAngle(Math.PI - bodyHeading);
					}
					
					addEvent(new HitObstacleEvent(angle, obj.getType()));
					
	
					// Update energy, but do not reset inactiveTurnCount
					if (statics.isAdvancedRobot()) {
						setEnergy(energy - Rules.getWallHitDamage(velocity), false);
					}
					
					currentCommands.setDistanceRemaining(0);
					velocity = 0;
				}
				else
				{
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

	private void updateMovement() {
		double distance = currentCommands.getDistanceRemaining();

		if (distance == 0 && velocity == 0) {
			return;
		}
		
		if (Double.isNaN(distance)) {
			distance = 0;
		}

		velocity = getNewVelocity(velocity, distance);

		double dx = velocity * sin(bodyHeading);
		double dy = velocity * cos(bodyHeading);

		x += dx;
		y += dy;

		if (dx != 0 || dy != 0) {
			updateBoundingBox();
		}
		
		if (distance != 0) {
			currentCommands.setDistanceRemaining(distance - velocity);
		}
	}

	/**
	 * Returns the new velocity based on the current velocity and distance to move.
	 *
	 * @param velocity the current velocity
	 * @param distance the distance to move
	 * @return the new velocity based on the current velocity and distance to move
	 */
	private double getNewVelocity(double velocity, double distance) {
		// If the distance is negative, then change it to be positive and change the sign of the input velocity and the result
		if (distance < 0) {
			return -getNewVelocity(-velocity, -distance);
		}

		double newVelocity;

		// Get the speed, which is always positive (because it is a scalar)
		final double speed = Math.abs(velocity); 

		// Check if we are decelerating, i.e. if the velocity is negative.
		// Note that if the speed is too high due to a new max. velocity, we must also decelerate.
		if (velocity < 0 || speed > currentCommands.getMaxVelocity()) {
			// If the velocity is negative, we are decelerating
			newVelocity = speed - Rules.DECELERATION;

			// Check if we are going from deceleration into acceleration
			if (newVelocity < 0) {
				// If we have decelerated to velocity = 0, then the remaining time must be used for acceleration
				double decelTime = speed / Rules.DECELERATION;
				double accelTime = (1 - decelTime);

				// New velocity (v) = d / t, where time = 1 (i.e. 1 turn). Hence, v = d / 1 => v = d
				// However, the new velocity must be limited by the max. velocity
				newVelocity = Math.min(currentCommands.getMaxVelocity(), Math.min(
						Rules.DECELERATION * decelTime * decelTime + Rules.ACCELERATION * 
						accelTime * accelTime,  distance));
								
				// Note: We change the sign here due to the sign check later when returning the result
				velocity *= -1;
			}
		} else {
			// Else, we are not decelerating, but might need to start doing so due to the remaining distance

			// Deceleration time (t) is calculated by: v = a * t => t = v / a
			final double decelTime = speed / Rules.DECELERATION;

			// Deceleration time (d) is calculated by: d = 1/2 a * t^2 + v0 * t + t
			// Adding the extra 't' (in the end) is special for Robocode, and v0 is the starting velocity = 0
			final double decelDist = 0.5 * Rules.DECELERATION * decelTime * decelTime + decelTime;

			// Check if we should start decelerating
			if (distance <= decelDist) {
				// If the distance < max. deceleration distance, we must decelerate so we hit a distance = 0

				// Calculate time left for deceleration to distance = 0
				double time = distance / (decelTime + 1); // 1 is added here due to the extra 't' for Robocode

				// New velocity (v) = a * t, i.e. deceleration * time, but not greater than the current speed

				if (time <= 1) {
					// When there is only one turn left (t <= 1), we set the speed to match the remaining distance
					newVelocity = Math.max(speed - Rules.DECELERATION, distance);
				} else {
					// New velocity (v) = a * t, i.e. deceleration * time
					newVelocity = time * Rules.DECELERATION;

					if (speed < newVelocity) {
						// If the speed is less that the new velocity we just calculated, then use the old speed instead
						newVelocity = speed;
					} else if (speed - newVelocity > Rules.DECELERATION) {
						// The deceleration must not exceed the max. deceleration.
						// Hence, we limit the velocity to the speed minus the max. deceleration.
						newVelocity = speed - Rules.DECELERATION;
					}
				}
			} else {
				// Else, we need to accelerate, but only to max. velocity
				newVelocity = Math.min(speed + Rules.ACCELERATION, currentCommands.getMaxVelocity());
			}
		}

		// 0 <= velocity <= max. velocity
		newVelocity = Math.min(currentCommands.getMaxVelocity(), Math.abs(newVelocity));

		// Return the new velocity with the correct sign. We have been working with the speed, which is always positive
		return (velocity < 0) ? -newVelocity : newVelocity;
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
		double scanRadians = (getRadarHeading() - startAngle);
		
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
		
		
		//TODO: clean this up
		double addedAngle = startAngle + scanRadians;
		if (addedAngle < -PI) {
			addedAngle = 2 * PI + addedAngle;
		} else if (scanRadians > PI) {
			addedAngle = addedAngle - 2 * PI;
		}
		
		scanArc.setArcByCenter(x, y, Rules.RADAR_SCAN_RADIUS,
				180.0 * startAngle / PI, 180.0 * scanRadians / PI, Arc2D.PIE);
//		Arc2D scanArc2 = new Arc2D.Double();
//		scanArc2.setArcByCenter(x, y - Rules.RADAR_SCAN_RADIUS / 2, Rules.RADAR_SCAN_RADIUS / 2,
//				180.0 * (addedAngle) / PI, 180.0 * scanRadians / PI, Arc2D.PIE);

		occludedScan = new ArrayList<Arc2D>();
		occludedScan = occludeScan(scanArc);

		for (RobjectPeer robject : battle.getRobjects()) {
			if (robject.isScannable() && intersects(occludedScan, robject.getBoundaryRect())) {
				double dx = robject.getX() - x;
				double dy = robject.getY() - y;
				double angle = atan2(dx, dy);
				double dist = Math.hypot(dx, dy);
				ScannedObjectEvent event = new ScannedObjectEvent(robject.getType(),
						normalRelativeAngle(angle - getBodyHeading()), dist, robject.isRobotStopper(),
						robject.isBulletStopper(), robject.isScanStopper(), robject.isDynamic());
				
				addEvent(event);
			}
		}
		
		for (RobotPeer otherRobot : robots) {
			if (!(otherRobot == null || otherRobot == this || otherRobot.isDead())
					&& intersects(occludedScan, otherRobot.boundingBox)) {
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
	 * returns a list of scan arcs that are stopped at scan-stopping obstacles
	 * 
	 * @param scanArc
	 * @return
	 */	
	private List<Arc2D> occludeScan(Arc2D scanArc) {
		
		List<Arc2D> arcList = new ArrayList<Arc2D>();		
		Line2D firstScanLine = new Line2D.Double(new Point2D.Double(x, y), scanArc.getStartPoint());
		Line2D secondScanLine = new Line2D.Double(new Point2D.Double(x, y), scanArc.getEndPoint());
		List<ArcTriple> scanRangesList = new ArrayList<ArcTriple>();
		boolean intersectsBothScanLines = false;
		boolean scanIsClockwise = scanArc.getAngleExtent() > 0;
		
		//we only care about objects within the scan arc
		for (RobjectPeer robject : battle.getRobjects())
		{
			if (intersects(scanArc, robject.getBoundaryRect()) && robject.isScanStopper())
			{	
				intersectsBothScanLines = false;
				
				//get the correct corners. If, for whatever reason, the robot is in the middle
				//of a scan-stopping object, nothing will be returned.
				Point2D a = null;
				Point2D b = null;
				
				//get the closest corner of the two not in use. Used when one object 
				//intersects a side of the scan arc.
				Point2D c = null;
				
				//check if it is vertically aligned with the robot
				if (x >= robject.getX() && x <= robject.getX() + robject.getWidth())
				{
					c = null;
					if (y < robject.getY())
					{
						a = new Point2D.Double(robject.getX(), robject.getY());
						b = new Point2D.Double(robject.getX() + robject.getWidth(), robject.getY());
					}
					else if (y > robject.getY() + robject.getHeight())
					{
						a = new Point2D.Double(robject.getX(), robject.getY() + robject.getHeight());
						b = new Point2D.Double(robject.getX() + robject.getWidth(), 
								robject.getY() + robject.getHeight());
					}
				}
				//check if object is horizontally aligned with the robot
				else if (y >= robject.getY() && y <= robject.getY() + robject.getHeight())
				{
					c = null;
					if (x < robject.getX())
					{
						a = new Point2D.Double(robject.getX(), robject.getY());
						b = new Point2D.Double(robject.getX(), robject.getY() + robject.getHeight());
					}
					else if (x > robject.getX() + robject.getWidth())
					{
						a = new Point2D.Double(robject.getX() + robject.getWidth(), robject.getY());
						b = new Point2D.Double(robject.getX() + robject.getWidth(),
								robject.getY() + robject.getHeight());
					}
				}
				//The object is southeast of the robot
				else if (x < robject.getX() && y > robject.getY() + robject.getHeight())
				{
					a = new Point2D.Double(robject.getX(), robject.getY());
					b = new Point2D.Double(robject.getX() + robject.getWidth(),
							robject.getY() + robject.getHeight());
					c = new Point2D.Double(robject.getX(), robject.getY() + robject.getHeight());
				}
				//The object is northeast of the robot
				else if (x < robject.getX() && y < robject.getY())
				{
					a = new Point2D.Double(robject.getX(), robject.getY() + robject.getHeight());
					b = new Point2D.Double(robject.getX() + robject.getWidth(), robject.getY());
					c = new Point2D.Double(robject.getX(), robject.getY());
				}
				//The object is northwest of the robot
				else if (x > robject.getX() + robject.getWidth() && y < robject.getY())
				{
					a = new Point2D.Double(robject.getX(), robject.getY());
					b = new Point2D.Double(robject.getX() + robject.getWidth(), 
							robject.getY() + robject.getHeight());
					c = new Point2D.Double(robject.getX() + robject.getWidth(), robject.getY());
				}
				//The object is southwest of the robot
				else if (x > robject.getX() + robject.getWidth() &&
						y > robject.getY() + robject.getHeight())
				{
					a = new Point2D.Double(robject.getX(), robject.getY() + robject.getHeight());
					b = new Point2D.Double(robject.getX() + robject.getWidth(), robject.getY());
					c = new Point2D.Double(robject.getX() + robject.getWidth(), 
							robject.getY() + robject.getHeight());
				}
				if (a == null || b == null)
				{
					//We're inside a scan-stopping obstacle. Put in a short scan arc and leave.
					arcList.clear();
					Arc2D arc = new Arc2D.Double();
					arc.setArcByCenter(x, y, 15, 5, 5, Arc2D.PIE);
					arcList.add(arc);
					return arcList;
				}
				

				//In case the object intersects with the scan arc
				Line2D intersectingFirstScan, intersectingSecondScan;
				if (c == null)
				{
					//only one face of the object is visible.
					intersectingSecondScan = intersectingFirstScan = new Line2D.Double(a, b);
				}
				else
				{
					//Two faces are visible(a,c and c,b), decide which one to use
					Line2D possibleLine = new Line2D.Double(a, c);
					intersectingFirstScan = (intersectionTwoLines(possibleLine, firstScanLine) != 
						null ?	possibleLine : new Line2D.Double(c, b));
					intersectingSecondScan = (intersectionTwoLines(possibleLine, secondScanLine) != 
						null ?	possibleLine : new Line2D.Double(c, b));
				}
				
				//If the object lies on the fringes of the scan arc, then one 
				//of the points must be outside the original scan arc. Figure out
				//a new point within the scan arc
				if (robject.getBoundaryRect().intersectsLine(firstScanLine))
				{
					if (scanArc.contains(a) && scanArc.contains(b))
					{
						//Do nothing. The boundary line (firstScanLine) taken between 
						//the center and the start point of the scanArc is not the
						//actual boundary.
					}
					else if (!scanArc.contains(a))
					{
						a = intersectionTwoLines(firstScanLine, intersectingFirstScan);
					}
					else
					{
						b = intersectionTwoLines(firstScanLine, intersectingFirstScan);
					}
				}
				if (robject.getBoundaryRect().intersectsLine(secondScanLine))
				{
					if (scanArc.contains(a) && scanArc.contains(b))
					{
						//Do nothing. The boundary line (secondScanLine) taken between 
						//the center and the end point of the scanArc is not the
						//exact actual boundary.
					}
					//'a' might have been set to the first scanArc line / object line intersection. 
					//scanArc.contains would return false in this case
					else if (!(scanArc.contains(a) || firstScanLine.ptSegDist(a) < 1))
					{
						a = intersectionTwoLines(secondScanLine, intersectingSecondScan);
					}
					else
					{
						b = intersectionTwoLines(secondScanLine, intersectingSecondScan);
					}
				}
				if (a == b || a == c || b == c)
				{
					//Corner case: the scan just touches a corner of the object. 
					//Ignore it.
					continue;
				}
				if (!robject.getBoundaryRect().intersectsLine(firstScanLine) &&
					!robject.getBoundaryRect().intersectsLine(secondScanLine) &&
					!(scanArc.contains(a) && scanArc.contains(b)))
				{
					//This handles another literal corner case where the scan lines don't 
					//intersect the object but the scan arc contains exactly one of our points. 
					//Ignore this object.
					continue;
				}
				if (firstScanLine.ptSegDist(a) < 1 && secondScanLine.ptSegDist(b) < 1)
				{
					//The object has both scan lines, set a flag
					intersectsBothScanLines = true;
				}
				
				//create an arcTriple, from points to robot
				double maxRadius;
				double startAngle = 0;
				double extendAngle = 0;
				double angleExtension = 0;
				double adx = a.getX() - x;
				double ady = a.getY() - y;
				double bdx = b.getX() - x;
				double bdy = b.getY() - y;
				double angleA = Math.atan((ady) / (adx));
				double angleB = Math.atan((bdy) / (bdx));
				double radius1 = (ady) / Math.sin(angleA);
				double radius2 = (bdy) / Math.sin(angleB);
				maxRadius = (Math.abs(radius1) > Math.abs(radius2) ? radius1 : radius2);
				
				//Decide which point to start our arc at and which to extend to.
				if (!intersectsBothScanLines)
				{
					if (Math.abs(adx - bdx) < Utils.NEAR_DELTA)
					{
						//both points are on a vertical line
						if (adx < 0)
						{
							//line is to the left 
							//the start point is the lower point if the scan is clockwise (angleExtent > 0)
							//the start point is the higher point if the scan is counterclockwise (angleExtent < 0)
							if ((ady < bdy && scanIsClockwise) || (ady > bdy && !scanIsClockwise))
							{
								startAngle = angleA;
								extendAngle = angleB;
							}
							else
							{
								startAngle = angleB;
								extendAngle = angleA;
							}
						}
						else 
						{
							//line is to the right
							//the start point is the higher point if the scan is clockwise (angleExtent > 0)
							//the start point is the lower point if the scan is counterclockwise (angleExtent < 0)
							if ((ady > bdy && scanIsClockwise) || (ady < bdy && !scanIsClockwise))
							{
								startAngle = angleA;
								extendAngle = angleB;
							}
							else
							{
								startAngle = angleB;
								extendAngle = angleA;
							}
						}	
					}
					else
					{
						//Both points are on a horizontal line, or we have scanned the 
						//corner of the object. The same algorithm applies for both cases. 
						if (ady > 0)
						{
							//points are above
							//the start point is the leftmost point if the scan is clockwise (angleExtent > 0)
							//the start point is the rightmost point if the scan is counterclockwise (angleExtent < 0)
							if ((adx < bdx && scanIsClockwise) || (adx > bdx && !scanIsClockwise))
							{
								startAngle = angleA;
								extendAngle = angleB;
							}
							else
							{
								startAngle = angleB;
								extendAngle = angleA;
							}
						}
						else 
						{
							//points are below
							//the start point is the rightmost point if the scan is clockwise (angleExtent > 0)
							//the start point is the leftmost point if the scan is counterclockwise (angleExtent < 0)
							if ((adx > bdx && scanIsClockwise) || (adx < bdx && !scanIsClockwise))
							{
								startAngle = angleA;
								extendAngle = angleB;
							}
							else 
							{
								startAngle = angleB;
								extendAngle = angleA;
							}
						}
					}
					
					//Figure out how long angleExtension should be
					if (angleA * angleB < 0)
					{
						//Compensate for the (-pi/2 < x < pi/2) domain of tangent, which
						//was used to get our angles. 
						if (adx * bdx < 0)
						{
							angleExtension = Math.PI - (Math.abs(startAngle) + Math.abs(extendAngle));
						}
						else
						{
							angleExtension = Math.abs(startAngle) + Math.abs(extendAngle); 
						}
						
					}
					else
					{
						angleExtension = startAngle - extendAngle;
					}
				
					//put start angle (obtained from tangent) in proper quadrant
					if ((angleA == startAngle && adx < 0) ||(angleA != startAngle && bdx < 0)) 
					{
						startAngle = Math.PI - startAngle;
					}
					else 
					{
						startAngle *= -1;
					}
					
					//get things to degrees
					startAngle = startAngle * 180 / Math.PI;
					if (startAngle < 0) startAngle += 360;
					angleExtension = angleExtension * 180 / Math.PI;
					
					//bound by scanArc's boundaries
					if (modAngleGreaterThan(scanArc.getAngleStart(), startAngle, scanIsClockwise))
					{
						angleExtension = startAngle + angleExtension - scanArc.getAngleStart();
						startAngle = scanArc.getAngleStart();
					}
					if (modAngleGreaterThan(startAngle + angleExtension, scanArc.getAngleStart() +
							scanArc.getAngleExtent(), scanIsClockwise) && 
							startAngle + angleExtension != 0)
					{
						angleExtension = scanArc.getAngleStart() + scanArc.getAngleExtent() - startAngle;
					}
					if (angleExtension > 360)
					{
						angleExtension -= 360;
					}
				}	
				else
				{
					//We've hit the same obstacle with both scan lines, make sure 
					//this arc is wide enough.
					//The measured distance between the two intersecting points would 
					//sometimes not cover the entirity of the scan arc, especially
					//when the robot was close to the object. This led to skinny
					//full-range arcs that would pass through robjects.
					angleExtension = scanArc.getAngleExtent();
					startAngle = scanArc.getAngleStart();
				}
				
				scanRangesList.add(new ArcTriple(startAngle, angleExtension, 
						Math.abs(maxRadius)));
			}
		}
			
		scanRangesList.add(new ArcTriple(scanArc.getAngleStart(), 
				scanArc.getAngleExtent(), scanArc.getHeight() / 2));
		
		//sort and trim
		Collections.sort(scanRangesList);
		for (int arcIndex = 0; arcIndex < scanRangesList.size(); arcIndex++)
		{
			boolean finishedSearch = false;
			ArcTriple startArc = scanRangesList.get(arcIndex);
			
			//More inaccuracy handling. If the current arc is close to the original
			//scan arc, expand it. In practice, I've found the difference could be
			//as great as .25 of a degree 
			if (Math.abs(startArc.StartAngle - scanArc.getAngleStart()) < .3)
			{
				startArc.StartAngle = scanArc.getAngleStart();
			}
			if (Math.abs(startArc.StartAngle + startArc.AngleExtension - 
					(scanArc.getAngleStart() + scanArc.getAngleExtent())) < .3)
			{
				startArc.AngleExtension = -startArc.StartAngle + 
					scanArc.getAngleExtent() + scanArc.getAngleStart();
			}
			
			//compare this arc to other arcs
			for (int searchIndex = 0; searchIndex < scanRangesList.size() &&
				!finishedSearch; searchIndex++)
			{
				ArcTriple searchingArc = scanRangesList.get(searchIndex);
				if (startArc.equals(searchingArc) || startArc.Radius > searchingArc.Radius)
				{
					continue;
				}

				//searchingArc starts and ends in the middle of startArc,
				//and is a longer arc.
				if (!modAngleGreaterThan(startArc.StartAngle, searchingArc.StartAngle, scanIsClockwise) &&
					!modAngleGreaterThan(searchingArc.StartAngle + searchingArc.AngleExtension, 
							startArc.StartAngle + startArc.AngleExtension, scanIsClockwise) &&
					searchingArc.Radius >= startArc.Radius)
				{
					scanRangesList.remove(searchIndex);
					if (searchIndex < arcIndex)
					{
						arcIndex--;
					}
					searchIndex--;
					
				}
				//searchingArc starts before startArc, ends in the middle, and is longer. 
				else if (modAngleGreaterThan(startArc.StartAngle, searchingArc.StartAngle, scanIsClockwise) &&
						!modAngleGreaterThan(searchingArc.StartAngle + searchingArc.AngleExtension, 
							startArc.StartAngle + startArc.AngleExtension, scanIsClockwise) &&
						modAngleGreaterThan(searchingArc.StartAngle + searchingArc.AngleExtension,
							startArc.StartAngle, scanIsClockwise) &&	
						searchingArc.Radius >= startArc.Radius)
				{
					searchingArc.AngleExtension = startArc.StartAngle - searchingArc.StartAngle;
				}
				//searchingArc starts in the middle of startArc, ends after, and is longer.
				else if (!modAngleGreaterThan(searchingArc.StartAngle, startArc.StartAngle + 
							startArc.AngleExtension, scanIsClockwise) && 
						!modAngleGreaterThan(startArc.StartAngle, searchingArc.StartAngle, scanIsClockwise) &&	
						modAngleGreaterThan(searchingArc.StartAngle + searchingArc.AngleExtension, 
							startArc.StartAngle + startArc.AngleExtension, scanIsClockwise) &&
						searchingArc.Radius >= startArc.Radius)
				{
					searchingArc.AngleExtension -= (startArc.StartAngle + startArc.AngleExtension - 
							searchingArc.StartAngle);
					searchingArc.StartAngle = startArc.StartAngle + startArc.AngleExtension;
				}
			
				//startArc is contained within searchingArc and has a shorter radius.
				//SearchingArc must be split and this trimming restarted.
				
				//An angleExtension of .3 is necessary to prevent an infinite loop, where a 
				//skinny arc would trim a sliver off a larger arc, then the larger arc would be
				//close enough to scanArc on the next pass through that its angleExtension would
				//be restored, restarting the cycle
				else if (modAngleGreaterThan(startArc.StartAngle, searchingArc.StartAngle, scanIsClockwise) &&
						modAngleGreaterThan(searchingArc.StartAngle + searchingArc.AngleExtension,
							startArc.StartAngle + startArc.AngleExtension, scanIsClockwise) && 
						startArc.Radius < searchingArc.Radius && Math.abs(startArc.AngleExtension) >= .3)	
//				else if (modAngleGreaterThan(startArc.StartAngle, searchingArc.StartAngle, scanIsClockwise)) 
//				{
//					if 	(modAngleGreaterThan(searchingArc.StartAngle + searchingArc.AngleExtension,					
//							startArc.StartAngle + startArc.AngleExtension, scanIsClockwise))
//					{
//						if (startArc.Radius < searchingArc.Radius)
//						{
//							if (Math.abs(startArc.AngleExtension) >= .3)
//							{
				{
								if (scanRangesList.size() > 2 * battle.getRobjects().size())
								{
									//error-tolerance
									break;
								}
								
								double newStart = startArc.StartAngle + startArc.AngleExtension;
								double newExtension = searchingArc.StartAngle + searchingArc.AngleExtension - 
									startArc.StartAngle - startArc.AngleExtension;
								ArcTriple newArc = new ArcTriple(newStart, newExtension, searchingArc.Radius);
								scanRangesList.add(newArc);
								searchingArc.AngleExtension = startArc.StartAngle - searchingArc.StartAngle;
								arcIndex = -1; //will be 0 after outer loop starts again.
								finishedSearch = true;
								
//							}
//						}
//					}
				}
			}
		}
		
		//lose any arc that's too skinny. Due to various floating point approximations,
		//some arcs might exist that shouldn't.
		for (int arcIndex = 0; arcIndex < scanRangesList.size(); arcIndex++)
		{
			if (Math.abs(scanRangesList.get(arcIndex).AngleExtension) < .4)
			{
				scanRangesList.remove(arcIndex);
				arcIndex--;
			}
		}
		
		//create arcs based off of scanRangesList
		for (int arcIndex = 0; arcIndex < scanRangesList.size(); arcIndex++)
		{
			ArcTriple arcTrip = scanRangesList.get(arcIndex);
			Arc2D.Double arc = new Arc2D.Double();
			arc.setArcByCenter(x, y, arcTrip.Radius, arcTrip.StartAngle, 
					arcTrip.AngleExtension, Arc2D.PIE);
			arcList.add(arc);
		}
		
		return arcList;
	}
	
	//This is a struct-like class that contains holds arc information;
	private class ArcTriple implements Comparable<ArcTriple>
	{
		public double StartAngle;
		public double AngleExtension;
		public double Radius;
		//TODO: add endAngle for clarity?
		
		public ArcTriple(double startAngle, double angleExtension, double radius)
		{
			if (startAngle < 0) startAngle += 360;
			StartAngle = startAngle;
			AngleExtension = angleExtension;
			Radius = radius;
		}

		public int compareTo(ArcTriple otherTriple) 
		{
			if (Math.abs(StartAngle - otherTriple.StartAngle) < Utils.NEAR_DELTA) return 0;
			if (modAngleGreaterThan(StartAngle, otherTriple.StartAngle, AngleExtension > 0))
			{
				return 1;
			}
			return -1;
		}
	}
	
	/**
	 * This function takes two angles (0 <= x < 360) and returns true if 
	 * the first angle is greater than the second. A half-circle 
	 * domain is used, so 5 > 355 returns true if clockwise is true.
	 * If clockwise is false, 355 > 5 returns true
	 * 
	 */
	private boolean modAngleGreaterThan(double firstAngle, double secondAngle, boolean clockwise)
	{
		if (Math.abs(firstAngle - secondAngle) > 180)
		{
			if (clockwise)
			{
				return (firstAngle < secondAngle);
			}
			else
			{
				return (firstAngle > secondAngle);
			}
		}
		else
		{
			if (clockwise)
			{
				return (firstAngle > secondAngle);
			}
			else
			{
				return (firstAngle < secondAngle);
			}
		}
	}
	
	/**
	 * Calculates a point on two intersecting lines, else returns null
	 */
	private Point2D.Double intersectionTwoLines(Line2D a, Line2D b)
	{
		//The following function returns the intersection of two lines, not line segments.
		//So we need to check before returning
		Point2D.Double point = intersectionTwoLines(a.getX1(), a.getY1(), a.getX2(), 
			a.getY2(), b.getX1(), b.getY1(), b.getX2(), b.getY2());
		
		if ( a.ptSegDist(point) < .5 && b.ptSegDist(point) < .5)
		{
			return point;
		}
		return null;
	}
	
	/**
	 * Calculates a point on two intersecting lines, else returns null
	 * 
	 * Code by Alexander Hristov, released under LGPL
	 * http://www.ahristov.com/tutorial/geometry-games/intersection-lines.html
	 * 
	 */
	private Point2D.Double intersectionTwoLines(double x1, double y1, double x2,double y2, 
			double x3, double y3, double x4, double y4)
	{
		double d = (x1-x2)*(y3-y4) - (y1-y2)*(x3-x4);
		if (d == 0) return null;
		  
		double xi = ((x3-x4)*(x1*y2-y1*x2)-(x1-x2)*(x3*y4-y3*x4))/d;
		double yi = ((y3-y4)*(x1*y2-y1*x2)-(y1-y2)*(x3*y4-y3*x4))/d;
		    
		return new Point2D.Double(xi,yi);
	}
	
	private boolean intersects(Arc2D arc, Rectangle2D rect) {
		return (rect.intersectsLine(arc.getCenterX(), arc.getCenterY(), arc.getStartPoint().getX(),
			arc.getStartPoint().getY()))
			|| arc.intersects(rect);
	}

	private boolean intersects(List<Arc2D> arcs, Rectangle2D rect) {
		for (Arc2D arc : arcs)
		{
			if ((rect.intersectsLine(arc.getCenterX(), arc.getCenterY(), arc.getStartPoint().getX(),
				arc.getStartPoint().getY()))
				|| arc.intersects(rect))
				return true;
		}
		return false;
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

	public void punishBadBehavior() {
		setState(RobotState.DEAD);
		statistics.setInactive();
		final IRobotRepositoryItem repositoryItem = (IRobotRepositoryItem) HiddenAccess.getFileSpecification(
				robotSpecification);

		// disable for next time, just if is not developed here
		if (!repositoryItem.isDevelopmentVersion()) {
			repositoryItem.setValid(false);
		}
	}

	public void updateEnergy(double delta) {
		if (!isExecFinishedAndDisabled && !isEnergyDrained) {
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

	public void setX(double x)
	{
		this.x = x;
	}
	
	public void setY(double y)
	{
		this.y = y;
	}
	
	public void setWinner(boolean newWinner) {
		isWinner = newWinner;
	}

	public void kill() {
		battle.getCustomRules().robotKill(this);
	}

	public void setIsExecFinishedAndDisabled(boolean value)
	{
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

