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


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
@SuppressWarnings("serial")
public class ClassSpecification extends FileSpecification {

	// Used in FileSpecification
	public ClassSpecification(IRobotFileSpecification robotFileSpecification) {
		this.developmentVersion = robotFileSpecification.isDevelopmentVersion();
		this.rootDir = robotFileSpecification.getRootDir();
		this.name = robotFileSpecification.getName();
		setFileName(robotFileSpecification.getFileName());
		setFilePath(robotFileSpecification.getFilePath());
		setFileType(robotFileSpecification.getFileType());
		setFileLastModified(robotFileSpecification.getFileLastModified());
		setFileLength(robotFileSpecification.getFileLength());
	}

	@Override
	public String getUid() {
		return getFilePath();
	}
}
