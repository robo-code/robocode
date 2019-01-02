/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
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

import java.io.BufferedInputStream;
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
 * @author Flemming N. Larsen (contributor)
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
			addRobotItemToJar(jarOut, jarEntries, robotItems, props, isTeam);

			// Add team item, if it exists
			if (isTeam) {
				addTeamItemToJar(jarOut, jarEntries, teamItem, props);
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

	private static void addRobotItemToJar(JarOutputStream target, Set<String> jarEntries, List<? extends IRobotItem> robotItems, RobotProperties props, boolean isTeam) throws IOException {
		for (IRobotItem robotItem : robotItems) {
			String path = robotItem.getRelativePath() + '/' + robotItem.getShortClassName() + ".properties";

			// Add the code size to the robot properties before storing them
			String classPath = robotItem.getClassPathURL().getPath(); // Takes the dev.path into account!
			File dir = new File(classPath, robotItem.getRelativePath());
			Integer codeSize = CodeSizeCalculator.getDirectoryCodeSize(dir);
			props.setCodeSize(codeSize);

			if (!isTeam) {
				// Store local .properties file
				FileOutputStream fis = null;
				try {
					File file = new File(classPath, path);
					fis = new FileOutputStream(file);
					robotItem.storeProperties(fis, props);
				} finally {
					FileUtil.cleanupStream(fis);
				}
			}
			// Package robot files (.class, .properties, .java) into jar file
			addRobotFilesToJar(target, jarEntries, robotItem, props);
		}
	}

	private static void addTeamItemToJar(JarOutputStream target, Set<String> jarEntries, TeamItem teamItem, RobotProperties props) throws IOException {
		String robotPathWithoutFileExt = teamItem.getRelativePath() + '/' + teamItem.getShortClassName();

		String fileExt = ".team";
		File file = new File(FileUtil.getRobotsDir(), robotPathWithoutFileExt + fileExt);

		// Store .team file into local robot dir
		FileOutputStream fis = null;
		try {
			fis = new FileOutputStream(file);
			teamItem.storeProperties(fis, props);
		} finally {
			FileUtil.cleanupStream(fis);
		}
		// Store .team file into jar file
		addToJar(target, jarEntries, FileUtil.getRobotsDir().getPath(), robotPathWithoutFileExt, fileExt);
	}

	private static void addRobotFilesToJar(JarOutputStream target, Set<String> jarEntries, IRobotItem robotItem, RobotProperties props) throws IOException {
		IHostManager host = Container.getComponent(IHostManager.class);
		for (String className : host.getReferencedClasses(robotItem)) {
			if (!className.startsWith("java") && !className.startsWith("robocode")) {
				String robotPath = className.replace('.', '/');
				addRobotFilesToJar(target, jarEntries, robotPath, robotItem, props);
			}
		}
	}

	private static void addRobotFilesToJar(JarOutputStream target, Set<String> jarEntries, String robotPathWithoutFileExt, IRobotItem robotItem, RobotProperties props) throws IOException {
		addClassFileToJar(target, jarEntries, robotPathWithoutFileExt, robotItem);
		addPropertiesFileToJar(target, jarEntries, robotPathWithoutFileExt, robotItem);
		addJavaFileToJar(target, jarEntries, robotPathWithoutFileExt, robotItem, props);
		addDataDirToJar(target, jarEntries, robotPathWithoutFileExt, robotItem, props);
	}

	private static void addClassFileToJar(JarOutputStream target, Set<String> jarEntries, String robotPathWithoutFileExt, IRobotItem robotItem) throws IOException {
		String classRootPath = robotItem.getClassPathURL().getPath();
		addToJar(target, jarEntries, classRootPath, robotPathWithoutFileExt, ".class");
	}

	private static void addPropertiesFileToJar(JarOutputStream target, Set<String> jarEntries, String robotPathWithoutFileExt, IRobotItem robotItem) throws IOException {
		String classRootPath = robotItem.getClassPathURL().getPath();
		addToJar(target, jarEntries, classRootPath, robotPathWithoutFileExt, ".properties");
	}

	private static void addJavaFileToJar(JarOutputStream target, Set<String> jarEntries, String robotFilePath, IRobotItem robotItem, RobotProperties props) throws IOException {
		if (props.isIncludeSource() && !robotFilePath.contains("$")) {
			for (URL sourcePathURL : robotItem.getSourcePathURLs()) {
				addToJar(target, jarEntries, sourcePathURL.getPath(), robotFilePath, ".java");
			}
		}		
	}

	private static void addDataDirToJar(JarOutputStream target, Set<String> jarEntries, String robotPathWithoutFileExt, IRobotItem robotItem, RobotProperties props) throws IOException {
		if (props.isIncludeData()) {
			String rootPath = robotItem.getRootPath().replace('\\', '/');
			if (rootPath.startsWith("file:/")) {
				rootPath = rootPath.substring("file:/".length());
			}
			addToJar(target, jarEntries, rootPath, robotPathWithoutFileExt, ".data");
		}
	}

	private static void addToJar(JarOutputStream target, Set<String> jarEntries, String rootPath, String robotPathWithoutFileExt, String fileExt) throws IOException {
		String filePath = robotPathWithoutFileExt + fileExt;
		try {
			rootPath = URLDecoder.decode(rootPath, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			Logger.logError(e);
		}
		File file = new File(rootPath, filePath);
		if (file.exists() && !jarEntries.contains(filePath)) {
			addFileToJar(file, filePath, target);
			jarEntries.add(filePath);
		}
	}

	private static void addFileToJar(File source, String entryPath, JarOutputStream target) throws IOException {
		// Jar/zip files only allows '/' as file separator
		String path = entryPath.replace("\\", "/");

		// No jar/zip entry must start with an '/'
		while (path.startsWith("/")) {
			path = path.substring(1);
		}

		if (source.isDirectory()) {
			// Directory entries must end with an '/'
			if (!path.isEmpty() && !path.endsWith("/")) {
				path += "/";
			}
			// Add all files in the directory to the target jar
			for (File nestedFile: source.listFiles()) {
				String newEntryPath = entryPath + '/' + nestedFile.getName();
				addFileToJar(nestedFile, newEntryPath, target);
			}
		} else {
			JarEntry entry = new JarEntry(path);
			entry.setTime(source.lastModified());
			target.putNextEntry(entry);

			// Source is a file
			
			FileInputStream fis = null;
			BufferedInputStream bis = null;
			try {
				fis = new FileInputStream(source);
				bis = new BufferedInputStream(fis);

				byte[] buffer = new byte[1024];
				while (true) {
					int count = bis.read(buffer);
					if (count == -1) {
						break;
					}
					target.write(buffer, 0, count);
				}
			} finally {
				target.closeEntry();

				FileUtil.cleanupStream(bis);
				FileUtil.cleanupStream(fis);
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
}
