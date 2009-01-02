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
package net.sf.robocode.host.security;

import net.sf.robocode.security.HiddenAccess;
import net.sf.robocode.core.Container;
import net.sf.robocode.io.Logger;

import java.io.IOException;

import org.junit.Test;
import org.junit.BeforeClass;
import org.junit.Assert;

/**
 * @author Pavel Savara (original)
 */
public class RestrictedClassLoaderTest {
	@BeforeClass
	public static void init() {
		HiddenAccess.createRobocodeManager();
	}

	@Test
	public void engineAlowed() throws ClassNotFoundException {
		final Class<?> c = Container.engineLoader.loadClass("net.sf.robocode.host.proxies.BasicRobotProxy");
		Assert.assertEquals(Container.engineLoader, c.getClassLoader());
	}

	@Test
	public void robotAlowed() throws ClassNotFoundException {
		RestrictedClassLoader cl=new RestrictedClassLoader();
		final Class<?> c = cl.loadClass("robocode.Robot", true);
		Assert.assertEquals(Container.systemLoader, c.getClassLoader());
	}

	@Test(expected = ClassNotFoundException.class)
	public void robotBlockedRobocode() throws ClassNotFoundException {
		RestrictedClassLoader cl=new RestrictedClassLoader();
		cl.loadClass("net.sf.robocode.host.proxies.BasicRobotProxy");
	}

	@Test(expected = ClassNotFoundException.class)
	public void robotBlockedControl() throws ClassNotFoundException {
		RestrictedClassLoader cl=new RestrictedClassLoader();
		cl.loadClass("robocode.control.RobocodeEngine");
	}
}
