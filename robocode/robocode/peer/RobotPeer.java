/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
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
 *******************************************************************************/
package robocode.peer;


import robocode.*;
import robocode.Event;
import robocode.battle.Battle;
import robocode.control.RandomFactory;
import robocode.control.RobotSpecification;
import robocode.exception.AbortedException;
import robocode.exception.DeathException;
import robocode.exception.WinException;
import robocode.io.Logger;
import static robocode.io.Logger.logMessage;
import robocode.manager.HostManager;
import robocode.manager.ThreadManager;
import robocode.peer.proxies.*;
import robocode.peer.robot.*;
import robocode.peer.robot.EventQueue;
import robocode.robotpaint.Graphics2DProxy;
import robocode.util.BoundingRectangle;
import static robocode.util.Utils.*;

import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;
import static java.lang.Math.*;
import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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
 */
public final class RobotPeer implements ContestantPeer {

	public static final int
			WIDTH = 40,
			HEIGHT = 40;

	private static final int
			HALF_WIDTH_OFFSET = (WIDTH / 2 - 2),
			HALF_HEIGHT_OFFSET = (HEIGHT / 2 - 2);

	private static final int maxSkippedTurns = 30;
	private static final int maxSkippedTurnsWithIO = 240;

	private Battle battle;
	private RobotStatistics statistics;
	private TeamPeer teamPeer;
	private RobotSpecification controlRobotSpecification;

	private IHostingRobotProxy robotProxy;
	private AtomicReference<RobotStatus> status = new AtomicReference<RobotStatus>();
	private AtomicReference<RobotCommands> commands = new AtomicReference<RobotCommands>();
	private AtomicReference<EventQueue> events = new AtomicReference<EventQueue>(new EventQueue());
	private AtomicReference<List<TeamMessage>> teamMessages = new AtomicReference<List<TeamMessage>>(
			new ArrayList<TeamMessage>());
	private final StringBuilder battleText = new StringBuilder(1024);
	private final StringBuilder proxyText = new StringBuilder(1024);
	private RobotStatics statics;
	private BattleRules battleRules;

	private double energy;
	private double velocity;
	private double bodyHeading;
	private double radarHeading;
	private double gunHeading;
	private double gunHeat;
	private double x;
	private double y;
	private int skippedTurns;

	private boolean slowingDown;
	private int moveDirection;
	private double acceleration;
	private boolean scan;
	private boolean turnedRadarWithGun; // last round

	private boolean isIORobot;
	private boolean paintEnabled;
	private boolean sgPaintEnabled;

	// thread is running
	private boolean isRunning;
	// waiting for next tick
	private boolean isSleeping;
	private boolean isWinner;
	private boolean halt;
	private boolean inCollision;
	private RobotState state;
	private Arc2D scanArc;
	private BoundingRectangle boundingBox;

	public RobotPeer(Battle battle, HostManager hostManager, RobotClassManager robotClassManager, int duplicate, TeamPeer team) {
		super();
		if (team != null) {
			team.add(this);
		}
		this.battle = battle;
		boundingBox = new BoundingRectangle();
		scanArc = new Arc2D.Double();
		teamPeer = team;
		state = RobotState.ACTIVE;
		boolean isLeader = teamPeer != null && teamPeer.size() == 1;

		statistics = new RobotStatistics(this, battle.getRobotsCount());

		statics = new RobotStatics(robotClassManager.getRobotSpecification(), duplicate, isLeader,
				battle.getBattleRules(), team);
		battleRules = battle.getBattleRules();

		controlRobotSpecification = robotClassManager.getControlRobotSpecification(); 

		if (statics.isTeamRobot()) {
			robotProxy = new TeamRobotProxy(robotClassManager, hostManager, this, statics);
		} else if (statics.isAdvancedRobot()) {
			robotProxy = new AdvancedRobotProxy(robotClassManager, hostManager, this, statics);
		} else if (statics.isInteractiveRobot()) {
			robotProxy = new StandardRobotProxy(robotClassManager, hostManager, this, statics);
		} else if (statics.isJuniorRobot()) {
			robotProxy = new JuniorRobotProxy(robotClassManager, hostManager, this, statics);
		} else {
			throw new AccessControlException("Unknown robot type");
		}

		commands.set(new RobotCommands());

	}

	public void println(String s) {
		synchronized (battleText) {
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

	public RobotStatistics getRobotStatistics() {
		return statistics;
	}

	public ContestantStatistics getStatistics() {
		return statistics;
	}

	public RobotSpecification getControlRobotSpecification() {
		return controlRobotSpecification;
	}

	public boolean isBattleRunning() {
		return battle.isRunning();
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

	// -------------------
	// status 
	// -------------------

	public void setPaintEnabled(boolean enabled) {
		paintEnabled = enabled;
	}

	public boolean isPaintEnabled() {
		return paintEnabled;
	}

	public void setSGPaintEnabled(boolean enabled) {
		sgPaintEnabled = enabled;
	}

	public boolean isSGPaintEnabled() {
		return sgPaintEnabled;
	}

	public boolean isIORobot() {
		return isIORobot;
	}

	public void setIORobot(boolean ioRobot) {
		this.isIORobot = ioRobot;
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

	public synchronized boolean isRunning() {
		return isRunning;
	}

	public synchronized void setRunning(boolean running) {
		this.isRunning = running;
	}

	public synchronized boolean isSleeping() {
		return isSleeping;
	}

	public synchronized boolean getHalt() {
		return halt;
	}

	public synchronized void setHalt(boolean halt) {
		this.halt = halt;
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

	public Color getBodyColor() {
		return commands.get().getBodyColor();
	}

	public Color getRadarColor() {
		return commands.get().getRadarColor();
	}

	public Color getGunColor() {
		return commands.get().getGunColor();
	}

	public Color getBulletColor() {
		return commands.get().getBulletColor();
	}

	public Color getScanColor() {
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

	public final ExecResult executeImpl(RobotCommands newCommands) {
		newCommands.validate(this);
        
		// from robot to battle
		commands.set(new RobotCommands(newCommands, true));
		printProxy(newCommands.getOutputText());

		// If we are stopping, yet the robot took action (in onWin or onDeath), stop now.
		if (battle.isAborted()) {
			throw new AbortedException();
		}
		if (isDead()) {
			throw new DeathException();
		}
		if (getHalt()) {
			if (isWinner) {
				throw new WinException();
			} else {
				throw new AbortedException();
			}
		}

		waitForNextRound();

		// from battle to robot
		final RobotCommands resCommands = new RobotCommands(this.commands.get(), false);
		final RobotStatus resStatus = status.get();

		final boolean shouldWait = battle.isAborted() || (battle.isLastRound() && isWinner());

		return new ExecResult(resCommands, resStatus, readoutEvents(), readoutTeamMessages(), getHalt(), shouldWait);
	}

	public final ExecResult waitForBattleEndImpl(RobotCommands newCommands) {
		if (!getHalt()) {
			// from robot to battle
			commands.set(new RobotCommands(newCommands, true));
			printProxy(newCommands.getOutputText());

			waitForNextRound();
		}
		// from battle to robot
		final RobotCommands resCommands = new RobotCommands(this.commands.get(), false);
		final RobotStatus resStatus = status.get();

		final boolean shouldWait = battle.isAborted() || (battle.isLastRound() && !isWinner());

		return new ExecResult(resCommands, resStatus, readoutEvents(), readoutTeamMessages(), getHalt(), shouldWait);
	}

	private List<Event> readoutEvents() {
		return events.getAndSet(new EventQueue());
	}

	private List<TeamMessage> readoutTeamMessages() {
		return teamMessages.getAndSet(new ArrayList<TeamMessage>());
	}

	private void waitForNextRound() {
		synchronized (this) {
			// Notify the battle that we are now asleep.
			// This ends any pending wait() call in battle.runRound().
			// Should not actually take place until we release the lock in wait(), below.
			isSleeping = true;
			notifyAll();
			// Notifying battle that we're asleep
			// Sleeping and waiting for battle to wake us up.
			try {
				wait();
			} catch (InterruptedException e) {
				// We are expecting this to happen when a round is ended!

				// Immediately reasserts the exception by interrupting the caller thread itself
				Thread.currentThread().interrupt();
			}
			isSleeping = false;
			// Notify battle thread, which is waiting in
			// our wakeup() call, to return.
			// It's quite possible, by the way, that we'll be back in sleep (above)
			// before the battle thread actually wakes up
			notifyAll();
		}
	}

	// -----------
	// called on battle thread
	// -----------

	public void initializeRound(List<RobotPeer> robots, double[][] initialRobotPositions) {
		boolean valid = false;

		if (initialRobotPositions != null) {
			int index = robots.indexOf(this);

			if (index >= 0 && index < initialRobotPositions.length) {
				double[] pos = initialRobotPositions[index];

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
		acceleration = velocity = 0;

		if (isTeamLeader() && statics.isDroid()) {
			energy = 220;
		} else if (isTeamLeader()) {
			energy = 200;
		} else if (statics.isDroid()) {
			energy = 120;
		} else {
			energy = 100;
		}
		gunHeat = 3;

		setHalt(false);

		scan = false;

		inCollision = false;

		scanArc.setAngleStart(0);
		scanArc.setAngleExtent(0);
		scanArc.setFrame(-100, -100, 1, 1);

		statistics.initialize();

		robotProxy.setSetCallCount(0);
		robotProxy.setGetCallCount(0);
		skippedTurns = 0;
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

	public void unsafeLoadRound(ThreadManager threadManager) {
		if (!robotProxy.unsafeLoadRound(threadManager)) {
			drainEnergy();
		}
	}

	public void startRound(ThreadManager tm, long waitTime) {
		synchronized (this) {
			try {
				Logger.logMessage(".", false);

				RobotCommands currentCommands = new RobotCommands();
				RobotStatus stat = new RobotStatus(this, currentCommands, battle);

				status.set(stat);
				robotProxy.initializeRound(currentCommands, stat);

				// Start the robot thread
				robotProxy.startThread(tm);

				if (!battle.isDebugging()) {
					// Wait for the robot to go to sleep (take action)
					wait(waitTime / 1000000, (int) (waitTime % 1000000));
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

	public final void update(RobotCommands currentCommands, List<RobotPeer> robots, double zapEnergy) {
		// Reset robot state to active if it is not dead
		if (isDead()) {
			return;
		}

		setState(RobotState.ACTIVE);

		updateGunHeat();

		final double lastHeading = bodyHeading;
		final double lastGunHeading = gunHeading;
		final double lastRadarHeading = radarHeading;
		final double lastX = x;
		final double lastY = y;

		if (!inCollision) {
			updateHeading(currentCommands);
		}

		updateGunHeading(currentCommands);
		updateRadarHeading(currentCommands);
		updateMovement(currentCommands);

		// At this point, robot has turned then moved.
		// We could be touching a wall or another bot...

		// First and foremost, we can never go through a wall:
		checkWallCollision(currentCommands);

		// Now check for robot collision
		checkRobotCollision(currentCommands, robots);

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
			zap(zapEnergy, currentCommands);
		}

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
				final String currentName = member.getName();
				final String currentNonVerName = member.statics.getNonVersionedName();

				if ((currentName.length() >= nl && currentName.substring(0, nl).equals(recipient))
						|| (currentNonVerName.length() >= nl
								&& member.statics.getNonVersionedName().substring(0, nl).equals(recipient))) {
					return true;
				}
			}
		}
		return false;
	}

	private void checkRobotCollision(RobotCommands currentCommands, List<RobotPeer> robots) {
		inCollision = false;

		for (int i = 0; i < robots.size(); i++) {
			RobotPeer otherRobot = robots.get(i);

			if (!(otherRobot == null || otherRobot == this || otherRobot.isDead())
					&& boundingBox.intersects(otherRobot.boundingBox)) {
				// Bounce back
				double angle = atan2(otherRobot.getX() - x, otherRobot.getY() - y);

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

					boolean teamFire = (getTeamPeer() != null && getTeamPeer() == otherRobot.getTeamPeer());

					if (!teamFire) {
						statistics.scoreRammingDamage(i);
					}

					this.updateEnergy(-Rules.ROBOT_HIT_DAMAGE);
					otherRobot.updateEnergy(-Rules.ROBOT_HIT_DAMAGE);

					if (otherRobot.getEnergy() == 0) {
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
							otherRobot.getEnergy(), atFault));
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

	private void checkWallCollision(RobotCommands currentCommands) {
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
			acceleration = 0;
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
			event.setTime(battle.getTime());
			queue.add(event);
		}
	}

	private void updateGunHeading(RobotCommands currentCommands) {
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

	private void updateHeading(RobotCommands currentCommands) {
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
			System.out.println("HOW IS HEADING NAN HERE");
		}
	}

	private void updateRadarHeading(RobotCommands currentCommands) {
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

	private void updateMovement(RobotCommands currentCommands) {
		if (currentCommands.getDistanceRemaining() == 0 && velocity == 0) {
			return;
		}

		if (!slowingDown) {
			// Set moveDir and slow down for move(0)
			if (moveDirection == 0) {
				// On move(0), we're always slowing down.
				slowingDown = true;

				// Pretend we were moving in the direction we're heading,
				if (velocity > 0) {
					moveDirection = 1;
				} else if (velocity < 0) {
					moveDirection = -1;
				} else {
					moveDirection = 0;
				}
			}
		}

		double desiredDistanceRemaining = currentCommands.getDistanceRemaining();

		if (slowingDown) {
			if (moveDirection == 1 && currentCommands.getDistanceRemaining() < 0) {
				desiredDistanceRemaining = 0;
			} else if (moveDirection == -1 && currentCommands.getDistanceRemaining() > 0) {
				desiredDistanceRemaining = 0;
			}
		}
		double slowDownVelocity = (int) ((sqrt(4 * abs(desiredDistanceRemaining) + 1) - 1));

		if (moveDirection == -1) {
			slowDownVelocity = -slowDownVelocity;
		}

		if (!slowingDown) {
			// Calculate acceleration
			if (moveDirection == 1) {
				// Brake or accelerate
				if (velocity < 0) {
					acceleration = Rules.DECELERATION;
				} else {
					acceleration = Rules.ACCELERATION;
				}

				if (velocity + acceleration > slowDownVelocity) {
					slowingDown = true;
				}
			} else if (moveDirection == -1) {
				if (velocity > 0) {
					acceleration = -Rules.DECELERATION;
				} else {
					acceleration = -Rules.ACCELERATION;
				}

				if (velocity + acceleration < slowDownVelocity) {
					slowingDown = true;
				}
			}
		}

		if (slowingDown) {
			// note:  if slowing down, velocity and distanceremaining have same sign
			if (currentCommands.getDistanceRemaining() != 0 && abs(velocity) <= Rules.DECELERATION
					&& abs(currentCommands.getDistanceRemaining()) <= Rules.DECELERATION) {
				slowDownVelocity = currentCommands.getDistanceRemaining();
			}

			double perfectAccel = slowDownVelocity - velocity;

			if (perfectAccel > Rules.DECELERATION) {
				perfectAccel = Rules.DECELERATION;
			} else if (perfectAccel < -Rules.DECELERATION) {
				perfectAccel = -Rules.DECELERATION;
			}

			acceleration = perfectAccel;
		}

		// Calculate velocity
		if (velocity > currentCommands.getMaxVelocity() || velocity < -currentCommands.getMaxVelocity()) {
			acceleration = 0;
		}

		velocity += acceleration;
		if (velocity > currentCommands.getMaxVelocity()) {
			velocity -= min(Rules.DECELERATION, velocity - currentCommands.getMaxVelocity());
		}
		if (velocity < -currentCommands.getMaxVelocity()) {
			velocity += min(Rules.DECELERATION, -velocity - currentCommands.getMaxVelocity());
		}

		double dx = velocity * sin(bodyHeading);
		double dy = velocity * cos(bodyHeading);

		x += dx;
		y += dy;

		boolean updateBounds = false;

		if (dx != 0 || dy != 0) {
			updateBounds = true;
		}

		if (slowingDown && velocity == 0) {
			currentCommands.setDistanceRemaining(0);
			moveDirection = 0;
			slowingDown = false;
			acceleration = 0;
		}

		if (updateBounds) {
			updateBoundingBox();
		}

		currentCommands.setDistanceRemaining(currentCommands.getDistanceRemaining() - velocity);
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

		scanArc.setArc(getX() - Rules.RADAR_SCAN_RADIUS, getY() - Rules.RADAR_SCAN_RADIUS, 2 * Rules.RADAR_SCAN_RADIUS,
				2 * Rules.RADAR_SCAN_RADIUS, 180.0 * startAngle / PI, 180.0 * scanRadians / PI, Arc2D.PIE);

		for (RobotPeer robotPeer : robots) {
			if (!(robotPeer == null || robotPeer == this || robotPeer.isDead())
					&& intersects(scanArc, robotPeer.boundingBox)) {
				double dx = robotPeer.getX() - getX();
				double dy = robotPeer.getY() - getY();
				double angle = atan2(dx, dy);
				double dist = Math.hypot(dx, dy);

				final ScannedRobotEvent event = new ScannedRobotEvent(robotPeer.getName(), robotPeer.getEnergy(),
						normalRelativeAngle(angle - getBodyHeading()), dist, robotPeer.getBodyHeading(),
						robotPeer.getVelocity());

				addEvent(event);
			}
		}
	}

	private boolean intersects(Arc2D arc, Rectangle2D rect) {
		return (rect.intersectsLine(arc.getCenterX(), arc.getCenterY(), arc.getStartPoint().getX(),
				arc.getStartPoint().getY()))
				|| arc.intersects(rect);
	}

	private void zap(double zapAmount, RobotCommands currentCommands) {
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

	public void drainEnergy() {
		setEnergy(0, true);
	}

	public void updateEnergy(double delta) {
		setEnergy(energy + delta, true);
	}

	private void setEnergy(double newEnergy, boolean resetInactiveTurnCount) {
		if (resetInactiveTurnCount && (energy != newEnergy)) {
			battle.resetInactiveTurnCount(energy - newEnergy);
		}
		energy = newEnergy;
		if (energy < .01) {
			energy = 0;
			RobotCommands currentCommands = commands.get();

			currentCommands.setDistanceRemaining(0);
			currentCommands.setBodyTurnRemaining(0);
		}
	}

	public void setWinner(boolean newWinner) {
		isWinner = newWinner;
	}

	public void kill() {
		battle.resetInactiveTurnCount(10.0);
		if (isAlive()) {
			addEvent(new DeathEvent());
			if (isTeamLeader()) {
				for (RobotPeer teammate : teamPeer) {
					if (!(teammate.isDead() || teammate == this)) {
						teammate.updateEnergy(-30);

						Bullet robotBullet = new Bullet(0, teammate.getX(), teammate.getY(), 4, getName());
						BulletPeer sBullet = new BulletPeer(this, battle, robotBullet);

						sBullet.setState(BulletState.HIT_VICTIM);
						sBullet.setX(teammate.getX());
						sBullet.setY(teammate.getY());
						sBullet.setVictim(teammate);
						sBullet.setPower(4);
						robotBullet.setPeer(sBullet);
						battle.addBullet(sBullet);
					}
				}
			}
			battle.registerDeathRobot(this);

			// 'fake' bullet for explosion on self
			Bullet robotBullet = new Bullet(0, getX(), getY(), 1, getName());
			final ExplosionPeer fake = new ExplosionPeer(this, battle, robotBullet);

			battle.addBullet(fake);
			robotBullet.setPeer(fake);
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
	}

	public Graphics2DProxy getGraphics() {
		return commands.get().getGraphicsProxy();
	}

	public void publishStatus(long currentTurn) {
		RobotStatus stat = new RobotStatus(this, commands.get(), battle);

		status.set(stat);

		if (!isDead()) {
			addEvent(new StatusEvent(stat));
			// Add paint event, if robot is a paint robot and its painting is enabled
			if (isPaintRobot() && isPaintEnabled() && currentTurn > 0) {
				addEvent(new PaintEvent());
			}
		}
	}

	public RobotCommands loadCommands(List<RobotPeer> robots, List<BulletPeer> bullets) {
		RobotCommands currentCommands = commands.get();

		fireBullets(currentCommands.getBullets(), robots, bullets);

		if (currentCommands.isScan()) {
			scan = true;
		}

		if (currentCommands.isMoved()) {
			acceleration = 0;
			if (currentCommands.getDistanceRemaining() == 0) {
				moveDirection = 0;
			} else if (currentCommands.getDistanceRemaining() > 0) {
				moveDirection = 1;
			} else {
				moveDirection = -1;
			}
			slowingDown = false;
			currentCommands.setMoved(false);
		}

		return currentCommands;
	}

	private void fireBullets(List<BulletCommand> bulletCommands, List<RobotPeer> robots, List<BulletPeer> bullets) {
		BulletPeer newBullet = null;

		for (BulletCommand bulletCmd : bulletCommands) {
			Bullet bullet = bulletCmd.getBullet();

			if (bullet == null) {
				println("SYSTEM: Bad bullet command");
				continue;
			}
			if (Double.isNaN(bullet.getPower())) {
				println("SYSTEM: You cannot call fire(NaN)");
				continue;
			}
			if (gunHeat > 0 || energy == 0) {
				continue;
			}

			double firePower = min(energy, min(max(bullet.getPower(), Rules.MIN_BULLET_POWER), Rules.MAX_BULLET_POWER));

			updateEnergy(-firePower);

			gunHeat += Rules.getGunHeat(firePower);

			newBullet = new BulletPeer(this, battle, bullet);

			newBullet.setPower(firePower);
			if (!turnedRadarWithGun || !bulletCmd.isFireAssistValid() || statics.isAdvancedRobot()) {
				newBullet.setHeading(gunHeading);
			} else {
				newBullet.setHeading(bulletCmd.getFireAssistAngle());
			}
			newBullet.setX(x);
			newBullet.setY(y);
			bullet.setPeer(newBullet);
		}
		// there is only last bullet in one turn
		if (newBullet != null) {
			newBullet.update(robots, bullets);
			battle.addBullet(newBullet);
		}
	}

	public void waitWakeup() {
		synchronized (this) {
			if (isSleeping) {
				// Wake up the thread
				notifyAll();
				try {
					wait(10000);
				} catch (InterruptedException e) {
					// Immediately reasserts the exception by interrupting the caller thread itself
					Thread.currentThread().interrupt();
				}
			}
		}
	}

	public void waitSleeping(long waitTime, int millisWait) {
		synchronized (this) { 
			// It's quite possible for simple robots to
			// complete their processing before we get here,
			// so we test if the robot is already asleep.

			if (!isSleeping()) {
				try {
					for (int i = millisWait; i > 0 && !isSleeping(); i--) {
						wait(0, 999999);
					}
					if (!isSleeping()) {
						wait(0, (int) (waitTime % 1000000));
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

		if (isSleeping() || !isRunning() || battle.isDebugging()) {
			skippedTurns = 0;
		} else {
			println("SK");
			skippedTurns++;
			events.get().clear(false);
			if (!isDead()) {
				addEvent(new SkippedTurnEvent());
			}

			if ((!isIORobot() && (skippedTurns > maxSkippedTurns))
					|| (isIORobot() && (skippedTurns > maxSkippedTurnsWithIO))) {
				println("SYSTEM: " + getName() + " has not performed any actions in a reasonable amount of time.");
				println("SYSTEM: No score will be generated.");
				getRobotStatistics().setInactive();
				robotProxy.forceStopThread();
			}
		}
	}

	public int compareTo(ContestantPeer cp) {
		double myScore = statistics.getTotalScore();
		double hisScore = cp.getStatistics().getTotalScore();

		if (battle.isRunning()) {
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
		return statics.getShortName() + "(" + (int) getEnergy() + ") X" + (int) getX() + " Y" + (int) getY() + " "
				+ state.toString() + (isSleeping ? " sleeping " : "") + (isRunning ? " running" : "")
				+ (halt ? " halted" : "");
	}
}

