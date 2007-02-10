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
package robocode.repository;


import java.io.Serializable;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
@SuppressWarnings("serial")
public class ClassSpecification extends FileSpecification implements Serializable {

	// Used in FileSpecification
	public ClassSpecification(RobotSpecification robotSpecification) {
		this.developmentVersion = robotSpecification.isDevelopmentVersion();
		this.rootDir = robotSpecification.getRootDir();
		this.name = robotSpecification.getName();
		setFileName(robotSpecification.getFileName());
		setFilePath(robotSpecification.getFilePath());
		setFileType(robotSpecification.getFileType());
		setFileLastModified(robotSpecification.getFileLastModified());
		setFileLength(robotSpecification.getFileLength());
	}

	@Override
	public String getUid() {
		return getFilePath();
	}
}
