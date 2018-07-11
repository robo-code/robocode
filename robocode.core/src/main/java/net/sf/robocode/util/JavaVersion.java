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
