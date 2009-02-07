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
package net.sf.robocode.repository.items;


import net.sf.robocode.host.IHostManager;
import net.sf.robocode.host.IRobotClassLoader;
import net.sf.robocode.io.FileUtil;
import net.sf.robocode.io.Logger;
import static net.sf.robocode.io.Logger.logError;
import net.sf.robocode.repository.IRobotRepositoryItem;
import net.sf.robocode.repository.root.IRepositoryRoot;
import net.sf.robocode.security.HiddenAccess;
import robocode.Droid;
import robocode.Robot;
import robocode.control.RobotSpecification;
import robocode.robotinterfaces.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Pavel Savara (original)
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
	private final static String ROBOT_CLASSNAME = "robot.classname";
	private final static String ROBOT_WEBPAGE = "robot.webpage";
	private final static String ROBOCODE_VERSION = "robocode.version";

	private boolean isJuniorRobot;
	private boolean isStandardRobot;
	private boolean isInteractiveRobot;
	private boolean isPaintRobot;
	private boolean isAdvancedRobot;
	private boolean isTeamRobot;
	private boolean isDroid;

	private boolean isExpectedRobot;
	private boolean isClassURL;
	private boolean isPropertiesURL;

	private URL propertiesUrl;

	public RobotItem(URL classUrl, URL propUrl, IRepositoryRoot root) {
		super(classUrl, root);
		propertiesUrl = propUrl;
		isValid = true;
		isPropertiesURL = (propertiesUrl != null);
		isClassURL = (url != null);

		init();
	}

	private void init() {
		propsUrlFromClassUrl();
		classUrlFromProperties();
		classUrlFromPropertiesUrl();
		classNameFromClassUrl(root);
	}

	public void setClassUrl(URL classUrl) {
		this.url = classUrl;
		isClassURL = (url != null);
		init();
	}

	public void setPropertiesUrl(URL propertiesUrl) {
		this.propertiesUrl = propertiesUrl;
		isPropertiesURL = (propertiesUrl != null);
		init();
	}

	// / -------------------------------------
	// / url initialization
	// / -------------------------------------

	private void propsUrlFromClassUrl() {
		if (propertiesUrl == null && isClassURL) {
			final String pUrl = url.toString().replaceAll("\\.class", ".properties");

			try {
				propertiesUrl = new URL(pUrl);
				loadProperties();
			} catch (MalformedURLException e) {
				Logger.logError(e);
			}
		}
	}

	private void classUrlFromProperties() {
		if (url == null && isPropertiesURL) {
			if (!loadProperties()) {
				isValid = false;
			} else {
				final String cn = properties.getProperty(ROBOT_CLASSNAME, null);

				if (cn != null) {
					isExpectedRobot = true;
					try {
						final String cUrl = root.getRootUrl().toString() + cn.replace('.', '/') + ".class";

						url = new URL(cUrl);
					} catch (MalformedURLException e) {
						Logger.logError(e);
					}
				} else{
					isValid = false;
				}
			}
		}
	}

	private void classUrlFromPropertiesUrl() {
		if (isPropertiesURL) {
			try {
				final String pUrl = propertiesUrl.toString();
				final String cUrl = pUrl.substring(0, pUrl.lastIndexOf('.')) + ".class";

				url = new URL(cUrl);
			} catch (MalformedURLException e) {
				Logger.logError(e);
			}
		}
	}

	private void classNameFromClassUrl(IRepositoryRoot root) {
		if (getFullClassName() == null && isClassURL) {
			String cUrl = url.toString();

			cUrl = cUrl.substring(0, cUrl.lastIndexOf('.'));
			final String cn = cUrl.substring(root.getRootUrl().toString().length()).replace('/', '.').replace('\\', '.');

			properties.setProperty(ROBOT_CLASSNAME, cn);
		}
	}

	private void htmlUrlFromPropertiesUrl() {
		try {
			if (propertiesUrl != null) {
				htmlUrl = new URL(propertiesUrl.toString().replaceAll("\\.properties", ".html"));
				// test that html file exists
				final URLConnection conn = htmlUrl.openConnection();

				conn.setUseCaches(false);
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
				if (url != null) {
					htmlUrl = new URL(url.toString().replaceAll("\\.class", ".html"));
					// test that html file exists
					final URLConnection conn = htmlUrl.openConnection();

					conn.setUseCaches(false);
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
		return propertiesUrl;
	}

	public List<String> getFriendlyUrls() {
		final ArrayList<String> urls = new ArrayList<String>();

		if (propertiesUrl != null) {
			final String pUrl = propertiesUrl.toString();
			final String noType = pUrl.substring(0, pUrl.lastIndexOf('.'));

			urls.add(pUrl);
			urls.add(noType);
			urls.add(propertiesUrl.getFile());
		}
		if (url != null) {
			final String cUrl = url.toString();
			final String noType = cUrl.substring(0, cUrl.lastIndexOf('.'));

			urls.add(noType);
			urls.add(url.getFile());
		}
		if (getFullClassName() != null) {
			urls.add(getFullClassName());
			urls.add(getUniqueFullClassNameWithVersion());
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
			if (url == null) {
				isValid = false;
			}
			loadProperties();
			verifyName();
			if (isValid) {
				loadClass(false);
			}
		}
	}

	// stronger than update
	public boolean validate() {
		loadClass(true);
		return isValid;
	}

	private boolean loadProperties() {
		if (propertiesUrl != null) {
			InputStream ios = null;

			try {
				URLConnection con = propertiesUrl.openConnection();

				con.setUseCaches(false);
				ios = con.getInputStream();
				properties.load(ios);
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
		if (fullClassName.contains("$")) {
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
				final String message = "Robot " + fullClassName + " has package name too long.  "
						+ MAX_FULL_PACKAGE_NAME_LENGTH + " characters maximum please.";

				if (!silent) {
					logError(message);
				}
				return false;
			}
		}

		if (shortClassName != null && shortClassName.length() > MAX_SHORT_CLASS_NAME_LENGTH) {
			final String message = "Robot " + fullClassName + " has classname too long.  " + MAX_SHORT_CLASS_NAME_LENGTH
					+ " characters maximum please.";

			if (!silent) {
				logError(message);
			}
			return false;
		}

		return true;
	}

	private void loadClass(boolean resolve) {
		IRobotClassLoader loader = null;

		try {
			final IHostManager hostManager = net.sf.robocode.core.Container.getComponent(IHostManager.class);

			loader = hostManager.createLoader(this);
			Class<?> robotClass = loader.loadRobotMainClass(resolve);

			if (robotClass == null || java.lang.reflect.Modifier.isAbstract(robotClass.getModifiers())) {
				// this class is not robot
				isValid = false;
				return;
			}
			checkInterfaces(robotClass);
			isValid = isJuniorRobot || isStandardRobot || isAdvancedRobot;

		} catch (Throwable t) {
			isValid = false;
			if (isExpectedRobot || isClassURL) {
				logError(getFullClassName() + ": Got an error with this class: " + t.toString()); // just message here
			}
		} finally {
			if (loader != null) {
				loader.cleanup();
			}
		}
	}

	private void checkInterfaces(Class<?> robotClass) {
		if (Droid.class.isAssignableFrom(robotClass)) {
			isDroid = true;
		}

		if (ITeamRobot.class.isAssignableFrom(robotClass)) {
			isTeamRobot = true;
		}

		if (IAdvancedRobot.class.isAssignableFrom(robotClass)) {
			isAdvancedRobot = true;
		}

		if (IInteractiveRobot.class.isAssignableFrom(robotClass)) {
			// in this case we make sure that robot don't waste time
			if (checkMethodOverride(robotClass, Robot.class, "getInteractiveEventListener")
					|| checkMethodOverride(robotClass, Robot.class, "onKeyPressed", KeyEvent.class)
					|| checkMethodOverride(robotClass, Robot.class, "onKeyReleased", KeyEvent.class)
					|| checkMethodOverride(robotClass, Robot.class, "onKeyTyped", KeyEvent.class)
					|| checkMethodOverride(robotClass, Robot.class, "onMouseClicked", MouseEvent.class)
					|| checkMethodOverride(robotClass, Robot.class, "onMouseEntered", MouseEvent.class)
					|| checkMethodOverride(robotClass, Robot.class, "onMouseExited", MouseEvent.class)
					|| checkMethodOverride(robotClass, Robot.class, "onMousePressed", MouseEvent.class)
					|| checkMethodOverride(robotClass, Robot.class, "onMouseReleased", MouseEvent.class)
					|| checkMethodOverride(robotClass, Robot.class, "onMouseMoved", MouseEvent.class)
					|| checkMethodOverride(robotClass, Robot.class, "onMouseDragged", MouseEvent.class)
					|| checkMethodOverride(robotClass, Robot.class, "onMouseWheelMoved", MouseWheelEvent.class)
					) {
				isInteractiveRobot = true;
			}
		}

		if (IPaintRobot.class.isAssignableFrom(robotClass)) {
			if (checkMethodOverride(robotClass, Robot.class, "getPaintEventListener")
					|| checkMethodOverride(robotClass, Robot.class, "onPaint", Graphics2D.class)
					) {
				isPaintRobot = true;
			}
		}

		if (Robot.class.isAssignableFrom(robotClass) && !isAdvancedRobot) {
			isStandardRobot = true;
		}

		if (IJuniorRobot.class.isAssignableFrom(robotClass)) {
			isJuniorRobot = true;
			if (isAdvancedRobot) {
				throw new AccessControlException(
						getFullClassName() + ": Junior robot should not implement IAdvancedRobot interface.");
			}
		}

		if (IBasicRobot.class.isAssignableFrom(robotClass)) {
			if (!(isAdvancedRobot || isJuniorRobot)) {
				isStandardRobot = true;
			}
		}
	}

	private boolean checkMethodOverride(Class<?> robotClass, Class<?> knownBase, String name, Class<?>... parameterTypes) {
		if (knownBase.isAssignableFrom(robotClass)) {
			final Method getInteractiveEventListener;

			try {
				getInteractiveEventListener = robotClass.getMethod(name, parameterTypes);
			} catch (NoSuchMethodException e) {
				return false;
			}
			if (getInteractiveEventListener.getDeclaringClass().equals(knownBase)) {
				return false;
			}
		}
		return true;
	}

	public void storeProperties(OutputStream os) throws IOException {
		properties.store(os, "Robocode Robot");
	}

	public boolean isDroid() {
		return isDroid;
	}

	public boolean isTeamRobot() {
		return isTeamRobot;
	}

	public boolean isAdvancedRobot() {
		return isAdvancedRobot;
	}

	public boolean isStandardRobot() {
		return isStandardRobot;
	}

	public boolean isInteractiveRobot() {
		return isInteractiveRobot;
	}

	public boolean isPaintRobot() {
		return isPaintRobot;
	}

	public boolean isJuniorRobot() {
		return isJuniorRobot;
	}

	public URL getRobotClassPath() {
		return root.getClassPathUrl();
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
		if (getRootPackage() == null){
			return null;
		}
		if (root.isPackage()){
			String jarFile = root.getClassPathUrl().getFile();
			jarFile = jarFile.substring(jarFile.lastIndexOf('/')+1, jarFile.length());
			return FileUtil.getCacheDir() + File.separator + jarFile + "_" + File.separator + getRootPackage();
		}else{
			return FileUtil.getCacheDir() + File.separator + getRootPackage();
		}
	}

	public String getWritableDirectory() {
		if (getRootPackage() == null){
			return null;
		}
		if (root.isPackage()){
			String jarFile = root.getClassPathUrl().getFile();
			jarFile = jarFile.substring(jarFile.lastIndexOf('/')+1, jarFile.length());
			return FileUtil.getCacheDir() + File.separator + jarFile + "_" + File.separator + getFullPackage();
		}else{
			return FileUtil.getCacheDir() + File.separator + getFullPackage();
		}
	}

	public RobotSpecification createRobotSpecification(RobotSpecification battleRobotSpec, String teamName) {
		RobotSpecification specification;

		if (battleRobotSpec != null) {
			specification = battleRobotSpec;
		} else {
			specification = createRobotSpecification();
		}
		if (teamName != null) {
			HiddenAccess.setTeamName(specification, teamName);
		}
		return specification;
	}

	public String toString() {
		return url.toString();
	}
}

