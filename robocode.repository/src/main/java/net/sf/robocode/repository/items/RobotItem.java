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
 *     Flemming N. Larsen
 *     - Extended to support both class path and source path
 *******************************************************************************/
package net.sf.robocode.repository.items;


import net.sf.robocode.io.FileUtil;
import net.sf.robocode.io.Logger;
import net.sf.robocode.io.URLJarCollector;
import static net.sf.robocode.io.Logger.logError;
import net.sf.robocode.repository.IRobotRepositoryItem;
import net.sf.robocode.repository.RobotType;
import net.sf.robocode.repository.root.IRepositoryRoot;
import net.sf.robocode.security.HiddenAccess;
import net.sf.robocode.core.Container;
import net.sf.robocode.host.IHostManager;
import net.sf.robocode.version.IVersionManager;
import robocode.control.RobotSpecification;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;


/**
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (contributor)
 */
public class RobotItem extends NamedItem implements IRobotRepositoryItem {
	private static final long serialVersionUID = 1L;

	// Allowed maximum length for a robot's full package name
	private final static int MAX_FULL_PACKAGE_NAME_LENGTH = 32;
	// Allowed maximum length for a robot's short class name
	private final static int MAX_SHORT_CLASS_NAME_LENGTH = 32;
	private final static String ROBOT_DESCRIPTION = "robot.description";
	private final static String ROBOT_AUTHOR_NAME = "robot.author.name";
	// private final static String ROBOT_AUTHOR_EMAIL = "robot.author.email";
	// private final static String ROBOT_AUTHOR_WEBSITE = "robot.author.website";
	private final static String ROBOT_JAVA_SOURCE_INCLUDED = "robot.java.source.included";
	private final static String ROBOT_VERSION = "robot.version";
	private final static String ROBOT_LANGUAGE = "robot.language";
	private final static String ROBOT_CLASSNAME = "robot.classname";
	private final static String ROBOT_WEBPAGE = "robot.webpage";
	private final static String ROBOCODE_VERSION = "robocode.version";

	private static final String CLASS_EXTENSION = ".class";

	RobotType robotType;

	private boolean isExpectedRobot;
	private boolean isClassURL;
	private boolean isPropertiesURL;
	private boolean isPropertiesLoaded;

	private boolean alwaysUseCacheForData = System.getProperty("ALWAYSUSECACHEFORDATA", "false").equals("true");

	private URL classURL;
	private URL propertiesURL;

	private URL classPathURL;
	private Set<URL> sourcePathURLs; // This is a Set in order to avoid duplicates

	public RobotItem(IRepositoryRoot root) {
		super(root.getRootUrl(), root);

		isValid = true;

		classPathURL = root.getRootUrl();
		sourcePathURLs = new HashSet<URL>();

		init();
	}

	protected void init() {
		propsUrlFromClassUrl();
		classUrlFromProperties();
		classUrlFromPropertiesUrl();
		classNameFromClassUrl(root);
	}

	public void setClassUrl(URL classUrl) {
		this.classURL = classUrl;
		isClassURL = (classUrl != null);
		init();
	}

	public void setPropertiesUrl(URL propertiesUrl) {
		this.propertiesURL = propertiesUrl;
		isPropertiesURL = (propertiesUrl != null);
		init();
	}

	public void setClassPathURL(URL classPathUrl) {
		this.classPathURL = classPathUrl;
	}

	public void addSourcePathURL(URL sourcePathUrl) {
		sourcePathURLs.add(sourcePathUrl);
	}

	// / -------------------------------------
	// / url initialization
	// / -------------------------------------

	private void propsUrlFromClassUrl() {
		if (propertiesURL == null && isClassURL) {
			final String pUrl = classURL.toString().replaceAll("\\" + CLASS_EXTENSION, ".properties");

			try {
				propertiesURL = new URL(pUrl);
				loadProperties();
			} catch (MalformedURLException e) {
				Logger.logError(e);
			}
		}
	}

	private void classUrlFromProperties() {
		if (classURL == null && isPropertiesURL) {
			if (!loadProperties()) {
				isValid = false;
			} else {
				final String cn = properties.getProperty(ROBOT_CLASSNAME, null);

				if (cn != null) {
					isExpectedRobot = true;
					try {
						final String cUrl = root.getRootUrl().toString() + cn.replace('.', '/') + CLASS_EXTENSION;

						classURL = new URL(cUrl);
					} catch (MalformedURLException e) {
						Logger.logError(e);
					}
				} else {
					isValid = false;
				}
			}
		}
	}

	private void classUrlFromPropertiesUrl() {
		if (isPropertiesURL) {
			try {
				final String pUrl = propertiesURL.toString();
				final String cUrl = pUrl.substring(0, pUrl.lastIndexOf('.')) + CLASS_EXTENSION;

				classURL = new URL(cUrl);
			} catch (MalformedURLException e) {
				Logger.logError(e);
			}
		}
	}

	private void classNameFromClassUrl(IRepositoryRoot root) {
		if (getFullClassName() == null && isClassURL) {
			String cUrl = classURL.toString();

			cUrl = cUrl.substring(0, cUrl.lastIndexOf('.'));
			final String cn = cUrl.substring(root.getRootUrl().toString().length()).replace('/', '.').replace('\\', '.');

			properties.setProperty(ROBOT_CLASSNAME, cn);
		}
	}

	private void htmlUrlFromPropertiesUrl() {
		try {
			if (propertiesURL != null) {
				htmlUrl = new URL(propertiesURL.toString().replaceAll("\\.properties", ".html"));
				// test that html file exists
				final URLConnection conn = URLJarCollector.openConnection(htmlUrl);

				conn.getInputStream().close();
			}
		} catch (IOException ignored) {
			// doesn't exist
			htmlUrl = null;
		}
	}

	private void htmlUrlFromClassUrl() {
		if (htmlUrl == null) {
			try {
				if (classURL != null) {
					htmlUrl = new URL(classURL.toString().replaceAll("\\.class", ".html"));
					// test that html file exists
					final URLConnection conn = URLJarCollector.openConnection(htmlUrl);

					conn.getInputStream().close();
				}
			} catch (IOException ignored) {
				// doesn't exist
				htmlUrl = null;
			}
		}
	}

	// / -------------------------------------
	// / public
	// / -------------------------------------

	public boolean isTeam() {
		return false;
	}

	public URL getHtmlUrl() {
		// lazy
		if (htmlUrl == null) {
			htmlUrlFromPropertiesUrl();
			htmlUrlFromClassUrl();
		}
		return htmlUrl;
	}

	public URL getPropertiesUrl() {
		return propertiesURL;
	}

	public List<String> getFriendlyUrls() {
		final ArrayList<String> urls = new ArrayList<String>();

		if (propertiesURL != null) {
			final String pUrl = propertiesURL.toString();
			final String noType = pUrl.substring(0, pUrl.lastIndexOf('.'));

			urls.add(pUrl);
			urls.add(noType);
			urls.add(propertiesURL.getFile());
		}
		if (classURL != null) {
			final String cUrl = classURL.toString();
			final String noType = cUrl.substring(0, cUrl.lastIndexOf('.'));

			urls.add(noType);
			urls.add(classURL.getFile());
		}
		if (getFullClassName() != null) {
			urls.add(getFullClassName());
			urls.add(getUniqueFullClassNameWithVersion());
		}
		if (root.isJar()) {
			urls.add(root.getRootUrl().toString());
		}
		return urls;
	}

	public void update(long lastModified, boolean force) {
		if (lastModified > this.lastModified || force) {
			if (force) {
				isValid = true;
			}

			// trying to guess all correct file locations
			init();

			this.lastModified = lastModified;
			if (classURL == null) {
				isValid = false;
			}
			loadProperties();
			if (root.isJar() && !isPropertiesLoaded) {
				isValid = false;
			}
			if (isValid) {
				validateType(false);
			}
			if (isValid) {
				verifyName();
			}
		}
	}

	protected void validateType(boolean resolve) {
		final IHostManager hostManager = Container.getComponent(IHostManager.class);

		robotType = hostManager.getRobotType(this, resolve, isExpectedRobot || isClassURL);
		if (!robotType.isValid()) {
			isValid = false;
		}
	}

	// stronger than update
	public boolean validate() {
		validateType(true);
		return isValid;
	}

	private boolean loadProperties() {
		if (propertiesURL != null) {
			InputStream ios = null;

			try {
				URLConnection con = URLJarCollector.openConnection(propertiesURL);

				ios = con.getInputStream();
				properties.load(ios);
				isPropertiesLoaded = true;
				return true;
			} catch (IOException e) {
				return false;
			} finally {
				FileUtil.cleanupStream(ios);
			}
		}
		return false;
	}

	private boolean verifyName() {
		String robotName = getFullClassName();
		String shortClassName = getShortClassName();

		final boolean valid = verifyRobotName(robotName, shortClassName, isExpectedRobot);

		if (!valid) {
			isValid = false;
		}
		return valid;
	}

	public static boolean verifyRobotName(String fullClassName, String shortClassName, boolean silent) {
		if (fullClassName == null || fullClassName.length() == 0 || fullClassName.contains("$")) {
			return false;
		}

		int lIndex = fullClassName.indexOf(".");

		if (lIndex > 0) {
			String rootPackage = fullClassName.substring(0, lIndex);

			if (rootPackage.equalsIgnoreCase("robocode")) {
				if (!silent) {
					logError("Robot " + fullClassName + " ignored. You cannot use package " + rootPackage);
				}
				return false;
			}

			if (rootPackage.length() > MAX_FULL_PACKAGE_NAME_LENGTH) {
				if (!silent) {
					logError(
							"Robot " + fullClassName + " has package name too long.  " + MAX_FULL_PACKAGE_NAME_LENGTH
							+ " characters maximum please.");
				}
				return false;
			}
		}

		if (shortClassName != null && shortClassName.length() > MAX_SHORT_CLASS_NAME_LENGTH) {
			if (!silent) {
				logError(
						"Robot " + fullClassName + " has classname too long.  " + MAX_SHORT_CLASS_NAME_LENGTH
						+ " characters maximum please.");
			}
			return false;
		}

		return true;
	}

	public void storeProperties(OutputStream os) throws IOException {
		properties.store(os, "Robocode Robot");
	}

	public void storeProperties(OutputStream os, URL web, String desc, String author, String version, boolean sourceIncluded) throws IOException {
		Properties copy = (Properties) properties.clone();

		if (version != null) {
			copy.setProperty(ROBOT_VERSION, version);
		}
		if (desc != null) {
			copy.setProperty(ROBOT_DESCRIPTION, desc);
		}
		if (author != null) {
			copy.setProperty(ROBOT_AUTHOR_NAME, author);
		}
		if (web != null) {
			copy.setProperty(ROBOT_WEBPAGE, web.toString());
		}
		copy.setProperty(ROBOT_JAVA_SOURCE_INCLUDED, "" + sourceIncluded);
		
		final IVersionManager vm = Container.getComponent(IVersionManager.class);

		copy.setProperty(ROBOCODE_VERSION, vm.getVersion());
		copy.store(os, "Robocode Robot");
	}

	public boolean isDroid() {
		return robotType.isDroid();
	}

	public boolean isTeamRobot() {
		return robotType.isTeamRobot();
	}

	public boolean isAdvancedRobot() {
		return robotType.isAdvancedRobot();
	}

	public boolean isStandardRobot() {
		return robotType.isStandardRobot();
	}

	public boolean isInteractiveRobot() {
		return robotType.isInteractiveRobot();
	}

	public boolean isPaintRobot() {
		return robotType.isPaintRobot();
	}

	public boolean isJuniorRobot() {
		return robotType.isJuniorRobot();
	}

	public URL getClassPathURL() {
		return classPathURL;
	}

	public URL[] getSourcePathURLs() {
		// If no source path URL exists, we must use the class path URL
		if (sourcePathURLs.size() == 0) {
			return new URL[] { classPathURL };
		}
		return sourcePathURLs.toArray(new URL[] {});
	}

	public String getFullClassName() {
		return properties.getProperty(ROBOT_CLASSNAME, null);
	}

	public String getVersion() {
		return properties.getProperty(ROBOT_VERSION, null);
	}

	public String getDescription() {
		return properties.getProperty(ROBOT_DESCRIPTION, null);
	}

	public String getAuthorName() {
		return properties.getProperty(ROBOT_AUTHOR_NAME, null);
	}

	public String getRobotLanguage() {
		final String lang = properties.getProperty(ROBOT_LANGUAGE, null);

		return lang == null ? "java" : lang;
	}

	public URL getWebpage() {
		try {
			return new URL(properties.getProperty(ROBOT_WEBPAGE, null));
		} catch (MalformedURLException e) {
			return null;
		}
	}

	public boolean getJavaSourceIncluded() {
		return properties.getProperty(ROBOT_JAVA_SOURCE_INCLUDED, "false").toLowerCase().equals("true");
	}

	public String getRobocodeVersion() {
		return properties.getProperty(ROBOCODE_VERSION, null);
	}

	public String getReadableDirectory() {
		if (getRootPackage() == null) {
			return null;
		}
		if (root.isJar()) {
			String jarFile = getClassPathURL().getFile();

			jarFile = jarFile.substring(jarFile.lastIndexOf('/') + 1, jarFile.length());
			return FileUtil.getRobotCacheDir() + File.separator + jarFile + "_" + File.separator + getRootPackage();
		} else {
			return getClassPathURL().getFile() + getRootPackage();
		}
	}

	public String getWritableDirectory() {
		if (getRootPackage() == null) {
			return null;
		}
		if (root.isJar()) {
			String jarFile = getClassPathURL().getFile();

			jarFile = jarFile.substring(jarFile.lastIndexOf('/') + 1, jarFile.length());
			return FileUtil.getRobotCacheDir() + File.separator + jarFile + "_" + File.separator
					+ getFullPackage().replace('.', File.separatorChar);
		} else {
			File vroot;

			if (alwaysUseCacheForData) {
				vroot = FileUtil.getRobotCacheDir();
			} else {
				vroot = root.getRootPath();
			}
			// to cacheDir
			return vroot + File.separator + getFullPackage().replace('.', File.separatorChar);
		}
	}

	public RobotSpecification createRobotSpecification(RobotSpecification battleRobotSpec, String teamId) {
		RobotSpecification specification;

		if (battleRobotSpec != null) {
			specification = battleRobotSpec;
		} else {
			specification = createRobotSpecification();
		}
		if (teamId != null) {
			HiddenAccess.setTeamId(specification, teamId);
		}
		return specification;
	}

	public String toString() {
		return url.toString();
	}
}
