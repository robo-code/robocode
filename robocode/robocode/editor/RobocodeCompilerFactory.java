/*******************************************************************************
 * Copyright (c) 2001, 2006 Mathew Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew Nelson - initial API and implementation
 *******************************************************************************/
package robocode.editor;


import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.util.jar.*;
import robocode.util.*;
import robocode.dialog.*;


/**
 * Insert the type's description here.
 * Creation date: (11/5/2001 6:40:39 PM)
 * @author: Administrator
 */
public class RobocodeCompilerFactory {
	private static CompilerProperties compilerProperties;
	private static String robotPath = null;
	private RobocodeEditor editor = null;
	private static boolean compilerInstalling = false;
	
	public static final String spinner[] = { "-", "\\", "|", "/"};

	/**
	 * RobocodeCompiler constructor comment.
	 */
	public RobocodeCompilerFactory(RobocodeEditor editor) {
		super();
		this.editor = editor;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (11/5/2001 6:47:53 PM)
	 * @return robocode.editor.RobocodeCompiler
	 */
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

	/**
	 * Insert the method's description here.
	 * Creation date: (8/3/2001 3:04:13 PM)
	 * @return boolean
	 * @param src java.io.File
	 * @param dest java.io.File
	 */
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

		statusDialog.show();
	
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
					} else // if (entry.getName().length() < 8 || !entry.getName().substring(0,8).equals("META-INF"))
					{
						status.setText(entryName + " " + spinner[spin++]);
						// System.out.print("Extracting file " + entry.getName() + ".");
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
								status.setText(entryName + " " + spinner[spin++] + " (" + index + " bytes)");
								if (spin > 3) {
									spin = 0;
								}
								count = 0;
								// System.out.println("");
							}
							// System.out.print(".");
						
						}
						fos.close();

						status.setText(entryName + " " + spinner[spin++] + " (" + index + " bytes)");
						// System.out.println(".  Extracted " + index +  " bytes"); 
					}
				}
				entry = jarIS.getNextJarEntry();
			}
			statusDialog.dispose();
			return true;
		} catch (IOException e) {
			statusDialog.dispose();
			Utils.error(null, e.toString());
			return false;
		}
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/18/2001 3:43:41 PM)
	 */
	public static CompilerProperties getCompilerProperties() {
		if (compilerProperties == null) {
			compilerProperties = new CompilerProperties();
			try {
				FileInputStream in = new FileInputStream(new File(Constants.cwd(), "compiler.properties"));

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

	/**
	 * Insert the method's description here.
	 * Creation date: (9/24/2001 12:31:19 PM)
	 * @return java.lang.String
	 */
	public static java.lang.String getRobotPath() {
		if (robotPath == null) {
			robotPath = System.getProperty("ROBOTPATH");
			if (robotPath == null) {
				robotPath = "robots";
			}
		}
		return robotPath;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (11/5/2001 7:03:51 PM)
	 */
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
		String compilerClassPath = "";
	
		String osName = System.getProperty("os.name");
		String osVersion = System.getProperty("os.version");

		// log("Detected OS: " + osName);
		// log("Detected OS Version: " + osVersion);

		// log(System.getProperty("java.home"));
		// log(System.getProperty("java.class.path"));
		// log(System.getProperty("java.ext.dirs"));


	
		ConsoleDialog console = new ConsoleDialog(editor, "Setting up compiler", false);

		console.setSize(500, 400);
		console.getOkButton().setEnabled(false);
		console.setText("Please wait while Robocode sets up a compiler for you...\n\n");
		Utils.centerShow(editor, console);

		console.append("Setting up compiler for " + osName + "\n");
		console.append("Java home is " + System.getProperty("java.home") + "\n\n");
		boolean javacOk = testJavac(console);
		boolean jikesOk = false;
		boolean rv = true;
		String javalib = "";
		boolean mustBuildJikes = false;
		String jikesJar = "";
		String jikesBinary = "";
		boolean noExtract = false;

		compilerProperties.setRobocodeVersion(editor.getManager().getVersionManager().getVersion());

		if (osName.indexOf("Windows") == 0) {
			javalib = System.getProperty("java.home") + File.separator + "lib" + File.separator + "rt.jar";
			if (javalib.indexOf(" ") > 0) {
				javalib = "\"" + javalib + "\"";
			}
		
			jikesJar = "compilers\\jikes-1.16.win.jar";
			jikesBinary = ".\\jikes-1.16\\bin\\jikes.exe";
			mustBuildJikes = false;
		} else if (osName.indexOf("Linux") == 0) {
			javalib = System.getProperty("java.home") + File.separator + "lib" + File.separator + "rt.jar";
			if (javalib.indexOf(" ") > 0) {
				javalib = "\"" + javalib + "\"";
			}
			jikesJar = "compilers/jikes-1.16.src.jar";
			mustBuildJikes = true;
			jikesBinary = "./jikes-1.16/bin/jikes";
		} else if (osName.indexOf("Mac") == 0) {
			javalib = new File(System.getProperty("java.home")).getParentFile().getPath() + File.separator + "Classes"
					+ File.separator + "classes.jar";
			if (javalib.indexOf(" ") > 0) {
				javalib = "\"" + javalib + "\"";
			}
			jikesJar = "compilers/jikes-1.16.src.jar";
		
			jikesBinary = "/usr/bin/jikes";
			jikesOk = testJikes(console, jikesBinary + " -classpath " + javalib);
			if (!jikesOk) {
				mustBuildJikes = true;
				jikesBinary = "./jikes-1.16/bin/jikes";
			}
		} else {
			javalib = System.getProperty("java.home") + File.separator + "lib" + File.separator + "rt.jar";
			if (javalib.indexOf(" ") > 0) {
				javalib = "\"" + javalib + "\"";
			}
			jikesJar = "compilers/jikes-1.16.src.jar";
			mustBuildJikes = true;
			jikesBinary = "./jikes-1.16/bin/jikes";
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
			compilerClassPath = "-classpath " + javalib + File.pathSeparator + "robocode.jar" + File.pathSeparator
					+ getRobotPath();
			getCompilerProperties().setCompilerBinary(compilerBinary);
			getCompilerProperties().setCompilerOptions(compilerOptions);
			getCompilerProperties().setCompilerClasspath(compilerClassPath);
			saveCompilerProperties();
			console.append("\nCongratulations!  Compiler set up successfully.\n");
			console.append("Click OK to continue.\n");
			console.scrollToBottom();
			rv = true;
		} else if (jikesOk) {
			compilerBinary = jikesBinary;
			compilerOptions = "-deprecation -g -Xstdout +T4";
			compilerClassPath = "-classpath " + javalib + File.pathSeparator + "robocode.jar" + File.pathSeparator
					+ getRobotPath();
			getCompilerProperties().setCompilerBinary(compilerBinary);
			getCompilerProperties().setCompilerOptions(compilerOptions);
			getCompilerProperties().setCompilerClasspath(
					"-classpath " + javalib + File.pathSeparator + "robocode.jar" + File.pathSeparator + getRobotPath());
			saveCompilerProperties();
			console.append("\nCongratulations!  Jikes set up successfully.\n");
			console.append("Click OK to continue.\n");
			console.scrollToBottom();
			rv = true;
		}
		if (!javacOk && !jikesOk) {
			console.append("\nExtracting Jikes...\n");
			if (noExtract || extract(new File(Constants.cwd(), jikesJar), new File("."))) {
				if (!noExtract) {
					console.append("Jikes extracted successfully.\n");
				}
				if (mustBuildJikes) {
					if (JOptionPane.showConfirmDialog(editor,
							"Robocode is now going to build Jikes for you.\nThis will take a while... got get a cup of coffee!\n",
							"Java time", JOptionPane.OK_CANCEL_OPTION)
							== JOptionPane.OK_OPTION) {
						if (makeJikes(editor, console, jikesBinary + " -classpath " + javalib)) {
							compilerBinary = jikesBinary;
							compilerOptions = "-deprecation -g -Xstdout +T4";
							compilerClassPath = "-classpath " + javalib + File.pathSeparator + "robocode.jar"
									+ File.pathSeparator + getRobotPath();
							getCompilerProperties().setCompilerBinary(compilerBinary);
							getCompilerProperties().setCompilerOptions(compilerOptions);
							getCompilerProperties().setCompilerClasspath(
									"-classpath " + javalib + File.pathSeparator + "robocode.jar" + File.pathSeparator
									+ getRobotPath());
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
							compilerClassPath = "-classpath " + javalib + File.pathSeparator + "robocode.jar"
									+ File.pathSeparator + getRobotPath();
							getCompilerProperties().setCompilerOptions(compilerOptions);
							getCompilerProperties().setCompilerClasspath(compilerClassPath);
							saveCompilerProperties();
							rv = false;
						}
					}
				} else {
					compilerBinary = jikesBinary;
					compilerOptions = "-deprecation -g -Xstdout +T4";
					compilerClassPath = "-classpath " + javalib + File.pathSeparator + "robocode.jar"
							+ File.pathSeparator + getRobotPath();
					getCompilerProperties().setCompilerBinary(compilerBinary);
					getCompilerProperties().setCompilerOptions(compilerOptions);
					getCompilerProperties().setCompilerClasspath(compilerClassPath);
					if (testJikes(console, jikesBinary + " -classpath " + javalib)) {
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
				compilerClassPath = "-classpath " + javalib + File.pathSeparator + "robocode.jar" + File.pathSeparator
						+ getRobotPath();
				getCompilerProperties().setCompilerOptions(compilerOptions);
				getCompilerProperties().setCompilerClasspath(compilerClassPath);
				saveCompilerProperties();
				rv = false;
			}
		}
	
		compilerInstalling = false;
		console.getOkButton().setEnabled(true);
		return rv;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/22/2001 1:41:21 PM)
	 * @param e java.lang.Exception
	 */
	public static void log(String s) {
		Utils.log(s);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/22/2001 1:41:21 PM)
	 * @param e java.lang.Exception
	 */
	public static void log(Throwable e) {
		Utils.log(e);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (11/5/2001 4:32:38 PM)
	 * @return boolean
	 */
	public static boolean makeJikes(RobocodeEditor editor, ConsoleDialog console, String jikesBinary) {
	
		String result = "";

		boolean rv = true;
	
		console.append("\nRobocode building Jikes...\n");
	
		try {
			String command = "./compilers/buildJikes.sh";

			log(command);
			Process p = Runtime.getRuntime().exec(command, null, Constants.cwd());

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

	/**
	 * Insert the method's description here.
	 * Creation date: (1/18/2001 3:21:22 PM)
	 */
	public static void saveCompilerProperties() {
		if (compilerProperties == null) {
			log("Cannot save null compiler properties");
			return;
		}
		try {
			FileOutputStream out = new FileOutputStream(new File(Constants.cwd(), "compiler.properties"));

			compilerProperties.store(out, "Robocode Compiler Properties");
		} catch (IOException e) {
			log(e);
		}
	
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (11/7/2001 6:02:02 PM)
	 * @param console robocode.dialog.ConsoleDialog
	 */
	public static boolean testJavac(ConsoleDialog console) {
		console.append("Testing compile with javac...\n");
		boolean javacOk = false;

		try {
			Process p = Runtime.getRuntime().exec("javac compilers" + File.separator + "CompilerTest.java", null,
					Constants.cwd());

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

	/**
	 * Insert the method's description here.
	 * Creation date: (11/7/2001 6:02:02 PM)
	 * @param console robocode.dialog.ConsoleDialog
	 */
	public static boolean testJikes(ConsoleDialog console, String jikesBinary) {
		console.append("\nTesting compile with Jikes...\n");
		boolean jikesOk = false;

		try {
			Process p = Runtime.getRuntime().exec(jikesBinary + " compilers" + File.separator + "CompilerTest.java",
					null, Constants.cwd());

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
