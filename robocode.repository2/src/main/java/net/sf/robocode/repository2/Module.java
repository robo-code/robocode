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
package net.sf.robocode.repository2;


import net.sf.robocode.core.Container;
import net.sf.robocode.repository.IRepositoryManager;
import net.sf.robocode.repository2.root.IRepositoryRoot;
import net.sf.robocode.repository2.root.ClassPathRoot;
import net.sf.robocode.repository2.root.JarRoot;


/**
 * @author Pavel Savara (original)
 */
public class Module {
	static {
		Container.cache.addComponent(IRepositoryManager.class, RepositoryManager.class);
	}
}
