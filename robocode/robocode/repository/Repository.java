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


import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Robert D. Maupin (contributor)
 */
public class Repository {
	private List<FileSpecification> fileSpecifications = Collections.synchronizedList(new ArrayList<FileSpecification>());
	private Hashtable<String, FileSpecification> fileSpecificationsDict = new Hashtable<String, FileSpecification>();

	public void add(FileSpecification fileSpecification) {
		fileSpecifications.add(fileSpecification);
		final String name = fileSpecification.getNameManager().getFullClassNameWithVersion();
		final String rootDir = fileSpecification.getRootDir().toString();

		fileSpecificationsDict.put(name, fileSpecification);
		fileSpecificationsDict.put(rootDir + name, fileSpecification);
	}

	public FileSpecification get(String fullClassNameWithVersion) {
		return fileSpecificationsDict.get(fullClassNameWithVersion);
	}

	public List<FileSpecification> getRobotSpecificationsList(boolean onlyWithSource, boolean onlyWithPackage,
			boolean onlyRobots, boolean onlyDevelopment, boolean onlyNotDevelopment, boolean ignoreTeamRobots) {

		List<FileSpecification> v = Collections.synchronizedList(new ArrayList<FileSpecification>());

		for (FileSpecification spec : fileSpecifications) {
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
				} else if (spec instanceof TeamSpecification) {
					TeamSpecification teamSpec = (TeamSpecification) spec;

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
			v.add(spec);
		}
		return v;
	}

	public void sortRobotSpecifications() {
		Collections.sort(fileSpecifications);
	}
}

