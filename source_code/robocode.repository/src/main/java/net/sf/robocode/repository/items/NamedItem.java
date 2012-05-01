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
package net.sf.robocode.repository.items;


import net.sf.robocode.repository.IRepositoryItem;
import net.sf.robocode.repository.root.IRepositoryRoot;
import net.sf.robocode.security.HiddenAccess;
import robocode.control.RobotSpecification;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Properties;
import java.util.StringTokenizer;


/**
 * @author Pavel Savara (original)
 */
public abstract class NamedItem extends BaseItem implements IRepositoryItem {
	private static final long serialVersionUID = 1L;

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
		if (htmlUrl != null) {// TODO ZAMO
		}
	}

	public boolean isDevelopmentVersion() {
		return (!getFullClassName().startsWith("sample")) && root.isDevel();
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
		if (getFullClassName() == null) {
			return null;
		}
		final int index = getFullClassName().lastIndexOf('.');

		if (index == -1) {
			return "";
		}
		return getFullClassName().substring(0, index);
	}

	// same as package but with slash
	public String getRelativePath() {
		final int index = getFullClassName().lastIndexOf('.');

		if (index == -1) {
			return "";
		}
		return getFullClassName().substring(0, index).replace('.', '/');
	}

	public String getShortClassName() {
		if (getFullClassName() == null) {
			return null;
		}
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
		if (getVersion() == null) {
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
		if (getVersion() == null) {
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
		if (getVersion() == null) {
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
		return HiddenAccess.createSpecification(this, getFullClassName(), getAuthorName(),
				(getWebpage() != null) ? getWebpage().toString() : null, getVersion(), getRobocodeVersion(),
				root.getRootUrl().toString(), getFullClassName(), getDescription());
	}

	public int compareTo(Object other) {
		if (other == this) {
			return 0;
		}
		if (other instanceof IRepositoryItem) {
			IRepositoryItem otherRI = (IRepositoryItem) other;

			return compare(getFullPackage(), getFullClassName(), getVersion(), otherRI.getFullPackage(),
					otherRI.getFullClassName(), otherRI.getVersion());
		}

		// for IgnoredItem
		return 0;
	}

	private static int compare(String p1, String c1, String v1, String p2, String c2, String v2) {
		if (p1 == null && p2 != null) {
			return 1;
		}
		if (p2 == null && p1 != null) {
			return -1;
		}

		if (p1 != null) // then p2 isn't either
		{
			// If packages are different, return
			int pc = p1.compareToIgnoreCase(p2);

			if (pc != 0) {
				return pc;
			}
		}

		if (c1 == null && c2 != null) {
			return 1;
		}
		if (c2 == null && c1 != null) {
			return -1;
		}

		if (c1 != null) {
			// Ok, same package... compare class:
			int cc = c1.compareToIgnoreCase(c2);

			if (cc != 0) {
				// Different classes, return
				return cc;
			}
		}

		// Ok, same robot... compare version
		if (v1 == null && v2 == null) {
			return 0;
		}
		if (v1 == null) {
			return 1;
		}
		if (v2 == null) {
			return -1;
		}

		if (v1.equals(v2)) {
			return 0;
		}

		if (v1.indexOf(".") < 0 || v2.indexOf(".") < 0) {
			return v1.compareToIgnoreCase(v2);
		}

		// Dot separated versions.
		StringTokenizer s1 = new StringTokenizer(v1, ".");
		StringTokenizer s2 = new StringTokenizer(v2, ".");

		while (s1.hasMoreTokens() && s2.hasMoreTokens()) {
			String tok1 = s1.nextToken();
			String tok2 = s2.nextToken();

			try {
				int i1 = Integer.parseInt(tok1);
				int i2 = Integer.parseInt(tok2);

				if (i1 != i2) {
					return i1 - i2;
				}
			} catch (NumberFormatException e) {
				int tc = tok1.compareToIgnoreCase(tok2);

				if (tc != 0) {
					return tc;
				}
			}
		}
		if (s1.hasMoreTokens()) {
			return 1;
		}
		if (s2.hasMoreTokens()) {
			return -1;
		}
		return 0;
	}
}
