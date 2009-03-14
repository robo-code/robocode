/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Code cleanup
 *     - Updated Jikes compiler to version 1.22
 *     - Changed deprecated method calls
 *     - File names are being quoted
 *     - Updated to use methods from FileUtil and Logger, which replaces methods
 *       that have been (re)moved from the robocode.util.Utils and Constants
 *     - Changed to use FileUtil.getCompilerConfigFile()
 *     - Added missing close() on FileInputStreams, FileOutputStreams, and
 *       JarInputStream
 *******************************************************************************/
package net.sf.robocode.ui.editor;


import net.sf.robocode.io.FileUtil;
import net.sf.robocode.io.Logger;
import static net.sf.robocode.io.Logger.logError;
import static net.sf.robocode.io.Logger.logMessage;
import net.sf.robocode.ui.dialog.ConsoleDialog;
import net.sf.robocode.ui.dialog.WindowUtil;
import net.sf.robocode.version.IVersionManager;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public class RobocodeCompilerFactory {

	private final static String COMPILER_CLASSPATH = "-classpath " + getJavaLib() + File.pathSeparator + "libs"
			+ File.separator + "robocode.jar" + File.pathSeparator
			+ FileUtil.quoteFileName(FileUtil.getRobotsDir().toString());

	private static CompilerProperties compilerProperties;
	private static boolean compilerInstalling;

	private static final char SPINNER[] = {
		'-', '\\', '|', '/'
	};

	private final IVersionManager versionManager;

	public RobocodeCompilerFactory(IVersionManager versionManager) {
		this.versionManager = versionManager;
	}

	public RobocodeCompiler createCompiler(RobocodeEditor editor) {
		compilerProperties = null;
		if (getCompilerProperties().getCompilerBinary() == null
				|| getCompilerProperties().getCompilerBinary().length() == 0) {
			if (installCompiler(editor)) {
				return new RobocodeCompiler(editor, getCompilerProperties().getCompilerBinary(),
						getCompilerProperties().getCompilerOptions(), getCompilerProperties().getCompilerClasspath());
			}
			logError("Unable to create compiler.");
			return null;
		}
		return new RobocodeCompiler(editor, getCompilerProperties().getCompilerBinary(),
				getCompilerProperties().getCompilerOptions(), getCompilerProperties().getCompilerClasspath());
	}

	public boolean extract(File src, File dest) {
		JDialog statusDialog = new JDialog();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int height = 50;

		if (File.separatorChar == '/') {
			height = 100;
		}

		statusDialog.setTitle("Installing");
		statusDialog.setLocation((screenSize.width - 500) / 2, (screenSize.height - height) / 2);
		statusDialog.setSize(500, height);
		JLabel status = new JLabel();

		statusDialog.getContentPane().setLayout(new BorderLayout());
		statusDialog.getContentPane().add(status, BorderLayout.CENTER);

		statusDialog.setVisible(true);

		FileInputStream fis = null;
		FileOutputStream fos = null;
		JarInputStream jarIS = null;

		String entryName;

		byte buf[] = new byte[2048];

		try {
			fis = new FileInputStream(src);
			jarIS = new JarInputStream(fis);

			JarEntry entry = jarIS.getNextJarEntry();

			while (entry != null) {
				int spin = 0;

				entryName = entry.getName();
				if (entry.isDirectory()) {
					File dir = new File(dest, entry.getName());

					if (!dir.exists() && !dir.mkdirs()) {
						Logger.logError("Can't create " + dir);
					}
				} else {
					status.setText(entryName + " " + SPINNER[spin++]);
					File out = new File(dest, entry.getName());
					File parentDirectory = new File(out.getParent());

					if (!parentDirectory.exists() && !parentDirectory.mkdirs()) {
						Logger.logError("Can't create " + parentDirectory);
					}

					int index = 0;

					try {
						fos = new FileOutputStream(out);

						int num;
						int count = 0;

						while ((num = jarIS.read(buf, 0, 2048)) != -1) {
							fos.write(buf, 0, num);
							index += num;
							count++;
							if (count > 80) {
								status.setText(entryName + " " + SPINNER[spin++] + " (" + index + " bytes)");
								if (spin > 3) {
									spin = 0;
								}
								count = 0;
							}
						}
					} finally {
						FileUtil.cleanupStream(fos);
					}

					status.setText(entryName + " " + SPINNER[spin] + " (" + index + " bytes)");
				}
				entry = jarIS.getNextJarEntry();
			}
			statusDialog.dispose();
			return true;
		} catch (IOException e) {
			statusDialog.dispose();
			WindowUtil.error(null, e.toString());
			return false;
		} finally {
			FileUtil.cleanupStream(fis);
			FileUtil.cleanupStream(jarIS);
		}
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
					logMessage("Setting up new compiler");
					compilerProperties.setCompilerBinary("");
				}
			} catch (FileNotFoundException e) {
				logMessage("Compiler configuration file was not found. A new one will be created.");
			} catch (IOException e) {
				logError("IO Exception reading " + file, e);
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

	public boolean installCompiler(RobocodeEditor editor) {
		if (compilerInstalling) {
			JOptionPane.showMessageDialog(editor,
					"Sorry, the compiler is still installing.\nPlease wait until it is complete.", "Error",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
		compilerInstalling = true;

		String compilerBinary;
		String compilerOptions;

		String osName = System.getProperty("os.name");

		ConsoleDialog console = new ConsoleDialog(editor, "Setting up compiler", false);

		console.setSize(500, 400);
		console.getOkButton().setEnabled(false);
		console.setText("Please wait while Robocode sets up a compiler for you...\n\n");
		WindowUtil.centerShow(editor, console);

		console.append("Setting up compiler for " + osName + "\n");
		console.append("Java home is " + System.getProperty("java.home") + "\n\n");

		boolean javacOk = testJavac(console);
		boolean jikesOk = false;
		boolean rv = true;
		boolean mustBuildJikes = false;
		String jikesJar;
		String jikesBinary;
		boolean noExtract = false;

		compilerProperties.setRobocodeVersion(versionManager.getVersion());

		if (osName.indexOf("Windows") == 0) {
			jikesJar = "compilers/jikes-1.22.win.jar";
			jikesBinary = "./jikes-1.22/bin/jikes.exe";
			mustBuildJikes = false;
		} else if (osName.indexOf("Linux") == 0) {
			jikesJar = "compilers/jikes-1.22.src.jar";
			jikesBinary = "./jikes-1.22/bin/jikes";
			mustBuildJikes = true;
		} else if (osName.indexOf("Mac") == 0) {
			jikesJar = "compilers/jikes-1.22.src.jar";
			jikesBinary = "/usr/bin/jikes";
			jikesOk = testJikes(console, jikesBinary + " -classpath " + getJavaLib());
			if (!jikesOk) {
				mustBuildJikes = true;
				jikesBinary = "./jikes-1.22/bin/jikes";
			}
		} else {
			jikesJar = "compilers/jikes-1.22.src.jar";
			mustBuildJikes = true;
			jikesBinary = "./jikes-1.22/bin/jikes";
		}

		if (javacOk) {
			String noString = "(If you click No, Robocode will install and use Jikes)";

			if (jikesOk) {
				noString = "(If you click No, Robocode will use Jikes)";
			}

			int rc = JOptionPane.showConfirmDialog(editor,
					"Robocode has found a working javac on this system.\nWould you like to use it?\n" + noString,
					"Confirm javac", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

			if (rc == JOptionPane.NO_OPTION) {
				javacOk = false;
			}
		}
		if (javacOk) {
			compilerBinary = "javac";
			compilerOptions = "-deprecation -g";
			getCompilerProperties().setCompilerBinary(compilerBinary);
			getCompilerProperties().setCompilerOptions(compilerOptions);
			getCompilerProperties().setCompilerClasspath(COMPILER_CLASSPATH);
			saveCompilerProperties();
			console.append("\nCongratulations!  Compiler set up successfully.\n");
			console.append("Click OK to continue.\n");
			console.scrollToBottom();
			rv = true;
		} else if (jikesOk) {
			compilerBinary = jikesBinary;
			compilerOptions = "-deprecation -g -Xstdout +T4";
			getCompilerProperties().setCompilerBinary(compilerBinary);
			getCompilerProperties().setCompilerOptions(compilerOptions);
			getCompilerProperties().setCompilerClasspath(COMPILER_CLASSPATH);
			saveCompilerProperties();
			console.append("\nCongratulations!  Jikes set up successfully.\n");
			console.append("Click OK to continue.\n");
			console.scrollToBottom();
			rv = true;
		}
		if (!javacOk && !jikesOk) {
			final String MESSAGE_BOX_HELP_TEXT = "\n\nPlease consult the console window for errors.\n"
					+ "For help with this, please post to the discussion at:\n" + "http://robocode.sourceforge.net/forum";
			
			console.append("\nExtracting Jikes...\n");
			if (noExtract || extract(new File(FileUtil.getCwd(), jikesJar), new File("."))) {
				if (!noExtract) {
					console.append("Jikes extracted successfully.\n");
				}
				if (mustBuildJikes) {
					if (JOptionPane.showConfirmDialog(editor,
							"Robocode is now going to build Jikes for you.\nThis will take a while...\n", "Java time",
							JOptionPane.OK_CANCEL_OPTION)
							== JOptionPane.OK_OPTION) {
						if (makeJikes(console, jikesBinary + " -classpath " + getJavaLib())) {
							compilerBinary = jikesBinary;
							compilerOptions = "-deprecation -g -Xstdout +T4";
							getCompilerProperties().setCompilerBinary(compilerBinary);
							getCompilerProperties().setCompilerOptions(compilerOptions);
							getCompilerProperties().setCompilerClasspath(COMPILER_CLASSPATH);
							saveCompilerProperties();
							console.append("\nCongratulations!  Jikes is installed successfully.\n");
							console.append("Click OK to continue.\n");
							console.scrollToBottom();
							rv = true;
						} else {
							JOptionPane.showMessageDialog(editor,
									"Robocode was unable to build and test Jikes." + MESSAGE_BOX_HELP_TEXT, "Error",
									JOptionPane.ERROR_MESSAGE);
							compilerOptions = "-deprecation -g";
							getCompilerProperties().setCompilerOptions(compilerOptions);
							getCompilerProperties().setCompilerClasspath(COMPILER_CLASSPATH);
							saveCompilerProperties();
							rv = false;
						}
					}
				} else {
					compilerBinary = jikesBinary;
					compilerOptions = "-deprecation -g -Xstdout +T4";
					getCompilerProperties().setCompilerBinary(compilerBinary);
					getCompilerProperties().setCompilerOptions(compilerOptions);
					getCompilerProperties().setCompilerClasspath(COMPILER_CLASSPATH);
					if (testJikes(console, jikesBinary + " -classpath " + getJavaLib())) {
						saveCompilerProperties();
						console.append("\nCongratulations! Jikes set up successfully.\n");
						console.append("Click OK to continue.\n");
						console.scrollToBottom();
						rv = true;
					} else {
						JOptionPane.showMessageDialog(editor,
								"Robocode was unable to successfully compile with Jikes." + MESSAGE_BOX_HELP_TEXT, "Error",
								JOptionPane.ERROR_MESSAGE);
						saveCompilerProperties();
						rv = false;
					}
				}
			} else {
				console.append("\nUnable to set up a compiler for Robocode.\n");
				console.append("javac could not be detected and Jikes could not be\n");
				console.append("extracted and set up as an alternative to javac.\n");

				console.scrollToBottom();
				JOptionPane.showMessageDialog(editor,
						"Robocode was unable set up a compiler for Robocode." + MESSAGE_BOX_HELP_TEXT, "Error",
						JOptionPane.ERROR_MESSAGE);
				compilerOptions = "-deprecation -g";
				getCompilerProperties().setCompilerOptions(compilerOptions);
				getCompilerProperties().setCompilerClasspath(COMPILER_CLASSPATH);
				saveCompilerProperties();
				rv = false;
			}
		}

		compilerInstalling = false;
		console.getOkButton().setEnabled(true);
		return rv;
	}

	private static boolean makeJikes(ConsoleDialog console, String jikesBinary) {
		boolean result = false;

		console.append("\nRobocode building Jikes...\n");

		try {
			String command = "./compilers/buildJikes.sh";

			logMessage(command);

			ProcessBuilder pb = new ProcessBuilder(command);

			pb.directory(FileUtil.getCwd());
			pb.redirectErrorStream(true);
			Process p = pb.start();

			// The waitFor() must done after reading the input and error stream of the process
			console.processStream(p.getInputStream());
			p.waitFor();

			if (p.exitValue() == 0) {
				console.append("Finished building Jikes\n");
				result = testJikes(console, jikesBinary);
			} else {
				console.append("Jikes compile Failed (" + p.exitValue() + ")\n");
			}
		} catch (IOException e) {
			console.append("\n" + e.toString() + "\n");
			console.setTitle("Jikes compile failed.\n");

		} catch (InterruptedException e) {
			// Immediately reasserts the exception by interrupting the caller thread itself
			Thread.currentThread().interrupt();

			console.append("\n" + e.toString() + "\n");
			console.setTitle("Jikes compile failed.\n");

		} finally {
			console.scrollToBottom();
		}
		return result;
	}

	public static void saveCompilerProperties() {
		if (compilerProperties == null) {
			logError("Cannot save null compiler properties");
			return;
		}
		FileOutputStream out = null;

		try {
			out = new FileOutputStream(FileUtil.getCompilerConfigFile());

			compilerProperties.store(out, "Robocode Compiler Properties");
		} catch (IOException e) {
			Logger.logError(e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException ignored) {}
			}
		}
	}

	private static boolean testJavac(ConsoleDialog console) {
		console.append("Testing compile with javac...\n");

		boolean javacOk = false;

		try {
			ProcessBuilder pb = new ProcessBuilder("javac", "compilers/CompilerTest.java");

			pb.directory(FileUtil.getCwd());
			pb.redirectErrorStream(true);
			Process p = pb.start();

			// The waitFor() must done after reading the input and error stream of the process
			console.processStream(p.getInputStream());
			p.waitFor();

			javacOk = (p.exitValue() == 0);

		} catch (IOException e) {
			logError(e);

		} catch (InterruptedException e) {
			// Immediately reasserts the exception by interrupting the caller thread itself
			Thread.currentThread().interrupt();
		}

		if (javacOk) {
			console.append("javac ok.\n");
		} else {
			console.append("javac does not exist.\n");
		}
		return javacOk;
	}

	private static boolean testJikes(ConsoleDialog console, String jikesBinary) {
		console.append("\nTesting compile with Jikes...\n");

		boolean jikesOk = false;

		final String command = jikesBinary + " compilers/CompilerTest.java";

		try {
			ProcessBuilder pb = new ProcessBuilder(command.split(" "));

			pb.directory(FileUtil.getCwd());
			pb.redirectErrorStream(true);
			Process p = pb.start();

			// The waitFor() must done after reading the input and error stream of the process
			console.processStream(p.getInputStream());
			p.waitFor();

			jikesOk = (p.exitValue() == 0);

		} catch (IOException e) {
			logError(e);

		} catch (InterruptedException e) {
			// Immediately reasserts the exception by interrupting the caller thread itself
			Thread.currentThread().interrupt();
		}

		if (jikesOk) {
			console.append("Jikes ok.\n");
		} else {
			console.append("Unable to compile with Jikes!\n");
		}
		return jikesOk;
	}
}
