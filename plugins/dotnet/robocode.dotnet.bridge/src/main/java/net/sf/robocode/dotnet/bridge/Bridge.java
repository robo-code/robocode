package net.sf.robocode.dotnet.bridge;

import java.io.File;
import java.io.IOException;
import java.security.CodeSource;

public class Bridge {
	private native void initDotNet();

	private static boolean loaded;
	private static boolean initialized;

	private static void load() {
		if (!loaded) {
			try {
				final CodeSource source = Bridge.class.getProtectionDomain().getCodeSource();
				final String file = source.getLocation().getFile();

				File path;
				if (file.endsWith("classes/")) {
					final String base = file.substring(0, file.length() - 8).replaceAll("robocode.dotnet.bridge", "robocode.dotnet.bridge.net") + "robocode.dotnet.bridge.net";
					path = new File(base + ".dll");
					if (!path.exists()) {
						path = new File(base + "-" + VersionReader.getNVersion() + ".dll");
					}
					if (!path.exists()) {
						throw new Error("Can't find robocode.dotnet.bridge.net.*.dll");
					}
				} else if (file.contains(".jar")) {
					final String base = file.substring(0, file.length() - 4).replaceAll("robocode.dotnet.bridge", "robocode.dotnet.bridge.net").replaceAll(VersionReader.getVersion(), VersionReader.getNVersion());
					path = new File(base + ".dll");
				} else {
					throw new Error("Can't find robocode.dotnet.bridge.net.*.dll");
				}
				System.load(path.getCanonicalPath());

			} catch (IOException e) {
				throw new Error("Can't find robocode.dotnet.bridge.net.*.dll");
			}
			loaded = true;
		}
	}

	public Bridge() {
		load();
	}

	public void init() {
		if (!initialized) {
			initDotNet();
			initialized = true;
		}
	}


	public void talkBack() {
		System.out.println("We are called back!");
	}

	public void talkBackInt(int a) {
		System.out.println("We are called back!" + a);
	}

}