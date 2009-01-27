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
package net.sf.robocode.repository2.root;


import net.sf.robocode.repository2.items.IItem;
import net.sf.robocode.repository2.Database;
import net.sf.robocode.io.Logger;

import java.net.URL;
import java.net.MalformedURLException;
import java.io.File;


/**
 * Represents one .jar file
 * @author Pavel Savara (original)
 */
public class 	JarRoot implements IRepositoryRoot {
	final Database db;
	URL url;
	File rootPath;
	long lastModified;

	public JarRoot(Database db, File rootPath) {
		this.db = db;
		this.rootPath = rootPath;
		try {
			url = rootPath.toURL();
		} catch (MalformedURLException e) {
			Logger.logError(e);
		}
	}

	public void update() {
		// TODO ZAMO
		lastModified = rootPath.lastModified();
	}

	public void update(IItem item, boolean force){

	}
	
	public boolean isChanged(IItem item) {
		return rootPath.lastModified() > lastModified;
	}

	public URL getUrl() {
		return url;
	}

	public boolean isDevel() {
		return false;
	}

	public String toString() {
		return url.toString();
	}
}
