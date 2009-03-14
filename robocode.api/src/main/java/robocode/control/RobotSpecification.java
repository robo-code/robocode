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


import net.sf.robocode.security.IHiddenSpecificationHelper;

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

	private final Object fileSpecification;
	private String teamName;
	private final String name;
	private final String author;
	private final String webpage;
	private final String version;
	private final String robocodeVersion;
	private final String jarFile;
	private final String fullClassName;
	private final String description;

	private RobotSpecification(Object fileSpecification, String name, String author, String webpage, String version, String robocodeVersion, String jarFile, String fullClassName, String description) {
		this.fileSpecification = fileSpecification;
		this.name = name;
		this.author = author;
		this.webpage = webpage;
		this.version = version;
		this.robocodeVersion = robocodeVersion;
		this.jarFile = jarFile;
		this.fullClassName = fullClassName;
		this.description = description;
	}

	/**
	 * Returns the name of this robot or team.
	 *
	 * @return the name of this robot or team.
	 * @see #getVersion()
	 * @see #getNameAndVersion()
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the version of this robot or team.
	 *
	 * @return the version of this robot or team.
	 * @see #getName()
	 * @see #getNameAndVersion()
	 */
	public String getVersion() {
		return version;
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
		return fullClassName;
	}

	/**
	 * Returns the JAR file containing this robot or team, or {@code null} if it
	 * does not come from a JAR file (could be class files instead).
	 *
	 * @return the JAR file containing this robot or team, or {@code null} if it
	 *         does not come from a JAR file (could be class files instead).
	 */
	public File getJarFile() {
		return new File(jarFile);
	}

	/**
	 * Returns the description provided by the author of this robot.
	 *
	 * @return the description provided by the author of this robot.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Returns the version of Robocode this robot was based on.
	 *
	 * @return the version of Robocode this robot was based on.
	 */
	public String getRobocodeVersion() {
		return robocodeVersion;
	}

	/**
	 * Returns the web page for this robot.
	 *
	 * @return the web page for this robot.
	 */
	public String getWebpage() {
		return webpage;
	}

	/**
	 * Returns the name of this robot's author.
	 *
	 * @return the name of this robot's author.
	 */
	public String getAuthorName() {
		return author;
	}

	/**
	 * @return id of the team in current battle
	 */
	public String getTeamId() {
		return teamName;
	}

	static IHiddenSpecificationHelper createHiddenHelper() {
		return new HiddenHelper();
	}

	private static class HiddenHelper implements IHiddenSpecificationHelper {

		public RobotSpecification createSpecification(Object fileSpecification, String name, String author, String webpage, String version, String robocodeVersion, String jarFile, String fullClassName, String description) {
			return new RobotSpecification(fileSpecification, name, author, webpage, version, robocodeVersion, jarFile,
					fullClassName, description);
		}

		public Object getFileSpecification(RobotSpecification specification) {
			return specification.fileSpecification;
		}

		public void setTeamName(RobotSpecification specification, String teamName) {
			specification.teamName = teamName;
		}

		public String getTeamName(RobotSpecification specification) {
			return specification.teamName;
		}
	}

}
