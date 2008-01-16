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
 *     Matthew Reeder
 *     - Changes for Find/Replace commands and Window menu
 *     Flemming N. Larsen
 *     - Code cleanup
 *     - Bugfixed the removeFromWindowMenu() method which did not remove the
 *       correct item, and did not break out of the loop when it was found.
 *     - Updated to use methods from ImageUtil, FileUtil, Logger, which replaces
 *       methods that have been (re)moved from the robocode.util.Utils class
 *     - Changed to use FileUtil.getRobocodeConfigFile() and
 *       FileUtil.getRobotsDir()
 *     - Added missing close() on FileInputStream, FileOutputStream, and
 *       FileReader
 *******************************************************************************/
package robocode.editor;


import static robocode.io.Logger.log;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.Document;

import robocode.gfx.ImageUtil;
import robocode.io.FileUtil;
import robocode.manager.BrowserManager;
import robocode.manager.RobocodeManager;
import robocode.manager.RobocodeProperties;


/**
 * @author Mathew A. Nelson (original)
 * @author Matthew Reeder (contributor)
 * @author Flemming N. Larsen (contributor)
 */
@SuppressWarnings("serial")
public class RobocodeEditor extends JFrame implements Runnable {
	private JPanel robocodeEditorContentPane;

	private RobocodeEditorMenuBar robocodeEditorMenuBar;
	private JDesktopPane desktopPane;
	public boolean isApplication;

	public Point origin = new Point();
	public File robotsDirectory;
	private JToolBar statusBar;
	private JLabel lineLabel;

	private RobocodeProperties robocodeProperties;
	private File editorDirectory;
	private RobocodeManager manager;

	private FindReplaceDialog findReplaceDialog;
	private ReplaceAction replaceAction;

	EventHandler eventHandler = new EventHandler();

	class EventHandler implements ComponentListener {
		public void componentMoved(ComponentEvent e) {}

		public void componentHidden(ComponentEvent e) {}

		public void componentShown(ComponentEvent e) {
			new Thread(RobocodeEditor.this).start();
		}

		public void componentResized(ComponentEvent e) {}
	}


	/**
	 * Action that launches the Replace dialog.
	 *
	 * The reason this is needed (and the menubar isn't sufficient) is that
	 * ctrl+H is bound in JTextComponents at a lower level to backspace and in
	 * order to override this, I need to rebind it to an Action when the
	 * JEditorPane is created.
	 */
	class ReplaceAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			replaceDialog();
		}
	}

	/**
	 * RoboCodeEditor constructor
	 */
	public RobocodeEditor(RobocodeManager manager) {
		super();
		this.manager = manager;
		if (manager != null) {
			robotsDirectory = manager.getRobotRepositoryManager().getRobotsDirectory();
		} else {
			robotsDirectory = FileUtil.getRobotsDir();
		}
		initialize();
	}

	public void addPlaceShowFocus(JInternalFrame internalFrame) {
		getDesktopPane().add(internalFrame);

		// Center a window
		Dimension screenSize = getDesktopPane().getSize();
		Dimension size = internalFrame.getSize();

		if (size.height > screenSize.height) {
			size.height = screenSize.height;
		}
		if (size.width > screenSize.width) {
			size.width = screenSize.width;
		}

		if (origin.x + size.width > screenSize.width) {
			origin.x = 0;
		}
		if (origin.y + size.height > screenSize.height) {
			origin.y = 0;
		}
		internalFrame.setLocation(origin);
		origin.x += 10;
		origin.y += 10;

		internalFrame.setVisible(true);
		getDesktopPane().moveToFront(internalFrame);
		if (internalFrame instanceof EditWindow) {
			((EditWindow) internalFrame).getEditorPane().requestFocus();
		} else {
			internalFrame.requestFocus();
		}
	}

	public boolean close() {
		JInternalFrame[] frames = getDesktopPane().getAllFrames();

		if (frames != null) {
			for (JInternalFrame frame : frames) {
				if (frame != null) {
					frame.moveToFront();
					if ((frame instanceof EditWindow) && !((EditWindow) frame).fileSave(true)) {
						return false;
					}
				}
			}
		}

		if (isApplication) {
			System.exit(0);
		} else {
			dispose();
		}
		return true;
	}

	public void createNewJavaFile() {
		String packageName = null;

		if (getActiveWindow() != null) {
			packageName = getActiveWindow().getPackage();
		}
		if (packageName == null) {
			packageName = "mypackage";
		}

		EditWindow editWindow = new EditWindow(this, robotsDirectory);

		editWindow.setModified(false);

		String templateName = "templates" + File.separatorChar + "newjavafile.tpt";

		String template = "";

		File f = new File(FileUtil.getCwd(), templateName);
		int size = (int) (f.length());
		byte buff[] = new byte[size];

		FileInputStream fis = null;
		DataInputStream dis = null;

		try {
			fis = new FileInputStream(f);
			dis = new DataInputStream(fis);

			dis.readFully(buff);
			template = new String(buff);
		} catch (IOException e) {
			template = "Unable to read template file: " + FileUtil.getCwd() + File.pathSeparatorChar + templateName;
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {}
			}
			if (dis != null) {
				try {
					dis.close();
				} catch (IOException e) {}
			}
		}

		String name = "MyClass";

		int index = template.indexOf("$");

		while (index >= 0) {
			if (template.substring(index, index + 10).equals("$CLASSNAME")) {
				template = template.substring(0, index) + name + template.substring(index + 10);
				index += name.length();
			} else if (template.substring(index, index + 8).equals("$PACKAGE")) {
				template = template.substring(0, index) + packageName + template.substring(index + 8);
				index += packageName.length();
			} else {
				index++;
			}
			index = template.indexOf("$", index);
		}

		editWindow.getEditorPane().setText(template);
		editWindow.getEditorPane().setCaretPosition(0);
		Document d = editWindow.getEditorPane().getDocument();

		if (d instanceof JavaDocument) {
			((JavaDocument) d).setEditing(true);
		}
		addPlaceShowFocus(editWindow);
	}

	public void createNewRobot() {
		boolean done = false;
		String message = "Type in the name for your robot.\nExample: MyFirstRobot";
		String name = "";

		while (!done) {
			name = (String) JOptionPane.showInputDialog(this, message, "New Robot", JOptionPane.PLAIN_MESSAGE, null,
					null, name);
			if (name == null) {
				return;
			}
			done = true;
			if (name.length() > 32) {
				message = "Please choose a shorter name.  (32 characters or less)";
				done = false;
				continue;
			}
			char firstLetter = name.charAt(0);

			if (!Character.isJavaIdentifierStart(firstLetter)) {
				message = "Please start your name with a letter (A-Z)";
				done = false;
				continue;
			}
			for (int t = 1; done && t < name.length(); t++) {
				if (!Character.isJavaIdentifierPart(name.charAt(t))) {
					message = "Your name contains an invalid character.\nPlease use only letters and/or digits.";
					done = false;
				}

			}
			if (!done) {
				continue;
			}

			if (!Character.isUpperCase(firstLetter)) // popup
			{
				message = "The first character should be uppercase,\nas should the first letter of all words in the name.\nExample: MyFirstRobot";
				name = name.substring(0, 1).toUpperCase() + name.substring(1);
				done = false;
			}
		}

		done = false;
		message = "Please enter your initials.\n" + "To avoid name conflicts with other robots named " + name + ",\n"
				+ "we need a short string to identify this one as one of yours.\n" + "Your initials will work well here,\n"
				+ "but you may choose any short string that you like.\n"
				+ "You should enter the same thing for all your robots.";
		String packageName = "";

		while (!done) {
			packageName = (String) JOptionPane.showInputDialog(this, message, name + " - package name",
					JOptionPane.PLAIN_MESSAGE, null, null, packageName);
			if (packageName == null) {
				return;
			}
			done = true;
			if (packageName.length() > 16) {
				message = "Please choose a shorter name.  (16 characters or less)";
				done = false;
				continue;
			}
			char firstLetter = packageName.charAt(0);

			if (!Character.isJavaIdentifierStart(firstLetter)) {
				message = "Please start with a letter (a-z)";
				done = false;
				continue;
			}
			for (int t = 1; done && t < packageName.length(); t++) {
				if (!Character.isJavaIdentifierPart(packageName.charAt(t))) {
					message = "The string you entered contains an invalid character.\nPlease use only letters and/or digits.";
					done = false;
				}
			}
			if (!done) {
				continue;
			}

			boolean lowercased = false;

			for (int i = 0; i < packageName.length(); i++) {
				if (!Character.isLowerCase(packageName.charAt(i)) && !Character.isDigit(packageName.charAt(i))) {
					lowercased = true;
				}
			}
			if (lowercased) {
				packageName = packageName.toLowerCase();
				message = "Please use all lowercase letters here.";
				done = false;
			}
			if (done && manager != null) {
				done = manager.getRobotRepositoryManager().verifyRootPackage(packageName + "." + name);
				if (!done) {
					message = "This package is reserved.  Please select a different package.";
				}
			}
		}

		EditWindow editWindow = new EditWindow(this, robotsDirectory);

		editWindow.setRobotName(name);
		editWindow.setModified(false);

		String templateName = "templates" + File.separatorChar + "newrobot.tpt";

		String template = "";

		File f = new File(FileUtil.getCwd(), templateName);
		int size = (int) (f.length());
		byte buff[] = new byte[size];
		FileInputStream fis = null;
		DataInputStream dis = null;

		try {
			fis = new FileInputStream(f);
			dis = new DataInputStream(fis);
			dis.readFully(buff);
			dis.close();
			template = new String(buff);
		} catch (IOException e) {
			template = "Unable to read template file: " + FileUtil.getCwd() + File.pathSeparatorChar + templateName;
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {}
			}
			if (dis != null) {
				try {
					dis.close();
				} catch (IOException e) {}
			}
		}

		int index = template.indexOf("$");

		while (index >= 0) {
			if (template.substring(index, index + 10).equals("$CLASSNAME")) {
				template = template.substring(0, index) + name + template.substring(index + 10);
				index += name.length();
			} else if (template.substring(index, index + 8).equals("$PACKAGE")) {
				template = template.substring(0, index) + packageName + template.substring(index + 8);
				index += packageName.length();
			} else {
				index++;
			}
			index = template.indexOf("$", index);
		}

		editWindow.getEditorPane().setText(template);
		editWindow.getEditorPane().setCaretPosition(0);
		Document d = editWindow.getEditorPane().getDocument();

		if (d instanceof JavaDocument) {
			((JavaDocument) d).setEditing(true);
		}
		addPlaceShowFocus(editWindow);
		if (manager != null) {
			manager.getRobotRepositoryManager().clearRobotList();
		}
	}

	public void findDialog() {
		getFindReplaceDialog().showDialog(false);
	}

	public void replaceDialog() {
		getFindReplaceDialog().showDialog(true);
	}

	public EditWindow getActiveWindow() {
		JInternalFrame[] frames = getDesktopPane().getAllFrames();
		EditWindow editWindow = null;

		if (frames != null) {
			for (JInternalFrame frame : frames) {
				if (frame.isSelected()) {
					if (frame instanceof EditWindow) {
						editWindow = (EditWindow) frame;
					}
					break;
				}
			}
		}
		return editWindow;
	}

	public RobocodeCompiler getCompiler() {
		RobocodeCompiler compiler = RobocodeCompilerFactory.createCompiler(this);

		if (compiler != null) {
			getRobocodeEditorMenuBar().enableMenus();
		}
		return compiler;
	}

	/**
	 * Return the desktopPane
	 *
	 * @return JDesktopPane
	 */
	public JDesktopPane getDesktopPane() {
		if (desktopPane == null) {
			desktopPane = new JDesktopPane();
			desktopPane.setBackground(new Color(128, 128, 128));
			desktopPane.setPreferredSize(new Dimension(600, 500));
		}
		return desktopPane;
	}

	/**
	 * Return the line label.
	 *
	 * @return JLabel
	 */
	private JLabel getLineLabel() {
		if (lineLabel == null) {
			lineLabel = new JLabel();
		}
		return lineLabel;
	}

	/**
	 * Return the robocodeEditorContentPane
	 *
	 * @return JPanel
	 */
	private JPanel getRobocodeEditorContentPane() {
		if (robocodeEditorContentPane == null) {
			robocodeEditorContentPane = new JPanel();
			robocodeEditorContentPane.setLayout(new BorderLayout());
			robocodeEditorContentPane.add(getDesktopPane(), "Center");
			robocodeEditorContentPane.add(getStatusBar(), "South");
		}
		return robocodeEditorContentPane;
	}

	/**
	 * Return the robocodeEditorMenuBar property value.
	 *
	 * @return robocode.editor.RobocodeEditorMenuBar
	 */
	private RobocodeEditorMenuBar getRobocodeEditorMenuBar() {
		if (robocodeEditorMenuBar == null) {
			robocodeEditorMenuBar = new RobocodeEditorMenuBar(this);
		}
		return robocodeEditorMenuBar;
	}

	public RobocodeProperties getRobocodeProperties() {
		if (robocodeProperties == null) {
			robocodeProperties = new RobocodeProperties(manager);

			FileInputStream in = null;

			try {
				in = new FileInputStream(FileUtil.getRobocodeConfigFile());
				robocodeProperties.load(in);
			} catch (FileNotFoundException e) {
				log("No " + FileUtil.getRobocodeConfigFile().getName() + " file, using defaults.");
			} catch (IOException e) {
				log("IO Exception reading " + FileUtil.getRobocodeConfigFile().getName() + ": " + e);
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {}
				}
			}
		}
		return robocodeProperties;
	}

	/**
	 * Return the toolBar.
	 *
	 * @return JToolBar
	 */
	private JToolBar getStatusBar() {
		if (statusBar == null) {
			statusBar = new JToolBar();
			statusBar.setLayout(new BorderLayout());
			statusBar.add(getLineLabel(), BorderLayout.WEST);
		}
		return statusBar;
	}

	/**
	 * Return the findReplaceDialog
	 *
	 * @return robocode.editor.FindReplaceDialog
	 */
	public FindReplaceDialog getFindReplaceDialog() {
		if (findReplaceDialog == null) {
			findReplaceDialog = new FindReplaceDialog(this);
		}
		return findReplaceDialog;
	}

	/**
	 * Return the replaceAction
	 *
	 * @return Action
	 */
	public Action getReplaceAction() {
		if (replaceAction == null) {
			replaceAction = new ReplaceAction();
		}
		return replaceAction;
	}

	/**
	 * Adds the given window to the Window menu.
	 */
	public void addToWindowMenu(EditWindow window) {
		WindowMenuItem item = new WindowMenuItem(window, getRobocodeEditorMenuBar().getWindowMenu());

		getRobocodeEditorMenuBar().getMoreWindowsDialog().addWindowItem(item);
	}

	/**
	 * Adds the given window to the Window menu.
	 */
	public void removeFromWindowMenu(EditWindow window) {
		for (Component c : getRobocodeEditorMenuBar().getWindowMenu().getMenuComponents()) {
			if (c instanceof WindowMenuItem) {
				WindowMenuItem item = (WindowMenuItem) c;

				if (item.getEditWindow() == window) {
					getRobocodeEditorMenuBar().getWindowMenu().remove(item);
					getRobocodeEditorMenuBar().getMoreWindowsDialog().removeWindowItem(item);
					break;
				}
			}
		}
	}

	/**
	 * Initialize the class.
	 */
	private void initialize() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				close();
			}
		});
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setIconImage(ImageUtil.getImage("/resources/icons/robocode-icon.png"));
		setTitle("Robot Editor");
		setJMenuBar(getRobocodeEditorMenuBar());
		setContentPane(getRobocodeEditorContentPane());
		addComponentListener(eventHandler);
	}

	/**
	 * main entrypoint - starts the part when it is run as an application
	 *
	 * @param args the arguments for the application
	 */
	public static void main(String[] args) {
		try {
			// Set the Look and Feel (LAF)
			robocode.manager.LookAndFeelManager.setLookAndFeel();

			RobocodeEditor robocodeEditor;

			robocodeEditor = new RobocodeEditor(null);
			robocodeEditor.isApplication = true; // used for close
			robocodeEditor.pack();
			// Center robocodeEditor
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			Dimension size = robocodeEditor.getSize();

			if (size.height > screenSize.height) {
				size.height = screenSize.height;
			}
			if (size.width > screenSize.width) {
				size.width = screenSize.width;
			}
			robocodeEditor.setLocation((screenSize.width - size.width) / 2, (screenSize.height - size.height) / 2);
			robocodeEditor.setVisible(true);
			// 2nd time for bug in some JREs
			robocodeEditor.setVisible(true);
		} catch (Throwable e) {
			log("Exception in RoboCodeEditor.main", e);
		}
	}

	public void openRobot() {
		if (editorDirectory == null) {
			editorDirectory = robotsDirectory;
		}
		JFileChooser chooser;

		chooser = new JFileChooser(editorDirectory);
		FileFilter filter = new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				if (pathname.isHidden()) {
					return false;
				}
				if (pathname.isDirectory()) {
					return true;
				}
				String fn = pathname.getName();
				int idx = fn.lastIndexOf('.');
				String extension = "";

				if (idx >= 0) {
					extension = fn.substring(idx);
				}
				if (extension.equalsIgnoreCase(".java")) {
					return true;
				}
				return false;
			}

			@Override
			public String getDescription() {
				return "Robots";
			}
		};

		chooser.setFileFilter(filter);
		int rv = chooser.showOpenDialog(this);

		if (rv == JFileChooser.APPROVE_OPTION) {
			String robotFilename = chooser.getSelectedFile().getPath();

			editorDirectory = chooser.getSelectedFile().getParentFile();
			
			FileReader fileReader = null;

			try {
				fileReader = new FileReader(robotFilename);
				
				EditWindow editWindow = new EditWindow(this, robotsDirectory);

				editWindow.getEditorPane().read(fileReader, new File(robotFilename));
				editWindow.getEditorPane().setCaretPosition(0);
				editWindow.setFileName(robotFilename);
				editWindow.setModified(false);
				Document d = editWindow.getEditorPane().getDocument();

				if (d instanceof JavaDocument) {
					((JavaDocument) d).setEditing(true);
				}
				addPlaceShowFocus(editWindow);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, e.toString());
				log(e);
			} finally {
				if (fileReader != null) {
					try {
						fileReader.close();
					} catch (IOException e) {}
				}
			}
		}
	}

	public void extractRobot() {
		manager.getWindowManager().showRobotExtractor(this);
	}

	public void run() {
		getCompiler();
	}

	public void saveAsRobot() {
		EditWindow editWindow = getActiveWindow();

		if (editWindow != null) {
			editWindow.fileSaveAs();
		}
	}

	public void saveRobocodeProperties() {
		if (robocodeProperties == null) {
			log("Cannot save null robocode properties");
			return;
		}
		FileOutputStream out = null;

		try {
			out = new FileOutputStream(FileUtil.getRobocodeConfigFile());

			robocodeProperties.store(out, "Robocode Properties");
		} catch (IOException e) {
			log(e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {}
			}
		}
	}

	public void resetCompilerProperties() {
		CompilerProperties props = RobocodeCompilerFactory.getCompilerProperties();

		props.resetCompiler();
		RobocodeCompilerFactory.saveCompilerProperties();
		getCompiler();
	}

	public void saveRobot() {
		EditWindow editWindow = getActiveWindow();

		if (editWindow != null) {
			editWindow.fileSave(false);
		}
	}

	public void setLineStatus(int line) {
		if (line >= 0) {
			getLineLabel().setText("Line: " + (line + 1));
		} else {
			getLineLabel().setText("");
		}
	}

	public void showHelpApi() {
		String helpurl = "file:" + new File(FileUtil.getCwd(), "").getAbsoluteFile() // System.getProperty("user.dir")
				+ System.getProperty("file.separator") + "javadoc" + System.getProperty("file.separator") + "index.html";

		try {
			BrowserManager.openURL(helpurl);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Unable to open browser!",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	/**
	 * Gets the manager.
	 *
	 * @return Returns a RobocodeManager
	 */
	public RobocodeManager getManager() {
		return manager;
	}
}
