/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.roborumble.util;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


/**
 * @author Flemming N. Larsen (original)
 */
public final class PropertiesUtil {

	/**
	 * Returns a new Properties instance that is initialized to the specified properties file.
	 *
	 * @param filename the filename of the properties file to load
	 * @return a new java.util.Properties instance
	 */
	public static Properties getProperties(String filename) {
		Properties props = new Properties();

		if (filename != null && filename.trim().length() > 0) {
			FileInputStream fis = null;

			try {
				fis = new FileInputStream(filename);
				props.load(fis);
			} catch (IOException e) {
				System.err.println("Could not load properties file: " + filename);
			} finally {
				if (fis != null) {
					try {
						fis.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return props;
	}

	/**
	 * Returns a new Properties instance that is initialized to the specified properties input stream.
	 *
	 * @param is the input stream of the properties to load
	 * @return a new java.util.Properties instance
	 */
	public static Properties getProperties(InputStream is) {
		Properties props = new Properties();

		try {
			props.load(is);
		} catch (Exception e) {
			System.err.println("Could not load properties input stream: " + is);
		}
		return props;
	}

	/**
	 * Stores a Properties instance to the specified properties file.
	 *
	 * @param properties the properties to store
	 * @param filename   the filename of the file to store the properties into
	 * @param comments   comments to include in the properties file
	 * @return true if the properties were stored; false otherwise
	 */
	public static boolean storeProperties(Properties properties, String filename, String comments) {
		if (properties == null || filename == null || filename.trim().length() == 0) {
			return false;
		}

		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(filename);
			properties.store(fos, comments);
		} catch (IOException e) {
			System.err.println("Could not store properties to file: " + filename);
			return false;
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return true;
	}
}
