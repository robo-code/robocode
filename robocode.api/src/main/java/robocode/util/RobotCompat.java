/*
 * Copyright (c) 2001-2026 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package robocode.util;


import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Scanner;


/**
 * Used internally by Robocode for keeping existing robots working with the robot security, as these cannot be expected
 * to be updated.
 * <p>
 * When robot classes are loaded, calls to the Java classes that these methods replace are replaced with calls to these
 * methods. Robots do not need any changes for this, and should not use this class directly.
 * <p>
 * The first two parameters of the methods replacing a constructor are always null. They take the place of the object
 * that the constructor would have initialized and the copy of it, so the replaced instructions have the same length
 * and stack effect.
 *
 * @author Flemming N. Larsen (original)
 */
public final class RobotCompat {

	private static final String USE_ROBOCODE_STREAMS = "You must use a RobocodeFileOutputStream or RobocodeFileWriter";

	private RobotCompat() {
	}

	// javax.imageio.ImageIO

	/**
	 * Does nothing, as the setting of ImageIO is shared with Robocode. {@link #write(RenderedImage, String, OutputStream)}
	 * never uses a cache file.
	 *
	 * @param useCache ignored.
	 */
	public static void setUseCache(boolean useCache) {
	}

	/**
	 * Writes an image like {@link ImageIO#write(RenderedImage, String, OutputStream)}, but without a cache file, as
	 * robots may only write files in their own data directory.
	 *
	 * @param image      the image to write.
	 * @param formatName the name of the image format.
	 * @param output     the stream to write to.
	 * @return false if there is no writer for the format; true otherwise.
	 * @throws IOException if the image cannot be written.
	 */
	@SuppressWarnings("removal")
	public static boolean write(final RenderedImage image, final String formatName, OutputStream output) throws IOException {
		if (output == null) {
			throw new IllegalArgumentException("output == null!");
		}
		final ImageOutputStream stream = new MemoryCacheImageOutputStream(output);

		try {
			// ImageIO looks up its plugins on the class path, which robots are not allowed to read, the first time it is
			// used. Code of the robot called from here, e.g. its own OutputStream, still has only the robot's permissions.
			return AccessController.doPrivileged(new PrivilegedExceptionAction<Boolean>() {
				public Boolean run() throws IOException {
					// ImageIO looks up plugins with the context class loader, which must not be the robot's class loader
					Thread thread = Thread.currentThread();
					ClassLoader contextClassLoader = thread.getContextClassLoader();

					thread.setContextClassLoader(RobotCompat.class.getClassLoader());
					try {
						return ImageIO.write(image, formatName, stream);
					} finally {
						thread.setContextClassLoader(contextClassLoader);
					}
				}
			});
		} catch (PrivilegedActionException e) {
			throw (IOException) e.getCause();
		} finally {
			stream.close();
		}
	}

	// java.lang.Thread

	/**
	 * Stops a thread of the robot like {@link Thread#stop()}. As threads cannot be stopped with Java 20 and newer, the
	 * thread is interrupted with these Java versions.
	 *
	 * @param thread the thread to stop.
	 * @throws SecurityException if the thread does not belong to the robot.
	 */
	@SuppressWarnings({"deprecation", "removal"})
	public static void stop(final Thread thread) {
		if (thread.getState() == Thread.State.TERMINATED) {
			return; // A thread that has terminated no longer belongs to a thread group
		}
		RobotFileAccess.getThreadManager().checkRobotThreadStop(thread);
		try {
			// The check above replaces the permission for this, which only Robocode has
			AccessController.doPrivileged(new PrivilegedAction<Void>() {
				public Void run() {
					thread.stop();
					return null;
				}
			});
		} catch (UnsupportedOperationException | LinkageError e) { // Thread.stop() is removed with Java 26
			thread.interrupt();
		}
	}

	// java.util.Scanner

	public static Scanner newScanner(Object ignored1, Object ignored2, File source) throws FileNotFoundException {
		return new Scanner(RobotFileAccess.checkRead(source));
	}

	public static Scanner newScanner(Object ignored1, Object ignored2, File source, String charsetName) throws FileNotFoundException {
		return new Scanner(RobotFileAccess.checkRead(source), charsetName);
	}

	public static Scanner newScanner(Object ignored1, Object ignored2, File source, Charset charset) throws FileNotFoundException {
		return new Scanner(RobotFileAccess.checkRead(source), charset.name());
	}

	// Used by the subclasses of the Java classes that robots cannot write files with, but where robots must only be
	// stopped when they really try it, as they must otherwise still work with it in code that is not run

	static <T> T denyWrite(T target) {
		RobotFileAccess.getThreadManager().denyRobotFileAccess(target, USE_ROBOCODE_STREAMS);
		throw new SecurityException(USE_ROBOCODE_STREAMS);
	}

	static <T> T denyRead(T target) {
		RobotFileAccess.getThreadManager().denyRobotFileAccess(target, "You may only read files in your own root package directory");
		throw new SecurityException("Robots cannot read from a file descriptor");
	}
}
