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
package net.sf.robocode.dotnet.repository.items;


import net.sf.robocode.repository.items.RobotItem;
import net.sf.robocode.repository.root.IRepositoryRoot;

import java.net.URL;


/**
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (contributor)
 */
public class DotNetRobotItem extends RobotItem {
	private static final long serialVersionUID = 1L;

	public DotNetRobotItem(IRepositoryRoot root, URL itemURL) {
		super(itemURL, root);
		isPropertiesLoaded = true;
	}

	@Override
	public String getRobotLanguage() {
		final String lang = properties.getProperty(ROBOT_LANGUAGE, null);

		return lang == null ? "cs" : lang;
	}
}
