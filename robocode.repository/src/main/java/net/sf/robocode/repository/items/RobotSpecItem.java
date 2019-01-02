/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.repository.items;


import net.sf.robocode.repository.IRobotSpecItem;
import net.sf.robocode.repository.root.IRepositoryRoot;
import net.sf.robocode.security.HiddenAccess;
import net.sf.robocode.util.AlphanumericComparator;
import robocode.control.RobotSpecification;

import java.net.URL;
import java.util.Properties;
import java.util.StringTokenizer;


/**
 * @author Pavel Savara (original)
 */
public abstract class RobotSpecItem extends RepositoryItem implements IRobotSpecItem {
	private static final long serialVersionUID = 1L;

	protected Properties properties = new Properties();
	protected URL htmlURL;

	RobotSpecItem(URL itemURL, IRepositoryRoot root) {
		super(itemURL, root);
	}

	public abstract URL getPropertiesURL();

	public abstract String getFullClassName();

	public abstract String getVersion();

	public abstract URL getWebpage();

	public abstract String getAuthorName();

	public abstract String getRobocodeVersion();

	public abstract String getDescription();

	public abstract URL getHtmlURL();

	public boolean isDevelopmentVersion() {
		return root.isDevelopmentRoot()
				&& !(getFullClassName().startsWith("sample") || getFullClassName().startsWith("tested.")); 
	}

	public String getRootPath() {
		return root.getURL().toString();
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

		return (index >= 0) ? getFullClassName().substring(0, index) : null;
	}

	// same as package but with slash
	public String getRelativePath() {
		final int index = getFullClassName().lastIndexOf('.');

		return (index < 0) ? "" : getFullClassName().substring(0, index).replaceAll("\\.", "/");
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
		String fullClassName = getFullClassName();

		if (getVersion() != null) {
			fullClassName += " " + getVersion();
		}
		return fullClassName;
	}

	public String getUniqueFullClassName() {
		String uniqueFullClassName = getFullClassName();

		if (isDevelopmentVersion()) {
			uniqueFullClassName += '*';
		}
		return uniqueFullClassName;
	}

	public String getUniqueFullClassNameWithVersion() {
		String uniqueFullClassName = (getVersion() == null) ? getFullClassName() : getFullClassNameWithVersion();

		if (isDevelopmentVersion()) {
			uniqueFullClassName += '*';
		}
		return uniqueFullClassName;
	}

	public String getUniqueShortClassNameWithVersion() {
		String uniqueShortClassName = (getVersion() == null) ? getShortClassName() : getShortClassNameWithVersion();

		if (isDevelopmentVersion()) {
			uniqueShortClassName += '*';
		}
		return uniqueShortClassName;
	}

	public String getUniqueVeryShortClassNameWithVersion() {
		String veryShortClassName = (getVersion() == null)
				? getVeryShortClassName()
				: getVeryShortClassNameWithVersion();

		if (isDevelopmentVersion()) {
			veryShortClassName += '*';
		}
		return veryShortClassName;
	}

	public String getShortClassNameWithVersion() {
		String shortClassName = getShortClassName();

		if (getVersion() != null) {
			shortClassName += " " + getVersion();
		}
		return shortClassName;
	}

	public String getVeryShortClassNameWithVersion() {
		String veryShortClassName = getVeryShortClassName();

		if (getVersion() != null) {
			veryShortClassName += " " + getVersion();
		}
		return veryShortClassName;
	}

	public String getVeryShortClassName() {
		String veryShortClassName = getShortClassName();

		if (veryShortClassName.length() > 12) {
			veryShortClassName = veryShortClassName.substring(0, 12) + "...";
		}
		return veryShortClassName;
	}	

	public RobotSpecification createRobotSpecification() {
		return HiddenAccess.createSpecification(this, getUniqueFullClassName(), getAuthorName(),
				(getWebpage() != null) ? getWebpage().toString() : null, getVersion(), getRobocodeVersion(),
				root.getURL().toString(), getFullClassName(), getDescription());
	}

	public int compareTo(Object other) {
		if (other == this) {
			return 0;
		}
		if (other instanceof IRobotSpecItem) {
			IRobotSpecItem otherRI = (IRobotSpecItem) other;

			return compare(getFullPackage(), getFullClassName(), getVersion(), otherRI.getFullPackage(),
					otherRI.getFullClassName(), otherRI.getVersion());
		}

		// for IgnoredItem
		return 0;
	}

	private static int compare(String p1, String c1, String v1, String p2, String c2, String v2) {
		AlphanumericComparator alphaNumComparator = new AlphanumericComparator();

		// Compare packages
		int result = alphaNumComparator.compare(p1, p2);

		if (result != 0) {
			return result;
		}

		// Same package, so compare classes
		result = alphaNumComparator.compare(c1, c2);
		if (result != 0) {
			return result;
		}

		// Same robot, so compare versions
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
			return alphaNumComparator.compare(v1, v2);
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
				int tc = alphaNumComparator.compare(tok1, tok2);

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
