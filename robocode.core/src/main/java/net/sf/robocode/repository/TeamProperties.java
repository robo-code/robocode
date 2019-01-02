/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.repository;


import java.net.URL;


/**
 * Container for Team properties used in Team packages.
 *
 * @author Flemming N. Larsen (original)
 */
public class TeamProperties {

	private String members;
	private String version;
	private String author;
	private String description;
	private URL webPage;

	public String getMembers() {
		return members;
	}

	public void setMembers(String members) {
		this.members = members;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public URL getWebPage() {
		return webPage;
	}

	public void setWebPage(URL webPage) {
		this.webPage = webPage;
	}
}
