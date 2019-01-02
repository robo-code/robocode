/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.repository.items;


import net.sf.robocode.io.FileUtil;
import net.sf.robocode.io.Logger;
import net.sf.robocode.io.RobocodeProperties;
import net.sf.robocode.io.URLJarCollector;
import net.sf.robocode.repository.IRobotSpecItem;
import net.sf.robocode.repository.RobotProperties;
import net.sf.robocode.repository.TeamProperties;
import net.sf.robocode.repository.root.IRepositoryRoot;
import net.sf.robocode.core.Container;
import net.sf.robocode.version.IVersionManager;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;


/**
 * @author Pavel Savara (original)
 */
public class TeamItem extends RobotSpecItem implements IRobotSpecItem {
	private static final long serialVersionUID = 1L;

	private static final String TEAM_DESCRIPTION = "team.description";
	private static final String TEAM_AUTHOR_NAME = "team.author.name";
	private static final String TEAM_VERSION = "team.version";
	private static final String TEAM_WEBPAGE = "team.webpage";
	private static final String TEAM_MEMBERS = "team.members";
	private static final String ROBOCODE_VERSION = "robocode.version";

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
			htmlURL = new URL(itemUrl.toString().replaceAll("\\.team", ".html"));

			// test that html file exists
			final URLConnection conn = URLJarCollector.openConnection(htmlURL);

			conn.getInputStream().close();
		} catch (IOException ignored) {
			// doesn't exist
			htmlURL = null;
		}
	}

	public Set<String> getFriendlyURLs() {
		Set<String> urls = new HashSet<String>();

		if (itemUrl != null) {
			String url = itemUrl.toString();
			String urlNoType = url.substring(0, url.lastIndexOf('.'));

			String path = itemUrl.getPath();
			String pathNoType = path.substring(0, path.lastIndexOf('.'));
			
			urls.add(urlNoType);
			urls.add(pathNoType);
		}
		if (RobocodeProperties.isTestingOn()) {
			urls.add(getFullClassName());
		} else {
			urls.add(getUniqueFullClassName());
		}
		urls.add(getUniqueFullClassNameWithVersion());

		return urls;
	}

	/**
	 * {@inheritDoc}
	 */
	public void update(long lastModified, boolean force) {
		if (lastModified > this.lastModified || force) {
			this.lastModified = lastModified;
			loadProperties();
		}
	}

	private boolean loadProperties() {
		if (itemUrl != null) {
			InputStream ios = null;

			try {
				final URLConnection connection = URLJarCollector.openConnection(itemUrl);

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
		return itemUrl;
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
			return new URL(properties.getProperty(TEAM_WEBPAGE, null));
		} catch (MalformedURLException e) {
			return null;
		}
	}

	public boolean getIncludeSource() {
		return false;
	}

	public boolean getIncludeData() {
		return false;
	}

	public boolean isSourceIncluded() {
		return false;
	}

	public String getRobocodeVersion() {
		return properties.getProperty(ROBOCODE_VERSION, null);
	}

	public String toString() {
		return itemUrl.toString();
	}

	public void storeProperties(OutputStream os, RobotProperties props) throws IOException {
		if (props.getVersion() != null) {
			properties.setProperty(TEAM_VERSION, props.getVersion());
		}
		if (props.getAuthor() != null) {
			properties.setProperty(TEAM_AUTHOR_NAME, props.getAuthor());
		}
		if (props.getDescription() != null) {
			properties.setProperty(TEAM_DESCRIPTION, props.getDescription());
		}
		if (props.getWebPage() != null) {
			properties.setProperty(TEAM_WEBPAGE, props.getWebPage().toExternalForm());
		}
		properties.setProperty(ROBOCODE_VERSION, Container.getComponent(IVersionManager.class).getVersion());

		properties.store(os, "Robocode Robot Team");
	}

	public static void createOrUpdateTeam(File target, TeamProperties props) throws IOException {
		FileOutputStream os = null;

		try {
			Properties team = loadTeamProperties(target);

			if (props.getVersion() != null) {
				team.setProperty(TEAM_VERSION, props.getVersion());
			}
			if (props.getMembers() != null) {
				team.setProperty(TEAM_MEMBERS, props.getMembers());
			}
			if (props.getAuthor() != null) {
				team.setProperty(TEAM_AUTHOR_NAME, props.getAuthor());
			}
			if (props.getDescription() != null) {
				team.setProperty(TEAM_DESCRIPTION, props.getDescription());
			}
			if (props.getWebPage() != null) {
				team.setProperty(TEAM_WEBPAGE, props.getWebPage().toExternalForm());
			}
			String version = Container.getComponent(IVersionManager.class).getVersion();
			team.setProperty(ROBOCODE_VERSION, version);

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
