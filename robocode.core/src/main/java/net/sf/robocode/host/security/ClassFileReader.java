package net.sf.robocode.host.security;

import net.sf.robocode.io.FileUtil;
import net.sf.robocode.io.Logger;
import net.sf.robocode.io.URLJarCollector;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;

public final class ClassFileReader {
	public static ByteBuffer readClassFileFromURL(URL url) {
		if (url == null) return null;

		InputStream is = null;
		BufferedInputStream bis = null;
		ByteBuffer result;
		try {
			URLConnection connection = URLJarCollector.openConnection(url);

			is = connection.getInputStream();
			bis = new BufferedInputStream(is);

			result = ByteBuffer.allocate(1024 * 8);
			boolean done = false;

			do {
				do {
					int res = bis.read(result.array(), result.position(), result.remaining());

					if (res == -1) {
						done = true;
						break;
					}
					result.position(result.position() + res);
				} while (result.remaining() != 0);
				result.flip();
				if (!done) {
					result = ByteBuffer.allocate(result.capacity() * 2).put(result);
				}
			} while (!done);

		} catch (FileNotFoundException ignore) {
			return null;
		} catch (IOException e) {
			Logger.logError(e);
			return null;
		} finally {
			FileUtil.cleanupStream(bis);
			FileUtil.cleanupStream(is);
		}
		return result;
	}

	public static ByteBuffer readClassFile(final URL url) {
		return AccessController.doPrivileged(new PrivilegedAction<ByteBuffer>() {
			public ByteBuffer run() {
				return readClassFileFromURL(url);
			}
		});
	}

	public static ClassAnalyzer.RobotMainClassPredicate createMainClassPredicate(final URL rootURL) {
		return new ClassAnalyzer.RobotMainClassPredicate(new ClassAnalyzer.ByteBufferFunction() {
			@Override
			public ByteBuffer get(String binaryName) {
				String fileName = binaryName + ".class";
				URL url;
				try {
					url = new URL(rootURL.getProtocol(), rootURL.getHost(), rootURL.getPort(), rootURL.getPath() + fileName);
				} catch (MalformedURLException e) {
					Logger.logError(e);
					return null;
				}

				return readClassFile(url);
			}
		});
	}
}
