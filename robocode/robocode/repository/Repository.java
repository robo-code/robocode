/*******************************************************************************
 * Copyright (c) 2001-2006 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Replaced FileSpecificationVector with plain Vector
 *     - Ported to Java 5
 *     - Code cleanup
 *******************************************************************************/
package robocode.repository;


import java.util.*;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (current)
 */
public class Repository {
	Vector<FileSpecification> fileSpecifications = new Vector<FileSpecification>();

	public void add(FileSpecification fileSpecification) {
		fileSpecifications.add(fileSpecification);
	}
	
	public Vector getRobotSpecificationsVector(boolean onlyWithSource, boolean onlyWithPackage,
			boolean onlyRobots, boolean onlyDevelopment, boolean onlyNotDevelopment, boolean ignoreTeamRobots) { // <FileSpecification>

		Vector<FileSpecification> v = new Vector<FileSpecification>();

		for (FileSpecification spec : fileSpecifications) {

			if (spec.isDuplicate()) {
				continue;
			}
			if (!(spec instanceof RobotSpecification)) {
				if (onlyRobots) {
					continue;
				}
				if (onlyDevelopment == true && spec.getFullPackage() != null
						&& (spec.getFullPackage().equals("sample") || spec.getFullPackage().equals("sampleteam"))) {
					continue;
				}
				if (onlyDevelopment == true && spec.isDevelopmentVersion() == false) {
					continue;
				}
			} else {
				RobotSpecification robotSpec = (RobotSpecification) spec;

				if (onlyWithSource && robotSpec.getRobotJavaSourceIncluded() == false) {
					continue;
				}
				if (onlyWithPackage && robotSpec.getFullPackage() == null) {
					continue;
				}
				if (onlyDevelopment && robotSpec.isDevelopmentVersion() == false) {
					continue;
				}
				if (onlyNotDevelopment && robotSpec.isDevelopmentVersion() == true) {
					continue;
				}
				if (onlyDevelopment && robotSpec.getFullPackage() != null
						&& (robotSpec.getFullPackage().equals("sample") || robotSpec.getFullPackage().equals("sampleteam"))) {
					continue;
				}
			}
			String version = spec.getVersion();

			if (version != null) {
				if ((version.indexOf(",") >= 0) || (version.indexOf(" ") >= 0) || (version.indexOf("*") >= 0)
						|| (version.indexOf("(") >= 0) || (version.indexOf(")") >= 0) || (version.indexOf("{") >= 0)
						|| (version.indexOf("}") >= 0)) {
					continue;
				}
			}
			v.add(spec);
		}
		return v;
	}

	public void sortRobotSpecifications() {
		Collections.sort(fileSpecifications);
	}
}

