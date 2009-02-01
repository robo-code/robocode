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
package net.sf.robocode.repository2.items;


import net.sf.robocode.repository.IRepositoryItem;
import net.sf.robocode.repository2.root.IRepositoryRoot;
import net.sf.robocode.security.HiddenAccess;
import robocode.control.RobotSpecification;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Properties;


/**
 * @author Pavel Savara (original)
 */
public abstract class NamedItem extends BaseItem implements IRepositoryItem {
	public NamedItem(URL url, IRepositoryRoot root) {
		super(url, root);
	}

	protected Properties properties = new Properties();
	protected URL htmlUrl;

	public abstract URL getPropertiesUrl();

	public abstract String getFullClassName();

	public abstract String getVersion();

	public abstract URL getWebpage();

	public abstract String getAuthorName();

	public abstract String getRobocodeVersion();

	public abstract String getDescription();

	public abstract URL getHtmlUrl();

	public void storeHtml(OutputStream os) throws IOException {
		if (htmlUrl != null) {}
	}

	public boolean isDevelopmentVersion() {
		return root.isDevel();
	}

	public String getRootFile() {
		return root.getRootUrl().toString();
	}

	public String getRootPackage() {
		int lIndex = getFullClassName().indexOf(".");
		String rootPackage = null;

		if (lIndex > 0) {
			rootPackage = getFullClassName().substring(0, lIndex);
		}
		return rootPackage;
	}

	public String getFullPackage() {
		final int index = getFullClassName().lastIndexOf('.');

		if (index == -1) {
			return "";
		}
		return getFullClassName().substring(0, index);
	}

	public String getRelativePath() {
		final int index = getFullClassName().lastIndexOf('.');

		if (index == -1) {
			return "";
		}
		return getFullClassName().substring(0, index).replace('.', '/');
	}

	public String getShortClassName() {
		final int index = getFullClassName().lastIndexOf('.');

		if (index == -1) {
			return getFullClassName();
		}
		return getFullClassName().substring(index + 1);
	}

	public String getFullClassNameWithVersion() {
		if (getVersion() == null) {
			return getFullClassName();
		} else {
			return getFullClassName() + " " + getVersion();
		}
	}

	public String getUniqueFullClassNameWithVersion() {
		if (getFullClassNameWithVersion().equals(getFullClassName())) {
			return getFullClassName();
		} else {
			if (!isDevelopmentVersion()) {
				return getFullClassNameWithVersion();
			} else {
				return getFullClassNameWithVersion() + "*";
			}
		}
	}

	public String getUniqueShortClassNameWithVersion() {
		if (getShortClassName().equals(getShortClassNameWithVersion())) {
			return getShortClassName();
		} else {
			if (!isDevelopmentVersion()) {
				return getShortClassNameWithVersion();
			} else {
				return getShortClassNameWithVersion() + "*";
			}
		}
	}

	public String getUniqueVeryShortClassNameWithVersion() {
		if (getVeryShortClassName().equals(getVeryShortClassNameWithVersion())) {
			return getVeryShortClassName();
		} else {
			if (!isDevelopmentVersion()) {
				return getVeryShortClassNameWithVersion();
			} else {
				return getVeryShortClassNameWithVersion() + "*";
			}
		}
	}

	public String getShortClassNameWithVersion() {
		if (getVersion() == null) {
			return getShortClassName();
		} else {
			return getShortClassName() + " " + getVersion();
		}
	}

	public String getVeryShortClassNameWithVersion() {
		if (getVersion() == null) {
			return getVeryShortClassName();
		} else {
			return  getVeryShortClassName() + " " + getVersion();
		}
	}

	public String getVeryShortClassName() {
		String veryShortClassName = getShortClassName();

		if (veryShortClassName.length() > 12) {
			veryShortClassName = veryShortClassName.substring(0, 12) + "...";
		}
		return veryShortClassName;
	}	

	public RobotSpecification createRobotSpecification() {
		return HiddenAccess.createSpecification(this, getFullClassNameWithVersion(), getAuthorName(),
				(getWebpage() != null) ? getWebpage().toString() : null, getVersion(), getRobocodeVersion(),
				root.getRootUrl().toString(), getFullClassName(), getDescription());
	}
}
