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
package robocode.editor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import robocode.manager.*;
import robocode.util.*;

/**
 * Insert the type's description here.
 * Creation date: (4/3/2001 2:04:07 PM)
 * @author: Mathew A. Nelson
 */
public class RobocodeEditor extends JFrame implements Runnable {
	private JPanel robocodeEditorContentPane = null;

	private RobocodeEditorMenuBar robocodeEditorMenuBar = null;
	private JDesktopPane desktopPane = null;
	public boolean isApplication = false;

	public java.awt.Point origin = new Point();
	public File robotsDirectory = null;
	private JToolBar statusBar = null;
	private JLabel lineLabel = null;

	private RobocodeProperties robocodeProperties = null;
	private File editorDirectory = null;
	private RobocodeManager manager = null;
	
	EventHandler eventHandler = new EventHandler();
class EventHandler implements ComponentListener {
	public void componentMoved(ComponentEvent e) {
	}
	public void componentHidden(ComponentEvent e) {
	}
	public void componentShown(ComponentEvent e) {
		new Thread(RobocodeEditor.this).start();
	}
	public void componentResized(ComponentEvent e) {
	}
}
/**
 * RoboCodeEditor constructor
 */
public RobocodeEditor(RobocodeManager manager) {
	super();
	this.manager = manager;
	if (manager != null)
		robotsDirectory = manager.getRobotRepositoryManager().getRobotsDirectory();
	else
	{
		robotsDirectory = new File(Constants.cwd(),"robots");
	}
	initialize();
}
/**
 * Insert the method's description here.
 * Creation date: (9/5/2001 2:41:04 PM)
 */
public void addPlaceShowFocus(JInternalFrame internalFrame) {
	getDesktopPane().add(internalFrame);
	
	// Center a window
	Dimension screenSize = getDesktopPane().getSize();
	Dimension size = internalFrame.getSize();
	if (size.height > screenSize.height)
		size.height = screenSize.height;
	if (size.width > screenSize.width)
		size.width = screenSize.width;

	if (origin.x + size.width > screenSize.width)
		origin.x = 0;
	if (origin.y + size.height > screenSize.height)
		origin.y = 0;
	internalFrame.setLocation(origin);
	origin.x += 10;
	origin.y += 10;

	internalFrame.setVisible(true);
	getDesktopPane().moveToFront(internalFrame);
	if (internalFrame instanceof EditWindow)
	{
		((EditWindow)internalFrame).getEditorPane().requestFocus();
	}
	else
		internalFrame.requestFocus();
}
/**
 * Insert the method's description here.
 * Creation date: (4/20/2001 1:14:00 PM)
 * @return boolean
 */
public boolean close() {
	JInternalFrame[] frames = getDesktopPane().getAllFrames();
	EditWindow editWindow = null;
	if (frames != null)
	{
		for (int i = 0; i < frames.length; i++)
		{
			editWindow = (EditWindow)frames[i];
			if (editWindow != null)
			{
				editWindow.moveToFront();
				if (editWindow.fileSave(true) == false)
					return false;
			}
		}
	};
	if (isApplication)
		System.exit(0);
	else
	{
		dispose();
		return true;
	}

	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (9/5/2001 2:33:21 PM)
 */
public void createNewJavaFile() {
	String packageName = null;
	if (getActiveWindow() != null)
	{
		packageName = getActiveWindow().getPackage();
	}
	if (packageName == null)
		packageName="mypackage";
	
	EditWindow editWindow = new EditWindow(this,robotsDirectory);

	editWindow.setModified(false);
	
	String templateName = "templates" + File.separatorChar + "newjavafile.tpt";

	String template = "";

	File f = null;
	try {
		f = new File(Constants.cwd(),templateName);
		int size = (int)(f.length());
		byte buff[] = new byte[size];
		FileInputStream fis = new FileInputStream(f);
		DataInputStream dis = new DataInputStream(fis);

		dis.readFully(buff);
		dis.close();
		template = new String(buff);
	} catch (Exception e) {
		if (f != null)
			template = "Unable to read template file: " + f.getPath();
		else
			template = "Unable to read template file (null)";
	}

	String name="MyClass";
	
	int index = template.indexOf("$");
	while (index >= 0)
	{
		if (template.substring(index,index+10).equals("$CLASSNAME"))
		{
			template = template.substring(0,index) + name + template.substring(index+10);
			index += name.length();
		}
		else if (template.substring(index,index+8).equals("$PACKAGE"))
		{
			template = template.substring(0,index) + packageName + template.substring(index+8);
			index += packageName.length();
		}
		else {
			index++;
		}
		index = template.indexOf("$",index);
	}	
	
	editWindow.getEditorPane().setText(template);
	editWindow.getEditorPane().setCaretPosition(0);
	javax.swing.text.Document d = editWindow.getEditorPane().getDocument();
	if (d instanceof JavaDocument)
		((JavaDocument)d).setEditing(true);
	addPlaceShowFocus(editWindow);
}
/**
 * Insert the method's description here.
 * Creation date: (9/5/2001 2:33:21 PM)
 */
public void createNewRobot() {
	boolean done = false;
	String message = "Type in the name for your robot.\nExample: MyFirstRobot";
	String name = "";
	while (!done)
	{
		
		name = (String)JOptionPane.showInputDialog(this,message,"New Robot",JOptionPane.PLAIN_MESSAGE,null,null,name);
		if (name == null)
		{
			return;
		}
		done = true;
		if (name.length() > 32)
		{
			message = "Please choose a shorter name.  (32 characters or less)";
			done = false;
			continue;
		}
		char firstLetter = name.charAt(0);
		if (!Character.isJavaIdentifierStart(firstLetter))
		{
			message = "Please start your name with a letter (A-Z)";
			done = false;
			continue;
		}
		for (int t = 1; done && t < name.length(); t++)
		{
			if (!Character.isJavaIdentifierPart(name.charAt(t)))
			{
			  message = "Your name contains an invalid character.\nPlease use only letters and/or digits.";
			  done = false;
			}
			
		}
		if (!done)
			continue;
			
		if (!Character.isUpperCase(firstLetter)) // popup
		{
			message = "The first character should be uppercase,\nas should the first letter of all words in the name.\nExample: MyFirstRobot";
			name = name.substring(0,1).toUpperCase() + name.substring(1);
			done = false;
		}
	}

	done = false;
	message = "Please enter your initials.\n" +
		"To avoid name conflicts with other robots named " + name + ",\n" +
		"we need a short string to identify this one as one of yours.\n" +
		"Your initials will work well here,\n" +
		"but you may choose any short string that you like.\n" +
		"You should enter the same thing for all your robots.";
	String packageName = "";
	while (!done)
	{
		
		packageName = (String)JOptionPane.showInputDialog(this,message,name + " - package name",JOptionPane.PLAIN_MESSAGE,null,null,packageName);
		if (packageName == null)
		{
			return;
		}
		done = true;
		if (packageName.length() > 16)
		{
			message = "Please choose a shorter name.  (16 characters or less)";
			done = false;
			continue;
		}
		char firstLetter = packageName.charAt(0);
		if (!Character.isJavaIdentifierStart(firstLetter))
		{
			message = "Please start with a letter (a-z)";
			done = false;
			continue;
		}
		for (int t = 1; done && t < packageName.length(); t++)
		{
			if (!Character.isJavaIdentifierPart(packageName.charAt(t)))
			{
			  message = "The string you entered contains an invalid character.\nPlease use only letters and/or digits.";
			  done = false;
			}
			
		}
		if (!done)
			continue;

		boolean lowercased = false;
		for (int i = 0; i < packageName.length(); i++)
		{
			if (!Character.isLowerCase(packageName.charAt(i)) && !Character.isDigit(packageName.charAt(i)))
			{
				lowercased = true;
			}
		}
		if (lowercased)
		{
			packageName = packageName.toLowerCase();
			message = "Please use all lowercase letters here.";
			done = false;
		}
		if (done && manager != null)
		{
			done = manager.getRobotRepositoryManager().verifyRootPackage(packageName + "." + name);
			if (!done)
				message = "This package is reserved.  Please select a different package.";
		}
	}


	
	EditWindow editWindow = new EditWindow(this,robotsDirectory);

//	editWindow.setTitle("Editing - " + name);//newRobotDialog.getNewRobotName());
	editWindow.setRobotName(name);
	editWindow.setModified(false);
	
	String templateName = "templates" + File.separatorChar + "newrobot.tpt";

	String template = "";

	File f = null;
	try {
		f = new File(Constants.cwd(),templateName);
		int size = (int)(f.length());
		byte buff[] = new byte[size];
		FileInputStream fis = new FileInputStream(f);
		DataInputStream dis = new DataInputStream(fis);

		dis.readFully(buff);
		dis.close();
		template = new String(buff);
	} catch (Exception e) {
		if (f != null)
			template = "Unable to read template file: " + f.getPath();
		else
			template = "Unable to read template file (null)";
	}

	int index = template.indexOf("$");
	while (index >= 0)
	{
		if (template.substring(index,index+10).equals("$CLASSNAME"))
		{
			template = template.substring(0,index) + name + template.substring(index+10);
			index += name.length();
		}
		else if (template.substring(index,index+8).equals("$PACKAGE"))
		{
			template = template.substring(0,index) + packageName + template.substring(index+8);
			index += packageName.length();
		}
		else {
			index++;
		}
		index = template.indexOf("$",index);
	}	
	
	editWindow.getEditorPane().setText(template);
	editWindow.getEditorPane().setCaretPosition(0);
	javax.swing.text.Document d = editWindow.getEditorPane().getDocument();
	if (d instanceof JavaDocument)
		((JavaDocument)d).setEditing(true);
	addPlaceShowFocus(editWindow);
	if (manager != null)
		manager.getRobotRepositoryManager().clearRobotList();
}
/**
 * Insert the method's description here.
 * Creation date: (4/19/2001 12:59:17 PM)
 * @return robocode.editor.EditWindow
 */
public EditWindow getActiveWindow() {
	JInternalFrame[] frames = getDesktopPane().getAllFrames();
	EditWindow editWindow = null;
	if (frames != null)
	{
		for (int i = 0; i < frames.length; i++)
		{
			if (frames[i].isSelected())
			{
				if (frames[i] instanceof EditWindow)
					editWindow = (EditWindow)frames[i];
				break;
			}
		}
	}
	return editWindow;
}
/**
 * Insert the method's description here.
 * Creation date: (11/5/2001 6:44:32 PM)
 * @return robocode.editor.RobocodeCompiler
 */
public RobocodeCompiler getCompiler() {
	RobocodeCompiler compiler = RobocodeCompilerFactory.createCompiler(this);
	if (compiler != null)
		getRobocodeEditorMenuBar().enableMenus();
	return compiler;
}
/**
 * Return the desktopPane
 * @return javax.swing.JDesktopPane
 */
public javax.swing.JDesktopPane getDesktopPane() {
	if (desktopPane == null) {
		try {
			desktopPane = new javax.swing.JDesktopPane();
			desktopPane.setName("desktopPane");
			desktopPane.setBackground(new java.awt.Color(128,128,128));
			desktopPane.setPreferredSize(new Dimension(600,500));
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return desktopPane;
}
/**
 * Return the line label.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getLineLabel() {
	if (lineLabel == null) {
		try {
			lineLabel = new javax.swing.JLabel();
			lineLabel.setName("lineLabel");
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return lineLabel;
}
/**
 * Return the robocodeEditorContentPane
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getRobocodeEditorContentPane() {
	if (robocodeEditorContentPane == null) {
		try {
			robocodeEditorContentPane = new javax.swing.JPanel();
			robocodeEditorContentPane.setName("robocodeEditorContentPane");
			robocodeEditorContentPane.setLayout(new java.awt.BorderLayout());
			robocodeEditorContentPane.add(getDesktopPane(), "Center");
			robocodeEditorContentPane.add(getStatusBar(), "South");
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return robocodeEditorContentPane;
}
/**
 * Return the robocodeEditorMenuBar property value.
 * @return robocode.editor.RobocodeEditorMenuBar
 */
private RobocodeEditorMenuBar getRobocodeEditorMenuBar() {
	if (robocodeEditorMenuBar == null) {
		try {
			robocodeEditorMenuBar = new RobocodeEditorMenuBar(this);
			robocodeEditorMenuBar.setName("robocodeEditorMenuBar");
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return robocodeEditorMenuBar;
}
/**
 * Insert the method's description here.
 * Creation date: (1/18/2001 3:43:41 PM)
 */
public RobocodeProperties getRobocodeProperties() {
	if (robocodeProperties == null)
	{
		robocodeProperties = new RobocodeProperties(manager);
		try {
			FileInputStream in = new FileInputStream(new File(Constants.cwd(),"robocode.properties"));
			robocodeProperties.load(in);
		} catch (FileNotFoundException e) {
			log("No robocode.properties file, using defaults.");
		} catch (IOException e) {
			log("IO Exception reading robocode.properties" + e);
		}
	}
	return robocodeProperties;
}
/**
 * Return the toolBar.
 * @return javax.swing.JToolBar
 */
private javax.swing.JToolBar getStatusBar() {
	if (statusBar == null) {
		try {
			statusBar = new javax.swing.JToolBar();
			statusBar.setName("statusBar");
			statusBar.setLayout(new BorderLayout());
			statusBar.add(getLineLabel(),BorderLayout.WEST);
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return statusBar;
}
/**
 * Initialize the class.
 */
private void initialize() {
	try {
		addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				close();
			}
		});
		setName("RoboCodeEditor");
		setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
		setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/icons/icon.jpg")));
		setTitle("Robot Editor");
//		setSize(595, 539);
		setJMenuBar(getRobocodeEditorMenuBar());
		setContentPane(getRobocodeEditorContentPane());
		addComponentListener(eventHandler);
	} catch (java.lang.Throwable e) {
		log(e);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (8/22/2001 1:41:21 PM)
 * @param e java.lang.Exception
 */
public void log(String s) {
	Utils.log(s);
}
/**
 * Insert the method's description here.
 * Creation date: (8/22/2001 1:41:21 PM)
 * @param e java.lang.Exception
 */
private static void log(String s, Throwable e) {
	Utils.log(s,e);
}
/**
 * Insert the method's description here.
 * Creation date: (8/22/2001 1:41:21 PM)
 * @param e java.lang.Exception
 */
private void log(Throwable e) {
	Utils.log(e);
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		/* Set native look and feel */
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		RobocodeEditor robocodeEditor;
		robocodeEditor = new RobocodeEditor(null);
		robocodeEditor.isApplication = true; // used for close
		robocodeEditor.pack();
		// Center robocodeEditor
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension size = robocodeEditor.getSize();
		if (size.height > screenSize.height)
			size.height = screenSize.height;
		if (size.width > screenSize.width)
			size.width = screenSize.width;
		robocodeEditor.setLocation((screenSize.width - size.width) / 2, (screenSize.height - size.height) / 2);
		robocodeEditor.setVisible(true);
		// 2nd time for bug in some JREs
		robocodeEditor.setVisible(true);

	} catch (Throwable e) {
		log("Exception in RoboCodeEditor.main",e);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (9/5/2001 2:44:59 PM)
 */
public void openRobot() {
	if (editorDirectory == null)
		editorDirectory = robotsDirectory;

	JFileChooser chooser;

	chooser = new JFileChooser(editorDirectory);
		
	javax.swing.filechooser.FileFilter filter = new javax.swing.filechooser.FileFilter()
	{
		public boolean accept(File pathname)
		{
			if (pathname.isHidden())
				return false;
			if (pathname.isDirectory())
				return true;
			String fn = pathname.getName();
			int idx = fn.lastIndexOf('.');
			String extension = "";
			if (idx >= 0)
			  extension = fn.substring(idx);
			if (extension.equalsIgnoreCase(".java"))
			  return true;
			return false;
		}
		public String getDescription() {
			return "Robots";
		}
	};

	chooser.setFileFilter(filter);
	int rv = chooser.showOpenDialog(this);
	if (rv == JFileChooser.APPROVE_OPTION)
	{
		String robotFilename = chooser.getSelectedFile().getPath();
		editorDirectory = chooser.getSelectedFile().getParentFile();
		try {
//			JOptionPane.showMessageDialog(this,"I will try to read " + robotFilename);
			EditWindow editWindow = new EditWindow(this,robotsDirectory);
			editWindow.getEditorPane().read(new FileReader(robotFilename),new File(robotFilename));
			editWindow.getEditorPane().setCaretPosition(0);
			editWindow.setFileName(robotFilename);
			editWindow.setModified(false);
			javax.swing.text.Document d = editWindow.getEditorPane().getDocument();
			if (d instanceof JavaDocument)
				((JavaDocument)d).setEditing(true);
			addPlaceShowFocus(editWindow);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this,e.toString());
			log(e);
		}
	}

}

public void extractRobot() {
	manager.getWindowManager().showRobotExtractor(this);
}

/**
 * Insert the method's description here.
 * Creation date: (4/18/2001 4:27:07 PM)
 * @param msg java.lang.String
 */
public void run() {
	getCompiler();
}
/**
 * Insert the method's description here.
 * Creation date: (9/5/2001 2:46:03 PM)
 */
public void saveAsRobot() {
	EditWindow editWindow = getActiveWindow();
	if (editWindow != null)
		editWindow.fileSaveAs();
}
/**
 * Insert the method's description here.
 * Creation date: (1/18/2001 3:21:22 PM)
 */
public void saveRobocodeProperties() {
	if (robocodeProperties == null)
	{
		log("Cannot save null robocode properties");
		return;
	}
	try {
		FileOutputStream out = new FileOutputStream(new File(Constants.cwd(),"robocode.properties"));
		robocodeProperties.store(out,"Robocode Properties");
	} catch (IOException e) {
		log(e);
	}
	

}
/**
 * Insert the method's description here.
 * Creation date: (1/18/2001 3:21:22 PM)
 */
public void resetCompilerProperties() {
	CompilerProperties props = RobocodeCompilerFactory.getCompilerProperties();
	props.resetCompiler();
	RobocodeCompilerFactory.saveCompilerProperties();
	getCompiler();
}
/**
 * Insert the method's description here.
 * Creation date: (9/5/2001 2:46:03 PM)
 */
public void saveRobot() {
	EditWindow editWindow = getActiveWindow();
	if (editWindow != null)
		editWindow.fileSave(false);
}
/**
 * Insert the method's description here.
 * Creation date: (10/10/2001 1:15:24 PM)
 * @param line int
 */
public void setLineStatus(int line) {
	if (line >= 0)
		getLineLabel().setText("Line: " + (line+1));
	else
		getLineLabel().setText("");
}
/**
 * Insert the method's description here.
 * Creation date: (8/22/2001 2:53:17 PM)
 */
public void showHelpApi() {
		String helpurl = "file:" + new File(Constants.cwd(),"").getAbsoluteFile() //System.getProperty("user.dir")
		+ System.getProperty("file.separator")
		+ "javadoc" + System.getProperty("file.separator") + "index.html";
		
//	BrowserControl.displayURL(helpurl);
	try {
		manager.getBrowserManager().openURL(helpurl);
	} catch (IOException e) {
			JOptionPane.showMessageDialog(this,
				e.getMessage(),
				"Unable to open browser!",
				JOptionPane.INFORMATION_MESSAGE);
	}

	/*
	java.net.URL u;
	try {
		u = new java.net.URL(helpurl);
	} catch (java.net.MalformedURLException e) {
		log("Could not form a URL from " + helpurl);
		return;
	}


	// Create the dialog
	RobocodeHtmlDialog hd = new RobocodeHtmlDialog();
	Dimension dialogSize = hd.getPreferredSize();
	Dimension frameSize = robocodeFrame.getSize();
	Point loc = robocodeFrame.getLocation();
	hd.setTitle("Robocode API");
	hd.setURL(u);
	hd.show();
	*/
}
	/**
	 * Gets the manager.
	 * @return Returns a RobocodeManager
	 */
	public RobocodeManager getManager() {
		return manager;
	}

}
