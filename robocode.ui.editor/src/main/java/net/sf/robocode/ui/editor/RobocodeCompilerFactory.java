/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.ui.editor;


import net.sf.robocode.core.ContainerBase;
import net.sf.robocode.io.FileUtil;
import net.sf.robocode.io.Logger;
import net.sf.robocode.manager.IVersionManagerBase;
import static net.sf.robocode.io.Logger.logError;
import static net.sf.robocode.io.Logger.logMessage;
import net.sf.robocode.ui.dialog.ConsoleDialog;
import net.sf.robocode.ui.dialog.WindowUtil;
import net.sf.robocode.util.JavaVersion;

import javax.swing.*;
import java.io.*;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public class RobocodeCompilerFactory {

	private CompilerProperties compilerProperties;

	public RobocodeCompiler createCompiler(RobocodeEditor editor) {
		compilerProperties = null;
		if (getCompilerProperties().getCompilerBinary() == null
				|| getCompilerProperties().getCompilerBinary().length() == 0) {
			if (configureCompiler(editor)) {
				return new RobocodeCompiler(editor, getCompilerProperties().getCompilerBinary(),
						getCompilerProperties().getCompilerOptions(), getCompilerProperties().getCompilerClasspath());
			}
			logError("Unable to create compiler.");
			return null;
		}
		return new RobocodeCompiler(editor, getCompilerProperties().getCompilerBinary(),
				getCompilerProperties().getCompilerOptions(), getCompilerProperties().getCompilerClasspath());
	}

	public CompilerProperties getCompilerProperties() {
		if (compilerProperties == null) {
			compilerProperties = new CompilerProperties();

			FileInputStream in = null;
			File file = null;

			try {
				file = FileUtil.getCompilerConfigFile();
				in = new FileInputStream(file);
				compilerProperties.load(in);
				if (compilerProperties.getRobocodeVersion() == null) {
					logMessage("Setting up new compiler.");
					compilerProperties.setCompilerBinary("");
				}
			} catch (FileNotFoundException e) {
				logMessage("Compiler configuration file was not found. A new one will be created.");
			} catch (IOException e) {
				logError("Error while reading " + file, e);
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException ignored) {}
				}
			}
		}
		return compilerProperties;
	}

	private static String getClassPath() {
		StringBuilder sb = new StringBuilder("-classpath ");

		if (JavaVersion.getJavaMajorVersion() < 9) {
			// rt.jar is not supported by Java 9+ anymore
			sb.append(getJavaLib()).append(File.pathSeparator);
		}
		sb.append("libs").append(File.separator);
		sb.append("robocode.jar").append(File.pathSeparator);
		sb.append(FileUtil.quoteFileName(FileUtil.getRobotsDir().toString()));

		return sb.toString();
	}
	
	private static String getJavaLib() {
		String javahome = System.getProperty("java.home");
		String javalib;
		if (System.getProperty("os.name").indexOf("Mac") == 0) {
			javalib = new File(javahome).getParentFile().getPath() + "/Classes/classes.jar";
		} else {
			javalib = javahome + "/lib/rt.jar";
		}

		return FileUtil.quoteFileName(javalib);
	}

	public boolean configureCompiler(RobocodeEditor editor) {
		ConsoleDialog console = new ConsoleDialog(editor, "Setting up compiler", false);

		console.setSize(500, 400);
		console.getOkButton().setEnabled(false);
		console.setText("Please wait while Robocode sets up a compiler for you...\n\n");
		WindowUtil.centerShow(editor, console);

		console.append("Setting up compiler\n");
		console.append("Java home is " + System.getProperty("java.home") + "\n\n");

		String compilerName = "Java Compiler (javac)";
		String compilerBinary = "javac";
		String compilerOptions = "-verbose -encoding UTF-8";

		boolean javacOK = testCompiler(compilerName, compilerBinary, console);
		boolean ecjOK = false;

		if (javacOK) {
			int rc = JOptionPane.showConfirmDialog(editor,
					"Robocode has found a working javac (Java Compiler) on this system.\nWould you like to use it?\n"
					+ "(If you click No, Robocode will use the build-in Eclipse Compiler for Java (ECJ))",
					"Confirm javac",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE);

			if (rc == JOptionPane.NO_OPTION) {
				javacOK = false;
			}
		}
		
		if (!javacOK) {
			compilerName = "Eclipse Compiler for Java (ECJ)";
			compilerBinary = "java -cp compilers/ecj.jar org.eclipse.jdt.internal.compiler.batch.Main";

			ecjOK = testCompiler("Eclipse Compiler for Java (ECJ)", compilerBinary, console);
		}

		boolean compilerOK = javacOK || ecjOK;

		if (compilerOK) {
			console.append("\nCompiler has been set up successfully.\nClick OK to continue.\n");

		} else {
			final String errorText = "Could not set up a working compiler for Robocode.\n"
					+ "Please consult the console window for errors.\n\n"
					+ "For help with this, please post to Help forum here:\n"
					+ "http://sourceforge.net/projects/robocode/forums/forum/116459";
			
			console.append("\nUnable to set up a working compiler for Robocode.\n");

			JOptionPane.showMessageDialog(editor, errorText, "Error", JOptionPane.ERROR_MESSAGE);

			compilerBinary = "";
			compilerOptions = "";
		}

		getCompilerProperties().setRobocodeVersion(ContainerBase.getComponent(IVersionManagerBase.class).getVersion());
		getCompilerProperties().setCompilerBinary(compilerBinary);
		getCompilerProperties().setCompilerOptions(compilerOptions);
		getCompilerProperties().setCompilerClasspath(getClassPath());
		saveCompilerProperties();

		console.scrollToBottom();
		console.getOkButton().setEnabled(true);

		return compilerOK;
	}

	public void saveCompilerProperties() {
		FileOutputStream out = null;

		try {
			out = new FileOutputStream(FileUtil.getCompilerConfigFile());

			getCompilerProperties().store(out, "Robocode Compiler Properties");
		} catch (IOException e) {
			Logger.logError(e);
		} finally {
			FileUtil.cleanupStream(out);
		}
	}

	/**
	 * Tests a compiler by trying to let it compile the CompilerTest.java file.
	 *
	 * @param friendlyName friendly name of the compiler to test.
	 * @param filepath the file path of the compiler.
	 * @param console the console which outputs the result.
	 * @return true if the compiler was found and did compile the test file; false otherwise.
	 */
	private static boolean testCompiler(String friendlyName, String filepath, ConsoleDialog console) {
		console.append("Testing compile with " + friendlyName + "\n");

		boolean result = false;

		try {
			String cmdAndArgs = filepath + " compilers/CompilerTest.java";

			// Must be split command and arguments individually
			ProcessBuilder pb = new ProcessBuilder(cmdAndArgs.split(" "));

			pb.directory(FileUtil.getCwd());
			pb.redirectErrorStream(true); // we can use p.getInputStream()		
			Process p = pb.start();

			// The waitFor() must done after reading the input and error stream of the process
			console.processStream(p.getInputStream());
			p.waitFor();

			result = (p.exitValue() == 0);

		} catch (IOException e) {
			logError(e);
		} catch (InterruptedException e) {
			// Immediately reasserts the exception by interrupting the caller thread itself
			Thread.currentThread().interrupt();
		}

		if (result) {
			console.append(friendlyName + " was found and is working.\n");
		} else {
			console.append(friendlyName + " does not exists or cannot compile.\n");
		}
		return result;
	}
}
