/*******************************************************************************
 * Copyright (c) 2001 Mathew Nelson
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew Nelson - initial API and implementation
 *******************************************************************************/
package robocode;

import java.io.*;
import java.util.jar.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Insert the type's description here.
 * Creation date: (8/3/2001 2:59:24 PM)
 * @author: Administrator
 */
public class AutoExtract implements ActionListener{
	public javax.swing.JDialog licenseDialog = null;
	public boolean accepted = false;
	public String spinner[] = {"-","\\","|","/"};
	public String message = "";
	public static String osName = System.getProperty("os.name");
	public static double osVersion = doubleValue(System.getProperty("os.version"));
	
/**
 * AutoExtract constructor comment.
 */
public AutoExtract() {
	super();
}

public static double doubleValue(String s) {
	int p = s.indexOf(".");
	if (p >= 0)
	p = s.indexOf(".",p+1);
	if (p >= 0)
	s = s.substring(0,p);
	
	double d = 0.0;
	try {
		d = Double.parseDouble(s);
	} catch (NumberFormatException e) {
	}
	return d;
}
/**
 * Insert the method's description here.
 * Creation date: (8/3/2001 3:04:13 PM)
 * @return boolean
 * @param src java.io.File
 * @param dest java.io.File
 */
public boolean acceptLicense(String licenseFile) {

	String licenseText = "";
	
	InputStream is = getClass().getClassLoader().getResourceAsStream(licenseFile);
	if (is == null) // no license
		return true;
		
	BufferedReader r = new BufferedReader(new InputStreamReader(is));
	try {
		String line = r.readLine();
		while (line != null)
		{
			licenseText += line;
			line = r.readLine();
		}
		return acceptReject(licenseText);
		
	} catch (IOException e) {
		System.err.println("Could not read line from license file: " + e);
	}
	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (8/6/2001 4:23:18 PM)
 * @return boolean
 */
public boolean acceptReject(String text) {
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	
	licenseDialog = new JDialog();
	licenseDialog.setTitle("License Agreement");
	licenseDialog.setModal(true);
	licenseDialog.setLocation((screenSize.width - 500)/2,(screenSize.height - 400)/2);
	licenseDialog.setSize(500,400);
	JTextPane t = new JTextPane();
	t.setContentType("text/html");
	t.setText(text);
	t.setFont(new Font("Dialog",Font.PLAIN,12));
	t.setEditable(false);

	JScrollPane s = new JScrollPane();
	s.setViewportView(t);

	licenseDialog.getContentPane().setLayout(new BorderLayout());
	licenseDialog.getContentPane().add(s,BorderLayout.CENTER);


	JPanel p = new JPanel();
	p.setLayout(new BorderLayout());
	JButton b1 = new JButton("Accept");
	JButton b2 = new JButton("Cancel");
	p.add(b1,BorderLayout.WEST);
	p.add(b2,BorderLayout.EAST);

	b1.addActionListener(this);
	b2.addActionListener(this);
	
	licenseDialog.getContentPane().add(p,BorderLayout.SOUTH);
	
	licenseDialog.show();
//	System.out.println("Dialog closed.");
	return accepted;
}
/**
 * Insert the method's description here.
 * Creation date: (8/6/2001 5:14:21 PM)
 * @param e java.awt.event.ActionEvent
 */
public void actionPerformed(ActionEvent e) {
	accepted = false;
	if (e.getActionCommand().equals("Accept"))
	{
		accepted = true;
//		System.out.println("Accepted.");
		licenseDialog.dispose();
		licenseDialog = null;
	}
	else if (e.getActionCommand().equals("Cancel"))
	{
//		System.out.println("Cancelled.");
		licenseDialog.dispose();
		licenseDialog = null;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (8/3/2001 3:04:13 PM)
 * @return boolean
 * @param src java.io.File
 * @param dest java.io.File
 */
public boolean extract(String src, File dest) {

	JDialog statusDialog = new JDialog();
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

	int height = 50;
	if (File.separatorChar == '/')
		height = 100;
	
	statusDialog.setTitle("Installing");
	statusDialog.setLocation((screenSize.width - 500)/2,(screenSize.height - height)/2);
	statusDialog.setSize(500,height);
	JLabel status = new JLabel();

	statusDialog.getContentPane().setLayout(new BorderLayout());
	statusDialog.getContentPane().add(status,BorderLayout.CENTER);

	statusDialog.show();
	
	FileOutputStream fos = null;
	String entryName = "";

	byte buf[] = new byte[2048];

	InputStream is = getClass().getClassLoader().getResourceAsStream(src.toString());
	if (is == null)
	{
		String r = getClass().getClassLoader().getResource(src.toString()).toString();
		int i = r.lastIndexOf("!");
		if (i >= 0)
			r = r.substring(0,i);
		JOptionPane.showMessageDialog(null,r + "\nContains an exclamation point.  Please move the file to a different directory.");
		System.exit(0);
	}
	try {
		JarInputStream jarIS = new JarInputStream(is);

		JarEntry entry = jarIS.getNextJarEntry();
		while (entry != null)
		{
			int spin = 0;
			entryName = entry.getName();
			if (entry == null) {
				System.err.println("Could not find entry: " + entry);
			}
			else
			{
				if (entry.isDirectory())
				{
					File dir = new File(dest,entry.getName());
					dir.mkdirs();
				}
				else
				{
					status.setText(entryName + " " + spinner[spin++]);
					//System.out.print("Extracting file " + entry.getName() + ".");
					File out = new File(dest,entry.getName());
					File parentDirectory = new File(out.getParent());
					parentDirectory.mkdirs();
					fos = new FileOutputStream(out);
					
					int index = 0;
					int num = 0;
					int count = 0;
					while((num = jarIS.read(buf,0,2048)) != -1) {
						fos.write(buf,0,num);
						index+= num;
						count++;
						if (count > 80)
						{
							status.setText(entryName + " " + spinner[spin++] + " (" + index + " bytes)");
							if (spin > 3)
								spin = 0;
							count = 0;
//							System.out.println("");
						}
//						System.out.print(".");
						
					}
					fos.close();

					if (entryName.length() > 3 && entryName.substring(entryName.length() - 3).equals(".sh"))
					{
						if (File.separatorChar == '/')
						Runtime.getRuntime().exec("chmod 755 " + out.toString());
					}
					
					status.setText(entryName + " " + spinner[spin++] + " (" + index + " bytes)");
//					System.out.println(".  Extracted " + index +  " bytes"); 
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
	public static void main(String argv[])
	{
		// Set native look and feel
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		}
		
		File installDir = null;
		File suggestedDir = null;;
		AutoExtract extractor = new AutoExtract();
		if (extractor.acceptLicense("cpl-v10.html"))
		{
			if (argv.length == 1)
				suggestedDir = new File(argv[0]);
			else if (File.separatorChar == '\\')
			{
				suggestedDir = new File("c:\\robocode\\");
			}
			else
			{
				suggestedDir = new File(System.getProperty("user.home") + File.separator + "robocode" + File.separator);
			}

			boolean done = false;
			while (!done)
			{
				int rc = JOptionPane.showConfirmDialog(null,"Robocode will be installed in:\n" + suggestedDir + "\nIs this ok?",
					"Installing Robocode",JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE);
				if (rc == JOptionPane.YES_OPTION)
				{
					installDir = suggestedDir;
					done = true;
				}
				else if (rc == JOptionPane.NO_OPTION)
				{
					Object r = JOptionPane.showInputDialog(null,"Please type in the installation directory","Installation Directory",JOptionPane.PLAIN_MESSAGE,null,null,suggestedDir);
					if (r == null)
					{
						JOptionPane.showMessageDialog(null,"Installation cancelled.");
						System.exit(0);
					}
					suggestedDir = new File(((String)r).trim());
				}
				else if (rc == JOptionPane.CANCEL_OPTION)
				{
					JOptionPane.showMessageDialog(null,"Installation cancelled.");
					System.exit(0);
				}
			}
			if (!installDir.exists())
			{
				int rc = JOptionPane.showConfirmDialog(null,installDir.getPath() + "\ndoes not exist.  Would you like to create it?",
					"Installing Robocode",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
				if (rc == JOptionPane.YES_OPTION)
				{
					installDir.mkdirs();
				}
				else
				{
					JOptionPane.showMessageDialog(null,"Installation cancelled.");
					System.exit(0);
				}
			}
			boolean rv = extractor.extract("extract.jar",installDir);
			if (rv)
			{
				extractor.createShortcuts(installDir,"robocode.bat","Robocode","Robocode");
			}
			else
				JOptionPane.showMessageDialog(null,extractor.message);

		}
		else
			JOptionPane.showMessageDialog(null,"Installation cancelled.");
		System.exit(0);
	}
	
	public void createShortcuts(File installDir, String runnable, String folder, String name)
	{
		
		if (osName.toLowerCase().indexOf("win") == 0)
		{
			if (createWindowsShortcuts(installDir,runnable,folder,name))
			{
			}
			else
			{
				JOptionPane.showMessageDialog(null,message + "\n" +
				 "To start Robocode, enter the following at a command prompt:\n" +
				 "cd " + installDir.getAbsolutePath() + "\n" +
		 		"robocode.bat");
			}
		}
		else if (osName.toLowerCase().indexOf("mac") == 0)
		{
			if (osVersion >= 10.1)
			{
				JOptionPane.showMessageDialog(null,message + "\n" +
				 "To start Robocode, browse to " + installDir + " then double-click robocode.jar\n");
			}
			else
			{
				JOptionPane.showMessageDialog(null,message + "\n" +
				 "To start Robocode, enter the following at a command prompt:\n" +
				 "cd " + installDir.getAbsolutePath() + "\n" +
				 "./robocode.sh");
			}
		}
		else
		{
			JOptionPane.showMessageDialog(null,message + "\n" +
			 "To start Robocode, enter the following at a command prompt:\n" +
			 "cd " + installDir.getAbsolutePath() + "\n" +
			 "./robocode.sh");
		}
	}
	
	public boolean createWindowsShortcuts(File installDir, String runnable, String folder, String name)
	{
		int rc = JOptionPane.showConfirmDialog(null,"Would you like to install a shortcut to Robocode in the Start menu? (Recommended)",
			"Create Shortcuts",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
		if (rc != JOptionPane.YES_OPTION)
			return false;

		String command = null;
		if (osName.indexOf("9") != -1)
		{
			command = "command.com /c cscript.exe "; // /nologo
		}
		else
			command = "cmd.exe /c cscript.exe "; // /nologo
		if (command != null)
		{
			try {
				File shortcutMaker = new File(installDir,"makeshortcut.js");
				PrintStream out = new PrintStream(new FileOutputStream(shortcutMaker));

out.println("WScript.Echo(\"Creating shortcuts...\");");
out.println("Shell = new ActiveXObject(\"WScript.Shell\");");
out.println("ProgramsPath = Shell.SpecialFolders(\"Programs\");");
out.println("fso = new ActiveXObject(\"Scripting.FileSystemObject\");");
out.println("if (!fso.folderExists(ProgramsPath + \"\\\\" + folder + "\"))");
out.println("	fso.CreateFolder(ProgramsPath + \"\\\\" + folder + "\");");
out.println("link = Shell.CreateShortcut(ProgramsPath + \"\\\\" + folder + "\\\\" + name + ".lnk\");");
out.println("link.Arguments = \"\";");
out.println("link.Description = \"" + name + "\";");
out.println("link.HotKey = \"\";");
out.println("link.IconLocation = \"" + escaped(installDir.getAbsolutePath()) + "\\\\" + "robocode.ico,0\";");
out.println("link.TargetPath = \"" + escaped(installDir.getAbsolutePath()) + "\\\\" + runnable + "\";");
out.println("link.WindowStyle = 1;");
out.println("link.WorkingDirectory = \"" + escaped(installDir.getAbsolutePath()) + "\";");
out.println("link.Save();");
out.println("DesktopPath = Shell.SpecialFolders(\"Desktop\");");
out.println("link = Shell.CreateShortcut(DesktopPath + \"\\\\" + name + ".lnk\");");
out.println("link.Arguments = \"\";");
out.println("link.Description = \"" + name + "\";");
out.println("link.HotKey = \"\";");
out.println("link.IconLocation = \"" + escaped(installDir.getAbsolutePath()) + "\\\\" + "robocode.ico,0\";");
out.println("link.TargetPath = \"" + escaped(installDir.getAbsolutePath()) + "\\\\" + runnable + "\";");
out.println("link.WindowStyle = 1;");
out.println("link.WorkingDirectory = \"" + escaped(installDir.getAbsolutePath()) + "\";");
out.println("link.Save();");
out.println("WScript.Echo(\"Shortcuts created.\");");

				out.close();
				Process p = Runtime.getRuntime().exec(command + " makeshortcut.js",null,installDir);
				p.waitFor();
				int rv = p.exitValue();
				try {
					shortcutMaker.delete();
				} catch (Exception e) {
				}
				if (rv == 0)
				{
					JOptionPane.showMessageDialog(null,message + "\n" +
					 "A Robocode program group has been added to your Start menu\n" +
					 "A Robocode icon has been added to your desktop.");
					return true;
				}
				else
					return false;
			} catch (Exception e) {
				return false;
			}
		}
		else
			return false;
	}
	public String escaped(String s) {
		String r = "";
		for (int i = 0; i < s.length(); i++)
		{
			if (s.charAt(i) == '\\')
				r += '\\';
			r += s.charAt(i);
		}
		return r;
	}
}
