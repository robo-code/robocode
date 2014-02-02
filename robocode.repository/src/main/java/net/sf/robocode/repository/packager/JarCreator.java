/*******************************************************************************
 * Copyright (c) 2001-2014 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 *******************************************************************************/
package net.sf.robocode.repository.packager;


import net.sf.robocode.core.Container;
import net.sf.robocode.host.IHostManager;
import net.sf.robocode.io.FileUtil;
import net.sf.robocode.io.Logger;
import net.sf.robocode.repository.CodeSizeCalculator;
import net.sf.robocode.repository.IRobotSpecItem;
import net.sf.robocode.repository.RobotProperties;
import net.sf.robocode.repository.items.RobotItem;
import net.sf.robocode.repository.items.TeamItem;
import net.sf.robocode.version.IVersionManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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

	public static String createPackage(File jarFile, List<RobotItem> robots, List<TeamItem> teams, RobotProperties props) {
		final String rVersion = Container.getComponent(IVersionManager.class).getVersion();
		JarOutputStream jarOut = null;
		FileOutputStream fos = null;
		Set<String> jarEntries = new HashSet<String>();

		try {
			fos = new FileOutputStream(jarFile);
			jarOut = new JarOutputStream(fos, createManifest(robots));
			jarOut.setComment(rVersion + " - Robocode version");

			// Add team robot specification
			addRobotSpecEntryToJar(jarEntries, jarOut, jarFile, robots, props);

			// Add team item specification
			addRobotSpecEntryToJar(jarEntries, jarOut, jarFile, teams, props);
		} catch (IOException e) {
			Logger.logError(e);
		} finally {
			FileUtil.cleanupStream(jarOut);
			FileUtil.cleanupStream(fos);
		}

		StringBuilder sb = new StringBuilder();
		for (String entry : jarEntries) {
			sb.append(entry).append('\n');
		}
		appendCodeSize(jarFile, sb);
		return sb.toString();
	}
	
	private static void addRobotSpecEntryToJar(Set<String> jarEntries, final JarOutputStream jarOut, File jarFile, List<? extends IRobotSpecItem> robotSpecItems, final RobotProperties props) throws IOException {
		for (final IRobotSpecItem robotSpecItem : robotSpecItems) {
			String fileExt = (robotSpecItem instanceof TeamItem) ? ".team" : ".properties";
			String entry = robotSpecItem.getRelativePath() + '/' + robotSpecItem.getShortClassName() + fileExt;

			// Add the code size to the robot properties before storing them
			File dir = new File(FileUtil.getRobotsDir(), robotSpecItem.getRelativePath());
			Integer codeSize = CodeSizeCalculator.getDirectoryCodeSize(dir);
			props.setCodeSize(codeSize);

			boolean added = addJarEntryAndExecute(jarOut, jarEntries, entry, new Runnable() {
				@Override
				public void run() {
					try {
						robotSpecItem.storeProperties(jarOut, props);
					} catch (IOException e) {
						Logger.logError(e);
					}
				}
			});
			if (added && (robotSpecItem instanceof RobotItem)) {
				packageRobotFiles(jarOut, jarEntries, (RobotItem) robotSpecItem, props);
			}
		}
	}

	private static void appendCodeSize(File jarFile, StringBuilder sb) {

		Integer codesize = CodeSizeCalculator.getJarFileCodeSize(jarFile);
		if (codesize != null) {
			String weightClass = null;

			if (codesize >= 1500) {
				weightClass = "MegaBot  (codesize >= 1500 bytes)";
			} else if (codesize >= 750) {
				weightClass = "MiniBot  (codesize < 1500 bytes)";
			} else if (codesize >= 250) {
				weightClass = "MicroBot (codesize < 750 bytes)";
			} else {
				weightClass = "NanoBot  (codesize < 250 bytes)";
			}
			sb.append("\n\n---- Codesize ----\n");
			sb.append("Codesize: ").append(codesize).append(" bytes\n");
			sb.append("Robot weight class: ").append(weightClass).append('\n');
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

	private static void packageRobotFiles(JarOutputStream jarOut, Set<String> jarEntries, RobotItem robotItem, RobotProperties props) throws IOException {
		IHostManager host = Container.getComponent(IHostManager.class);
		for (String className : host.getReferencedClasses(robotItem)) {
			if (!className.startsWith("java") && !className.startsWith("robocode")) {
				String robotPath = className.replace('.', '/');
				packageRobotFiles(jarOut, jarEntries, robotPath, robotItem, props);
			}
		}
	}

	private static void packageRobotFiles(final JarOutputStream jarOut, Set<String> jarEntries, String robotPath, RobotItem robotItem, RobotProperties props) throws IOException {
		addJavaFileToJar(jarOut, jarEntries, robotPath, robotItem, props);
		addClassFileToJar(jarOut, jarEntries, robotPath, robotItem, props);
	}

	private static void addJavaFileToJar(final JarOutputStream jarOut, Set<String> jarEntries, String robotPath, RobotItem robotItem, RobotProperties props) throws UnsupportedEncodingException {
		String javaFileName = robotPath + ".java";

		if (props.isIncludeSources() && !jarEntries.contains(javaFileName) && !robotPath.contains("$")) {
			for (URL sourcePathURL : robotItem.getSourcePathURLs()) {
				String sourcePath = sourcePathURL.getPath();
				sourcePath = URLDecoder.decode(sourcePath, "UTF-8");

				final File javaFile = new File(sourcePath, javaFileName);
				if (javaFile.exists()) {
					addJarEntryAndExecute(jarOut, jarEntries, javaFileName, new Runnable() {
						@Override
						public void run() {
							try {
								copy(javaFile, jarOut);
							} catch (IOException e) {
								Logger.logError(e);
							}
						}
					});
				}
			}
		}		
	}

	private static void addClassFileToJar(final JarOutputStream jarOut, Set<String> jarEntries, String robotPath, RobotItem robotItem, RobotProperties props) throws UnsupportedEncodingException {
		String classFileName = robotPath + ".class";

		String classPath = robotItem.getClassPathURL().getPath();
		classPath = URLDecoder.decode(classPath, "UTF-8");

		final File classFile = new File(classPath, classFileName);
		if (classFile.exists()) {
			addJarEntryAndExecute(jarOut, jarEntries, classFileName, new Runnable() {
				@Override
				public void run() {
					try {
						copy(classFile, jarOut);
					} catch (IOException e) {
						Logger.logError(e);
					}
				}
			});
		}
	}

	private static void copy(File inFile, JarOutputStream out) throws IOException {
		byte[] buffer = new byte[4096];
		FileInputStream in = null;
		try {
			in = new FileInputStream(inFile);
			while (in.available() > 0) {
				int count = in.read(buffer, 0, buffer.length);
				out.write(buffer, 0, count);
			}
		} finally {
			FileUtil.cleanupStream(in);
		}
	}

	private static boolean addJarEntryAndExecute(JarOutputStream jarOut, Set<String> jarEntries, String jarEntry, Runnable runnable) {
		if (!jarEntries.contains(jarEntry)) {
			try {
				jarOut.putNextEntry(new JarEntry(jarEntry));
				jarEntries.add(jarEntry); // called here after a new jar entry was successfully created in the jar output
				try {
					runnable.run();
					return true;
				} finally {
					jarOut.closeEntry();
				}
			} catch (IOException e) {
				Logger.logError(e);
			}
		}
		return false;
	}
}
