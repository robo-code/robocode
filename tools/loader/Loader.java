/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;


/**
 * @author Pavel Savara (original)
 */
public class Loader {
	public static void main(String[] args) throws IOException {
		for (int i = 2; i < args.length; i++) {
			System.out.print("Downloading " + args[0] + args[i] + " to " + args[1] + "  [...");
			downloadFile(args[0], args[1], args[i]);
			System.out.println("...] done");
		}
	}

	private static void downloadFile(String libraries, String directory, String file) throws IOException {
		URL url = new URL(libraries + file);
		final URLConnection con = url.openConnection();
		InputStream is = null;
		FileOutputStream fos = null;

		try {
			is = con.getInputStream();
			fos = new FileOutputStream(directory + file);
			do {
				final int b = is.read();

				if (b == -1) {
					break;
				} else {
					fos.write(b);
				}
			} while (true);
		} finally {
			if (fos != null) {
				fos.close();
			}
			if (is != null) {
				is.close();
			}
		}
	}
}
