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


import static net.sf.robocode.io.Logger.logMessage;
import net.sf.robocode.peer.ExecCommands;
import net.sf.robocode.peer.IRobotPeer;
import robocode.RobotStatus;
import robocode.exception.AbortedException;
import robocode.exception.DeathException;
import robocode.exception.DisabledException;
import robocode.exception.WinException;
import robocode.manager.IHostManager;
import robocode.peer.RobotStatics;
import robocode.peer.robot.EventManager;
import robocode.peer.robot.RobotFileSystemManager;
import robocode.peer.robot.RobotOutputStream;
import robocode.peer.robot.RobotThreadManager;
import robocode.repository.RobotFileSpecification;
import robocode.robotinterfaces.IBasicRobot;
import robocode.robotinterfaces.peer.IBasicRobotPeer;
import robocode.security.RobotClassLoader;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;


/**
 * @author Pavel Savara (original)
 */
public abstract class HostingRobotProxy implements IHostingRobotProxy, IHostedThread {
	protected EventManager eventManager;
	protected RobotThreadManager robotThreadManager;
	protected RobotFileSystemManager robotFileSystemManager;
	private RobotFileSpecification robotSpecification;
	protected RobotClassLoader robotClassLoader;
	protected final RobotStatics statics;
	protected RobotOutputStream out;
	protected final IRobotPeer peer;
	protected final IHostManager hostManager;
	protected IBasicRobot robot;

	HostingRobotProxy(RobotFileSpecification robotSpecification, IHostManager hostManager, IRobotPeer peer, RobotStatics statics) {
		this.peer = peer;
		this.statics = statics;
		this.hostManager = hostManager;
		this.robotSpecification = robotSpecification;
		this.robotClassLoader = new RobotClassLoader(robotSpecification);

		out = new RobotOutputStream();
		robotThreadManager = new RobotThreadManager(this);

		loadClassBattle();

		robotFileSystemManager = new RobotFileSystemManager(this, hostManager.getRobotFilesystemQuota(),
				robotClassLoader.getClassDirectory(), robotClassLoader.getRootPackageDirectory());
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

	// -----------
	// battle driven methods
	// -----------

	protected abstract void initializeRound(ExecCommands commands, RobotStatus status);

	public void startRound(ExecCommands commands, RobotStatus status) {
		initializeRound(commands, status);
		robotThreadManager.start(hostManager.getThreadManager());
	}

	public void forceStopThread() {
		if (!robotThreadManager.forceStop()) {
			peer.punishBadBehavior();
			peer.setRunning(false);
		}
	}

	public void waitForStopThread() {
		if (!robotThreadManager.waitForStop()) {
			peer.punishBadBehavior();
			peer.setRunning(false);
		}
	}

	private void loadClassBattle() {
		try {} catch (Throwable e) {
			println("SYSTEM: Could not load " + statics.getName() + " : ");
			println(e);
			drainEnergy();
		}
	}

	private boolean loadRobotRound() {
		robot = null;
		Class<?> robotClass;

		try {
			hostManager.getThreadManager().setLoadingRobot(this);
			robotClass = robotClassLoader.loadRobotClass();
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
		robotThreadManager.initAWT();

		peer.setRunning(true);

		if (!robotSpecification.isValid() || !loadRobotRound()) {
			drainEnergy();
			peer.punishBadBehavior();
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
}
