/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Updated to use methods from FileUtil and Logger, which replaces methods
 *       that have been (re)moved from the robocode.util.Utils class
 *     - Code cleanup
 *     Pavel Savara
 *     - Re-work of robot interfaces
 *******************************************************************************/
package net.sf.robocode.repository;


import net.sf.robocode.host.IHostManager;
import net.sf.robocode.io.FileUtil;
import net.sf.robocode.io.Logger;
import static net.sf.robocode.io.Logger.logError;
import robocode.Droid;
import robocode.Robot;
import robocode.robotinterfaces.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessControlException;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Pavel Savara (contributor)
 */
@SuppressWarnings("serial")
class RobotFileSpecification extends NamedFileSpecification implements IRobotFileSpecificationExt {
	private final static String ROBOT_DESCRIPTION = "robot.description";
	private final static String ROBOT_AUTHOR_NAME = "robot.author.name";
	private final static String ROBOT_AUTHOR_EMAIL = "robot.author.email";
	private final static String ROBOT_AUTHOR_WEBSITE = "robot.author.website";
	private final static String ROBOT_JAVA_SOURCE_INCLUDED = "robot.java.source.included";
	private final static String ROBOT_VERSION = "robot.version";
	private final static String ROBOT_CLASSNAME = "robot.classname";
	private final static String ROBOT_WEBPAGE = "robot.webpage";

	private String uid = "";

	protected boolean robotJavaSourceIncluded;

	private boolean isJuniorRobot;
	private boolean isStandardRobot;
	private boolean isInteractiveRobot;
	private boolean isPaintRobot;
	private boolean isAdvancedRobot;
	private boolean isTeamRobot;
	private boolean isDroid;

	// Used in RepositoryManager
	protected RobotFileSpecification(File f, File rootDir, String prefix, boolean developmentVersion) {
		valid = true;
		String filename = f.getName();
		String filepath = f.getPath();
		String fileType = FileUtil.getFileType(filename);

		this.rootDir = rootDir; 

		this.developmentVersion = developmentVersion;
		if (prefix.length() == 0 && fileType.equals(".jar")) {
			throw new RuntimeException("Robot Specification can only be constructed from a .class file");
		} else if (fileType.equals(".team")) {
			throw new RuntimeException("Robot Specification can only be constructed from a .class file");
		} else if (fileType.equals(".java")) {
			loadProperties(filepath);
			setName(prefix + FileUtil.getClassName(filename));
		} else if (fileType.equals(".class")) {
			loadProperties(filepath);
			setName(prefix + FileUtil.getClassName(filename));
			if (isDevelopmentVersion()) {
				String jfn = filepath.substring(0, filepath.lastIndexOf(".")) + ".java";
				File jf = new File(jfn);

				if (jf.exists()) {
					setRobotJavaSourceIncluded(true);
				}
			}
		} else if (fileType.equals(".properties")) {
			loadProperties(filepath);
			setName(prefix + FileUtil.getClassName(filename));
		}
	}

	private void loadProperties(String filepath) {
		// Load properties if they exist...
		String propertiesFileName = filepath.substring(0, filepath.lastIndexOf(".")) + ".properties";
		File propertiesFile = new File(propertiesFileName);

		FileInputStream in = null;

		try {
			if (propertiesFile.exists()) {
				in = new FileInputStream(propertiesFile);

				load(in);
				in.close();
				if (propertiesFile.length() == 0) {
					setRobotVersion("?");
					if (!developmentVersion) {
						valid = false;
					}
				}
			} // Don't accept robots in robotcache without .properties file
			else if (!developmentVersion) {
				valid = false;
			}
		} catch (IOException e) {
			// Oh well.
			Logger.logError("Warning:  Could not load properties file: " + propertiesFileName);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ignored) {}
			}
		}

		setPropertiesFileName(propertiesFileName);
		String htmlfn = filepath.substring(0, filepath.lastIndexOf(".")) + ".html";
		File htmlFile = new File(htmlfn);

		if (htmlFile.exists() && (getWebpage() == null || getWebpage().toString().length() == 0)) {
			try {
				setRobotWebpage(htmlFile.toURI().toURL());
			} catch (MalformedURLException e) {
				setRobotWebpage(null);
			}
		}
		File classFile = new File(filepath.substring(0, filepath.lastIndexOf(".")) + ".class");

		if (classFile.exists()) {
			setFileLastModified(classFile.lastModified());
			setFileLength(classFile.length());
			setFileType(".class");
			try {
				setFilePath(classFile.getCanonicalPath());
			} catch (IOException e) {
				Logger.logError("Warning:  Unable to determine canonical path for " + classFile.getPath());
				setFilePath(classFile.getPath());
			}
			setFileName(classFile.getName());
		}
	}

	@Override
	public void load(FileInputStream in) throws IOException {
		super.load(in);
		authorEmail = props.getProperty(ROBOT_AUTHOR_EMAIL);
		authorName = props.getProperty(ROBOT_AUTHOR_NAME);
		authorWebsite = props.getProperty(ROBOT_AUTHOR_WEBSITE);
		description = props.getProperty(ROBOT_DESCRIPTION);
		version = props.getProperty(ROBOT_VERSION);
		if (version != null && version.length() == 0) {
			version = null;
		}
		name = props.getProperty(ROBOT_CLASSNAME);
		String w = props.getProperty(ROBOT_WEBPAGE);

		if (w == null) {
			webpage = null;
		} else if (w.length() == 0) {
			webpage = null;
		} else {
			try {
				webpage = new URL(w);
			} catch (MalformedURLException e) {
				try {
					webpage = new URL("http://" + w);
				} catch (MalformedURLException e2) {
					webpage = null;
				}
			}
		}
		robotJavaSourceIncluded = Boolean.valueOf(props.getProperty(ROBOT_JAVA_SOURCE_INCLUDED, "false"));
	}

	public void setName(String name) {
		this.name = name;
		props.setProperty(ROBOT_CLASSNAME, name);
	}

	public void setRobotDescription(String robotDescription) {
		this.description = robotDescription;
		props.setProperty(ROBOT_DESCRIPTION, robotDescription);
	}

	public void setRobotAuthorName(String robotAuthorName) {
		this.authorName = robotAuthorName;
		props.setProperty(ROBOT_AUTHOR_NAME, robotAuthorName);
	}

	public void setRobotAuthorEmail(String robotAuthorEmail) {
		this.authorEmail = robotAuthorEmail;
		props.setProperty(ROBOT_AUTHOR_EMAIL, robotAuthorEmail);
	}

	public void setRobotAuthorWebsite(String robotAuthorWebsite) {
		this.authorWebsite = robotAuthorWebsite;
		props.setProperty(ROBOT_AUTHOR_WEBSITE, robotAuthorWebsite);
	}

	public boolean getRobotJavaSourceIncluded() {
		return robotJavaSourceIncluded;
	}

	public void setRobotJavaSourceIncluded(boolean robotJavaSourceIncluded) {
		this.robotJavaSourceIncluded = robotJavaSourceIncluded;
		props.setProperty(ROBOT_JAVA_SOURCE_INCLUDED, "" + robotJavaSourceIncluded);
	}

	public void setRobotVersion(String robotVersion) {
		version = robotVersion;
		if (version != null && version.length() == 0) {
			version = null;
		}
		if (version != null) {
			props.setProperty(ROBOT_VERSION, version);
		} else {
			props.remove(ROBOT_VERSION);
		}
	}

	public URL getRobotClassPath() {
		try {
			return rootDir.getCanonicalFile().toURL();
		} catch (MalformedURLException e) {
			throw new Error(e);
		} catch (IOException e) {
			throw new Error(e);
		}
	}

	public String getReadableDirectory() {
		int dotIndex = name.indexOf(".");
		String rootPackage = (dotIndex > 0) ? name.substring(0, dotIndex) : null;

		return rootDir.toString() + File.separator + rootPackage;
	}

	public String getWritableDirectory() {
		return rootDir.toString() + File.separator + getFullPackage();
	}

	public void setRobotWebpage(URL robotWebpage) {
		this.webpage = robotWebpage;
		if (robotWebpage == null) {
			props.remove(ROBOT_WEBPAGE);
		} else {
			props.setProperty(ROBOT_WEBPAGE, this.webpage.toString());
		}
	}

	@Override
	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
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

	public boolean update(IHostManager hostManager) {

		try {
			Class<?> robotClass = hostManager.loadRobotClass(this);

			if (java.lang.reflect.Modifier.isAbstract(robotClass.getModifiers())) {
				// this class is not robot
				return false;
			}
			checkInterfaces(robotClass);
			return isJuniorRobot || isStandardRobot || isAdvancedRobot;
		} catch (Throwable t) {
			logError(getName() + ": Got an error with this class: " + t.toString()); // just message here
			return false;
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
						getName() + ": Junior robot should not implement IAdvancedRobot interface.");
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

	public boolean verifyRobotName() {
		return RepositoryManager.verifyRobotNameStatic(getName(), getShortClassName());
	}

}
