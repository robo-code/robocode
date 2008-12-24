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
 *     - Code cleanup
 *     - Updated to use methods from FileUtil and Logger, which replaces methods
 *       that have been (re)moved from the robocode.util.Utils class
 *******************************************************************************/
package robocode.repository;


import net.sf.robocode.io.FileUtil;
import net.sf.robocode.io.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
@SuppressWarnings("serial")
class TeamFileSpecification extends NamedFileSpecification implements ITeamFileSpecificationExt {
	private final static String TEAM_DESCRIPTION = "team.description";
	private final static String TEAM_AUTHOR_NAME = "team.author.name";
	private final static String TEAM_AUTHOR_EMAIL = "team.author.email";
	private final static String TEAM_AUTHOR_WEBSITE = "team.author.website";
	private final static String TEAM_VERSION = "team.version";
	private final static String TEAM_WEBPAGE = "team.webpage";
	private final static String TEAM_MEMBERS = "team.members";
	private final static String TEAM_JAVA_SOURCE_INCLUDED = "team.java.source.included";

	protected boolean teamJavaSourceIncluded = false;

	private String members = "";
	private String uid = "";

	// Used in FileSpecification
	protected TeamFileSpecification(File f, File rootDir, String prefix, boolean developmentVersion) {
		this.developmentVersion = developmentVersion;
		this.rootDir = rootDir;
		valid = true;
		String filename = f.getName();
		String fileType = FileUtil.getFileType(filename);

		if (fileType.equals(".team")) {
			try {
				FileInputStream in = new FileInputStream(f);

				load(in);
			} catch (IOException e) {
				Logger.logError("Warning:  Could not load team: " + f);
			}
			if (filename.indexOf(" ") >= 0) {
				setName(prefix + FileUtil.getClassName(filename.substring(0, filename.indexOf(" "))));
			} else {
				setName(prefix + FileUtil.getClassName(filename));
			}
			setFileLastModified(f.lastModified());
			setFileLength(f.length());
			setFileType(".team");
			try {
				setFilePath(f.getCanonicalPath());
			} catch (IOException e) {
				Logger.logError("Warning:  Unable to determine canonical path for " + f.getPath());
				setFilePath(f.getPath());
			}
			setPropertiesFileName(f.getPath());
			setFileName(f.getName());
		} else {
			throw new RuntimeException("TeamFileSpecification can only be constructed from a .team file");
		}
		byte mb[] = getMembers().getBytes();
		long uid1 = 0;

		for (byte element : mb) {
			uid1 += element;
		}
		long uid2 = mb.length;

		uid = uid1 + "" + uid2;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public String getUid() {
		return uid;
	}

	public TeamFileSpecification() {}

	@Override
	public void load(FileInputStream in) throws IOException {
		super.load(in);
		authorEmail = props.getProperty(TEAM_AUTHOR_EMAIL);
		authorName = props.getProperty(TEAM_AUTHOR_NAME);
		authorWebsite = props.getProperty(TEAM_AUTHOR_WEBSITE);
		description = props.getProperty(TEAM_DESCRIPTION);
		version = props.getProperty(TEAM_VERSION);
		if (version != null && version.length() == 0) {
			version = null;
		}
		members = props.getProperty(TEAM_MEMBERS);
		try {
			String team_webpage = props.getProperty(TEAM_WEBPAGE);

			if (team_webpage != null) {
				webpage = new URL(team_webpage);
			}
		} catch (MalformedURLException e) {
			webpage = null;
		}
		teamJavaSourceIncluded = Boolean.valueOf(props.getProperty(TEAM_JAVA_SOURCE_INCLUDED, "false"));
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setTeamDescription(String teamDescription) {
		this.description = teamDescription;
		props.setProperty(TEAM_DESCRIPTION, teamDescription);
	}

	public void setTeamAuthorName(String teamAuthorName) {
		this.authorName = teamAuthorName;
		props.setProperty(TEAM_AUTHOR_NAME, teamAuthorName);
	}

	public void setTeamAuthorEmail(String teamAuthorEmail) {
		this.authorEmail = teamAuthorEmail;
		props.setProperty(TEAM_AUTHOR_EMAIL, teamAuthorEmail);
	}

	public void setTeamAuthorWebsite(String teamAuthorWebsite) {
		this.authorWebsite = teamAuthorWebsite;
		props.setProperty(TEAM_AUTHOR_WEBSITE, teamAuthorWebsite);
	}

	public void setTeamVersion(String teamVersion) {
		this.version = teamVersion;
		if (version != null && version.length() == 0) {
			version = null;
		}
		if (version != null) {
			props.setProperty(TEAM_VERSION, version);
		} else {
			props.remove(TEAM_VERSION);
		}
	}

	public void setTeamWebpage(URL teamWebpage) {
		this.webpage = teamWebpage;
		if (teamWebpage != null) {
			props.setProperty(TEAM_WEBPAGE, teamWebpage.toString());
		} else {
			props.remove(TEAM_WEBPAGE);
		}
	}

	public String getMembers() {
		return members;
	}

	public void setMembers(String members) {
		this.members = members;
		props.setProperty(TEAM_MEMBERS, members);
	}

	public void addMember(RobotFileSpecification robotFileSpecification) {
		if (members == null || members.length() == 0) {
			members = robotFileSpecification.getFullClassNameWithVersion();
		} else {
			members += "," + robotFileSpecification.getFullClassNameWithVersion();
		}
		props.setProperty(TEAM_MEMBERS, members);
	}

	public boolean getTeamJavaSourceIncluded() {
		return teamJavaSourceIncluded;
	}

	public void setTeamJavaSourceIncluded(boolean teamJavaSourceIncluded) {
		this.teamJavaSourceIncluded = teamJavaSourceIncluded;
		props.setProperty(TEAM_JAVA_SOURCE_INCLUDED, "" + teamJavaSourceIncluded);
	}
}
