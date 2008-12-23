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
package robocode.repository;


import net.sf.robocode.io.Logger;
import static net.sf.robocode.io.Logger.logError;
import robocode.Droid;
import robocode.Robot;
import robocode.io.FileUtil;
import robocode.robotinterfaces.*;
import robocode.security.RobotClassLoader;

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
public class RobotFileSpecification extends FileSpecification {
	// Allowed maximum length for a robot's full package name
	private final static int MAX_FULL_PACKAGE_NAME_LENGTH = 32;
	// Allowed maximum length for a robot's short class name
	private final static int MAX_SHORT_CLASS_NAME_LENGTH = 32;

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
		nameManager = new NameManager(name, version, developmentVersion);
	}

	private void loadProperties(String filepath) {
		// Load properties if they exist...
		String pfn = filepath.substring(0, filepath.lastIndexOf(".")) + ".properties";
		File pf = new File(pfn);

		FileInputStream in = null;

		try {
			if (pf.exists()) {
				in = new FileInputStream(pf);

				load(in);
				in.close();
				if (pf.length() == 0) {
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
			Logger.logError("Warning:  Could not load properties file: " + pfn);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ignored) {}
			}
		}

		setThisFileName(pfn);
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

	/**
	 * Sets the robotName.
	 *
	 * @param name The robotName to set
	 */
	public void setName(String name) {
		this.name = name;
		props.setProperty(ROBOT_CLASSNAME, name);
	}

	/**
	 * Sets the robotDescription.
	 *
	 * @param robotDescription The robotDescription to set
	 */
	public void setRobotDescription(String robotDescription) {
		this.description = robotDescription;
		props.setProperty(ROBOT_DESCRIPTION, robotDescription);
	}

	/**
	 * Sets the robotAuthorName.
	 *
	 * @param robotAuthorName The robotAuthorName to set
	 */
	public void setRobotAuthorName(String robotAuthorName) {
		this.authorName = robotAuthorName;
		props.setProperty(ROBOT_AUTHOR_NAME, robotAuthorName);
	}

	/**
	 * Sets the robotAuthorEmail.
	 *
	 * @param robotAuthorEmail The robotAuthorEmail to set
	 */
	public void setRobotAuthorEmail(String robotAuthorEmail) {
		this.authorEmail = robotAuthorEmail;
		props.setProperty(ROBOT_AUTHOR_EMAIL, robotAuthorEmail);
	}

	/**
	 * Sets the robotAuthorWebsite.
	 *
	 * @param robotAuthorWebsite The robotAuthorWebsite to set
	 */
	public void setRobotAuthorWebsite(String robotAuthorWebsite) {
		this.authorWebsite = robotAuthorWebsite;
		props.setProperty(ROBOT_AUTHOR_WEBSITE, robotAuthorWebsite);
	}

	/**
	 * Gets the robotJavaSourceIncluded.
	 *
	 * @return Returns a boolean
	 */
	public boolean getRobotJavaSourceIncluded() {
		return robotJavaSourceIncluded;
	}

	/**
	 * Sets the robotJavaSourceIncluded.
	 *
	 * @param robotJavaSourceIncluded The robotJavaSourceIncluded to set
	 */
	public void setRobotJavaSourceIncluded(boolean robotJavaSourceIncluded) {
		this.robotJavaSourceIncluded = robotJavaSourceIncluded;
		props.setProperty(ROBOT_JAVA_SOURCE_INCLUDED, "" + robotJavaSourceIncluded);
	}

	/**
	 * Sets the robotVersion.
	 *
	 * @param robotVersion The robotVersion to set
	 */
	public void setRobotVersion(String robotVersion) {
		this.version = robotVersion;
		if (robotVersion != null) {
			props.setProperty(ROBOT_VERSION, robotVersion);
		} else {
			props.remove(ROBOT_VERSION);
		}
	}

	/**
	 * Gets the robotClasspath.
	 *
	 * @return Returns a String
	 */
	public String getRobotClassPath() {
		return rootDir.getPath();
	}

	/**
	 * Sets the robotWebpage.
	 *
	 * @param robotWebpage The robotWebpage to set
	 */
	public void setRobotWebpage(URL robotWebpage) {
		this.webpage = robotWebpage;
		if (robotWebpage == null) {
			props.remove(ROBOT_WEBPAGE);
		} else {
			props.setProperty(ROBOT_WEBPAGE, this.webpage.toString());
		}
	}

	/**
	 * Gets the uid.
	 *
	 * @return Returns a String
	 */
	@Override
	public String getUid() {
		return uid;
	}

	/**
	 * Sets the uid.
	 *
	 * @param uid The uid to set
	 */
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

	public boolean update() {

		try {
			RobotClassLoader classLoader = new RobotClassLoader(this);

			classLoader.loadRobotClass();
			Class<?> robotClass = classLoader.loadRobotClass();

			if (!java.lang.reflect.Modifier.isAbstract(robotClass.getModifiers())) {
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
					if (!isAdvancedRobot || !isJuniorRobot) {
						isStandardRobot = true;
					}
				}
			}
			if (!isJuniorRobot && !isStandardRobot && !isAdvancedRobot) {
				// this class is not robot
				return false;
			}
			setUid(classLoader.getUid());
			return true;
		} catch (Throwable t) {
			logError(getName() + ": Got an error with this class: " + t.toString()); // just message here
			return false;
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
		return verifyRobotName(getName(), getShortClassName());
	}

	public static boolean verifyRobotName(String robotName, String shortClassName) {
		int lIndex = robotName.indexOf(".");

		if (lIndex > 0) {
			String rootPackage = robotName.substring(0, lIndex);

			if (rootPackage.equalsIgnoreCase("robocode")) {
				logError("Robot " + robotName + " ignored.  You cannot use package " + rootPackage);
				return false;
			}

			if (rootPackage.length() > MAX_FULL_PACKAGE_NAME_LENGTH) {
				final String message = "Robot " + robotName + " has package name too long.  "
						+ MAX_FULL_PACKAGE_NAME_LENGTH + " characters maximum please.";

				logError(message);
				return false;
			}
		}

		if (shortClassName != null && shortClassName.length() > MAX_SHORT_CLASS_NAME_LENGTH) {
			final String message = "Robot " + robotName + " has classname too long.  " + MAX_SHORT_CLASS_NAME_LENGTH
					+ " characters maximum please.";

			logError(message);
			return false;
		}

		return true;
	}

}
