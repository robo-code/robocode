/*
 * Copyright (c) 2001-2026 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.test.host.security;


import net.sf.robocode.core.Container;
import net.sf.robocode.host.security.RobotClassLoader;
import net.sf.robocode.io.Logger;
import net.sf.robocode.security.HiddenAccess;
import net.sf.robocode.test.helpers.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * Tests that robot classes are verified when loaded, so robots that use something that is not allowed are rejected,
 * while all sample robots and legal test robots can still be loaded.
 *
 * @author Flemming N. Larsen (original)
 */
public class RobotClassVerifierTest {
	static URL classPath;
	static File robotsDir;

	// Attack robots that can be loaded, as their attacks are prevented by checks when the robot runs
	static final List<String> RUNTIME_CHECKED_ATTACKS = java.util.Arrays.asList(
			"ConstructorReflectionAttack.class",
			"DeserializationAttack.class",
			"FileAttack.class",
			"FileDeleteAttack.class",
			"FileListAttack.class",
			"FileOutputStreamAttack.class",
			"FileOutputStreamReflectionAttack.class",
			"FileReadAttack.class",
			"FileReaderDescriptorAttack.class",
			"FileWriterAttack.class",
			"ReflectionAccessibleAttack.class",
			"ReflectionAttack.class",
			"ReflectionExecAttack.class",
			"ResourceTraversalAttack.class",
			"RobocodeFileOutputStreamAttack.class",
			"ScannerReadAttack.class",
			"ThreadLimitAttack.class",
			"ZipFileReadAttack.class");

	@BeforeAll
	public static void init() throws IOException {
		HiddenAccess.initContainer();
		try {
			robotsDir = new File("../.sandbox/test-robots").getCanonicalFile().getAbsoluteFile();
		} catch (IOException e) {
			e.printStackTrace(Logger.realErr);
			throw new Error(e);
		}
		System.setProperty("ROBOTPATH", robotsDir.getPath());
		classPath = robotsDir.toURI().toURL();
	}

	@Test
	public void attackRobotsAreRejected() {
		// Attack robot -> the class or member that it must be rejected for
		Map<String, String> attacks = new LinkedHashMap<String, String>();

		attacks.put("tested.robots.AwtAttack", "javax.swing");
		attacks.put("tested.robots.ClassForNameLoaderAttack", "java.lang.Class.forName");
		attacks.put("tested.robots.ClassLoaderAttack", "java.lang.ClassLoader");
		attacks.put("tested.robots.ConstructorAwtAttack", "javax.swing");
		attacks.put("tested.robots.ConstructorHttpAttack", "java.net.URL");
		attacks.put("tested.robots.ConstructorSocketAttack", "java.net.ServerSocket");
		attacks.put("tested.robots.ConstructorThreadAttack", "java.lang.ThreadGroup");
		attacks.put("tested.robots.DnsAttack", "java.net.URL");
		attacks.put("tested.robots.EnvAttack", "java.lang.System.setProperty");
		attacks.put("tested.robots.FileDescriptorReadAttack", "java.io.FileInputStream.<init>");
		attacks.put("tested.robots.FileOutputStreamSubclassAttack", "java.io.FileOutputStream");
		attacks.put("tested.robots.FileSubclassDeleteAttack", "java.io.File.delete");
		attacks.put("tested.robots.HttpAttack", "java.net.URL");
		attacks.put("tested.robots.FileHandlerAttack", "java.util.logging.FileHandler");
		attacks.put("tested.robots.ImageIoReadAttack", "javax.imageio.ImageIO.read");
		attacks.put("tested.robots.IncludeNamespaceAttack", "net.sf.robocode.security.HiddenAccess");
		attacks.put("tested.robots.KeyStrokeSubclassAttack", "java.awt.AWTKeyStroke.registerSubclass");
		attacks.put("tested.robots.LogManagerAttack", "java.util.logging.LogManager");
		attacks.put("tested.robots.LoggerGlobalStateAttack", "java.util.logging.Logger.addHandler");
		attacks.put("tested.robots.MethodReferenceAttack", "java.lang.Runtime.exec");
		attacks.put("tested.robots.ObjectInputStreamSubclassAttack", "java.io.ObjectInputStream");
		attacks.put("tested.robots.PrintStreamFileAttack", "java.io.PrintStream.<init>");
		attacks.put("tested.robots.ProcessExecAttack", "java.lang.ProcessBuilder");
		attacks.put("tested.robots.ScannerPathAttack", "java.util.Scanner.<init>");
		attacks.put("tested.robots.SecurityProviderAttack", "java.security.Security");
		attacks.put("tested.robots.SocketAttack", "java.net.ServerSocket");
		attacks.put("tested.robots.ZipFileDeleteAttack", "java.util.zip.ZipFile.<init>");
		attacks.put("tested.robots.ZipFileSubclassAttack", "java.util.zip.ZipFile");
		attacks.put("tested.robots.SystemConsoleAttack", "java.lang.System.console");
		attacks.put("tested.robots.SystemPropertiesAttack", "java.lang.System.getProperties");
		attacks.put("tested.robots.ThreadAttack", "java.lang.ThreadGroup");
		attacks.put("tested.robots.ThreadGroupAttack", "java.lang.ThreadGroup");
		attacks.put("tested.robots.ThreadInterruptAttack", "java.lang.Thread.getAllStackTraces");
		attacks.put("tested.robots.ThreadSuspendAttack", "java.lang.Thread.suspend");

		List<String> failures = new ArrayList<String>();

		for (Map.Entry<String, String> attack : attacks.entrySet()) {
			RobotClassLoader cl = new RobotClassLoader(classPath, attack.getKey());
			try {
				cl.loadRobotMainClass(true);
				failures.add(attack.getKey() + " was not rejected");
			} catch (ClassNotFoundException e) {
				String message = String.valueOf(e.getMessage());

				if (!message.contains("not allowed to use " + attack.getValue())
						&& !message.contains("not allowed to extend " + attack.getValue())) {
					failures.add(attack.getKey() + " was rejected with an unexpected message: " + message);
				}
			} finally {
				cl.cleanup();
			}
		}
		Assert.assertTrue(failures.isEmpty(), failures.toString());
	}

	@Test
	public void sampleAndLegalTestRobotsAreAllowed() {
		List<String> robots = new ArrayList<String>();

		for (String packageName : new String[] {"sample", "sampleex", "samplesentry", "sampleteam", "tested.robots"}) {
			File[] files = new File(robotsDir, packageName.replace('.', File.separatorChar)).listFiles();

			Assert.assertNotNull(files, "Missing robot directory for package: " + packageName);
			for (File file : files) {
				String name = file.getName();

				// Attack robots are tested by attackRobotsAreRejected(), except those stopped when they run
				boolean rejectedAttack = name.endsWith("Attack.class") && !RUNTIME_CHECKED_ATTACKS.contains(name);

				if (name.endsWith(".class") && !name.contains("$") && !rejectedAttack) {
					robots.add(packageName + '.' + name.substring(0, name.length() - ".class".length()));
				}
			}
		}
		Assert.assertTrue(robots.size() > 40, "Too few robots found: " + robots);

		List<String> failures = new ArrayList<String>();

		for (String robot : robots) {
			RobotClassLoader cl = new RobotClassLoader(classPath, robot);
			try {
				cl.loadRobotMainClass(true);
			} catch (ClassNotFoundException e) {
				failures.add(robot + ": " + e.getMessage());
			} finally {
				cl.cleanup();
			}
		}
		Assert.assertTrue(failures.isEmpty(), failures.toString());
	}

	@Test
	public void robotBlockedFromHostLibraries() throws ClassNotFoundException {
		Assert.assertThrows(ClassNotFoundException.class, () -> {
			final String hostLibraryClass = "org.picocontainer.DefaultPicoContainer";

			// The library is available for the game engine ...
			Assert.assertNotNull(Container.systemLoader.loadClass(hostLibraryClass));

			// ... but not for robots
			RobotClassLoader cl = new RobotClassLoader(classPath, "tested.robots.Ahead");

			cl.loadClass(hostLibraryClass);
		});
	}
}
