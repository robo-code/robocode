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
import net.sf.robocode.repository.IRobotItem;
import net.sf.robocode.repository.RobotProperties;
import net.sf.robocode.repository.items.RobotItem;
import net.sf.robocode.repository.items.TeamItem;
import net.sf.robocode.version.IVersionManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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

	public static String createPackage(File jarFile, List<RobotItem> robotItems, TeamItem teamItem, RobotProperties props) {
		JarOutputStream jarOut = null;
		FileOutputStream fos = null;
		Set<String> jarEntries = new HashSet<String>();
		try {
			fos = new FileOutputStream(jarFile);
			jarOut = new JarOutputStream(fos, createManifest(robotItems));

			String rVersion = Container.getComponent(IVersionManager.class).getVersion();
			jarOut.setComment(rVersion + " - Robocode version");

			boolean isTeam = teamItem != null;
			
			// Add robot items
			addRobotItemToJar(jarOut, jarEntries, jarFile, robotItems, props, isTeam);

			// Add team item, if it exists
			if (isTeam) {
				addTeamItemToJar(jarOut, jarEntries, jarFile, teamItem, props);
			}
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
	
	private static void addRobotItemToJar(final JarOutputStream jarOut, final Set<String> jarEntries, File jarFile, List<? extends IRobotItem> robotItems, final RobotProperties props, final boolean isTeam) throws IOException {
		for (final IRobotItem robotItem : robotItems) {
			final String entry = robotItem.getRelativePath() + '/' + robotItem.getShortClassName() + ".properties";

			// Add the code size to the robot properties before storing them
			File dir = new File(FileUtil.getRobotsDir(), robotItem.getRelativePath());
			Integer codeSize = CodeSizeCalculator.getDirectoryCodeSize(dir);
			props.setCodeSize(codeSize);

			if (!isTeam) {
				// Store local .properties file
				FileOutputStream fis = null;
				try {
					File propertiesFile = new File(FileUtil.getRobotsDir(), entry);
					fis = new FileOutputStream(propertiesFile);
					robotItem.storeProperties(fis, props);
				} finally {
					FileUtil.cleanupStream(fis);
				}
			}
			// Package robot files (.class, .properties, .java) into jar file
			packageRobotFiles(jarOut, jarEntries, robotItem, props);
		}
	}

	private static void addTeamItemToJar(final JarOutputStream jarOut, final Set<String> jarEntries, File jarFile, final TeamItem teamItem, final RobotProperties props) throws IOException {
		final String entry = teamItem.getRelativePath() + '/' + teamItem.getShortClassName() + ".team";

		addJarEntryAndExecute(jarOut, jarEntries, entry, new Runnable() {
			@Override
			public void run() {
				try {
					// Store local .team file
					FileOutputStream fis = null;
					try {
						File propertiesFile = new File(FileUtil.getRobotsDir(), entry);
						fis = new FileOutputStream(propertiesFile);
						teamItem.storeProperties(fis, props);
					} finally {
						FileUtil.cleanupStream(fis);
					}
					// Store .team file into jar file
					teamItem.storeProperties(jarOut, props);
				} catch (IOException e) {
					Logger.logError(e);
				}
			}
		});
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

	private static void packageRobotFiles(JarOutputStream jarOut, Set<String> jarEntries, IRobotItem robotItem, RobotProperties props) throws IOException {
		IHostManager host = Container.getComponent(IHostManager.class);
		for (String className : host.getReferencedClasses(robotItem)) {
			if (!className.startsWith("java") && !className.startsWith("robocode")) {
				String robotPath = className.replace('.', '/');
				packageRobotFiles(jarOut, jarEntries, robotPath, robotItem, props);
			}
		}
	}

	private static void packageRobotFiles(final JarOutputStream jarOut, Set<String> jarEntries, String robotPath, IRobotItem robotItem, RobotProperties props) throws IOException {
		addJavaFileToJar(jarOut, jarEntries, robotPath, robotItem, props);
		addClassFileToJar(jarOut, jarEntries, robotPath, robotItem, props);
		addPropertiesFileToJar(jarOut, jarEntries, robotPath, robotItem, props);
	}

	private static void addJavaFileToJar(final JarOutputStream jarOut, Set<String> jarEntries, String robotFilePath, IRobotItem robotItem, RobotProperties props) throws IOException {
		if (props.isIncludeSources() && !robotFilePath.contains("$")) {
			for (URL sourcePathURL : robotItem.getSourcePathURLs()) {
				writeOutFile(jarOut, jarEntries, sourcePathURL.getPath(), robotFilePath, ".java");
			}
		}		
	}

	private static void addClassFileToJar(final JarOutputStream jarOut, Set<String> jarEntries, String robotFilePath, IRobotItem robotItem, RobotProperties props) throws IOException {
		String classPath = robotItem.getClassPathURL().getPath();
		writeOutFile(jarOut, jarEntries, classPath, robotFilePath, ".class");
	}

	private static void addPropertiesFileToJar(final JarOutputStream jarOut, Set<String> jarEntries, String robotFilePath, IRobotItem robotItem, RobotProperties props) throws IOException {
		String classPath = robotItem.getClassPathURL().getPath();
		writeOutFile(jarOut, jarEntries, classPath, robotFilePath, ".properties");
	}

	private static void writeOutFile(final JarOutputStream jarOut, Set<String> jarEntries, String rootPath, String robotFilePath, String fileExt) throws IOException {
		String fileName = robotFilePath + fileExt;
		try {
			rootPath = URLDecoder.decode(rootPath, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			Logger.logError(e);
		}
		final File file = new File(rootPath, fileName);
		if (file.exists()) {
			addJarEntryAndExecute(jarOut, jarEntries, fileName, new Runnable() {
				@Override
				public void run() {
					try {
						writeOutFile(jarOut, file);
					} catch (IOException e) {
						Logger.logError(e);
					}
				}
			});
		}
	}
	
	private static void writeOutFile(OutputStream out, File file) throws IOException {
		byte[] buffer = new byte[4096];
		FileInputStream in = null;
		try {
			in = new FileInputStream(file);
			while (in.available() > 0) {
				int count = in.read(buffer, 0, buffer.length);
				out.write(buffer, 0, count);
			}
		} finally {
			FileUtil.cleanupStream(in);
		}
	}

	private static void addJarEntryAndExecute(JarOutputStream jarOut, Set<String> jarEntries, String jarEntry, Runnable runnable) throws IOException {
		if (!jarEntries.contains(jarEntry)) {
			jarOut.putNextEntry(new JarEntry(jarEntry));
			jarEntries.add(jarEntry); // called here after a new jar entry was successfully created in the jar output
			try {
				runnable.run();
			} finally {
				jarOut.closeEntry();
			}
		}
	}
}
