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


import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;


/**
 * @author Pavel Savara (original)
 */
public interface IRobotFileSpecificationExt extends IRobotFileSpecification, Cloneable {
	void setRobotDescription(String robotDescription);
	void setRobotJavaSourceIncluded(boolean robotJavaSourceIncluded);
	void setRobotAuthorName(String robotAuthorName);
	void setRobotWebpage(URL robotWebpage);
	void setRobocodeVersion(String version);
	void setRobotVersion(String robotVersion);
	
	String getPropertiesFileName();
	File getRootDir();
	String getName();

	void store(OutputStream out, String desc) throws IOException;
	Object clone() throws CloneNotSupportedException;
}
