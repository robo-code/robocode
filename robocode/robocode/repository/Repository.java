/*******************************************************************************
 * Copyright (c) 2001, 2006 Mathew Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew Nelson - initial API and implementation
 *******************************************************************************/
package robocode.repository;


import robocode.util.*;
import robocode.manager.*;


public class Repository {

	private RobotRepositoryManager repositoryManager;
	
	private Repository() {}
	
	public Repository(RobotRepositoryManager repositoryManager) {
		this.repositoryManager = repositoryManager;
	}
	
	FileSpecificationVector fileSpecifications = new FileSpecificationVector();
	public void add(FileSpecification fileSpecification) {
		fileSpecifications.add(fileSpecification);
	}
	
	public FileSpecificationVector getRobotSpecificationsVector(boolean onlyWithSource, boolean onlyWithPackage, boolean onlyRobots, boolean onlyDevelopment, boolean onlyNotDevelopment, boolean ignoreTeamRobots) {
		// log("Getting robot specs.");
		// if (onlyWithSource == false && onlyWithPackage == false && onlyRobots == false && onlyDevelopment == false && onlyNotDevelopment == false)
		// return (FileSpecificationVector)fileSpecifications.clone();
			
		FileSpecificationVector v = new FileSpecificationVector();

		for (int i = 0; i < fileSpecifications.size(); i++) {
			FileSpecification spec = (FileSpecification) fileSpecifications.elementAt(i);

			if (spec.isDuplicate()) {
				continue;
			}
			if (!(spec instanceof RobotSpecification)) {
				if (onlyRobots) {
					continue;
				}
				if (onlyDevelopment == true && spec.getFullPackage() != null
						&& (spec.getFullPackage().equals("sample") || spec.getFullPackage().equals("sampleteam"))) {
					// log("Ignoring " + spec.getName() + " with package " + spec.getFullPackage());
					continue;
				}
				if (onlyDevelopment == true && spec.isDevelopmentVersion() == false) {
					continue;
				}
			} else {
				RobotSpecification robotSpec = (RobotSpecification) spec;

				if (onlyWithSource == true && robotSpec.getRobotJavaSourceIncluded() == false) {
					continue;
				}
				if (onlyWithPackage == true && robotSpec.getFullPackage() == null) {
					continue;
				}
				if (onlyDevelopment == true && robotSpec.isDevelopmentVersion() == false) {
					continue;
				}
				if (onlyNotDevelopment == true && robotSpec.isDevelopmentVersion() == true) {
					continue;
				}
				if (onlyDevelopment == true && robotSpec.getFullPackage() != null
						&& (robotSpec.getFullPackage().equals("sample") || robotSpec.getFullPackage().equals("sampleteam"))) {
					continue;
				}

				/*
				 if (ignoreTeamRobots == true && (robotSpec.isTeamRobot() || robotSpec.isDroid()))
				 {
				 continue;
				 }
				 */
			}
			String version = spec.getVersion();

			if (version != null) {
				if ((version.indexOf(",") >= 0) || (version.indexOf(" ") >= 0) || (version.indexOf("*") >= 0)
						|| (version.indexOf("(") >= 0) || (version.indexOf(")") >= 0) || // (version.indexOf("[") >= 0) ||
						// (version.indexOf("]") >= 0) ||
						(version.indexOf("{") >= 0) || (version.indexOf("}") >= 0)) {
					continue;
				}
			}
			v.add(spec);
		}
		// log("Returning " + v.size() + " robot specs.");
		return v;
	}

	public void sortRobotSpecifications() {
		fileSpecifications.sort();
	}

	private void log(String s) {
		Utils.log(s);
	}

	private void log(Throwable e) {
		Utils.log(e);
	}

	/*
	 public TeamSpecification findTeam(String name)
	 {
	 else if (currentFileSpecification instanceof TeamSpecification)
	 {
	 TeamSpecification currentTeam = (TeamSpecification)currentFileSpecification;
	 TeamPeer teamManager = new TeamPeer(currentTeam.getName());
	 StringTokenizer teamTokenizer;
	 teamTokenizer = new StringTokenizer(currentTeam.getMembers(),",");
	 while (teamTokenizer.hasMoreTokens())
	 {
	 bot = teamTokenizer.nextToken();
	 log("Looking for: " + bot);
	 RobotSpecification match = null;
	 for (int j = 0; j < robotSpecificationsVector.size();j++)
	 {
	 currentFileSpecification = (FileSpecification)robotSpecificationsVector.elementAt(j);
	 log("Looking at: " + currentFileSpecification.getName());
	 // Teams cannot include teams
	 if (currentFileSpecification instanceof TeamSpecification)
	 continue;
	 
	 if (currentFileSpecification.getNameManager().getUniqueFullClassNameWithVersion().equals(bot))
	 {
	 
	 log("Found " + currentFileSpecification.getNameManager().getUniqueFullClassNameWithVersion() + ", " + currentTeam.getRootDir() + " - " + currentFileSpecification.getRootDir());
	 // Found team member
	 match = (RobotSpecification)currentFileSpecification;
	 if (currentTeam.getRootDir().equals(currentFileSpecification.getRootDir()) ||currentTeam.getRootDir().equals(currentFileSpecification.getRootDir().getParentFile()) )
	 {
	 log("This is a match.");
	 break;
	 }
	 else
	 log("Still looking.");
	 }
	 }
	 battlingRobotsVector.add(new RobotClassManager(match,teamManager));
	 }
	 break;
	 }
	 return null;
	 }
	 
	 public RobotSpecification findRobot(String name)
	 {
	 return findRobot(null,name);
	 }
	 
	 public RobotSpecification findRobot(TeamSpecification team, String name)
	 {
	 RobotSpecification currentRobotSpecification;
	 RobotSpecification match = null;
	 for (int i = 0; i < fileSpecifications.size(); i++)
	 {
	 if (!(fileSpecifications.elementAt(i) instanceof RobotSpecification))
	 continue;
	 
	 currentRobotSpecification = (RobotSpecification)fileSpecifications.elementAt(i);
	 if (currentRobotSpecification.getNameManager().getUniqueFullClassNameWithVersion().equals(name))
	 {
	 if (team == null)
	 {
	 }
	 else
	 {
	 if (team.isDevelopmentVersion())
	 {
	 if (currentRobotSpecification.getRootDir().equals(repositoryManager.getRobotsDir()))
	 {
	 return currentRobotSpecification;	
	 }
	 else if (currentRobotSpecification.getRootDir().equals(repositorymanager.getRobotCacheDir()))
	 {
	 match = currentRobotSpecification;
	 }
	 else if (match == null)
	 {
	 match = currentRobotSpecification;
	 }
	 }
	 if (currentRobotSpecification.getRootDir().getParentFile().equals(team.getRootDir()))
	 return currentRobotSpecification;
	 
	 // Name matches.
	 break;
	 }
	 }
	 }

	 }
	 */
}

