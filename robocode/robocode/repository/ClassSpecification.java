/*******************************************************************************
 * Copyright (c) 2001 Mathew Nelson
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew Nelson - initial API and implementation
 *******************************************************************************/
package robocode.repository;


import java.io.*;
import robocode.util.*;


public class ClassSpecification extends FileSpecification implements Serializable {

	private static int rebuild = 0;
	
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

	public String getUid() {
		return getFilePath();
	}
	
	private void log(String s) {
		Utils.log(s);
	}
	
	private void log(String s, Throwable t) {
		Utils.log(s, t);
	}
	
	private void log(Throwable e) {
		Utils.log(e);
	}
	
}

