/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.host.proxies;


import net.sf.robocode.host.events.EventManager;
import net.sf.robocode.host.io.RobotFileSystemManager;
import net.sf.robocode.host.io.RobotOutputStream;
import net.sf.robocode.host.security.RobotThreadManager;
import net.sf.robocode.host.*;
import static net.sf.robocode.io.Logger.logError;
import static net.sf.robocode.io.Logger.logMessage;
import net.sf.robocode.peer.BadBehavior;
import net.sf.robocode.peer.ExecCommands;
import net.sf.robocode.peer.IRobotPeer;
import net.sf.robocode.repository.IRobotItem;
import net.sf.robocode.core.Container;
import robocode.RobotStatus;
import robocode.exception.AbortedException;
import robocode.exception.DeathException;
import robocode.exception.DisabledException;
import robocode.exception.WinException;
import robocode.robotinterfaces.IBasicRobot;
import robocode.robotinterfaces.peer.IBasicRobotPeer;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


// XXX Remember to update the .NET version whenever a change is made to this class!

/**
 * @author Pavel Savara (original)
 */
abstract class HostingRobotProxy implements IHostingRobotProxy, IHostedThread {

	private final IRobotItem robotSpecification;

	protected EventManager eventManager;
	private final IHostManager hostManager;
	protected RobotThreadManager robotThreadManager;
	protected RobotFileSystemManager robotFileSystemManager;
	private IThreadManager threadManager;

	private IBasicRobot robot;
	protected final IRobotPeer peer;
	protected IRobotClassLoader robotClassLoader;

	protected final RobotStatics statics;
	protected RobotOutputStream out;

	private final Set<String> securityViolations = Collections.synchronizedSet(new HashSet<String>());

	HostingRobotProxy(IRobotItem robotSpecification, IHostManager hostManager, IRobotPeer peer, RobotStatics statics) {
		this.peer = peer;
		this.statics = statics;
		this.hostManager = hostManager;
		this.robotSpecification = robotSpecification;

		robotClassLoader = getHost(robotSpecification).createLoader(robotSpecification);
		robotClassLoader.setRobotProxy(this);

		out = new RobotOutputStream();
		robotThreadManager = new RobotThreadManager(this);

		loadClassBattle();

		robotFileSystemManager = new RobotFileSystemManager(this, hostManager.getRobotFilesystemQuota(),
				robotSpecification.getWritableDirectory(), robotSpecification.getReadableDirectory(),
				robotSpecification.getRootPath());

		robotFileSystemManager.initialize();
	}

	private JavaHost getHost(IRobotItem robotSpecification) {
		return (JavaHost) Container.cache.getComponent("robocode.host." + robotSpecification.getPlatform().toLowerCase());
	}

	public void cleanup() {
		robot = null;

		// Remove the file system and the manager
		robotFileSystemManager = null;
		if (out != null) {
			out.close();
			out = null;
		}

		if (robotThreadManager != null) {
			robotThreadManager.cleanup();
		}
		robotThreadManager = null;

		// Cleanup and remove class manager
		if (robotClassLoader != null) {
			robotClassLoader.cleanup();
			robotClassLoader = null;
		}
	}

	public RobotOutputStream getOut() {
		return out;
	}

	public void println(String s) {
		out.println(s);
	}

	private void println(Throwable ex) {
		ex.printStackTrace(out);
	}

	public RobotStatics getStatics() {
		return statics;
	}

	public RobotFileSystemManager getRobotFileSystemManager() {
		return robotFileSystemManager;
	}
	
	public ClassLoader getRobotClassloader() {
		return (ClassLoader) robotClassLoader;
	}

	// -----------
	// battle driven methods
	// -----------

	protected abstract void initializeRound(ExecCommands commands, RobotStatus status);

	public void startRound(ExecCommands commands, RobotStatus status) {
		initializeRound(commands, status);
		threadManager = ((HostManager) hostManager).getThreadManager();
		robotThreadManager.start(threadManager);
	}

	public void forceStopThread() {
		if (!robotThreadManager.forceStop()) {
			peer.punishBadBehavior(BadBehavior.UNSTOPPABLE);
			peer.setRunning(false);
		}
	}

	public void waitForStopThread() {
		if (!robotThreadManager.waitForStop()) {
			peer.punishBadBehavior(BadBehavior.UNSTOPPABLE);
			peer.setRunning(false);
		}
	}

	private void loadClassBattle() {
		try {
			robotClassLoader.loadRobotMainClass(true);
		} catch (Throwable e) {
			println("SYSTEM: Could not load " + statics.getName() + " : ");
			println(e);
			drainEnergy();
		}
	}

	private boolean loadRobotRound() {
		robot = null;
		try {
			threadManager.setLoadingRobot(this);
			robot = robotClassLoader.createRobotInstance();
			if (robot == null) {
				println("SYSTEM: Skipping robot: " + statics.getName());
				return false;
			}
			robot.setOut(out);
			robot.setPeer((IBasicRobotPeer) this);
			eventManager.setRobot(robot);
		} catch (IllegalAccessException e) {
			println("SYSTEM: Unable to instantiate this robot: " + e);
			println("SYSTEM: Is your constructor marked public?");
			println(e);
			robot = null;
			logError(e);
			return false;
		} catch (Throwable e) {
			println("SYSTEM: An error occurred during initialization of " + statics.getName());
			println("SYSTEM: " + e);
			println(e);
			robot = null;
			logError(e);
			return false;
		} finally {
			threadManager.setLoadingRobot(null);
		}
		return true;
	}

	protected abstract void executeImpl();

	public void run() {
		// Only initialize AWT if we are not running in headless mode.
		// Bugfix [2833271] IllegalThreadStateException with the AWT-Shutdown thread.
		// Read more about headless mode here:
		// http://java.sun.com/developer/technicalArticles/J2SE/Desktop/headless/
		if (System.getProperty("java.awt.headless", "true").equals("false")) {
			robotThreadManager.initAWT();
		}

		if (robotSpecification.isValid() && loadRobotRound()) {
			try {
				if (robot != null) {
					peer.setRunning(true);

					// Process all events for the first turn.
					// This is done as the first robot status event must occur before the robot
					// has started running.
					eventManager.processEvents();

					// Call user code
					callUserCode();
				}
				while (peer.isRunning()) {
					executeImpl();
				}
			} catch (WinException e) {// Do nothing
			} catch (AbortedException e) {// Do nothing
			} catch (DeathException e) {
				println("SYSTEM: " + statics.getName() + " has died");
			} catch (DisabledException e) {
				drainEnergy();

				String msg = e.getMessage();
				if (msg == null) {
					msg = "";
				} else {
					msg = ": " + msg;
				}
				println("SYSTEM: Robot disabled: " + msg);
				logMessage("Robot disabled: " + statics.getName());
			} catch (Exception e) {
				drainEnergy();
				println(e);
				logMessage(statics.getName() + ": Exception: " + e); // without stack here
			} catch (ThreadDeath e) {
				drainEnergy();
				println(e);
				logMessage(statics.getName() + " stopped successfully.");
				throw e; // must be re-thrown in order to stop the thread
			} catch (Throwable t) {
				drainEnergy();
				println(t);
				logMessage(statics.getName() + ": Throwable: " + t); // without stack here
			} finally {
				waitForBattleEndImpl();
			}
		} else {
			drainEnergy();
			peer.punishBadBehavior(BadBehavior.CANNOT_START);
			waitForBattleEndImpl();
		}

		peer.setRunning(false);

		// If battle is waiting for us, well, all done!
		synchronized (this) {
			notifyAll();
		}
	}

	private void callUserCode() {
		Runnable runnable = robot.getRobotRunnable();
		if (runnable != null) {
			runnable.run();
		}
	}

	protected abstract void waitForBattleEndImpl();

	public void drainEnergy() {
		peer.drainEnergy();
	}

	public void punishSecurityViolation(String message) {
		// Prevent unit tests of failing if multiple threads are calling this method in the same time.
		// We only want the a specific type of security violation logged once so we only get one error
		// per security violation.
		synchronized (securityViolations) {
			String key = message;

			if (key.startsWith("Preventing Thread-")) {
				key = key.replaceAll("\\d+", "X");
			}
			if (!securityViolations.contains(key)) {
				securityViolations.add(key);
				logError(message);
				println("SYSTEM: " + message);

				if (securityViolations.size() == 1) {
					peer.drainEnergy();
					peer.punishBadBehavior(BadBehavior.SECURITY_VIOLATION);
				}
			}
		}
	}
}
