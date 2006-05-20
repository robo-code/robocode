/*******************************************************************************
 * Copyright (c) 2001 Mathew Nelson
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew Nelson - initial API and implementation
 *******************************************************************************/
package robocode.io;


import java.io.*;


public class RobocodeObjectInputStream extends java.io.ObjectInputStream {
	
	private ClassLoader classLoader;
	public RobocodeObjectInputStream(InputStream in, ClassLoader classLoader) throws IOException {
		super(in);
		this.classLoader = classLoader;
	}

	protected Class resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
		String name = desc.getName();

		try {
			return Class.forName(name, false, classLoader);
		} catch (ClassNotFoundException ex) {
			return super.resolveClass(desc);
		}
	}
}

