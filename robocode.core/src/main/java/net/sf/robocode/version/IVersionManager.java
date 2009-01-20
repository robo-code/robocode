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
package net.sf.robocode.version;


import net.sf.robocode.manager.IVersionManagerBase;


/**
 * @author Pavel Savara (original)
 */
public interface IVersionManager extends IVersionManagerBase {
	int compare(String a, String b);
	boolean isFinal(String version);
	String checkForNewVersion();
}
