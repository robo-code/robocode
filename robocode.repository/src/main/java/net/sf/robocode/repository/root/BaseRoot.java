/*******************************************************************************
 * Copyright (c) 2001, 2009 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package net.sf.robocode.repository.root;


import net.sf.robocode.repository.Database;

import java.io.File;
import java.io.Serializable;
import java.net.URL;


/**
 * @author Pavel Savara (original)
 */
public abstract class BaseRoot implements Serializable, IRepositoryRoot {
	private static final long serialVersionUID = 1L;

	protected transient Database db;
	protected URL url;
	protected File rootPath;

	public BaseRoot(Database db, File rootPath) {
		this.db = db;
		this.rootPath = rootPath;
	}

	public URL getClassPathUrl() {
		return url;
	}

	public URL getRootUrl() {
		return url;
	}

	public File getRootPath() {
		return rootPath;
	}

	public void setDatabase(Database db) {
		this.db = db;
	}

	public String toString() {
		return url.toString();
	}

	public void extractJar() {
		throw new UnsupportedOperationException();
	}
}
