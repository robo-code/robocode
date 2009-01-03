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
package net.sf.robocode.test.host.security;


import net.sf.robocode.security.HiddenAccess;
import net.sf.robocode.core.Container;
import net.sf.robocode.host.security.RobotClassLoader;

import java.io.IOException;
import java.io.File;
import java.net.URL;

import org.junit.Test;
import org.junit.BeforeClass;
import org.junit.Assert;


/**
 * @author Pavel Savara (original)
 */
public class RobotClassLoaderTest {
	static URL classPath;
	final String badRobot = "tested.robots.IncludeNamespaceAttack";
	final String goodRobot = "tested.robots.Ahead";

	@BeforeClass
	public static void init() throws IOException {
		HiddenAccess.createRobocodeManager();
		classPath = new File("target/classes").getCanonicalFile().toURL();
	}

	@Test
	public void engineAlowed() throws ClassNotFoundException {
		final Class<?> c = Container.engineLoader.loadClass("net.sf.robocode.host.proxies.BasicRobotProxy");

		Assert.assertEquals(Container.engineLoader, c.getClassLoader());
	}

	@Test
	public void robotAlowed() throws ClassNotFoundException {
		RobotClassLoader cl = new RobotClassLoader(classPath, goodRobot);
		final Class<?> c = cl.loadClass("robocode.Robot", true);

		Assert.assertEquals(Container.systemLoader, c.getClassLoader());
	}

	@Test
	public void robotAlowedMain() throws ClassNotFoundException {
		RobotClassLoader cl = new RobotClassLoader(classPath, goodRobot);
		final Class<?> c = cl.loadRobotMainClass();

		Assert.assertEquals(cl, c.getClassLoader());
	}

	@Test(expected = ClassNotFoundException.class)
	public void robotBlockedBad() throws ClassNotFoundException {
		RobotClassLoader cl = new RobotClassLoader(classPath, badRobot);

		cl.loadRobotMainClass();
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
