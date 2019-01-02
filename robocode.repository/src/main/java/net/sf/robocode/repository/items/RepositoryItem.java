/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.repository.items;


import net.sf.robocode.repository.root.IRepositoryRoot;

import java.io.Serializable;
import java.net.URL;


/**
 * @author Pavel Savara (original)
 */
public abstract class RepositoryItem implements IRepositoryItem, Serializable {
	private static final long serialVersionUID = 1L;

	protected URL itemUrl;
	protected IRepositoryRoot root;
	protected long lastModified;
	protected boolean isValid;

	RepositoryItem(URL itemURL, IRepositoryRoot root) {
		this.itemUrl = itemURL;
		this.root = root;
		this.lastModified = 0;
	}

	public URL getItemURL() {
		return itemUrl;
	}

	public IRepositoryRoot getRoot() {
		return root;
	}

	public boolean isInJAR() {
		return root.isJAR();
	}

	public long getLastModified() {
		return lastModified;
	}

	public boolean isValid() {
		return isValid;
	}

	public void setValid(boolean valid) {
		isValid = valid;
	}

	@Override
	public int hashCode() {
		return 31 + ((itemUrl == null) ? 0 : itemUrl.hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || !(obj instanceof RepositoryItem)) {
			return false;
		}
		RepositoryItem other = (RepositoryItem) obj;
		if (itemUrl == null && other.itemUrl != null) {
			return false;
		}
		return itemUrl.equals(other.itemUrl);
	}
}
