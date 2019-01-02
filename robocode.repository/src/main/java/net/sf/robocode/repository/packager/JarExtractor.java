/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.repository.packager;


import net.sf.robocode.io.FileUtil;
import net.sf.robocode.io.Logger;
import net.sf.robocode.io.URLJarCollector;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
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
		BufferedInputStream bis = null;
		JarInputStream jarIS = null;

		try {
			final URLConnection con = URLJarCollector.openConnection(url);

			is = con.getInputStream();
			bis = new BufferedInputStream(is);
			jarIS = new JarInputStream(bis);

			JarEntry entry = jarIS.getNextJarEntry();

			while (entry != null) {
				if (entry.isDirectory()) {
					File dir = new File(dest, entry.getName());

					if (!dir.exists() && !dir.mkdirs()) {
						Logger.logError("Cannot create dir: " + dir);
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
			FileUtil.cleanupStream(bis);
			FileUtil.cleanupStream(is);
		}
	}

	public static void extractFile(File dest, JarInputStream jarIS, JarEntry entry) throws IOException {
		File out = new File(dest, entry.getName());
		File parentDirectory = new File(out.getParent());

		if (!parentDirectory.exists() && !parentDirectory.mkdirs()) {
			Logger.logError("Cannot create dir: " + parentDirectory);
		}
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		byte buf[] = new byte[2048];

		try {
			fos = new FileOutputStream(out);
			bos = new BufferedOutputStream(fos);

			int num;

			while ((num = jarIS.read(buf, 0, 2048)) != -1) {
				bos.write(buf, 0, num);
			}
		} finally {
			FileUtil.cleanupStream(bos);
			FileUtil.cleanupStream(fos);
		}
	}
}
