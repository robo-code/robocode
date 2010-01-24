/*******************************************************************************
 * Copyright (c) 2001, 2009 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
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
 */
public class DotNetRobotItem extends RobotItem {
	private static final long serialVersionUID = 1L;

	public DotNetRobotItem(IRepositoryRoot root, URL classUrl) {
		super(root);
        url=classUrl;
        init();
	}

	@Override
	protected void init() {
		if (properties.getProperty(ROBOT_CLASSNAME) == null && url != null) {
			final String file = url.getFile();
			final String clazz = file.substring(file.lastIndexOf("!/") + 2);
            if (clazz.length()>0){
                properties.setProperty(ROBOT_CLASSNAME, clazz);
            }
		}
		isPropertiesLoaded = true; // TODO something real ?
	}

	@Override
	public String getRobotLanguage() {
		final String lang = properties.getProperty(ROBOT_LANGUAGE, null);

		return lang == null ? "cs" : lang;
	}
}
