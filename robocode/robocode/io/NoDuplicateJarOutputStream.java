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
 *     - Moved this class from the robocode.util package into the robocode.io
 *       package
 *     Robert D. Maupin
 *     - Replaced old collection types like Vector and Hashtable with
 *       synchronized List and HashMap
 *******************************************************************************/
package robocode.io;


import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Robert D. Maupin (contributor)
 */
public class NoDuplicateJarOutputStream extends JarOutputStream {
	private Map<String, String> entries = new HashMap<String, String>();

	public NoDuplicateJarOutputStream(OutputStream out) throws IOException {
		super(out);
	}

	public NoDuplicateJarOutputStream(OutputStream out, Manifest man) throws IOException {
		super(out, man);
	}

	@Override
	public void putNextEntry(ZipEntry ze) throws IOException {
		if (entries.containsKey(ze.getName())) {
			throw new ZipException("duplicate entry: " + ze.getName());
		}
		entries.put(ze.getName(), "");
		super.putNextEntry(ze);
	}

	@Override
	public void closeEntry() throws IOException {
		super.closeEntry();
	}
}
