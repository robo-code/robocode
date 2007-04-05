/*******************************************************************************
 * Copyright (c) 2001, 2007 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Code cleanup
 *******************************************************************************/
package robocode.manager;


import java.io.Serializable;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
@SuppressWarnings("serial")
public class NameManager implements Serializable {

	private String fullClassName;
	private String version;
	private boolean developmentVersion;

	private String fullClassNameWithVersion;
	private String uniqueFullClassNameWithVersion;
	private String fullPackage;
	private String rootPackage;
	private String shortClassName;
	private String veryShortClassName;
	private String shortClassNameWithVersion;
	private String veryShortClassNameWithVersion;
	private String uniqueVeryShortClassNameWithVersion;
	private String uniqueShortClassNameWithVersion;

	public NameManager(String className, String version, boolean developmentVersion, boolean isTeam) {
		if (className == null) {
			throw new NullPointerException("className cannot be null.");
		}

		this.fullClassName = className;
		if (version != null) {
			if (version.length() > 10) {
				version = version.substring(0, 10);
			}
		}
		this.version = version;
		this.developmentVersion = developmentVersion;
	}

	public String getFullClassName() {
		return fullClassName;
	}

	public String getRootPackage() {
		if (rootPackage == null) {
			if (fullClassName.indexOf(".") > 0) {
				rootPackage = fullClassName.substring(0, fullClassName.indexOf("."));
			} else {
				rootPackage = null;
			}
		}
		return rootPackage;
	}

	public String getShortClassName() {
		if (shortClassName == null) {
			if (getFullClassName().lastIndexOf(".") > 0) {
				shortClassName = getFullClassName().substring(getFullClassName().lastIndexOf(".") + 1);
			} else {
				shortClassName = getFullClassName();
			}
		}
		return shortClassName;
	}

	public String getShortClassNameWithVersion() {
		if (shortClassNameWithVersion == null) {
			if (getVersion().length() == 0) {
				shortClassNameWithVersion = getShortClassName();
			} else {
				shortClassNameWithVersion = getShortClassName() + " " + getVersion();
			}
		}
		return shortClassNameWithVersion;
	}

	/**
	 * Example: sample.Walls 1.0*
	 * The * indicates a development version, or not from the cache.
	 */
	public String getUniqueFullClassNameWithVersion() {
		if (uniqueFullClassNameWithVersion == null) {
			if (getFullClassNameWithVersion().equals(getFullClassName())) {
				uniqueFullClassNameWithVersion = getFullClassName();
			} else {
				if (!developmentVersion) {
					uniqueFullClassNameWithVersion = getFullClassNameWithVersion();
				} else {
					uniqueFullClassNameWithVersion = getFullClassNameWithVersion() + "*";
				}
			}
		}
		return uniqueFullClassNameWithVersion;
	}

	public String getUniqueShortClassNameWithVersion() {
		if (uniqueShortClassNameWithVersion == null) {
			if (getShortClassName().equals(getShortClassNameWithVersion())) {
				uniqueShortClassNameWithVersion = getShortClassName();
			} else {
				if (!developmentVersion) {
					uniqueShortClassNameWithVersion = getShortClassNameWithVersion();
				} else {
					uniqueShortClassNameWithVersion = getShortClassNameWithVersion() + "*";
				}
			}
		}
		return uniqueShortClassNameWithVersion;
	}

	public String getUniqueVeryShortClassNameWithVersion() {
		if (uniqueVeryShortClassNameWithVersion == null) {
			if (getVeryShortClassName().equals(getVeryShortClassNameWithVersion())) {
				uniqueVeryShortClassNameWithVersion = getVeryShortClassName();
			} else {
				if (!developmentVersion) {
					uniqueVeryShortClassNameWithVersion = getVeryShortClassNameWithVersion();
				} else {
					uniqueVeryShortClassNameWithVersion = getVeryShortClassNameWithVersion() + "*";
				}
			}
		}
		return uniqueVeryShortClassNameWithVersion;
	}

	public boolean isDevelopmentVersion() {
		return developmentVersion;
	}

	public String getVersion() {
		if (version == null) {
			version = "";
		}
		return version;
	}

	public String getVeryShortClassName() {
		if (veryShortClassName == null) {
			veryShortClassName = getShortClassName();
			if (veryShortClassName.length() > 12) {
				veryShortClassName = veryShortClassName.substring(0, 12) + "...";
			}
		}
		return veryShortClassName;
	}

	public String getVeryShortClassNameWithVersion() {
		if (veryShortClassNameWithVersion == null) {
			if (getVersion().length() == 0) {
				veryShortClassNameWithVersion = getVeryShortClassName();
			} else {
				veryShortClassNameWithVersion = getVeryShortClassName() + " " + getVersion();
			}
		}
		return veryShortClassNameWithVersion;
	}

	public String getFullClassNameWithVersion() {
		if (fullClassNameWithVersion == null) {
			if (getVersion().length() == 0) {
				fullClassNameWithVersion = getFullClassName();
			} else {
				fullClassNameWithVersion = getFullClassName() + " " + getVersion();
			}
		}
		return fullClassNameWithVersion;
	}

	public String getFullPackage() {
		if (fullPackage == null) {
			if (fullClassName.lastIndexOf(".") > 0) {
				fullPackage = fullClassName.substring(0, fullClassName.lastIndexOf("."));
			} else {
				fullPackage = null;
			}
		}
		return fullPackage;
	}
}
