/*
 * Copyright (c) 2001-2026 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.installer;

public final class OsUtil {
    public static final String osName = System.getProperty("os.name");
    public static final int JAVA_MAJOR_VERSION = javaMajorVersion();

    private OsUtil() {
    }

    public static boolean isWindowsOS() {
        return osName.startsWith("Windows ");
    }

    public static boolean isMacOSX() {
        return osName.startsWith("Mac OS X");
    }

    public static boolean isFreeBSD() {
        return osName.equals("FreeBSD");
    }

    public static String getWindowsCmd() {
        return "cmd.exe /C ";
    }

    public static int javaMajorVersion() {
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
