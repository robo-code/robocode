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
 *     Flemming N. Larsen
 *     - Replaced FileSpecificationVector with plain Vector
 *     - Ported to Java 5
 *     - Code cleanup
 *     Robert D. Maupin
 *     - Replaced old collection types like Vector and Hashtable with
 *       synchronized List and HashMap
 *******************************************************************************/
package robocode.repository;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Robert D. Maupin (contributor)
 */
public class Repository {
	private List<FileSpecification> fileSpecifications = Collections.synchronizedList(new ArrayList<FileSpecification>());

	public void add(FileSpecification fileSpecification) {
		fileSpecifications.add(fileSpecification);
	}

	public List<FileSpecification> getRobotSpecificationsList(boolean onlyWithSource, boolean onlyWithPackage,
			boolean onlyRobots, boolean onlyDevelopment, boolean onlyNotDevelopment, boolean ignoreTeamRobots) {

		List<FileSpecification> v = Collections.synchronizedList(new ArrayList<FileSpecification>());

		for (FileSpecification spec : fileSpecifications) {

			if (spec.isDuplicate()) {
				continue;
			}
			if (!(spec instanceof RobotSpecification)) {
				if (onlyRobots) {
					continue;
				}
				if (onlyDevelopment && spec.getFullPackage() != null
						&& (spec.getFullPackage().equals("sample") || spec.getFullPackage().equals("sampleteam"))) {
					continue;
				}
				if (onlyDevelopment && !spec.isDevelopmentVersion()) {
					continue;
				}
			} else {
				RobotSpecification robotSpec = (RobotSpecification) spec;

				if (onlyWithSource && !robotSpec.getRobotJavaSourceIncluded()) {
					continue;
				}
				if (onlyWithPackage && robotSpec.getFullPackage() == null) {
					continue;
				}
				if (onlyDevelopment && !robotSpec.isDevelopmentVersion()) {
					continue;
				}
				if (onlyNotDevelopment && robotSpec.isDevelopmentVersion()) {
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

