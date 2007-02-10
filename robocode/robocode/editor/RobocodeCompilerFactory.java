/*******************************************************************************
 * Copyright (c) 2001, 2007 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Updated Jikes compiler to version 1.22
 *     - Changed deprecated method calls
 *     - File names are being quoted
 *     - Code cleanup
 *     - Updated to use methods from FileUtil and Logger, which replaces methods
 *       that have been (re)moved from the robocode.util.Utils and Constants
 *******************************************************************************/
package robocode.editor;


import static robocode.io.Logger.log;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import robocode.dialog.ConsoleDialog;
import robocode.dialog.WindowUtil;
import robocode.io.FileUtil;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public class RobocodeCompilerFactory {
	private final static String COMPILER_CLASSPATH = "-classpath " + getJavaLib() + File.pathSeparator + "robocode.jar"
			+ File.pathSeparator + getRobotPath();

	private static CompilerProperties compilerProperties;
	private static String robotPath;
	private static boolean compilerInstalling;

	private static final char SPINNER[] = {
		'-', '\\', '|', '/'
	};

	/**
	 * RobocodeCompiler constructor comment.
	 */
	public RobocodeCompilerFactory() {
		super();
	}

	public static RobocodeCompiler createCompiler(RobocodeEditor editor) {
		compilerProperties = null;
		if (getCompilerProperties().getCompilerBinary() == null
				|| getCompilerProperties().getCompilerBinary().equals("")) {
			if (installCompiler(editor)) {
				return new RobocodeCompiler(editor, getCompilerProperties().getCompilerBinary(),
						getCompilerProperties().getCompilerOptions(), getCompilerProperties().getCompilerClasspath());
			} else {
				log("Unable to create compiler.");
				return null;
			}
		} else {
			return new RobocodeCompiler(editor, getCompilerProperties().getCompilerBinary(),
					getCompilerProperties().getCompilerOptions(), getCompilerProperties().getCompilerClasspath());
		}
	}

	public static boolean extract(File src, File dest) {
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

		FileOutputStream fos = null;
		String entryName = "";

		byte buf[] = new byte[2048];

		try {
			JarInputStream jarIS = new JarInputStream(new FileInputStream(src));

			JarEntry entry = jarIS.getNextJarEntry();

			while (entry != null) {
				int spin = 0;

				entryName = entry.getName();
				if (entry == null) {
					System.err.println("Could not find entry: " + entry);
				} else {
					if (entry.isDirectory()) {
						File dir = new File(dest, entry.getName());

						dir.mkdirs();
					} else {
						status.setText(entryName + " " + SPINNER[spin++]);
						File out = new File(dest, entry.getName());
						File parentDirectory = new File(out.getParent());

						parentDirectory.mkdirs();
						fos = new FileOutputStream(out);

						int index = 0;
						int num = 0;
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
						fos.close();

						status.setText(entryName + " " + SPINNER[spin++] + " (" + index + " bytes)");
					}
				}
				entry = jarIS.getNextJarEntry();
			}
			statusDialog.dispose();
			return true;
		} catch (IOException e) {
			statusDialog.dispose();
			WindowUtil.error(null, e.toString());
			return false;
		}
	}

	public static CompilerProperties getCompilerProperties() {
		if (compilerProperties == null) {
			compilerProperties = new CompilerProperties();
			try {
				FileInputStream in = new FileInputStream(new File(FileUtil.getCwd(), "compiler.properties"));

				compilerProperties.load(in);
				if (compilerProperties.getRobocodeVersion() == null) {
					log("Setting up new compiler");
					compilerProperties.setCompilerBinary("");
				}
			} catch (FileNotFoundException e) {
				log("Setting up compiler.");
			} catch (IOException e) {
				log("IO Exception reading compiler.properties" + e);
			}
		}
		return compilerProperties;
	}

	private static String getJavaLib() {
		String javahome = System.getProperty("java.home");

		String javalib = "";

		if (System.getProperty("os.name").indexOf("Mac") == 0) {
			javalib = new File(javahome).getParentFile().getPath() + "/Classes/classes.jar";
		} else {
			javalib = javahome + "/lib/rt.jar";
		}

		return FileUtil.quoteFileName(javalib);
	}

	private static String getRobotPath() {
		if (robotPath == null) {
			robotPath = System.getProperty("ROBOTPATH", "robots");
		}

		return FileUtil.quoteFileName(robotPath);
	}

	public static boolean installCompiler(RobocodeEditor editor) {
		if (compilerInstalling) {
			JOptionPane.showMessageDialog(editor,
					"Sorry, the compiler is still installing.\nPlease wait until it is complete.", "Error",
					JOptionPane.ERROR_MESSAGE);
			return false;
		} else {
			compilerInstalling = true;
		}

		String compilerBinary = null;
		String compilerOptions = "";

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
		String jikesJar = "";
		String jikesBinary = "";
		boolean noExtract = false;

		compilerProperties.setRobocodeVersion(editor.getManager().getVersionManager().getVersion());

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
			console.append("\nExtracting Jikes...\n");
			if (noExtract || extract(new File(FileUtil.getCwd(), jikesJar), new File("."))) {
				if (!noExtract) {
					console.append("Jikes extracted successfully.\n");
				}
				if (mustBuildJikes) {
					if (JOptionPane.showConfirmDialog(editor,
							"Robocode is now going to build Jikes for you.\nThis will take a while... got get a cup of coffee!\n",
							"Java time", JOptionPane.OK_CANCEL_OPTION)
							== JOptionPane.OK_OPTION) {
						if (makeJikes(editor, console, jikesBinary + " -classpath " + getJavaLib())) {
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
									"Robocode was unable to build and test Jikes.\n"
									+ "Please consult the console window for errors.\n"
									+ "For help with this, please post to the discussion at:\n" + "http://robocode.net/forum",
									"Error",
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
						console.append("\nCongratulations!  Compiler set up successfully.\n");
						console.append("Click OK to continue.\n");
						console.scrollToBottom();
						rv = true;
					} else {
						JOptionPane.showMessageDialog(editor,
								"Robocode was unable to successfully compile with Jikes\n"
								+ "Please consult the console window for errors.\n"
								+ "For help with this, please post to the discussion at:\n"
								+ "http://robocode.sourceforge.net/forum",
								"Error",
								JOptionPane.ERROR_MESSAGE);
						saveCompilerProperties();
						rv = false;
					}
				}
			} else {
				console.append("Unable to extract Jikes.\n");
				console.append("Unable to determine compiler for this sytem.\n");
				console.scrollToBottom();
				JOptionPane.showMessageDialog(editor,
						"Robocode was unable to extract Jikes." + "Please consult the console window for errors.\n"
						+ "For help with this, please post to the discussion at:\n" + "http://robocode.sourceforge.net/forum",
						"Error",
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

	private static boolean makeJikes(RobocodeEditor editor, ConsoleDialog console, String jikesBinary) {
		String result = "";
		boolean rv = true;

		console.append("\nRobocode building Jikes...\n");

		try {
			String command = "./compilers/buildJikes.sh";

			log(command);
			Process p = Runtime.getRuntime().exec(command, null, FileUtil.getCwd());

			console.processStream(p.getInputStream());
			console.processStream(p.getErrorStream());
			p.waitFor();
			if (p.exitValue() == 0) {
				console.append("Finished building Jikes\n");
				console.scrollToBottom();
				if (testJikes(console, jikesBinary)) {
					rv = true;
				} else {
					rv = false;
				}
			} else {
				result = "Jikes compile Failed (" + p.exitValue() + ")\n";
				console.append(result);
				console.scrollToBottom();
				rv = false;
			}
		} catch (Exception e) {
			console.append("\n" + e.toString() + "\n");
			console.setTitle("Jikes compile failed.\n");
			console.scrollToBottom();
			rv = false;
		}
		return rv;
	}

	public static void saveCompilerProperties() {
		if (compilerProperties == null) {
			log("Cannot save null compiler properties");
			return;
		}
		try {
			FileOutputStream out = new FileOutputStream(new File(FileUtil.getCwd(), "compiler.properties"));

			compilerProperties.store(out, "Robocode Compiler Properties");
		} catch (IOException e) {
			log(e);
		}
	}

	private static boolean testJavac(ConsoleDialog console) {
		console.append("Testing compile with javac...\n");
		boolean javacOk = false;

		try {
			Process p = Runtime.getRuntime().exec("javac compilers/CompilerTest.java", null, FileUtil.getCwd());

			console.processStream(p.getInputStream());
			console.processStream(p.getErrorStream());
			p.waitFor();
			if (p.exitValue() == 0) {
				javacOk = true;
			}
		} catch (IOException e) {} catch (InterruptedException e) {}
		if (!javacOk) {
			console.append("javac does not exist.\n");
		} else {
			console.append("javac ok.\n");
		}
		return javacOk;
	}

	private static boolean testJikes(ConsoleDialog console, String jikesBinary) {
		console.append("\nTesting compile with Jikes...\n");
		boolean jikesOk = false;

		try {
			Process p = Runtime.getRuntime().exec(jikesBinary + " compilers/CompilerTest.java", null, FileUtil.getCwd());

			console.processStream(p.getInputStream());
			console.processStream(p.getErrorStream());
			p.waitFor();
			if (p.exitValue() == 0) {
				jikesOk = true;
			}
		} catch (IOException e) {} catch (InterruptedException e) {}
		if (!jikesOk) {
			console.append("Unable to compile with Jikes!\n");
		} else {
			console.append("Jikes ok.\n");
		}
		return jikesOk;
	}
}
