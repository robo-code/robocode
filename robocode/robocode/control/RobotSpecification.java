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
 *     Flemming N. Lasen
 *     - Code cleanup
 *     - Added the getNameAndVersion() method
 *     - Changed to use the FileSpecification as local specification instead of
 *       RobotFileSpecification. This change was done in order to support teams
 *     - This class now implements java.io.Serializable
 *     - Updated the Javadocs
 *******************************************************************************/
package robocode.control;


import robocode.repository.FileSpecification;

import java.io.File;


/**
 * Defines the properties of a robot, which is returned from
 * {@link RobocodeEngine#getLocalRepository()} or
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public class RobotSpecification implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private FileSpecification fileSpecification;

	/**
	 * This constructor is called by the game in order to construct a new
	 * RobotSpecification.
	 *
	 * @param fileSpecification the file specification of the robot
	 */
	RobotSpecification(FileSpecification fileSpecification) {
		this.fileSpecification = fileSpecification;
	}

	/**
	 * Returns the name of this robot or team.
	 *
	 * @return the name of this robot or team.
	 * @see #getVersion()
	 * @see #getNameAndVersion()
	 */
	public String getName() {
		return fileSpecification.getName();
	}

	/**
	 * Returns the version of this robot or team.
	 *
	 * @return the version of this robot or team.
	 * @see #getName()
	 * @see #getNameAndVersion()
	 */
	public String getVersion() {
		if (fileSpecification.getVersion() != null) {
			if (fileSpecification.isDevelopmentVersion()) {
				return fileSpecification.getVersion() + "*";
			}
		}
		return fileSpecification.getVersion();
	}

	/**
	 * Returns the name and version of this robot or team.
	 *
	 * @return the name and version of this robot or team.
	 * @see #getName()
	 * @see #getVersion()
	 * @since 1.3
	 */
	public String getNameAndVersion() {
		String nameAndVersion = getName();
		String version = getVersion();

		if (version != null && version.trim().length() > 0) {
			nameAndVersion += ' ' + version;
		}
		return nameAndVersion;
	}

	/**
	 * Returns the full class name of this robot or team.
	 *
	 * @return the full class name of this robot or team.
	 */
	public String getClassName() {
		return fileSpecification.getFullClassName();
	}

	/**
	 * Returns the JAR file containing this robot or team, or {@code null} if it
	 * does not come from a JAR file (could be class files instead).
	 *
	 * @return the JAR file containing this robot or team, or {@code null} if it
	 *         does not come from a JAR file (could be class files instead).
	 */
	public File getJarFile() {
		return fileSpecification.getJarFile();
	}

	/**
	 * Returns the description provided by the author of this robot.
	 *
	 * @return the description provided by the author of this robot.
	 */
	public String getDescription() {
		return fileSpecification.getDescription();
	}

	/**
	 * Returns the version of Robocode this robot was based on.
	 *
	 * @return the version of Robocode this robot was based on.
	 */
	public String getRobocodeVersion() {
		return fileSpecification.getRobocodeVersion();
	}

	/**
	 * Returns the web page for this robot.
	 *
	 * @return the web page for this robot.
	 */
	public String getWebpage() {
		return (fileSpecification.getWebpage() != null) ? fileSpecification.getWebpage().toString() : null;
	}

	/**
	 * Returns the name of this robot's author.
	 *
	 * @return the name of this robot's author.
	 */
	public String getAuthorName() {
		return fileSpecification.getAuthorName();
	}
}
