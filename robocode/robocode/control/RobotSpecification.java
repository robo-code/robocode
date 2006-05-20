/*******************************************************************************
 * Copyright (c) 2001 Mathew Nelson
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew Nelson - initial API and implementation
 *******************************************************************************/
package robocode.control;


import java.io.*;


/**
 * Defines a robot.
 * This class is returned from {@link RobocodeEngine#getLocalRepository()}
 * @author Mathew A. Nelson
 */
public class RobotSpecification {

	private robocode.repository.RobotSpecification local = null;
	
	RobotSpecification(robocode.repository.RobotSpecification spec) {
		this.local = spec;
	}
	
	/**
	 * Gets the className of this robot
	 * @return the className of this robot
	 */
	public String getClassName() {
		return local.getFullClassName();
	}

	/**
	 * Gets the version of this robot.
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
	 * @return the description of this robot
	 */
	public String getDescription() {
		return local.getDescription();
	}

	/**
	 * Gets the version of Robocode this robot was designed for
	 * @return the version of Robocode this robot was designed for
	 */
	public String getRobocodeVersion() {
		return local.getRobocodeVersion();
	}

	/**
	 * Gets this robot's webpage
	 * @return this robot's webpage
	 */
	public String getWebpage() {
		if (local.getWebpage() == null) {
			return null;
		} else {
			return local.getWebpage().toString();
		}
	}

	/**
	 * Gets the name of this robot's author
	 * @return the name of this robot's author
	 */
	public String getAuthorName() {
		return local.getAuthorName();
	}

	robocode.repository.RobotSpecification getLocal() {
		return local;
	}
}

