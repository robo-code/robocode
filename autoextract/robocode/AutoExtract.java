/*******************************************************************************
 * Copyright (c) 2001, 2006 Mathew Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Added NAME, FILENAME, and FOLDERNAME constants for installation
 *       customization
 *     - Replaced deprecated method calls
 *     - Code cleanup
 *******************************************************************************/
package robocode;


import java.io.*;
import java.util.jar.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


/**
 * @author Mathew A. Nelson (original version)
 * @author Flemming N. Larsen (current version)
 */
public class AutoExtract implements ActionListener {
	private static final String NAME = "Robocode";

	private static final String FILENAME = "robocode";
	private static final String FOLDER_NAME = "Robocode";

	private static final String OS_NAME = System.getProperty("os.name");
	private static final double OS_VERSION = doubleValue(System.getProperty("os.version"));

	private static final char SPINNER[] = { '-', '\\', '|', '/' };

	private JDialog licenseDialog;

	private boolean accepted;

	private String message = "";

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
		} catch (NumberFormatException e) {}

		return d;
	}

	private boolean acceptLicense(String licenseFile) {
		String licenseText = "";

		InputStream is = getClass().getClassLoader().getResourceAsStream(licenseFile);

		if (is == null) { // no license
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

	private boolean extract(String src, File dest) {
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

		InputStream is = getClass().getClassLoader().getResourceAsStream(src.toString());

		if (is == null) {
			String r = getClass().getClassLoader().getResource(src.toString()).toString();
			int i = r.lastIndexOf("!");

			if (i >= 0) {
				r = r.substring(0, i);
			}
			JOptionPane.showMessageDialog(null,
					r + "\nContains an exclamation point.  Please move the file to a different directory.");

			System.exit(0);
		}
		try {
			JarInputStream jarIS = new JarInputStream(is);

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

						if (entryName.length() > 3 && entryName.substring(entryName.length() - 3).equals(".sh")) {
							if (File.separatorChar == '/') {
								Runtime.getRuntime().exec("chmod 755 " + out.toString());
							}
						}

						status.setText(entryName + " " + SPINNER[spin++] + " (" + index + " bytes)");
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
		// Set native look and feel
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {}

		File installDir = null;
		File suggestedDir = null;

		AutoExtract extractor = new AutoExtract();

		if (extractor.acceptLicense("cpl-v10.html")) {
			if (argv.length == 1) {
				suggestedDir = new File(argv[0]);
			} else if (File.separatorChar == '\\') {
				suggestedDir = new File("C:\\" + FOLDER_NAME + '\\');
			} else {
				suggestedDir = new File(System.getProperty("user.home") + File.separator + FOLDER_NAME + File.separator);
			}

			boolean done = false;

			while (!done) {
				int rc = JOptionPane.showConfirmDialog(null,
						NAME + " will be installed in:\n" + suggestedDir + "\nIs this ok?", "Installing " + NAME,
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
					}
					suggestedDir = new File(((String) r).trim());
				} else if (rc == JOptionPane.CANCEL_OPTION) {
					JOptionPane.showMessageDialog(null, "Installation cancelled.");
					System.exit(0);
				}
			}
			if (!installDir.exists()) {
				int rc = JOptionPane.showConfirmDialog(null,
						installDir.getPath() + "\ndoes not exist.  Would you like to create it?", "Installing " + NAME,
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

				if (rc == JOptionPane.YES_OPTION) {
					installDir.mkdirs();
				} else {
					JOptionPane.showMessageDialog(null, "Installation cancelled.");
					System.exit(0);
				}
			}

			boolean rv = extractor.extract("extract.jar", installDir);

			if (rv) {
				extractor.createShortcuts(installDir);
			} else {
				JOptionPane.showMessageDialog(null, extractor.message);
			}

		} else {
			JOptionPane.showMessageDialog(null, "Installation cancelled.");
		}
		System.exit(0);
	}

	private void createShortcuts(File installDir) {
		if (OS_NAME.toLowerCase().indexOf("win") == 0) {
			if (createWindowsShortcuts(installDir)) {} else {
				JOptionPane.showMessageDialog(null,
						message + "\n" + "To start " + NAME + ", enter the following at a command prompt:\n" + "cd "
						+ installDir.getAbsolutePath() + "\n" + FILENAME + ".bat");
			}
		} else if (OS_NAME.toLowerCase().indexOf("mac") == 0) {
			if (OS_VERSION >= 10.1) {
				JOptionPane.showMessageDialog(null,
						message + "\n" + "To start " + NAME + ", browse to " + installDir + " then double-click " + FILENAME
						+ ".jar\n");
			} else {
				JOptionPane.showMessageDialog(null,
						message + "\n" + "To start " + NAME + ", enter the following at a command prompt:\n" + "cd "
						+ installDir.getAbsolutePath() + "\n" + "./" + FILENAME + ".sh");
			}
		} else {
			JOptionPane.showMessageDialog(null,
					message + "\n" + "To start " + NAME + ", enter the following at a command prompt:\n" + "cd "
					+ installDir.getAbsolutePath() + "\n" + "./" + FILENAME + ".sh");
		}
	}

	private boolean createWindowsShortcuts(File installDir) {
		int rc = JOptionPane.showConfirmDialog(null,
				"Would you like to install a shortcut to " + NAME + " in the Start menu? (Recommended)", "Create Shortcuts",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

		if (rc != JOptionPane.YES_OPTION) {
			return false;
		}

		String command = null;

		if (OS_NAME.indexOf("9") != -1) {
			command = "command.com /c cscript.exe "; // /nologo
		} else {
			command = "cmd.exe /c cscript.exe ";
		} // /nologo
		if (command != null) {
			try {
				File shortcutMaker = new File(installDir, "makeshortcut.js");
				PrintStream out = new PrintStream(new FileOutputStream(shortcutMaker));

				out.println("WScript.Echo(\"Creating shortcuts...\");");
				out.println("Shell = new ActiveXObject(\"WScript.Shell\");");
				out.println("ProgramsPath = Shell.SpecialFolders(\"Programs\");");
				out.println("fso = new ActiveXObject(\"Scripting.FileSystemObject\");");
				out.println("if (!fso.folderExists(ProgramsPath + \"\\\\" + NAME + "\"))");
				out.println("	fso.CreateFolder(ProgramsPath + \"\\\\" + NAME + "\");");
				out.println("link = Shell.CreateShortcut(ProgramsPath + \"\\\\" + NAME + "\\\\" + NAME + ".lnk\");");
				out.println("link.Arguments = \"\";");
				out.println("link.Description = \"" + NAME + "\";");
				out.println("link.HotKey = \"\";");
				out.println(
						"link.IconLocation = \"" + escaped(installDir.getAbsolutePath()) + "\\\\" + FILENAME + ".ico,0\";");
				out.println(
						"link.TargetPath = \"" + escaped(installDir.getAbsolutePath()) + "\\\\" + FILENAME + ".bat" + "\";");
				out.println("link.WindowStyle = 1;");
				out.println("link.WorkingDirectory = \"" + escaped(installDir.getAbsolutePath()) + "\";");
				out.println("link.Save();");
				out.println("DesktopPath = Shell.SpecialFolders(\"Desktop\");");
				out.println("link = Shell.CreateShortcut(DesktopPath + \"\\\\" + NAME + ".lnk\");");
				out.println("link.Arguments = \"\";");
				out.println("link.Description = \"" + NAME + "\";");
				out.println("link.HotKey = \"\";");
				out.println(
						"link.IconLocation = \"" + escaped(installDir.getAbsolutePath()) + "\\\\" + FILENAME + ".ico,0\";");
				out.println(
						"link.TargetPath = \"" + escaped(installDir.getAbsolutePath()) + "\\\\" + FILENAME + ".bat" + "\";");
				out.println("link.WindowStyle = 1;");
				out.println("link.WorkingDirectory = \"" + escaped(installDir.getAbsolutePath()) + "\";");
				out.println("link.Save();");
				out.println("WScript.Echo(\"Shortcuts created.\");");

				out.close();

				Process p = Runtime.getRuntime().exec(command + " makeshortcut.js", null, installDir);

				p.waitFor();

				int rv = p.exitValue();

				try {
					shortcutMaker.delete();
				} catch (Exception e) {}

				if (rv == 0) {
					JOptionPane.showMessageDialog(null,
							message + "\n" + "A " + FOLDER_NAME + " program group has been added to your Start menu\n" + "A "
							+ FOLDER_NAME + " icon has been added to your desktop.");
					return true;
				} else {
					return false;
				}
			} catch (Exception e) {
				return false;
			}
		} else {
			return false;
		}
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
