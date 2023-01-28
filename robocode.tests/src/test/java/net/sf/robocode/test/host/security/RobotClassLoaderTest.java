/*
 * Copyright (c) 2001-2023 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.test.host.security;


import net.sf.robocode.core.Container;
import net.sf.robocode.core.EngineClassLoader;
import net.sf.robocode.host.security.RobotClassLoader;
import net.sf.robocode.io.Logger;
import net.sf.robocode.security.HiddenAccess;
import net.sf.robocode.test.helpers.RobocodeTestBed;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;


/**
 * @author Pavel Savara (original)
 */
public class RobotClassLoaderTest {
	static URL classPath;
	static String robotsPath;
	final String badRobot = "tested.robots.IncludeNamespaceAttack";
	final String goodRobot = "tested.robots.Ahead";

	@BeforeClass
	public static void init() throws IOException {
		HiddenAccess.initContainer();
		try {
			File robotsPathFile = new File("../.sandbox/robots").getCanonicalFile().getAbsoluteFile();
			robotsPath = robotsPathFile.getPath();
		} catch (IOException e) {
			e.printStackTrace(Logger.realErr);
			throw new Error(e);
		}
		System.setProperty("ROBOTPATH", robotsPath);
		classPath = new File(robotsPath).getCanonicalFile().toURI().toURL();
	}

	@Test
	public void engineAllowed() throws ClassNotFoundException {
		final ClassLoader engineLoader = new EngineClassLoader(ClassLoader.getSystemClassLoader());

		engineLoader.loadClass("net.sf.robocode.host.proxies.BasicRobotProxy");
		final Class<?> c = engineLoader.loadClass("net.sf.robocode.host.proxies.BasicRobotProxy");

		Assert.assertEquals(engineLoader, c.getClassLoader());
	}

	@Test
	public void robotAllowed() throws ClassNotFoundException {
		RobotClassLoader cl = new RobotClassLoader(classPath, goodRobot);
		final Class<?> c = cl.loadClass("robocode.Robot", true);

		Assert.assertEquals(Container.systemLoader, c.getClassLoader());
	}

	@Test
	public void robotAllowedMain() throws ClassNotFoundException {
		RobotClassLoader cl = new RobotClassLoader(classPath, goodRobot);
		final Class<?> c = cl.loadRobotMainClass(true);

		Assert.assertEquals(cl, c.getClassLoader());
	}

	@Test(expected = ClassNotFoundException.class)
	public void robotBlockedBad() throws ClassNotFoundException {
		RobotClassLoader cl = new RobotClassLoader(classPath, badRobot);

		cl.loadRobotMainClass(true);
	}

	@Test(expected = ClassNotFoundException.class)
	public void robotBlockedRobocode() throws ClassNotFoundException {
		RobotClassLoader cl = new RobotClassLoader(classPath, goodRobot);

		cl.loadClass("net.sf.robocode.host.proxies.BasicRobotProxy");
	}

	@Test(expected = ClassNotFoundException.class)
	public void robotBlockedControl() throws ClassNotFoundException {
		RobotClassLoader cl = new RobotClassLoader(classPath, goodRobot);

		cl.loadClass("robocode.control.RobocodeEngine");
	}
}
