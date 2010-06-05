/*******************************************************************************
 * Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package net.sf.robocode.repository;


import robocode.control.RobotSpecification;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;


/**
 * @author Pavel Savara (original)
 */
public interface IRepositoryItem extends Comparable<Object> {
	boolean isValid();

	boolean isTeam();

	boolean isInJAR();

	void setValid(boolean value);

	String getVersion();

	String getDescription();

	String getAuthorName();

	URL getWebpage();

	boolean isJavaSourceIncluded();

	String getRootPath();

	URL getItemURL();

	URL getPropertiesURL();

	long getLastModified();

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
	
	void storeProperties(OutputStream os) throws IOException;

	void storeProperties(OutputStream os, URL web, String desc, String author, String version) throws IOException;
}
