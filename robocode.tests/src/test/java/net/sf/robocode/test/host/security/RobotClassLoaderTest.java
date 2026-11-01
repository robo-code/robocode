/*
 * Copyright (c) 2001-2026 Mathew A. Nelson and Robocode contributors
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
import net.sf.robocode.test.helpers.Assert;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;


/**
 * @author Pavel Savara (original)
 */
public class RobotClassLoaderTest {
	static URL classPath;
	static String robotsPath;
	final String badRobot = "tested.robots.IncludeNamespaceAttack";
	final String goodRobot = "tested.robots.Ahead";

	@BeforeAll
	public static void init() throws IOException {
		HiddenAccess.initContainer();
		try {
			File robotsPathFile = new File("../.sandbox/test-robots").getCanonicalFile().getAbsoluteFile();
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

	@Test
	public void robotBlockedBad() throws ClassNotFoundException {
		Assert.assertThrows(ClassNotFoundException.class, () -> {
			RobotClassLoader cl = new RobotClassLoader(classPath, badRobot);

			cl.loadRobotMainClass(true);
		});
	}

	@Test
	public void robotBlockedRobocode() throws ClassNotFoundException {
		Assert.assertThrows(ClassNotFoundException.class, () -> {
			RobotClassLoader cl = new RobotClassLoader(classPath, goodRobot);

			cl.loadClass("net.sf.robocode.host.proxies.BasicRobotProxy");
		});
	}

	@Test
	public void finalizeIsRenamed() throws Exception {
		URL robotClassPath = compileRobot("finalizer", "Finalizer", "{\n"
				+ "\tprotected void finalize() throws Throwable { super.finalize(); }\n");

		// The JVM must not call finalize() of robots on its finalizer thread
		Class<?> c = new RobotClassLoader(robotClassPath, "finalizer.Finalizer").loadClass("finalizer.Finalizer", true);
		Method renamed = c.getDeclaredMethod("robocode$finalize");

		renamed.setAccessible(true);
		renamed.invoke(c.getDeclaredConstructor().newInstance()); // super.finalize() still works
		Assert.assertThrows(NoSuchMethodException.class, () -> c.getDeclaredMethod("finalize"));
	}

	// Compiles a class outside the class path of the tests, as its classes are otherwise treated as system classes
	private static URL compileRobot(String packageName, String className, String body) throws IOException {
		Path dir = Files.createTempDirectory("robots");
		Path source = Files.createDirectories(dir.resolve(packageName)).resolve(className + ".java");
		Files.write(source, ("package " + packageName + ";\n"
				+ "public class " + className + " " + body + "}\n").getBytes("UTF-8"));
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		Assumptions.assumeTrue(compiler != null);
		Assert.assertEquals(0, compiler.run(null, null, null, "-nowarn", "-cp", System.getProperty("java.class.path"),
				source.toString()));
		return dir.toUri().toURL();
	}

	@Test
	public void robotBlockedControl() throws ClassNotFoundException {
		Assert.assertThrows(ClassNotFoundException.class, () -> {
			RobotClassLoader cl = new RobotClassLoader(classPath, goodRobot);

			cl.loadClass("robocode.control.RobocodeEngine");
		});
	}
}
