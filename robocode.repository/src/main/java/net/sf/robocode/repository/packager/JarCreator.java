/*******************************************************************************
 * Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
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
import java.net.URLDecoder;


/**
 * @author Pavel Savara (original)
 */
public class JarCreator {
	public static String createPackage(File target, boolean includeSources, List<RobotItem> robots, List<TeamItem> teams, URL web, String desc, String author, String version) {
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
					JarEntry jt = new JarEntry(teamEntry);

					jarout.putNextEntry(jt);
					entries.add(teamEntry); // called here, as an exception might occur before this line
					try {
						team.storeProperties(jarout, web, desc, author, version);
					} finally {
						jarout.closeEntry();
					}
				}
			}

			for (RobotItem robot : robots) {
				final String propsEntry = robot.getRelativePath() + '/' + robot.getShortClassName() + ".properties";

				if (!entries.contains(propsEntry)) {
					JarEntry jt = new JarEntry(propsEntry);

					jarout.putNextEntry(jt);
					entries.add(propsEntry); // called here, as an exception might occur before this line
					try {
						robot.storeProperties(jarout, web, desc, author, version);
					} finally {
						jarout.closeEntry();
					}
					packageSourceAndClasses(includeSources, host, jarout, robot, entries);
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

	private static void packageSourceAndClasses(boolean includeSources, IHostManager host, JarOutputStream jarout, RobotItem robot, Set<String> entries) throws IOException {
		for (String className : host.getReferencedClasses(robot)) {
			if (!className.startsWith("java") && !className.startsWith("robocode")) {
				String name = className.replace('.', '/');

				packageSourceAndClass(includeSources, jarout, robot, name, entries);
			}
		}
	}

	private static void packageSourceAndClass(boolean includeSource, JarOutputStream jarout, RobotItem robot, String name, Set<String> entries) throws IOException {
		final String javaFileName = name + ".java";
		final String classFileName = name + ".class";

		if (includeSource && !entries.contains(javaFileName) && !name.contains("$")) {
			for (URL sourcePathURL : robot.getSourcePathURLs()) {
				String sourcePath = sourcePathURL.getPath();

				sourcePath = URLDecoder.decode(sourcePath, "UTF-8");
				File javaFile = new File(sourcePath, javaFileName);

				if (javaFile.exists()) {
					JarEntry je = new JarEntry(javaFileName);

					jarout.putNextEntry(je);
					entries.add(javaFileName); // called here, as an exception might occur before this line
					try {
						copy(javaFile, jarout);
					} finally {
						jarout.closeEntry();
					}
				}
			}
		}
		String classPath = robot.getClassPathURL().getPath();

		classPath = URLDecoder.decode(classPath, "UTF-8");
		File classFile = new File(classPath, classFileName);

		if (classFile.exists() && !entries.contains(classFileName)) {
			JarEntry je = new JarEntry(classFileName);

			jarout.putNextEntry(je);
			entries.add(classFileName); // called here, as an exception might occur before this line
			try {
				copy(classFile, jarout);
			} finally {
				jarout.closeEntry();
			}
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
