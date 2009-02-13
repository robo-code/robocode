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
package net.sf.robocode.js.repository.items.handlers;


import net.sf.robocode.repository.items.handlers.PropertiesHandler;
import net.sf.robocode.repository.items.RobotItem;
import net.sf.robocode.repository.root.IRepositoryRoot;
import net.sf.robocode.repository.Database;
import net.sf.robocode.js.repository.items.JsRobotItem;

import java.net.URL;


/**
 * @author Pavel Savara (original)
 */
public class JsPropertiesHandler extends PropertiesHandler {

	@Override
	protected RobotItem createItem(URL itemURL, IRepositoryRoot root, Database db) {
		return new JsRobotItem(null, itemURL, root);
	}
}
