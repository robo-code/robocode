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
 *     - Replaced deprecated methods
 *     - Added check for the Java version that the user has installed. If the
 *       Java version is not 5.0, an error dialog will be display and the
 *       installation will terminate
 *     - Changed the information message for how to run robocode.sh, where the
 *       user does not have to change the directory before calling robocode.sh
 *     - Code cleanup
 *******************************************************************************/
package net.sf.robocode.installer;


import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URL;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;


/**
 * Installer for Robocode.
 *
 * @author Mathew A. Nelsen (original)
 * @author Flemming N. Larsen (contributor)
 */
public class AutoExtract implements ActionListener {
	private JDialog licenseDialog;
	private boolean accepted;
	private final String[] spinner = { "-", "\\", "|", "/"};
	private String message = "";
	private static final String osName = System.getProperty("os.name");
	private static final double osVersion = doubleValue(System.getProperty("os.version"));
	private static final String javaVersion = System.getProperty("java.version");

	/**
	 * AutoExtract constructor.
	 */
	public AutoExtract() {
		super();
	}

	private static double doubleValue(String s) {
		int p = s.indexOf(".");

		if (p >= 0) {
			p = s.indexOf(".", p + 1);
		}
		if (p >= 0) {
			s = s.substring(0, p);
		}

		double d = 0.0;

		try {
			d = Double.parseDouble(s);
		} catch (NumberFormatException e) {
			e.printStackTrace(System.err);
		}

		return d;
	}

	private boolean acceptLicense() {
		String licenseText = "";

		InputStream is;

		try {
			JarFile extractJar = new JarFile("extract.jar");

			is = extractJar.getInputStream(extractJar.getJarEntry("license/cpl-v10.html"));
		} catch (IOException e) {
			return true;
		}
		if (is == null) {
			return true;
		}

		BufferedReader r = new BufferedReader(new InputStreamReader(is));

		try {
			String line = r.readLine();

			while (line != null) {
				licenseText += line;
				line = r.readLine();
			}
			return acceptReject(licenseText);

		} catch (IOException e) {
			System.err.println("Could not read line from license file: " + e);
		}
		return true;
	}

	private boolean acceptReject(String text) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		licenseDialog = new JDialog();
		licenseDialog.setTitle("License Agreement");
		licenseDialog.setModal(true);
		licenseDialog.setLocation((screenSize.width - 500) / 2, (screenSize.height - 400) / 2);
		licenseDialog.setSize(500, 400);
		JTextPane t = new JTextPane();

		t.setContentType("text/html");
		t.setText(text);
		t.setFont(new Font("Dialog", Font.PLAIN, 12));
		t.setEditable(false);

		JScrollPane s = new JScrollPane();

		s.setViewportView(t);

		licenseDialog.getContentPane().setLayout(new BorderLayout());
		licenseDialog.getContentPane().add(s, BorderLayout.CENTER);

		JPanel p = new JPanel();

		p.setLayout(new BorderLayout());
		JButton b1 = new JButton("Accept");
		JButton b2 = new JButton("Cancel");

		p.add(b1, BorderLayout.WEST);
		p.add(b2, BorderLayout.EAST);

		b1.addActionListener(this);
		b2.addActionListener(this);

		licenseDialog.getContentPane().add(p, BorderLayout.SOUTH);

		licenseDialog.setVisible(true);

		return accepted;
	}

	public void actionPerformed(ActionEvent e) {
		accepted = e.getActionCommand().equals("Accept");
		licenseDialog.dispose();
		licenseDialog = null;
	}

	private boolean extract(File dest) {
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

		FileOutputStream fos;
		String entryName;

		byte buf[] = new byte[2048];

		final String name = AutoExtract.class.getName().replaceAll("\\.", "/") + ".class";
		String urlJar = AutoExtract.class.getClassLoader().getResource(name).toString();
		final String src = urlJar.substring("jar:file:/".length(), urlJar.indexOf("!/"));

		if (src.indexOf('!') > -1) {
			final String message = src
					+ "\nContains an exclamation point.  Please move the file to a different directory.";

			JOptionPane.showMessageDialog(null, message);
			System.err.println(message);
			System.exit(0);
		}
		try {
			final URL url = new URL("file:/" + src);
			InputStream is = url.openStream();
			JarInputStream jarIS = new JarInputStream(is);

			JarEntry entry = jarIS.getNextJarEntry();

			while (entry != null) {
				int spin = 0;

				entryName = entry.getName();
				if (entry.isDirectory()) {
					if (!entryName.startsWith("net")) {
						File dir = new File(dest, entry.getName());

						if (!dir.exists() && !dir.mkdirs()) {
							System.err.println("Can't create dir: " + dir);
						}
					}
				} else {
					if (!entryName.equals(name)) {
						status.setText(entryName + " " + spinner[spin++]);

						File out = new File(dest, entry.getName());
						File parentDirectory = new File(out.getParent());

						if (!parentDirectory.exists() && !parentDirectory.mkdirs()) {
							System.err.println("Can't create dir: " + parentDirectory);
						}
						fos = new FileOutputStream(out);

						int index = 0;
						int num;
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
							}
						}
						fos.close();

						if (entryName.length() > 3 && entryName.substring(entryName.length() - 3).equals(".sh")) {
							if (File.separatorChar == '/') {
								Runtime.getRuntime().exec("chmod 755 " + out.toString());
							}
						}

						status.setText(entryName + " " + spinner[spin] + " (" + index + " bytes)");
					}
				}
				entry = jarIS.getNextJarEntry();
			}
			statusDialog.dispose();
			message = "Installation successful";
			return true;
		} catch (IOException e) {
			message = "Installation failed" + e;
			return false;
		}
	}

	public static void main(String argv[]) {
		// Verify that the Java version is version 5 (1.5.0) or newer
		if (javaVersion.startsWith("1.") && javaVersion.charAt(2) < '5') {
			final String message = "Robocode requires Java 5.0 (1.5.0) or newer.\n"
					+ "Your system is currently running Java " + javaVersion + ".\n"
					+ "If you have not installed (or activated) at least\n" + "JRE 5.0 or JDK 5.0, please do so.";

			JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
			System.err.println(message);
			System.exit(0);
		}

		// Set native look and feel
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Throwable t) {// For some reason Ubuntu 7 can cause a NullPointerException when trying to getting the LAF
		}

		File installDir = null;
		File suggestedDir;

		AutoExtract extractor = new AutoExtract();

		if (extractor.acceptLicense()) {
			if (argv.length == 1) {
				suggestedDir = new File(argv[0]);
			} else if (File.separatorChar == '\\') {
				suggestedDir = new File("c:\\robocode\\");
			} else {
				suggestedDir = new File(System.getProperty("user.home") + File.separator + "robocode" + File.separator);
			}

			boolean done = false;

			while (!done) {
				int rc = JOptionPane.showConfirmDialog(null,
						"Robocode will be installed in:\n" + suggestedDir + "\nIs this ok?", "Installing Robocode",
						JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

				if (rc == JOptionPane.YES_OPTION) {
					installDir = suggestedDir;
					done = true;
				} else if (rc == JOptionPane.NO_OPTION) {
					Object r = JOptionPane.showInputDialog(null, "Please type in the installation directory",
							"Installation Directory", JOptionPane.PLAIN_MESSAGE, null, null, suggestedDir);

					if (r == null) {
						JOptionPane.showMessageDialog(null, "Installation cancelled.");
						System.exit(0);
					} else {
						suggestedDir = new File(((String) r).trim());
					}
				} else if (rc == JOptionPane.CANCEL_OPTION) {
					JOptionPane.showMessageDialog(null, "Installation cancelled.");
					System.exit(0);
				}
			}
			if (!installDir.exists()) {
				int rc = JOptionPane.showConfirmDialog(null,
						installDir.getPath() + "\ndoes not exist.  Would you like to create it?", "Installing Robocode",
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

				if (rc == JOptionPane.YES_OPTION) {
					if (!installDir.exists() && !installDir.mkdirs()) {
						System.err.println("Can't create dir: " + installDir);
					}
				} else {
					JOptionPane.showMessageDialog(null, "Installation cancelled.");
					System.exit(0);
				}
			}
			deleteOldLibs(installDir);
			
			deleteDir(new File(installDir, "robots/.robotcache"));

			boolean rv = extractor.extract(installDir);

			if (rv) {
				extractor.createShortcuts(installDir, "robocode.bat", "Robocode", "Robocode");
			} else {
				JOptionPane.showMessageDialog(null, extractor.message);
			}
		} else {
			JOptionPane.showMessageDialog(null, "Installation cancelled.");
		}
		System.exit(0);
	}

	private static void deleteOldLibs(File installDir) {
		File libs = new File(installDir, "libs");

		if (libs.exists()) {
			final File[] del = libs.listFiles(new FilenameFilter() {

				public boolean accept(File dir, String name) {
					String test = name.toLowerCase();

					return test.endsWith(".jar");
				}
			});

			for (File d : del) {
				if (!d.delete()) {
					System.err.println("Can't delete: " + d);
				}
			}
		}
	}

	public static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			for (File file : dir.listFiles()) {
				if (file.isDirectory()) {
					// Skip directories ending with ".data" 
					if (file.getName().endsWith(".data")) {
						continue;
					}
					try {
						// Test for symlink and ignore.
						// Robocode won't create one, but just in case a user does...
						if (file.getCanonicalFile().getParentFile().equals(dir.getCanonicalFile())) {
							deleteDir(file);
							if (file.exists() && !file.delete()) {
								System.err.println("Can't delete: " + file);
							}
						} else {
							System.out.println("Warning: " + file + " may be a symlink. It has been ignored");
						}
					} catch (IOException e) {
						System.out.println(
								"Warning: Cannot determine canonical file for " + file + ". It has been ignored");
					}
				} else {
					if (file.exists() && !file.delete()) {
						System.err.println("Can't delete: " + file);
					}
				}
			}
			return dir.delete();
		}
		return false;
	}

	private void createShortcuts(File installDir, String runnable, String folder, String name) {

		if (osName.toLowerCase().indexOf("win") == 0) {
			if (createWindowsShortcuts(installDir, runnable, folder, name)) {} else {
				JOptionPane.showMessageDialog(null,
						message + "\n" + "To start Robocode, enter the following at a command prompt:\n" + "cd "
						+ installDir.getAbsolutePath() + "\n" + "robocode.bat");
			}
		} else if (osName.toLowerCase().indexOf("mac") == 0) {
			if (osVersion >= 10.1) {
				JOptionPane.showMessageDialog(null,
						message + "\n" + "To start Robocode, browse to " + installDir + " then double-click robocode.sh\n");
			} else {
				JOptionPane.showMessageDialog(null,
						message + "\n" + "To start Robocode, enter the following at a command prompt:\n"
						+ installDir.getAbsolutePath() + "/robocode.sh");
			}
		} else {
			JOptionPane.showMessageDialog(null,
					message + "\n" + "To start Robocode, enter the following at a command prompt:\n"
					+ installDir.getAbsolutePath() + "/robocode.sh");
		}
	}

	private boolean createWindowsShortcuts(File installDir, String runnable, String folder, String name) {
		int rc = JOptionPane.showConfirmDialog(null,
				"Would you like to install a shortcut to Robocode in the Start menu? (Recommended)", "Create Shortcuts",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

		if (rc != JOptionPane.YES_OPTION) {
			return false;
		}

		String command;

		if (osName.indexOf("9") != -1) {
			command = "command.com /c cscript.exe "; // /nologo
		} else {
			command = "cmd.exe /c cscript.exe ";
		} // /nologo
		try {
			File shortcutMaker = new File(installDir, "makeshortcut.vbs");
			PrintStream out = new PrintStream(new FileOutputStream(shortcutMaker));

			out.println("WScript.Echo(\"Creating shortcuts...\")");
			out.println("Set Shell = CreateObject (\"WScript.Shell\")");
			out.println("Set fso = CreateObject(\"Scripting.FileSystemObject\")");
			out.println("ProgramsPath = Shell.SpecialFolders(\"Programs\")");
			out.println("if (not(fso.folderExists(ProgramsPath + \"\\\\" + folder + "\"))) Then");
			out.println("	fso.CreateFolder(ProgramsPath + \"\\\\" + folder + "\")");
			out.println("End If");
			out.println("Set link = Shell.CreateShortcut(ProgramsPath + \"\\\\" + folder + "\\\\" + name + ".lnk\")");
			out.println("link.Arguments = \"\"");
			out.println("link.Description = \"" + name + "\"");
			out.println("link.HotKey = \"\"");
			out.println("link.IconLocation = \"" + escaped(installDir.getAbsolutePath()) + "\\\\" + "robocode.ico,0\"");
			out.println("link.TargetPath = \"" + escaped(installDir.getAbsolutePath()) + "\\\\" + runnable + "\"");
			out.println("link.WindowStyle = 1");
			out.println("link.WorkingDirectory = \"" + escaped(installDir.getAbsolutePath()) + "\"");
			out.println("link.Save()");
			out.println("DesktopPath = Shell.SpecialFolders(\"Desktop\")");
			out.println("Set link = Shell.CreateShortcut(DesktopPath + \"\\\\" + name + ".lnk\")");
			out.println("link.Arguments = \"\"");
			out.println("link.Description = \"" + name + "\"");
			out.println("link.HotKey = \"\"");
			out.println("link.IconLocation = \"" + escaped(installDir.getAbsolutePath()) + "\\\\" + "robocode.ico,0\"");
			out.println("link.TargetPath = \"" + escaped(installDir.getAbsolutePath()) + "\\\\" + runnable + "\"");
			out.println("link.WindowStyle = 1");
			out.println("link.WorkingDirectory = \"" + escaped(installDir.getAbsolutePath()) + "\"");
			out.println("link.Save()");
			out.println("WScript.Echo(\"Shortcuts created.\")");

			out.close();

			Process p = Runtime.getRuntime().exec(command + " makeshortcut.vbs", null, installDir);
			int rv = p.waitFor();

			if (rv != 0) {
				System.err.println("Can't create shortcut: " + shortcutMaker);
				return false;
			}
			JOptionPane.showMessageDialog(null,
					message + "\n" + "A Robocode program group has been added to your Start menu\n"
					+ "A Robocode icon has been added to your desktop.");
			if (!shortcutMaker.delete()) {
				System.err.println("Can't delete: " + shortcutMaker);
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace(System.err);
		} catch (InterruptedException e) {
			e.printStackTrace(System.err);
		}

		return false;
	}

	private String escaped(String s) {
		String r = "";

		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) == '\\') {
				r += '\\';
			}
			r += s.charAt(i);
		}
		return r;
	}
}
