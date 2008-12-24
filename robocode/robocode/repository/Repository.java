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
 *     - Replaced FileSpecificationVector with plain Vector
 *     - Ported to Java 5
 *     - Code cleanup
 *     - Bugfixed to handle TeamSpecification as well and the new sampleex
 *       robots
 *     Robert D. Maupin
 *     - Replaced old collection types like Vector and Hashtable with
 *       synchronized List and HashMap
 *******************************************************************************/
package robocode.repository;


import net.sf.robocode.repository.INamedFileSpecification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Robert D. Maupin (contributor)
 */
class Repository {
	private final List<NamedFileSpecification> fileSpecifications = Collections.synchronizedList(
			new ArrayList<NamedFileSpecification>());
	private final Hashtable<String, NamedFileSpecification> fileSpecificationsDict = new Hashtable<String, NamedFileSpecification>();

	public void add(NamedFileSpecification fileSpecification) {
		fileSpecifications.add(fileSpecification);
		final String name = fileSpecification.getFullClassNameWithVersion();
		final String unname = fileSpecification.getUniqueFullClassNameWithVersion();
		final String rootDir = fileSpecification.getRootDir().toString();

		fileSpecificationsDict.put(name, fileSpecification);
		fileSpecificationsDict.put(rootDir + name, fileSpecification);
		if (!name.equals(unname)) {
			fileSpecificationsDict.put(unname, fileSpecification);
			fileSpecificationsDict.put(rootDir + unname, fileSpecification);
		}
	}

	public FileSpecification get(String fullClassNameWithVersion) {
		return fileSpecificationsDict.get(fullClassNameWithVersion);
	}

	public List<INamedFileSpecification> getRobotSpecificationsList(boolean onlyWithSource, boolean onlyWithPackage,
			boolean onlyRobots, boolean onlyDevelopment, boolean onlyNotDevelopment, boolean ignoreTeamRobots) {

		List<INamedFileSpecification> result = Collections.synchronizedList(new ArrayList<INamedFileSpecification>());

		for (NamedFileSpecification spec : fileSpecifications) {
			if (!spec.isValid()) {
				continue;
			}
			if (spec.isDuplicate()) {
				continue;
			}
			if (!(spec instanceof RobotFileSpecification) && onlyRobots) {
				continue;
			} else {
				if (onlyWithPackage && spec.getFullPackage() == null) {
					continue;
				}
				if (onlyNotDevelopment && spec.isDevelopmentVersion()) {
					continue;
				}

				if (spec instanceof RobotFileSpecification) {
					RobotFileSpecification robotSpec = (RobotFileSpecification) spec;

					if (onlyWithSource && !robotSpec.getRobotJavaSourceIncluded()) {
						continue;
					}

					if (ignoreTeamRobots && robotSpec.isTeamRobot()) {
						continue;
					}
				} else if (spec instanceof TeamFileSpecification) {
					TeamFileSpecification teamSpec = (TeamFileSpecification) spec;

					if (onlyWithSource && !teamSpec.getTeamJavaSourceIncluded()) {
						continue;
					}
				}
			}
			if (onlyDevelopment) {
				if (!spec.isDevelopmentVersion()) {
					continue;
				}

				String fullPackage = spec.getFullPackage();

				if (fullPackage != null
						&& (fullPackage.equals("sample") || fullPackage.equals("sampleteam") || fullPackage.equals("sampleex"))) {
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
			result.add(spec);
		}
		return result;
	}

	public void sortRobotSpecifications() {
		Collections.sort(fileSpecifications);
	}
}

