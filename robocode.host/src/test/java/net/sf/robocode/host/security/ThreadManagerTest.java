/*
 * Copyright (c) 2001-2026 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.host.security;


import net.sf.robocode.host.IHostedThread;
import net.sf.robocode.host.RobotStatics;
import net.sf.robocode.host.io.RobotFileSystemManager;
import net.sf.robocode.repository.IRobotItem;
import net.sf.robocode.security.HiddenAccess;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import robocode.control.RobotSpecification;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;


/**
 * @author Robert Maupin (original)
 */
public class ThreadManagerTest {

	@TempDir
	File folder;

	// The thread would keep the whole robot in memory, and Java 24+ has no SecurityManager to remove it
	@Test
	public void aThreadIsNotKeptAfterItOpensAFile() throws IOException {
		File root = new File(folder, "data");

		Assertions.assertTrue(root.mkdir());
		IHostedThread robot = createAdvancedRobot(root);
		ThreadManager manager = new ThreadManager();

		manager.addThreadGroup(Thread.currentThread().getThreadGroup(), robot);

		FileOutputStream stream = manager.createRobotFileStream(new File(root, "Robot.data/file").getPath(), false);

		stream.close();

		Assertions.assertFalse(manager.checkRobotFileStream());
	}

	@Test
	public void aDataFileMustBeInsideTheDataDirectory() {
		File root = new File(folder, "data");

		Assertions.assertTrue(root.mkdir());
		RobotFileSystemManager manager = createAdvancedRobot(root).getRobotFileSystemManager();

		for (String name : new String[] {"../outside", "sub/../../outside", ".*./outside"}) {
			Assertions.assertThrows(SecurityException.class, () -> manager.getDataFile(name), name);
		}
		Assertions.assertFalse(new File(root, "outside").exists());
	}

	private static IHostedThread createAdvancedRobot(final File root) {
		HiddenAccess.init();

		IRobotItem item = stub(IRobotItem.class, new InvocationHandler() {
			public Object invoke(Object proxy, Method method, Object[] args) {
				switch (method.getName()) {
				case "isAdvancedRobot":
					return true;

				case "getFullClassName":
					return "test.Robot";

				case "getShortClassName":
					return "Robot";

				default:
					return defaultValue(method.getReturnType());
				}
			}
		});
		RobotSpecification specification = HiddenAccess.createSpecification(item, "test.Robot 1.0", null, null, "1.0", null, null,
				"test.Robot", null);
		final RobotStatics statics = new RobotStatics(specification, "test.Robot 1.0", "", false, null, null, null, 0, 0);

		return stub(IHostedThread.class, new InvocationHandler() {
			private RobotFileSystemManager manager;

			public Object invoke(Object proxy, Method method, Object[] args) {
				switch (method.getName()) {
				case "getStatics":
					return statics;

				case "getRobotFileSystemManager":
					if (manager == null) {
						manager = new RobotFileSystemManager((IHostedThread) proxy, 200000, root.getPath(), root.getPath(), null);
					}
					return manager;

				default:
					return defaultValue(method.getReturnType());
				}
			}
		});
	}

	@Test
	public void aRobotCanOnlyStopItsOwnThreads() throws InterruptedException {
		ThreadManager manager = new ThreadManager();
		ThreadGroup robotGroup = new ThreadGroup("robot");
		ThreadGroup otherGroup = new ThreadGroup("other");
		final Thread own = new Thread(robotGroup, "own");
		final Thread other = new Thread(otherGroup, "other");
		final Throwable[] results = new Throwable[1];

		manager.addThreadGroup(robotGroup, createAdvancedRobot(folder));

		Thread robotThread = new Thread(robotGroup, () -> {
			manager.checkRobotThreadStop(own);
			try {
				manager.checkRobotThreadStop(other);
			} catch (SecurityException e) {
				results[0] = e;
			}
		});

		robotThread.start();
		robotThread.join();
		Assertions.assertNotNull(results[0], "A robot must not be able to stop a thread of another thread group");
	}

	@SuppressWarnings("unchecked")
	private static <T> T stub(Class<T> type, InvocationHandler handler) {
		return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] { type}, handler);
	}

	private static Object defaultValue(Class<?> type) {
		if (type == boolean.class) {
			return false;
		}
		return type.isPrimitive() && type != void.class ? 0 : null;
	}
}
