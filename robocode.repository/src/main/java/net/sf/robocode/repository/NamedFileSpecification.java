/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package net.sf.robocode.repository;


/**
 * @author Pavel Savara (original)
 */
abstract class NamedFileSpecification extends FileSpecification implements Comparable<INamedFileSpecification>, INamedFileSpecification {
	private static final long serialVersionUID = 1L;

	private String fullPackage;

	private String fullClassNameWithVersion;
	private String uniqueFullClassNameWithVersion;

	private String shortClassName;
	private String shortClassNameWithVersion;
	private String uniqueShortClassNameWithVersion;

	private String veryShortClassName;
	private String veryShortClassNameWithVersion;
	private String uniqueVeryShortClassNameWithVersion;

	public String getFullClassName() {
		return name;
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
			if (version == null) {
				shortClassNameWithVersion = getShortClassName();
			} else {
				shortClassNameWithVersion = getShortClassName() + " " + version;
			}
		}
		return shortClassNameWithVersion;
	}

	// Example: sample.Walls 1.0*
	// The * indicates a development version, or not from the cache.
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
			if (version == null) {
				veryShortClassNameWithVersion = getVeryShortClassName();
			} else {
				veryShortClassNameWithVersion = getVeryShortClassName() + " " + version;
			}
		}
		return veryShortClassNameWithVersion;
	}

	public String getFullClassNameWithVersion() {
		if (fullClassNameWithVersion == null) {
			if (version == null) {
				fullClassNameWithVersion = getFullClassName();
			} else {
				fullClassNameWithVersion = getFullClassName() + " " + version;
			}
		}
		return fullClassNameWithVersion;
	}

	public String getFullPackage() {
		if (fullPackage == null) {
			if (name.lastIndexOf(".") > 0) {
				fullPackage = name.substring(0, name.lastIndexOf("."));
			} else {
				fullPackage = null;
			}
		}
		return fullPackage;
	}

	public int compareTo(INamedFileSpecification other) {
		return compare(getFullPackage(), getFullClassName(), getVersion(), other.getFullPackage(),
				other.getFullClassName(), other.getVersion());
	}
}
