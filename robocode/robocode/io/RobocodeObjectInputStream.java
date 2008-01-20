/*******************************************************************************
 * Copyright (c) 2001, 2007 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Ported to Java 5.0
 *******************************************************************************/
package robocode.io;


import robocode.security.IRobocodeClassLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public class RobocodeObjectInputStream extends ObjectInputStream {

	private IRobocodeClassLoader classLoader;

	public RobocodeObjectInputStream(InputStream in, IRobocodeClassLoader classLoader) throws IOException {
		super(in);
		this.classLoader = classLoader;
	}

	@Override
	protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
		String name = desc.getName();

		try {
			return Class.forName(name, false, (ClassLoader)classLoader);
		} catch (ClassNotFoundException ex) {
			return super.resolveClass(desc);
		}
	}
}
