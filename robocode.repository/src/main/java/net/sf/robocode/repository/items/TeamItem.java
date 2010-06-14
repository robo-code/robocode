/*******************************************************************************
 * Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package net.sf.robocode.repository.items;


import net.sf.robocode.io.FileUtil;
import net.sf.robocode.io.Logger;
import net.sf.robocode.io.URLJarCollector;
import net.sf.robocode.repository.IRepositoryItem;
import net.sf.robocode.repository.root.IRepositoryRoot;
import net.sf.robocode.core.Container;
import net.sf.robocode.version.IVersionManager;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;


/**
 * @author Pavel Savara (original)
 */
public class TeamItem extends NamedItem implements IRepositoryItem {
	private static final long serialVersionUID = 1L;

	private final static String TEAM_DESCRIPTION = "team.description";
	private final static String TEAM_AUTHOR_NAME = "team.author.name";
	// private final static String TEAM_AUTHOR_EMAIL = "team.author.email";
	private final static String TEAM_AUTHOR_WEBSITE = "team.author.website";
	private final static String TEAM_VERSION = "team.version";
	private final static String TEAM_WEBPAGE = "team.webpage";
	private final static String TEAM_MEMBERS = "team.members";
	private final static String ROBOCODE_VERSION = "robocode.version";

	private final String fullTeamName;

	public TeamItem(URL itemURL, IRepositoryRoot root) {
		super(itemURL, root);
		String tUrl = itemURL.toString();

		tUrl = tUrl.substring(0, tUrl.lastIndexOf(".team"));
		final int versionSeparator = tUrl.lastIndexOf(" ");
		final int rootLen = root.getURL().toString().length();

		if (versionSeparator != -1) {
			fullTeamName = tUrl.substring(rootLen, versionSeparator).replace('/', '.').replace('\\', '.');
		} else {
			fullTeamName = tUrl.substring(rootLen).replace('/', '.').replace('\\', '.');
		}
		if (loadProperties()) {
			isValid = true;
		}
	}

	private void htmlURLFromPropertiesURL() {
		try {
			htmlURL = new URL(itemURL.toString().replaceAll("\\.team", ".html"));

			// test that html file exists
			final URLConnection conn = URLJarCollector.openConnection(htmlURL);

			conn.getInputStream().close();
		} catch (IOException ignored) {
			// doesn't exist
			htmlURL = null;
		}
	}

	public List<String> getFriendlyURLs() {
		final Set<String> urls = new HashSet<String>();

		final String tUrl = itemURL.toString();
		final String noType = tUrl.substring(0, tUrl.lastIndexOf('.'));

		urls.add(tUrl);
		urls.add(noType);
		urls.add(itemURL.getPath());

		if (System.getProperty("TESTING", "false").equals("true")) {
			urls.add(getFullClassName());
		} else {
			urls.add(getUniqueFullClassName());
		}
		urls.add(getUniqueFullClassNameWithVersion());

		return new ArrayList<String>(urls);
	}

	public void update(long lastModified, boolean force) {
		if (lastModified > this.lastModified || force) {
			this.lastModified = lastModified;
			loadProperties();
		}
	}

	private boolean loadProperties() {
		if (itemURL != null) {
			InputStream ios = null;

			try {
				final URLConnection connection = URLJarCollector.openConnection(itemURL);

				ios = connection.getInputStream();
				
				properties.load(ios);
				return true;
			} catch (IOException e) {
				Logger.logError(e);
			} finally {
				FileUtil.cleanupStream(ios);
			}
		}
		return false;
	}

	public URL getHtmlURL() {
		// lazy
		if (htmlURL == null) {
			htmlURLFromPropertiesURL();
		}
		return htmlURL;
	}

	public URL getPropertiesURL() {
		return itemURL;
	}

	public boolean isTeam() {
		return true;
	}

	public String getFullClassName() {
		return fullTeamName;
	}

	public String getMembers() {
		return properties.getProperty(TEAM_MEMBERS, null);
	}

	public String getVersion() {
		return properties.getProperty(TEAM_VERSION, null);
	}

	public String getDescription() {
		return properties.getProperty(TEAM_DESCRIPTION, null);
	}

	public String getAuthorName() {
		return properties.getProperty(TEAM_AUTHOR_NAME, null);
	}

	public URL getWebpage() {
		try {
			return new URL(properties.getProperty(TEAM_AUTHOR_WEBSITE, null));
		} catch (MalformedURLException e) {
			return null;
		}
	}

	public boolean isJavaSourceIncluded() {
		return false;
	}

	public String getRobocodeVersion() {
		return properties.getProperty(ROBOCODE_VERSION, null);
	}

	public String toString() {
		return itemURL.toString();
	}

	public void storeProperties(OutputStream os) throws IOException {
		properties.store(os, "Robocode Robot Team");
	}

	public void storeProperties(OutputStream os, URL web, String desc, String author, String version) throws IOException {
		Properties copy = (Properties) properties.clone();

		if (version != null) {
			copy.setProperty(TEAM_VERSION, version);
		}
		if (desc != null) {
			copy.setProperty(TEAM_DESCRIPTION, desc);
		}
		if (author != null) {
			copy.setProperty(TEAM_AUTHOR_NAME, author);
		}
		if (web != null) {
			copy.setProperty(TEAM_WEBPAGE, web.toString());
		}
		final IVersionManager vm = Container.getComponent(IVersionManager.class);

		copy.setProperty(ROBOCODE_VERSION, vm.getVersion());
		copy.store(os, "Robocode Robot");
	}

	public static void createOrUpdateTeam(File target, URL web, String desc, String author, String members, String teamVersion, String robocodeVersion) throws IOException {
		FileOutputStream os = null;

		try {
			Properties team = loadTeamProperties(target);

			if (robocodeVersion != null) {
				team.setProperty(ROBOCODE_VERSION, robocodeVersion);
			}
			if (web != null) {
				team.setProperty(TEAM_WEBPAGE, web.toString());
			}
			if (desc != null) {
				team.setProperty(TEAM_DESCRIPTION, desc);
			}
			if (author != null) {
				team.setProperty(TEAM_AUTHOR_NAME, author);
			}
			if (members != null) {
				team.setProperty(TEAM_MEMBERS, members);
			}
			if (teamVersion != null) {
				team.setProperty(TEAM_VERSION, teamVersion);
			}

			os = new FileOutputStream(target);
			team.store(os, "Robocode robot team");
		} finally {
			FileUtil.cleanupStream(os);
		}
	}

	private static Properties loadTeamProperties(File target) {
		Properties team = new Properties();

		if (target.exists()) {
			FileInputStream fis = null;

			try {
				fis = new FileInputStream(target);
				team.load(fis);
			} catch (Exception e) {
				Logger.logError(e);
			} finally {
				FileUtil.cleanupStream(fis);
			}
		}
		return team;
	}
}
