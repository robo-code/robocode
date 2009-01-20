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


import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;


/**
 * @author Pavel Savara (original)
 */
public interface ITeamFileSpecificationExt extends INamedFileSpecification, Cloneable {
	String getMembers();
	String getPropertiesFileName();

	void setTeamDescription(String teamDescription);
	void setTeamAuthorName(String teamAuthorName);
	void setTeamWebpage(URL teamWebpage);
	void setTeamVersion(String teamVersion);
	void setRobocodeVersion(String version);
	void setMembers(String members);

	void store(OutputStream out, String desc) throws IOException;
	Object clone() throws CloneNotSupportedException;
}
