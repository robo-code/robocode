/*******************************************************************************
 * Copyright (c) 2001, 2006 Mathew Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew Nelson - initial API and implementation
 *******************************************************************************/
package robocode.manager;


import java.io.*;
import robocode.util.Utils;


public class NameManager implements Serializable {
	
	private java.lang.String fullClassName = null;
	private java.lang.String version = null;
	private boolean developmentVersion = false;
	
	private java.lang.String fullClassNameWithVersion = null;
	private java.lang.String uniqueFullClassNameWithVersion = null;
	private java.lang.String fullPackage = null;
	private java.lang.String rootPackage = null;
	private java.lang.String shortClassName = null;
	private java.lang.String veryShortClassName = null;
	private java.lang.String shortClassNameWithVersion = null;
	private java.lang.String veryShortClassNameWithVersion = null;
	private java.lang.String uniqueVeryShortClassNameWithVersion = null;
	private java.lang.String uniqueShortClassNameWithVersion = null;

	private boolean isTeam = false;
	
	private NameManager() {}
	
	public NameManager(String className, String version, boolean developmentVersion, boolean isTeam) {
		if (className == null) {
			throw new NullPointerException("className cannot be null.");
		}
			
		this.fullClassName = className;
		this.isTeam = isTeam;
		if (version != null) {
			if (version.length() > 10) {
				version = version.substring(0, 10);
			}
		}
		this.version = version;
		this.developmentVersion = developmentVersion;
	}
	
	public String getFullClassName() {
		// if (isTeam)
		// return "Team: " + fullClassName;
		// else
		return fullClassName;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/27/2001 1:28:54 PM)
	 * @return java.lang.String
	 */
	public java.lang.String getRootPackage() {
		if (rootPackage == null) {
			if (fullClassName.indexOf(".") > 0) {
				rootPackage = fullClassName.substring(0, fullClassName.indexOf("."));
			} else {
				rootPackage = null;
			}
		}
		return rootPackage;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/27/2001 1:28:54 PM)
	 * @return java.lang.String
	 */
	public java.lang.String getShortClassName() {
		if (shortClassName == null) {
			if (getFullClassName().lastIndexOf(".") > 0) {
				shortClassName = getFullClassName().substring(getFullClassName().lastIndexOf(".") + 1);
				// if (isTeam)
				// shortClassName = "Team: " + shortClassName;
			} else {
				shortClassName = getFullClassName();
			}
		}
		return shortClassName;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/27/2001 1:28:54 PM)
	 * @return java.lang.String
	 */
	public java.lang.String getShortClassNameWithVersion() {
		if (shortClassNameWithVersion == null) {
			if (getVersion().equals("")) {
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
	public java.lang.String getUniqueFullClassNameWithVersion() {
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

	/**
	 * Insert the method's description here.
	 * Creation date: (1/27/2001 1:28:54 PM)
	 * @return java.lang.String
	 */
	public java.lang.String getUniqueShortClassNameWithVersion() {
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

	/**
	 * Insert the method's description here.
	 * Creation date: (1/27/2001 1:28:54 PM)
	 * @return java.lang.String
	 */
	public java.lang.String getUniqueVeryShortClassNameWithVersion() {
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

	/**
	 * Insert the method's description here.
	 * Creation date: (1/27/2001 1:28:54 PM)
	 * @return java.lang.String
	 */
	public java.lang.String getVersion() {
		if (version == null) {
			version = "";
		}
		return version;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/27/2001 1:28:54 PM)
	 * @return java.lang.String
	 */
	public java.lang.String getVeryShortClassName() {
		if (veryShortClassName == null) {
			veryShortClassName = getShortClassName();
			if (veryShortClassName.length() > 12) {
				veryShortClassName = veryShortClassName.substring(0, 12) + "...";
			}
		}
		return veryShortClassName;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/27/2001 1:28:54 PM)
	 * @return java.lang.String
	 */
	public java.lang.String getVeryShortClassNameWithVersion() {
		if (veryShortClassNameWithVersion == null) {
			if (getVersion().equals("")) {
				veryShortClassNameWithVersion = getVeryShortClassName();
			} else {
				veryShortClassNameWithVersion = getVeryShortClassName() + " " + getVersion();
			}
		}
		return veryShortClassNameWithVersion;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/27/2001 1:28:54 PM)
	 * @return java.lang.String
	 */
	public java.lang.String getFullClassNameWithVersion() {
		if (fullClassNameWithVersion == null) {
			if (getVersion().equals("")) {
				fullClassNameWithVersion = getFullClassName();
			} else {
				fullClassNameWithVersion = getFullClassName() + " " + getVersion();
			}
		}
		return fullClassNameWithVersion;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/27/2001 1:28:54 PM)
	 * @return java.lang.String
	 */
	public java.lang.String getFullPackage() {
		if (fullPackage == null) {
			if (fullClassName.lastIndexOf(".") > 0) {
				fullPackage = fullClassName.substring(0, fullClassName.lastIndexOf("."));
			} else {
				fullPackage = null;
			}
		}
		return fullPackage;
	}
	
	private static void log(String s) {
		Utils.log(s);
	}

}

