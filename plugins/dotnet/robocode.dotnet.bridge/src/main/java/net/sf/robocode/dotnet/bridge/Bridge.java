package net.sf.robocode.dotnet.bridge;

import java.io.File;
import java.io.IOException;
import java.io.FilenameFilter;
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

				String path = ".";
				if (file.endsWith("classes/")){
					path=file.substring(0,file.length()-8).replaceFirst("dotnet.bridge", "dotnet.bridge.net");
				}
				System.err.println(file);
				if (file.contains(".jar")){
					path=file.substring(0,file.length()-4).replaceFirst("dotnet.bridge", "dotnet.bridge.net")+".dll";
				}

				File p=new File(path);
				final File[] files = p.listFiles(new FilenameFilter() {
					public boolean accept(File dir, String name) {
						return name.contains("dotnet.bridge.net") && name.endsWith(".dll");
					}
				});
				System.load(files[0].getCanonicalPath());

			} catch (IOException e) {
				System.err.println(e);
			}
			loaded=true;
		}
	}

	public Bridge() {
		load();
	}

	public void init(){
		if (!initialized){
			initDotNet();
			initialized=true;
		}
	}


	public void talkBack() {
		System.out.println("We are called back!");
	}

	public void talkBackInt(int a) {
		System.out.println("We are called back!" + a);
	}

}