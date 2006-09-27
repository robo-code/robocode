/*******************************************************************************
 * Copyright (c) 2001-2006 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Ported to Java 5.0
 *******************************************************************************/
package robocode.util;


import java.io.*;
import java.util.jar.*;
import java.util.zip.*;
import java.util.*;


/**
 * @author Mathew A. Nelson (original)
 */
public class NoDuplicateJarOutputStream extends JarOutputStream {

	private Hashtable<String, String> entries = new Hashtable<String, String>();
	
	public NoDuplicateJarOutputStream(OutputStream out) throws IOException {
		super(out);
	}

	public NoDuplicateJarOutputStream(OutputStream out, Manifest man) throws IOException {
		super(out, man);
	}

	public void putNextEntry(ZipEntry ze) throws IOException {
		if (entries.containsKey(ze.getName())) {
			throw new ZipException("duplicate entry: " + ze.getName());
		}
		entries.put(ze.getName(), "");
		super.putNextEntry(ze);
	}

	public void closeEntry() throws IOException {
		super.closeEntry();
	}
}
