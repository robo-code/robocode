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
package net.sf.robocode.repository.packager;


import codesize.Codesize;
import net.sf.robocode.core.Container;
import net.sf.robocode.host.IHostManager;
import net.sf.robocode.io.FileUtil;
import net.sf.robocode.io.Logger;
import net.sf.robocode.repository.items.RobotItem;
import net.sf.robocode.repository.items.TeamItem;
import net.sf.robocode.version.IVersionManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.net.URL;


/**
 * @author Pavel Savara (original)
 */
public class JarCreator {
	public static String createPackage(File target, boolean source, List<RobotItem> robots, List<TeamItem> teams, URL web, String desc, String author, String version) {
		final IHostManager host = Container.getComponent(IHostManager.class);
		final String rVersion = Container.getComponent(IVersionManager.class).getVersion();
		JarOutputStream jarout = null;
		FileOutputStream fos = null;
		Set<String> entries = new HashSet<String>();

		try {
			fos = new FileOutputStream(target);
			jarout = new JarOutputStream(fos, createManifest(robots));
			jarout.setComment(rVersion + " - Robocode version");

			for (TeamItem team : teams) {
				final String teamEntry = team.getRelativePath() + '/' + team.getShortClassName() + ".team";

				if (!entries.contains(teamEntry)) {
					entries.add(teamEntry);
					JarEntry jt = new JarEntry(teamEntry);

					jarout.putNextEntry(jt);
					team.storeProperties(jarout, web, desc, author, version);
					jarout.closeEntry();
				}
			}

			for (RobotItem robot : robots) {
				final String proEntry = robot.getRelativePath() + '/' + robot.getShortClassName() + ".properties";

				if (!entries.contains(proEntry)) {
					entries.add(proEntry);
					JarEntry jt = new JarEntry(proEntry);

					jarout.putNextEntry(jt);
					robot.storeProperties(jarout, web, desc, author, version);
					jarout.closeEntry();
					packageClasses(source, host, jarout, robot, entries);
				}
			}
		} catch (IOException e) {
			Logger.logError(e);
		} finally {
			FileUtil.cleanupStream(jarout);
			FileUtil.cleanupStream(fos);
		}

		StringBuilder sb = new StringBuilder();

		for (String entry : entries) {
			sb.append(entry);
			sb.append('\n');
		}

		codeSize(target, sb);

		return sb.toString();
	}

	private static void codeSize(File target, StringBuilder sb) {
		Codesize.Item item = Codesize.processZipFile(target);
		int codesize = item.getCodeSize();
		String weightClass;

		if (codesize >= 1500) {
			weightClass = "MegaBot  (codesize >= 1500 bytes)";
		} else if (codesize > 750) {
			weightClass = "MiniBot  (codesize < 1500 bytes)";
		} else if (codesize > 250) {
			weightClass = "MicroBot  (codesize < 750 bytes)";
		} else {
			weightClass = "NanoBot  (codesize < 250 bytes)";
		}

		sb.append("\n\n---- Codesize ----\n");
		sb.append("Codesize: ").append(codesize).append(" bytes\n");
		sb.append("Robot weight class: ").append(weightClass).append('\n');
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

	private static void packageClasses(boolean source, IHostManager host, JarOutputStream jarout, RobotItem robot, Set<String> entries) throws IOException {
		for (String className : host.getReferencedClasses(robot)) {
			if (!className.startsWith("java") && !className.startsWith("robocode")) {
				String name = className.replace('.', '/');

				packageClass(source, jarout, robot, name, entries);
			}
		}
	}

	private static void packageClass(boolean source, JarOutputStream jarout, RobotItem robot, String name, Set<String> entries) throws IOException {
		if (source && !name.contains("$") && !entries.contains(name + ".java")) {
			File javaFile = new File(robot.getRoot().getRootPath(), name + ".java");

			if (javaFile.exists()) {
				entries.add(name + ".java");
				JarEntry je = new JarEntry(name + ".java");

				jarout.putNextEntry(je);
				copy(javaFile, jarout);
				jarout.closeEntry();
			}
		}
		File classFile = new File(robot.getRoot().getRootPath(), name + ".class");

		if (classFile.exists() && !entries.contains(name + ".class")) {
			entries.add(name + ".class");
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
		} finally {
			FileUtil.cleanupStream(in);
		}
	}
}
