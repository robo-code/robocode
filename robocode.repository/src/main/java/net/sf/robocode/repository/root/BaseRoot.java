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
package net.sf.robocode.repository.root;


import net.sf.robocode.io.Logger;
import net.sf.robocode.repository.Database;

import java.io.File;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * @author Pavel Savara (original)
 */
public abstract class BaseRoot implements Serializable, IRepositoryRoot {
	private static final long serialVersionUID = 1L;

	protected transient Database db;
	protected final File rootPath;
	protected final URL rootURL;

	public BaseRoot(Database db, File rootPath) {
		this.db = db;
		this.rootPath = rootPath;

		URL url;

		try {
			url = rootPath.toURI().toURL();
		} catch (MalformedURLException e) {
			url = null;
			Logger.logError(e);
		}
		this.rootURL = url;
	}

	public URL getURL() {
		return rootURL;
	}

	public File getPath() {
		return rootPath;
	}

	public void setDatabase(Database db) {
		this.db = db;
	}

	public String toString() {
		return rootURL != null ? rootURL.toString() : null;
	}

	public void extractJAR() {
		throw new UnsupportedOperationException();
	}

	public boolean equals(Object obj) {
		if (obj instanceof IRepositoryRoot) {
			return ((IRepositoryRoot) obj).getURL().equals(rootURL);
		}
		return false;
	}
}
