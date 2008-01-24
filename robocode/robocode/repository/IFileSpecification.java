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


import robocode.manager.NameManager;

import java.io.File;
import java.io.Serializable;
import java.net.URL;


/**
 * @author Pavel Savara (original)
 */
public interface IFileSpecification extends Comparable<IFileSpecification>, Serializable, Cloneable {
	String getFileName();
	String getFilePath();
	String getFileType();
	long getFileLastModified();
	long getFileLength();
	boolean exists();

	String getFullClassName();
	String getVersion();
	String getFullPackage();
	String getDescription();
	String getRobocodeVersion();
	URL getWebpage();
	String getAuthorName();

	boolean isDuplicate();
	boolean isSameFile(String filePath, long fileLength, long fileLastModified);
	void setDuplicate(boolean value);
	boolean isDevelopmentVersion();

	// move ?
	NameManager getNameManager();
	String getName();
	String getFullClassNameWithVersion();
	File getRootDir();
	String getUid();
	boolean getValid();
	File getJarFile();
}
