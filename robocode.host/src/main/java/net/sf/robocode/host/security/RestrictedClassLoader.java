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
package net.sf.robocode.host.security;

import net.sf.robocode.core.Container;

/**
 * @author Pavel Savara (original)
 */
public class RestrictedClassLoader extends ClassLoader{
	private static final boolean isSecutityOn = !System.getProperty("NOSECURITY", "false").equals("true");

	public RestrictedClassLoader() {
		super(Container.systemLoader);
	}

	public synchronized Class<?> loadClass(String name, boolean resolve)
	throws ClassNotFoundException
	{
		if (isSecutityOn){
			if (name.startsWith("net.sf.robocode")){
				throw new ClassNotFoundException();
			}
			if (name.startsWith("robocode.control")){
				throw new ClassNotFoundException();
			}
		}
		return super.loadClass(name, resolve);
	}

	
}

