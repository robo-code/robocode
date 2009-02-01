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
package net.sf.robocode.repository2.items;


import net.sf.robocode.host.IHostManager;
import net.sf.robocode.host.IRobotClassLoader;
import net.sf.robocode.io.FileUtil;
import net.sf.robocode.io.Logger;
import static net.sf.robocode.io.Logger.logError;
import net.sf.robocode.repository.IRobotRepositoryItem;
import net.sf.robocode.repository2.root.IRepositoryRoot;
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

	private URL propertiesUrl;

	public RobotItem(URL classUrl, URL propUrl, IRepositoryRoot root) {
		super(classUrl, root);
		propertiesUrl = propUrl;
		isValid = true;

		// trying to guess all correct file locations
		propsUrlFromClassUrl();
		classUrlFromProperties();
		classUrlFromPropertiesUrl();
		classNameFromClassUrl(root);
		verifyName();
	}

	// / -------------------------------------
	// / url initialization
	// / -------------------------------------

	private void propsUrlFromClassUrl() {
		if (isValid && propertiesUrl == null && url != null) {
			final String pUrl = url.toString().replaceAll("\\.class", ".properties");

			try {
				propertiesUrl = new URL(pUrl);
			} catch (MalformedURLException e) {
				Logger.logError(e);
			}
		}
	}

	private void classUrlFromProperties() {
		if (isValid && url == null) {
			if (!loadProperties()) {
				isValid = false;
			} else {
				final String cn = properties.getProperty(ROBOT_CLASSNAME, null);

				if (cn != null) {
					try {
						final String cUrl = root.getRootUrl().toString() + cn.replace('.', '/') + ".class";

						url = new URL(cUrl);
					} catch (MalformedURLException e) {
						Logger.logError(e);
					}
				}
			}
		}
	}

	private void classUrlFromPropertiesUrl() {
		if (isValid && url == null) {
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
		if (isValid && getFullClassName() == null) {
			String cUrl = url.toString();

			cUrl = cUrl.substring(0, cUrl.lastIndexOf('.'));
			final String cn = cUrl.substring(root.getRootUrl().toString().length()).replace('/', '.').replace('\\', '.');

			properties.setProperty(ROBOT_CLASSNAME, cn);
		}
	}

	private void htmlUrlFromPropertiesUrl() {
		try {
			htmlUrl = new URL(propertiesUrl.toString().replaceAll("\\.properties", ".html"));
			// test that html file exists
			final URLConnection conn = htmlUrl.openConnection();

			conn.setUseCaches(false);
			conn.getInputStream().close();
		} catch (IOException ignored) {
			// doesn't exist
			htmlUrl = null;
		}
	}

	private void htmlUrlFromClassUrl() {
		if (htmlUrl == null) {
			try {
				htmlUrl = new URL(url.toString().replaceAll("\\.class", ".html"));
				// test that html file exists
				final URLConnection conn = htmlUrl.openConnection();

				conn.setUseCaches(false);
				conn.getInputStream().close();
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

	public void setClassUrl(URL classUrl) {
		this.url = classUrl;
	}

	public void setPropertiesUrl(URL propertiesUrl) {
		this.propertiesUrl = propertiesUrl;
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

			urls.add(pUrl);
			urls.add(pUrl.substring(0, pUrl.lastIndexOf('.')));
			urls.add(propertiesUrl.getFile());
		}
		final String cUrl = url.toString();

		final String c = cUrl.substring(0, cUrl.lastIndexOf('.'));

		urls.add(c);
		urls.add(url.getFile());
		urls.add(getFullClassName());
		urls.add(getUniqueFullClassNameWithVersion());
		return urls;
	}

	public void update(long lastModified, boolean force) {
		if (lastModified > this.lastModified || force) {
			if (force) {
				isValid = true;
			}
			this.lastModified = lastModified;
			if (url == null) {
				isValid = false;
			}
			if (!verifyName()) {
				return;
			}
			loadProperties();
			loadClass(false);
		}
	}

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

		final boolean valid = verifyRobotName(robotName, shortClassName);

		if (!valid) {
			isValid = false;
		}
		return valid;
	}

	public static boolean verifyRobotName(String fullClassName, String shortClassName) {
		if (fullClassName.contains("$")) {
			return false;
		}

		int lIndex = fullClassName.indexOf(".");

		if (lIndex > 0) {
			String rootPackage = fullClassName.substring(0, lIndex);

			if (rootPackage.equalsIgnoreCase("robocode")) {
				logError("Robot " + fullClassName + " ignored.  You cannot use package " + rootPackage);
				return false;
			}

			if (rootPackage.length() > MAX_FULL_PACKAGE_NAME_LENGTH) {
				final String message = "Robot " + fullClassName + " has package name too long.  "
						+ MAX_FULL_PACKAGE_NAME_LENGTH + " characters maximum please.";

				logError(message);
				return false;
			}
		}

		if (shortClassName != null && shortClassName.length() > MAX_SHORT_CLASS_NAME_LENGTH) {
			final String message = "Robot " + fullClassName + " has classname too long.  " + MAX_SHORT_CLASS_NAME_LENGTH
					+ " characters maximum please.";

			logError(message);
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
			logError(getFullClassName() + ": Got an error with this class: " + t.toString()); // just message here
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
		return getRootPackage() == null
				? null
				: root.isPackage()
						? root.getClassPathUrl().getFile() + "_" + File.separator + getRootPackage()
						: root.getClassPathUrl().getFile() + getRootPackage();
	}

	public String getWritableDirectory() {
		return getRootPackage() == null
				? null
				: root.isPackage()
						? root.getClassPathUrl().getFile() + "_" + File.separator + getFullPackage()
						: root.getClassPathUrl().getFile() + getFullPackage();
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

