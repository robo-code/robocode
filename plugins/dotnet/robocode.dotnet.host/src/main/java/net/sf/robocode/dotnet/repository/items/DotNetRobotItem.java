/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
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

		properties.setProperty(ROBOT_PLATFORM, "DotNet"); // instead of ".NET", as it is used for class names etc.
		isPropertiesLoaded = true;
	}
}
