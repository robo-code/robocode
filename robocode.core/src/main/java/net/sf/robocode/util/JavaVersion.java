/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.util;

public final class JavaVersion {

	public static int getJavaMajorVersion() {
		String version = System.getProperty("java.version");

		String major;

		if (version.startsWith("1.")) {
			major = version.substring(2, version.lastIndexOf('.'));
		} else {		
			int index = version.indexOf('.');
			if (index > 0) {
				major = version.substring(0, index);
			} else {
				index = version.indexOf('-');
				if (index > 0) {
					major = version.substring(0, index);
				} else {
					major = version;
				}
			}
		}
		return Integer.parseInt(major);
	}
}
