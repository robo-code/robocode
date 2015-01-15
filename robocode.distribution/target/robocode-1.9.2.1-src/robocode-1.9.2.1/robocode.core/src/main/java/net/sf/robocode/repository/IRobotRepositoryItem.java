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


import java.net.URL;


/**
 * @author Pavel Savara (original)
 */
public interface IRobotRepositoryItem extends IRepositoryItem {
	URL getClassPathURL();

	String getWritableDirectory();

	String getReadableDirectory();

	String getRobotLanguage();

	boolean isDroid();

	boolean isTeamRobot();

	boolean isAdvancedRobot();

	boolean isStandardRobot();

	boolean isInteractiveRobot();

	boolean isPaintRobot();

	boolean isJuniorRobot();
}
