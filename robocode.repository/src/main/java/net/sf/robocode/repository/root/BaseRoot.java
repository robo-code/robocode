/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.repository.root;


import net.sf.robocode.io.Logger;
import net.sf.robocode.repository.IRepository;
import net.sf.robocode.ui.IWindowManager;

import java.io.File;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (contributor)
 */
public abstract class BaseRoot implements Serializable, IRepositoryRoot {
	private static final long serialVersionUID = 1L;

	protected transient IRepository repository;
	protected final File rootPath;
	protected final URL rootURL;

	public BaseRoot(IRepository repository, File rootPath) {
		this.repository = repository;
		this.rootPath = rootPath;

		URL url = null;
		try {
			url = rootPath.toURI().toURL();
		} catch (MalformedURLException e) {
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

	public void setRepository(IRepository repository) {
		this.repository = repository;
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

	protected static void setStatus(String message) {
		IWindowManager windowManager = net.sf.robocode.core.Container.getComponent(IWindowManager.class);
		if (windowManager != null) {
			windowManager.setStatus(message);
		}
	}
}
