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
package net.sf.robocode.repository2.packager;

import net.sf.robocode.repository2.items.RobotItem;
import net.sf.robocode.repository2.items.TeamItem;
import net.sf.robocode.host.IHostManager;
import net.sf.robocode.host.IRobotClassLoader;
import net.sf.robocode.core.Container;
import net.sf.robocode.version.IVersionManager;
import net.sf.robocode.io.Logger;
import net.sf.robocode.io.FileUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.util.List;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.jar.JarEntry;
import java.util.jar.Attributes;

/**
 * @author Pavel Savara (original)
 */
public class JarCreator {
	public static void createPackage(File target, boolean source, List<RobotItem> robots, List<TeamItem> teams) {
		final IHostManager host = Container.getComponent(IHostManager.class);
		final String rVersion = Container.getComponent(IVersionManager.class).getVersion();
		JarOutputStream jarout = null;
		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(target);
			jarout = new JarOutputStream(fos, createManifest(robots));
			jarout.setComment(rVersion + " - Robocode version");

			for (TeamItem team : teams) {
				JarEntry jt = new JarEntry(team.getRelativePath() + '/' + team.getShortClassName() + ".team");

				jarout.putNextEntry(jt);
				team.storeProperties(jarout);
				jarout.closeEntry();
			}

			for (RobotItem robot : robots) {
				JarEntry jt = new JarEntry(robot.getRelativePath() + '/' + robot.getShortClassName() + ".properties");

				jarout.putNextEntry(jt);
				robot.storeProperties(jarout);
				jarout.closeEntry();
				packageClasses(source, host, jarout, robot);
			}
		} catch (IOException e) {
			Logger.logError(e);
		} finally {
			FileUtil.cleanupStream(jarout);
			FileUtil.cleanupStream(fos);
		}
	}

	private static Manifest createManifest(List<RobotItem> robots) {
		Manifest manifest;
		manifest = new Manifest();
		manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
		StringBuilder robotsString = new StringBuilder();

		for (int i = 0; i < robots.size(); i++) {
			robotsString.append(robots.get(i).getFullClassName());
			if (i < robots.size() - 1) {
				robotsString.append(',');
			}
		}
		manifest.getMainAttributes().put(new Attributes.Name("robots"), robotsString.toString());
		return manifest;
	}

	private static void packageClasses(boolean source, IHostManager host, JarOutputStream jarout, RobotItem robot) throws IOException {
		IRobotClassLoader loader =null;
		try {
			loader = host.createLoader(robot);
			loader.loadRobotMainClass(true);

			for (String className : loader.getReferencedClasses()) {
				if (!className.startsWith("java") && !className.startsWith("robocode")) {
					String name = className.replace('.', '/');
					packageClass(source, jarout, robot, name);
				}
			}

		} catch (ClassNotFoundException e) {
			Logger.logError(e);
		} finally {
			if (loader!=null){
				loader.cleanup();
			}
		}
	}

	private static void packageClass(boolean source, JarOutputStream jarout, RobotItem robot, String name) throws IOException {
		if (source && !name.contains("$")) {
			File javaFile = new File(robot.getRootFile(), name +".java");
			if (javaFile.exists()){
				JarEntry je = new JarEntry(name + ".class");
				jarout.putNextEntry(je);
				copy(javaFile, jarout);
				jarout.closeEntry();
			}
		}
		File classFile = new File(robot.getRoot().getRootPath(), name +".class");
		if (classFile.exists()){
			JarEntry je = new JarEntry(name + ".class");
			jarout.putNextEntry(je);
			copy(classFile, jarout);
			jarout.closeEntry();
		}
	}

	private static void copy(File inFile, JarOutputStream out) throws IOException {
		FileInputStream in = null;
		try {
			in = new FileInputStream(inFile);
			final byte[] buf = new byte[4096];
			while (in.available() > 0) {
				int count = in.read(buf, 0, 4096);
				out.write(buf, 0, count);
			}
		}
		finally {
			FileUtil.cleanupStream(in);
		}
	}
}
