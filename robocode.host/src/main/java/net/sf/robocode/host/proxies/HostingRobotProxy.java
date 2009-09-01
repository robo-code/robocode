/*******************************************************************************
 * Copyright (c) 2001, 2009 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
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
import net.sf.robocode.repository.IRobotRepositoryItem;
import net.sf.robocode.core.Container;
import robocode.RobotStatus;
import robocode.exception.AbortedException;
import robocode.exception.DeathException;
import robocode.exception.DisabledException;
import robocode.exception.WinException;
import robocode.robotinterfaces.IBasicRobot;
import robocode.robotinterfaces.peer.IBasicRobotPeer;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


/**
 * @author Pavel Savara (original)
 */
public abstract class HostingRobotProxy implements IHostingRobotProxy, IHostedThread {
	protected EventManager eventManager;
	protected RobotThreadManager robotThreadManager;
	protected RobotFileSystemManager robotFileSystemManager;
	private final IRobotRepositoryItem robotSpecification;
	protected IRobotClassLoader robotClassLoader;
	protected final RobotStatics statics;
	protected RobotOutputStream out;
	protected final IRobotPeer peer;
	protected final IHostManager hostManager;
	private IThreadManager threadManager;
	protected IBasicRobot robot;
	private final Set<String> securityViolations = Collections.synchronizedSet(new HashSet<String>());

	HostingRobotProxy(IRobotRepositoryItem robotSpecification, IHostManager hostManager, IRobotPeer peer, RobotStatics statics) {
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
				robotSpecification.getRootFile());

		robotFileSystemManager.initialize();
	}

	private JavaHost getHost(IRobotRepositoryItem robotSpecification) {
		return (JavaHost) Container.cache.getComponent("robocode.host." + robotSpecification.getRobotLanguage());
	}

	public void cleanup() {
		// Clear all static field on the robot (at class level)
		cleanupStaticFields();

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

	private void cleanupStaticFields() {
		if (robot == null) {
			return;
		}

		Field[] fields = new Field[0];

		// This try-catch-throwable must be here, as it is not always possible to get the
		// declared fields without getting a Throwable like java.lang.NoClassDefFoundError.
		try {
			fields = robot.getClass().getDeclaredFields();
		} catch (Throwable t) {// Do nothing
		}

		for (Field f : fields) {
			int m = f.getModifiers();

			if (Modifier.isStatic(m) && !(Modifier.isFinal(m) || f.getType().isPrimitive())) {
				try {
					f.setAccessible(true);
					f.set(robot, null);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public RobotOutputStream getOut() {
		return out;
	}

	public void println(String s) {
		out.println(s);
	}

	public void println(Throwable ex) {
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

		peer.setRunning(true);

		if (!robotSpecification.isValid() || !loadRobotRound()) {
			drainEnergy();
			peer.punishBadBehavior(BadBehavior.CANNOT_START);
			waitForBattleEndImpl();
		} else {
			try {
				if (robot != null) {

					// Process all events for the first turn.
					// This is done as the first robot status event must occur before the robot
					// has started running.
					eventManager.processEvents();

					Runnable runnable = robot.getRobotRunnable();

					if (runnable != null) {
						runnable.run();
					}
				}

				// noinspection InfiniteLoopStatement
				for (;;) {
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
				println("SYSTEM: Robot disabled" + msg);
				logMessage(statics.getName() + "Robot disabled");
			} catch (Exception e) {
				drainEnergy();
				println(e);
				logMessage(statics.getName() + ": Exception: " + e); // without stack here
			} catch (Throwable t) {
				drainEnergy();
				if (t instanceof ThreadDeath) {
					logMessage(statics.getName() + " stopped successfully.");
				} else {
					println(t);
					logMessage(statics.getName() + ": Throwable: " + t); // without stack here
				}
			} finally {
				waitForBattleEndImpl();
			}
		}

		// If battle is waiting for us, well, all done!
		synchronized (this) {
			peer.setRunning(false);
			notifyAll();
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
			if (securityViolations.contains(message)) {
				return;
			}
			securityViolations.add(message);
		}

		logError(message);
		println("SYSTEM: " + message);
		peer.punishBadBehavior(BadBehavior.SECURITY_VIOLATION);
	}
}
