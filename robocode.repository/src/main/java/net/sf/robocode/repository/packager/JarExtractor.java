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
package net.sf.robocode.repository.packager;


import net.sf.robocode.io.FileUtil;
import net.sf.robocode.io.Logger;
import net.sf.robocode.io.URLJarCollector;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileOutputStream;
import java.util.jar.JarInputStream;
import java.util.jar.JarEntry;
import java.net.URLConnection;
import java.net.URL;


/**
 * @author Pavel Savara (original)
 */
public class JarExtractor {
	public static void extractJar(URL url) {
		File dest = FileUtil.getRobotsDir();
		InputStream is = null;
		JarInputStream jarIS = null;

		try {
			final URLConnection con = URLJarCollector.openConnection(url);

			is = con.getInputStream();
			jarIS = new JarInputStream(is);

			JarEntry entry = jarIS.getNextJarEntry();

			while (entry != null) {
				if (entry.isDirectory()) {
					File dir = new File(dest, entry.getName());

					if (!dir.exists() && !dir.mkdirs()) {
						Logger.logError("Can't create dir " + dir);
					}
				} else {
					extractFile(dest, jarIS, entry);
				}
				entry = jarIS.getNextJarEntry();
			}
		} catch (IOException e) {
			Logger.logError(e);
		} finally {
			FileUtil.cleanupStream(jarIS);
			FileUtil.cleanupStream(is);
		}
	}

	private static void extractFile(File dest, JarInputStream jarIS, JarEntry entry) throws IOException {
		File out = new File(dest, entry.getName());
		File parentDirectory = new File(out.getParent());

		if (!parentDirectory.exists() && !parentDirectory.mkdirs()) {
			Logger.logError("Can't create dir " + parentDirectory);
		}
		FileOutputStream fos = null;
		byte buf[] = new byte[2048];

		try {
			fos = new FileOutputStream(out);

			int num;

			while ((num = jarIS.read(buf, 0, 2048)) != -1) {
				fos.write(buf, 0, num);
			}
		} finally {
			FileUtil.cleanupStream(fos);
		}
	}

}
