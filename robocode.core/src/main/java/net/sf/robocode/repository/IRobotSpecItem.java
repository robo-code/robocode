/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.repository;


import robocode.control.RobotSpecification;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;


/**
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (contributor)
 */
public interface IRobotSpecItem extends Comparable<Object> {
	boolean isValid();

	boolean isTeam();

	boolean isInJAR();

	void setValid(boolean value);

	String getVersion();

	String getDescription();

	String getAuthorName();

	URL getWebpage();

	boolean getIncludeSource();

	boolean getIncludeData();

	boolean isSourceIncluded();

	String getRootPath();

	URL getItemURL();

	boolean isDevelopmentVersion();

	String getRobocodeVersion();

	String getFullPackage();
	
	String getRelativePath();

	String getRootPackage();

	String getFullClassNameWithVersion();

	String getUniqueFullClassName();

	String getUniqueFullClassNameWithVersion();

	String getUniqueShortClassNameWithVersion();

	String getUniqueVeryShortClassNameWithVersion();

	String getFullClassName();

	String getShortClassName();

	RobotSpecification createRobotSpecification();

	void storeProperties(OutputStream os, RobotProperties robotProperties) throws IOException;
}
