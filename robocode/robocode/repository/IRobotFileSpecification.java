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
package robocode.repository;


import java.net.URL;


/**
 * @author Pavel Savara (original)
 */
public interface IRobotFileSpecification extends IFileSpecification {
	void setUid(String uid);
	String getRobotClassPath();
	boolean getRobotJavaSourceIncluded();
	boolean getNeedsExternalLoader();
	void setRobotDescription(String d);
	void setRobotJavaSourceIncluded(boolean b);
	void setRobotAuthorName(String an);
	void setRobotWebpage(URL u);
	void store(java.io.OutputStream outputStream, java.lang.String s) throws java.io.IOException;
	void setRobocodeVersion(String v);
	void setRobotVersion(String v);
	Object clone(); 
	String getThisFileName();

	boolean isAdvancedRobot();
	boolean isInteractiveRobot();
	boolean isJuniorRobot();
	boolean isTeamRobot();
	boolean isDroid();
	void setAdvancedRobot(boolean value);
	void setInteractiveRobot(boolean value);
	void setJuniorRobot(boolean value);
	void setTeamRobot(boolean value);
	void setDroid(boolean value);
}
