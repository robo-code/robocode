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


import robocode.manager.HostManager;
import robocode.manager.ThreadManager;
import robocode.peer.RobotPeer;
import robocode.peer.RobotStatics;
import robocode.peer.robot.RobotFileSystemManager;
import robocode.peer.robot.RobotOutputStream;
import robocode.peer.robot.RobotThreadManager;
import robocode.peer.robot.EventManager;
import robocode.robotinterfaces.IBasicRobot;
import robocode.robotinterfaces.peer.IBasicRobotPeer;
import static robocode.io.Logger.logMessage;
import robocode.exception.*;
import robocode.robotpaint.Graphics2DProxy;
import robocode.Event;
import robocode.BattleEndedEvent;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;


/**
 * @author Pavel Savara (original)
 */
public abstract class HostingRobotProxy implements IHostingRobotProxy {
	protected EventManager eventManager;
	protected RobotThreadManager robotThreadManager;
	protected RobotFileSystemManager robotFileSystemManager;
	protected RobotStatics statics;
	protected RobotOutputStream out;
	protected RobotPeer peer;
	protected HostManager hostManager;
	protected IBasicRobot robot;

	HostingRobotProxy(HostManager hostManager, RobotPeer peer, RobotStatics statics) {
		this.peer = peer;
		this.statics = statics;
		this.hostManager = hostManager;

		robotFileSystemManager = new RobotFileSystemManager(this, hostManager.getRobotFilesystemQuota());
		robotFileSystemManager.initializeQuota();

		out = new RobotOutputStream();        
		robotThreadManager = new RobotThreadManager(peer);
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

	public RobotStatics getStatics() {
		return statics;
	}

	// TODO temporary
	public String getRootPackageDirectory() {
		return peer.getRobotClassManager().getRobotClassLoader().getRootPackageDirectory();
	}

	// TODO temporary
	public String getClassDirectory() {
		return peer.getRobotClassManager().getRobotClassLoader().getClassDirectory();
	}

	// TODO temporary
	public RobotFileSystemManager getRobotFileSystemManager() {
		return robotFileSystemManager;
	}

	// -----------
	// battle driven methods
	// -----------

	public void startThread(ThreadManager tm) {
		tm.addThreadGroup(robotThreadManager.getThreadGroup(), peer);
		robotThreadManager.start();
	}

	public void forceStopThread() {
		robotThreadManager.forceStop();
	}

	public void waitForStopThread() {
		robotThreadManager.waitForStop();
	}

	public boolean unsafeLoadRound(ThreadManager threadManager) {
		robot = null;
		Class<?> robotClass;

		try {
			threadManager.setLoadingRobot(peer);
			robotClass = peer.getRobotClassManager().getRobotClass();
			if (robotClass == null) {
				peer.println("SYSTEM: Skipping robot: " + statics.getName());
				return false;
			}
			robot = (IBasicRobot) robotClass.newInstance();
			robot.setOut(getOut());
			robot.setPeer((IBasicRobotPeer) this);
			eventManager.setRobot(robot);
		} catch (IllegalAccessException e) {
			peer.println("SYSTEM: Unable to instantiate this robot: " + e);
			peer.println("SYSTEM: Is your constructor marked public?");
			peer.print(e);
			robot = null;
			logMessage(e);
			return false;
		} catch (Throwable e) {
			peer.println("SYSTEM: An error occurred during initialization of " + statics.getName());
			peer.println("SYSTEM: " + e);
			peer.print(e);
			robot = null;
			logMessage(e);
			return false;
		} finally {
			threadManager.setLoadingRobot(null);
		}
		return true;
	}

	// /


	public abstract void execute();

	public void run() {
		peer.setRunning(true);
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
				execute();
			}
		} catch (WinException e) {// Do nothing
		} catch (AbortedException e) {// Do nothing
		} catch (DeathException e) {
			peer.println("SYSTEM: " + statics.getName() + " has died");
		} catch (DisabledException e) {
			peer.drainEnergy();
			String msg = e.getMessage();

			if (msg == null) {
				msg = "";
			} else {
				msg = ": " + msg;
			}
			peer.println("SYSTEM: Robot disabled" + msg);
		} catch (Exception e) {
			peer.drainEnergy();
			final String message = statics.getName() + ": Exception: " + e;

			peer.print(e);
			logMessage(message);
		} catch (Throwable t) {
			peer.drainEnergy();
			if (!(t instanceof ThreadDeath)) {
				final String message = peer.getName() + ": Throwable: " + t;

				peer.print(t);
				logMessage(message);
			} else {
				logMessage(statics.getName() + " stopped successfully.");
			}
		} finally {
			waitForBattleEndImpl();
		}

		// If battle is waiting for us, well, all done!
		synchronized (this) {
			peer.setRunning(false);
			notifyAll();
		}
	}

	protected abstract void waitForBattleEndImpl();
}
