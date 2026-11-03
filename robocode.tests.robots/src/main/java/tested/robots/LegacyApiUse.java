/*
 * Copyright (c) 2001-2026 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package tested.robots;


import robocode.AdvancedRobot;
import robocode.RobocodeFileOutputStream;

import javax.imageio.ImageIO;
import java.awt.AWTKeyStroke;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;


/**
 * Uses the Java classes that existing robots use in the ways that must keep working with the robot security, as these
 * robots cannot be expected to be updated.
 *
 * @author Flemming N. Larsen (original)
 */
public class LegacyApiUse extends AdvancedRobot {

	public static final String OK_MESSAGE = "Legacy API use OK";

	public void run() {
		try {
			check(useLogger(), "Logger");
			check(useKeyStroke(), "AWTKeyStroke");
			check(useScanner(), "Scanner");
			check(useImageIO(), "ImageIO");
			check(stopOwnThread(), "Thread.stop");
			check(useZipFile(), "ZipFile");
			check(useSecurityClasses(), "MessageDigest and SecureRandom");
			out.println(OK_MESSAGE);
		} catch (IOException e) {
			e.printStackTrace(out);
		} catch (GeneralSecurityException e) {
			e.printStackTrace(out);
		} catch (InterruptedException e) {
			return;
		}
		for (int i = 0; i < 10; i++) {
			turnLeft(10);
		}
	}

	private void check(boolean condition, String what) {
		if (!condition) {
			out.println("Failed: " + what);
			throw new IllegalStateException(what);
		}
	}

	private boolean useZipFile() throws IOException {
		File file = getDataFile("data.zip");
		ZipOutputStream output = new ZipOutputStream(new RobocodeFileOutputStream(file));

		output.putNextEntry(new ZipEntry("entry"));
		output.write('h');
		output.close();

		ZipFile zip = new ZipFile(file);

		try {
			return zip.getInputStream(zip.getEntry("entry")).read() == 'h';
		} finally {
			zip.close();
			file.delete();
		}
	}

	private boolean useSecurityClasses() throws GeneralSecurityException {
		MessageDigest digest = MessageDigest.getInstance("SHA-1");

		digest.update(new byte[] {1});

		SecureRandom random = new SecureRandom();
		SecureRandom provided = SecureRandom.getInstance("SHA1PRNG", "SUN");

		provided.setSeed(random.nextLong());
		return digest.digest().length == 20 && provided.nextInt(2) < 2;
	}

	private boolean useLogger() {
		Logger.getLogger(LegacyApiUse.class.getName()).log(Level.FINEST, null, new CloneNotSupportedException());
		return true;
	}

	private boolean useKeyStroke() {
		AWTKeyStroke keyStroke = AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_A, 0);

		return keyStroke.getKeyCode() == KeyEvent.VK_A && keyStroke.getModifiers() == 0;
	}

	private boolean useScanner() throws IOException {
		File file = getDataFile("scanner.txt");
		PrintStream ps = new PrintStream(new RobocodeFileOutputStream(file));

		ps.println("hello");
		ps.close();

		Scanner scanner = new Scanner(file);

		try {
			return "hello".equals(scanner.nextLine());
		} finally {
			scanner.close();
			file.delete();
		}
	}

	private boolean useImageIO() throws IOException {
		File file = getDataFile("image.png");
		RobocodeFileOutputStream output = new RobocodeFileOutputStream(file);

		try {
			ImageIO.setUseCache(false);
			return ImageIO.write(new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB), "png", output) && file.length() > 0;
		} finally {
			output.close();
			file.delete();
		}
	}

	@SuppressWarnings("deprecation")
	private boolean stopOwnThread() throws InterruptedException {
		Thread thread = new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(60000);
				} catch (InterruptedException ignore) {// Java 20+ cannot stop threads, so these are interrupted
				}
			}
		});

		thread.start();
		thread.stop();
		thread.join(10000);

		// A call via a subclass of Thread is compiled with the subclass as the owner of the method
		Worker worker = new Worker();

		worker.start();
		worker.stop();
		worker.join(10000);

		// A method reference is a method handle in the class file
		Worker referenced = new Worker();
		Consumer<Thread> stopper = Thread::stop;

		referenced.start();
		stopper.accept(referenced);
		referenced.join(10000);

		return !thread.isAlive() && !worker.isAlive() && !referenced.isAlive();
	}

	private static class Worker extends Thread {
		@Override
		public void run() {
			try {
				Thread.sleep(60000);
			} catch (InterruptedException ignore) {// Java 20+ cannot stop threads, so these are interrupted
			}
		}
	}

	private interface FileWriterFactory {
		FileWriter create(String fileName) throws IOException;
	}

	/**
	 * Never called, but a method reference to a constructor of a replaced class must not prevent the robot from being
	 * loaded.
	 */
	@SuppressWarnings("unused")
	private FileWriterFactory constructorReference() {
		return FileWriter::new;
	}

	/**
	 * Never called. Existing robots may have code like this, e.g. from a library, which must not prevent them from being
	 * loaded. It is stopped if a robot runs it.
	 */
	@SuppressWarnings("unused")
	private void notCalled() throws IOException {
		new FileOutputStream("file").close();
		new FileOutputStream(FileDescriptor.out).close();
		new FileOutputStream("file", true).close();
		new FileOutputStream(new File("file")).close();
		new FileOutputStream(new File("file"), true).close();
		new FileWriter("file").close();
		new FileWriter("file", true).close();
		new FileWriter(new File("file")).close();
		new FileWriter(new File("file"), true).close();
		new FileWriter(FileDescriptor.out).close();
		new FileReader(FileDescriptor.in).close();
	}
}
