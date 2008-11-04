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


import robocode.exception.AbortedException;
import robocode.exception.DeathException;
import robocode.exception.DisabledException;
import robocode.exception.WinException;
import static robocode.io.Logger.logMessage;
import robocode.manager.HostManager;
import robocode.peer.RobotStatics;
import robocode.peer.IRobotPeer;
import robocode.peer.RobotCommands;
import robocode.peer.robot.*;
import robocode.robotinterfaces.IBasicRobot;
import robocode.robotinterfaces.peer.IBasicRobotPeer;
import robocode.security.RobocodeClassLoader;
import robocode.RobotStatus;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * @author Pavel Savara (original)
 */
public abstract class HostingRobotProxy implements IHostingRobotProxy, IHostedThread {
	protected EventManager eventManager;
	protected RobotThreadManager robotThreadManager;
	protected RobotFileSystemManager robotFileSystemManager;
	protected RobotClassManager robotClassManager;
	protected RobotStatics statics;
	protected RobotOutputStream out;
	protected IRobotPeer peer;
	protected HostManager hostManager;
	protected IBasicRobot robot;

	// thread is running
	private AtomicBoolean isRunning = new AtomicBoolean(false);

	HostingRobotProxy(RobotClassManager robotClassManager, HostManager hostManager, IRobotPeer peer, RobotStatics statics) {
		this.peer = peer;
		this.statics = statics;
		this.hostManager = hostManager;
		this.robotClassManager = robotClassManager;

		out = new RobotOutputStream();
		robotThreadManager = new RobotThreadManager(this);

		loadClassBattle();

		robotFileSystemManager = new RobotFileSystemManager(this, hostManager.getRobotFilesystemQuota(),
				robotClassManager.getRobotClassLoader().getClassDirectory(),
				robotClassManager.getRobotClassLoader().getRootPackageDirectory());
		robotFileSystemManager.initializeQuota();
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
		if (robotClassManager != null) {
			robotClassManager.cleanup();
			robotClassManager = null;
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
		out.println(ex);
	}

	public RobotStatics getStatics() {
		return statics;
	}

	public Class<?> getRobotClass() {
		return robotClassManager.getRobotClass();
	}

	public RobotFileSystemManager getRobotFileSystemManager() {
		return robotFileSystemManager;
	}

	// -----------
	// battle driven methods
	// -----------

	protected abstract void initializeRound(RobotCommands commands, RobotStatus status);
	
	public void startRound(RobotCommands commands, RobotStatus status) {
		initializeRound(commands, status);
		hostManager.getThreadManager().addThreadGroup(robotThreadManager.getThreadGroup(), this);
		robotThreadManager.start();
	}

	public void forceStopThread() {
		if (!robotThreadManager.forceStop()) {
			peer.setInactive();
			isRunning.set(false);
		}
	}

	public boolean waitForStopThread() {
		return robotThreadManager.waitForStop();
	}

	private void loadClassBattle() {
		try {
			Class<?> c;

			String className = robotClassManager.getFullClassName();
			RobocodeClassLoader classLoader = robotClassManager.getRobotClassLoader(); 

			// Pre-load robot classes without security...
			// loadClass WILL NOT LINK the class, so static "cheats" will not work.
			// in the safe robot loader the class is linked.
			if (RobotClassManager.isSecutityOn()) {
				c = classLoader.loadRobotClass(className, true);
			} else {
				c = classLoader.loadClass(className);
			}

			robotClassManager.setRobotClass(c);

		} catch (Throwable e) {
			println("SYSTEM: Could not load " + statics.getName() + " : " + e);
			println(e);
			drainEnergy();
		}
	}

	private boolean loadRobotRound() {
		robot = null;
		Class<?> robotClass;

		try {
			hostManager.getThreadManager().setLoadingRobot(this);
			robotClass = robotClassManager.getRobotClass();
			if (robotClass == null) {
				println("SYSTEM: Skipping robot: " + statics.getName());
				return false;
			}
			robot = (IBasicRobot) robotClass.newInstance();
			robot.setOut(getOut());
			robot.setPeer((IBasicRobotPeer) this);
			eventManager.setRobot(robot);
		} catch (IllegalAccessException e) {
			println("SYSTEM: Unable to instantiate this robot: " + e);
			println("SYSTEM: Is your constructor marked public?");
			println(e);
			robot = null;
			logMessage(e);
			return false;
		} catch (Throwable e) {
			println("SYSTEM: An error occurred during initialization of " + statics.getName());
			println("SYSTEM: " + e);
			println(e);
			robot = null;
			logMessage(e);
			return false;
		} finally {
			hostManager.getThreadManager().setLoadingRobot(null);
		}
		return true;
	}

	// /


	protected abstract void executeImpl();

	public void run() {
		if (!loadRobotRound()) {
			drainEnergy();
			peer.setInactive();
			waitForBattleEndImpl();
			return;
		}

		isRunning.set(true);
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
		} catch (Exception e) {
			drainEnergy();
			final String message = statics.getName() + ": Exception: " + e;

			println(e);
			logMessage(message);
		} catch (Throwable t) {
			drainEnergy();
			if (!(t instanceof ThreadDeath)) {
				final String message = statics.getName() + ": Throwable: " + t;

				println(t);
				logMessage(message);
			} else {
				logMessage(statics.getName() + " stopped successfully.");
			}
		} finally {
			waitForBattleEndImpl();
		}

		// If battle is waiting for us, well, all done!
		synchronized (this) {
			isRunning.set(false);
			notifyAll();
		}
	}

	protected abstract void waitForBattleEndImpl();

	// TODO minimize border crossing between battle and proxy spaces
	public void drainEnergy() {
		peer.drainEnergy();
	}

	// TODO minimize border crossing between battle and proxy spaces
	public boolean isRunning() {
		return isRunning.get();
	}

}
