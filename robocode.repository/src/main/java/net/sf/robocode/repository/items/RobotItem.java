/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.repository.items;


import net.sf.robocode.io.FileUtil;
import net.sf.robocode.io.Logger;
import net.sf.robocode.io.RobocodeProperties;
import net.sf.robocode.io.URLJarCollector;
import static net.sf.robocode.io.Logger.logError;
import net.sf.robocode.repository.IRobotItem;
import net.sf.robocode.repository.RobotProperties;
import net.sf.robocode.repository.RobotType;
import net.sf.robocode.repository.root.ClasspathRoot;
import net.sf.robocode.repository.root.IRepositoryRoot;
import net.sf.robocode.security.HiddenAccess;
import net.sf.robocode.core.Container;
import net.sf.robocode.host.IHostManager;
import net.sf.robocode.version.IVersionManager;
import robocode.control.RobotSpecification;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Set;


/**
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (contributor)
 */
public class RobotItem extends RobotSpecItem implements IRobotItem {
	private static final long serialVersionUID = 1L;

	// Allowed maximum length for a robot's full package name
	private static final int MAX_FULL_PACKAGE_NAME_LENGTH = 32;
	// Allowed maximum length for a robot's short class name
	private static final int MAX_SHORT_CLASS_NAME_LENGTH = 32;

	protected static final String ROBOT_PLATFORM = "robot.platform";
	private static final String ROBOT_CLASSNAME = "robot.classname";
	private static final String ROBOT_VERSION = "robot.version";
	private static final String ROBOT_DESCRIPTION = "robot.description";
	private static final String ROBOT_AUTHOR_NAME = "robot.author.name";
	private static final String ROBOT_WEBPAGE = "robot.webpage";
	private static final String ROBOCODE_VERSION = "robocode.version";
	private static final String ROBOT_INCLUDE_SOURCE = "robot.include.source"; 
	private static final String ROBOT_INCLUDE_DATA = "robot.include.data"; 
	private static final String ROBOT_CODESIZE = "robot.codesize";

	// File extensions
	private static final String CLASS_EXTENSION = ".class";
	private static final String PROPERTIES_EXTENSION = ".properties";
	private static final String HTML_EXTENSION = ".html";

	private static final boolean ALWAYS_USE_CACHE_FOR_DATA = System.getProperty("ALWAYSUSECACHEFORDATA", "false").equals(
			"true");

	private RobotType robotType;

	private URL classPathURL;
	private Set<URL> sourcePathURLs; // This is a Set in order to avoid duplicates

	private URL classURL;
	private URL propertiesURL;

	private String className;
	protected boolean isPropertiesLoaded;

	public RobotItem(URL itemURL, IRepositoryRoot root) {
		super(itemURL, root);

		isValid = true;

		classPathURL = root.getURL();
		sourcePathURLs = new HashSet<URL>();
	}

	private void populate() {
		populatePropertiesURLFromClassURL();
		populateClassURLFromPropertiesURL();
		loadProperties();
	}

	public void setClassURL(URL classUrl) {
		this.classURL = classUrl;
		populate();
	}

	public void setPropertiesURL(URL propertiesUrl) {
		this.propertiesURL = propertiesUrl;
		populate();
	}

	public void setClassPathURL(URL classPathUrl) {
		this.classPathURL = classPathUrl;
	}

	public void addSourcePathURL(URL sourcePathUrl) {
		sourcePathURLs.add(sourcePathUrl);
	}

	// -------------------------------------
	// URL initialization
	// -------------------------------------

	private void populatePropertiesURLFromClassURL() {
		if (propertiesURL == null && classURL != null) {
			final String path = classURL.toString().replaceFirst("\\" + CLASS_EXTENSION, PROPERTIES_EXTENSION);

			try {
				propertiesURL = new URL(path);
			} catch (MalformedURLException e) {
				Logger.logError(e);
			}
			if (isInvalidURL(propertiesURL)) {
				propertiesURL = null;
			}
		}
	}

	private void populateClassURLFromPropertiesURL() {
		if (classURL == null && propertiesURL != null) {
			final String path = propertiesURL.toString().replaceAll("\\" + PROPERTIES_EXTENSION, CLASS_EXTENSION);

			try {
				classURL = new URL(path);
			} catch (MalformedURLException e) {
				Logger.logError(e);
			}
			if (isInvalidURL(classURL)) {
				classURL = null;
			}
		}
	}

	private void populateClassNameFromClassURL() {
		populate();

		if (className == null && classURL != null) {
			final String path = classURL.toString();

			// .dll file?
			int index = (path.toLowerCase().indexOf(".dll!/"));

			if (index > 0) {
				className = path.substring(index + 6);
				return;
			}

			// Class within .jar or regular file path?

			index = path.lastIndexOf('!');

			if (index > 0) {
				// .jar file
				className = path.substring(index + 2);
			} else {
				// file path
				className = path.substring(root.getURL().toString().length());
			}

			// Remove the file extension from the class name
			index = className.lastIndexOf('.');

			if (index > 0) {
				className = className.substring(0, index);
			}

			// Replace all file separators with dots (from file path the package/namespace)
			className = className.replaceAll("[\\\\\\/]", ".");
		}
	}

	private void populateHtmlURL() {
		populate();

		if (htmlURL == null && classURL != null) {
			try {
				htmlURL = new URL(classURL.toString().replaceFirst(CLASS_EXTENSION, HTML_EXTENSION));
			} catch (MalformedURLException ignore) {}
		}
		if (htmlURL == null && propertiesURL != null) {
			try {
				htmlURL = new URL(propertiesURL.toString().replaceFirst(PROPERTIES_EXTENSION, HTML_EXTENSION));
			} catch (MalformedURLException ignore) {}
		}
		if (isInvalidURL(htmlURL)) {
			htmlURL = null;
		}
	}

	private boolean isInvalidURL(URL url) {
		if (url != null) {
			InputStream is = null;
			try {
				URLConnection conn = URLJarCollector.openConnection(url);
				is = conn.getInputStream();
				return false;
			} catch (IOException e) {
				return true;
			} finally {
				FileUtil.cleanupStream(is);
			}
		}
		return true;
	}

	// / -------------------------------------
	// / public
	// / -------------------------------------

	public boolean isTeam() {
		return false;
	}

	public URL getHtmlURL() {
		// Lazy
		if (htmlURL == null) {
			populateHtmlURL();
		}
		return htmlURL;
	}

	public URL getPropertiesURL() {
		if (propertiesURL == null) {
			populatePropertiesURLFromClassURL();
		}
		return propertiesURL;
	}

	public Set<String> getFriendlyURLs() {
		populate();

		Set<String> urls = new HashSet<String>();

		URL url = null;
		if (classURL != null) {
			url = classURL;
		} else if (propertiesURL != null) {
			url = propertiesURL;
		}
		if (url != null) {
			String sUrl = url.toString();
			String urlNoType = sUrl.substring(0, sUrl.lastIndexOf('.'));

			String path = url.getPath();
			String pathNoType = path.substring(0, path.lastIndexOf('.'));
			
			urls.add(urlNoType);
			urls.add(pathNoType);
		}
		if (getFullClassName() != null) {
			if (RobocodeProperties.isTestingOn()) {
				urls.add(getFullClassName());
			} else {
				urls.add(getUniqueFullClassName());
			}
			urls.add(getUniqueFullClassNameWithVersion());
		}
		if (root.isJAR()) {
			urls.add(root.getURL().toString());
		}
		if (root instanceof ClasspathRoot) {
			String friendly = ((ClasspathRoot) root).getFriendlyProjectURL(itemUrl);

			if (friendly != null) {
				urls.add(friendly);
			}
		}
		return urls;
	}

	/**
	 * {@inheritDoc}
	 */
	public void update(long lastModified, boolean force) {
		if (lastModified > this.lastModified || force) {
			if (force) {
				isValid = true;
			}
			if (isValid) {
				validateType(false);
			}
			this.lastModified = lastModified;
			if (classURL == null) {
				isValid = false;
			}
			loadProperties();
			if (!isTeamRobot() && root.isJAR() && !isPropertiesLoaded) {
				isValid = false;
			}
			if (isValid) {
				verifyName();
			}
		}
	}

	protected void validateType(boolean resolve) {
		populate();

		final IHostManager hostManager = Container.getComponent(IHostManager.class);

		robotType = hostManager.getRobotType(this, resolve, classURL != null);
		if (!robotType.isValid()) {
			isValid = false;
		}
	}

	// Stronger than update
	public boolean validate() {
		validateType(true);
		return isValid;
	}

	// Note that ROBOCODE_CLASSNAME can be invalid, an hence can't be trusted when loaded!
	// Hence, we read the fullClassName field instead and NOT the ROBOCODE_CLASSNAME property.
	private boolean loadProperties() {
		if (!isPropertiesLoaded && propertiesURL != null) {
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

		final boolean valid = verifyRobotName(robotName, shortClassName, false);

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
					logError("Robot " + fullClassName + " ignored.  You cannot use package " + rootPackage);
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

	public void storeProperties(OutputStream os, RobotProperties robotProps) throws IOException {
		if (className != null) {
			properties.setProperty(ROBOT_CLASSNAME, className);
		}
		if (robotProps.getVersion() != null) {
			properties.setProperty(ROBOT_VERSION, robotProps.getVersion());
		}
		if (robotProps.getDescription() != null) {
			properties.setProperty(ROBOT_DESCRIPTION, robotProps.getDescription());
		}
		if (robotProps.getAuthor() != null) {
			properties.setProperty(ROBOT_AUTHOR_NAME, robotProps.getAuthor());
		}
		if (robotProps.getWebPage() != null) {
			properties.setProperty(ROBOT_WEBPAGE, robotProps.getWebPage().toExternalForm());
		}
		if (robotProps.getCodeSize() != null) {
			properties.setProperty(ROBOT_CODESIZE, "" + robotProps.getCodeSize());
		}
		properties.setProperty(ROBOT_INCLUDE_SOURCE, "" + robotProps.isIncludeSource());

		String version = Container.getComponent(IVersionManager.class).getVersion();
		properties.setProperty(ROBOCODE_VERSION, version);

		saveProperties(os);

		saveProperties();
	}

	private void saveProperties(OutputStream os) throws IOException {
		properties.store(os, "Robocode Robot");
	}

	private void saveProperties() {
		File file = new File(root.getPath(), className.replaceAll("\\.", "/") + PROPERTIES_EXTENSION);
		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(file);
			saveProperties(fos);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			FileUtil.cleanupStream(fos);
		}
		populatePropertiesURLFromClassURL();
	}

	public boolean isJuniorRobot() {
		return robotType.isJuniorRobot();
	}

	public boolean isStandardRobot() {
		return robotType.isStandardRobot();
	}

	public boolean isAdvancedRobot() {
		return robotType.isAdvancedRobot();
	}

	public boolean isTeamRobot() {
		return robotType.isTeamRobot();
	}

	public boolean isDroid() {
		return robotType.isDroid();
	}

	public boolean isSentryRobot() {
		return robotType.isSentryRobot();
	}

	public boolean isInteractiveRobot() {
		return robotType.isInteractiveRobot();
	}

	public boolean isPaintRobot() {
		return robotType.isPaintRobot();
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
		// Lazy
		if (className == null) {
			populateClassNameFromClassURL();
		}
		return className;
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

	public String getPlatform() {
		return properties.getProperty(ROBOT_PLATFORM, "Java");
	}

	public URL getWebpage() {
		try {
			String webPage = properties.getProperty(ROBOT_WEBPAGE, null);
			if (webPage == null || webPage.trim().isEmpty()) {
				return null;
			}
			return new URL(webPage);
		} catch (MalformedURLException e) {
			return null;
		}
	}

	public Integer getCodeSize() {
		String value = properties.getProperty(ROBOT_CODESIZE);
		if (value == null) {
			return null;
		}
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return null;
		}
	}
	
	public boolean getIncludeSource() {
		return properties.getProperty(ROBOT_INCLUDE_SOURCE, "true").equalsIgnoreCase("true");
	}

	public boolean getIncludeData() {
		return properties.getProperty(ROBOT_INCLUDE_DATA, "true").equalsIgnoreCase("true");
	}

	public boolean isSourceIncluded() {
		return sourcePathURLs.size() > 0;
	}

	public String getRobocodeVersion() {
		return properties.getProperty(ROBOCODE_VERSION, null);
	}

	public String getReadableDirectory() {
		String path;
		if (root.isJAR()) {
			String jarFile = getClassPathURL().getFile();
			jarFile = jarFile.substring(jarFile.lastIndexOf('/') + 1, jarFile.length());
			path = FileUtil.getRobotsDataDir().getPath();
			if (jarFile.length() > 0) {
				path += File.separator + jarFile + '_';
			}
		} else {
			path = getClassPathURL().getFile();
		}
		if (getFullPackage() != null) {
			path += File.separator + getFullPackage().replace('.', File.separatorChar);
		}
		return path;
	}

	public String getWritableDirectory() {
		String path;
		if (root.isJAR()) {
			String jarFile = getClassPathURL().getFile();	
			jarFile = jarFile.substring(jarFile.lastIndexOf('/') + 1, jarFile.length());
			path = FileUtil.getRobotsDataDir().getPath();
			if (jarFile.length() > 0) {
				path += File.separator + jarFile + '_';
			}
		} else {
			path = ALWAYS_USE_CACHE_FOR_DATA ? FileUtil.getRobotsDataDir().getPath() : getClassPathURL().getFile();
		}
		if (getFullPackage() != null) {
			path += File.separator + getFullPackage().replace('.', File.separatorChar);
		}
		return path;
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
		return itemUrl.toString();
	}
}
