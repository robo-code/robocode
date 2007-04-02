/*******************************************************************************
 * Copyright (c) 2001, 2007 Mathew A. Nelson and Robocode contributors
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
 *       RobotSpecification. This change was done in order to support teams
 *******************************************************************************/
package robocode.control;


import java.io.File;


/**
 * Defines a robot.
 * This class is returned from {@link RobocodeEngine#getLocalRepository()}
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public class RobotSpecification {

	private robocode.repository.FileSpecification local;

	RobotSpecification(robocode.repository.FileSpecification spec) {
		this.local = spec;
	}

	/**
	 * Gets the name of this robot or team
	 * 
	 * @return the name of this robot or team
	 */
	public String getName() {
		return local.getName();
	}

	/**
	 * Gets the name and version of this robot or team
	 * 
	 * @return the name and version of this robot or team
	 * 
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
	 * Gets the className of this robot
	 *
	 * @return the className of this robot
	 */
	public String getClassName() {
		return local.getFullClassName();
	}

	/**
	 * Gets the version of this robot.
	 *
	 * @return the version of this robot.
	 */
	public String getVersion() {
		if (local.getVersion() != null) {
			if (local.isDevelopmentVersion()) {
				return local.getVersion() + "*";
			}
		}
		return local.getVersion();
	}

	/**
	 * Gets the jar file containing this robot.
	 * This method will return null if the robot does not come from a jar
	 * @return the jar file containing this robot
	 */
	public File getJarFile() {
		return local.getJarFile();
	}

	/**
	 * Gets the description of this robot
	 *
	 * @return the description of this robot
	 */
	public String getDescription() {
		return local.getDescription();
	}

	/**
	 * Gets the version of Robocode this robot was designed for
	 *
	 * @return the version of Robocode this robot was designed for
	 */
	public String getRobocodeVersion() {
		return local.getRobocodeVersion();
	}

	/**
	 * Gets this robot's webpage
	 *
	 * @return this robot's webpage
	 */
	public String getWebpage() {
		return (local.getWebpage() != null) ? local.getWebpage().toString() : null;
	}

	/**
	 * Gets the name of this robot's author
	 *
	 * @return the name of this robot's author
	 */
	public String getAuthorName() {
		return local.getAuthorName();
	}
}
